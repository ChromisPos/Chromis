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

import java.util.Date;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.BeanFactoryApp;
import uk.chromis.pos.forms.BeanFactoryException;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.forms.JPanelView;
import uk.chromis.pos.payment.JPaymentSelect;
import uk.chromis.pos.payment.JPaymentSelectCustomer;
import uk.chromis.pos.payment.PaymentInfo;
import uk.chromis.pos.payment.PaymentInfoTicket;
import uk.chromis.pos.printer.TicketParser;
import uk.chromis.pos.printer.TicketPrinterException;
import uk.chromis.pos.scripting.ScriptEngine;
import uk.chromis.pos.scripting.ScriptException;
import uk.chromis.pos.scripting.ScriptFactory;
import uk.chromis.pos.ticket.TicketInfo;
import uk.chromis.pos.ticket.TicketType;

/**
 *
 * @author  adrianromero
 */
public class CustomersPayment extends javax.swing.JPanel implements JPanelView, BeanFactoryApp {

    private AppView app;
    private DataLogicCustomers dlcustomers;
    private DataLogicSales dlsales;
    private DataLogicSystem dlsystem;
    private TicketParser ttp;    
    private JPaymentSelect paymentdialog;
    
    private CustomerInfoExt customerext;
    private final DirtyManager dirty;

    /** Creates new form CustomersPayment */
    public CustomersPayment() {

        initComponents();
        
        editorcard.addEditorKeys(m_jKeys);
        txtNotes.addEditorKeys(m_jKeys);

        dirty = new DirtyManager();
        txtNotes.addPropertyChangeListener("Text", dirty);
    }

    /**
     *
     * @param app
     * @throws BeanFactoryException
     */
    @Override
    public void init(AppView app) throws BeanFactoryException {

        this.app = app;
        dlcustomers = (DataLogicCustomers) app.getBean("uk.chromis.pos.customers.DataLogicCustomers");
        dlsales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");
        dlsystem = (DataLogicSystem) app.getBean("uk.chromis.pos.forms.DataLogicSystem");
        ttp = new TicketParser(app.getDeviceTicket(), dlsystem);
    }

    /**
     *
     * @return
     */
    @Override
    public Object getBean() {
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.CustomersPayment");
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {

        paymentdialog = JPaymentSelectCustomer.getDialog(this);        
        paymentdialog.init(app);

        resetCustomer();

        editorcard.reset();
        editorcard.activate();
    }

    /**
     *
     * @return
     */
    @Override
    public boolean deactivate() {
        if (dirty.isDirty()) {
            int res = JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.wannasave"), AppLocal.getIntString("title.editor"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                save();
                return true;
            } else {
                return res == JOptionPane.NO_OPTION;
            }
        } else {
            return true;
        }
    }

