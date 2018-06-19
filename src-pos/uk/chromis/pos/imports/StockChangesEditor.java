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

package uk.chromis.pos.imports;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.UUID;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.gui.JMessageDialog;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.forms.DataLogicStockChanges;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.sync.DataLogicSync;

/**
 *
 * @author John
 */
public class StockChangesEditor extends javax.swing.JPanel implements EditorRecord {

    private Object m_oId;
    private final DataLogicStockChanges m_dlChanges;
    private final DataLogicSales m_dlSales;
    private DataLogicSync dlSync;
    private static DataLogicSystem m_dlSystem;

    private final SentenceList m_sentcat;
    private ComboBoxValModel m_CategoryModel;
    private final SentenceList m_taxcatsent;
    private ComboBoxValModel m_taxcatmodel;

    private String m_Location;
    private String m_ProductName;
    private String m_ProductRef;
    private String m_ProductID;
    private final DirtyManager m_Dirty;
    private String siteGuid;

    public StockChangesEditor(DataLogicStockChanges dlChanges, DataLogicSales dlSales, DataLogicSystem dlSystem, DataLogicSync dlSync, DirtyManager dirty) {
        m_dlChanges = dlChanges;
        m_dlSales = dlSales;
        m_dlSystem = dlSystem;
        m_Dirty = dirty;

        siteGuid = dlSync.getSiteGuid();

        initComponents();

        // Categories model
        m_sentcat = dlSales.getCategoriesList(siteGuid);
        m_CategoryModel = new ComboBoxValModel();

        // Taxes model
        m_taxcatsent = dlSales.getTaxCategoriesList(siteGuid);
        m_taxcatmodel = new ComboBoxValModel();

        m_jCategory.addActionListener(dirty);
        m_jTax.addActionListener(dirty);

        jComboChangeType.addActionListener(dirty);
        jComboAction.addActionListener(dirty);
        jComboField.addActionListener(dirty);
        jTextValue.getDocument().addDocumentListener(dirty);
        m_jImage.addPropertyChangeListener("image", dirty);

        writeValueEOF();
    }

    public void activate() throws BasicException {

        m_CategoryModel = new ComboBoxValModel(m_sentcat.list());
        m_jCategory.setModel(m_CategoryModel);

        m_taxcatmodel = new ComboBoxValModel(m_taxcatsent.list());
        m_jTax.setModel(m_taxcatmodel);

    }

    private void setValueControl(String field) {

        jTextValue.setVisible(false);
        m_jImage.setVisible(false);
        m_jCategory.setVisible(false);
        m_jTax.setVisible(false);

        if (field == null) {
            return;
        }

        if (field.contentEquals("IMAGE")) {
            m_jImage.setVisible(true);
        } else if (field.contentEquals("CATEGORY")) {
            m_jCategory.setVisible(true);
        } else if (field.contentEquals("TAXCAT")) {
            m_jTax.setVisible(true);
        } else {
            jTextValue.setVisible(true);
        }
    }

    /**
     *
     */
    @Override
    public void writeValueEOF() {
        m_jTitle.setText(AppLocal.getIntString("label.recordeof"));

        m_oId = null;

        jComboChangeType.setSelectedIndex(0);
        jComboAction.setSelectedIndex(0);
        jComboField.setSelectedIndex(0);
        m_CategoryModel.setSelectedKey(null);
        m_taxcatmodel.setSelectedKey(null);
        jTextProduct.setText(null);
        jTextUploadTime.setText(null);
        jTextUser.setText(null);
        jTextValue.setText(null);
        m_jImage.setImage(null);

        jComboChangeType.setEnabled(false);
        jComboAction.setEnabled(false);
        jComboField.setEnabled(false);
        jTextValue.setEnabled(false);
        m_jImage.setEnabled(false);
        jTextValue.setEnabled(false);
        m_jTax.setEnabled(false);
        m_jCategory.setEnabled(false);

        setValueControl(null);
    }

