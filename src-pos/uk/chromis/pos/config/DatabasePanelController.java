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
package uk.chromis.pos.config;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javax.swing.JOptionPane;
import uk.chromis.custom.controls.LabeledComboBox;
import uk.chromis.custom.controls.LabeledPasswordField;
import uk.chromis.custom.controls.LabeledTextField;
import uk.chromis.data.loader.Session;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DriverWrapper;
import uk.chromis.pos.util.AltEncrypter;

/**
 * FXML Controller class
 *
 * @author John
 */
public class DatabasePanelController implements Initializable, BaseController {

    @FXML
    private LabeledComboBox databaseDetails;
    @FXML
    private LabeledTextField dbDriverLib;
    @FXML
    private LabeledTextField dbDriverClass;
    @FXML
    private LabeledTextField dbURL;
    @FXML
    private LabeledTextField dbUserName;
    @FXML
    private LabeledPasswordField dbPassword;
    @FXML
    private WebView webViewMessage;
    @FXML
    private Button btnLibrary;

    protected BooleanProperty dirty = new SimpleBooleanProperty();

    private Image image;
    // public WebView webViewMessage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dirty.bindBidirectional(databaseDetails.dirty);
        ObservableList<String> dbEngines = FXCollections.observableArrayList("Apache Derby Embedded", "MySQL", "PostgreSQL");
        databaseDetails.setLabel("Database Engine");
        databaseDetails.setWidthSizes(120.0, 400.0);
        databaseDetails.addItemList(dbEngines);

        dirty.bindBidirectional(dbDriverLib.dirty);
        dbDriverLib.setLabel(AppLocal.getIntString("label.dbdriverlib"));
        dbDriverLib.setWidthSizes(120.0, 400.0);

        dirty.bindBidirectional(dbDriverClass.dirty);
        dbDriverClass.setLabel(AppLocal.getIntString("Label.DbDriver"));
        dbDriverClass.setWidthSizes(120.0, 400.0);

        dirty.bindBidirectional(dbURL.dirty);
        dbURL.setLabel(AppLocal.getIntString("Label.DbURL"));
        dbURL.setWidthSizes(120.0, 400.0);

        dirty.bindBidirectional(dbUserName.dirty);
        dbUserName.setLabel(AppLocal.getIntString("Label.DbURL"));
        dbUserName.setWidthSizes(120.0, 170.0);

        dirty.bindBidirectional(dbPassword.dirty);
        dbPassword.setLabel(AppLocal.getIntString("Label.DbPassword"));
        dbPassword.setWidthSizes(120.0, 170.0);

