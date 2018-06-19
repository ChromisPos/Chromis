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
package uk.chromis.pos.suppliers;

import java.awt.Component;
import java.util.UUID;
import javax.swing.JPanel;
import uk.chromis.basic.BasicException;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.format.Formats;

public final class SuppliersEditor extends JPanel implements EditorRecord {

    private String siteGuid;
    private boolean reportlock = false;
    private Object m_id;

    public SuppliersEditor(DirtyManager dirty, String siteGuid) {
        initComponents();
        this.siteGuid = siteGuid;

        m_SupplierActive.addActionListener(dirty);
        m_SupplierAccount.getDocument().addDocumentListener(dirty);
        m_SupplierAddr1.getDocument().addDocumentListener(dirty);
        m_SupplierAddr2.getDocument().addDocumentListener(dirty);
        m_SupplierCity.getDocument().addDocumentListener(dirty);
        m_SupplierComments.getDocument().addDocumentListener(dirty);
        m_SupplierContact.getDocument().addDocumentListener(dirty);
        m_SupplierCredit.getDocument().addDocumentListener(dirty);
        m_SupplierEmail.getDocument().addDocumentListener(dirty);
        m_SupplierName.getDocument().addDocumentListener(dirty);
        m_SupplierPostCode.getDocument().addDocumentListener(dirty);
        m_SupplierTelephone.getDocument().addDocumentListener(dirty);
        m_SupplierTerms.getDocument().addDocumentListener(dirty);

        writeValueEOF();
    }

    public void activate() throws BasicException {

    }

    @Override
    public void refreshGuid(String guid) {
    }

    @Override
    public void refresh() {
    }

    @Override
    public void writeValueEOF() {
        reportlock = true;

        m_id = null;
        m_SupplierActive.setSelected(true);
        m_SupplierAccount.setText(null);
        m_SupplierAddr1.setText(null);
        m_SupplierAddr2.setText(null);
        m_SupplierCity.setText(null);
        m_SupplierComments.setText(null);
        m_SupplierContact.setText(null);
        m_SupplierCredit.setText(null);
        m_SupplierEmail.setText(null);
        m_SupplierName.setText(null);
        m_SupplierPostCode.setText(null);
        m_SupplierTelephone.setText(null);
        m_SupplierTerms.setText(null);

        reportlock = false;

        m_SupplierActive.setEnabled(false);
        m_SupplierAccount.setEnabled(false);
        m_SupplierAddr1.setEnabled(false);
        m_SupplierAddr2.setEnabled(false);
        m_SupplierCity.setEnabled(false);
        m_SupplierComments.setEnabled(false);
        m_SupplierContact.setEnabled(false);
        m_SupplierCredit.setEnabled(false);
        m_SupplierEmail.setEnabled(false);
        m_SupplierName.setEnabled(false);
        m_SupplierPostCode.setEnabled(false);
        m_SupplierTelephone.setEnabled(false);
        m_SupplierTerms.setEnabled(false);
       
    }