    /**
     *
     */
    @Override
    public void writeValueInsert() {
        m_jTitle.setText(AppLocal.getIntString("label.recordnew"));

        m_oId = UUID.randomUUID().toString();

        jComboChangeType.setSelectedIndex(0);
        jComboAction.setSelectedIndex(0);
        jComboField.setSelectedIndex(0);
        m_CategoryModel.setSelectedKey(null);
        m_taxcatmodel.setSelectedKey(null);
        jTextProduct.setText(null);
        jTextUploadTime.setText(null);
        jTextUser.setText(null);
        jTextValue.setText(null);
        m_jImage.setImage(null);

        jComboChangeType.setEnabled(true);
        jComboAction.setEnabled(true);
        jComboField.setEnabled(true);
        jTextValue.setEnabled(true);
        m_jTax.setEnabled(true);
        m_jCategory.setEnabled(true);
        m_jImage.setEnabled(true);

        setValueControl(null);
    }

    public static void setSelectedValue(JComboBox comboBox, String value) {
        String item;
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            item = (String) comboBox.getItemAt(i);
            if (item.contentEquals(value)) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueEdit(Object value) {

        Object[] aValue = (Object[]) value;

        m_jTitle.setText(Formats.STRING.formatValue(aValue[m_dlChanges.getIndexOf("PRODUCTNAME")]));

        m_oId = aValue[m_dlChanges.getIndexOf("ID")];
        m_Location = (String) aValue[m_dlChanges.getIndexOf("LOCATION")];
        m_ProductName = (String) aValue[m_dlChanges.getIndexOf("PRODUCTNAME")];
        m_ProductRef = (String) aValue[m_dlChanges.getIndexOf("PRODUCTREF")];
        m_ProductID = (String) aValue[m_dlChanges.getIndexOf("PRODUCTID")];

        Integer type = (Integer) aValue[m_dlChanges.getIndexOf("CHANGETYPE")];
        jComboChangeType.setSelectedIndex(type);

        jComboAction.setSelectedIndex((Integer) aValue[m_dlChanges.getIndexOf("CHANGES_PROCESSED")]);

        String field = (String) aValue[m_dlChanges.getIndexOf("FIELD")];
        setSelectedValue(jComboField, field);

        jTextProduct.setText(m_ProductName);
        jTextUploadTime.setText(Formats.TIMESTAMP.formatValue(aValue[m_dlChanges.getIndexOf("UPLOADTIME")]));
        jTextUser.setText(Formats.STRING.formatValue(aValue[m_dlChanges.getIndexOf("USERNAME")]));
        jTextValue.setText(Formats.STRING.formatValue(aValue[m_dlChanges.getIndexOf("TEXTVALUE")]));
        m_jImage.setImage((BufferedImage) aValue[m_dlChanges.getIndexOf("BLOBVALUE")]);
        if (field.contentEquals("CATEGORY")) {
            m_CategoryModel.setSelectedKey(aValue[m_dlChanges.getIndexOf("TEXTVALUE")]);
        }

        if (field.contentEquals("TAXCAT")) {
            m_taxcatmodel.setSelectedKey(aValue[m_dlChanges.getIndexOf("TEXTVALUE")]);
        }

        jComboChangeType.setEnabled(true);
        jComboAction.setEnabled(true);
        jComboField.setEnabled(true);
        jTextValue.setEnabled(true);
        m_jTax.setEnabled(true);
        m_jCategory.setEnabled(true);
        m_jImage.setEnabled(true);

        setValueControl(field);

    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueDelete(Object value) {
        m_jTitle.setText(AppLocal.getIntString("label.recorddeleted"));

        Object[] aValue = (Object[]) value;

        m_oId = aValue[m_dlChanges.getIndexOf("ID")];
        m_Location = (String) aValue[m_dlChanges.getIndexOf("LOCATION")];
        m_ProductName = (String) aValue[m_dlChanges.getIndexOf("PRODUCTNAME")];
        m_ProductRef = (String) aValue[m_dlChanges.getIndexOf("PRODUCTREF")];
        m_ProductID = (String) aValue[m_dlChanges.getIndexOf("PRODUCTID")];

        Integer type = (Integer) aValue[m_dlChanges.getIndexOf("CHANGETYPE")];
        jComboChangeType.setSelectedIndex(type);

        jComboAction.setSelectedIndex((Integer) aValue[m_dlChanges.getIndexOf("CHANGES_PROCESSED")]);

        String field = (String) aValue[m_dlChanges.getIndexOf("FIELD")];
        setSelectedValue(jComboField, field);

        jTextProduct.setText(m_ProductName);
        jTextUploadTime.setText(Formats.TIMESTAMP.formatValue(aValue[m_dlChanges.getIndexOf("UPLOADTIME")]));
        jTextUser.setText(Formats.STRING.formatValue(aValue[m_dlChanges.getIndexOf("USERNAME")]));
        jTextValue.setText(Formats.STRING.formatValue(aValue[m_dlChanges.getIndexOf("TEXTVALUE")]));
        m_jImage.setImage((BufferedImage) aValue[m_dlChanges.getIndexOf("BLOBVALUE")]);

        if (field.contentEquals("CATEGORY")) {
            m_CategoryModel.setSelectedKey(aValue[m_dlChanges.getIndexOf("TEXTVALUE")]);
        }

        if (field.contentEquals("TAXCAT")) {
            m_taxcatmodel.setSelectedKey(aValue[m_dlChanges.getIndexOf("TEXTVALUE")]);
        }

        jComboChangeType.setEnabled(false);
        jComboAction.setEnabled(false);
        jComboField.setEnabled(false);
        jTextValue.setEnabled(false);
        m_jTax.setEnabled(false);
        m_jCategory.setEnabled(false);
        m_jImage.setEnabled(false);

        setValueControl(field);
    }

    @Override
    public Object createValue() throws BasicException {
        int count = m_dlChanges.getFieldCount();

        String field = (String) jComboField.getSelectedItem();

        Object[] changes = new Object[count];
        changes[m_dlChanges.getIndexOf("ID")] = m_oId;
        changes[m_dlChanges.getIndexOf("LOCATION")] = m_Location;
        changes[m_dlChanges.getIndexOf("USERNAME")] = jTextUser.getText();
        changes[m_dlChanges.getIndexOf("UPLOADTIME")] = (Date) Formats.TIMESTAMP.parseValue(jTextUploadTime.getText());
        changes[m_dlChanges.getIndexOf("CHANGETYPE")] = jComboChangeType.getSelectedIndex();
        changes[m_dlChanges.getIndexOf("CHANGES_PROCESSED")] = jComboAction.getSelectedIndex();

        changes[m_dlChanges.getIndexOf("FIELD")] = field;

        changes[m_dlChanges.getIndexOf("PRODUCTID")] = m_ProductID;
        changes[m_dlChanges.getIndexOf("PRODUCTNAME")] = m_ProductName;
        changes[m_dlChanges.getIndexOf("PRODUCTREF")] = m_ProductRef;

        if (field.contentEquals("CATEGORY")) {
            changes[m_dlChanges.getIndexOf("TEXTVALUE")] = m_CategoryModel.getSelectedKey();
        } else if (field.contentEquals("TAXCAT")) {
            changes[m_dlChanges.getIndexOf("TEXTVALUE")] = m_taxcatmodel.getSelectedKey();
        } else if (field.contentEquals("IMAGE")) {
            changes[m_dlChanges.getIndexOf("BLOBVALUE")] = m_jImage.getImage();
        } else {
            changes[m_dlChanges.getIndexOf("TEXTVALUE")] = jTextValue.getText();
        }

        return changes;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void refresh() {
    }

    @Override
    public void refreshGuid(String siteGuid) {
        this.siteGuid = siteGuid;
    }

    private void showDialog(String message) {
        // Get details of the original font before we change it otherwise all dialogboxes will use new settings
        JOptionPane pane = new JOptionPane();
        Font originalFont = pane.getFont();

        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("ARIAL", Font.PLAIN, 20)));
        JLabel FontText = new JLabel(message);

        JOptionPane newpane = new JOptionPane();
        newpane.setMessage(FontText);
//        newpane.setPreferredSize( new Dimension(450,150));

        Dialog dlg = newpane.createDialog(AppLocal.getIntString("Menu.StockChanges"));
        dlg.setVisible(true);

        // Return to default settings
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font(originalFont.getName(), originalFont.getStyle(), originalFont.getSize())));

    }

    private void ProcessAllAccepted() {
        String sql = (m_dlSystem.getResourceAsText("sql.ActionStockChanges"));
        if (sql != null && sql.length() > 0) {
            try {
                m_dlChanges.ActionSql(sql);
                String message = AppLocal.getIntString("message.stockchangesactioned");
                showDialog(message);
            } catch (BasicException ex) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotexecute"), ex));
            }
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

        m_jTitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextUser = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextUploadTime = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextProduct = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jComboChangeType = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jComboField = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jComboAction = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        jTextValue = new javax.swing.JTextField();
        m_jImage = new uk.chromis.data.gui.JImageEditor();
        m_jTax = new javax.swing.JComboBox();
        m_jCategory = new javax.swing.JComboBox();
        jButtonProcess = new javax.swing.JButton();

        m_jTitle.setFont(new java.awt.Font("SansSerif", 3, 18)); // NOI18N
        m_jTitle.setPreferredSize(new java.awt.Dimension(320, 30));

        jLabel1.setText("User");

        jTextUser.setEditable(false);

        jLabel2.setText("Upload Time");

        jTextUploadTime.setEditable(false);

        jLabel3.setText("Product");

        jTextProduct.setEditable(false);

        jLabel4.setText("Change Type");

        jComboChangeType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "Adjust Value", "Set Value", "Set Image", "New Value", "New Image", "New Record" }));

        jLabel5.setText("Field");

        jComboField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "NAME", "REFERENCE", "CODE", "CATEGORY", "LOCATION", "PRICEBUY", "PRICESELL", "TAXCAT", "QTY_INSTOCK", "QTY_MAX", "QTY_MIN", "IMAGE" }));

        jLabel6.setText("Value");

        jLabel7.setText("Action");

        jComboAction.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Accept", "Reject", "Processed" }));

        m_jTax.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jTaxActionPerformed(evt);
            }
        });

        m_jCategory.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jButtonProcess.setText("Process All Accepted");
        jButtonProcess.setToolTipText("");
        jButtonProcess.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonProcessMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 517, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboChangeType, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboField, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextUploadTime, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextUser, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(m_jTax, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(m_jCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(m_jImage, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jTextValue)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(jComboAction, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(82, 82, 82)
                                .addComponent(jButtonProcess))))
                    .addComponent(m_jTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(m_jTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jButtonProcess))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextUploadTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboChangeType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jImage, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(m_jTax, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(m_jCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        m_jTitle.getAccessibleContext().setAccessibleName("m_jTitle");
        m_jTitle.getAccessibleContext().setAccessibleDescription("");
    }// </editor-fold>//GEN-END:initComponents

    private void m_jTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jTaxActionPerformed

    }//GEN-LAST:event_m_jTaxActionPerformed

    private void jButtonProcessMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonProcessMouseClicked
        ProcessAllAccepted();
    }//GEN-LAST:event_jButtonProcessMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonProcess;
    private javax.swing.JComboBox jComboAction;
    private javax.swing.JComboBox jComboChangeType;
    private javax.swing.JComboBox jComboField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextProduct;
    private javax.swing.JTextField jTextUploadTime;
    private javax.swing.JTextField jTextUser;
    private javax.swing.JTextField jTextValue;
    private javax.swing.JComboBox m_jCategory;
    private uk.chromis.data.gui.JImageEditor m_jImage;
    private javax.swing.JComboBox m_jTax;
    private javax.swing.JLabel m_jTitle;
    // End of variables declaration//GEN-END:variables

}
