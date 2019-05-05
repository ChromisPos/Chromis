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
package uk.chromis.pos.inventory;

import uk.chromis.basic.BasicException;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import java.awt.Component;
import java.sql.Statement;
import uk.chromis.data.gui.MessageInf;

public class ProductPacksEditor extends javax.swing.JPanel implements EditorRecord {

    public Object prodid;
    public Object prodref;
    public Object prodname;
    public Object location;
    public Object inStock;
    public Object packQuantity;
    public Object packprodid;
    public Object packPrice;
    public Object atrSetId;

    private String siteGuid;
    private String m_location;
    private Statement stmt;
    private String SQL;

    @Override
    public void refreshGuid(String siteGuid) {
         this.siteGuid = siteGuid;  
    }

    public interface SplitNotify {

        public void SplitNotify(
                String Location,
                String prodID,
                Double packQuantity,
                Double unitsInPack,
                Double unitsToSplit,
                String packProdID,
                Double packPrice,
                String atrSetId,
                String siteGuid
        ) throws BasicException;
    };

    private SplitNotify m_splitNotify = null;

    public void setSplitNotify(SplitNotify fn) {
        m_splitNotify = fn;
    }

    /**
     * Creates new form ProductPacksEditor
     *
     * @param dirty
     */
    public ProductPacksEditor(DirtyManager dirty, String siteGuid) {
        this.siteGuid = siteGuid;
        initComponents();
    }

    
    /**
     *
     */
    @Override
    public void writeValueEOF() {
        m_jTitle.setText(AppLocal.getIntString("label.recordeof"));
        prodid = null;
        prodref = null;
        prodname = null;
        location = null;
        inStock = null;
        packQuantity = null;
        packprodid = null;
        packPrice = null;
        atrSetId = null;
        m_jInStock.setText(null);
        m_jToSplit.setText(null);
        m_jProduct.setText(null);
        m_jToSplit.setEnabled(true);
        m_jSplit.setEnabled(false);
    }

    /**
     *
     */
    @Override
    public void writeValueInsert() {
        m_jTitle.setText(AppLocal.getIntString("label.recordnew"));
        prodid = null;
        prodref = null;
        prodname = null;
        location = null;
        inStock = null;
        packQuantity = null;
        packprodid = null;
        packPrice = null;
        atrSetId = null;
        m_jInStock.setText(null);
        m_jToSplit.setText(null);
        m_jProduct.setText(null);
        m_jToSplit.setEnabled(true);
        m_jSplit.setEnabled(false);
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueEdit(Object value) {
        Object[] myprod = (Object[]) value;
        prodid = myprod[0];
        prodref = myprod[1];
        prodname = myprod[2];
        location = myprod[3];
        packQuantity = myprod[6];
        inStock = myprod[7];
        packprodid = myprod[8];
        packPrice = myprod[9];
        atrSetId = myprod[10];

        m_jTitle.setText(Formats.STRING.formatValue(myprod[1]) + " - " + Formats.STRING.formatValue(myprod[2]));
        m_jInStock.setText(
                Formats.DOUBLE.formatValue(inStock) + " "
                + AppLocal.getIntString("label.packof") + " "
                + Formats.DOUBLE.formatValue(packQuantity)
        );
        m_jToSplit.setText("1");
        m_jProduct.setText(Formats.STRING.formatValue(myprod[4]) + " - " + Formats.STRING.formatValue(myprod[5]));
        m_jToSplit.setEnabled(true);
        siteGuid = myprod[11].toString();
        m_jSplit.setEnabled((double) inStock > 0 ? true : false);
    }

    
      
    
    
    /**
     *
     * @param value
     */
    @Override
    public void writeValueDelete(Object value) {
        writeValueEdit(value);
    }

    /**
     *
     * @return @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public Component getComponent() {
        return this;
    }

    /**
     *
     */
    @Override
    public void refresh() {
    }

    private void SplitActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            Double stk = Double.parseDouble(Formats.DOUBLE.formatValue(inStock));
            Double pq = Double.parseDouble(Formats.DOUBLE.formatValue(packQuantity));
            Double tosplit = Double.parseDouble(Formats.DOUBLE.formatValue(Formats.CURRENCY.parseValue(m_jToSplit.getText())));
            Double inPack = Double.parseDouble(Formats.DOUBLE.formatValue(packQuantity));
            Double price = Double.parseDouble(Formats.DOUBLE.formatValue(packPrice));
            String attrId = (atrSetId == null) ? null : atrSetId.toString();
            

            if (tosplit > 0 && m_splitNotify != null) {
                m_splitNotify.SplitNotify(
                        location.toString(), prodid.toString(), pq, inPack, tosplit, packprodid.toString(), price, attrId, siteGuid);

                stk = stk - tosplit;
                inStock = stk;
                m_jInStock.setText(
                        Formats.DOUBLE.formatValue(inStock) + " "
                        + AppLocal.getIntString("label.packof") + " "
                        + Formats.DOUBLE.formatValue(packQuantity)
                );

            }
        } catch (BasicException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.nosave"), e);
            msg.show(this);
        }
    }

    private void initComponents() {

        m_jTitle = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        m_jInStock = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        m_jToSplit = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        m_jProduct = new javax.swing.JLabel();
        m_jSplit = new javax.swing.JButton();

        setLayout(null);

        m_jTitle.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        add(m_jTitle);
        m_jTitle.setBounds(10, 10, 320, 30);

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("label.packsinstock")); // NOI18N
        add(jLabel3);
        jLabel3.setBounds(10, 50, 350, 25);

        m_jInStock.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        add(m_jInStock);
        m_jInStock.setBounds(160, 50, 80, 25);

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("label.productinpack")); // NOI18N
        add(jLabel4);
        jLabel4.setBounds(10, 80, 150, 25);

        m_jProduct.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        add(m_jProduct);
        m_jProduct.setBounds(160, 80, 350, 25);

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText(AppLocal.getIntString("label.packstosplit")); // NOI18N
        add(jLabel5);
        jLabel5.setBounds(10, 110, 150, 25);

        m_jToSplit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jToSplit.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        add(m_jToSplit);
        m_jToSplit.setBounds(160, 110, 80, 25);

        m_jSplit.setFont(new java.awt.Font("Arial", 0, 12));
        m_jSplit.setText(AppLocal.getIntString("label.split"));
        add(m_jSplit);
        m_jSplit.setBounds(270, 110, 80, 25);
        m_jSplit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SplitActionPerformed(evt);
            }
        });

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel m_jInStock;
    private javax.swing.JTextField m_jToSplit;
    private javax.swing.JLabel m_jProduct;
    private javax.swing.JLabel m_jTitle;
    private javax.swing.JButton m_jSplit;
    // End of variables declaration//GEN-END:variables

}
