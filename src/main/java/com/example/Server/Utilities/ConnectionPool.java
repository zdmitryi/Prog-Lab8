package com.example.Server.Utilities;

import com.jcraft.jsch.JSchException;
import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionPool implements AutoCloseable {
    private static final Logger logger = Logger.getLogger(ConnectionPool.class.getName());
    private static final int size = 10;

    private final ArrayBlockingQueue<Connection> pool;
    private SshTunnel tunnel;
    private String url;
    private final String user;
    private final String password;
    private volatile boolean closed = false;

    public ConnectionPool() {
        this.user = "s502358";
        this.password = "";
        this.pool = new ArrayBlockingQueue<>(size, true);

        initialize();
    }

    private void initialize() {
        try {
            this.tunnel = new SshTunnel(
                    "se.ifmo.ru", 2222, user, "", "pg", 5432
            );
            this.url = "jdbc:postgresql://localhost:" + tunnel.getLocalPort() + "/studs";

            for (int i = 0; i < size; i++) {
                Connection conn = createConnection();
                pool.offer(conn);
            }

        } catch (JSchException | SQLException e) {
            throw new RuntimeException("Cannot initialize ConnectionPool", e);
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }


    public Connection getConnection() throws SQLException {
        if (closed) {
            throw new SQLException("ConnectionPool is closed");
        }

        try {
            Connection conn = pool.poll(3, TimeUnit.SECONDS);

            if (conn == null) {
                throw new SQLException("Tаймаут ожидания пула");
            }

            // Проверяем, живо ли соединение
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

        // Закрываем SSH-туннель
        if (tunnel != null) {
            try { tunnel.close(); } catch (Exception e) {
                logger.log(Level.WARNING, "Error closing tunnel", e);
            }
        }
        logger.info("ConnectionPool closed");
    }

    public int getIdleCount() {
        return pool.size();
    }
}