    /**
     *
     */
    @Override
    public void writeValueInsert() {
        reportlock = true;

        m_id = UUID.randomUUID().toString();
        m_SupplierActive.setSelected(true);
        m_SupplierAccount.setText(null);
        m_SupplierAddr1.setText(null);
        m_SupplierAddr2.setText(null);
        m_SupplierCity.setText(null);
        m_SupplierComments.setText(null);
        m_SupplierContact.setText(null);
        m_SupplierCredit.setText("0.00");
        m_SupplierEmail.setText(null);
        m_SupplierName.setText(null);
        m_SupplierPostCode.setText(null);
        m_SupplierTelephone.setText(null);
        m_SupplierTerms.setText(null);

        reportlock = false;

        m_SupplierActive.setEnabled(true);
        m_SupplierAccount.setEnabled(true);
        m_SupplierAddr1.setEnabled(true);
        m_SupplierAddr2.setEnabled(true);
        m_SupplierCity.setEnabled(true);
        m_SupplierComments.setEnabled(true);
        m_SupplierContact.setEnabled(true);
        m_SupplierCredit.setEnabled(true);
        m_SupplierEmail.setEnabled(true);
        m_SupplierName.setEnabled(true);
        m_SupplierPostCode.setEnabled(true);
        m_SupplierTelephone.setEnabled(true);
        m_SupplierTerms.setEnabled(true);

    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueDelete(Object value) {
        reportlock = true;
        Object[] supplier = (Object[]) value;
        m_id = supplier[0];
        m_SupplierName.setText(Formats.STRING.formatValue(supplier[1]));
        m_SupplierAccount.setText(Formats.STRING.formatValue(supplier[2]));
        m_SupplierAddr1.setText(Formats.STRING.formatValue(supplier[3]));
        m_SupplierAddr2.setText(Formats.STRING.formatValue(supplier[4]));
        m_SupplierCity.setText(Formats.STRING.formatValue(supplier[5]));
        m_SupplierPostCode.setText(Formats.STRING.formatValue(supplier[6]));
        m_SupplierEmail.setText(Formats.STRING.formatValue(supplier[7]));
        m_SupplierTelephone.setText(Formats.STRING.formatValue(supplier[8]));
        m_SupplierCredit.setText(Formats.DOUBLE.formatValue(supplier[9]));
        m_SupplierTerms.setText(Formats.STRING.formatValue(supplier[10]));
        m_SupplierActive.setSelected(((Boolean) supplier[11]));
        m_SupplierContact.setText(Formats.STRING.formatValue(supplier[12]));
        m_SupplierComments.setText(Formats.STRING.formatValue(supplier[13]));

        reportlock = false;

        m_SupplierActive.setEnabled(false);
        m_SupplierAccount.setEnabled(false);
        m_SupplierAddr1.setEnabled(false);
        m_SupplierAddr2.setEnabled(false);
        m_SupplierCity.setEnabled(false);
        m_SupplierComments.setEnabled(false);
        m_SupplierContact.setEnabled(false);
        m_SupplierCredit.setEnabled(false);
        m_SupplierEmail.setEnabled(false);
        m_SupplierName.setEnabled(false);
        m_SupplierPostCode.setEnabled(false);
        m_SupplierTelephone.setEnabled(false);
        m_SupplierTerms.setEnabled(false);

    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueEdit(Object value) {
        Object[] supplier = (Object[]) value;
        m_id = supplier[0];

        m_SupplierName.setText(Formats.STRING.formatValue(supplier[1]));
        m_SupplierAccount.setText(Formats.STRING.formatValue(supplier[2]));
        m_SupplierAddr1.setText(Formats.STRING.formatValue(supplier[3]));
        m_SupplierAddr2.setText(Formats.STRING.formatValue(supplier[4]));
        m_SupplierCity.setText(Formats.STRING.formatValue(supplier[5]));
        m_SupplierPostCode.setText(Formats.STRING.formatValue(supplier[6]));
        m_SupplierEmail.setText(Formats.STRING.formatValue(supplier[7]));
        m_SupplierTelephone.setText(Formats.STRING.formatValue(supplier[8]));
        m_SupplierCredit.setText(Formats.DOUBLE.formatValue(supplier[9]));
        m_SupplierTerms.setText(Formats.STRING.formatValue(supplier[10]));
        m_SupplierActive.setSelected(((Boolean) supplier[11]));
        m_SupplierContact.setText(Formats.STRING.formatValue(supplier[12]));
        m_SupplierComments.setText(Formats.STRING.formatValue(supplier[13]));
        siteGuid = supplier[14].toString();

    }

    /**
     *
     * @return myprod
     * @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {
        Object[] supplier = new Object[15];

        supplier[0] = m_id;
        supplier[1] = m_SupplierName.getText();
        supplier[2] = m_SupplierAccount.getText();
        supplier[3] = m_SupplierAddr1.getText();
        supplier[4] = m_SupplierAddr2.getText();
        supplier[5] = m_SupplierCity.getText();
        supplier[6] = m_SupplierPostCode.getText();
        supplier[7] = m_SupplierEmail.getText();
        supplier[8] = m_SupplierTelephone.getText();
        supplier[9] = Formats.DOUBLE.parseValue(m_SupplierCredit.getText());
        supplier[10] = m_SupplierTerms.getText();
        supplier[11] = m_SupplierActive.isSelected();
        supplier[12] = m_SupplierContact.getText();
        supplier[13] = m_SupplierComments.getText();
        supplier[14] = siteGuid;

        return supplier;
    }

    /**
     *
     * @return this
     */
    @Override
    public Component getComponent() {
        return this;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel24 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jSupplierName = new javax.swing.JLabel();
        m_SupplierName = new javax.swing.JTextField();
        jSupplierAccount = new javax.swing.JLabel();
        m_SupplierAccount = new javax.swing.JTextField();
        jSupplierAddr1 = new javax.swing.JLabel();
        m_SupplierCity = new javax.swing.JTextField();
        m_SupplierAddr1 = new javax.swing.JTextField();
        m_SupplierAddr2 = new javax.swing.JTextField();
        jSupplierPostcode = new javax.swing.JLabel();
        m_SupplierPostCode = new javax.swing.JTextField();
        jSupplierCity = new javax.swing.JLabel();
        jSupplierContact = new javax.swing.JLabel();
        m_SupplierContact = new javax.swing.JTextField();
        jSupplierTerms = new javax.swing.JLabel();
        m_SupplierEmail = new javax.swing.JTextField();
        jSupplierComments = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_SupplierComments = new javax.swing.JTextArea();
        jSupplierTelephone = new javax.swing.JLabel();
        m_SupplierTelephone = new javax.swing.JTextField();
        jSupplierEmail = new javax.swing.JLabel();
        m_SupplierCredit = new javax.swing.JTextField();
        jSupplierActive = new javax.swing.JLabel();
        m_SupplierTerms = new javax.swing.JTextField();
        jSupplierCredit = new javax.swing.JLabel();
        m_SupplierActive = new eu.hansolo.custom.SteelCheckBox();

        jLabel24.setText("jLabel24");

        jLabel27.setText("jLabel27");

        jLabel1.setText("jLabel1");

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSupplierName.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jSupplierName.setText(bundle.getString("label.suppliername")); // NOI18N
        add(jSupplierName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 100, 20));

        m_SupplierName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_SupplierNameActionPerformed(evt);
            }
        });
        add(m_SupplierName, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 20, 250, -1));

        jSupplierAccount.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jSupplierAccount.setText(bundle.getString("label.supplieraccount")); // NOI18N
        add(jSupplierAccount, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 20, 70, 20));
        add(m_SupplierAccount, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 20, 160, -1));

        jSupplierAddr1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jSupplierAddr1.setText(bundle.getString("label.supplierAddress")); // NOI18N
        add(jSupplierAddr1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 46, 100, 20));
        add(m_SupplierCity, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 98, 250, -1));
        add(m_SupplierAddr1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 46, 250, -1));

        m_SupplierAddr2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_SupplierAddr2ActionPerformed(evt);
            }
        });
        add(m_SupplierAddr2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 72, 250, -1));

        jSupplierPostcode.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jSupplierPostcode.setText(bundle.getString("label.supplierpostcode")); // NOI18N
        add(jSupplierPostcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 98, 70, 20));
        add(m_SupplierPostCode, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 98, 160, -1));

        jSupplierCity.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jSupplierCity.setText(bundle.getString("label.suppliercity")); // NOI18N
        add(jSupplierCity, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 98, 98, 20));

        jSupplierContact.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jSupplierContact.setText(bundle.getString("label.suppliercontact")); // NOI18N
        add(jSupplierContact, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 124, 100, 20));
        add(m_SupplierContact, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 124, 250, -1));

        jSupplierTerms.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jSupplierTerms.setText(bundle.getString("label.suppliercreditterms")); // NOI18N
        add(jSupplierTerms, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 176, 100, 20));
        add(m_SupplierEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 150, 250, -1));

        jSupplierComments.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jSupplierComments.setText(bundle.getString("label.suppliercomments")); // NOI18N
        add(jSupplierComments, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 230, 100, 20));

        m_SupplierComments.setColumns(20);
        m_SupplierComments.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        m_SupplierComments.setRows(5);
        jScrollPane1.setViewportView(m_SupplierComments);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 230, 500, 100));

        jSupplierTelephone.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jSupplierTelephone.setText(bundle.getString("label.suppliertelephone")); // NOI18N
        add(jSupplierTelephone, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 124, 70, 20));
        add(m_SupplierTelephone, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 124, 160, -1));

        jSupplierEmail.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jSupplierEmail.setText(bundle.getString("label.supplieremail")); // NOI18N
        add(jSupplierEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 100, 20));
        add(m_SupplierCredit, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 176, 70, -1));

        jSupplierActive.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jSupplierActive.setText(bundle.getString("label.supplieractive")); // NOI18N
        add(jSupplierActive, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 200, 100, 30));
        add(m_SupplierTerms, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 176, 310, -1));

        jSupplierCredit.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jSupplierCredit.setText(bundle.getString("label.suppliercreditlimit")); // NOI18N
        add(jSupplierCredit, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 176, 100, 20));

        m_SupplierActive.setText(" ");
        m_SupplierActive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_SupplierActiveActionPerformed(evt);
            }
        });
        add(m_SupplierActive, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 200, 40, 30));
    }// </editor-fold>//GEN-END:initComponents

    private void m_SupplierActiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_SupplierActiveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_m_SupplierActiveActionPerformed

    private void m_SupplierNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_SupplierNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_m_SupplierNameActionPerformed

    private void m_SupplierAddr2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_SupplierAddr2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_m_SupplierAddr2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jSupplierAccount;
    private javax.swing.JLabel jSupplierActive;
    private javax.swing.JLabel jSupplierAddr1;
    private javax.swing.JLabel jSupplierCity;
    private javax.swing.JLabel jSupplierComments;
    private javax.swing.JLabel jSupplierContact;
    private javax.swing.JLabel jSupplierCredit;
    private javax.swing.JLabel jSupplierEmail;
    private javax.swing.JLabel jSupplierName;
    private javax.swing.JLabel jSupplierPostcode;
    private javax.swing.JLabel jSupplierTelephone;
    private javax.swing.JLabel jSupplierTerms;
    private javax.swing.JTextField m_SupplierAccount;
    private eu.hansolo.custom.SteelCheckBox m_SupplierActive;
    private javax.swing.JTextField m_SupplierAddr1;
    private javax.swing.JTextField m_SupplierAddr2;
    private javax.swing.JTextField m_SupplierCity;
    private javax.swing.JTextArea m_SupplierComments;
    private javax.swing.JTextField m_SupplierContact;
    private javax.swing.JTextField m_SupplierCredit;
    private javax.swing.JTextField m_SupplierEmail;
    private javax.swing.JTextField m_SupplierName;
    private javax.swing.JTextField m_SupplierPostCode;
    private javax.swing.JTextField m_SupplierTelephone;
    private javax.swing.JTextField m_SupplierTerms;
    // End of variables declaration//GEN-END:variables

}
