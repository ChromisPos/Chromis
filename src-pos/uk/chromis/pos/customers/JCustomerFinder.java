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


package uk.chromis.pos.customers;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.util.ArrayList;
import javax.swing.JFrame;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.QBFCompareEnum;
import uk.chromis.data.user.EditorCreator;
import uk.chromis.data.user.ListProvider;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.pos.forms.AppLocal;

/**
 *
 * @author adrianromero
 */
public class JCustomerFinder extends javax.swing.JDialog implements EditorCreator {

    private CustomerInfo selectedCustomer;
    private ListProvider lpr;
    private String siteGuid;

    private JCustomerFinder(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }

    /**
     * Creates new form JCustomerFinder
     */
    private JCustomerFinder(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
    }

    public static JCustomerFinder getCustomerFinder(Component parent, DataLogicCustomers dlCustomers) {        
        Window window = getWindow(parent);

        JCustomerFinder myMsg;
        if (window instanceof Frame) {
            myMsg = new JCustomerFinder((Frame) window, true);
        } else {
            myMsg = new JCustomerFinder((Dialog) window, true);
        }
        myMsg.init(dlCustomers, null);
        myMsg.applyComponentOrientation(parent.getComponentOrientation());
        return myMsg;
    }

    public static JCustomerFinder getCustomerFinder(Component parent, DataLogicCustomers dlCustomers, String siteGuid) {
        Window window = getWindow(parent);

        JCustomerFinder myMsg;
        if (window instanceof Frame) {
            myMsg = new JCustomerFinder((Frame) window, true);
        } else {
            myMsg = new JCustomerFinder((Dialog) window, true);
        }
        myMsg.init(dlCustomers, siteGuid);
        myMsg.applyComponentOrientation(parent.getComponentOrientation());
        return myMsg;
    }

    public CustomerInfo getSelectedCustomer() {
        return selectedCustomer;
    }

