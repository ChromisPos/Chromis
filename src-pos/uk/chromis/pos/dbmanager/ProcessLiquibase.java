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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.DriverWrapper;
import uk.chromis.pos.util.AltEncrypter;

/**
 *
 * @author John
 */
public class ProcessLiquibase {

    static final Dimension SCREEN_DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();

    public static Boolean DBFAILED = true;
    private static Connection con;
    private static PreparedStatement stmt2;

    public ProcessLiquibase() {

    }

    public Boolean performAction(String changelog) {
        String db_user = (AppConfig.getInstance().getProperty("db.user"));
        String db_url = (AppConfig.getInstance().getProperty("db.URL"));
        String db_password = (AppConfig.getInstance().getProperty("db.password"));

        if (db_user != null && db_password != null && db_password.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
            db_password = cypher.decrypt(db_password.substring(6));
        }

        Liquibase liquibase = null;
        try {
            Connection con = DriverManager.getConnection(db_url, db_user, db_password);
            ClassLoader cloader = new URLClassLoader(new URL[]{new File(AppConfig.getInstance().getProperty("db.driverlib")).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getInstance().getProperty("db.driver"), true, cloader).newInstance()));
// lets check if the database has passed new database test
            try {

                PreparedStatement stmt2 = con.prepareStatement("select count(*) from databasechangelog where id='V0.70 indicator'");
                ResultSet rs = stmt2.executeQuery();
                if (rs.next()) {
                    if (rs.getInt(1) != 0) {
                        changelog = "uk/chromis/pos/liquibase/scripts/update/dbupdate.xml";
                    }
                }

                stmt2 = con.prepareStatement("select count(*) from databasechangelog where id='new database'");
                rs = stmt2.executeQuery();
                if (rs.next()) {
                    if (rs.getInt(1) != 0) {
                        changelog = "uk/chromis/pos/liquibase/upgrade/systemupdate.xml";
                    }
                }
            } catch (SQLException ex) {
                System.out.println("systemudate.xml");
            }
// Ensure there are no liquibase locks
            try {
                stmt2 = con.prepareStatement("drop table databasechangeloglock");
                stmt2.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("databasechangeloglock");
            }

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(DriverManager.getConnection(db_url, db_user, db_password)));
            liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database);
            liquibase.setRedirect(true);
            liquibase.setErrorFile(System.getProperty("user.home") + "/liquibase.log");
            liquibase.update("implement");
            DBFAILED = false;

            changelog = "uk/chromis/pos/liquibase/scripts/update/dbupdate.xml";
            database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(DriverManager.getConnection(db_url, db_user, db_password)));
            liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database);
            liquibase.setRedirect(true);
            liquibase.setErrorFile(System.getProperty("user.home") + "/liquibase.log");
            liquibase.update("implement");
            DBFAILED = false;
            
            
        } catch (DatabaseException | MalformedURLException | SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ProcessLiquibase.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (LiquibaseException ex) {
            System.out.println(ex);
            return false;
        } finally {
            if (con != null) {
                try {
                    con.rollback();
                    con.close();
                } catch (SQLException e) {
                    //nothing to do
                }
            }
        }
        insertMenuEntry(db_user, db_url, db_password);  //insert in to menu.root
        insertNewButtons(db_user, db_url, db_password); //insert in to ticket.buttons
        removeMenuEntry(db_user, db_url, db_password);  //insert in to menu.root
        return true;
    }

    private static void insertMenuEntry(String db_user, String db_url, String db_password) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_user, db_password);
            String decodedData = "";
            Statement stmt = (Statement) con.createStatement();
            // get the menu from the resources table
            String SQL = "select * from resources where name='Menu.Root'";
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                byte[] bytesData = rs.getBytes("content");
                decodedData = new String(bytesData);
            }
            // get the date from the menu entries table
            SQL = "select * from add_newmenuentry ";
            rs = stmt.executeQuery(SQL);
            // while we have some entries lets process them
            while (rs.next()) {
                // lets check if the entry is in the menu
                int index1 = decodedData.indexOf(rs.getString("entry"));
                if (index1 == -1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(rs.getString("follows"));
                    sb.append("\");\n        submenu.addPanel(\"");
                    sb.append(rs.getString("graphic"));
                    sb.append("\", \"");
                    sb.append(rs.getString("title"));
                    sb.append("\", \"");
                    sb.append(rs.getString("entry"));
                    decodedData = decodedData.replaceAll(rs.getString("follows"), sb.toString());
                    byte[] bytesData = decodedData.getBytes();
                    String SQL2 = "update resources set content = ? where name = 'Menu.Root' ";
                    PreparedStatement stmt2 = con.prepareStatement(SQL2);
                    stmt2.setBytes(1, bytesData);
                    stmt2.executeUpdate();
                }
            }
            SQL = "delete from add_newmenuentry ";
            PreparedStatement stmt2 = con.prepareStatement(SQL);
            stmt2.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProcessLiquibase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    private static void removeMenuEntry(String db_user, String db_url, String db_password) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_user, db_password);
            String decodedData = "";
            Statement stmt = (Statement) con.createStatement();
            // get the menu from the resources table
            String SQL = "select * from resources where name='Menu.Root'";
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                byte[] bytesData = rs.getBytes("content");
                decodedData = new String(bytesData);
            }

            String[] lines = decodedData.split(System.getProperty("line.separator"));

            // get the date from the menu entries table
            SQL = "select * from remove_menuentry ";
            rs = stmt.executeQuery(SQL);
            StringBuilder sb = new StringBuilder();
            int numberOfLines = lines.length;
            // while we have some entries lets process them
            while (rs.next()) {
                for (int i = 0; i < numberOfLines; i++) {
                    if (lines[i].contains("\"" + rs.getString("entry") + "\"")) {
                        lines[i] = "";
                    }
                    sb.append(lines[i]);
                    sb.append("\n");
                }
            }
            for (int i = 0; i < numberOfLines; i++) {
                if (lines[i].length() != 0) {
                    sb.append(lines[i]);
                    sb.append("\n");
                }

            }

            byte[] bytesData = sb.toString().getBytes();
            String SQL2 = "update resources set content = ? where name = 'Menu.Root' ";
            PreparedStatement stmt2 = con.prepareStatement(SQL2);
            stmt2.setBytes(1, bytesData);
            stmt2.executeUpdate();

            SQL = "delete from remove_menuentry ";
            stmt2 = con.prepareStatement(SQL);
            stmt2.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Remove table does not yet exist");
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    private static void insertNewButtons(String db_user, String db_url, String db_password) {
        try {
            Connection con = DriverManager.getConnection(db_url, db_user, db_password);
            String decodedData = "";
            Statement stmt = (Statement) con.createStatement();
            // get the buttons from the resources table
            String SQL = "select * from resources where name='Ticket.Buttons'";
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                byte[] bytesData = rs.getBytes("content");
                decodedData = new String(bytesData);
            }
            // get the date from the menu entries table
            SQL = "select * from add_newbutton ";
            rs = stmt.executeQuery(SQL);
            // while we have some entries lets process them
            while (rs.next()) {
                // lets check if the entry is in the menu
                int index1 = decodedData.indexOf(rs.getString("entry"));
                if (index1 == -1) {
                    StringBuilder sb = new StringBuilder();
                    if (rs.getString("entry").substring(0, 2).equals("!!")) {
                        sb.append("    <!-- ");
                        sb.append(rs.getString("entry").substring(2, rs.getString("entry").length()));
                        sb.append(" -->\n");

                    } else {
                        sb.append("    <!-- <");
                        sb.append(rs.getString("entry"));
                        sb.append("/> -->\n");
                    }
                    sb.append("</configuration>");
                    decodedData = decodedData.replaceAll("</configuration>", sb.toString());
                    byte[] bytesData = decodedData.getBytes();
                    String SQL2 = "update resources set content = ? where name = 'Ticket.Buttons' ";
                    PreparedStatement stmt2 = con.prepareStatement(SQL2);
                    stmt2.setBytes(1, bytesData);
                    stmt2.executeUpdate();
                }
            }
            SQL = "delete from add_newbutton ";
            PreparedStatement stmt2 = con.prepareStatement(SQL);
            stmt2.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ProcessLiquibase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}
