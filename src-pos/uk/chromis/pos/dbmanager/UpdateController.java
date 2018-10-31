
/*
 Chromis  - The future of Point Of Sale
 Copyright (c) 2015 chromis.co.uk (John Lewis)
 http://www.chromis.co.uk

 kitchen Screen v1.42

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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import uk.chromis.data.loader.ConnectionFactory;
import uk.chromis.pos.dialogs.JAlert;
import uk.chromis.pos.forms.AppLocal;

/**
 * FXML Controller class
 *
 */
public class UpdateController implements Initializable {

    public Button btnExit;
    public Button btnUpdateDB;
    public Label lblDbVersion;
    public Label lblAppVersion;
    public Label lblProgressMsg;
    public TextArea dbUpdateMsg;
    public TextField txtDbVersion;
    public TextField txtAppVersion;

    public ProgressBar pb;

    private Boolean dbUpdate = false;
    private Task worker;
    private Boolean updating = true;
    private Thread doit;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblProgressMsg.setText("");
        txtAppVersion.setText(AppLocal.APP_VERSION);
        lblDbVersion.setText(AppLocal.getIntString("Label.dbVersionFound"));
        lblAppVersion.setText(AppLocal.getIntString("Label.applicationVersion"));
        dbUpdateMsg.setText(AppLocal.getIntString("Message.updateMessage"));
        btnExit.setText(AppLocal.getIntString("Button.exit"));
        btnUpdateDB.setText(AppLocal.getIntString("Button.updateDatabase"));
    }

    public Boolean isUpdating() {
        return updating;
    }

    public void setDbVersion(String dbVersion) {
        txtDbVersion.setText(dbVersion);
    }

    public void handleExitClick() {
        System.exit(0);
    }

    private void alertDialog(int alert, String title, String headerText, String contentText) {
        JAlert jAlert = new JAlert(alert);
        jAlert.setTitle(title);
        jAlert.setHeaderText(headerText);
        jAlert.setContextText(contentText);
        jAlert.setVisible(true);
    }

    public void handleUpdateClick() throws IOException {
        Platform.runLater(new Runnable() {
            public void run() {
                pb.requestFocus();
            }
        });

        pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        lblProgressMsg.setText(AppLocal.getIntString("Message.updatingDatabase"));
        lblProgressMsg.setTextFill(Color.web("#0000FF"));
        btnExit.setDisable(true);
        btnUpdateDB.setDisable(true);
        Connection connection = ConnectionFactory.getInstance().getConnection();
        worker = updatedb(connection);
        new Thread(worker).start();

        worker.setOnSucceeded(e -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (dbUpdate) {
                        pb.setProgress(100);
                        alertDialog(1, AppLocal.getIntString("Message.titleUpdateDatabase"),
                                AppLocal.getIntString("Message.databaseUpdateHeader"),
                                AppLocal.getIntString("Message.updateReady"));
                        lblProgressMsg.setText(AppLocal.getIntString("Message.updateReady"));
                        lblProgressMsg.setTextFill(Color.web("#000000"));
                    } else {
                        pb.setProgress(0);
                        alertDialog(2, AppLocal.getIntString("Message.titleUpdateDatabase"),
                                AppLocal.getIntString("Message.databaseUpdateHeader"),
                                AppLocal.getIntString("Message.updateFailed"));
                        lblProgressMsg.setText(AppLocal.getIntString("Message.updateFailed"));
                        lblProgressMsg.setTextFill(Color.web("#FF0000"));
                    }
                    btnExit.setDisable(false);
                }
            });
        });
    }

    private Task updatedb(Connection connection) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                dbUpdate = true;
                try {
                    String changelog = "uk/chromis/pos/liquibase/scripts/update/dbupdate.xml";
                    Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
                    Liquibase liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database);
                    liquibase.update("implement");
                } catch (DatabaseException ex) {
                    Logger.getLogger(UpdateController.class.getName()).log(Level.SEVERE, null, ex);
                    dbUpdate = false;
                } catch (LiquibaseException ex) {
                    Logger.getLogger(UpdateController.class.getName()).log(Level.SEVERE, null, ex);
                    dbUpdate = false;
                }

                if (dbUpdate) {
                    insertMenuEntry(connection);
                    removeMenuEntry(connection);
                    insertNewButtons(connection);
                }

                //worker.cancel(true);
                return null;
            }
        ;
    }

    ;
    }

    private void insertMenuEntry(Connection connection) {
        try {
            String decodedData = "";
            Statement stmt = (Statement) connection.createStatement();
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
                    PreparedStatement stmt2 = connection.prepareStatement(SQL2);
                    stmt2.setBytes(1, bytesData);
                    stmt2.executeUpdate();
                    connection.commit();
                }
            }
            SQL = "delete from add_newmenuentry ";
            PreparedStatement pstmt = connection.prepareStatement(SQL);
            pstmt.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            dbUpdate = false;
            //  Logger.getLogger(UpdateController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void removeMenuEntry(Connection connection) {
        try {
            String decodedData = "";
            Statement stmt = (Statement) connection.createStatement();
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
            PreparedStatement stmt2 = connection.prepareStatement(SQL2);
            stmt2.setBytes(1, bytesData);
            stmt2.executeUpdate();
            connection.commit();

            SQL = "delete from remove_menuentry ";
            PreparedStatement pstmt = connection.prepareStatement(SQL);
            pstmt.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            dbUpdate = false;
            // Logger.getLogger(UpdateController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void insertNewButtons(Connection connection) {
        try {
            String decodedData = "";
            Statement stmt = (Statement) connection.createStatement();
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
                    PreparedStatement stmt2 = connection.prepareStatement(SQL2);
                    stmt2.setBytes(1, bytesData);
                    stmt2.executeUpdate();
                    connection.commit();
                }
            }
            SQL = "delete from add_newbutton ";
            PreparedStatement pstmt = connection.prepareStatement(SQL);
            pstmt.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            dbUpdate = false;
            //  Logger.getLogger(UpdateController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
