package com.crazyxacker.apps.drop2deck.ftp;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FTPServer {
    private final FtpServer server;

    private FTPServer(Map<String, Ftplet> ftpletsMap, String homePath, String userName, String userPassword, int port) throws IOException {
        // Create FtpServer factory
        FtpServerFactory serverFactory = new FtpServerFactory();

        // Add Ftplets
        if (ftpletsMap != null) {
            serverFactory.setFtplets(ftpletsMap);
        }

        // Replace default listener
        serverFactory.addListener("default", createListener(port));

        // Create user manager
        serverFactory.setUserManager(createUserManager(homePath, userName, userPassword));

        // Create server
        server = serverFactory.createServer();
    }

    /**
     * Create {@link FtpServer} instance for use with {@link #start()} and {@link #stop()} methods
     * @param ftpletsMap {@link Map} with custom {@link Ftplet} implementations
     * @param homePath {@link String} path to home directory
     * @param userName {@link User} username
     * @param userPassword {@link User} password
     * @param port {@link FtpServer} port
     * @throws IOException if properties {@link File} doesn't exists and can't be created
     */
    public static FTPServer create(Map<String, Ftplet> ftpletsMap, String homePath, String userName, String userPassword, int port) throws IOException {
        return new FTPServer(ftpletsMap, homePath, userName, userPassword, port);
    }

    /**
     * Check if {@link FtpServer} is stopped
     */
    public boolean isStopped() {
        return server.isStopped();
    }

    /**
     * Start {@link FtpServer}
     */
    public void start() throws FtpException {
        server.start();
    }

    /**
     * Stop launched {@link FtpServer}
     */
    public void stop() {
        server.stop();
    }

    /**
     * Create {@link Listener} with custom {@link FtpServer} port
     * @param port {@link FtpServer} port
     * @return {@link Listener} {@link FtpServer} listener
     */
    private static Listener createListener(int port) {
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(port);
        return listenerFactory.createListener();
    }

    /**
     * Create {@link UserManager} instance for {@link FtpServer}
     * @param homePath {@link String} path to home directory
     * @param userName {@link User} username
     * @param userPassword {@link User} password
     * @return {@link UserManager} instance
     * @throws IOException if properties {@link File} doesn't exists and can't be created
     */
    private static UserManager createUserManager(String homePath, String userName, String userPassword) throws IOException {
        UserManager userManager = createUserManagerFactory().createUserManager();
        try {
            userManager.save(createUser(homePath, userName, userPassword));
        } catch (FtpException ex) {
            ex.printStackTrace();
        }
        return userManager;
    }

    /**
     * Create {@link PropertiesUserManagerFactory} factory for file user properties and plain passwords
     * @return {@link PropertiesUserManagerFactory} factory
     * @throws IOException if properties {@link File} doesn't exists and can't be created
     */
    private static PropertiesUserManagerFactory createUserManagerFactory() throws IOException {
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setFile(getUserPropertiesFile());
        userManagerFactory.setPasswordEncryptor(new PasswordEncryptor() {
            @Override
            public String encrypt(String password) {
                return password;
            }

            @Override
            public boolean matches(String passwordToCheck, String storedPassword) {
                return passwordToCheck.equals(storedPassword);
            }
        });
        return userManagerFactory;
    }

    /**
     * Create R/W access {@link User} with provided username and password
     * @param homePath {@link String} path to home directory
     * @param userName {@link User} username
     * @param userPassword {@link User} password
     * @return {@link User} object with credentials and R/W access
     */
    private static User createUser(String homePath, String userName, String userPassword) {
        BaseUser user = new BaseUser();
        user.setName(userName);
        user.setPassword(userPassword);
        user.setHomeDirectory(homePath);

        List<Authority> authorities = new ArrayList<>();
        authorities.add(new WritePermission());
        user.setAuthorities(authorities);

        return user;
    }

    /**
     * Create properties {@link File} where {@link User}s will be stored
     * @return properties {@link File}
     * @throws IOException if properties {@link File} doesn't exists and can't be created
     */
    private static File getUserPropertiesFile() throws IOException {
        File file = new File("./users.properties");
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
}
