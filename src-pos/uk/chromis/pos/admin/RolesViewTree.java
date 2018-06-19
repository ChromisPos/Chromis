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

import eu.floraresearch.lablib.gui.checkboxtree.CheckboxTree;
import eu.floraresearch.lablib.gui.checkboxtree.DefaultCheckboxTreeCellRenderer;
import eu.floraresearch.lablib.gui.checkboxtree.TreeCheckingEvent;
import eu.floraresearch.lablib.gui.checkboxtree.TreeCheckingListener;
import eu.floraresearch.lablib.gui.checkboxtree.TreeCheckingModel;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.parsers.SAXParser;
import org.apache.commons.lang.StringUtils;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.util.SessionFactory;

/**
 *
 * @John Lewis - July 2014
 */
public final class RolesViewTree extends javax.swing.JPanel implements EditorRecord {

    private Object m_oId;
    private DefaultTreeModel model;
    private CheckboxTree uTree;
    private DefaultMutableTreeNode root;
    private DefaultMutableTreeNode node;
    private List branches;
    private final DataLogicAdmin m_dlAdmin;
    private SentenceList m_branches;
    private List permissions;
    private List userPermissions;
    private final DirtyManager passedDirty;
    private List<DBPermissionsInfo> dbPermissions;
    private HashMap<DefaultMutableTreeNode, String> descriptionMap;
    private HashMap<String, String> classMap;
    private HashMap<String, DefaultMutableTreeNode> nodePaths;
    private StringBuilder sent;
    private static SAXParser m_sp = null;
    private HashSet<String> m_apermissions = null;
    private String section;
    private String displayName;
    private String description;
    private TreeCheckingModel cm;
    private Boolean hasPermissions;
    private final AppView m_app;
    private String siteGuid;
    private DataLogicSystem dlSystem;
    private ArrayList<String> permParams;

    /**
     * Creates new form RolesEditor
     */
    public RolesViewTree(DataLogicAdmin dlAdmin, DirtyManager dirty, AppView app, String localGuid) {
        initComponents();
       
        dlSystem = new DataLogicSystem();
        dlSystem.init(SessionFactory.getInstance().getSession());
        siteGuid = localGuid;
        passedDirty = dirty;
        jRightsLevel.addChangeListener(dirty);
        m_dlAdmin = dlAdmin;
        m_app = app;

        m_jName.getDocument().addDocumentListener(dirty);
        refresh();
        writeValueEOF();

    }

    private void createTree() {
//Create the jtree            
        root = new DefaultMutableTreeNode();
        uTree = new CheckboxTree(root);
        root.setUserObject("All Permissions");
        uTree.getCheckingModel().setCheckingMode(TreeCheckingModel.CheckingMode.PROPAGATE_PRESERVING_CHECK);
        uTree.clearSelection();

        DefaultCheckboxTreeCellRenderer renderer = (DefaultCheckboxTreeCellRenderer) uTree.getCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);

// set up listeners
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = uTree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = uTree.getPathForLocation(e.getX(), e.getY());

                if (selPath != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
// If using Right to left Language change the way the check tree works    
                    if (!uTree.getComponentOrientation().isLeftToRight()) {
                        if (uTree.isPathChecked(new TreePath(node.getPath()))) {
                            uTree.removeCheckingPath(new TreePath(node.getPath()));
                        } else {
                            uTree.addCheckingPath(new TreePath(node.getPath()));
                        }
                    }
                    jPermissionDesc.setText(descriptionMap.get(node));
                }
            }
        };
        uTree.addMouseListener(ml);

// when this listener is fired changes state to dirty 
        uTree.addTreeCheckingListener(new TreeCheckingListener() {
            public void valueChanged(TreeCheckingEvent e) {
                passedDirty.setDirty(true);
            }
        }
        );

        try {
// Get list of all the permisions in the database
// and the list of sections
            dbPermissions = (List) m_dlAdmin.getAlldbPermissions(siteGuid);
            branches = m_dlAdmin.getSectionsList(siteGuid);
        } catch (BasicException ex) {
            Logger.getLogger(RolesViewTree.class.getName()).log(Level.SEVERE, null, ex);
        }

// Create the main branches in the tree
        for (Object branch : branches) {
            section = ((StringUtils.substring(branch.toString(), 0, 2)).equals("##")) ? AppLocal.getIntString(StringUtils.right(branch.toString(), branch.toString().length() - 2)) : branch.toString();
            root.add(new DefaultMutableTreeNode(section));
        }

        classMap = new HashMap();
        descriptionMap = new HashMap();
        nodePaths = new HashMap();