        databaseDetails.getComboBox().getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            String dirname = System.getProperty("dirname.path");
            dirname = dirname == null ? "./" : dirname;
            if ("Apache Derby Embedded".equals(databaseDetails.getSelected())) {
                dbDriverLib.setText(new File(new File(dirname), "lib/derby.jar").getAbsolutePath());
                dbDriverClass.setText("org.apache.derby.jdbc.EmbeddedDriver");
                dbURL.setText("jdbc:derby:" + new File(new File(System.getProperty("user.home")), AppLocal.APP_ID + "-database").getAbsolutePath() + ";create=true");
                dbUserName.setText("");
                dbPassword.setPassword("");
            } else if ("MySQL".equals(databaseDetails.getSelected())) {
                dbDriverLib.setText(new File(new File(dirname), "lib/mysql-connector-java-5.1.42.jar").getAbsolutePath());
                dbDriverClass.setText("com.mysql.jdbc.Driver");
                dbURL.setText("jdbc:mysql://localhost:3306/chromispos");
                dbUserName.setText("");
                dbPassword.setPassword("");
            } else if ("PostgreSQL".equals(databaseDetails.getSelected())) {
                dbDriverLib.setText(new File(new File(dirname), "lib/postgresql-9.2-1003.jdbc4.jar").getAbsolutePath());
                dbDriverClass.setText("org.postgresql.Driver");
                dbURL.setText("jdbc:postgresql://localhost:5432/chromispos");
                dbUserName.setText("");
                dbPassword.setPassword("");
            }
        });

        load();

        // add the icons to the open file button
        image = new Image(getClass().getResourceAsStream("/uk/chromis/images/fileopen.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(18);
        imageView.setFitWidth(18);
        btnLibrary.setGraphic(imageView);

        // create the message tab info
        WebEngine webEngine = webViewMessage.getEngine();
        loadStyle(webEngine);
        webViewMessage.getEngine().loadContent(AppLocal.getIntString("message.DBDefault"));
    }

    public void chromisWebSite() {
        try {
            String URL = "http://www.chromis.co.uk";
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(URL));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    private void loadStyle(WebEngine node) {
        try {
            final String resource = Paths.get(System.getProperty("user.dir") + "/cssStyles/Chromis-webview.css").toUri().toURL().toExternalForm();
            node.setUserStyleSheetLocation(resource);
        } catch (MalformedURLException e) {
            System.out.println("No Chromis-webview.css File found !");
        }
    }

    public void handleTestConnection() {
        try {
            String driverlib = dbDriverLib.getText();
            String driver = dbDriverClass.getText();
            String url = dbURL.getText();
            String user = dbUserName.getText();
            String password = new String(dbPassword.getPassword());

            ClassLoader cloader = new URLClassLoader(new URL[]{new File(driverlib).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(driver, true, cloader).newInstance()));

            Session session = new Session(url, user, password);
            Connection connection = session.getConnection();
            boolean isValid;
            isValid = (connection == null) ? false : connection.isValid(1000);

            if (isValid) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Connection Test");
                alert.setHeaderText(AppLocal.getIntString("message.connectionheader"));
                alert.setContentText(AppLocal.getIntString("message.databasesuccess"));
                ButtonType buttonExit = new ButtonType("Exit");
                alert.getButtonTypes().setAll(buttonExit);
                Optional<ButtonType> result = alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Connection Error");
                alert.setHeaderText(AppLocal.getIntString("message.connectionfailed"));
                alert.setContentText(AppLocal.getIntString("message.databaseconnectionerror"));
                ButtonType buttonExit = new ButtonType("Exit");
                alert.getButtonTypes().setAll(buttonExit);
                Optional<ButtonType> result = alert.showAndWait();
            }
        } catch (InstantiationException | IllegalAccessException | MalformedURLException | ClassNotFoundException e) {
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setHeaderText(null);
            alert.setContentText(AppLocal.getIntString("message.databaseconnectionerror"));
            ButtonType buttonExit = new ButtonType("Exit");
            alert.getButtonTypes().setAll(buttonExit);
            Optional<ButtonType> result = alert.showAndWait();
        } catch (Exception e) {
        }
    }

    public void handleSelectLibFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            dbDriverLib.setText(selectedFile.getAbsoluteFile().toString());
        }
    }

    private String comboValue(Object value) {
        return value == null ? "" : value.toString();
    }

    public void load() {
        if (AppConfig.getInstance().getProperty("db.engine") != null) {
            databaseDetails.setSelected(AppConfig.getInstance().getProperty("db.engine"));
        } else {
            databaseDetails.setSelected("Apache Derby Embedded");
        }
        dbDriverLib.setText(AppConfig.getInstance().getProperty("db.driverlib"));
        dbDriverClass.setText(AppConfig.getInstance().getProperty("db.driver"));
        dbURL.setText(AppConfig.getInstance().getProperty("db.URL"));

        String sDBUser = AppConfig.getInstance().getProperty("db.user");
        String sDBPassword = AppConfig.getInstance().getProperty("db.password");
        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + sDBUser);
            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
        }
        dbUserName.setText(sDBUser);
        dbPassword.setPassword(sDBPassword);

        dirty.setValue(false);
    }

    @Override
    public void save() {
        AppConfig.getInstance().setProperty("db.engine", comboValue(databaseDetails.getSelected()));
        AppConfig.getInstance().setProperty("db.driverlib", dbDriverLib.getText());
        AppConfig.getInstance().setProperty("db.driver", dbDriverClass.getText());
        AppConfig.getInstance().setProperty("db.URL", dbURL.getText());
        AppConfig.getInstance().setProperty("db.user", dbUserName.getText());
        AltEncrypter cypher = new AltEncrypter("cypherkey" + dbUserName.getText());
        AppConfig.getInstance().setProperty("db.password", "crypt:" + cypher.encrypt(new String(dbPassword.getPassword())));

        dirty.setValue(false);
    }

    @Override
    public Boolean isDirty() {
        return dirty.getValue();
    }

    @Override
    public void setDirty(Boolean value) {
        dirty.setValue(value);
    }

}
