package com.example.server.utilities;

import com.example.common.enums.*;
import com.example.common.models.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.sql.Date;
import java.util.HexFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RepositoryManager {
    private final ConnectionPool connectionPool;
    private static final Logger logger = Logger.getLogger(RepositoryManager.class.getName());
    public RepositoryManager (ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
    }
    public void deleteGroup(int id, int ownerID){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            String sql = "DELETE FROM study_groups WHERE id = ? AND owner_id = ? RETURNING id";
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) throw new RuntimeException("Нет доступа к данным");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {}
            if (connection != null) {
                connectionPool.releaseConnection(connection);
            }
        }
    }

    public void insertGroup(StudyGroup group, int ownerId){
        ResultSet resultSet = null;
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            String sql = "INSERT INTO study_groups (name, students_count, should_be_expelled, form_of_education, semester, creation_date, owner_id, coord_x, coord_y, admin_name, admin_weight, admin_eye_color, admin_hair_color, admin_nationality, admin_location_x, admin_location_y, admin_location_z, admin_location_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
            connection = connectionPool.getConnection();
            System.out.println(connection);
            statement = connection.prepareStatement(sql);
            statement.setString(1, group.getName());
            statement.setLong(2, group.getStudentsCount());
            statement.setLong(3, group.getShouldBeExpelled());
            statement.setString(4, group.getFormOfEducation().toString());
            statement.setString(5, group.getSemester().toString());
            statement.setDate(6, Date.valueOf(group.getCreationDate()));
            statement.setInt(7, ownerId);
            statement.setDouble(8, group.getCoordinates().getX());
            statement.setDouble(9, group.getCoordinates().getY());
            Person admin = group.getGroupAdmin();
            statement.setString(10, admin.getName());
            statement.setDouble(11, admin.getWeight());
            statement.setString(12, admin.getEyeColor().toString());
            statement.setString(13, admin.getHairColor().toString());
            statement.setString(14, admin.getNationality().toString());
            Location location = admin.getLocation();
            statement.setLong(15, location.getX());
            statement.setLong(16, location.getY());
            statement.setLong(17, location.getZ());
            statement.setString(18, location.getName());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                group.setId(resultSet.getInt(1));
            }
        } catch (SQLException e){ throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (SQLException e) {throw new RuntimeException(e);}
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connectionPool.releaseConnection(connection);
            }
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(password.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public int insertUser(String login, String password) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login не может быть пустым");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password не может быть пустым");
        }

        String sql = "INSERT INTO users (login, password_hash) VALUES (?, ?) RETURNING id";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, login);
            statement.setString(2, hashPassword(password));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    logger.info("Пользователь создан: " + login);
                    return resultSet.getInt("id");
                } else {
                    logger.info("Пользователь уже существует: " + login);
                    return -1;
                }
            }

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                logger.info("Дубликат login: " + login);
                return -1;
            }
            logger.log(Level.SEVERE, "Ошибка БД при вставке пользователя", e);
            throw new RuntimeException("Ошибка при вставке пользователя", e);
        }
    }

    public StudyGroup mapToStudyGroup(ResultSet resultSet) throws SQLException{
        Coordinates coordinates = new Coordinates(
                resultSet.getDouble("coord_x"),
                resultSet.getLong("coord_y")
        );
        Location location = new Location(
                resultSet.getLong("admin_location_x"),
                resultSet.getLong("admin_location_y"),
                resultSet.getLong("admin_location_z"),
                resultSet.getString("admin_location_name")
        );

        String eyeColorStr = resultSet.getString("admin_eye_color");
        Color eyeColor = eyeColorStr != null ? Color.valueOf(eyeColorStr) : null;

        Double weight = resultSet.getDouble("admin_weight");
        if (resultSet.wasNull()) weight = null;

        Person admin = new Person(
                resultSet.getString("admin_name"),
                weight,
                eyeColor,
                Color.valueOf(resultSet.getString("admin_hair_color")),
                Country.valueOf(resultSet.getString("admin_nationality")),
                location
        );

        StudyGroup group = new StudyGroup(
                resultSet.getString("name"),
                coordinates,
                resultSet.getLong("students_count"),
                resultSet.getInt("should_be_expelled"),
                FormOfEducation.valueOf(resultSet.getString("form_of_education")),
                Semester.valueOf(resultSet.getString("semester")),
                admin
        );

        group.setId(resultSet.getInt("id"));
        group.setCreationDate(resultSet.getDate("creation_date").toLocalDate());
        group.setOwnerId(resultSet.getInt("owner_id"));
        return group;
    }


    public Set<StudyGroup> selectAllGroups() {
        String sql = "SELECT id, name, students_count, should_be_expelled, " +
                "form_of_education, semester, creation_date, owner_id, " +
                "coord_x, coord_y, " +
                "admin_name, admin_weight, admin_eye_color, " +
                "admin_hair_color, admin_nationality, " +
                "admin_location_x, admin_location_y, admin_location_z, admin_location_name " +
                "FROM study_groups";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Set<StudyGroup> groups = ConcurrentHashMap.newKeySet();;

        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                StudyGroup group = mapToStudyGroup(resultSet);
                groups.add(group);
            }

            return groups;

        } catch (SQLException e) {
            e.printStackTrace();
            return groups;
        } finally {
            try { if (resultSet != null) resultSet.close(); } catch (SQLException e) {}
            try { if (statement != null) statement.close(); } catch (SQLException e) {}
            if (connection != null) {
                connectionPool.releaseConnection(connection);
            }
        }
    }

    public StudyGroup selectStudyGroup(int id){
        String sql = "SELECT * FROM study_groups WHERE id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapToStudyGroup(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } catch (SQLException e) {}
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {}
            if (connection != null) connectionPool.releaseConnection(connection);
        }
    }






    public int countLessThan(Person admin){
        String sql = "SELECT COUNT(*) FROM study_groups WHERE admin_weight < ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setDouble(1, admin.getWeight());
            resultSet = statement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e){
            throw new RuntimeException("Не удалось выполнить countLessThanGroupAdmin");
        } finally {
            try { if (statement != null) statement.close(); } catch (SQLException e) {}
            if (connection != null) connectionPool.releaseConnection(connection);
        }
    }

    public int clearGroups(int ownerID){
        String sql = "DELETE FROM study_groups WHERE owner_ID = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, ownerID);
            return statement.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException("Не удалось отчистить коллекцию: " + e);
        } finally {
            try { if (statement != null) statement.close(); } catch (SQLException e) {}
            if (connection != null) connectionPool.releaseConnection(connection);
        }
    }

    public int removeGreaterSQL(String nameOfGroup, int ownerID){
        String sql = "DELETE FROM study_groups WHERE owner_ID = ? AND name > ?";
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, ownerID);
            statement.setString(2, nameOfGroup);
            return statement.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException("Не удалось выполнить RemoveGreater");
        } finally {
            try { if (statement != null) statement.close(); } catch (SQLException e) {}
            if (connection != null) connectionPool.releaseConnection(connection);
        }
    }


    public int selectOwnerId(String login, String password) {
        String sql = "SELECT id FROM users WHERE login = ? AND password_hash = ?";
        String hash = hashPassword(password);
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, login);
            statement.setString(2, hash);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось выполнить selectOwnerId");
        } finally {
            try { if (resultSet != null) resultSet.close(); } catch (SQLException e) {}
            try { if (statement != null) statement.close(); } catch (SQLException e) {}
            if (connection != null) connectionPool.releaseConnection(connection);
        }
    }


    public  HashMap<Integer, List<String>> selectAllUsers() {
        String sql = "SELECT * FROM users";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        HashMap<Integer, List<String>> mapOfUsers = new HashMap<>();
        try {
            connection = connectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                mapOfUsers.put(resultSet.getInt("id"),
                        Arrays.asList(resultSet.getString("login"), resultSet.getString("password_hash")));
            }
            return mapOfUsers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try { if (resultSet != null) resultSet.close(); } catch (SQLException e) {}
            try { if (statement != null) statement.close(); } catch (SQLException e) {}
            if (connection != null) connectionPool.releaseConnection(connection);
        }
    }
}
