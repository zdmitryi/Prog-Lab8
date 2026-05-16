package com.example;

import com.example.enums.Country;
import com.example.enums.FormOfEducation;
import com.example.enums.Semester;
import com.example.models.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import java.util.stream.*;

import java.util.*;
import java.util.function.Consumer;

public class DataService {

    private final ClientNetworkManager networkManager;
    private final String login;
    private final String password;
    private final int ownerId;

    private final ObservableList<StudyGroupDto> masterData;
    private final FilteredList<StudyGroupDto> filteredData;
    private Consumer<Boolean> onLoadingChanged;

    private long requestId = 1;
    private volatile boolean refreshing = false;

    private Consumer<Integer> onTotalCountChanged;
    private Consumer<Integer> onMyCountChanged;
    private Consumer<String> onError;
    private Consumer<String> onCommandResult;

    public DataService(ClientNetworkManager networkManager, String login,
                       String password, int ownerId) {
        this.networkManager = networkManager;
        this.login = login;
        this.password = password;
        this.ownerId = ownerId;

        this.masterData = FXCollections.observableArrayList();
        this.filteredData = new FilteredList<>(masterData, p -> true);
    }

    public ObservableList<StudyGroupDto> getMasterData() {
        return masterData;
    }

    public FilteredList<StudyGroupDto> getFilteredData() {
        return filteredData;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getLogin(){
        return login;
    }

    public void setOnTotalCountChanged(Consumer<Integer> callback) {
        this.onTotalCountChanged = callback;
    }

    public void setOnMyCountChanged(Consumer<Integer> callback) {
        this.onMyCountChanged = callback;
    }

    public void setOnError(Consumer<String> callback) {
        this.onError = callback;
    }

    public void setOnCommandResult(Consumer<String> callback) {
        this.onCommandResult = callback;
    }

    private Consumer<String> onHistoryResult;

    public void setOnHistoryResult(Consumer<String> callback) {
        this.onHistoryResult = callback;
    }

    public void refreshData() {
        if (refreshing) return;
        refreshing = true;

        Platform.runLater(() -> {
        });

        new Thread(() -> {
            try {
                CommandRequest request = new CommandRequest(login, password, 0,
                        "show", new String[0], null);
                networkManager.send(request);
                CommandResponse response = networkManager.receive();

                if (response.isSuccessful() && response.answer() != null) {
                    List<StudyGroupDto> parsedData = parseData(response.answer());
                    Platform.runLater(() -> {
                        masterData.setAll(parsedData);
                        updateStats();
                        refreshing = false;
                    });
                } else {
                    Platform.runLater(() -> {
                        refreshing = false;
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    refreshing = false;
                    if (onError != null) onError.accept("Error: " + e.getMessage());
                });
            }
        }).start();
    }

    private List<StudyGroupDto> parseData(String answer) {
        List<StudyGroupDto> result = new ArrayList<>();

        List<String> groupStrings = new ArrayList<>();
        if (answer.contains("SEPARATOR")) {
            for (String part : answer.split("SEPARATOR")) {
                if (!part.trim().isEmpty()) groupStrings.add(part.trim());
            }
        } else {
            for (String part : answer.split("Group ID:")) {
                if (!part.trim().isEmpty()) groupStrings.add("Group ID:" + part.trim());
            }
        }

        for (String groupStr : groupStrings) {
            StudyGroup group = parseGroup(groupStr);
            if (group != null) {
                result.add(new StudyGroupDto(group));
            }
        }

        return result;
    }


    private void updateStats() {
        long total = masterData.size();
        long myCount = masterData.stream()
                .filter(d -> d.getOwnerId() == ownerId)
                .count();

        if (onTotalCountChanged != null) {
            onTotalCountChanged.accept((int) total);
        }
        if (onMyCountChanged != null) {
            onMyCountChanged.accept((int) myCount);
        }
    }

    public void executeCommand(String cmd, String[] args, StudyGroup group) {
        new Thread(() -> {
            try {
                CommandRequest request = new CommandRequest(login, password, requestId++,
                        cmd, args != null ? args : new String[0], group);
                networkManager.send(request);
                CommandResponse response = networkManager.receive();
                System.out.println(response.answer());
                Platform.runLater(() -> {
                    if (onCommandResult != null) {
                        String msg = response.answer();
                        if (msg != null && msg.length() > 200 && !cmd.equals("help")) {
                            msg = "Command executed successfully";
                        }
                        onCommandResult.accept(msg);
                    }
                    if (response.isSuccessful()) {
                        if (cmd.equals("add") || cmd.equals("updateId") ||
                                cmd.equals("removeId") || cmd.equals("clear") ||
                                cmd.equals("addIfMin") || cmd.equals("removeGreater")) {
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ex) {}
                                refreshData();
                            }).start();
                        }
                        if (onCommandResult != null && !cmd.equals("history")) {
                            onCommandResult.accept(response.answer());
                        }
                        if (response.isSuccessful()) {
                            refreshHistory();
                        }
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    if (onError != null) {
                        onError.accept("Error: " + e.getMessage());
                    }
                });
            }
        }).start();
    }

    private void refreshHistory() {
        new Thread(() -> {
            try {
                CommandRequest request = new CommandRequest(login, password, requestId++,
                        "history", new String[0], null);
                networkManager.send(request);
                CommandResponse response = networkManager.receive();

                if (onHistoryResult != null) {
                    Platform.runLater(() -> onHistoryResult.accept(response.answer()));
                }
            } catch (Exception e) {
            }
        }).start();
    }

