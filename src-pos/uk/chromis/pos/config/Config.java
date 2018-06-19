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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javafx.scene.image.Image;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.StartPOS;

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
        String currentPath = null;
        currentPath = System.getProperty("user.dir");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd-HHmm-");
        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("/debug")) {
                //send output to log files
                try {
                    System.setErr(new PrintStream(new FileOutputStream(currentPath + "/Logs/" + simpleDateFormat.format(new Date()) + "Config.log")));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(StartPOS.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } 

      
        File newIcons = null;
        String colour;
        if (AppConfig.getInstance().getProperty("icon.colour") == null || AppConfig.getInstance().getProperty("icon.colour").equals("")) {
            colour = "royalblue";
        } else {
            colour = AppConfig.getInstance().getProperty("icon.colour");
        }

        switch (colour) {
            case "red":
                newIcons = new File(currentPath + "/iconsets/redimages.jar");
                break;
            case "green":
                newIcons = new File(currentPath + "/iconsets/greenimages.jar");
                break;
            case "black":
                newIcons = new File(currentPath + "/iconsets/blackimages.jar");
                break;
            case "orange":
                newIcons = new File(currentPath + "/iconsets/orangeimages.jar");
                break;
            case "blue":
                newIcons = new File(currentPath + "/iconsets/blueimages.jar");
                break;
            case "royalblue":
                newIcons = new File(currentPath + "/iconsets/royalblueimages.jar");
                break;
            default:
                newIcons = new File(currentPath + "/iconsets/royalblueimages.jar");
        }
        // File icons = new File(currentPath + "/lib");
        try {
            URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method m = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            m.setAccessible(true);
            m.invoke(urlClassLoader, newIcons.toURI().toURL());
            String cp = System.getProperty("java.class.path");
            if (cp != null) {
                cp += File.pathSeparatorChar + newIcons.getCanonicalPath();
            } else {
                cp = newIcons.toURI().getPath();
            }
            System.setProperty("java.class.path", cp);
        } catch (Exception ex) {
        }
        launch(args);
    }

}
