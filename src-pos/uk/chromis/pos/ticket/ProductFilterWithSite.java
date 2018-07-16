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

package uk.chromis.pos.ticket;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.gui.ListQBFModelNumber;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.QBFCompareEnum;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerWrite;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.inventory.ProductsPanel;
import uk.chromis.pos.reports.ReportEditorCreator;
import uk.chromis.pos.sync.DataLogicSync;

/**
 *
 *
 */
public class ProductFilterWithSite extends javax.swing.JPanel implements ReportEditorCreator {

    private SentenceList m_sentcat;
    private ComboBoxValModel m_CategoryModel;
    private SentenceList m_sentSites;
    public ComboBoxValModel m_LocationsModel;
    private DataLogicSync dlSync;
    private String siteGuid;
    private DataLogicSales dlSales;  
    private List catlist;

    public ProductFilterWithSite(Boolean isCentral) {
        initComponents();
        jSites.setVisible(true); // Workaround to always see the sites panel
        //jSites.setVisible(isCentral);
    }

    @Override
    public void init(AppView app) {
        dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");
        
        m_sentSites = dlSync.getSitesList();
        
        m_sentcat = dlSales.getCategoriesList(dlSync.getSiteGuid());
        m_CategoryModel = new ComboBoxValModel();

        m_jCboName.setModel(ListQBFModelNumber.getMandatoryString());
        m_jCboPriceBuy.setModel(ListQBFModelNumber.getMandatoryNumber());
        m_jCboPriceSell.setModel(ListQBFModelNumber.getMandatoryNumber());
    }

    @Override
    public void activate() throws BasicException {
        catlist = m_sentcat.list();
        catlist.add(0, null);
        m_CategoryModel = new ComboBoxValModel(catlist);
        m_jCategory.setModel(m_CategoryModel);

        List a;
        try {
            a = m_sentSites.list();
        } catch (BasicException ex) {
            a = dlSync.getSingleSite().list();
        }

        //if (dlSync.getCentralGuid().equals(dlSync.getSiteGuid())) {
        //    SitesInfo tempSite = new SitesInfo(null, null);
        //    a.add(0, tempSite);
        // }
        addFirst(a);
        m_LocationsModel = new ComboBoxValModel(a);
        if (!dlSync.isCentral()) {
            m_LocationsModel.setSelectedFirst();
        }
        m_jLocation.setModel(m_LocationsModel);
    }

    protected void addFirst(List a) {
        // do nothing
    }

