/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.dbmanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author John
 */
public class Connect {

    private JFrame frame;
    private JLabel jLabel1;
    private JPanel connectPanel;
    private GroupLayout layout;

    public Connect() {
        createPanel();
        frame = new JFrame();
        frame.setUndecorated(true);
        frame.add(connectPanel, BorderLayout.CENTER);
        frame.setSize(190, 50);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
    }

    public void setVisible(Boolean visible) {
        frame.setVisible(visible);
    }

    public void remove(){
        frame.dispose();
    }
    
    private void createPanel() {
        connectPanel = new JPanel();
        jLabel1 = new JLabel();

        connectPanel.setPreferredSize(new Dimension(192, 50));

        jLabel1.setFont(new Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("Connecting to Database");

        layout = new GroupLayout(connectPanel);
        connectPanel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(21, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                        .addContainerGap())
        );
        connectPanel.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
    }

}
