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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.IntStream;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import uk.chromis.data.loader.Session;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DriverWrapper;
import uk.chromis.pos.util.AltEncrypter;

/**
 * FXML Controller class
 *
 */
public class NewDBController implements Initializable {

    public Button exit;
    public Button createDB;
    public Button testDB;

    public Label lblDbEngine;
    public Label lblDbURL;
    public Label lblDbUser;
    public Label lblDbPassword;
    public Button btnSelectFile;
    public Label lblProgressMsg;

    public TextField dbDriverLibrary;
    public TextField dbDriverClass;
    public TextField dbURL;
    public TextField dbUser;
    public TextField dbPassword;

    public ComboBox DbEngine;
    public ProgressBar pb;

    private final DirtyManager dirty = new DirtyManager();
    private FileChooser fileChooser;
    private File file;
    private String dirname;
    private Boolean dbBuilt = false;
    private Boolean dbConnect = false;
    private Task worker;
    private Task dbconnection;
    private Task testCreateWorker;
    private Boolean checkdb;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dirname = System.getProperty("dirname.path");
        dirname = dirname == null ? "./" : dirname;
        DbEngine.getItems().addAll("Apache Derby Embedded");
        DbEngine.getItems().addAll("MySQL");
        DbEngine.getItems().addAll("PostgreSQL");
        DbEngine.setValue("Apache Derby Embedded");
        dbURL.setText("jdbc:derby:" + new File(new File(System.getProperty("user.home")), AppLocal.APP_ID + "-database").getAbsolutePath() + ";create=true");
        dbDriverLibrary.setText(new File(new File(dirname), "lib/derby-10.10.2.0.jar").getAbsolutePath());
        dbDriverClass.setText("org.apache.derby.jdbc.EmbeddedDriver");
        lblProgressMsg.setText("");

