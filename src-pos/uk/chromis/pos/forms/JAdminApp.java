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
package uk.chromis.pos.forms;

import java.awt.CardLayout;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import uk.chromis.basic.BasicException;
import uk.chromis.beans.JFlowPanel;
import uk.chromis.beans.JPasswordDialog;
import uk.chromis.data.gui.JMessageDialog;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.loader.Session;
import uk.chromis.pos.printer.DeviceTicket;
import uk.chromis.pos.printer.TicketParser;
import uk.chromis.pos.printer.TicketPrinterException;
import uk.chromis.pos.scale.DeviceScale;
import uk.chromis.pos.scanpal2.DeviceScanner;
import uk.chromis.pos.scanpal2.DeviceScannerFactory;
import uk.chromis.pos.util.AltEncrypter;

public class JAdminApp extends JPanel implements AppView {

    private AppProperties m_props;
    private Session session;
    private DataLogicSystem m_dlSystem;

    private Properties m_propsdb = null;
    private String m_sActiveCashIndex;
    private int m_iActiveCashSequence;
    private Date m_dActiveCashDateStart;
    private Date m_dActiveCashDateEnd;
    private String m_sInventoryLocation;
    private StringBuilder inputtext;
    private DeviceScale m_Scale;
    private DeviceScanner m_Scanner;
    private DeviceTicket m_TP;
    private TicketParser m_TTP;
    private Map<String, BeanFactory> m_aBeanFactories;
    private JPrincipalAdminApp m_principalapp = null;
    private static HashMap<String, String> m_oldclasses; // This is for backwards compatibility purposes

    private String m_clock;
    private String m_date;
    private Connection con;
    private ResultSet rs;
    private Statement stmt;
    private String SQL;
    private String roles;
    private DatabaseMetaData md;
    private SimpleDateFormat formatter;
    private MessageInf msg;

    private String db_user;
    private String db_url;
    private String db_password;
    private JAdminFrame frame = null;

    static {
        m_oldclasses = new HashMap<>();
    }

