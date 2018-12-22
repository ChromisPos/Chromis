/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c)2015-2018
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;
import uk.chromis.pos.dialogs.JAlert;
import uk.chromis.pos.forms.AppLocal;

/**
 *
 * @author John
 */
public class Connect extends JFrame {

    private JPanel connectPanel;
    private GroupLayout layout;
    private JLabel lblWaiting;

    public Connect() {
        createPanel();
        setUndecorated(true);
        setSize(275, 65);
        this.add(connectPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setResizable(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void showPanel(Boolean visible) {
        setVisible(visible);
        if (visible) {
            try {
                //show frame for a min of 2 seconds
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void remove() {
        dispose();
    }

    private void createPanel() {
        MigLayout layout = new MigLayout("insets 5 5 0 10", "");
        connectPanel = new JPanel(layout);
        connectPanel.setPreferredSize(new Dimension(275, 60));
        connectPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLUE));
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/fixedimages/spinning.gif")));
        connectPanel.add(logoLabel);
        lblWaiting = new JLabel();
//        lblWaiting.setFont(new Font("Arial", 1, 14));
        lblWaiting.setHorizontalAlignment(SwingConstants.RIGHT);
        connectPanel.add(lblWaiting, "gapx 10");
    }

}
