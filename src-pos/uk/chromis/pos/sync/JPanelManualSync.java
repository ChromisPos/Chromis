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

package uk.chromis.pos.sync;

import uk.chromis.pos.forms.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import uk.chromis.pos.util.AltEncrypter;
//import uk.chromis.pos.sync.Sync;

/**
 *
 *
 * @author John
 */
public class JPanelManualSync extends JPanel implements JPanelView {
    
    private static AltEncrypter cypher;
    
    private Connection localConnection;
    private Connection remoteConnection;
    
    private String syncType = "";
    private String localGUID = "";
    private String remoteGUID = "";
    private String remoteURL;
    private String remoteUser;
    private String remotePassword;
    
    private ResultSet rs;
    private ResultSet rs2;
    private String SQL;
    private PreparedStatement pstmt;
    
    public JPanelManualSync(AppView oApp) {
        initComponents();
        if (oApp != null) {
            jbtnExit.setVisible(false);
        }
    }
    
    @Override
    public JComponent getComponent() {
        return this;
    }
    
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Sync");
    }
    
    @Override
    public boolean deactivate() {
        try {
            localConnection.close();
            if (remoteConnection != null) {
                remoteConnection.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanelManualSync.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (true);
    }
    
    public void activate() {

        // lets get a connection to the local database
        localConnection = getLocalConnection();

        // get what type of site the local system is
        syncType = getSiteType(localConnection);
        
        if (syncType.equals("remote")) {
            remoteConnection = getRemoteConnection();
            jtxtSyncType.setText("Remote to Central System");
            if (remoteConnection == null) {
                jtxtConnectionStatus.setText("Unable to connect to Remote System");
            } else {
                jtxtConnectionStatus.setText("Connected to Central System");
                jtxtFromLocal.setText(checkLocalRecords(localConnection).toString() + " local records found to process.");
                jtxtChangedObjects.setText(checkLocalTransferRecords(localConnection, localGUID).toString() + " local records found to transfer.");
                jtxtFromRemote.setText(checkRemoteRecords(remoteConnection, localGUID).toString() + " remote records found to transfer.");
                jtxtToProcess.setText(checkLocalRecordsToProcess(localConnection, localGUID) + " transfer records to process.");
            }
        } else if (syncType.equals("central")) {
            remoteConnection = getSiteConnection(localConnection);
            jtxtSyncType.setText("Central System to Remote");
            if (remoteConnection == null) {
                jtxtConnectionStatus.setText("Unable to connect to Remote System");
            } else {
                jtxtConnectionStatus.setText("Connected to Remote System");
                jtxtFromLocal.setText(checkLocalRecords(localConnection).toString() + " local records found to process.");
                jtxtChangedObjects.setText(checkLocalTransferRecords(localConnection, localGUID).toString() + " local records found to transfer.");
                jtxtFromRemote.setText(checkRemoteRecords(remoteConnection, localGUID).toString() + " remote records found to transfer.");
                jtxtToProcess.setText(checkLocalRecordsToProcess(localConnection, localGUID) + " transfer records to process.");
            }
        } else {
            jtxtSyncType.setText("Option not available for this system");
            jbtnSync.setEnabled(false);
        }
    }
    
    private String getSiteType(Connection connection) {
        Statement stmt;
        try {
            stmt = (Statement) localConnection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM SITEGUID");
            while (rs.next()) {
                localGUID = rs.getString("GUID");
            }
            stmt.close();
            DatabaseMetaData mtdt = localConnection.getMetaData();
            rs2 = mtdt.getTables(null, null, "CENTRALSERVER", null);
            while (rs2.next()) {
                stmt = (Statement) localConnection.createStatement();
                rs = stmt.executeQuery("SELECT * FROM CENTRALSERVER");
                while (rs.next()) {
                    remoteGUID = rs.getString("GUID");
                    remoteURL = rs.getString("URL");
                    remoteUser = rs.getString("USERNAME");
                    remotePassword = rs.getString("PASSWORD");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanelManualSync.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (localGUID.equals(remoteGUID)) {
            return "central";
        } else if (!remoteGUID.equals("")) {
            return "remote";
        }
        return "";
    }
    
    private Connection getLocalConnection() {
        String sDBUser = AppConfig.getInstance().getProperty("db.user");
        String sDBPassword = AppConfig.getInstance().getProperty("db.password");
        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
            cypher = new AltEncrypter("cypherkey" + sDBUser);
            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
        }
        try {
            return DriverManager.getConnection(AppConfig.getInstance().getProperty("db.URL"), sDBUser, sDBPassword);
        } catch (SQLException ex) {
            return null;
        }
    }
    
    private Connection getRemoteConnection() {
        String sDBUser = remoteUser;
        String sDBPassword = remotePassword;
        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
            cypher = new AltEncrypter("cypherkey" + sDBUser);
            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
        }
        try {
            return DriverManager.getConnection(remoteURL, sDBUser, sDBPassword);
        } catch (SQLException ex) {
            return null;
        }
    }
    
    private Connection getSiteConnection(Connection connection) {
        try {
            Statement stmt = (Statement) localConnection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM SITES");
            while (rs.next()) {
                remoteGUID = rs.getString("GUID");
                remoteURL = rs.getString("SITEURL");
                remoteUser = rs.getString("SITEUSERNAME");
                remotePassword = rs.getString("SITEPASSWORD");
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanelManualSync.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return getRemoteConnection();
    }
    
    private Integer checkLocalRecords(Connection connection) {
        // check if there are any records to process       
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM SYNCDATA WHERE PROCESSED = false");
            ResultSet rs2 = pstmt.executeQuery();
            if (rs2.next()) {
                return rs2.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanelManualSync.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    private Integer checkRemoteRecords(Connection connection, String GUID) {
        // check if there are any records to process       
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM CHANGEDOBJECT WHERE TRANSFERSTATUS = false AND TARGETSITE = ?");
            pstmt.setString(1, GUID);
            ResultSet rs2 = pstmt.executeQuery();
            if (rs2.next()) {
                return rs2.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanelManualSync.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    private Integer checkLocalTransferRecords(Connection connection, String GUID) {
        // check if there are any records to process       
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM CHANGEDOBJECT WHERE TRANSFERSTATUS = false AND SOURCESITE = ?");
            pstmt.setString(1, GUID);
            ResultSet rs2 = pstmt.executeQuery();
            if (rs2.next()) {
                return rs2.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanelManualSync.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    private Integer checkLocalRecordsToProcess(Connection connection, String GUID) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM CHANGEDOBJECT WHERE CHANGESTATUS = false AND TARGETSITE = ?");
            pstmt.setString(1, GUID);
            ResultSet rs2 = pstmt.executeQuery();
            if (rs2.next()) {
                return rs2.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanelManualSync.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtxtSyncType = new javax.swing.JTextField();
        jtxtConnectionStatus = new javax.swing.JTextField();
        jlabelSyncstatus = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtxtFromLocal = new javax.swing.JTextField();
        jtxtChangedObjects = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxtSyncProcess = new javax.swing.JTextField();
        jbtnSync = new javax.swing.JButton();
        jbtnExit = new javax.swing.JButton();
        jtxtFromRemote = new javax.swing.JTextField();
        jtxtToProcess = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(600, 300));

        jPanel1.setLayout(null);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLabel1.setText(bundle.getString("label.syncservertype")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(113, 25));

        jtxtSyncType.setEditable(false);
        jtxtSyncType.setBackground(new java.awt.Color(255, 255, 255));
        jtxtSyncType.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtSyncType.setBorder(null);
        jtxtSyncType.setPreferredSize(new java.awt.Dimension(0, 25));

        jtxtConnectionStatus.setEditable(false);
        jtxtConnectionStatus.setBackground(new java.awt.Color(255, 255, 255));
        jtxtConnectionStatus.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtConnectionStatus.setBorder(null);
        jtxtConnectionStatus.setMinimumSize(new java.awt.Dimension(6, 25));
        jtxtConnectionStatus.setPreferredSize(new java.awt.Dimension(67, 25));

        jlabelSyncstatus.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jlabelSyncstatus.setText(bundle.getString("label.syncstatus")); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(bundle.getString("label.syncstats")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(119, 25));

        jtxtFromLocal.setEditable(false);
        jtxtFromLocal.setBackground(new java.awt.Color(255, 255, 255));
        jtxtFromLocal.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtFromLocal.setBorder(null);

        jtxtChangedObjects.setEditable(false);
        jtxtChangedObjects.setBackground(new java.awt.Color(255, 255, 255));
        jtxtChangedObjects.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtChangedObjects.setBorder(null);

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText(bundle.getString("label.syncprocess")); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(137, 25));

        jtxtSyncProcess.setEditable(false);
        jtxtSyncProcess.setBackground(new java.awt.Color(255, 255, 255));
        jtxtSyncProcess.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtSyncProcess.setBorder(null);

        jbtnSync.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnSync.setText(AppLocal.getIntString("button.activatesync")); // NOI18N
        jbtnSync.setMaximumSize(new java.awt.Dimension(70, 33));
        jbtnSync.setMinimumSize(new java.awt.Dimension(70, 33));
        jbtnSync.setPreferredSize(new java.awt.Dimension(70, 33));
        jbtnSync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSyncActionPerformed(evt);
            }
        });

        jbtnExit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnExit.setText(AppLocal.getIntString("Button.Exit")); // NOI18N
        jbtnExit.setMaximumSize(new java.awt.Dimension(70, 33));
        jbtnExit.setMinimumSize(new java.awt.Dimension(70, 33));
        jbtnExit.setPreferredSize(new java.awt.Dimension(70, 33));
        jbtnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnExitActionPerformed(evt);
            }
        });

        jtxtFromRemote.setEditable(false);
        jtxtFromRemote.setBackground(new java.awt.Color(255, 255, 255));
        jtxtFromRemote.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtFromRemote.setBorder(null);

        jtxtToProcess.setEditable(false);
        jtxtToProcess.setBackground(new java.awt.Color(255, 255, 255));
        jtxtToProcess.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jtxtToProcess.setBorder(null);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlabelSyncstatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jbtnSync, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jtxtSyncProcess, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtChangedObjects, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtFromLocal, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtConnectionStatus, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jtxtSyncType, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jtxtFromRemote, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtToProcess))
                        .addGap(67, 67, 67))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtSyncType, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtConnectionStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlabelSyncstatus, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtFromLocal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addComponent(jtxtChangedObjects, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jtxtFromRemote, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jtxtToProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtSyncProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnSync, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    private void jbtnSyncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSyncActionPerformed
        // create a new sync instance
        /*
        Sync sync = new Sync();
        String syncMessage = "Unable to run 'Sync Process'";
        
        if (sync.checkSync(localConnection, remoteConnection)) {
            syncMessage = "";
        }

// process the sync table data on this machine
        jtxtSyncProcess.setText(syncMessage.equals("") ? "Processing local syncdata table." : syncMessage);
        sync.processSyncData(localConnection);

        // Sync the changedobject tables       
        jtxtSyncProcess.setText(syncMessage.equals("") ? "Synchronising data between server." : syncMessage);
        sync.syncSites(localConnection);

        jtxtSyncProcess.setText(syncMessage.equals("") ? "Processing local changed objects table." : syncMessage);
        sync.processChangedObjects(localConnection);

        sync.updateStock(localConnection);
        sync.updateStatusFlags(localConnection);
        sync.cleanChangedObjectsTable(localConnection);
        sync.getSiteList(localConnection);
        
        jtxtFromLocal.setText(checkLocalRecords(localConnection).toString() + " local records found to process.");
        jtxtChangedObjects.setText(checkLocalTransferRecords(localConnection, localGUID).toString() + " local records found to transfer.");
        jtxtFromRemote.setText(checkRemoteRecords(remoteConnection, localGUID).toString() + " remote records found to transfer.");
        jtxtToProcess.setText(checkLocalRecordsToProcess(localConnection, localGUID) + " transfered records to process.");
        
        jtxtSyncProcess.setText(syncMessage.equals("") ? "Sync Process Complete ..." : syncMessage);
*/
    }//GEN-LAST:event_jbtnSyncActionPerformed

    private void jbtnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExitActionPerformed
        deactivate();
        System.exit(0);
    }//GEN-LAST:event_jbtnExitActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton jbtnExit;
    private javax.swing.JButton jbtnSync;
    private javax.swing.JLabel jlabelSyncstatus;
    private javax.swing.JTextField jtxtChangedObjects;
    private javax.swing.JTextField jtxtConnectionStatus;
    private javax.swing.JTextField jtxtFromLocal;
    private javax.swing.JTextField jtxtFromRemote;
    private javax.swing.JTextField jtxtSyncProcess;
    private javax.swing.JTextField jtxtSyncType;
    private javax.swing.JTextField jtxtToProcess;
    // End of variables declaration//GEN-END:variables
}
