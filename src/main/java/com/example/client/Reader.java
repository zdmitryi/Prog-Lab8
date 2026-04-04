package com.example.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import com.example.common.CommandInfo;
import com.example.common.CommandRequest;
import com.example.common.CommandResponse;
import com.example.common.enums.Color;
import com.example.common.enums.Country;
import com.example.common.enums.FormOfEducation;
import com.example.common.enums.Semester;
import com.example.common.models.Coordinates;
import com.example.common.models.Location;
import com.example.common.models.Person;
import com.example.common.models.StudyGroup;

public class Reader {
    Scanner scanner = new Scanner(System.in);
    private boolean isInConsole = true;
    CommandValidator validator;
    ClientNetworkManager manager;
    private String password;
    private String login;

    public String getPassword(){
        return password;
    }

    public String getLogin(){
        return login;
    }

    public Reader(CommandValidator validator, ClientNetworkManager manager){
        this.validator = validator;
        this.manager = manager;
    }
    public boolean isInConsole() {
        return this.isInConsole;
    }
    public void inConsole(boolean b) {
        this.isInConsole = b;
    }
    public void setScanner(Scanner s){
        this.scanner = s;
    }
    public String readString(String prompt){
        while (true) {
            String s = scanner.nextLine().trim();
            if (!s.isBlank()){
            return s;
            }
            if (isInConsole){
            System.out.println("Некорректный ввод, попробуйте еще раз");
            System.out.print(prompt);
            }
        }
    }
    public int readInt(String prompt){
        while (true){ 
        try {
            String s = scanner.nextLine().trim(); 
            if (s.isBlank()){
                if (isInConsole){
                System.out.println("Некорректный ввод, попробуйте еще раз");
                System.out.print(prompt);
                }
                continue;
            }
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            if (isInConsole){
            System.out.println("Ошибка, введите число");
            System.out.print(prompt);
            }
        }
        }
    }
    public String readLine(String prompt) {
        while (true){
        String s = scanner.nextLine().trim();  
        if (!s.isBlank()) {
            return s;
        }  
        if (isInConsole){
        System.out.println("Некорректный ввод, попробуйте еще раз");
        System.out.print(prompt);
        }
        }
    }
    public String readStringForEnum(String prompt, ArrayList<String> names){
        while (true) {
        String s = scanner.nextLine().trim();
        if (!s.isBlank()){
            return s;
        }
        if (isInConsole){
        System.out.println("Некорректный ввод, попробуйте еще раз");
        System.out.printf(prompt, names);
        }
        }
    }
    public double readDouble(String prompt){
        while (true){ 
        try {
            String s = scanner.nextLine().trim(); 
            if (s.isBlank()){
                if (isInConsole){
                System.out.println("Некорректный ввод, попробуйте еще раз");
                System.out.print(prompt);
                }
                continue;
            }
            s = s.replace(",", ".");
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            if (isInConsole){
            System.out.println("Ошибка, введите число");
            System.out.print(prompt);
            }
        }
        }
    }
    public long readLong(String prompt){
        while (true){ 
        try {
            String s = scanner.nextLine().trim(); 
            if (s.isBlank()){
                if (isInConsole){
                System.out.println("Некорректный ввод, попробуйте еще раз");
                System.out.print(prompt);
                }
                continue;
            }
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            if (isInConsole){
            System.out.println("Ошибка, введите число");
            System.out.print(prompt);
            }
        }
        }
    }
    public StudyGroup createGroup(){
        String name;
        if (isInConsole){
        System.out.print("Введите название группы: ");
        name = this.readLine("Введите название группы: ");
        } else {
             name = this.readLine("");
        }
        double CoordX;
        while (true) {
            double c;
            if (isInConsole){
            System.out.print("Введите координату группы по X, её значение должно быть меньше 225: ");
            c = this.readDouble("Введите координату группы по X, её значение должно быть меньше 225: ");
            } else { 
                c = this.readDouble("");
            }
            if (c <= 225){
                CoordX = c; 
                break; 
            }
            if (isInConsole) System.out.println("Неверный ввод, попробуйте еще раз");
        }

        long CoordY;
        if (isInConsole){
        System.out.print("Введите координату группы по Y: ");
        CoordY = this.readLong("Введите координату группы по Y: ");
        } else CoordY = this.readLong("");

        Coordinates coordinates = new Coordinates(CoordX, CoordY);

        int amount;
        while (true){
            int a;
            if (isInConsole){
            System.out.print("Введите количество человек в группе: ");
            a = this.readInt("Введите количество человек в группе: ");
            } else a = this.readInt("");
            if (a > 0){
                amount = a; 
                break; 
            }
            if (isInConsole){
            System.out.println("Неверный ввод, попробуйте еще раз");
            }
        }

        
        int amountEx;
        while (true){
            int a;
            if (isInConsole){
            System.out.print("Введите количество человек в группе, рекомендованных к отчислению: ");
            a = this.readInt("Введите количество человек в группе, рекомендованных к отчислению: ");
            } else a = this.readInt("");
            if (a > 0 && a <= amount){
                amountEx = a; 
                break; 
            }
            if (isInConsole){
            System.out.println("Неверный ввод, попробуйте еще раз");
            }
        }

        ArrayList<String> forms = new ArrayList<>();
        for (FormOfEducation f : FormOfEducation.values()) forms.add(f.name());
        String form = null;
        while (form == null) { 
            String a;
            if (isInConsole){
            System.out.printf("Выберите форму обучения - %s: ", forms);
            a = this.readStringForEnum("Выберите форму обучения - %s: ", forms);
            } else a = this.readStringForEnum("", new ArrayList<>());
            for (String e : forms){
                if (a.equals(e)) {
                    form = a;
                    break;
                }
            }
            if (isInConsole){
            if (form == null){
            System.out.println("Неверный ввод, попробуйте еще раз");
            }
        }
        }
        FormOfEducation formEnum = FormOfEducation.valueOf(form);


        ArrayList<String> sems = new ArrayList<>();
        for (Semester f : Semester.values()) sems.add(f.name());
        String sem = null;
        while (sem == null) {
            String a;
            if (isInConsole){ 
            System.out.printf("Выберите семестр - %s: ", sems);
            a = this.readStringForEnum("Выберите семестр - %s: ", sems);
            } else a = this.readStringForEnum("", new ArrayList<>());
            for (String e : sems){
                if (a.equals(e)) {
                    sem = a;
                    break;
                }
            }
            if (isInConsole){
            if (sem == null){
            System.out.println("Неверный ввод, попробуйте еще раз");
            }
        }
        }
        Semester semEnum = Semester.valueOf(sem);

        Person admin = this.readPerson();

        StudyGroup group = new StudyGroup(name, coordinates, amount, amountEx, formEnum, semEnum, admin);

        return group;
    }
    public StudyGroup createAdminGroup(){
        Person admin = this.readPerson();
        StudyGroup group = new StudyGroup("", new Coordinates(1,1), 1, 1, FormOfEducation.FULL_TIME_EDUCATION, Semester.FIFTH, admin);
        return group;
    }
    public Person readPerson(){
        String adminName;
        if (isInConsole){
        System.out.print("Введите имя админа: ");
        adminName = this.readLine("Введите имя админа: ");
        } else adminName = this.readLine("");
        Double weight = null;
        if (isInConsole){
        System.out.print("Введите вес админа: ");
        }
        while (true){ 
            try {
                String s = scanner.nextLine().trim();
                if (s.isBlank()){
                    weight = null;
                    break;
                }
                s = s.replace(",", ".");
                weight = Double.parseDouble(s);
                break;
            } catch (NumberFormatException e) {
                if (isInConsole){
                System.out.println("Ошибка, введите число");
                System.out.print("Введите вес админа: ");
                }
            }
        }

        ArrayList<String> eyeColors = new ArrayList<>();
        for (Color c : Color.values()) eyeColors.add(c.name());
        String eyeColorStr = null;
        Color eyeColorEnum = null;
        while (eyeColorEnum == null) { 
            String a;
            if (isInConsole){
            System.out.printf("Выберите цвет глаз - %s: ", eyeColors);
            a = this.readStringForEnum("Выберите цвет глаз - %s: ", eyeColors);
            } else a = this.readStringForEnum("", new ArrayList<>());
            if (a.isBlank()) {
                break;
            }
            for (String e : eyeColors){
                if (a.equals(e)) {
                    eyeColorStr = a;
                    break;
                }
            }
            if (eyeColorStr != null) {
                eyeColorEnum = Color.valueOf(eyeColorStr);
            } else {
                if (isInConsole){
                System.out.println("Неверный ввод, попробуйте еще раз");
                }
            }
        }

        ArrayList<String> hairColors = new ArrayList<>();
        for (Color f : Color.values()) hairColors.add(f.name());
        String hairColor = null;
        while (hairColor == null) { 
            String a;
            if (isInConsole){
            System.out.printf("Выберите цвет волос - %s: ", hairColors);
            a = this.readStringForEnum("Выберите цвет волос - %s: ", hairColors);
            } else a = this.readStringForEnum("", new ArrayList<>());
            for (String e : hairColors){
                if (a.equals(e)) {
                    hairColor = a;
                    break;
                }
            }
            if (hairColor == null){
                if (isInConsole){
                System.out.println("Неверный ввод, попробуйте еще раз");
            }
            }
        }
        Color hairColorEnum = Color.valueOf(hairColor);

        ArrayList<String> nationalities = new ArrayList<>();
        for (Country f : Country.values()) nationalities.add(f.name());
        String country = null;
        while (country == null) { 
            String a;
            if (isInConsole){
            System.out.printf("Выберите национальность админа - %s: ", nationalities);
            a = this.readStringForEnum("Выберите национальность админа - %s: ", nationalities);
            } else a = this.readStringForEnum("", new ArrayList<>());
            for (String e : nationalities){
                if (a.equals(e)) {
                    country = a;
                    break;
                }
            }
            if (country == null){
                if (isInConsole){
                System.out.println("Неверный ввод, попробуйте еще раз");
            }
            }
        }
        
        Country countryEnum = Country.valueOf(country);

        long LocX;
        if (isInConsole){
        System.out.print("Введите локацию админа по X: ");
        LocX = this.readLong("Введите локацию админа по X: ");
        } else LocX = this.readLong("");

        long LocY;
        if (isInConsole){
        System.out.print("Введите локацию админа по Y: ");
        LocY = this.readLong("Введите локацию админа по Y: ");
        } else LocY = this.readLong("");

        long LocZ;
        if (isInConsole){
        System.out.print("Введите локацию админа по Z: ");
        LocZ = this.readLong("Введите локация админа по Z: ");
        } else LocZ = this.readLong("");

        if (isInConsole){
        System.out.print("Введите название локации админа: ");
        }
        String LocName = scanner.nextLine();

        Location location = new Location(LocX, LocY, LocZ, LocName);

        Person admin = new Person(adminName, weight, eyeColorEnum, hairColorEnum, countryEnum, location);

        return admin;
     }
     private long nextRequestId = 1;

