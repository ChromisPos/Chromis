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

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.pos.forms.AppLocal;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import uk.chromis.pos.dialogs.JAlert;

public class DbDialogController implements Initializable {

    //Controls etc on form    
    public Label lblDBEngine;
    public Label lblDBLibrary;
    public Label lblDBClass;
    public Label lblDBServer;
    public Label lblDBPort;
    public Label lblDBName;
    public Label lblDBUsername;
    public Label lblDBPassword;
    public Label lblProgressMsg;

    public Button btnExit;
    public Button btnTestDB;
    public Button btnCreate;
    public Button btnSelectFile;
    public Button btnSaveConfig;

    public TextField dbDriverLibrary;
    public TextField dbDriverClass;
    public TextField dbServer;
    public TextField dbPort;
    public TextField dbDatabase;
    public TextField dbUserName;
    public TextField dbPassword;

    public CheckBox dbVersion;

    public ProgressBar pb;
    public RadioButton mySQL;
    public RadioButton postgreSQL;
    public RadioButton derby;

    private String dirname;
    private final DirtyManager dirty = new DirtyManager();
    private FileChooser fileChooser;
    private File file;

    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int centreX;
    private int centreY;
    private final DbUser dbUser = new DbUser();
    private Task doTest;
    private Task doCreate;
    private Object[] result;
    private ToggleGroup dbsource;
    private Boolean dbBuilt = true;
    private String dbEngine;
    private CountDownLatch latchToWaitForConnectionTest;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dirname = System.getProperty("user.dir") == null ? "./" : System.getProperty("user.dir");
        centreX = screenSize.width / 2;
        centreY = screenSize.height / 2;

//Set the label & Button texts
        lblDBEngine.setText(AppLocal.getIntString("Label.dbEngine"));
        lblDBLibrary.setText(AppLocal.getIntString("Label.dbLibrary"));
        lblDBClass.setText(AppLocal.getIntString("Label.dbClass"));
        lblDBServer.setText(AppLocal.getIntString("Label.dbServer"));
        lblDBPort.setText(AppLocal.getIntString("Label.dbPort"));
        lblDBName.setText(AppLocal.getIntString("Label.dbName"));
        lblDBUsername.setText(AppLocal.getIntString("Label.dbUsername"));
        lblDBPassword.setText(AppLocal.getIntString("Label.dbPassword"));
        lblProgressMsg.setText(AppLocal.getIntString("Label.dbConnectivity"));
        btnExit.setText(AppLocal.getIntString("Button.exit"));
        btnTestDB.setText(AppLocal.getIntString("Button.dbTestConnection"));
        btnCreate.setText(AppLocal.getIntString("Button.dbCreate"));
        btnSaveConfig.setText(AppLocal.getIntString("Button.saveConfig"));

//Set up the radion buttons
        dbsource = new ToggleGroup();
        mySQL.setToggleGroup(dbsource);
        postgreSQL.setToggleGroup(dbsource);
        derby.setToggleGroup(dbsource);
        mySQL.setSelected(true);

//Add the image to file selector button
        Image image = new Image(getClass().getResourceAsStream("/uk/chromis/fixedimages/fileopen.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(18);
        imageView.setFitWidth(18);
        btnSelectFile.setGraphic(imageView);

//Ensure that the progress bar has no text message        
        lblProgressMsg.setText("");

//Add the dirty listener to the fields
        dbDriverLibrary.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            dbUser.setDbLibrary(dbDriverLibrary.getText());
            dirty.setDirty(true);
        });
        dbDriverClass.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            dbUser.setDbClass(dbDriverLibrary.getText());
            dirty.setDirty(true);
        });
        dbServer.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            dbUser.setServerName(dbServer.getText());
            dirty.setDirty(true);
        });
        dbPort.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            dbUser.setServerPort(dbPort.getText());
            dirty.setDirty(true);
        });
        dbDatabase.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            dbUser.setDatabaseName(dbDatabase.getText());
            dirty.setDirty(true);
        });
        dbUserName.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            dbUser.setUserName(dbUserName.getText());
            dirty.setDirty(true);
        });
        dbPassword.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            dbUser.setUserPassword(dbPassword.getText());
            dirty.setDirty(true);
        });
        dbVersion.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            dbUser.setUseSSL(dbVersion.isSelected());
            dirty.setDirty(true);
        });

        dbsource.selectedToggleProperty().addListener(new DbToggleHandler());

        loadProperties();

