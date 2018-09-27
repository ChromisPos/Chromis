/*
 Chromis  - The future of Point Of Sale
 Copyright (c) 2018 chromis.co.uk (John Lewis)
 http://www.chromis.co.uk

 Version V2018.7

 This file is part of chromis & its associated programs

 chromis is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 chromis is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with chromis.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.chromis.pos.dbmanager;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.util.AltEncrypter;

public class DbUser {

    private String dbEngine;
    private String dbLibrary;
    private String dbClass;
    private String serverName;
    private Boolean useSSL;
    private String serverPort;
    private String databaseName;
    private String userName;
    private String userPassword;
    private String URL;
    private Boolean newConnection = false;

    public DbUser() {

    }

    public Boolean getUserDetails() {
        //String dirname = System.getProperty("dirname.path");
        String dirname = System.getProperty("user.home");
        File file = new File(new File(dirname == null ? "./" : dirname), AppLocal.APP_ID + ".properties");
        if (!file.exists()) {
            setUserDefaults();
            newConnection = true;
            return true;
        }

        dbEngine = AppConfig.getInstance().getProperty("db.engine");
        int charPos;
        dbClass = AppConfig.getInstance().getProperty("db.driver");
        dbEngine = AppConfig.getInstance().getProperty("db.engine");
        userName = AppConfig.getInstance().getProperty("db.user");
        String sDBPassword = AppConfig.getInstance().getProperty("db.password");
        if (userName != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + userName);
            userPassword = cypher.decrypt(sDBPassword.substring(6));
        }

        if (dbEngine == null) {
            StringBuilder dburl = new StringBuilder();
            dburl.append(AppConfig.getInstance().getProperty("db.URL"));
//Parse the url to build missing details from older version
            dburl.replace(0, 5, "");
            System.out.println(dburl.substring(0, 5));
            switch (dburl.substring(0, 5)) {
                case "mysql":
                    dbLibrary = dirname + "\\lib\\mysql-connector-java-5.1.42.jar";
                    dbEngine = "MySQL";
                    useSSL = false;
                    dburl.replace(0, 8, "");
                    charPos = dburl.indexOf(":", 0);
                    serverName = dburl.substring(0, charPos);
                    dburl.replace(0, charPos + 1, "");
                    charPos = dburl.indexOf("/", 0);
                    serverPort = dburl.substring(0, charPos);
                    dburl.replace(0, charPos + 1, "");
                    databaseName = dburl.toString();
                    return false;
                case "postg":
                    dbLibrary = dirname + "\\lib\\postgresql-9.4-1201-jdbc4.jar";
                    dbEngine = "PostgreSQL";
                    useSSL = false;
                    dburl.replace(0, 13, "");
                    charPos = dburl.indexOf(":", 0);
                    serverName = dburl.substring(0, charPos);
                    dburl.replace(0, charPos + 1, "");
                    charPos = dburl.indexOf("/", 0);
                    serverPort = dburl.substring(0, charPos);
                    dburl.replace(0, charPos + 1, "");
                    databaseName = dburl.toString();
                    return false;
                case "derby":
                    dbLibrary = dirname + "\\lib\\derby-10.14.1.0.jar";
                    dbEngine = "Derby";
                    useSSL = false;
                    dburl.replace(0, 8, "");
                    serverName = "";
                    serverPort = "";
                    charPos = dburl.indexOf("-", 0);
                    dburl.replace(charPos, dburl.length(), "");
                    charPos = dburl.lastIndexOf("\\");
                    databaseName = dburl.substring(charPos + 1, dburl.length());
                    return false;
            }
        }
        
//Read in the properties details to the form and set dirty to false
        useSSL = AppConfig.getInstance().getBoolean("db.usessl");
        dbLibrary = AppConfig.getInstance().getProperty("db.driverlib");
        serverName = AppConfig.getInstance().getProperty("db.server");
        serverPort = AppConfig.getInstance().getProperty("db.port");
        databaseName = AppConfig.getInstance().getProperty("db.databasename");
        URL = buildURL(dbEngine);
        return false;
    }

    public DbUser(String dbEngine, String dbLibrary, String dbClass, String serverName, Boolean useSSL, String serverPort, String databaseName, String userName, String userPassword) {
        this.dbEngine = dbEngine;
        this.dbLibrary = dbLibrary;
        this.dbClass = dbClass;
        this.serverName = serverName;
        this.useSSL = useSSL;
        this.serverPort = serverPort;
        this.databaseName = databaseName;
        this.userName = userName;
        this.userPassword = userPassword;

    }

    private void setUserDefaults() {
        String dirname = System.getProperty("user.dir") == null ? "./" : System.getProperty("user.dir");
        dbEngine = "MySQL";
        dbLibrary = dirname + "\\lib\\mysql-connector-java-5.1.42.jar";
        dbClass = "com.mysql.jdbc.Driver";
        serverName = "localhost";
        useSSL = true;
        serverPort = "3306";
        databaseName = "chromispos";
        userName = "";
        userPassword = "";
        URL = buildURL(dbEngine);
    }

    public Boolean save() {
        // following line to be removed in future plans
        AppConfig.getInstance().load();
        URL = buildURL(dbEngine);
        try {
            AppConfig.getInstance().setProperty("db.URL", URL);
            AppConfig.getInstance().setProperty("db.engine", dbEngine);
            AppConfig.getInstance().setProperty("db.driverlib", dbLibrary);
            AppConfig.getInstance().setProperty("db.driver", dbClass);
            AppConfig.getInstance().setProperty("db.server", serverName);
            AppConfig.getInstance().setBoolean("db.sslversion", useSSL);
            AppConfig.getInstance().setProperty("db.port", serverPort);
            AppConfig.getInstance().setProperty("db.databasename", databaseName);
            AppConfig.getInstance().setProperty("db.user", userName);
            AltEncrypter cypher = new AltEncrypter("cypherkey" + userName);
            AppConfig.getInstance().setProperty("db.password", "crypt:" + cypher.encrypt(userPassword));
            AppConfig.getInstance().save();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private String buildURL(String engine) {
        StringBuilder url;
        switch (engine) {
            case "Derby":
                url = new StringBuilder();
                url.append("jdbc:derby:");
                url.append(new File(System.getProperty("user.home")));
                url.append("\\");
                url.append(databaseName);
                url.append("-database;create=true");
                return url.toString();
            case "PostgreSQL":
                url = new StringBuilder();
                url.append("jdbc:postgresql://");
                url.append(serverName);
                url.append(":");
                url.append(serverPort);
                url.append("/");
                url.append(databaseName);
                return url.toString();
            case "MySQL":
                url = new StringBuilder();
                url.append("jdbc:mysql://");
                url.append(serverName);
                url.append(":");
                url.append(serverPort);
                url.append("/");
                url.append(databaseName);
                url.append("?useSSL=false");
                return url.toString();
        }
        return null;
    }

    public void displayUser() {
        System.out.println("Engine         : " + dbEngine);
        System.out.println("Library        : " + dbLibrary);
        System.out.println("Class          : " + dbClass);
        System.out.println("SeverName      : " + serverName);
        System.out.println("SSL            : " + useSSL);
        System.out.println("Port           : " + serverPort);
        System.out.println("Database       : " + databaseName);
        System.out.println("UserName       : " + userName);
        System.out.println("Password       : " + userPassword);
        System.out.println("URL            : " + buildURL(dbEngine));
        System.out.println("New Connection : " + newConnection);
    }

    public String getURL() {
        URL = buildURL(dbEngine);
        return URL;
    }

    public String getDbEngine() {
        return dbEngine;
    }

    public void setDbEngine(String dbEngine) {
        this.dbEngine = dbEngine;
    }

    public String getDbLibrary() {
        return dbLibrary;
    }

    public void setDbLibrary(String dbLibrary) {
        this.dbLibrary = dbLibrary;
    }

    public String getDbClass() {
        return dbClass;
    }

    public void setDbClass(String dbClass) {
        this.dbClass = dbClass;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Boolean getUseSSL() {
        if (useSSL == null) {
            useSSL = false;
        }
        return useSSL;
    }

    public void setUseSSL(Boolean useSSL) {
        this.useSSL = useSSL;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

}
