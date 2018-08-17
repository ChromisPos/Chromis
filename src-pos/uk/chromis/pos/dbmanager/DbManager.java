/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c)2015-2016
**    http://www.chromis.co.uk
**
**    This file is part of Chromis POS Version V0.60.2 beta
**
**    Chromis POS is free software: you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation, either version 3 of the License, or
**    (at your option) any later version.
**
**    Chromis POS is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>
**
**
 */
package uk.chromis.pos.dbmanager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Session;
import uk.chromis.pos.dialogs.JOpenWarningDlg;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.forms.DriverWrapper;
import uk.chromis.pos.util.AltEncrypter;
import uk.chromis.pos.util.SessionFactory;

/**
 *
 * @author John
 */
public class DbManager {

    private static AltEncrypter cypher;
    private Session session;
    private DataLogicSystem m_dlSystem;
    private String sDBVersion;
    private Boolean admin;
    private Boolean testResult;
    private final Connect connectTest = new Connect();
    private final NewDB newDB = new NewDB();

    public DbManager(Boolean admin) {
        this.admin = admin;
    }

    public boolean DBChecks() {

        // if this is not a new setup then there should be a properties file
        File file = new File(System.getProperty("user.home"), AppLocal.APP_ID + ".properties");
        if (!file.exists()) {
            //properties file does not exists - test for embedded first as this does not require properties file if everything is default
            if (DbManager.embeddedExists()) {
                // embedded db exists so lets check for updates                
                session = SessionFactory.getInstance().getSession();
                m_dlSystem = new DataLogicSystem();
                m_dlSystem.init(session);
                sDBVersion = readDataBaseVersion();
                updateDB();
                connectTest.remove();
                return true;
            } else {
                //embedded db does not exist so invoke new db process
                configuration();
                if (newDB.getStatus()) {
                    return true;
                }
            }
        } else {
            //properties file does exists attempt to connect to the database in the file                
            connectTest.setVisible(true);
            testResult = testConnection();
            if (testResult) {
                // we can coonect to the datbase but it might not have been initialized yet
                // so lets check that            
                session = SessionFactory.getInstance().getSession();
                m_dlSystem = new DataLogicSystem();
                m_dlSystem.init(session);
                sDBVersion = readDataBaseVersion();
                if (sDBVersion == null) {
                    configuration();
                    if (newDB.getStatus()) {
                        return true;
                    }
                } else {
                    updateDB();
                    connectTest.remove();
                    return true;
                }
            }
            while (!testResult) {
                JOpenWarningDlg wDlg = new JOpenWarningDlg("Database connection error", AppLocal.getIntString("message.retryorconfig"), true, true);
                wDlg.setModal(true);
                wDlg.setVisible(true);
                int rc = JOpenWarningDlg.CHOICE;
                switch (rc) {
                    case 1: // configuration                        
                        configuration();
                        if (newDB.getStatus()) {
                            return true;
                        }
                        break;
                    case 2:
                        System.exit(0);
                }
                connectTest.setVisible(true);
                testResult = testConnection();
            }
        }
        return false;
    }