    private void init(DataLogicCustomers dlCustomers, String siteGuid) {

        initComponents();

        jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));

        m_jtxtTaxID.addEditorKeys(m_jKeys);
        m_jtxtSearchKey.addEditorKeys(m_jKeys);
        m_jtxtName.addEditorKeys(m_jKeys);
        m_jtxtPostal.addEditorKeys(m_jKeys);
        m_jtxtPhone.addEditorKeys(m_jKeys);
        m_jtxtEmail.addEditorKeys(m_jKeys);

        m_jtxtTaxID.reset();
        m_jtxtSearchKey.reset();
        m_jtxtName.reset();
        m_jtxtPostal.reset();
        m_jtxtPhone.reset();
        m_jtxtEmail.reset();

        m_jtxtTaxID.activate();

        lpr = new ListProviderCreator(dlCustomers.getCustomerList(siteGuid), this);

        jListCustomers.setCellRenderer(new CustomerRenderer());

        getRootPane().setDefaultButton(jcmdOK);

        selectedCustomer = null;
    }

    public void search(CustomerInfo customer) {

        if (customer == null || customer.getName() == null || customer.getName().equals("")) {

            m_jtxtTaxID.reset();
            m_jtxtSearchKey.reset();
            m_jtxtName.reset();
            m_jtxtPostal.reset();
            m_jtxtPhone.reset();
            m_jtxtEmail.reset();

            m_jtxtTaxID.activate();

            cleanSearch();
        } else {

            m_jtxtTaxID.setText(customer.getTaxid());
            m_jtxtSearchKey.setText(customer.getSearchkey());
            m_jtxtName.setText(customer.getName());
            m_jtxtPostal.setText(customer.getPostal());
            m_jtxtPhone.setText(customer.getPhone());
            m_jtxtEmail.setText(customer.getEmail());

            m_jtxtTaxID.activate();

            executeSearch();
        }
    }

    private void cleanSearch() {
        jListCustomers.setModel(new MyListData(new ArrayList()));
    }

    public void executeSearch() {
        try {
            jListCustomers.setModel(new MyListData(lpr.loadData()));
            if (jListCustomers.getModel().getSize() > 0) {
                jListCustomers.setSelectedIndex(0);
            }
        } catch (BasicException e) {
        }
    }

    @Override
    public Object createValue() throws BasicException {

        Object[] afilter = new Object[12];

        // TaxID
        if (m_jtxtTaxID.getText() == null || m_jtxtTaxID.getText().equals("")) {
            afilter[0] = QBFCompareEnum.COMP_NONE;
            afilter[1] = null;
        } else {
            afilter[0] = QBFCompareEnum.COMP_RE;
            afilter[1] = "%" + m_jtxtTaxID.getText() + "%";
        }

        // SearchKey
        if (m_jtxtSearchKey.getText() == null || m_jtxtSearchKey.getText().equals("")) {
            afilter[2] = QBFCompareEnum.COMP_NONE;
            afilter[3] = null;
        } else {
            afilter[2] = QBFCompareEnum.COMP_RE;
            afilter[3] = "%" + m_jtxtSearchKey.getText() + "%";
        }

        // Name
        if (m_jtxtName.getText() == null || m_jtxtName.getText().equals("")) {
            afilter[4] = QBFCompareEnum.COMP_NONE;
            afilter[5] = null;
        } else {
            afilter[4] = QBFCompareEnum.COMP_RE;
            afilter[5] = "%" + m_jtxtName.getText() + "%";
        }

        // Postal
        if (m_jtxtPostal.getText() == null || m_jtxtPostal.getText().equals("")) {
            afilter[6] = QBFCompareEnum.COMP_NONE;
            afilter[7] = null;
        } else {
            afilter[6] = QBFCompareEnum.COMP_RE;
            afilter[7] = "%" + m_jtxtPostal.getText() + "%";
        }

        // Phone
        if (m_jtxtPhone.getText() == null || m_jtxtPhone.getText().equals("")) {
            afilter[8] = QBFCompareEnum.COMP_NONE;
            afilter[9] = null;
        } else {
            afilter[8] = QBFCompareEnum.COMP_RE;
            afilter[9] = "%" + m_jtxtPhone.getText() + "%";
        }

        // Email
        if (m_jtxtEmail.getText() == null || m_jtxtEmail.getText().equals("")) {
            afilter[10] = QBFCompareEnum.COMP_NONE;
            afilter[11] = null;
        } else {
            afilter[10] = QBFCompareEnum.COMP_RE;
            afilter[11] = "%" + m_jtxtEmail.getText() + "%";
        }

        return afilter;
    }

    private static Window getWindow(Component parent) {
        if (parent == null) {
            return new JFrame();
        } else if (parent instanceof Frame || parent instanceof Dialog) {
            return (Window) parent;
        } else {
            return getWindow(parent.getParent());
        }
    }

    private static class MyListData extends javax.swing.AbstractListModel {

        private final java.util.List m_data;

        public MyListData(java.util.List data) {
            m_data = data;
        }

        @Override
        public Object getElementAt(int index) {
            return m_data.get(index);
        }

        @Override
        public int getSize() {
            return m_data.size();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        m_jKeys = new uk.chromis.editor.JEditorKeys();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLblTaxID = new javax.swing.JLabel();
        m_jtxtTaxID = new uk.chromis.editor.JEditorString();
        jLblSearchKey = new javax.swing.JLabel();
        m_jtxtSearchKey = new uk.chromis.editor.JEditorString();
        jLblPostal = new javax.swing.JLabel();
        m_jtxtPostal = new uk.chromis.editor.JEditorString();
        jLblName = new javax.swing.JLabel();
        m_jtxtName = new uk.chromis.editor.JEditorString();
        jLblPhone = new javax.swing.JLabel();
        jLblEmail = new javax.swing.JLabel();
        m_jtxtEmail = new uk.chromis.editor.JEditorString();
        m_jtxtPhone = new uk.chromis.editor.JEditorString();
        jPanel6 = new javax.swing.JPanel();
        jcmdReset = new javax.swing.JButton();
        jcmdExecute = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListCustomers = new javax.swing.JList();
        jPanel8 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jcmdCancel = new javax.swing.JButton();
        jcmdOK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(AppLocal.getIntString("form.customertitle")); // NOI18N
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel2.add(m_jKeys, java.awt.BorderLayout.NORTH);

        getContentPane().add(jPanel2, java.awt.BorderLayout.LINE_END);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel5.setLayout(new java.awt.BorderLayout());

        jLblTaxID.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLblTaxID.setText(AppLocal.getIntString("label.taxid")); // NOI18N
        jLblTaxID.setMaximumSize(new java.awt.Dimension(120, 25));
        jLblTaxID.setMinimumSize(new java.awt.Dimension(120, 25));
        jLblTaxID.setPreferredSize(new java.awt.Dimension(120, 25));

        m_jtxtTaxID.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jtxtTaxID.setPreferredSize(new java.awt.Dimension(220, 25));

        jLblSearchKey.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLblSearchKey.setText(AppLocal.getIntString("label.searchkey")); // NOI18N
        jLblSearchKey.setMaximumSize(new java.awt.Dimension(120, 25));
        jLblSearchKey.setMinimumSize(new java.awt.Dimension(120, 25));
        jLblSearchKey.setPreferredSize(new java.awt.Dimension(120, 25));

        m_jtxtSearchKey.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jtxtSearchKey.setPreferredSize(new java.awt.Dimension(220, 25));

        jLblPostal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLblPostal.setText("Postal");
        jLblPostal.setMaximumSize(new java.awt.Dimension(120, 25));
        jLblPostal.setMinimumSize(new java.awt.Dimension(120, 25));
        jLblPostal.setPreferredSize(new java.awt.Dimension(120, 25));

        m_jtxtPostal.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jtxtPostal.setPreferredSize(new java.awt.Dimension(220, 25));

        jLblName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLblName.setText(AppLocal.getIntString("label.prodname")); // NOI18N
        jLblName.setMaximumSize(new java.awt.Dimension(120, 25));
        jLblName.setMinimumSize(new java.awt.Dimension(120, 25));
        jLblName.setPreferredSize(new java.awt.Dimension(120, 25));

        m_jtxtName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jtxtName.setPreferredSize(new java.awt.Dimension(220, 25));

        jLblPhone.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLblPhone.setText(bundle.getString("label.phone")); // NOI18N
        jLblPhone.setMaximumSize(new java.awt.Dimension(120, 25));
        jLblPhone.setPreferredSize(new java.awt.Dimension(120, 25));

        jLblEmail.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLblEmail.setText(bundle.getString("label.companyemail")); // NOI18N
        jLblEmail.setMaximumSize(new java.awt.Dimension(120, 25));
        jLblEmail.setMinimumSize(new java.awt.Dimension(120, 25));
        jLblEmail.setPreferredSize(new java.awt.Dimension(120, 25));

        m_jtxtEmail.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jtxtEmail.setPreferredSize(new java.awt.Dimension(220, 25));

        m_jtxtPhone.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jtxtPhone.setPreferredSize(new java.awt.Dimension(220, 25));

        jcmdReset.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jcmdReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/reload.png"))); // NOI18N
        jcmdReset.setText(bundle.getString("button.reset")); // NOI18N
        jcmdReset.setToolTipText(bundle.getString("tiptext.clearfilter")); // NOI18N
        jcmdReset.setActionCommand("Reset ");
        jcmdReset.setFocusable(false);
        jcmdReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcmdResetActionPerformed(evt);
            }
        });
        jPanel6.add(jcmdReset);
        jcmdReset.getAccessibleContext().setAccessibleDescription("");

        jcmdExecute.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jcmdExecute.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/ok.png"))); // NOI18N
        jcmdExecute.setText(AppLocal.getIntString("button.executefilter")); // NOI18N
        jcmdExecute.setToolTipText(bundle.getString("tiptext.executefilter")); // NOI18N
        jcmdExecute.setFocusPainted(false);
        jcmdExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcmdExecuteActionPerformed(evt);
            }
        });
        jPanel6.add(jcmdExecute);
        jcmdExecute.getAccessibleContext().setAccessibleDescription("");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLblPostal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLblTaxID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLblSearchKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLblPhone, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLblName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(m_jtxtSearchKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jtxtTaxID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jtxtPostal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jtxtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(m_jtxtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_jtxtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 35, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(m_jtxtTaxID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLblTaxID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLblSearchKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jtxtSearchKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLblPostal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jtxtPostal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jtxtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLblName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLblPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jtxtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jtxtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        m_jtxtName.getAccessibleContext().setAccessibleName("");

        jPanel5.add(jPanel7, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jListCustomers.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jListCustomers.setFocusable(false);
        jListCustomers.setRequestFocusEnabled(false);
        jListCustomers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListCustomersMouseClicked(evt);
            }
        });
        jListCustomers.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListCustomersValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jListCustomers);

        jPanel4.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel8.setLayout(new java.awt.BorderLayout());

        jcmdCancel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jcmdCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/cancel.png"))); // NOI18N
        jcmdCancel.setText(AppLocal.getIntString("Button.Cancel")); // NOI18N
        jcmdCancel.setFocusPainted(false);
        jcmdCancel.setFocusable(false);
        jcmdCancel.setMargin(new java.awt.Insets(8, 16, 8, 16));
        jcmdCancel.setRequestFocusEnabled(false);
        jcmdCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcmdCancelActionPerformed(evt);
            }
        });
        jPanel1.add(jcmdCancel);

        jcmdOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/ok.png"))); // NOI18N
        jcmdOK.setText(AppLocal.getIntString("Button.OK")); // NOI18N
        jcmdOK.setEnabled(false);
        jcmdOK.setFocusPainted(false);
        jcmdOK.setFocusable(false);
        jcmdOK.setMargin(new java.awt.Insets(8, 16, 8, 16));
        jcmdOK.setMaximumSize(new java.awt.Dimension(103, 44));
        jcmdOK.setMinimumSize(new java.awt.Dimension(103, 44));
        jcmdOK.setPreferredSize(new java.awt.Dimension(103, 44));
        jcmdOK.setRequestFocusEnabled(false);
        jcmdOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcmdOKActionPerformed(evt);
            }
        });
        jPanel1.add(jcmdOK);

        jPanel8.add(jPanel1, java.awt.BorderLayout.LINE_END);

        jPanel3.add(jPanel8, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        setSize(new java.awt.Dimension(613, 497));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    private void jcmdOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcmdOKActionPerformed

        selectedCustomer = (CustomerInfo) jListCustomers.getSelectedValue();
        dispose();

    }//GEN-LAST:event_jcmdOKActionPerformed

    private void jcmdCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcmdCancelActionPerformed

        dispose();

    }//GEN-LAST:event_jcmdCancelActionPerformed

    private void jcmdExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcmdExecuteActionPerformed

        executeSearch();

    }//GEN-LAST:event_jcmdExecuteActionPerformed

    private void jListCustomersValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListCustomersValueChanged

        jcmdOK.setEnabled(jListCustomers.getSelectedValue() != null);

    }//GEN-LAST:event_jListCustomersValueChanged

    private void jListCustomersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListCustomersMouseClicked

        if (evt.getClickCount() == 2) {
            selectedCustomer = (CustomerInfo) jListCustomers.getSelectedValue();
            dispose();
        }

    }//GEN-LAST:event_jListCustomersMouseClicked

