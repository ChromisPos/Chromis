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
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.JMessageDialog;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.pos.customers.CustomerInfo;
import uk.chromis.pos.scripting.ScriptEngine;
import uk.chromis.pos.scripting.ScriptException;
import uk.chromis.pos.scripting.ScriptFactory;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.util.Hashcypher;

/**
 *
 * @author adrianromero
 */
public class JPrincipalAdminApp extends javax.swing.JPanel implements AppUserView {

    private static final Logger logger = Logger.getLogger("uk.chromis.pos.forms.JPrincipalAdminApp");
    private static JAdminApp m_appview;
    private static AppUser m_appuser;
    private DataLogicSystem m_dlSystem;
    private JLabel m_principalnotificator;
    private JPanelView m_jLastView;
    private Action m_actionfirst;
    private Map<String, JPanelView> m_aPreparedViews; // Prepared views   
    private Map<String, JPanelView> m_aCreatedViews;

    //HS Updates
    private CustomerInfo customerInfo;
    private DataLogicSync m_dlSync;

    public JPrincipalAdminApp(JAdminApp appview, AppUser appuser) {

        m_appview = appview;
        m_appuser = appuser;

        m_dlSystem = (DataLogicSystem) m_appview.getBean("uk.chromis.pos.forms.DataLogicSystem");
        m_dlSync = (DataLogicSync) m_appview.getBean("uk.chromis.pos.sync.DataLogicSync");

        m_appuser.fillPermissions(m_dlSystem, m_dlSync);

        m_actionfirst = null;
        m_jLastView = null;

        m_aPreparedViews = new HashMap<>();
        m_aCreatedViews = new HashMap<>();

        initComponents();

        jPanel2.add(Box.createVerticalStrut(50), 0);

        applyComponentOrientation(appview.getComponentOrientation());

        m_principalnotificator = new JLabel();
        m_principalnotificator.applyComponentOrientation(getComponentOrientation());
        m_principalnotificator.setText(m_appuser.getName());
        m_principalnotificator.setIcon(m_appuser.getIcon());
        m_jPanelTitle.setVisible(false);
        m_jPanelContainer.add(new JPanel(), "<NULL>");
        showView("<NULL>");
    }

    public JPrincipalAdminApp getInstance() {
        return this;
    }