    public void refreshGuid(String guid) {
        siteGuid = guid;
        try {
            m_sentcat = dlSales.getCategoriesList(siteGuid);
            catlist = m_sentcat.list();
            catlist.add(0, null);
            m_CategoryModel = new ComboBoxValModel(catlist);
            m_jCategory.setModel(m_CategoryModel);
        } catch (BasicException ex) {
            Logger.getLogger(ProductFilterWithSite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addActionListener(ActionListener l) {
        m_jLocation.addActionListener(l);
    }
    
    @Override
    public SerializerWrite getSerializerWrite() {
        return new SerializerWriteBasic(
                new Datas[]{Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING});
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Object createValue() throws BasicException {

        if (m_jBarcode.getText() == null || m_jBarcode.getText().equals("")) {
            // Filtro por formulario
            return new Object[]{
                m_jCboName.getSelectedItem(), m_jName.getText(),
                m_jCboPriceBuy.getSelectedItem(), Formats.CURRENCY.parseValue(m_jPriceBuy.getText()),
                m_jCboPriceSell.getSelectedItem(), Formats.CURRENCY.parseValue(m_jPriceSell.getText()),
                m_CategoryModel.getSelectedKey() == null ? QBFCompareEnum.COMP_ISNOTNULL : QBFCompareEnum.COMP_EQUALS, m_CategoryModel.getSelectedKey(),
                QBFCompareEnum.COMP_NONE, null,
                QBFCompareEnum.COMP_EQUALS, m_LocationsModel.getSelectedKey()

            };
        } else {
            return new Object[]{
                QBFCompareEnum.COMP_NONE, null,
                QBFCompareEnum.COMP_NONE, null,
                QBFCompareEnum.COMP_NONE, null,
                QBFCompareEnum.COMP_NONE, null,
                QBFCompareEnum.COMP_RE, "%" + m_jBarcode.getText() + "%",
                QBFCompareEnum.COMP_EQUALS, m_LocationsModel.getSelectedKey()
            };
        }
    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            siteGuid = (String) m_LocationsModel.getSelectedKey();
            try {
                m_sentcat = dlSales.getCategoriesList(siteGuid);
                catlist = m_sentcat.list();
                catlist.add(0, null);
                m_CategoryModel = new ComboBoxValModel(catlist);
                m_jCategory.setModel(m_CategoryModel);
            } catch (BasicException ex) {
                Logger.getLogger(ProductFilterWithSite.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMainPanel = new javax.swing.JPanel();
        jSites = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        m_jLocation = new javax.swing.JComboBox();
        jBarcode = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        m_jBarcode = new javax.swing.JTextField();
        jByform = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        m_jCboName = new javax.swing.JComboBox();
        m_jName = new javax.swing.JTextField();
        m_jPriceBuy = new javax.swing.JTextField();
        m_jCboPriceBuy = new javax.swing.JComboBox();
        m_jCboPriceSell = new javax.swing.JComboBox();
        m_jPriceSell = new javax.swing.JTextField();
        m_jCategory = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();

        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jSites.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("label.bystore"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        jSites.setPreferredSize(new java.awt.Dimension(178, 92));

        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText(AppLocal.getIntString("label.sitename")); // NOI18N

        m_jLocation.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jLocationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jSitesLayout = new javax.swing.GroupLayout(jSites);
        jSites.setLayout(jSitesLayout);
        jSitesLayout.setHorizontalGroup(
            jSitesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jSitesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jSitesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jSitesLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(m_jLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jSitesLayout.setVerticalGroup(
            jSitesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jSitesLayout.createSequentialGroup()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 70, Short.MAX_VALUE))
            .addGroup(jSitesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jSitesLayout.createSequentialGroup()
                    .addGap(33, 33, 33)
                    .addComponent(m_jLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(35, Short.MAX_VALUE)))
        );

        jBarcode.setBorder(javax.swing.BorderFactory.createTitledBorder(null, AppLocal.getIntString("label.bybarcode"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        jBarcode.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText(AppLocal.getIntString("label.prodbarcode")); // NOI18N

        m_jBarcode.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        javax.swing.GroupLayout jBarcodeLayout = new javax.swing.GroupLayout(jBarcode);
        jBarcode.setLayout(jBarcodeLayout);
        jBarcodeLayout.setHorizontalGroup(
            jBarcodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jBarcodeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jBarcodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jBarcodeLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 83, Short.MAX_VALUE))
                    .addComponent(m_jBarcode))
                .addContainerGap())
        );
        jBarcodeLayout.setVerticalGroup(
            jBarcodeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jBarcodeLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jByform.setBorder(javax.swing.BorderFactory.createTitledBorder(null, AppLocal.getIntString("label.byform"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        jByform.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("label.prodcategory")); // NOI18N

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("label.prodpricesell")); // NOI18N

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("label.prodpricebuy")); // NOI18N

        m_jCboName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        m_jName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        m_jPriceBuy.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        m_jCboPriceBuy.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        m_jCboPriceSell.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        m_jPriceSell.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        m_jCategory.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("label.prodname")); // NOI18N

        javax.swing.GroupLayout jByformLayout = new javax.swing.GroupLayout(jByform);
        jByform.setLayout(jByformLayout);
        jByformLayout.setHorizontalGroup(
            jByformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jByformLayout.createSequentialGroup()
                .addGroup(jByformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jByformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(m_jName)
                    .addComponent(m_jCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(m_jCboName, 0, 192, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jByformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jPriceBuy, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jCboPriceBuy, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jByformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(m_jCboPriceSell, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jByformLayout.createSequentialGroup()
                        .addGroup(jByformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(m_jPriceSell, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 36, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jByformLayout.setVerticalGroup(
            jByformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jByformLayout.createSequentialGroup()
                .addGroup(jByformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jByformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_jCboPriceBuy, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jCboPriceSell, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jCboName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jByformLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m_jPriceSell, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jPriceBuy, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jMainPanelLayout = new javax.swing.GroupLayout(jMainPanel);
        jMainPanel.setLayout(jMainPanelLayout);
        jMainPanelLayout.setHorizontalGroup(
            jMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jMainPanelLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jSites, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jByform, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jMainPanelLayout.setVerticalGroup(
            jMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jMainPanelLayout.createSequentialGroup()
                .addGroup(jMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jMainPanelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSites, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jByform, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(1, 1, 1))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jMainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jMainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void m_jLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jLocationActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_m_jLocationActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jBarcode;
    private javax.swing.JPanel jByform;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jMainPanel;
    private javax.swing.JPanel jSites;
    private javax.swing.JTextField m_jBarcode;
    private javax.swing.JComboBox m_jCategory;
    private javax.swing.JComboBox m_jCboName;
    private javax.swing.JComboBox m_jCboPriceBuy;
    private javax.swing.JComboBox m_jCboPriceSell;
    public javax.swing.JComboBox m_jLocation;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextField m_jPriceBuy;
    private javax.swing.JTextField m_jPriceSell;
    // End of variables declaration//GEN-END:variables

}
