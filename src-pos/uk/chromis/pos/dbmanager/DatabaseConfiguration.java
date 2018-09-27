/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c)2015-2018
**
**    http://www.chromis.co.uk
**
**    This file is part of Chromis POS Version V1234.x
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
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author John
 */
public class DatabaseConfiguration extends Application {

    private DbDialogController rootController;
    private Dimension screenSize;
    private int centreX;
    private int centreY;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        centreX = screenSize.width / 2;
        centreY = screenSize.height / 2;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/chromis/pos/dbmanager/DbDialog.fxml"));
        Parent connectRoot = loader.load();
        rootController = loader.getController();
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setX(centreX - 345);
        primaryStage.setY(centreY - 150);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(connectRoot, 690, 300));
        setUserAgentStylesheet(STYLESHEET_MODENA);
        primaryStage.show();

    }

}