//Set the default focus to be the 'Exit' button        
        Platform.runLater(new Runnable() {
            public void run() {
                btnExit.requestFocus();
            }
        });
    }

    /*
     * Listen for database engine change and update defaults values      
     */
    private class DbToggleHandler implements ChangeListener<Toggle> {

        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldBtn, Toggle newBtn) {
            dbUserName.setText("");
            dbPassword.setText("");
            dirty.setDirty(true);
            dbEngine = ((RadioButton) newBtn).getText();
            switch (dbEngine) {
                case "PostgreSQL":
                    postgreSQL.setSelected(true);
                    dbDriverLibrary.setText(dirname + "lib\\postgresql-9.4-1201-jdbc4.jar");
                    dbDriverClass.setText("org.postgresql.Driver");
                    dbPort.setText("5432");
                    dbServer.setText("localhost");
                    dbDatabase.setText("chromispos");
                    dbVersion.setSelected(true);
                    dbVersion.setVisible(false);
                    break;
                case "MySQL":
                    mySQL.setSelected(true);
                    dbDriverLibrary.setText(dirname + "lib\\mysql-connector-java-5.1.42.jar");
                    dbDriverClass.setText("com.mysql.jdbc.Driver");
                    dbPort.setText("3306");
                    dbServer.setText("localhost");
                    dbDatabase.setText("chromispos");
                    dbVersion.setSelected(true);
                    dbVersion.setVisible(true);
                    break;
                case "Derby":
                    derby.setSelected(true);
                    dbDriverLibrary.setText(dirname + "lib\\derby-10.14.1.0.jar");
                    dbDriverClass.setText("org.apache.derby.jdbc.EmbeddedDriver");
                    dbPort.setText("");
                    dbServer.setText("");
                    dbDatabase.setText("chromispos");
                    dbVersion.setSelected(true);
                    dbVersion.setVisible(false);
                    break;
            }
        }
    }

    public void selectFile() {
        fileChooser = new FileChooser();
        fileChooser.setTitle(AppLocal.getIntString("Message.titleSelectJDBC"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("jar", "*.jar"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "\\lib"));
        file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            dirty.setDirty(true);
            dbDriverLibrary.setText(file.getAbsolutePath());
        }
    }

    //Load the properties file into the dialog panel    
    private void loadProperties() {
        Boolean dirtyUser = dbUser.getUserDetails();
        dbEngine = dbUser.getDbEngine();
        switch (dbEngine) {
            case "MySQL":
                mySQL.setSelected(true);
                break;
            case "PostgreSQL":
                postgreSQL.setSelected(true);
                break;
            case "Derby":
                derby.setSelected(true);
                break;
        }

        dbDriverClass.setText(dbUser.getDbClass());
        dbUserName.setText(dbUser.getUserName());
        dbPassword.setText(dbUser.getUserPassword());
        dbVersion.setSelected(dbUser.getUseSSL());
        dbDriverLibrary.setText(dbUser.getDbLibrary());
        dbServer.setText(dbUser.getServerName());
        dbPort.setText(dbUser.getServerPort());
        dbDatabase.setText(dbUser.getDatabaseName());
        dirty.setDirty(dirtyUser);
    }

    //Save the latest version of the properties file
    public boolean saveConfig() {
        if (dbUser.save()) {
            JAlert jAlert = new JAlert(1);
            jAlert.setTitle(AppLocal.getIntString("Alert.configurationTitle"));
            jAlert.setHeaderText(AppLocal.getIntString("Message.configSavedHeader"));
            jAlert.setContextText(AppLocal.getIntString("Message.configSavedContext"));
            jAlert.setVisible(true);
            dirty.setDirty(false);
            return true;
        } else {
            JAlert jAlert = new JAlert(0);
            jAlert.setTitle(AppLocal.getIntString("Alert.configurationTitle"));
            jAlert.setHeaderText(AppLocal.getIntString("Message.configSavedHeaderFailed"));
            jAlert.setContextText(AppLocal.getIntString("Message.configSavedContextFailed"));
            jAlert.setVisible(true);
            dirty.setDirty(true);
            return false;
        }
    }

    private void alertDialog(int alert, String title, String headerText, String contentText) {
        JAlert jAlert = new JAlert(alert);
        jAlert.setTitle(title);
        jAlert.setHeaderText(headerText);
        jAlert.setContextText(contentText);
        jAlert.setVisible(true);
    }

    //Entry for test from FXL panel
    public void doConnectionTest() {
        disableAllControls();
        doDBWork(false);
    }

    public void createNewDB() {
        disableAllControls();
        doDBWork(true);
    }

    //Execute testing database connection
    //if createNewDB = true the test and create db is attempted  
    private void doDBWork(Boolean createNewDB) {
        Platform.runLater(new Runnable() {
            public void run() {
                pb.requestFocus();
            }
        });

        pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        lblProgressMsg.setText(AppLocal.getIntString("Message.attemptingConnection"));
        lblProgressMsg.setTextFill(Color.web("#000000"));

        doTest = tryConnection(dbUser, true);
        new Thread(doTest).start();
        doTest.setOnCancelled(new EventHandler() {
            @Override
            public void handle(Event e) {
                pb.setProgress(0);
                if ((Boolean) result[0]) {
                    pb.setProgress(100);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    String content;
                    if (createNewDB) {
                        if ((!(Boolean) result[3] && result[4].equals("")) || (!(Boolean) result[3])) {
                            content = AppLocal.getIntString("Message.databaseNotEmpty");
                            alertDialog(0, AppLocal.getIntString("Message.titleConnectionTest"), AppLocal.getIntString("Message.connectionHeader"), content);
                            enableAllControls();
                            pb.setProgress(0);
                            lblProgressMsg.setText(AppLocal.getIntString("Message.createDatabaseFailed"));
                            lblProgressMsg.setTextFill(Color.web("#ff0000"));
                        } else {
                            //OK to create the database 
                            doCreate = createdb((Connection) result[2]);
                            pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                            lblProgressMsg.setText(AppLocal.getIntString("Message.creatingDatabase"));
                            lblProgressMsg.setTextFill(Color.web("#000000"));
                            new Thread(doCreate).start();
                            doCreate.setOnCancelled(new EventHandler() {
                                @Override
                                public void handle(Event e) {
                                    String content = "";
                                    if (dbBuilt) {
                                        alertDialog(3, AppLocal.getIntString("Message.titleCreateDatabase"),
                                                AppLocal.getIntString("Message.createNewDatabase"),
                                                AppLocal.getIntString("Message.newDatabaseReady"));
                                    } else {
                                        alertDialog(3, AppLocal.getIntString("Message.titleCreateDatabase"),
                                                AppLocal.getIntString("Message.createNewDatabase"),
                                                AppLocal.getIntString("Message.createDatabaseFailed"));
                                    }
                                }
                            });
                        }
                    } else {
                        if (!(Boolean) result[3] && result[4].equals("")) {
                            alertDialog(1, AppLocal.getIntString("Message.titleConnectionTest"),
                                    AppLocal.getIntString("Message.connectionHeader"),
                                    AppLocal.getIntString("Message.databaseNotChromis"));
                        } else if ((Boolean) result[3]) {
                            alertDialog(1, AppLocal.getIntString("Message.titleConnectionTest"),
                                    AppLocal.getIntString("Message.connectionHeader"),
                                    AppLocal.getIntString("Message.databaseEmpty"));
                        } else if ((Boolean) result[0]) {
                            alertDialog(1, AppLocal.getIntString("Message.titleConnectionTest"),
                                    AppLocal.getIntString("Message.connectionHeader"),
                                    AppLocal.getIntString("Message.databaseGood") + result[4]);
                        }
                        lblProgressMsg.setText(AppLocal.getIntString("Message.connectionSuccessful"));
                        lblProgressMsg.setTextFill(Color.web("#000000"));
                        enableAllControls();
                    }

                } else {
                    pb.setProgress(0);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(AppLocal.getIntString("Alert.configurationTitle"));
                    alertDialog(2, AppLocal.getIntString("Message.titleConnectionTest"), 
                            AppLocal.getIntString("Message.connectionFailed"), 
                            result[1].toString());
                    lblProgressMsg.setText(AppLocal.getIntString("Message.connectionUnsuccessful"));
                    lblProgressMsg.setTextFill(Color.web("#FF0000"));
                    enableAllControls();
                }
            }
        }
        );
    }

    public Object[] tryConnection(DbUser dbUser) {
        doTest = tryConnection(dbUser, false);
        return result;
    }

    private Task tryConnection(DbUser dbUser, Boolean dbDialog) {
        latchToWaitForConnectionTest = new CountDownLatch(1);
        if (dbDialog) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    disableAllControls();
                }
            });
        }
        return new Task() {
            @Override
            protected Void call() throws Exception {
                result = DbUtils.connectionTest(dbUser);                
                if (dbDialog) {
                    doTest.cancel(true);
                }
                return null;
            }
        ;
    }

    ;
}
    
    //Exit the config dialog, but flag is config is dirty and offer option to save    
    public void handleExitClick() {
        if (dirty.isDirty()) {
            JAlert jAlert = new JAlert(3);
            jAlert.setTitle(AppLocal.getIntString("Alert.configurationTitle"));
            jAlert.setHeaderText(AppLocal.getIntString("Message.unsavedSettings"));
            jAlert.setContextText(AppLocal.getIntString("Message.unsavedSettings"));
            jAlert.setYesNoButtons();
            jAlert.setVisible(true);
            if (jAlert.getChoice() == 5) {
                if (saveConfig()) {
                    System.exit(0);
                } else {
                    dirty.setDirty(true);
                }
            } else {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    private Task createdb(Connection connection) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                dbBuilt = true;
                try {
                    String changelog = "uk/chromis/pos/liquibase/scripts/create/createdb.xml";
                    Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
                    Liquibase liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database);
                    liquibase.update("implement");

                } catch (DatabaseException ex) {
                    Logger.getLogger(DbDialogController.class.getName()).log(Level.SEVERE, null, ex);
                    dbBuilt = false;

                } catch (LiquibaseException ex) {
                    Logger.getLogger(DbDialogController.class.getName()).log(Level.SEVERE, null, ex);
                    dbBuilt = false;
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (dbBuilt) {
                            pb.setProgress(100);
                            lblProgressMsg.setText(AppLocal.getIntString("Message.newDatabaseReady"));
                            lblProgressMsg.setTextFill(Color.web("#000000"));
                        } else {
                            pb.setProgress(0);
                            lblProgressMsg.setText(AppLocal.getIntString("Message.createDatabaseFailed"));
                            lblProgressMsg.setTextFill(Color.web("#ff0000"));
                            enableAllControls();
                        }
                        btnExit.setDisable(false);
                    }
                });
                doCreate.cancel(true);
                return null;
            }
        ;
    }

    ;
}
    
    private void disableAllControls() {
        postgreSQL.setDisable(true);
        mySQL.setDisable(true);
        derby.setDisable(true);
        dbDriverLibrary.setDisable(true);
        dbDriverClass.setDisable(true);
        dbServer.setDisable(true);
        dbVersion.setDisable(true);
        dbPort.setDisable(true);
        dbDatabase.setDisable(true);
        dbUserName.setDisable(true);
        dbPassword.setDisable(true);
        btnSelectFile.setDisable(true);
        btnTestDB.setDisable(true);
        btnExit.setDisable(true);
        dbVersion.setDisable(true);
        btnCreate.setDisable(true);
        btnSaveConfig.setDisable(true);
    }

    private void enableAllControls() {
        postgreSQL.setDisable(false);
        mySQL.setDisable(false);
        derby.setDisable(false);
        dbDriverLibrary.setDisable(false);
        dbDriverClass.setDisable(false);
        dbServer.setDisable(false);
        dbVersion.setDisable(false);
        dbPort.setDisable(false);
        dbDatabase.setDisable(false);
        dbUserName.setDisable(false);
        dbPassword.setDisable(false);
        btnSelectFile.setDisable(false);
        btnTestDB.setDisable(false);
        btnExit.setDisable(false);
        dbVersion.setDisable(false);
        btnCreate.setDisable(false);
        btnSaveConfig.setDisable(false);
    }

}
