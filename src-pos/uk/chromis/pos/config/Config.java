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

import javafx.scene.image.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author John
 */
public class Config extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene;
        Parent root = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("Configuration.fxml"));
            root = loader.load();
        } catch (IOException ex) {
            System.out.println("Error trying to load Start Config");
            System.out.println("*********************************");
            System.out.println(ex);
        }
        ConfigurationController controller = loader.getController();
        scene = new Scene(root, 800, 580);

        loadStyle(scene);
        stage.setScene(scene);

        stage.getIcons().add(new Image(Config.class.getResourceAsStream("/uk/chromis/fixedimages/smllogo.png")));
        stage.setTitle("Chromis Configuration");
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                if (controller.isDirty()) {
                    controller.handleExit();
                }
            }
        });
        stage.show();

    }

    private void loadStyle(Scene node) {
        try {
            final String resource = Paths.get(System.getProperty("user.dir") + "/cssStyles/Chromis.css").toUri().toURL().toExternalForm();
            node.getStylesheets().add(resource);
        } catch (MalformedURLException e) {
            System.out.println("No Chromis.css File found !");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