    public JAdminApp() {
        db_user = (AppConfig.getInstance().getProperty("db.user"));
        db_url = (AppConfig.getInstance().getProperty("db.URL"));
        db_password = (AppConfig.getInstance().getProperty("db.password"));
        if (db_user != null && db_password != null && db_password.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
            db_password = cypher.decrypt(db_password.substring(6));
        }
        m_aBeanFactories = new HashMap<>();
        initComponents();
        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(30, 30));
    }

    public boolean initApp(AppProperties props, JAdminFrame frame) {
        this.frame = frame;
        return initApp(props);

    }

    public boolean initApp(AppProperties props) {

        m_props = props;

        // support for different component orientation languages.
        applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        // Database start
        try {
            session = AppViewConnection.createSession(m_props);
        } catch (BasicException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_DANGER, e.getMessage(), e));
            return false;
        }

        m_dlSystem = (DataLogicSystem) getBean("uk.chromis.pos.forms.DataLogicSystem");

        String sDBVersion = readDataBaseVersion();
        if (!AppConfig.getInstance().getBoolean("chromis.tickettype") && sDBVersion != null) {
            UpdateTicketType.updateTicketType();
        }

        // Clear the cash drawer table as required, by setting
        m_dlSystem.cleanCashDrawerTable();

        m_propsdb = m_dlSystem.getResourceAsProperties(AppConfig.getInstance().getHost() + "/properties");

        try {
            String sActiveCashIndex = m_propsdb.getProperty("activecash");
            Object[] valcash = sActiveCashIndex == null
                    ? null
                    : m_dlSystem.findActiveCash(sActiveCashIndex);
            if (valcash == null || !AppConfig.getInstance().getHost().equals(valcash[0])) {
                setActiveCash(UUID.randomUUID().toString(), m_dlSystem.getSequenceCash(AppConfig.getInstance().getHost()) + 1, new Date(), null);

                m_dlSystem.execInsertCash(
                        new Object[]{getActiveCashIndex(), AppConfig.getInstance().getHost(), getActiveCashSequence(), getActiveCashDateStart(), getActiveCashDateEnd()});
            } else {
                setActiveCash(sActiveCashIndex, (Integer) valcash[1], (Date) valcash[2], (Date) valcash[3]);
            }
        } catch (BasicException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.cannotclosecash"), e);
            msg.show(this);
            session.close();
            return false;
        }

        m_sInventoryLocation = m_propsdb.getProperty("location");
        if (m_sInventoryLocation
                == null) {
            m_sInventoryLocation = "0";
            m_propsdb.setProperty("location", m_sInventoryLocation);
            m_dlSystem.setResourceAsProperties(AppConfig.getInstance().getHost() + "/properties", m_propsdb);
        }

        m_TP = new DeviceTicket(this, m_props);
        m_TTP = new TicketParser(getDeviceTicket(), m_dlSystem);

        printerStart();

        m_Scale = new DeviceScale(this, m_props);

        m_Scanner = DeviceScannerFactory.createInstance(m_props);

        String sWareHouse;

        try {
            sWareHouse = m_dlSystem.findLocationName(m_sInventoryLocation);
        } catch (BasicException e) {
            sWareHouse = null; // no he encontrado el almacen principal
        }

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/fixedimages/chromisadmin.png")));

        showLogin();

        return true;

    }

    private String readDataBaseVersion() {
        try {
            return m_dlSystem.findVersion();
        } catch (BasicException ed) {
            return null;
        }
    }

    public String getDbVersion() {
        String sdbmanager = m_dlSystem.getDBVersion();
        if ("MySQL".equals(sdbmanager)) {
            return ("m");
        } else if ("PostgreSQL".equals(sdbmanager)) {
            return ("p");
        } else if ("Apache Derby".equals(sdbmanager)) {
            return ("d");
        } else if ("Derby".equals(sdbmanager)) {
            return ("d");
        } else {
            return ("x");
        }
    }

    public void tryToClose() {
        if (closeAppView()) {
            session.close();
            SwingUtilities.getWindowAncestor(this).dispose();
        }
    }

    @Override
    public Object getBean(String beanfactory) throws BeanFactoryException {
        beanfactory = mapNewClass(beanfactory);
        BeanFactory bf = m_aBeanFactories.get(beanfactory);
        if (bf == null) {
            if (beanfactory.startsWith("/")) {
                //Change the report string to use the database version
                beanfactory = beanfactory.replace("/uk/chromis/reports/", "/uk/chromis/reports/" + m_dlSystem.getDBVersion().toLowerCase() + "/");
                bf = new BeanFactoryScript(beanfactory);
            } else {
                try {
                    Class bfclass = Class.forName(beanfactory);

                    if (BeanFactory.class
                            .isAssignableFrom(bfclass)) {
                        bf = (BeanFactory) bfclass.newInstance();

                    } else {
                        Constructor constMyView = bfclass.getConstructor(new Class[]{AppView.class
                        });
                        Object bean = constMyView.newInstance(new Object[]{this});

                        bf = new BeanFactoryObj(bean);
                    }

                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
                    throw new BeanFactoryException(e);
                }
            }
            m_aBeanFactories.put(beanfactory, bf);
            if (bf instanceof BeanFactoryApp) {
                ((BeanFactoryApp) bf).init(this);
            }
        }
        return bf.getBean();
    }

    private static String mapNewClass(String classname) {
        String newclass = m_oldclasses.get(classname);
        return newclass == null
                ? classname
                : newclass;
    }

    private void printerStart() {
        String sresource = m_dlSystem.getResourceAsXML("Printer.Start");
        if (sresource == null) {
            m_TP.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
        } else {
            try {
                m_TTP.printTicket(sresource);
            } catch (TicketPrinterException eTP) {
                m_TP.getDeviceDisplay().writeVisor(AppLocal.APP_NAME, AppLocal.APP_VERSION);
            }
        }
    }

    private void listPeople() {
        try {
            jScrollPane1.getViewport().setView(null);
            JFlowPanel jPeople = new JFlowPanel();
            jPeople.applyComponentOrientation(getComponentOrientation());
            java.util.List people = m_dlSystem.listPeopleVisible();

            for (int i = 0; i < people.size(); i++) {

                AppUser user = (AppUser) people.get(i);
                JButton btn = new JButton(new AppUserAction(user));
                btn.applyComponentOrientation(getComponentOrientation());
                btn.setFocusPainted(false);
                btn.setFocusable(false);
                btn.setRequestFocusEnabled(false);
                btn.setMaximumSize(new Dimension(130, 60));
                btn.setPreferredSize(new Dimension(130, 60));
                btn.setMinimumSize(new Dimension(130, 60));
                btn.setHorizontalAlignment(SwingConstants.CENTER);
                btn.setHorizontalTextPosition(AbstractButton.CENTER);
                btn.setVerticalTextPosition(AbstractButton.BOTTOM);
                jPeople.add(btn);
            }
            jScrollPane1.getViewport().setView(jPeople);
        } catch (BasicException ee) {
        }
    }

    private class AppUserAction extends AbstractAction {

        private final AppUser m_actionuser;

        public AppUserAction(AppUser user) {
            m_actionuser = user;
            putValue(Action.SMALL_ICON, m_actionuser.getIcon());
            putValue(Action.NAME, m_actionuser.getName());
        }

        public AppUser getUser() {
            return m_actionuser;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            // String sPassword = m_actionuser.getPassword();
            if (m_actionuser.authenticate()) {
                openAppView(m_actionuser);
            } else {
                String sPassword = JPasswordDialog.showEditPassword(JAdminApp.this,
                        AppLocal.getIntString("Label.Password"),
                        m_actionuser.getName(),
                        m_actionuser.getIcon());
                if (sPassword != null) {
                    if (m_actionuser.authenticate(sPassword)) {
                        openAppView(m_actionuser);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                AppLocal.getIntString("message.BadPassword"),
                                "Password Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }
    }

    private void showView(String view) {
        CardLayout cl = (CardLayout) (m_jPanelContainer.getLayout());
        cl.show(m_jPanelContainer, view);
    }

    public JPrincipalAdminApp getPrincipalApp() {
        return m_principalapp;
    }

    public String getCurrentUser() {
        return m_principalapp.getUser().getName();
    }

    private void openAppView(AppUser user) {
        if (closeAppView()) {
            m_principalapp = new JPrincipalAdminApp(this, user);
            m_jPanelContainer.add(m_principalapp, "_" + m_principalapp.getUser().getId());
            showView("_" + m_principalapp.getUser().getId());
            m_principalapp.showTask("uk.chromis.pos.forms.BlankPanel");
            if (frame != null) {
                JAdminFrame newFrame = new JAdminFrame();
                newFrame = frame;
                newFrame.createMenu();
            }
        }
    }

    public void exitToLogin() {
        closeAppView();
        showLogin();
    }

    public boolean closeAppView() {
        if (m_principalapp == null) {
            return true;
        } else if (!m_principalapp.deactivate()) {
            return false;
        } else {
            m_jPanelContainer.remove(m_principalapp);
            m_principalapp = null;
            showLogin();
            if (frame != null) {
                JAdminFrame newFrame = new JAdminFrame();
                newFrame = frame;
                newFrame.createExitOnly();
            }

            return true;
        }
    }

    private void showLogin() {
        listPeople();
        showView("login");
        /*
        inputtext = new StringBuilder();
        m_txtKeys.setText(null);
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                m_txtKeys.requestFocus();
            }
        });
         */
    }

    private void processKey(char c) {

        if ((c == '\n') || (c == '?')) {

            AppUser user = null;
            try {
                user = m_dlSystem.findPeopleByCard(inputtext.toString());
            } catch (BasicException e) {
            }

            if (user == null) {
                // user not found
                JOptionPane.showMessageDialog(null,
                        AppLocal.getIntString("message.nocard"),
                        "User Card", JOptionPane.WARNING_MESSAGE);
            } else {
                openAppView(user);
            }

            inputtext = new StringBuilder();

        } else {
            inputtext.append(c);
        }
    }

    @Override
    public DeviceTicket getDeviceTicket() {
        return m_TP;
    }

    @Override
    public DeviceScale getDeviceScale() {
        return m_Scale;
    }

    @Override
    public DeviceScanner getDeviceScanner() {
        return m_Scanner;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public String getInventoryLocation() {
        return m_sInventoryLocation;
    }

    @Override
    public String getActiveCashIndex() {
        return m_sActiveCashIndex;
    }

    @Override
    public int getActiveCashSequence() {
        return m_iActiveCashSequence;
    }

    @Override
    public Date getActiveCashDateStart() {
        return m_dActiveCashDateStart;
    }

    @Override
    public Date getActiveCashDateEnd() {
        return m_dActiveCashDateEnd;
    }

    @Override
    public void setActiveCash(String sIndex, int iSeq, Date dStart, Date dEnd) {
        m_sActiveCashIndex = sIndex;
        m_iActiveCashSequence = iSeq;
        m_dActiveCashDateStart = dStart;
        m_dActiveCashDateEnd = dEnd;
        m_propsdb.setProperty("activecash", m_sActiveCashIndex);
        m_dlSystem.setResourceAsProperties(AppConfig.getInstance().getHost() + "/properties", m_propsdb);
    }

    @Override
    public AppProperties getProperties() {
        return m_props;
    }

    @Override
    public void waitCursorBegin() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    @Override
    public void waitCursorEnd() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public AppUserView getAppUserView() {
        return m_principalapp;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        m_jPanelContainer = new javax.swing.JPanel();
        m_jPanelLogin = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        setEnabled(false);
        setPreferredSize(new java.awt.Dimension(1024, 768));
        setLayout(new java.awt.BorderLayout());

        m_jPanelContainer.setLayout(new java.awt.CardLayout());

        jPanel4.setMinimumSize(new java.awt.Dimension(518, 177));
        jPanel4.setPreferredSize(new java.awt.Dimension(518, 177));
        jPanel4.setLayout(new java.awt.BorderLayout(50, 0));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel4.add(jScrollPane1, java.awt.BorderLayout.PAGE_START);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 784, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout m_jPanelLoginLayout = new org.jdesktop.layout.GroupLayout(m_jPanelLogin);
        m_jPanelLogin.setLayout(m_jPanelLoginLayout);
        m_jPanelLoginLayout.setHorizontalGroup(
            m_jPanelLoginLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(m_jPanelLoginLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 266, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        m_jPanelLoginLayout.setVerticalGroup(
            m_jPanelLoginLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(m_jPanelLoginLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
            .add(m_jPanelLoginLayout.createSequentialGroup()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 715, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );

        m_jPanelContainer.add(m_jPanelLogin, "login");

        add(m_jPanelContainer, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel m_jPanelContainer;
    private javax.swing.JPanel m_jPanelLogin;
    // End of variables declaration//GEN-END:variables
}