    /**
     *
     * @return
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    private void editCustomer(CustomerInfoExt customer) {

        customerext = customer;

        txtTaxId.setText(customer.getTaxid());
        txtName.setText(customer.getName());
        txtCard.setText(customer.getCard());
        txtNotes.reset();
        txtNotes.setText(customer.getNotes());
        txtMaxdebt.setText(Formats.CURRENCY.formatValue(customer.getMaxdebt()));
        txtCurdebt.setText(Formats.CURRENCY.formatValue(customer.getCurdebt()));
        txtCurdate.setText(Formats.DATE.formatValue(customer.getCurdate()));
        txtDiscount.setText(Formats.PERCENT.formatValue(customer.getDiscount()));

        txtNotes.setEnabled(true);

        dirty.setDirty(false);

        btnSave.setEnabled(true);    
        btnPay.setEnabled(true);
        
        btnPay.setEnabled(customer.getCurdebt() != null && customer.getCurdebt() > 0.0);
        
    }

    private void resetCustomer() {

        customerext = null;

        txtTaxId.setText(null);
        txtName.setText(null);
        txtCard.setText(null);
        txtNotes.reset();
        txtMaxdebt.setText(null);
        txtCurdebt.setText(null);
        txtCurdate.setText(null);
        txtDiscount.setText(null);
        
        txtNotes.setEnabled(false);

        dirty.setDirty(false);

        btnSave.setEnabled(false);
        btnPay.setEnabled(false);

    }

    private void readCustomer() {

        try {
            CustomerInfoExt customer = dlsales.findCustomerExt(editorcard.getText());
            if (customer == null) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"));
                msg.show(this);
            } else {
                editCustomer(customer);
            }

        } catch (BasicException ex) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"), ex);
            msg.show(this);
        }

        editorcard.reset();
        editorcard.activate();
    }

    private void save() {

        customerext.setNotes(txtNotes.getText());

        try {
            dlcustomers.updateCustomerExt(customerext);
            editCustomer(customerext);
        } catch (BasicException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.nosave"), e);
            msg.show(this);
        }

    }

    private void printTicket(String resname, TicketInfo ticket, CustomerInfoExt customer) {

        String resource = dlsystem.getResourceAsXML(resname);
        if (resource == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            msg.show(this);
        } else {
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("ticket", ticket);
                script.put("customer", customer);
                ttp.printTicket(script.eval(resource).toString());
            } catch (    ScriptException | TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        btnCustomer = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnPay = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        m_jKeys = new uk.chromis.editor.JEditorKeys();
        jPanel5 = new javax.swing.JPanel();
        editorcard = new uk.chromis.editor.JEditorString();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtCard = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtCurdebt = new javax.swing.JTextField();
        txtCurdate = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtMaxdebt = new javax.swing.JTextField();
        txtNotes = new uk.chromis.editor.JEditorString();
        txtTaxId = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtDiscount = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.BorderLayout());

        btnCustomer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/customer_sml.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        btnCustomer.setToolTipText(bundle.getString("tiptext.customeraccount")); // NOI18N
        btnCustomer.setFocusPainted(false);
        btnCustomer.setFocusable(false);
        btnCustomer.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnCustomer.setRequestFocusEnabled(false);
        btnCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomerActionPerformed(evt);
            }
        });
        jPanel6.add(btnCustomer);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/filesave.png"))); // NOI18N
        btnSave.setToolTipText(bundle.getString("tiptext.save")); // NOI18N
        btnSave.setFocusPainted(false);
        btnSave.setFocusable(false);
        btnSave.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnSave.setRequestFocusEnabled(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jPanel6.add(btnSave);
        jPanel6.add(jSeparator1);

        btnPay.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnPay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/pay.png"))); // NOI18N
        btnPay.setText(AppLocal.getIntString("button.pay")); // NOI18N
        btnPay.setToolTipText(bundle.getString("tiptext.payaccount")); // NOI18N
        btnPay.setFocusPainted(false);
        btnPay.setFocusable(false);
        btnPay.setMargin(new java.awt.Insets(8, 14, 8, 14));
        btnPay.setMaximumSize(new java.awt.Dimension(110, 44));
        btnPay.setMinimumSize(new java.awt.Dimension(110, 44));
        btnPay.setPreferredSize(new java.awt.Dimension(110, 44));
        btnPay.setRequestFocusEnabled(false);
        btnPay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayActionPerformed(evt);
            }
        });
        jPanel6.add(btnPay);

        jPanel2.add(jPanel6, java.awt.BorderLayout.LINE_START);

        add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));

        m_jKeys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jKeysActionPerformed(evt);
            }
        });
        jPanel4.add(m_jKeys);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel5.setLayout(new java.awt.GridBagLayout());
        jPanel5.add(editorcard, new java.awt.GridBagConstraints());

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/ok.png"))); // NOI18N
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.setMargin(new java.awt.Insets(8, 14, 8, 14));
        jButton1.setRequestFocusEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel5.add(jButton1, gridBagConstraints);

        jPanel4.add(jPanel5);

        jPanel3.add(jPanel4, java.awt.BorderLayout.NORTH);

        add(jPanel3, java.awt.BorderLayout.LINE_END);

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("label.name")); // NOI18N

        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText(AppLocal.getIntString("label.notes")); // NOI18N

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText(AppLocal.getIntString("label.card")); // NOI18N

        txtCard.setEditable(false);
        txtCard.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtCard.setFocusable(false);
        txtCard.setRequestFocusEnabled(false);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("label.maxdebt")); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("label.curdebt")); // NOI18N

        txtCurdebt.setEditable(false);
        txtCurdebt.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtCurdebt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCurdebt.setFocusable(false);
        txtCurdebt.setRequestFocusEnabled(false);

        txtCurdate.setEditable(false);
        txtCurdate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtCurdate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCurdate.setFocusable(false);
        txtCurdate.setRequestFocusEnabled(false);

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText(AppLocal.getIntString("label.curdate")); // NOI18N

        txtName.setEditable(false);
        txtName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtName.setFocusable(false);
        txtName.setRequestFocusEnabled(false);

        txtMaxdebt.setEditable(false);
        txtMaxdebt.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtMaxdebt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMaxdebt.setFocusable(false);
        txtMaxdebt.setRequestFocusEnabled(false);

        txtNotes.setEnabled(false);
        txtNotes.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txtTaxId.setEditable(false);
        txtTaxId.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtTaxId.setFocusable(false);
        txtTaxId.setRequestFocusEnabled(false);

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText(AppLocal.getIntString("label.taxid")); // NOI18N

        txtDiscount.setEditable(false);
        txtDiscount.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiscount.setFocusable(false);
        txtDiscount.setRequestFocusEnabled(false);

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("label.discount")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTaxId, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCard, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMaxdebt, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCurdebt, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCurdate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(81, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTaxId, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCard, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(txtNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMaxdebt, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCurdebt, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCurdate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(192, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        readCustomer();
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void m_jKeysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jKeysActionPerformed

        readCustomer();
        
    }//GEN-LAST:event_m_jKeysActionPerformed

    private void btnCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomerActionPerformed

        JCustomerFinder finder = JCustomerFinder.getCustomerFinder(this, dlcustomers);
        finder.search(null);
        finder.setVisible(true);
        CustomerInfo customer = finder.getSelectedCustomer();
        if (customer != null) {
            try {
                CustomerInfoExt c = dlsales.loadCustomerExt(customer.getId());
                if (c == null) {
                    MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"));
                    msg.show(this);
                } else {
                    editCustomer(c);
                }
            } catch (BasicException ex) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"), ex);
                msg.show(this);
            }
        }  
        editorcard.reset();
        editorcard.activate();
                
}//GEN-LAST:event_btnCustomerActionPerformed

    private void btnPayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayActionPerformed

        paymentdialog.setPrintSelected(true);
        
        if (paymentdialog.showDialog(customerext.getCurdebt(), null)) {

            // Save the ticket
            TicketInfo ticket = new TicketInfo();
          //  ticket.setTicketType(TicketInfo.RECEIPT_PAYMENT);
            ticket.setTicketType(TicketType.PAYMENT);

            List<PaymentInfo> payments = paymentdialog.getSelectedPayments();

            double total = 0.0;
            for (PaymentInfo p : payments) {
                total += p.getTotal();
            }

            payments.add(new PaymentInfoTicket(-total, "debtpaid"));

            ticket.setPayments(payments);

            ticket.setUser(app.getAppUserView().getUser().getUserInfo());
            ticket.setActiveCash(app.getActiveCashIndex());
            ticket.setDate(new Date());
            ticket.setCustomer(customerext);

            try {
                dlsales.saveTicket(ticket, app.getInventoryLocation());
            } catch (BasicException eData) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.nosaveticket"), eData);
                msg.show(this);
            }


            // reload customer
            CustomerInfoExt c;
            try {
                c = dlsales.loadCustomerExt(customerext.getId());
                if (c == null) {
                    MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"));
                    msg.show(this);
                } else {
                    editCustomer(c);
                }
            } catch (BasicException ex) {
                c = null;
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"), ex);
                msg.show(this);
            }

            printTicket(paymentdialog.isPrintSelected()
                    ? "Printer.CustomerPaid"
                    : "Printer.CustomerPaid2",
                    ticket, c);
        }
        
        editorcard.reset();
        editorcard.activate();
        
}//GEN-LAST:event_btnPayActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed

        if (dirty.isDirty()) {
            save();

            editorcard.reset();
            editorcard.activate();
        }
        
}//GEN-LAST:event_btnSaveActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCustomer;
    private javax.swing.JButton btnPay;
    private javax.swing.JButton btnSave;
    private uk.chromis.editor.JEditorString editorcard;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSeparator jSeparator1;
    private uk.chromis.editor.JEditorKeys m_jKeys;
    private javax.swing.JTextField txtCard;
    private javax.swing.JTextField txtCurdate;
    private javax.swing.JTextField txtCurdebt;
    private javax.swing.JTextField txtDiscount;
    private javax.swing.JTextField txtMaxdebt;
    private javax.swing.JTextField txtName;
    private uk.chromis.editor.JEditorString txtNotes;
    private javax.swing.JTextField txtTaxId;
    // End of variables declaration//GEN-END:variables
}