        dbDriverClass.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                dirty.setDirty(true);
                dbConnect = false;
            }

        });

        dbDriverLibrary.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                dirty.setDirty(true);
                dbConnect = false;
            }

        });

        dbUser.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                dirty.setDirty(true);
                dbConnect = false;
            }

        });
        dbPassword.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                dirty.setDirty(true);
                dbConnect = false;
            }

        });

        dbURL.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                dirty.setDirty(true);
                dbConnect = false;
            }

        });

        loadProperties();
    }

    public Task tryConnect() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    String driverlib = dbDriverLibrary.getText();
                    String driver = dbDriverClass.getText();
                    String url = dbURL.getText();
                    String user = dbUser.getText();
                    String password = dbPassword.getText();
                    ClassLoader cloader = new URLClassLoader(new URL[]{new File(driverlib).toURI().toURL()});
                    DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(driver, true, cloader).newInstance()));

                    Session session = new Session(url, user, password);
                    Connection connection = session.getConnection();
                    boolean isValid;
                    isValid = (connection == null) ? false : connection.isValid(1000);
                    if (isValid) {
                        dbConnect = true;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                lblProgressMsg.setText("Connected to the database..");
                            }
                        });
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                lblProgressMsg.setText("Unable to connect to the database!!");
                                lblProgressMsg.setTextFill(Color.web("#FF0000"));
                            }
                        });
                    }
                } catch (SQLException e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            lblProgressMsg.setText("Unable to connect to the database!!");
                            lblProgressMsg.setTextFill(Color.web("#FF0000"));
                        }
                    });
                } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            lblProgressMsg.setText("Unable to connect to the database!!");
                            lblProgressMsg.setTextFill(Color.web("#FF0000"));
                        }
                    });
                }
                dbconnection.cancel(true);
                return null;
            }
        };
    }

    private Task createdb() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                String changelog = "uk/chromis/pos/liquibase/scripts/create/chromis.xml";
                ProcessLiquibase db = new ProcessLiquibase();
                if (db.performAction(changelog)) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            pb.setProgress(100);
                            lblProgressMsg.setText("New Database created and ready for use.");
                            lblProgressMsg.setTextFill(Color.web("#000000"));
                            createDB.setDisable(true);
                            exit.setDisable(false);
                            NewDB.setStatus(true);
                            dbBuilt = true;
                        }
                    });
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            pb.setProgress(0);
                            lblProgressMsg.setText("Error creating database, check 'liquibase.log' for error messages");
                            lblProgressMsg.setTextFill(Color.web("#FF0000"));
                            exit.setDisable(false);
                            testDB.setDisable(true);
                        }
                    });
                }
                worker.cancel(true);
                return null;
            }
        };
    }

    private Task testCreate() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    String driverlib = dbDriverLibrary.getText();
                    String driver = dbDriverClass.getText();
                    String url = dbURL.getText();
                    String user = dbUser.getText();
                    String password = dbPassword.getText();
                    ClassLoader cloader = new URLClassLoader(new URL[]{new File(driverlib).toURI().toURL()});
                    DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(driver, true, cloader).newInstance()));

                    Session session = new Session(url, user, password);
                    Connection connection = session.getConnection();                   
                    boolean isValid;
                    isValid = (connection == null) ? false : connection.isValid(1000);
                    if (isValid) {                       
                        // now create the database
                        String changelog = "uk/chromis/pos/liquibase/scripts/create/chromis.xml";
                        ProcessLiquibase db = new ProcessLiquibase();
                        if (db.performAction(changelog)) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    pb.setProgress(100);
                                    lblProgressMsg.setText("New Database created and ready for use.");
                                    lblProgressMsg.setTextFill(Color.web("#000000"));
                                    createDB.setDisable(true);
                                    exit.setDisable(false);
                                    NewDB.setStatus(true);
                                    dbBuilt = true;
                                }
                            });
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    pb.setProgress(0);
                                    lblProgressMsg.setText("Error creating database, check 'liquibase.log' for error messages");
                                    lblProgressMsg.setTextFill(Color.web("#FF0000"));
                                    exit.setDisable(false);
                                    testDB.setDisable(false);
                                }
                            });
                        }
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pb.setProgress(0);
                                lblProgressMsg.setText("Unable to connect to the database!!");
                                lblProgressMsg.setTextFill(Color.web("#FF0000"));
                                btnSelectFile.setDisable(false);
                                createDB.setDisable(false);
                                testDB.setDisable(false);
                            }
                        });
                    }
                } catch (SQLException e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            pb.setProgress(0);
                            lblProgressMsg.setText("Unable to connect to the database!!");
                            lblProgressMsg.setTextFill(Color.web("#FF0000"));
                            btnSelectFile.setDisable(false);
                            createDB.setDisable(false);
                            testDB.setDisable(false);
                        }
                    });
                } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            pb.setProgress(0);
                            lblProgressMsg.setText("Unable to connect to the database!!");
                            lblProgressMsg.setTextFill(Color.web("#FF0000"));
                            btnSelectFile.setDisable(false);
                            createDB.setDisable(false);
                            testDB.setDisable(false);
                        }
                    });
                }
                testCreateWorker.cancel(true);
                return null;
            }
        };
    }

    public void handleComboChange() {
        String strEngine = DbEngine.getSelectionModel().getSelectedItem().toString();
        dirty.setDirty(true);
        switch (strEngine) {
            case "Apache Derby Embedded":
                dbURL.setText("jdbc:derby:" + new File(new File(System.getProperty("user.home")), AppLocal.APP_ID + "-database").getAbsolutePath() + ";create=true");
                dbDriverLibrary.setText(new File(new File(dirname), "lib/derby.jar").getAbsolutePath());
                dbDriverClass.setText("org.apache.derby.jdbc.EmbeddedDriver");
                break;
            case "MySQL":
                dbURL.setText("jdbc:mysql://localhost:3306/chromispos");
                dbDriverLibrary.setText(new File(new File(dirname), "lib/mysql-connector-java-5.1.42.jar").getAbsolutePath());
                dbDriverClass.setText("com.mysql.jdbc.Driver");
                break;
            case "PostgreSQL":
                dbURL.setText("jdbc:postgresql://localhost:5432/chromispos");
                dbDriverLibrary.setText(new File(new File(dirname), "lib/postgresql-9.2-1003.jdbc4.jar").getAbsolutePath());
                dbDriverClass.setText("org.postgresql.Driver");
                break;
        }

    }

    public void selectFile() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Select Properties File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("jar", "*.jar"));
        file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            dirty.setDirty(true);
            dbDriverLibrary.setText(file.getAbsolutePath());
        }
    }

    public void loadProperties() {
        if (AppConfig.getInstance().getProperty("db.engine") != null) {
            DbEngine.setValue(AppConfig.getInstance().getProperty("db.engine"));
            dbDriverLibrary.setText(AppConfig.getInstance().getProperty("db.driverlib"));
            dbDriverClass.setText(AppConfig.getInstance().getProperty("db.driver"));
            dbURL.setText(AppConfig.getInstance().getProperty("db.URL"));
            String sDBUser = AppConfig.getInstance().getProperty("db.user");
            String sDBPassword = AppConfig.getInstance().getProperty("db.password");
            if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
                AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
                sDBPassword = cypher.decrypt(sDBPassword.substring(6));
            }
            dbUser.setText(sDBUser);
            dbPassword.setText(sDBPassword);
            dirty.setDirty(false);
        }
    }

    public void handleCreateClick() throws IOException {
        AppConfig.getInstance().setProperty("db.engine", DbEngine.getSelectionModel().getSelectedItem().toString());
        AppConfig.getInstance().setProperty("db.driverlib", dbDriverLibrary.getText());
        AppConfig.getInstance().setProperty("db.driver", dbDriverClass.getText());
        AppConfig.getInstance().setProperty("db.URL", dbURL.getText());
        AppConfig.getInstance().setProperty("db.user", dbUser.getText());
        AltEncrypter cypher = new AltEncrypter("cypherkey" + dbUser.getText());
        AppConfig.getInstance().setProperty("db.password", "crypt:" + cypher.encrypt(dbPassword.getText()));
        AppConfig.getInstance().save();
        dirty.setDirty(false);
        // lets create the database here
        // lets check we can connect to the database if the user has not already done so
        if (dbConnect) {
            worker = createdb();
            pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            lblProgressMsg.setText("Creating Database. Please wait.");
            lblProgressMsg.setTextFill(Color.web("#0000FF"));
            exit.setDisable(true);
            new Thread(worker).start();
            worker.setOnCancelled((Event e) -> {
                Platform.runLater(() -> {
                    createDB.setDisable(true);
                });
            });
        } else {
            testCreateWorker = testCreate();
            pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            lblProgressMsg.setText("Creating Database. Please wait.");
            lblProgressMsg.setTextFill(Color.web("#0000FF"));
            btnSelectFile.setDisable(true);
            createDB.setDisable(true);
            testDB.setDisable(true);
            new Thread(testCreateWorker).start();
            testCreateWorker.setOnCancelled((Event e) -> {
                Platform.runLater(() -> {
                    createDB.setDisable(true);
                });
            });
        }
    }

    public void handleExitClick() {
        if (dirty.isDirty()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Unsaved data");
            alert.setHeaderText("It looks like you have made changes, but not saved them");
            alert.setContentText("Do you wish to save your change?");
            ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(okButton, noButton);
            alert.showAndWait().ifPresent(new Consumer<ButtonType>() {
                @Override
                public void accept(ButtonType type) {
                    if (type.getText().equalsIgnoreCase("yes")) {
                        AppConfig.getInstance().setProperty("db.engine", DbEngine.getSelectionModel().getSelectedItem().toString());
                        AppConfig.getInstance().setProperty("db.driverlib", dbDriverLibrary.getText());
                        AppConfig.getInstance().setProperty("db.driver", dbDriverClass.getText());
                        AppConfig.getInstance().setProperty("db.URL", dbURL.getText());
                        AppConfig.getInstance().setProperty("db.user", dbUser.getText());
                        AltEncrypter cypher = new AltEncrypter("cypherkey" + dbUser.getText());
                        AppConfig.getInstance().setProperty("db.password", "crypt:" + cypher.encrypt(dbPassword.getText()));
                        try {
                            AppConfig.getInstance().save();

                        } catch (IOException ex) {
                            Logger.getLogger(NewDBController.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                        dirty.setDirty(false);
                    }
                }
            });
        }
        if (dbBuilt) {
            NewDB.setWorking(false);
            return;
        }
        System.exit(0);
    }

    public void handleTestDB() {
        dbconnection = tryConnect();
        createDB.setDisable(true);
        testDB.setDisable(true);
        btnSelectFile.setDisable(true);
        DbEngine.setDisable(true);

        dbConnect = false;
        new Thread(dbconnection).start();
        pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        lblProgressMsg.setText("Attempting to connect to database ..");
        lblProgressMsg.setTextFill(Color.web("#000000"));

        dbconnection.setOnCancelled(e -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    pb.setProgress(0);
                    btnSelectFile.setDisable(false);
                    createDB.setDisable(false);
                    testDB.setDisable(false);
                    DbEngine.setDisable(false);
                }
            });
        });

    }

    public class HeadersNoDate extends SimpleFormatter {

        @Override
        public String format(LogRecord record) {
            if (record.getLevel() == Level.INFO) {
                return record.getMessage() + "\r\n";
            } else {
                return super.format(record);
            }
        }
    }

}