    private Component getScriptMenu(String menutext) throws ScriptException {

        ScriptMenu menu = new ScriptMenu();

        ScriptEngine eng = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);
        eng.put("menu", menu);
        eng.eval(menutext);
        return menu.getTaskPane();
    }

    public JAdminApp getAppView() {
        return this.m_appview;
    }

    public AppUser getAppUser() {
        return this.m_appuser;
    }

    public class ScriptMenu {

        private final JXTaskPaneContainer taskPane;

        private ScriptMenu() {
            taskPane = new JXTaskPaneContainer();
            taskPane.applyComponentOrientation(getComponentOrientation());
        }

        public ScriptGroup addGroup(String key) {
            ScriptGroup group = new ScriptGroup(key);
            taskPane.add(group.getTaskGroup());
            return group;
        }

        public JXTaskPaneContainer getTaskPane() {
            return taskPane;
        }
    }

    public class ScriptGroup {

        private final JXTaskPane taskGroup;

        private ScriptGroup(String key) {
            taskGroup = new JXTaskPane();
            taskGroup.applyComponentOrientation(getComponentOrientation());
            taskGroup.setFocusable(false);
            taskGroup.setRequestFocusEnabled(false);
            taskGroup.setTitle(AppLocal.getIntString(key));
            taskGroup.setVisible(false); // Only groups with sons are visible.
        }

        public void addPanel(String icon, String key, String classname) {
            addAction(new MenuPanelAction(m_appview, icon, key, classname));
        }

        public void addExecution(String icon, String key, String classname) {
            addAction(new MenuExecAction(m_appview, icon, key, classname));
        }

        public ScriptSubmenu addSubmenu(String icon, String key, String classname) {
            ScriptSubmenu submenu = new ScriptSubmenu(key);
            m_aPreparedViews.put(classname, new JPanelMenu(submenu.getMenuDefinition()));
            addAction(new MenuPanelAction(m_appview, icon, key, classname));
            return submenu;
        }

        private void addAction(Action act) {
            if (m_appuser.hasPermission((String) act.getValue(AppUserView.ACTION_TASKNAME))) {
                Component c = taskGroup.add(act);
                c.applyComponentOrientation(getComponentOrientation());
                c.setFocusable(false);
                taskGroup.setVisible(true);

                if (m_actionfirst == null) {
                    m_actionfirst = act;
                }
            }
        }

        public JXTaskPane getTaskGroup() {
            return taskGroup;
        }
    }

    public class ScriptSubmenu {

        private final MenuDefinition menudef;

        private ScriptSubmenu(String key) {
            menudef = new MenuDefinition(key);
        }

        public void addTitle(String key) {
            menudef.addMenuTitle(key);
        }

        public void addPanel(String icon, String key, String classname) {
            menudef.addMenuItem(new MenuPanelAction(m_appview, icon, key, classname));
        }

        public void addExecution(String icon, String key, String classname) {
            menudef.addMenuItem(new MenuExecAction(m_appview, icon, key, classname));
        }

        public ScriptSubmenu addSubmenu(String icon, String key, String classname) {
            ScriptSubmenu submenu = new ScriptSubmenu(key);
            m_aPreparedViews.put(classname, new JPanelMenu(submenu.getMenuDefinition()));
            menudef.addMenuItem(new MenuPanelAction(m_appview, icon, key, classname));
            return submenu;
        }

        public MenuDefinition getMenuDefinition() {
            return menudef;
        }
    }

    public JComponent getNotificator() {
        return m_principalnotificator;
    }

    public void activate() {
        if (m_actionfirst != null) {
            m_actionfirst.actionPerformed(null);
            m_actionfirst = null;
        }
    }

    public boolean deactivate() {
        if (m_jLastView == null) {
            return true;
        } else if (m_jLastView.deactivate()) {
            m_jLastView = null;
            showView("<NULL>");
            return true;
        } else {
            return false;
        }

    }

    /**
     *
     */
    public void exitToLogin() {
        m_appview.closeAppView();
    }

    public void changePassword() {
        String sNewPassword = Hashcypher.changePassword(JPrincipalAdminApp.this, m_appuser.getPassword());
        if (sNewPassword != null) {
            try {
                m_dlSystem.execChangePassword(new Object[]{sNewPassword, m_appuser.getId()});
                m_appuser.setPassword(sNewPassword);
            } catch (BasicException e) {
                JOptionPane.showMessageDialog(null,
                        AppLocal.getIntString("message.cannotchangepassword"),
                        "Password Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void showView(String sView) {
        CardLayout cl = (CardLayout) (m_jPanelContainer.getLayout());
        cl.show(m_jPanelContainer, sView);
    }

    @Override
    public AppUser getUser() {
        return m_appuser;
    }

    @Override
    public void showTask(String sTaskClass) {

        customerInfo = new CustomerInfo("");
        customerInfo.setName("");

        m_appview.waitCursorBegin();

        if (m_appuser.hasPermission(sTaskClass) || sTaskClass.equals("uk.chromis.pos.forms.BlankPanel") || sTaskClass.equals("uk.chromis.pos.sites.SitesPanel")) {

            JPanelView m_jMyView = (JPanelView) m_aCreatedViews.get(sTaskClass);

            if (m_jLastView == null || (m_jMyView != m_jLastView && m_jLastView.deactivate())) {

                if (m_jMyView == null) {

                    // Is the view prepared
                    m_jMyView = m_aPreparedViews.get(sTaskClass);
                    if (m_jMyView == null) {
                        // The view is not prepared. Try to get as a Bean...
                        try {
                            m_jMyView = (JPanelView) m_appview.getBean(sTaskClass);
                        } catch (BeanFactoryException e) {
                            m_jMyView = new JPanelNull(m_appview, e);
                        }
                    }

                    m_jMyView.getComponent().applyComponentOrientation(getComponentOrientation());
                    m_jPanelContainer.add(m_jMyView.getComponent(), sTaskClass);
                    m_aCreatedViews.put(sTaskClass, m_jMyView);
                }

                try {
                    m_jMyView.activate();
                } catch (BasicException e) {
                    JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.notactive"), e));
                }
                m_jLastView = m_jMyView;
                showView(sTaskClass);
                String sTitle = m_jMyView.getTitle();
                m_jPanelTitle.setVisible(sTitle != null);
                m_jTitle.setText(sTitle);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    AppLocal.getIntString("message.notpermissions") + " " + sTaskClass,
                    "Access Error", JOptionPane.WARNING_MESSAGE);
        }
        m_appview.waitCursorEnd();
    }

    @Override
    public void executeTask(String sTaskClass) {
        m_appview.waitCursorBegin();

        if (m_appuser.hasPermission(sTaskClass)) {
            try {
                ProcessAction myProcess = (ProcessAction) m_appview.getBean(sTaskClass);

                try {
                    MessageInf m = myProcess.execute();
                    if (m != null) {
                        JMessageDialog.showMessage(JPrincipalAdminApp.this, m);
                    }
                } catch (BasicException eb) {
                    JMessageDialog.showMessage(JPrincipalAdminApp.this, new MessageInf(eb));
                }
            } catch (BeanFactoryException e) {
                JOptionPane.showMessageDialog(null, e,
                        AppLocal.getIntString("Label.LoadError"),
                        JOptionPane.WARNING_MESSAGE);
            }
        } else {

            JOptionPane.showMessageDialog(null,
                    AppLocal.getIntString("message.notpermissions") + " " + sTaskClass,
                    "Access Error", JOptionPane.WARNING_MESSAGE);
        }
        m_appview.waitCursorEnd();
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
        m_jPanelLeft = new javax.swing.JScrollPane();
        m_jPanelRight = new javax.swing.JPanel();
        m_jPanelTitle = new javax.swing.JPanel();
        m_jTitle = new javax.swing.JLabel();
        m_jPanelContainer = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));
        jPanel1.add(jPanel2, java.awt.BorderLayout.LINE_END);
        jPanel1.add(m_jPanelLeft, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.LINE_START);

        m_jPanelRight.setLayout(new java.awt.BorderLayout());

        m_jPanelTitle.setLayout(new java.awt.BorderLayout());

        m_jTitle.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        m_jTitle.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.darkGray), javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        m_jPanelTitle.add(m_jTitle, java.awt.BorderLayout.NORTH);

        m_jPanelRight.add(m_jPanelTitle, java.awt.BorderLayout.NORTH);

        m_jPanelContainer.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jPanelContainer.setLayout(new java.awt.CardLayout());
        m_jPanelRight.add(m_jPanelContainer, java.awt.BorderLayout.CENTER);

        add(m_jPanelRight, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel m_jPanelContainer;
    private javax.swing.JScrollPane m_jPanelLeft;
    private javax.swing.JPanel m_jPanelRight;
    private javax.swing.JPanel m_jPanelTitle;
    private javax.swing.JLabel m_jTitle;
    // End of variables declaration//GEN-END:variables

}