private void jcmdResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcmdResetActionPerformed

    m_jtxtTaxID.reset();
    m_jtxtSearchKey.reset();
    m_jtxtName.reset();
    m_jtxtPostal.reset();
    m_jtxtPhone.reset();
    m_jtxtEmail.reset();

    m_jtxtTaxID.activate();

    cleanSearch();
}//GEN-LAST:event_jcmdResetActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLblEmail;
    private javax.swing.JLabel jLblName;
    private javax.swing.JLabel jLblPhone;
    private javax.swing.JLabel jLblPostal;
    private javax.swing.JLabel jLblSearchKey;
    private javax.swing.JLabel jLblTaxID;
    private javax.swing.JList jListCustomers;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jcmdCancel;
    private javax.swing.JButton jcmdExecute;
    private javax.swing.JButton jcmdOK;
    private javax.swing.JButton jcmdReset;
    private uk.chromis.editor.JEditorKeys m_jKeys;
    private uk.chromis.editor.JEditorString m_jtxtEmail;
    private uk.chromis.editor.JEditorString m_jtxtName;
    private uk.chromis.editor.JEditorString m_jtxtPhone;
    private uk.chromis.editor.JEditorString m_jtxtPostal;
    private uk.chromis.editor.JEditorString m_jtxtSearchKey;
    private uk.chromis.editor.JEditorString m_jtxtTaxID;
    // End of variables declaration//GEN-END:variables
}
