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
package uk.chromis.pos.cleandb;

import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.JMessageDialog;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.loader.Session;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.pos.forms.*;
import uk.chromis.pos.util.AltEncrypter;
import uk.chromis.pos.util.DirectoryEvent;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 *
 *
 */
public class JPanelCleandb extends JPanel implements JPanelView {

    private DirtyManager dirty = new DirtyManager();
    private Connection con;
    private String sdbmanager;
    private Connection con2;
    private String sdbmanager2;
    private Statement stmt;
    // private List<PanelConfig> m_panelconfig;

    private String db_user2;
    private String db_url2;
    private char[] pass;
    private String db_password2;
    private String changelog;
    private Liquibase liquibase;
    private List<String> tableList;
    private List<String> mysqlList;
    private List<String> derbyList;
    private List<String> postgresqlList;
    private DatabaseMetaData md;

    public JPanelCleandb(AppView oApp) {
        this(oApp.getProperties());
    }

    public JPanelCleandb(AppProperties props) {

        tableList = new ArrayList<>();

        initComponents();
        jPanel2.setPreferredSize(new java.awt.Dimension(645, 209));
        //m_panelconfig = new ArrayList<>();

        jtxtDbDriverLib.getDocument().addDocumentListener(dirty);
        jtxtDbDriver.getDocument().addDocumentListener(dirty);
        jtxtDbURL.getDocument().addDocumentListener(dirty);
        jtxtDbPassword.getDocument().addDocumentListener(dirty);
        jtxtDbUser.getDocument().addDocumentListener(dirty);
        jbtnDbDriverLib.addActionListener(new DirectoryEvent(jtxtDbDriverLib));
        jNewdbType.addActionListener(dirty);

        jNewdbType.addItem("Apache Derby Embedded");
        jNewdbType.addItem("MySQL");
        jNewdbType.addItem("PostgreSQL");

    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Configuration");
    }

