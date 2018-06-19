/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c) 2015-2018
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

package uk.chromis.pos.admin;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.parsers.SAXParser;
import uk.chromis.data.loader.Session;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.util.DbUtils;

/**
 *
 *
 */
public class JPermissionsList extends javax.swing.JDialog {

    private Connection con;
    private ResultSet rs;
    private PreparedStatement pstmt;
    private String ID;
    private String SQL;
    private DataLogicAdmin dlAdmin;
    private Session ls;
    private String siteGuid;
    private DataLogicSync dlSync;
    private static SAXParser m_sp = null;

    private JPermissionsList(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }

    private JPermissionsList(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
    }

    private void init(Session s, String siteGuid) {
        this.siteGuid = siteGuid;

        ls = s;
        initComponents();
        dlSync = new DataLogicSync();
        dlSync.init(ls);
        if (!dlSync.isCentral()) {
            jbtnDeleteEntryAllSites.setVisible(false);
            setTitle(AppLocal.getIntString("label.selectclass"));
        }

        buildPermissionList();
    }

    private void buildPermissionList() {
        try {
            con = ls.getConnection();

            pstmt = con.prepareStatement("SELECT CLASSNAME FROM DBPERMISSIONS WHERE SITEGUID = ? ORDER BY LOWER (CLASSNAME) ");
            pstmt.setString(1, siteGuid);
            rs = pstmt.executeQuery();

            jTableSelector.setModel(DbUtils.resultSetToTableModel(rs));
            jTableSelector.getColumnModel().getColumn(0).setPreferredWidth(180);
            jTableSelector.setRowSelectionAllowed(true);
            jTableSelector.getTableHeader().setReorderingAllowed(true);
        } catch (Exception e) {
        }

    }

    public static JPermissionsList getPermissionsList(Component parent, Session s, String siteGuid) {
        Window window = SwingUtilities.getWindowAncestor(parent);
        JPermissionsList myMsg;
        if (window instanceof Frame) {
            myMsg = new JPermissionsList((Frame) window, true);
        } else {
            myMsg = new JPermissionsList((Dialog) window, true);
        }
        myMsg.init(s, siteGuid);
        myMsg.applyComponentOrientation(parent.getComponentOrientation());

        return myMsg;
    }

    private void updateRoles(String classname, String siteGuid) {
        try {
            String sql;
            Statement stmt = (Statement) con.createStatement();
            PreparedStatement pstmt = con.prepareStatement("UPDATE ROLES SET PERMISSIONS = ? WHERE ID = ?  AND SITEGUID = ?");
            if (siteGuid.equals("")) {
                sql = "SELECT * FROM ROLES ";
            } else {
                sql = "SELECT * FROM ROLES WHERE SITEGUID = '" + siteGuid + "' ";
            }
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
                byte[] bdata = rs.getBytes("PERMISSIONS");
                sb.append(new String(bdata));
                String subStr = "<class name=\"" + classname + "\"/>";
                int position = sb.lastIndexOf(subStr);
                if (position >= 0) {
                    sb.replace(position, position + subStr.length(), "");
                    pstmt.setBytes(1, String.valueOf(sb).getBytes());
                    pstmt.setString(2, rs.getString("ID"));
                    pstmt.setString(3, rs.getString("SITEGUID"));
                    pstmt.executeUpdate();
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(JPermissionsList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTableSelector = new javax.swing.JTable();
        jText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jbtnDeleteEntry1 = new javax.swing.JButton();
        jbtnDeleteEntry = new javax.swing.JButton();
        jbtnDeleteEntryAllSites = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTableSelector.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTableSelector.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Classname", "Description"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableSelector.setRowHeight(25);
        jTableSelector.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableSelectorMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableSelector);
        if (jTableSelector.getColumnModel().getColumnCount() > 0) {
            jTableSelector.getColumnModel().getColumn(1).setPreferredWidth(70);
        }

        jText.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jText.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jText.setEnabled(false);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLabel1.setText(bundle.getString("label.removeentryfor")); // NOI18N

        jbtnDeleteEntry1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnDeleteEntry1.setText(bundle.getString("Button.Exit")); // NOI18N
        jbtnDeleteEntry1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDeleteEntry1ActionPerformed(evt);
            }
        });

        jbtnDeleteEntry.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnDeleteEntry.setText(bundle.getString("Button.deleteclass")); // NOI18N
        jbtnDeleteEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDeleteEntryActionPerformed(evt);
            }
        });

        jbtnDeleteEntryAllSites.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnDeleteEntryAllSites.setText(bundle.getString("Button.deleteclassallsites")); // NOI18N
        jbtnDeleteEntryAllSites.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDeleteEntryAllSitesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jbtnDeleteEntryAllSites, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(jbtnDeleteEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jbtnDeleteEntry1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnDeleteEntryAllSites)
                    .addComponent(jbtnDeleteEntry)
                    .addComponent(jbtnDeleteEntry1))
                .addGap(0, 8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(1, 1, 1))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jText)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(675, 306));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnDeleteEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteEntryActionPerformed

        Object[] options = {AppLocal.getIntString("Button.Yes"), AppLocal.getIntString("Button.No")};
        if (JOptionPane.showOptionDialog(this,
                AppLocal.getIntString("message.deleteclass"), AppLocal.getIntString("Message.adminwarning"),
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]) == 0) {
            try {
                con = ls.getConnection();
                pstmt = con.prepareStatement("DELETE FROM DBPERMISSIONS WHERE CLASSNAME = ? AND SITEGUID = ? ");
                pstmt.setString(1, jText.getText());
                pstmt.setString(2, siteGuid);
                pstmt.executeUpdate();
                dispose();
                updateRoles(jText.getText(), siteGuid);
            } catch (Exception e) {
            }
        }

    }//GEN-LAST:event_jbtnDeleteEntryActionPerformed

    private void jTableSelectorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableSelectorMouseClicked
        int row = jTableSelector.getSelectedRow();
        jText.setText(jTableSelector.getModel().getValueAt(row, 0).toString());
    }//GEN-LAST:event_jTableSelectorMouseClicked

    private void jbtnDeleteEntry1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteEntry1ActionPerformed
        dispose();
    }//GEN-LAST:event_jbtnDeleteEntry1ActionPerformed

    private void jbtnDeleteEntryAllSitesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteEntryAllSitesActionPerformed
        Object[] options = {AppLocal.getIntString("Button.Yes"), AppLocal.getIntString("Button.No")};
        if (JOptionPane.showOptionDialog(this,
                AppLocal.getIntString("message.deleteclass"), AppLocal.getIntString("Message.adminwarning"),
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]) == 0) {
            try {
                con = ls.getConnection();
                pstmt = con.prepareStatement("DELETE FROM DBPERMISSIONS WHERE CLASSNAME = ? ");
                pstmt.setString(1, jText.getText());
                pstmt.executeUpdate();
                updateRoles(jText.getText(), "");
                dispose();
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_jbtnDeleteEntryAllSitesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableSelector;
    private javax.swing.JTextField jText;
    private javax.swing.JButton jbtnDeleteEntry;
    private javax.swing.JButton jbtnDeleteEntry1;
    private javax.swing.JButton jbtnDeleteEntryAllSites;
    // End of variables declaration//GEN-END:variables

}