    public void readAuthorizationInfo(){
        System.out.print("Введите логин пользователя\n>");
        String login = scanner.nextLine().trim();
        System.out.print("Введите пароль\n>");
        String password = scanner.nextLine().trim();
        this.login = login;
        this.password = password;
    }

     public void readCommand() throws IOException, ClassNotFoundException, InterruptedException{
        if (isInConsole) System.out.print(">");
        String line = scanner.nextLine().trim();
        if (line.isBlank() && !isInConsole){
            return;
        }
        if (line.equals("exit")){
            manager.close();
            System.exit(0);
        }
        CommandInfo info = validator.findInfo(line);
        if (info == null) return;
        String[] parts = line.split("\\s+");
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        if (validator.validate(line)) {
                if (info.needsOnlyAdmin()) {
                    manager.send(new CommandRequest(
                        login,
                        password,
                        ++nextRequestId,
                        info.name(),
                        args,
                        this.createAdminGroup()
                    ));
                    CommandResponse response;
                        while (true){
                        response = manager.receive();
                        if (response.responseId() == nextRequestId) break;
                        }
                } else if(info.needsFile()){
                    String fileName = args[0];
                    ArrayList<String> openedFiles = new ArrayList<>();
                    for (String n : openedFiles){
                        if (fileName.equals(n)){
                            System.out.println("Найдена бесконечная рекурсия, принудительное завершение выполнения скрипта");
                            return;
                        }
                    }
                    openedFiles.add(fileName);
                    try{
                    scanner = new Scanner(new File(fileName));
                    this.setScanner(scanner);
                    this.inConsole(false);
                    while (this.scanner.hasNextLine()){
                        this.readCommand();
                    }
                    this.inConsole(true);
                    this.setScanner(new Scanner(System.in));
                    } catch (FileNotFoundException e) {
                        this.inConsole(false);
                        this.setScanner(new Scanner(System.in));
                    }
                } else if(info.needsObject()){
                    manager.send(new CommandRequest(login, password,
                        ++nextRequestId,
                        info.name(),
                        args,
                        this.createGroup()
                    ));
                    CommandResponse response;
                        while (true){
                        response = manager.receive();
                        if (response.responseId() == nextRequestId) break;
                        }
                } else {
                    manager.send(new CommandRequest(login, password,
                        ++nextRequestId,
                        info.name(),
                        args,
                        null
                    ));
                    CommandResponse response;
                        while (true){
                        response = manager.receive();
                        if (response.responseId() == nextRequestId) break;
                        }
                        String answer = response.answer();
                        String[] groups = answer.split("SEPARATOR");
                        if (groups.length > 10) {
                            System.out.println("Всего элементов: " + (groups.length-1));
                            System.out.println("Выводится первые 10 элементов, для вывода следующих 10 элементов введите \"+\", для остановки введите любой другой символ");
                            String a1 = scanner.nextLine().trim();
                            if (!a1.equals("+")) {
                                return;
                            }
                            for (int i = 0; i < groups.length; i += 10) {
                                for (int j = i; j < i + 10 && j < groups.length; j++) {
                                    System.out.println(groups[j]);
                                }
                                if (i + 10 < groups.length) {
                                    String a = scanner.nextLine().trim();
                                    if (!a.equals("+")) {
                                        break;
                                    }
                                }
                            }
                        } else System.out.println(answer);

                }
        }
    }
    }




