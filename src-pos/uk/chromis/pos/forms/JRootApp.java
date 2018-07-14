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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import uk.chromis.basic.BasicException;
import uk.chromis.beans.JFlowPanel;
import uk.chromis.beans.JPasswordDialog;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.loader.Session;
import uk.chromis.format.Formats;
import uk.chromis.pos.dialogs.JOpenWarningDlg;
import uk.chromis.pos.printer.DeviceTicket;
import uk.chromis.pos.printer.TicketParser;
import uk.chromis.pos.printer.TicketPrinterException;
import uk.chromis.pos.scale.DeviceScale;
import uk.chromis.pos.scanpal2.DeviceScanner;
import uk.chromis.pos.scanpal2.DeviceScannerFactory;
import uk.chromis.pos.sync.DataLogicSync;
//import uk.chromis.pos.sync.Sync;
import uk.chromis.pos.util.AltEncrypter;
import uk.chromis.pos.util.OSValidator;

public class JRootApp extends JPanel implements AppView {

    private AppProperties m_props;
    private Session session;
    private DataLogicSystem m_dlSystem;
    private DataLogicSync m_dlSync;

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
    private JPrincipalApp m_principalapp = null;
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

    static {
        m_oldclasses = new HashMap<>();
    }

    private class PrintTimeAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            m_clock = getLineTimer();
            m_date = getLineDate();
            m_jLblTitle.setText(m_dlSystem.getResourceAsText("Window.Title"));
            jLabel2.setText("  " + m_date + "  " + m_clock);
        }
    }

    private String getLineTimer() {
        return Formats.TIME.formatValue(new Date());
    }

    private String getLineDate() {
        return Formats.DATE.formatValue(new Date());
    }

    /**
     * Creates new form JRootApp
     */
    public JRootApp() {
        // get some default settings 
        db_user = (AppConfig.getInstance().getProperty("db.user"));
        db_url = (AppConfig.getInstance().getProperty("db.URL"));
        db_password = (AppConfig.getInstance().getProperty("db.password"));

        if (db_user != null && db_password != null && db_password.startsWith("crypt:")) {
            // the password is encrypted
            AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
            db_password = cypher.decrypt(db_password.substring(6));
        }

        m_aBeanFactories = new HashMap<>();
        // Inicializo los componentes visuales
        initComponents();
        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(30, 30));
    }

    public static final int INIT_SUCCESS = 0;
    public static final int INIT_FAIL_CONFIG = 1;
    public static final int INIT_FAIL_EXIT = 2;
    public static final int INIT_FAIL_RETRY = 3;

    /**
     *
     * @param props
     * @return
     */
    public int initApp(AppProperties props) {

        m_props = props;
        m_jPanelDown.setVisible(AppConfig.getInstance().getBoolean("till.hideinfo"));

        // support for different component orientation languages.
        applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        try {
            session = AppViewConnection.createSession(m_props);
        } catch (BasicException e) {
        }

        m_dlSystem = (DataLogicSystem) getBean("uk.chromis.pos.forms.DataLogicSystem");
        m_dlSync = (DataLogicSync) getBean("uk.chromis.pos.sync.DataLogicSync");

        String sDBVersion = readDataBaseVersion();
        if (!AppConfig.getInstance().getBoolean("chromis.tickettype") && sDBVersion != null) {
            UpdateTicketType.updateTicketType();
        }

        // Clear the cash drawer table as required, by setting
        m_dlSystem.cleanCashDrawerTable();

        m_propsdb = m_dlSystem.getResourceAsProperties(AppConfig.getInstance().getHost() + "/properties");
        if (!m_dlSync.isCentral()) {
            try {
                String sActiveCashIndex = m_propsdb.getProperty("activecash");
                Object[] valcash = sActiveCashIndex == null
                        ? null
                        : m_dlSystem.findActiveCash(sActiveCashIndex);
                if (valcash == null || !AppConfig.getInstance().getHost().equals(valcash[0])) {
                    setActiveCash(UUID.randomUUID().toString(), m_dlSystem.getSequenceCash(AppConfig.getInstance().getHost()), new Date(), null);
                    m_dlSystem.execInsertCash(
                            new Object[]{getActiveCashIndex(), AppConfig.getInstance().getHost(), getActiveCashSequence(), getActiveCashDateStart(), getActiveCashDateEnd(), 0});
                } else {
                    setActiveCash(sActiveCashIndex, (Integer) valcash[1], (Date) valcash[2], (Date) valcash[3]);
                }
            } catch (BasicException e) {
                session.close();
                JOpenWarningDlg wDlg = new JOpenWarningDlg(e.getMessage(), AppLocal.getIntString("message.retryorconfig"), false, true);
                wDlg.setModal(true);
                wDlg.setVisible(true);
                return JOpenWarningDlg.CHOICE;
            }
        }

        m_sInventoryLocation = m_propsdb.getProperty("location");
        if (m_sInventoryLocation
                == null) {
            m_sInventoryLocation = "0";
            m_propsdb.setProperty("location", m_sInventoryLocation);
            m_dlSystem.setResourceAsProperties(AppConfig.getInstance().getHost() + "/properties", m_propsdb);
        }

        // setup the display
        m_TP = new DeviceTicket(this, m_props);

        // Inicializamos 
        m_TTP = new TicketParser(getDeviceTicket(), m_dlSystem);

        printerStart();

        // Inicializamos la bascula
        m_Scale = new DeviceScale(this, m_props);

        // Inicializamos la scanpal
        m_Scanner = DeviceScannerFactory.createInstance(m_props);

        new javax.swing.Timer(
                250, new PrintTimeAction()).start();

        String sWareHouse;

        try {
            sWareHouse = m_dlSystem.findLocationName(m_sInventoryLocation);
        } catch (BasicException e) {
            sWareHouse = null; 
        }

        // Show Hostname, Warehouse and URL in taskbar
        String url;

        try {
            url = session.getURL();
        } catch (SQLException e) {
            url = "";
        }

        m_jHost.setText(
                "<html>" + AppConfig.getInstance().getHost() + " - " + sWareHouse + "<br>" + url);

        // display the new logo if set
        String newLogo = AppConfig.getInstance().getProperty("start.logo");
        if (newLogo
                != null) {
            if ("".equals(newLogo)) {
                jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/fixedimages/chromis.png")));
            } else {
                jLabel1.setIcon(new javax.swing.ImageIcon(newLogo));
            }
        }

        // change text under logo
        String newText = AppConfig.getInstance().getProperty("start.text");
        if (newText != null) {
            if (newText.equals("")) {
                jLabel1.setText("<html><center>Chromis POS - The New Face of Open Source POS<br>"
                        + "Copyright \u00A9 (c) 2015 - 2018 Chromis <br>"
                        + "<br>"
                        + "http://www.chromis.co.uk<br>"
                        + "<br>"
                        + " Chromis POS is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.<br>"
                        + "<br>"
                        + " Chromis POS is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.<br>"
                        + "<br>"
                        + "You should have received a copy of the GNU General Public License along with Chromis POS.  If not, see http://www.gnu.org/licenses/<br>"
                        + "</center>");
            } else {
                try {
                    String newTextCode = new Scanner(new File(newText), "UTF-8").useDelimiter("\\A").next();
                    jLabel1.setText(newTextCode);
                } catch (Exception e) {
                    System.out.println("");
                }

                jLabel1.setAlignmentX(0.5F);
                jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                jLabel1.setMaximumSize(new java.awt.Dimension(800, 1024));
                jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            }
        }

        showLogin();

        return INIT_SUCCESS;
    }

    private class doWork implements Runnable {

        @Override
        public void run() {

        }
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
            // success. continue with the shut down
            m_TP.getDeviceDisplay().clearVisor();
            session.close();
            // Download Root form
            SwingUtilities.getWindowAncestor(this).dispose();
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

    /**
     *
     * @return
     */
    @Override
    public Date getActiveCashDateEnd() {
        return m_dActiveCashDateEnd;
    }

    /**
     *
     * @param sIndex
     * @param iSeq
     * @param dStart
     * @param dEnd
     */
    @Override
    public void setActiveCash(String sIndex, int iSeq, Date dStart, Date dEnd) {
        m_sActiveCashIndex = sIndex;
        m_iActiveCashSequence = iSeq;
        m_dActiveCashDateStart = dStart;
        m_dActiveCashDateEnd = dEnd;
        m_propsdb.setProperty("activecash", m_sActiveCashIndex);
        m_dlSystem.setResourceAsProperties(AppConfig.getInstance().getHost() + "/properties", m_propsdb);
    }

    /**
     *
     * @return
     */
    @Override
    public AppProperties getProperties() {
        return m_props;
    }

    /**
     *
     * @param beanfactory
     * @return
     * @throws BeanFactoryException
     */
    @Override
    public Object getBean(String beanfactory) throws BeanFactoryException
  {
    beanfactory = mapNewClass(beanfactory);
    BeanFactory bf = (BeanFactory)this.m_aBeanFactories.get(beanfactory);
    if (bf == null)
    {
      if (beanfactory.startsWith("/"))
      {
        beanfactory = beanfactory.replace("/uk/chromis/reports/", "/uk/chromis/reports/" + this.m_dlSystem.getDBVersion().toLowerCase() + "/");
        bf = new BeanFactoryScript(beanfactory);
      }
      else
      {
        try
        {
          Class bfclass = Class.forName(beanfactory);
          if (BeanFactory.class.isAssignableFrom(bfclass))
          {
            bf = (BeanFactory)bfclass.newInstance();
          }
          else
          {
            Constructor constMyView = bfclass.getConstructor(new Class[] { AppView.class });
            
            Object bean = constMyView.newInstance(new Object[] { this });
            bf = new BeanFactoryObj(bean);
          }
        }
        catch (ClassNotFoundException|InstantiationException|IllegalAccessException|NoSuchMethodException|SecurityException|IllegalArgumentException|InvocationTargetException e)
        {
          throw new BeanFactoryException(e);
        }
      }
      this.m_aBeanFactories.put(beanfactory, bf);
      if ((bf instanceof BeanFactoryApp)) {
        ((BeanFactoryApp)bf).init(this);
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

    /**
     *
     */
    @Override
    public void waitCursorBegin() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    /**
     *
     */
    @Override
    public void waitCursorEnd() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     *
     * @return
     */
    @Override
    public AppUserView getAppUserView() {
        return m_principalapp;
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

    // La accion del selector
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
                // p'adentro directo, no tiene password        
                openAppView(m_actionuser);
            } else {
                // comprobemos la clave antes de entrar...
                String sPassword = JPasswordDialog.showEditPassword(JRootApp.this,
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

    private void openAppView(AppUser user) {

        if (closeAppView()) {

            m_principalapp = new JPrincipalApp(this, user);

            jPanel3.add(m_principalapp.getNotificator());
            jPanel3.revalidate();

            m_jPanelContainer.add(m_principalapp, "_" + m_principalapp.getUser().getId());
            showView("_" + m_principalapp.getUser().getId());

            m_principalapp.activate();
        }
    }

    /**
     *
     */
    public void exitToLogin() {
        closeAppView();
        showLogin();
    }

    /**
     *
     * @return
     */
    public boolean closeAppView() {

        if (m_principalapp == null) {
            return true;
        } else if (!m_principalapp.deactivate()) {
            return false;
        } else {
            // the status label
            jPanel3.remove(m_principalapp.getNotificator());
            jPanel3.revalidate();
            jPanel3.repaint();
            m_jPanelContainer.remove(m_principalapp);
            m_principalapp = null;

            showLogin();
            return true;
        }
    }

    private void showLogin() {

        // Show Login
        listPeople();
        showView("login");

        // show welcome message
        printerStart();

        // keyboard listener activation
        inputtext = new StringBuilder();
        m_txtKeys.setText(null);
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                m_txtKeys.requestFocus();
            }
        });
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jPanelTitle = new javax.swing.JPanel();
        m_jLblTitle = new javax.swing.JLabel();
        poweredby = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        m_jPanelContainer = new javax.swing.JPanel();
        m_jPanelLogin = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 0));
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        m_jLogonName = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        m_txtKeys = new javax.swing.JTextField();
        m_jClose = new javax.swing.JButton();
        m_jPanelDown = new javax.swing.JPanel();
        panelTask = new javax.swing.JPanel();
        m_jHost = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();

        setEnabled(false);
        setPreferredSize(new java.awt.Dimension(1024, 768));
        setLayout(new java.awt.BorderLayout());

        m_jPanelTitle.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        m_jPanelTitle.setLayout(new java.awt.BorderLayout());

        m_jLblTitle.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        m_jLblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        m_jLblTitle.setText("Window.Title");
        m_jPanelTitle.add(m_jLblTitle, java.awt.BorderLayout.CENTER);

        poweredby.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        poweredby.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/fixedimages/poweredby.png"))); // NOI18N
        poweredby.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        poweredby.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        poweredby.setMaximumSize(new java.awt.Dimension(222, 34));
        poweredby.setPreferredSize(new java.awt.Dimension(180, 34));
        poweredby.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                poweredbyMouseClicked(evt);
            }
        });
        m_jPanelTitle.add(poweredby, java.awt.BorderLayout.LINE_END);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setPreferredSize(new java.awt.Dimension(280, 34));
        jLabel2.setRequestFocusEnabled(false);
        m_jPanelTitle.add(jLabel2, java.awt.BorderLayout.LINE_START);

        add(m_jPanelTitle, java.awt.BorderLayout.NORTH);

        m_jPanelContainer.setLayout(new java.awt.CardLayout());

        m_jPanelLogin.setLayout(new java.awt.BorderLayout());

        jPanel4.setMinimumSize(new java.awt.Dimension(518, 177));
        jPanel4.setPreferredSize(new java.awt.Dimension(518, 177));
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));
        jPanel4.add(filler2);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/fixedimages/chromis.png"))); // NOI18N
        jLabel1.setText("<html><center>Chromis POS - The New Face of open source POS<br>" +
            "Copyright \u00A9 2015 - 2018 Chromis <br>" +
            "http://www.chromis.co.uk<br>" +
            "<br>" +
            "Chromis POS is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.<br>" +
            "<br>" +
            "Chromis POS is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.<br>" +
            "<br>" +
            "You should have received a copy of the GNU General Public License along with Chromis POS.  If not, see http://www.gnu.org/licenses/<br>" +
            "</center>");
        jLabel1.setAlignmentX(0.5F);
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setMaximumSize(new java.awt.Dimension(800, 1024));
        jLabel1.setMinimumSize(new java.awt.Dimension(518, 177));
        jLabel1.setPreferredSize(new java.awt.Dimension(518, 177));
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel4.add(jLabel1);

        m_jPanelLogin.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel5.setPreferredSize(new java.awt.Dimension(300, 400));

        m_jLogonName.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        m_jLogonName.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 100));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel8.setLayout(new java.awt.GridLayout(0, 1, 5, 5));
        jPanel2.add(jPanel8, java.awt.BorderLayout.NORTH);

        m_jLogonName.add(jPanel2, java.awt.BorderLayout.LINE_END);

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        m_txtKeys.setPreferredSize(new java.awt.Dimension(0, 0));
        m_txtKeys.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_txtKeysKeyTyped(evt);
            }
        });

        m_jClose.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/exit.png"))); // NOI18N
        m_jClose.setText(AppLocal.getIntString("Button.Close")); // NOI18N
        m_jClose.setFocusPainted(false);
        m_jClose.setFocusable(false);
        m_jClose.setPreferredSize(new java.awt.Dimension(100, 50));
        m_jClose.setRequestFocusEnabled(false);
        m_jClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jCloseActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(m_txtKeys, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(m_jClose, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 289, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(m_txtKeys, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(m_jClose, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jScrollPane1))
                .add(104, 104, 104)
                .add(m_jLogonName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(15, 15, 15)
                .add(m_jLogonName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(434, 565, Short.MAX_VALUE))
            .add(jPanel5Layout.createSequentialGroup()
                .add(jScrollPane1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        m_jPanelLogin.add(jPanel5, java.awt.BorderLayout.EAST);

        m_jPanelContainer.add(m_jPanelLogin, "login");

        add(m_jPanelContainer, java.awt.BorderLayout.CENTER);

        m_jPanelDown.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")));
        m_jPanelDown.setLayout(new java.awt.BorderLayout());

        m_jHost.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        m_jHost.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/display.png"))); // NOI18N
        m_jHost.setText("*Hostname");
        panelTask.add(m_jHost);

        m_jPanelDown.add(panelTask, java.awt.BorderLayout.LINE_START);
        m_jPanelDown.add(jPanel3, java.awt.BorderLayout.LINE_END);

        add(m_jPanelDown, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents


    private void m_jCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jCloseActionPerformed

        tryToClose();

    }//GEN-LAST:event_m_jCloseActionPerformed

    private void m_txtKeysKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_txtKeysKeyTyped

        if (evt.getModifiers() != 0) {
            String keys = evt.getKeyModifiersText(evt.getModifiers()) + "+" + evt.getKeyChar();
            if ((keys.equals("Alt+Shift+P")) || (keys.equals("Alt+Shift+p"))) {
                superUserLogin();
            }
        }
        m_txtKeys.setText("0");
        processKey(evt.getKeyChar());

    }//GEN-LAST:event_m_txtKeysKeyTyped

    private void superUserLogin() {
        //lets check if the super user exists
        AppUser user = null;
        try {
            user = m_dlSystem.getsuperuser();
            if (user == null) {
                ClassLoader cloader = new URLClassLoader(new URL[]{new File(AppConfig.getInstance().getProperty("db.driverlib")).toURI().toURL()});
                DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getInstance().getProperty("db.driver"), true, cloader).newInstance()));
                Class.forName(AppConfig.getInstance().getProperty("db.driver"));
                con = DriverManager.getConnection(db_url, db_user, db_password);
                PreparedStatement stmt = con.prepareStatement("INSERT INTO PEOPLE (ID, NAME, ROLE, VISIBLE) VALUES ('99', 'SuperAdminUser', '0', true)");
                stmt.executeUpdate();
                user = m_dlSystem.getsuperuser();

            }
        } catch (BasicException e) {
        } catch (SQLException | MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(JRootApp.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        openAppView(user);
    }


    private void poweredbyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_poweredbyMouseClicked

        JFrame sampleFrame = new JFrame();
        final Action exit = new AbstractAction("Exit") {
            @Override
            public final void actionPerformed(final ActionEvent e) {
                sampleFrame.setVisible(false);
                sampleFrame.dispose();
            }
        };

        String currentPath = null;

        if (OSValidator.isMac()) {
            try {
                currentPath = new File(JRootApp.class
                        .getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).toString();
            } catch (URISyntaxException ex) {
            }
        } else {
            currentPath = System.getProperty("user.dir") + "\\chromispos.jar";
        }

        String md5 = null;
        try {
            FileInputStream fis = new FileInputStream(new File(currentPath));
            md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
            fis.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JRootApp.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(JRootApp.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        int mb = 1024 * 1024;
        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();
        /*
        System.out.println("##### Heap utilization statistics [MB] #####");         
        //Print used memory
        System.out.println("Used Memory:"
            + (runtime.totalMemory() - runtime.freeMemory()) / mb); 
        //Print free memory
        System.out.println("Free Memory:"
            + runtime.freeMemory() / mb);         
        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb); 
        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);
         */
        AboutDialog dialog = new AboutDialog();
        JPanel dialogPanel = new JPanel();
        MigLayout layout = new MigLayout("", "[fill]");
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        model.addColumn("Details");
        model.addColumn("Value");
        model.addRow(new Object[]{"Database Version", readDataBaseVersion()});
        model.addRow(new Object[]{"Java Version", System.getProperty("java.version")});
        model.addRow(new Object[]{"Jar MD5", md5});
        model.addRow(new Object[]{"Operating System", System.getProperty("os.name")});
       // model.addRow(new Object[]{"Sync library", Sync.getVersion()});
        model.addRow(new Object[]{"Memory Used", ((runtime.totalMemory() - runtime.freeMemory()) / mb) + " MB"});
        model.addRow(new Object[]{"Total Memory Allocated", (runtime.totalMemory() / mb) + " MB"});
        model.addRow(new Object[]{"Max. Memory Available", (runtime.maxMemory() / mb) + " MB"});
        model.addRow(new Object[]{"Thread Count", java.lang.management.ManagementFactory.getThreadMXBean().getThreadCount()});
        model.addRow(new Object[]{"Available Processors", runtime.availableProcessors()});

        JScrollPane scrollPane = new JScrollPane(table);
        JPanel mainPanel = new JPanel(layout);
        JLabel label = new JLabel();
        JPanel btnPanel = new JPanel();
        dialogPanel.add(dialog);
        mainPanel.add(dialogPanel, "wrap");
        mainPanel.add(scrollPane, "wrap");
        JButton btnExit = new JButton(exit);
        btnPanel.add(btnExit, "width 100!");
        mainPanel.add(btnPanel, "right, wrap");
        mainPanel.add(new JLabel(), "wrap");
        sampleFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        sampleFrame.setPreferredSize(new Dimension(500, 400));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        sampleFrame.setLocation(dim.width / 2 - 250, dim.height / 2 - 200);
        sampleFrame.setUndecorated(true);
        mainPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        sampleFrame.add(mainPanel);
        sampleFrame.pack();
        sampleFrame.setVisible(true);

    }//GEN-LAST:event_poweredbyMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton m_jClose;
    private javax.swing.JLabel m_jHost;
    private javax.swing.JLabel m_jLblTitle;
    private javax.swing.JPanel m_jLogonName;
    private javax.swing.JPanel m_jPanelContainer;
    private javax.swing.JPanel m_jPanelDown;
    private javax.swing.JPanel m_jPanelLogin;
    private javax.swing.JPanel m_jPanelTitle;
    private javax.swing.JTextField m_txtKeys;
    private javax.swing.JPanel panelTask;
    private javax.swing.JLabel poweredby;
    // End of variables declaration//GEN-END:variables
}
