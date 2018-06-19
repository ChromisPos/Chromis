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

public class UpdateDB {

    public static JFrame uframe;   
    private String sDBVersion;
    public static Boolean working = true;

    public UpdateDB() {
    }

    public void initSwingComponents() {
        uframe = new JFrame("Chromis Point of Sale");
        uframe.setLayout(new BorderLayout());
        JFXPanel jfxPanel = new JFXPanel();

      
      try {
            uframe.setIconImage(ImageIO.read(UpdateDB.class.getResourceAsStream("/uk/chromis/fixedimages/smllogo.png")));
        } catch (IOException e) {
        //remove coffe cup icon in JFrame
        Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
          uframe.setIconImage(icon);
        }
        uframe.add(jfxPanel, BorderLayout.CENTER);
        uframe.setSize(520, 300);
        uframe.setLocationRelativeTo(null);
        uframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        uframe.setVisible(true);
        Platform.runLater(() -> initFX(jfxPanel));
    }

    private void initFX(JFXPanel jfxPanel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/chromis/pos/dbmanager/Update.fxml"));
            Parent root = loader.load();
            UpdateController controller = loader.getController();
            Scene scene = new Scene(root, 520, 300);
            controller.setDBVersion(sDBVersion);           
            jfxPanel.setScene(scene);
            root.requestFocus();
        } catch (IOException exc) {
            System.exit(1);
        }
    }

    public void setVersion(String version) {
        sDBVersion = version;
    }

    public static void stopWorking() {
        working = false;
    }

    public static boolean isWorking() {
        return working;
    }

}
