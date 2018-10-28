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

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.sql.Connection;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.JMessageDialog;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.loader.Session;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppProperties;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.forms.JPanelView;

public class JPanelConfiguration extends JPanel implements JPanelView {

    private AppView m_App;
    private AppProperties m_props;
    private Session s;
    private Connection con;
    private ConfigurationController controller;

    private DataLogicSystem m_dlSystem;

    private static final int JFXPANEL_WIDTH_INT = 1000;
    private static final int JFXPANEL_HEIGHT_INT = 800;
    private static JFXPanel fxContainer;

    public JPanelConfiguration(AppView app) {

        m_App = app;
        m_props = m_App.getProperties();
        fxContainer = new JFXPanel();

        JLabel lbl = new JLabel();
        lbl.setText(AppLocal.getIntString("label.pleasewait"));
        add(lbl);

        //  fxContainer.setPreferredSize(new Dimension(JFXPANEL_WIDTH_INT, JFXPANEL_HEIGHT_INT));
        add(fxContainer, BorderLayout.CENTER);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                createScene();
                lbl.setVisible(false);
            }
        });

    }

    private void createScene() {
        Scene scene;
        Parent root = null;
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(getClass().getResource("/uk/chromis/pos/config/Configuration.fxml"));
            root = loader.load();
        } catch (IOException ex) {
            System.out.println("Error trying to load Config");
            System.out.println("***************************");
            System.out.println(ex);
        }
        controller = loader.getController();
        scene = new Scene(root, 980, 640);
        loadStyle(scene);
        fxContainer.setScene(scene);
        controller.btnExit.setVisible(false);

    }

    private void loadStyle(Scene node) {
        try {
            final String resource = Paths.get(System.getProperty("user.dir") + "/cssStyles/Chromis.css").toUri().toURL().toExternalForm();
            node.getStylesheets().add(resource);
        } catch (MalformedURLException e) {
            System.out.println("No Chromis.css File found !");
        }
    }

    @Override
    public void activate() throws BasicException {

    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Configuration");
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public boolean deactivate() {
        if (controller.isDirty()) {
            int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannasave"), AppLocal.getIntString("title.editor"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                try {
                    AppConfig.getInstance().save();
                    JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.restartchanges"), AppLocal.getIntString("message.title"), JOptionPane.INFORMATION_MESSAGE);
                    controller.setDirty(false);
                } catch (IOException e) {
                    JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotsaveconfig"), e));
                }
                return true;
            } else {
                return res == JOptionPane.NO_OPTION;
            }
        } else {
            return true;
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMinimumSize(new java.awt.Dimension(1000, 800));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(1000, 800));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
