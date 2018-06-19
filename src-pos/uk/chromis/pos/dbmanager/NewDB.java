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

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class NewDB {

    public static Boolean dbOK = false;
    private static JFrame frame;
    private static Boolean working = true;

    public NewDB() {

    }

    public void initSwingComponents() {
        frame = new JFrame("Chromis Point of Sale");

        frame.setLayout(new BorderLayout());
        JFXPanel jfxPanel = new JFXPanel();
        try {
            frame.setIconImage(ImageIO.read(NewDB.class.getResourceAsStream("/uk/chromis/fixedimages/smllogo.png")));
        } catch (IOException e) {
            //remove coffe cup icon in JFrame
            Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
            frame.setIconImage(icon);
        }

        frame.add(jfxPanel, BorderLayout.CENTER);
        frame.setSize(650, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        Platform.runLater(() -> initFX(jfxPanel));
    }

    private void initFX(JFXPanel jfxPanel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/chromis/pos/dbmanager/NewDB.fxml"));
            Parent root = loader.load();
            NewDBController controller = loader.getController();
            Scene scene = new Scene(root, 650, 300);
            jfxPanel.setScene(scene);
            root.requestFocus();
        } catch (IOException exc) {
            System.exit(1);
        }
    }

    public static void setWorking(Boolean working) {
        NewDB.working = working;
    }

    public Boolean isWorking() {
        return working;
    }

    public Boolean getStatus() {
        return dbOK;
    }

    public static void setStatus(Boolean status) {
        dbOK = status;
    }

    public void setVisible(Boolean visible) {
        frame.setVisible(visible);
    }

    public static void remove() {
        frame.setVisible(false);
        frame = null;
    }
}