// Replace displayname, Section and Description 
// from the database with the correct details from the permissions locale        
        for (DBPermissionsInfo Perm : dbPermissions) {
            Perm.setDisplayName(((StringUtils.substring(Perm.getDisplayName(), 0, 2)).equals("##")) ? AppLocal.getIntString(StringUtils.right(Perm.getDisplayName(), Perm.getDisplayName().length() - 2)) : Perm.getDisplayName());
            Perm.setSection(((StringUtils.substring(Perm.getSection(), 0, 2)).equals("##")) ? AppLocal.getIntString(StringUtils.right(Perm.getSection(), Perm.getSection().length() - 2)) : Perm.getSection());
            Perm.setDescription(((StringUtils.substring(Perm.getDescription(), 0, 2)).equals("##")) ? AppLocal.getIntString(StringUtils.right(Perm.getDescription(), Perm.getDescription().length() - 2)) : Perm.getDescription());
        }
//put the list into order by display name
        sort();
// Create the leaf nodes & fill in hashmap's
        for (DBPermissionsInfo Perm : dbPermissions) {
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(Perm.getDisplayName(), false);
            if (searchNode(Perm.getSection(), root) != null) {
                searchNode(Perm.getSection(), root).add(newNode);
                classMap.put("[All Permissions, " + Perm.getSection() + ", " + newNode + "]", Perm.getClassName());
                descriptionMap.put(newNode, Perm.getDescription());
                nodePaths.put(Perm.getClassName(), newNode);
            }
        }
        root = sortTree(root);
        jScrollPane1.setViewportView(uTree);
        uTree.expandAll();
    }

    public HashSet<String> buildPermissionsStr() {
        HashSet<String> set = new HashSet<>();

        set.add("uk.chromis.pos.forms.JPanelMenu");
        set.add("Menu.Exit");

        TreePath[] treePaths = uTree.getCheckingPaths();
        hasPermissions = false;

        if (treePaths != null) {
            for (TreePath path : treePaths) {
                // we know any paths not 3 are not a leaf node        
                if (path.getPathCount() == 3) {
                  set.add(classMap.get(path.toString()));
                    hasPermissions = true;
                }
            }
        }
        return set;
    }

    
    public void loadUserPermissionsIntoTree() throws BasicException {
// Populate the tree with the selected users permissions
        uTree.clearChecking();
        if (!"".equals(m_jName.getText())) {
            getPermissions();
            m_apermissions.stream().filter((userPermission) -> (nodePaths.containsKey(userPermission))).forEachOrdered((userPermission) -> {
                uTree.addCheckingPath(new TreePath(nodePaths.get(userPermission).getPath()));
            });
            jRightsLevel.setValue(m_dlAdmin.getRightsLevel(m_jName.getText(), siteGuid));
        }
    }

// returns the node referenced by the nodestr    
    public DefaultMutableTreeNode searchNode(String nodeStr, DefaultMutableTreeNode tNode) {
        DefaultMutableTreeNode node = null;
        Enumeration e = tNode.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            node = (DefaultMutableTreeNode) e.nextElement();
            if (nodeStr.equals(node.getUserObject().toString())) {
                return node;
            }
        }
        return null;
    }

