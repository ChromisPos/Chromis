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
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DriverWrapper;
import uk.chromis.pos.forms.StartupDialog;
import uk.chromis.pos.util.AltEncrypter;

/**
 *
 *
 */
public class DbUtils {

    private static AltEncrypter cypher;

    public static TableModel resultSetToTableModel(ResultSet rs) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            Vector columnNames = new Vector();

            // Get the column names
            for (int column = 0; column < numberOfColumns; column++) {
                columnNames.addElement(metaData.getColumnLabel(column + 1));
            }
            // Get all rows.
            Vector rows = new Vector();
            while (rs.next()) {
                Vector newRow = new Vector();

                for (int i = 1; i <= numberOfColumns; i++) {
                    newRow.addElement(rs.getObject(i));
                }

                rows.addElement(newRow);
            }
            return new DefaultTableModel(rows, columnNames);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static Boolean basicConnectionTest(DbUser dbUser) {
        try {
            ClassLoader cloader = new URLClassLoader(new URL[]{new File(dbUser.getDbLibrary()).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(dbUser.getDbClass(), true, cloader).newInstance()));
            DriverManager.setLoginTimeout(5);
            Connection connection = (Connection) DriverManager.getConnection(dbUser.getURL(), dbUser.getUserName(), dbUser.getUserPassword());
            Boolean isValid = (connection == null) ? false : connection.isValid(5);
            DatabaseManager.waitForConnection.countDown();
            return isValid;
        } catch (MalformedURLException | SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            DatabaseManager.waitForConnection.countDown();
            return false;
        }
    }

    public static Object[] connectionTest(DbUser dbUser) {
        return connectionTest(dbUser, false);
    }

    public static Object[] connectionTest(DbUser dbUser, Boolean useLatch) {
        String errorMsg = "";
        Boolean isConnected = false;
        Connection connection = null;
        Boolean isValid;
        Boolean isEmpty = true;

        int versionInt = 0;
        String version = "";
        try {
            ClassLoader cloader = new URLClassLoader(new URL[]{new File(dbUser.getDbLibrary()).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(dbUser.getDbClass(), true, cloader).newInstance()));
            DriverManager.setLoginTimeout(5);
            connection = (Connection) DriverManager.getConnection(dbUser.getURL(), dbUser.getUserName(), dbUser.getUserPassword());
            isValid = (connection == null) ? false : connection.isValid(5);
            if (isValid) {
                isConnected = true;
                //Test if the database is empty or
                try {
                    DatabaseMetaData meta = connection.getMetaData();
                    ResultSet rs = meta.getTables(null, null, "%", null);
                    if (rs.next()) {
                        isEmpty = false;
                    }
                } catch (SQLException ex) {

                }
                try {
                    //Get the application Version
                    String sql = "Select * from applications ";
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    if (rs.next()) {
                        version = rs.getString("version");
                        versionInt = rs.getInt("versionint");
                    }
                } catch (SQLException ex) {

                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }
            }
        } catch (SQLException ex) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
            switch (ex.getSQLState()) {
                //unable to connect to server
                case "08S01":
                case "08001":
                    errorMsg = AppLocal.getIntString("Message.unableToConnect");
                    break;
                //database not available
                case "42000":
                case "3D000":
                    errorMsg = AppLocal.getIntString("Message.databaseNotFound");
                    break;
                //Authentication error
                case "28000":
                case "28P01":
                    errorMsg = AppLocal.getIntString("Message.authenticationError");
                    break;
                default:
                    errorMsg = AppLocal.getIntString("Message.unknownError");
            }
        } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
        }
        Object[] result = new Object[]{isConnected, errorMsg, connection, isEmpty, version, versionInt};
        if (useLatch) {
            DatabaseManager.waitForConnection.countDown();
        }
        return result;
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

    public static Connection getConnection() {
        String sDBUser = AppConfig.getInstance().getProperty("db.user");
        String sDBPassword = AppConfig.getInstance().getProperty("db.password");
        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
            cypher = new AltEncrypter("cypherkey" + sDBUser);
            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
        }
        try {
            return DriverManager.getConnection(AppConfig.getInstance().getProperty("db.url"), sDBUser, sDBPassword);
        } catch (SQLException ex) {
            return null;
        }
    }

    public static void checkJava() {
        String sJavaVersion = System.getProperty("java.vm.specification.version");
        double dJavaVersion = Double.parseDouble(sJavaVersion);
        //String sJavaVersion = System.getProperty("java.version");
        //double dJavaVersion = Double.parseDouble(sJavaVersion.substring(0, sJavaVersion.indexOf('.', sJavaVersion.indexOf('.') + 1)));

        if (dJavaVersion < 1.8) {
            StartupDialog dialog = new StartupDialog();
            JFrame frame = new JFrame("");
            JPanel dialogPanel = new JPanel();

            dialogPanel.add(dialog);
            JOptionPane.showMessageDialog(frame,
                    dialogPanel,
                    "Incorrect Java version ",
                    JOptionPane.PLAIN_MESSAGE);
            System.exit(-1);
        }
    }
}
