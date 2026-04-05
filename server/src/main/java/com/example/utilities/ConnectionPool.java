package com.example.utilities;

import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionPool implements AutoCloseable {
    private static final Logger logger = Logger.getLogger(ConnectionPool.class.getName());
    private static final int size = 10;

    private final ArrayBlockingQueue<Connection> pool;
    private volatile boolean closed = false;
    private static String url;
    private static String user;
    private static String password;
    public ConnectionPool(String url, String user, String password) {
        this.pool = new ArrayBlockingQueue<>(size, true);
        this.url = url;
        this.user = user;
        this.password = password;
        initialize(url, user, password);
    }

    private void initialize(String url, String user, String password) {
        try {
            for (int i = 0; i < size; i++) {
                Connection conn = createConnection();
                pool.offer(conn);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Невозможно создать ConnectionPool", e);
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }


    public Connection getConnection() throws SQLException {
        if (closed) {
            throw new SQLException("ConnectionPool закрыт");
        }

        try {
            Connection conn = pool.poll(3, TimeUnit.SECONDS);

            if (conn == null) {
                throw new SQLException("Tаймаут ожидания пула");
            }

            if (!conn.isValid(2)) {
                logger.warning("Cоединение разорвано");
                return createConnection();
            }

            return conn;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Прерывание соединения", e);
        }
    }


    public void releaseConnection(Connection conn) {
        if (conn == null || closed) {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
            return;
        }

        try {
            if (conn.isValid(2) && !conn.isClosed()) {
                if (!conn.getAutoCommit()) conn.setAutoCommit(true);
                try { conn.rollback(); } catch (SQLException ignored) {}
                if (!pool.offer(conn, 2, TimeUnit.SECONDS)) {
                    logger.warning("Пул заполнен");
                    conn.close();
                }
            } else {
                conn.close();
            }
        } catch (SQLException | InterruptedException e) {
            logger.log(Level.WARNING, "Ошибка соединения с БД", e);
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }


    @Override
    public void close() {
        closed = true;
        Connection conn;
        while ((conn = pool.poll()) != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
        logger.info("ConnectionPool закрыт");
    }
}