    public Boolean getSeconddbDetails() {
        db_user2 = jtxtDbUser.getText();
        db_url2 = jtxtDbURL.getText();
        pass = jtxtDbPassword.getPassword();
        db_password2 = new String(pass);
        Properties connectionProps = new Properties();
        connectionProps.put("user", db_user2);
        connectionProps.put("password", db_password2);
        try {
            Class.forName(jtxtDbDriver.getText());
            ClassLoader cloader = new URLClassLoader(new URL[]{new File(jtxtDbDriverLib.getText()).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(jtxtDbDriver.getText(), true, cloader).newInstance()));
            con2 = (Connection) DriverManager.getConnection(db_url2, db_user2, db_password2);
            Session session2 = new Session(db_url2, db_user2, db_password2);
            sdbmanager2 = con2.getMetaData().getDatabaseProductName();
            return (true);
        } catch (ClassNotFoundException | MalformedURLException | InstantiationException | IllegalAccessException | SQLException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, AppLocal.getIntString("database.UnableToConnect"), e));
            return (false);
        }
    }

    @Override
    public void activate() throws BasicException {
        String db_user = (AppConfig.getInstance().getProperty("db.user"));
        String db_url = (AppConfig.getInstance().getProperty("db.URL"));
        String db_password = (AppConfig.getInstance().getProperty("db.password"));
        if (db_user != null && db_password != null && db_password.startsWith("crypt:")) {
            // the password is encrypted
            AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
            db_password = cypher.decrypt(db_password.substring(6));
        }
        try {
            Session session = AppViewConnection.createSession();
            con = DriverManager.getConnection(db_url, db_user, db_password);
            sdbmanager = con.getMetaData().getDatabaseProductName();
        } catch (BasicException | SQLException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, AppLocal.getIntString("database.UnableToConnect"), e));
            System.exit(0);
        }
    }

    @Override
    public boolean deactivate() {
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbtnMigrate = new javax.swing.JButton();
        jbtnExit = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jtxtDbDriverLib = new javax.swing.JTextField();
        jbtnDbDriverLib = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jtxtDbDriver = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jtxtDbURL = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtxtDbUser = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtxtDbPassword = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        jNewdbType = new javax.swing.JComboBox();
        jButtonTest = new javax.swing.JButton();
        pb = new javax.swing.JProgressBar();
        jPanel3 = new javax.swing.JPanel();
        jChkStockRecords = new javax.swing.JCheckBox();
        jchkFloorplans = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setPreferredSize(new java.awt.Dimension(600, 300));

        jbtnMigrate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnMigrate.setText("Create Clean Database");
        jbtnMigrate.setMaximumSize(new java.awt.Dimension(70, 33));
        jbtnMigrate.setMinimumSize(new java.awt.Dimension(70, 33));
        jbtnMigrate.setPreferredSize(new java.awt.Dimension(70, 33));
        jbtnMigrate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnMigrateActionPerformed(evt);
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

        jPanel1.setLayout(null);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "New Database details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText(AppLocal.getIntString("label.dbdriverlib")); // NOI18N

        jtxtDbDriverLib.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jbtnDbDriverLib.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/fileopen.png"))); // NOI18N
        jbtnDbDriverLib.setMaximumSize(new java.awt.Dimension(64, 32));
        jbtnDbDriverLib.setMinimumSize(new java.awt.Dimension(64, 32));
        jbtnDbDriverLib.setPreferredSize(new java.awt.Dimension(64, 32));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("Label.DbDriver")); // NOI18N

        jtxtDbDriver.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("Label.DbURL")); // NOI18N

        jtxtDbURL.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("Label.DbUser")); // NOI18N

        jtxtDbUser.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("Label.DbPassword")); // NOI18N

        jtxtDbPassword.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel5.setText("New  Database ");

        jNewdbType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNewdbTypeActionPerformed(evt);
            }
        });

        jButtonTest.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButtonTest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/database.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jButtonTest.setText(bundle.getString("Button.Test")); // NOI18N
        jButtonTest.setActionCommand(bundle.getString("Button.Test")); // NOI18N
        jButtonTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTestjButtonTestConnectionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtxtDbUser, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jtxtDbPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addComponent(jButtonTest, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jNewdbType, 0, 394, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jtxtDbDriver, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                                    .addComponent(jtxtDbDriverLib, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtxtDbURL))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jbtnDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jNewdbType, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtDbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtxtDbURL, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtDbUser, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jtxtDbPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jButtonTest, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("title.keep"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jChkStockRecords.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jChkStockRecords.setText(bundle.getString("label.stockrecords")); // NOI18N

        jchkFloorplans.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jchkFloorplans.setText(bundle.getString("label.floorplans")); // NOI18N

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText(" All products and their associated references, Users, Resources and Permissions will be \n retained in the new database.\n\n You may also retain details from the following");
        jScrollPane1.setViewportView(jTextArea1);
        jTextArea1.getAccessibleContext().setAccessibleParent(jTextArea1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jChkStockRecords, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jchkFloorplans, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 8, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jChkStockRecords)
                    .addComponent(jchkFloorplans))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jbtnMigrate, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jbtnExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pb, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(pb, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnMigrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void replaceSequenceNumbers() {
        pb.setString("Transferring sequences tables ");
        ResultSet seqRS;

        try {
            seqRS = con.createStatement().executeQuery("SELECT * FROM PICKUP_NUMBER");
            updateSequence(getNextValue(seqRS), "PICKUP_NUMBER");
        } catch (SQLException ex) {
        }
        try {
            seqRS = con.createStatement().executeQuery("SELECT * FROM TICKETSNUM");
            updateSequence(getNextValue(seqRS), "TICKETSNUM");
        } catch (SQLException ex) {
        }
        try {
            seqRS = con.createStatement().executeQuery("SELECT * FROM TICKETSNUM_PAYMENT");
            updateSequence(getNextValue(seqRS), "TICKETSNUM_PAYMENT");
        } catch (SQLException ex) {
        }
        try {
            seqRS = con.createStatement().executeQuery("SELECT * FROM TICKETSNUM_REFUND");
            updateSequence(getNextValue(seqRS), "TICKETSNUM_REFUND");
        } catch (SQLException ex) {
        }
        try {
            seqRS = con.createStatement().executeQuery("SELECT * FROM TICKETSNUM_INVOICE");
            updateSequence(getNextValue(seqRS), "TICKETSNUM_INVOICE");
        } catch (SQLException ex) {
        }
    }

    private void updateSequence(String nextValue, String table) throws SQLException {
        if ("MySQL".equals(sdbmanager2)) {
            con2.createStatement().executeUpdate("UPDATE " + table + " SET ID = " + nextValue);
        } else if (("Apache Derby".equals(sdbmanager2))) {
            con2.createStatement().executeUpdate("DROP TABLE " + table);
            Integer nextInt = Integer.parseInt(nextValue);
            nextInt++;
            con2.createStatement().executeUpdate("CREATE TABLE " + table + " (ID INTEGER GENERATED ALWAYS AS IDENTITY (START WITH " + nextInt.toString() + "))");
        } else {
            Integer nextInt = Integer.parseInt(nextValue);
            nextInt++;
            con2.createStatement().executeUpdate("ALTER SEQUENCE " + table + " RESTART WITH " + nextInt.toString());
        }
    }

    private String getNextValue(ResultSet rs) {
        try {
            while (rs.next()) {
                if (("PostgreSQL".equals(sdbmanager))) {
                    return rs.getString("LAST_VALUE");
                } else {;
                    return rs.getString("ID");
                }
            }
        } catch (SQLException ex) {
        }
        return "1";
    }

    private void performAction() {
        if (getSeconddbDetails()) {
            if (createDB()) {
                //DatabaseMetaData md;
                ResultSet rsTables = null;
                Statement stmtTables;
                try {
                    md = con.getMetaData();
                    switch (sdbmanager) {
                        case "MySQL":
                            rsTables = md.getTables(null, null, "%", null);
                            break;
                        case "PostgreSQL":
                            stmtTables = con.createStatement();
                            rsTables = stmtTables.executeQuery("SELECT * FROM information_schema.tables WHERE table_type = 'BASE TABLE' AND table_schema = 'public' ORDER BY table_type, table_name");
                            break;
                        default:
                            stmtTables = con.createStatement();
                            rsTables = stmtTables.executeQuery("SELECT * FROM sys.systables WHERE tabletype='T' ORDER BY tablename");
                    }
                    while (rsTables.next()) {
                        String tableName;
                        switch (sdbmanager) {
                            case "MySQL":
                                tableName = rsTables.getString(3).toUpperCase();
                                break;
                            case "PostgreSQL":
                                tableName = rsTables.getString("table_name").toUpperCase();
                                break;
                            default:
                                tableName = rsTables.getString("tablename").toUpperCase();
                        }

                        // if (!tableName.equalsIgnoreCase("ticketsnum") && !tableName.equalsIgnoreCase("pickup_number")
                        //         && !tableName.equalsIgnoreCase("ticketsnum_invoice") && !tableName.equalsIgnoreCase("ticketsnum_payment")
                        //         && !tableName.equalsIgnoreCase("ticketsnum_refund")) {
                        if (tableList.contains(tableName)) {

                            pb.setString("Transferring data from table " + tableName);
                            String SQL = " SELECT * FROM " + tableName;
                            Statement dataStmt = con.createStatement();
                            ResultSet dataRS = dataStmt.executeQuery(SQL);
                            ResultSetMetaData rsmd = dataRS.getMetaData();
                            PreparedStatement pstmt;

                            while (dataRS.next()) {
                                StringBuilder SQLInsert = new StringBuilder();
                                StringBuilder SQLSubString = new StringBuilder();
                                SQLSubString.append("values (");
                                SQLInsert.append("INSERT INTO ");
                                SQLInsert.append(tableName);
                                SQLInsert.append(" (");

                                for (int j = 1; j <= rsmd.getColumnCount(); j++) {
                                    if (dataRS.getString(rsmd.getColumnName(j)) != null) {
                                        SQLInsert.append(rsmd.getColumnName(j));
                                        SQLInsert.append(", ");
                                        SQLSubString.append("?, ");
                                    }
                                }

                                SQLInsert.setLength(SQLInsert.length() - 2);
                                SQLInsert.append(") ");
                                SQLSubString.setLength(SQLSubString.length() - 2);
                                SQLSubString.append(") ");
                                SQLInsert.append(SQLSubString);

                                pstmt = con2.prepareStatement(SQLInsert.toString());
                                int i = 1;
                                for (int j = 1; j <= rsmd.getColumnCount(); j++) {

                                    if (dataRS.getString(rsmd.getColumnName(j)) != null) {
                                        switch (rsmd.getColumnType(j)) {
                                            case 12: //varchar              
                                                pstmt.setString(i, dataRS.getString(rsmd.getColumnName(j)));
                                                break;
                                            case 8:  //double
                                                pstmt.setDouble(i, dataRS.getDouble(rsmd.getColumnName(j)));
                                                break;
                                            case 16: //boolean
                                            case -7:
                                                pstmt.setBoolean(i, dataRS.getBoolean(rsmd.getColumnName(j)));
                                                break;
                                            case 2004: //blob
                                            case -2:
                                            case -4:
                                                pstmt.setBytes(i, dataRS.getBytes(rsmd.getColumnName(j)));
                                                break;
                                            case 4:
                                            case 5://integer
                                                pstmt.setInt(i, dataRS.getInt(rsmd.getColumnName(j)));
                                                break;
                                            case 93:  //timestamp
                                                pstmt.setTimestamp(i, dataRS.getTimestamp(rsmd.getColumnName(j)));
                                                break;
                                            case 2:
                                                pstmt.setBigDecimal(i, dataRS.getBigDecimal(rsmd.getColumnName(j)));
                                                break;
                                        }
                                        i++;
                                    }
                                }
                                pstmt.executeUpdate();
                            }
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(JPanelCleandb.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (!jChkStockRecords.isSelected()) {
                    try {
                        con2.createStatement().executeUpdate("UPDATE STOCKCURRENT SET UNITS = 0.0");
                    } catch (SQLException ex) {
                    }
                }

                //replaceSequenceNumbers();
                try {
                    con2.createStatement().executeUpdate("DELETE FROM DATABASECHANGELOG WHERE ID='New Database'");
                } catch (SQLException ex) {

                }

                try {
                    con2.createStatement().executeUpdate("DELETE FROM DATABASECHANGELOG WHERE ID='Good FKs'");
                } catch (SQLException ex) {
                }

                try {
                    con2.createStatement().executeUpdate("DELETE FROM DATABASECHANGELOG WHERE ID='derby10.10.20'");
                } catch (SQLException ex) {
                }

                try {
                    con2.createStatement().executeUpdate("DELETE FROM DATABASECHANGELOG WHERE ID='Insert into tables required indexes for migration (New Database)'");
                } catch (SQLException ex) {
                }

                try {
                    con2.createStatement().executeUpdate("DELETE FROM DATABASECHANGELOG WHERE ID='Create Foreign Keys in tables part of migration (New Database)'");
                } catch (SQLException ex) {
                }

                try {
                    con2.createStatement().executeUpdate("DELETE FROM DATABASECHANGELOG WHERE ID='Create Primary keys (new db - migrate function)'");
                } catch (SQLException ex) {
                }

                try {
                    ClassLoader cloader = new URLClassLoader(new URL[]{new File(AppConfig.getInstance().getProperty("db.driverlib")).toURI().toURL()});
                    DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getInstance().getProperty("db.driver"), true, cloader).newInstance()));
                    Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(DriverManager.getConnection(db_url2, db_user2, db_password2)));
                    pb.setString("Adding System Defaults ");
                    changelog = "uk/chromis/pos/liquibase/common/adddefaults.xml";
                    liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database);
                    liquibase.update("implement");

                    pb.setString("Adding Keys & Indexes ");
                    changelog = "uk/chromis/pos/liquibase/scripts/cleandb/cleandb.xml";
                    liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database);
                    liquibase.update("implement");



                } catch (DatabaseException ex) {
                    Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LiquibaseException ex) {
                    Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MalformedURLException | SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
                }

                pb.setString("Clean Database Creation Complete ... ");
                pb.setIndeterminate(false);
               
                AppConfig.getInstance().setProperty("db.engine", sdbmanager2);
                AppConfig.getInstance().setProperty("db.driverlib", jtxtDbDriverLib.getText());
                AppConfig.getInstance().setProperty("db.driver", jtxtDbDriver.getText());
                AppConfig.getInstance().setProperty("db.URL", jtxtDbURL.getText());
                AppConfig.getInstance().setProperty("db.user", jtxtDbUser.getText());
                AltEncrypter cypher = new AltEncrypter("cypherkey" + jtxtDbUser.getText());
                AppConfig.getInstance().setProperty("db.password", "crypt:" + cypher.encrypt(new String(jtxtDbPassword.getPassword())));
                dirty.setDirty(false);
                 
                //  for (PanelConfig c : m_panelconfig) {
                //      c.saveProperties();
                //  }
                try {
                    AppConfig.getInstance().save();
                    JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.restartchanges"), AppLocal.getIntString("message.title"), JOptionPane.INFORMATION_MESSAGE);
                    jbtnMigrate.setEnabled(false);
                } catch (IOException ex) {
                    Logger.getLogger(JPanelCleandb.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                pb.setString("");
                pb.setIndeterminate(false);
                JOptionPane.showMessageDialog(this, "Database already exists", "Database Found", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private Boolean createDB() {
        // check if the database already exists
        Statement chkStmt;
        try {
            chkStmt = con2.createStatement();
            ResultSet chkRS = chkStmt.executeQuery("SELECT * FROM APPLICATIONS");
            chkRS.close();
            chkStmt.close();
            return false;
        } catch (SQLException ex) {
        }

        pb.setString("Creating blank database ..... ");
        if ((!"MySQL".equals(sdbmanager2)) && (!"PostgreSQL".equals(sdbmanager2)) && (!"Apache Derby".equals(sdbmanager2))) {
            return (false);
        }

        // DatabaseMetaData md;
        ResultSet rsTables = null;
        Statement stmtTables;
        try {
            md = con.getMetaData();
            switch (sdbmanager) {
                case "MySQL":
                    rsTables = md.getTables(null, null, "%", null);
                    break;
                case "PostgreSQL":
                    stmtTables = con.createStatement();
                    rsTables = stmtTables.executeQuery("SELECT * FROM information_schema.tables WHERE table_type = 'BASE TABLE' AND table_schema = 'public' ORDER BY table_type, table_name");
                    break;
                case "Apache Derby":
                    stmtTables = con.createStatement();
                    rsTables = stmtTables.executeQuery("SELECT * FROM sys.systables WHERE tabletype='T' ORDER BY tablename");
            }

            while (rsTables.next()) {
                switch (sdbmanager) {
                    case "MySQL":
                        if (!rsTables.getString(3).equalsIgnoreCase("ticketsnum") && !rsTables.getString(3).equalsIgnoreCase("pickup_number")
                                && !rsTables.getString(3).equalsIgnoreCase("ticketsnum_invoice") && !rsTables.getString(3).equalsIgnoreCase("ticketsnum_payment")
                                && !rsTables.getString(3).equalsIgnoreCase("ticketsnum_refund") && !rsTables.getString(3).equalsIgnoreCase("databasechangeloglock")) {
                            createTable(rsTables.getString(3));
                        }
                        break;
                    case "PostgreSQL":
                        if (!rsTables.getString("table_name").equalsIgnoreCase("ticketsnum") && !rsTables.getString("table_name").equalsIgnoreCase("pickup_number")
                                && !rsTables.getString("table_name").equalsIgnoreCase("ticketsnum_invoice") && !rsTables.getString("table_name").equalsIgnoreCase("ticketsnum_payment")
                                && !rsTables.getString("table_name").equalsIgnoreCase("ticketsnum_refund")) {
                            createTable(rsTables.getString("table_name").toLowerCase());
                        }
                        break;
                    case "Apache Derby":
                        if (!rsTables.getString("tablename").equalsIgnoreCase("ticketsnum") && !rsTables.getString("tablename").equalsIgnoreCase("pickup_number")
                                && !rsTables.getString("tablename").equalsIgnoreCase("ticketsnum_invoice") && !rsTables.getString("tablename").equalsIgnoreCase("ticketsnum_payment")
                                && !rsTables.getString("tablename").equalsIgnoreCase("ticketsnum_refund")) {
                            createTable(rsTables.getString("tablename").toUpperCase());                           
                        }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanelCleandb.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            ClassLoader cloader = new URLClassLoader(new URL[]{new File(AppConfig.getInstance().getProperty("db.driverlib")).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getInstance().getProperty("db.driver"), true, cloader).newInstance()));
            changelog = "uk/chromis/pos/liquibase/scripts/cleandb/sequences.xml";
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(DriverManager.getConnection(db_url2, db_user2, db_password2)));
            liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database);
            liquibase.update("implement");

        } catch (DatabaseException ex) {
            Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);

        } catch (LiquibaseException ex) {
            Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);

        } catch (MalformedURLException | SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(JRootApp.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            stmt = con2.createStatement();
            stmt.executeUpdate("DELETE FROM DATABASECHANGELOG");
            stmt.executeUpdate("DELETE FROM DATABASECHANGELOGLOCK");
        } catch (SQLException ex) {

        }
       
        return (true);
    }

  
    private void createTable(String table) {
        try {
            String SQL = " SELECT * FROM " + table;
            Statement dataStmt = con.createStatement();
            ResultSet dataRS = dataStmt.executeQuery(SQL);
            ResultSetMetaData rsmd = dataRS.getMetaData();

            DatabaseMetaData mdColumns = con.getMetaData();
            ResultSet rsColumns = mdColumns.getColumns(null, null, table, null);
            StringBuilder sql = new StringBuilder();
            sql.append("CREATE TABLE ");
            sql.append(table.toUpperCase());
            sql.append(" (\n");
            //System.out.println("************************************************************");
            //System.out.println(table);
            //System.out.println("************************************************************");

            while (rsColumns.next()) {
                sql.append(rsColumns.getString("COLUMN_NAME"));
                switch (rsColumns.getInt("DATA_TYPE")) {
                    case 12:
                        sql.append(" varchar(");
                        sql.append(rsColumns.getInt("COLUMN_SIZE"));
                        sql.append(")");
                        break;
                    case 8:
                        sql.append((sdbmanager2.equals("MySQL")) ? " DOUBLE" : " DOUBLE PRECISION");
                        break;
                    case -2:
                    case 2004:
                    case -4:
                        if (sdbmanager2.equals("MySQL")) {
                            sql.append(" mediumblob");
                            break;
                        } else if (sdbmanager2.equals("Apache Derby")) {
                            sql.append(" blob");
                            break;
                        }
                        sql.append(" bytea");
                        break;
                    case 16:
                    case -7:
                        sql.append((sdbmanager2.equals("MySQL")) ? " bit(1)" : " boolean");
                        break;
                    case 93:
                        // sql.append((sdbmanager2.equals("MySQL")) ? " datetime" : " timestamp");
                        sql.append(" timestamp");
                        break;
                    case 4:
                    case 5:
                        sql.append(" integer");
                        break;
                    case 2:
                        sql.append(" numeric(");
                        for (int k = 1; k <= rsmd.getColumnCount(); k++) {
                            if (rsmd.getColumnName(k).equalsIgnoreCase(rsColumns.getString("COLUMN_NAME"))) {
                                sql.append(rsmd.getPrecision(k));
                                sql.append(",");
                                sql.append(rsmd.getScale(k));
                                sql.append(")");
                            }
                        }
                        break;
                }

                if (rsColumns.getString("IS_NULLABLE").equalsIgnoreCase("yes")) {
                    sql.append(" null");
                } else {
                    sql.append(" not null ");
                    //  if (rsColumns.getString("IS_NULLABLE").equalsIgnoreCase("no")) {

                }
                // if (rsColumns.getString("COLUMN_DEF") == null) {
                //     sql.append(" not null");
                //  } else {
                if (rsColumns.getString("COLUMN_DEF") != null) {
                    sql.append(" default");
                    //System.out.println(rsColumns.getString("COLUMN_DEF"));
                    switch (rsColumns.getInt("DATA_TYPE")) {
                        case 4:
                        case 5:
                            sql.append(" ");
                            sql.append(rsColumns.getString("COLUMN_DEF"));
                            //sql.append(" not null");
                            break;
                        case 8:
                            sql.append(" ");
                            sql.append(rsColumns.getString("COLUMN_DEF"));
                            //sql.append(" not null");
                            break;
                        case 12:
                            sql.append(" '");
                            sql.append(rsColumns.getString("COLUMN_DEF").replaceAll("::character varying", "").replaceAll("'", ""));
                            sql.append("'");
                            //sql.append(" not null");
                            break;
                        case 93:
                            if (rsColumns.getString("COLUMN_DEF").equalsIgnoreCase("CURRENT_TIMESTAMP") || rsColumns.getString("COLUMN_DEF").equalsIgnoreCase("now()")) {
                                sql.append(" ");
                                sql.append("CURRENT_TIMESTAMP");
                                break;
                            } else {
                                sql.append(" '");
                                sql.append(rsColumns.getString("COLUMN_DEF").replaceAll("::timestamp without time zone", "").replaceAll("'", "").replaceAll("TIMESTAMP", "").replace("(", "").replace(")", ""));
                                sql.append("'");
                                //sql.append(" not null");
                                break;
                            }
                        case 16:
                        case -7:
                            if (sdbmanager2.equals("MySQL")) {
                                if (rsColumns.getString("COLUMN_DEF").equalsIgnoreCase("true")) {
                                    sql.append(" b'1'");
                                    //sql.append(" not null");
                                    break;
                                } else if (rsColumns.getString("COLUMN_DEF").equalsIgnoreCase("false")) {
                                    sql.append(" b'0'");
                                    //sql.append(" not null");
                                    break;
                                }
                            }
                            if (rsColumns.getString("COLUMN_DEF").contains("B'1'") || rsColumns.getString("COLUMN_DEF").contains("b'1'")) {
                                sql.append(" true");
                                //sql.append(" not null");
                                break;
                            } else if (rsColumns.getString("COLUMN_DEF").contains("B'0'") || rsColumns.getString("COLUMN_DEF").contains("b'0'")) {
                                sql.append(" false");
                                //sql.append(" not null");
                                break;
                            }
                            sql.append(" ");
                            sql.append(rsColumns.getString("COLUMN_DEF"));
                            //sql.append(" not null");
                            break;
                        case 2:
                            sql.append(" ");
                            sql.append(rsColumns.getString("COLUMN_DEF"));
                            //sql.append(" not null");
                            break;
                        default:
                            sql.append(" ");
                            sql.append(rsColumns.getString("COLUMN_DEF"));
                            //sql.append(" not null");
                            break;
                    }
                }

                //  }else{
                //       System.out.println("");
                // }
                sql.append(",\n");

                String name = rsColumns.getString("COLUMN_NAME");
                String type = rsColumns.getString("TYPE_NAME");
                int size = rsColumns.getInt("COLUMN_SIZE");
                String dvalue = rsColumns.getString("COLUMN_DEF");
                int dType = rsColumns.getInt("DATA_TYPE");
               // System.out.println("Column name: [" + name + "]; type: [" + type
               //         + "]; typeint: [" + dType + "]; size: [" + size + "]" + "; defaultvalue:[" + dvalue + "];");
            }
            sql.setLength(sql.length() - 2);
            sql.append("\n)");
            if (sdbmanager2.equals("MySQL")) {
                sql.append(" ENGINE = InnoDB DEFAULT CHARSET=utf8  ");
            }
           // System.out.println("--------------------------------------------------------------");
           // System.out.println(sql.toString());
            Statement stmt2;
            stmt2 = con2.createStatement();
            stmt2.executeUpdate(sql.toString());

            ResultSet PK = md.getPrimaryKeys(null, null, table);

            Boolean good = false;
            // add Primary keys
            sql = new StringBuilder();
            sql.append("ALTER TABLE ");
            sql.append(table.toUpperCase());
            sql.append(" ADD PRIMARY KEY (");

            while (PK.next()) {
                good = true;
             //   System.out.println(PK.getString("COLUMN_NAME") + "===" + PK.getString("PK_NAME"));
                sql.append(PK.getString("COLUMN_NAME"));
                sql.append(", ");
            }
            sql.setLength(sql.length() - 2);
            sql.append(")");
            //System.out.println(sql.toString());
            if (good) {
                stmt2 = con2.createStatement();
                //   stmt2.executeUpdate(sql.toString());
            }

        } catch (SQLException ex) {
            Logger.getLogger(JPanelCleandb.class.getName()).log(Level.SEVERE, null, ex);
            //try {
            //    con2.close();
            //    con.close();

            //} catch (SQLException ex1) {
            //    Logger.getLogger(JPanelCleandb.class.getName()).log(Level.SEVERE, null, ex1);
            //}
        }

    }

    private void jbtnMigrateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnMigrateActionPerformed

        tableList.clear();
        tableList.add("APPLICATIONS");
        tableList.add("PRODUCTS");
        tableList.add("CATEGORIES");
        tableList.add("ATTRIBUTE");
        tableList.add("ATTRIBUTEINSTANCE");
        tableList.add("ATTRIBUTESET");
        tableList.add("ATTRIBUTESETINSTANCE");
        tableList.add("ATTRIBUTEUSE");
        tableList.add("ATTRIBUTEVALUE");
        tableList.add("PRODUCTS_COM");
        tableList.add("PRODUCTS_KIT");
        tableList.add("TAXCATEGORIES");
        tableList.add("TAXCUSTCATEGORIES");
        tableList.add("TAXES");
        tableList.add("SITEGUID");
        tableList.add("DBPERMISSIONS");
        tableList.add("ROLES");
        tableList.add("PEOPLE");
        tableList.add("RESOURCES");
        tableList.add("STOCKCURRENT");
        tableList.add("LOCATIONS");
        tableList.add("SUPPLIERS");
        tableList.add("SITEGUID");
        tableList.add("SITES");

        if (jchkFloorplans.isSelected()) {
            tableList.add("PLACES");
            tableList.add("FLOORS");
        }

        //     mysqlList.clear();
        Thread workThread = new Thread() {
            public void run() {
                performAction();
            }
        };
        workThread.start();

        pb.setIndeterminate(true);
        pb.setStringPainted(true);

    }//GEN-LAST:event_jbtnMigrateActionPerformed

    private void jbtnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnExitActionPerformed
        deactivate();
        System.exit(0);
    }//GEN-LAST:event_jbtnExitActionPerformed

    private void jButtonTestjButtonTestConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestjButtonTestConnectionActionPerformed
        try {
            String driverlib = jtxtDbDriverLib.getText();
            String driver = jtxtDbDriver.getText();
            String url = jtxtDbURL.getText();
            String user = jtxtDbUser.getText();
            String password = new String(jtxtDbPassword.getPassword());

            ClassLoader cloader = new URLClassLoader(new URL[]{new File(driverlib).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(driver, true, cloader).newInstance()));

            Session session = new Session(url, user, password);
            Connection connection = session.getConnection();
            boolean isValid = (connection == null) ? false : connection.isValid(1000);

            if (isValid) {
                JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.databaseconnectsuccess"), "Connection Test", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, "Connection Error"));
            }
        } catch (InstantiationException | IllegalAccessException | MalformedURLException | ClassNotFoundException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.databasedrivererror"), e));
        } catch (SQLException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.databaseconnectionerror"), e));
        } catch (HeadlessException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, "Unknown exception", e));
        }
    }//GEN-LAST:event_jButtonTestjButtonTestConnectionActionPerformed

    private void jNewdbTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNewdbTypeActionPerformed
        if ("MySQL".equals(jNewdbType.getSelectedItem())) {
            jtxtDbDriverLib.setText(System.getProperty("user.dir") + "/lib/mysql-connector-java-5.1.42.jar");
            jtxtDbDriver.setText("com.mysql.jdbc.Driver");
            jtxtDbURL.setText("jdbc:mysql://localhost:3306/chromispos");
            //jtxtDbURL.setText("jdbc:mysql://192.168.254.22:3306/jdl");
            //jtxtDbUser.setText("eposuser");
            //jtxtDbPassword.setText("epos");
        } else if ("PostgreSQL".equals(jNewdbType.getSelectedItem())) {
            jtxtDbDriverLib.setText(System.getProperty("user.dir") + "/lib/postgresql-9.2-1003.jdbc4.jar");
            jtxtDbDriver.setText("org.postgresql.Driver");
            jtxtDbURL.setText("jdbc:postgresql://localhost:5432/chromispos");
        } else if ("Apache Derby Embedded".equals(jNewdbType.getSelectedItem())) {
            jtxtDbDriverLib.setText(System.getProperty("user.dir") + "/lib/derby-10.10.2.0.jar");
            jtxtDbDriver.setText("org.apache.derby.jdbc.EmbeddedDriver");
            jtxtDbURL.setText("jdbc:derby:" + new File(new File(System.getProperty("user.home")), AppLocal.APP_ID + "-database").getAbsolutePath() + ";create=true");
            jtxtDbUser.setText("");
            jtxtDbPassword.setText("");
        }
    }//GEN-LAST:event_jNewdbTypeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonTest;
    private javax.swing.JCheckBox jChkStockRecords;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JComboBox jNewdbType;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton jbtnDbDriverLib;
    private javax.swing.JButton jbtnExit;
    private javax.swing.JButton jbtnMigrate;
    private javax.swing.JCheckBox jchkFloorplans;
    private javax.swing.JTextField jtxtDbDriver;
    private javax.swing.JTextField jtxtDbDriverLib;
    private javax.swing.JPasswordField jtxtDbPassword;
    private javax.swing.JTextField jtxtDbURL;
    private javax.swing.JTextField jtxtDbUser;
    private javax.swing.JProgressBar pb;
    // End of variables declaration//GEN-END:variables
}