    private void updateDB() {
        connectTest.setVisible(false);

        int dbVersion;
        if (!sDBVersion.equals(AppLocal.APP_VERSION)) {
            try {
                // Ready for future release, this will prevent upgrade running against older version
                // if (AppLocal.APP_VERSIONINT > readDataBaseIntVersion()) {

                dbVersion = m_dlSystem.getVerisonInt();
            } catch (BasicException ex) {
                dbVersion = 1;

            }

            if (AppLocal.APP_VERSIONINT > dbVersion) {
                UpdateDB updb = new UpdateDB();
                updb.setVersion(readDataBaseVersion());
                SwingUtilities.invokeLater(() -> updb.initSwingComponents());
                while (updb.isWorking()) {
                    try {
                        TimeUnit.SECONDS.sleep(1); // wait for 1 second and test again
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }
    }

    private void configuration() {
        connectTest.setVisible(false);
        newDB.setWorking(true);
        SwingUtilities.invokeLater(() -> newDB.initSwingComponents());
        while (newDB.isWorking()) {
            try {
                TimeUnit.SECONDS.sleep(1); // wait for 1 second and test again
            } catch (InterruptedException ex) {
            }
        }
        //we only return here if the db creation was good
        newDB.remove();
    }

    private boolean testConnection() {

        try {
            ClassLoader cloader = new URLClassLoader(new URL[]{new File(AppConfig.getInstance().getProperty("db.driverlib")).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getInstance().getProperty("db.driver"), true, cloader).newInstance()));
        } catch (ClassNotFoundException | MalformedURLException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(DbManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        Connection connection = null;

        String dbURL = (AppConfig.getInstance().getProperty("db.URL"));
        String sDBUser = AppConfig.getInstance().getProperty("db.user");
        String sDBPassword = AppConfig.getInstance().getProperty("db.password");
        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
        }
        try {
            connection = DriverManager.getConnection(dbURL, sDBUser, sDBPassword);
        } catch (SQLException ex) {
        }
        if (connection == null) {
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            connectTest.setVisible(false);
            return true;
        }
        try {
            connection.close();
            try {
                 TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }

            connectTest.setVisible(false);
            return true;
        } catch (Exception e) {
        }
        connectTest.setVisible(false);
        return false;
    }

    private String readDataBaseVersion() {
        try {
            return m_dlSystem.findVersion();
        } catch (BasicException ed) {
            return null;
        }
    }

    private Integer readDataBaseIntVersion() {
        try {
            return m_dlSystem.getVerisonInt();
        } catch (BasicException ed) {
            return null;
        }
    }

    private String getDbVersion() {
        String sdbmanager = m_dlSystem.getDBVersion();
        if ("MySQL".equals(sdbmanager)) {
            return ("m");
        } else if ("PostgreSQL".equals(sdbmanager)) {
            return ("p");
        } else if ("Apache Derby".equals(sdbmanager)) {
            return ("d");
        } else if ("Derby".equals(sdbmanager)) {
            return ("d");
        } else {
            return ("x"); // unknown database
        }
    }

    public static Connection getRemoteConnection(String sDBUser, String sDBPassword, String sURL) {
        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
            cypher = new AltEncrypter("cypherkey" + sDBUser);
            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
        }
        try {
            return DriverManager.getConnection(sURL, sDBUser, sDBPassword);
        } catch (SQLException ex) {
            return null;
        }
    }

    public static Boolean exists() {
        // routine to check for database and create if required
        // get the details from the properties file
        String db_user = (AppConfig.getInstance().getProperty("db.user"));
        String db_url = (AppConfig.getInstance().getProperty("db.URL"));
        String db_password = (AppConfig.getInstance().getProperty("db.password"));
        if (db_user != null && db_password != null && db_password.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
            db_password = cypher.decrypt(db_password.substring(6));
        }
        // test for an application table
        Boolean dbFound = false;
        try {
            Connection tmpConnection = getRemoteConnection(db_user, db_password, db_url);
            // we have a connection to the database so look for application table
            String sql = "select * from applications";
            Statement stmt;
            stmt = (Statement) tmpConnection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                dbFound = true;
            }
            stmt.close();
            tmpConnection.close();
        } catch (SQLException ex) {
        }
        return dbFound;
    }

    public static Boolean embeddedExists() {
        // routine to check for database and create if required
        // get the details from the properties file        
        String db_url = "jdbc:derby:" + new File(new File(System.getProperty("user.home")), AppLocal.APP_ID + "-database").getAbsolutePath() + ";create=true";
        // test for an application table
        Boolean dbFound = false;
        try {
            Connection tmpConnection = getRemoteConnection("", "", db_url);
            // we have a connection to the database so look for application table
            String sql = "select * from applications";
            Statement stmt;
            stmt = (Statement) tmpConnection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                dbFound = true;
            }
            stmt.close();
            tmpConnection.close();
        } catch (SQLException ex) {
        }
        return dbFound;
    }

}