// Puts the tree into alpabetical order.    
    public static DefaultMutableTreeNode sortTree(DefaultMutableTreeNode root) {
        for (int i = 0; i < root.getChildCount() - 1; i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);
            String nt = node.getUserObject().toString();
            for (int j = i + 1; j <= root.getChildCount() - 1; j++) {
                DefaultMutableTreeNode prevNode = (DefaultMutableTreeNode) root.getChildAt(j);
                String np = prevNode.getUserObject().toString();
                if (nt.compareToIgnoreCase(np) > 0) {
                    root.insert(node, j);
                    root.insert(prevNode, i);
                }
            }
            if (node.getChildCount() > 0) {
                node = sortTree(node);
            }
        }
        return root;
    }

    // Sorts the tree ready for displaying
    private void sort() {
        for (int i = 0; i < dbPermissions.size(); i++) {
            for (int j = i; j > 0; j--) {
                if ((dbPermissions.get(j).getDisplayName()).compareTo(dbPermissions.get(j - 1).getDisplayName()) < 0) {
                    String tmpDisplayName = dbPermissions.get(j).getDisplayName();
                    String tmpClassname = dbPermissions.get(j).getClassName();
                    String tmpDescription = dbPermissions.get(j).getDescription();
                    String tmpSection = dbPermissions.get(j).getSection();

                    dbPermissions.get(j).setDisplayName(dbPermissions.get(j - 1).getDisplayName());
                    dbPermissions.get(j).setClassName(dbPermissions.get(j - 1).getClassName());
                    dbPermissions.get(j).setDescription(dbPermissions.get(j - 1).getDescription());
                    dbPermissions.get(j).setSection(dbPermissions.get(j - 1).getSection());

                    dbPermissions.get(j - 1).setDisplayName(tmpDisplayName);
                    dbPermissions.get(j - 1).setClassName(tmpClassname);
                    dbPermissions.get(j - 1).setDescription(tmpDescription);
                    dbPermissions.get(j - 1).setSection(tmpSection);
                }
            }
        }
    }

    /**
     *
     */
    @Override
    public void writeValueEOF() {
        m_oId = null;
        m_jName.setText(null);;
        m_jName.setEnabled(false);
        jRightsLevel.setValue(3);
    }

    /**
     *
     */
    @Override
    public void writeValueInsert() {
        uTree.clearChecking();
        m_oId = null;
        m_jName.setText(null);
        m_jName.setEnabled(true);
        jRightsLevel.setValue(3);
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueDelete(Object value) {
        Object[] role = (Object[]) value;
        m_oId = role[0];
        m_jName.setText(Formats.STRING.formatValue(role[1]));
        m_jName.setEnabled(false);
       
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueEdit(Object value) {
        Object[] role = (Object[]) value;
        m_oId = role[0];
        m_jName.setText(Formats.STRING.formatValue(role[1]));
        m_jName.setEnabled(true);
        try {
            loadUserPermissionsIntoTree();
        } catch (BasicException ex) {
        }
        siteGuid = role[4].toString();
    }

    /**
     *
     * @return @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {
        ArrayList<String> permParams = new ArrayList<>(buildPermissionsStr());
        Object[] role = new Object[5];
        role[0] = m_oId == null ? UUID.randomUUID().toString() : m_oId;
        role[1] = m_jName.getText();
        role[2] = permParams;
        role[3] = jRightsLevel.getValue();
        role[4] = siteGuid;

        if (!hasPermissions) {
            Object[] options = {AppLocal.getIntString("Button.Yes"), AppLocal.getIntString("Button.No")};
            if (JOptionPane.showOptionDialog(this,
                    AppLocal.getIntString("Message.adminpermissions1") + m_jName.getText() + " " + AppLocal.getIntString("Message.adminpermissions2"), AppLocal.getIntString("Message.adminwarning"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]) == 1) {
                role = new Object[5];
                role[0] = m_oId == null ? UUID.randomUUID().toString() : m_oId;
                role[1] = m_jName.getText();
                role[2] = permParams;
                role[3] = m_dlAdmin.getRightsLevel(m_jName.getText(), siteGuid);
                role[4] = siteGuid;
                // return role;
            }
        }
        return role;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void refresh() {
        createTree();
        uTree.scrollPathToVisible(uTree.getPathForRow(0));
        uTree.expandAll();
    }

    public void reloadTree(String siteGuid) {
        this.siteGuid = siteGuid;
        refresh();
    }

    public void getPermissions() throws BasicException {
        m_apermissions = new HashSet<>();
        try {
            String m_roles = m_dlAdmin.getRoleID(m_jName.getText(), siteGuid);
            m_apermissions = new HashSet<>();
            m_apermissions = dlSystem.getNewPermissions(m_roles);
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(RolesViewTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void refreshGuid(String siteGuid) {
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSpinner1 = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPermissionDesc = new javax.swing.JTextArea();
        jAddEntry = new javax.swing.JButton();
        jDeleteEntry = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jRightsLevel = new javax.swing.JSpinner();

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("Label.Name")); // NOI18N

        m_jName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPermissionDesc.setColumns(20);
        jPermissionDesc.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jPermissionDesc.setLineWrap(true);
        jPermissionDesc.setRows(5);
        jScrollPane2.setViewportView(jPermissionDesc);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jAddEntry.setText(bundle.getString("Button.addclass")); // NOI18N
        jAddEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAddEntryActionPerformed(evt);
            }
        });

        jDeleteEntry.setText(bundle.getString("Button.deleteclass")); // NOI18N
        jDeleteEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDeleteEntryActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText(bundle.getString("label.rightslevel")); // NOI18N

        jRightsLevel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jRightsLevel.setModel(new javax.swing.SpinnerNumberModel(3, 0, 9, 1));
        jRightsLevel.setMinimumSize(new java.awt.Dimension(35, 28));
        jRightsLevel.setPreferredSize(new java.awt.Dimension(35, 28));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jAddEntry)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDeleteEntry)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRightsLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 76, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jRightsLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jAddEntry)
                        .addComponent(jDeleteEntry)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jAddEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddEntryActionPerformed
        if (RolesAddNewEntry.showDialog(this, siteGuid)) {
            try {
                // lets reload the tree
                // With permissions from the selected site.
                dbPermissions = (List) m_dlAdmin.getAlldbPermissions(siteGuid);
            } catch (BasicException ex) {
            }

            uTree.setModel(null);
            refresh();
        }
    }//GEN-LAST:event_jAddEntryActionPerformed

    private void jDeleteEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDeleteEntryActionPerformed
        JPermissionsList permissions = JPermissionsList.getPermissionsList(this, m_app.getSession(), siteGuid);
        permissions.setVisible(true);
        refresh();
    }//GEN-LAST:event_jDeleteEntryActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jAddEntry;
    private javax.swing.JButton jDeleteEntry;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextArea jPermissionDesc;
    private javax.swing.JSpinner jRightsLevel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextField m_jName;
    // End of variables declaration//GEN-END:variables
}