    private StudyGroup parseGroup(String str) {
        try {
            String[] parts = str.split("\\|");

            int id = 0;
            String name = "";
            double coordX = 0;
            long coordY = 0;
            long studentsCount = 0;
            int shouldBeExpelled = 0;
            String formOfEducation = "";
            String semester = "";
            String adminName = "Unknown";
            int ownerId = 0;

            for (String part : parts) {
                String[] kv = part.split(":", 2);
                if (kv.length != 2) continue;
                String key = kv[0].trim();
                String value = kv[1].trim();

                switch (key) {
                    case "ID": id = Integer.parseInt(value); break;
                    case "Name": name = value; break;
                    case "X":
                        coordX = Double.parseDouble(value.replace(",", "."));
                        break;
                    case "Y":
                        coordY = Long.parseLong(value.replace(",", ".").trim());
                        break;
                    case "Students": studentsCount = Long.parseLong(value); break;
                    case "Expelled": shouldBeExpelled = Integer.parseInt(value); break;
                    case "Form": formOfEducation = value; break;
                    case "Sem": semester = value; break;
                    case "Admin": adminName = value; break;
                    case "Owner": ownerId = Integer.parseInt(value); break;
                }
            }

            Coordinates coordinates = new Coordinates(coordX, coordY);
            Location location = new Location(0L, 0L, 0L, null);
            Person admin = new Person(adminName, null, null,
                    com.example.enums.Color.BLACK, Country.GERMANY, location);
            if (formOfEducation.isEmpty()) {
                System.err.println("Empty formOfEducation! Defaulting to FULL_TIME_EDUCATION");
                formOfEducation = "FULL_TIME_EDUCATION";
            }
            if (semester.isEmpty()) {
                System.err.println("Empty semester! Defaulting to FIFTH");
                semester = "FIFTH";
            }

            StudyGroup group = new StudyGroup(name, coordinates, studentsCount,
                    shouldBeExpelled, FormOfEducation.valueOf(formOfEducation),
                    Semester.valueOf(semester), admin);

            group.setId(id);
            group.setOwnerId(ownerId);

            return group;

        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
            return null;
        }
    }
    public void sortBy(String field, boolean ascending) {
        List<StudyGroupDto> sorted;
        Stream<StudyGroupDto> stream = masterData.stream();

        switch (field) {
            case "id":
                sorted = ascending ?
                        stream.sorted(Comparator.comparingInt(StudyGroupDto::getId)).collect(Collectors.toList()) :
                        stream.sorted(Comparator.comparingInt(StudyGroupDto::getId).reversed()).collect(Collectors.toList());
                break;
            case "name":
                sorted = ascending ?
                        stream.sorted(Comparator.comparing(StudyGroupDto::getName)).collect(Collectors.toList()) :
                        stream.sorted(Comparator.comparing(StudyGroupDto::getName).reversed()).collect(Collectors.toList());
                break;
            case "x":
                sorted = ascending ?
                        stream.sorted(Comparator.comparingDouble(StudyGroupDto::getCoordX)).collect(Collectors.toList()) :
                        stream.sorted(Comparator.comparingDouble(StudyGroupDto::getCoordX).reversed()).collect(Collectors.toList());
                break;
            case "y":
                sorted = ascending ?
                        stream.sorted(Comparator.comparingLong(StudyGroupDto::getCoordY)).collect(Collectors.toList()) :
                        stream.sorted(Comparator.comparingLong(StudyGroupDto::getCoordY).reversed()).collect(Collectors.toList());
                break;
            case "students":
                sorted = ascending ?
                        stream.sorted(Comparator.comparingLong(StudyGroupDto::getStudentsCount)).collect(Collectors.toList()) :
                        stream.sorted(Comparator.comparingLong(StudyGroupDto::getStudentsCount).reversed()).collect(Collectors.toList());
                break;
            case "expelled":
                sorted = ascending ?
                        stream.sorted(Comparator.comparingInt(StudyGroupDto::getShouldBeExpelled)).collect(Collectors.toList()) :
                        stream.sorted(Comparator.comparingInt(StudyGroupDto::getShouldBeExpelled).reversed()).collect(Collectors.toList());
                break;
            case "form":
                sorted = ascending ?
                        stream.sorted(Comparator.comparing(StudyGroupDto::getFormOfEducation)).collect(Collectors.toList()) :
                        stream.sorted(Comparator.comparing(StudyGroupDto::getFormOfEducation).reversed()).collect(Collectors.toList());
                break;
            case "semester":
                sorted = ascending ?
                        stream.sorted(Comparator.comparing(StudyGroupDto::getSemester)).collect(Collectors.toList()) :
                        stream.sorted(Comparator.comparing(StudyGroupDto::getSemester).reversed()).collect(Collectors.toList());
                break;
            case "admin":
                sorted = ascending ?
                        stream.sorted(Comparator.comparing(StudyGroupDto::getAdminName)).collect(Collectors.toList()) :
                        stream.sorted(Comparator.comparing(StudyGroupDto::getAdminName).reversed()).collect(Collectors.toList());
                break;
            default:
                return;
        }

        masterData.setAll(sorted);
    }
}