package com.example.Server.Utilities;


import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SshTunnel implements AutoCloseable {
        private final Session session;
        private final int localPort;
        public SshTunnel(String host, int port, String user, String password,
                         String remoteHost, int remotePort) throws JSchException {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            this.localPort = session.setPortForwardingL(0, remoteHost, remotePort);
        }
        public int getLocalPort() {
            return localPort;
        }
        @Override
        public void close() {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
}

