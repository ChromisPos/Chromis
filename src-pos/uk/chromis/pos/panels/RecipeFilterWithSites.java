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

package uk.chromis.pos.panels;

import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.loader.SerializerWrite;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.reports.ReportEditorCreator;
import uk.chromis.pos.ticket.ProductInfoExt;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;
import java.util.List;
import javax.swing.event.EventListenerList;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.sync.SitesInfo;

public class RecipeFilterWithSites extends javax.swing.JPanel implements ReportEditorCreator {

    private ProductInfoExt product;
    private DataLogicSales m_dlSales;
    private String siteGuid;
    private DataLogicSync dlSync;
    private SentenceList m_sentSites;
    private ComboBoxValModel m_SitesModel;

    protected EventListenerList listeners = new EventListenerList();

    public RecipeFilterWithSites() {

        initComponents();
    }

    @Override
    public void init(AppView app) {
        m_dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");
        siteGuid = dlSync.getSiteGuid();

        m_sentSites = dlSync.getSitesList();
        m_SitesModel = new ComboBoxValModel();
        m_jSite.addActionListener(new ReloadActionListener());

    }

    @Override
    public void activate() throws BasicException {
        List a;
        try {
            a = m_sentSites.list();
        } catch (BasicException ex) {
            a = dlSync.getSingleSite().list();
        }

        /*
        if (dlSync.getCentralGuid().equals(dlSync.getSiteGuid())) {
            SitesInfo tempSite = new SitesInfo(dlSync.getCentralGuid(), dlSync.getCentralName());
            a.add(0, tempSite);
        }
         */
        addFirst(a);
        m_SitesModel = new ComboBoxValModel(a);
        m_SitesModel.setSelectedFirst();
        m_jSite.setModel(m_SitesModel);

        product = null;
        m_jSearch.setText(null);
        m_jBarcode1.setText(null);
        m_jReference1.setText(null);
    }

    protected void addFirst(List a) {
        // do nothing
    }

    @Override
    public SerializerWrite getSerializerWrite() {
        return new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING});
    }

    public void addActionListener(ActionListener l) {
        listeners.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
        listeners.remove(ActionListener.class, l);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Object createValue() throws BasicException {
        if (product == null) {
            return new Object[]{siteGuid, null};
        } else {
            return new Object[]{siteGuid, product.getID()};
        }
    }

    public ProductInfoExt getProductInfoExt() {
        return product;
    }

    private void assignProduct(ProductInfoExt prod) {
        product = prod;
        if (product == null) {
            m_jSearch.setText(null);
            m_jBarcode1.setText(null);
            m_jReference1.setText(null);
        } else {
            m_jSearch.setText(product.getReference() + " - " + product.getName());
            m_jBarcode1.setText(product.getCode());
            m_jReference1.setText(product.getReference());
        }

        fireSelectedProduct();
    }

    protected void fireSelectedProduct() {
        EventListener[] l = listeners.getListeners(ActionListener.class);
        ActionEvent e = null;
        for (int i = 0; i < l.length; i++) {
            if (e == null) {
                e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "SELECTED");
            }
            ((ActionListener) l[i]).actionPerformed(e);
        }
    }

    private void assignProductByCode() {
        try {
            ProductInfoExt prod = m_dlSales.getProductInfoByCode(m_jBarcode1.getText(), siteGuid);
            if (prod == null) {
                Toolkit.getDefaultToolkit().beep();
            }
            assignProduct(prod);
        } catch (BasicException eData) {
            MessageInf msg = new MessageInf(eData);
            msg.show(this);
            assignProduct(null);
        }
    }

    private void assignProductByReference() {
        try {
            ProductInfoExt prod = m_dlSales.getProductInfoByReference(m_jReference1.getText(), siteGuid);
            if (prod == null) {
                Toolkit.getDefaultToolkit().beep();
            }
            assignProduct(prod);
        } catch (BasicException eData) {
            MessageInf msg = new MessageInf(eData);
            msg.show(this);
            assignProduct(null);
        }
    }

    public String getGuid() {
        return siteGuid;
    }

    public String getSelectKey() {
        return m_SitesModel.getSelectedKey().toString();
    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            siteGuid = m_SitesModel.getSelectedKey().toString();
            assignProduct(null);
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

        jProductPanel = new javax.swing.JPanel();
        jSites = new javax.swing.JPanel();
        jSitePanel = new javax.swing.JPanel();
        m_jSite = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jProduct = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        m_jReference1 = new javax.swing.JTextField();
        Enter1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        m_jBarcode1 = new javax.swing.JTextField();
        Enter2 = new javax.swing.JButton();
        m_jSearch = new javax.swing.JTextField();
        search = new javax.swing.JButton();

        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setPreferredSize(new java.awt.Dimension(710, 100));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jProductPanel.setPreferredSize(new java.awt.Dimension(720, 70));

        jSitePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, AppLocal.getIntString("label.bystore"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        jSitePanel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSitePanel.setPreferredSize(new java.awt.Dimension(370, 60));

        m_jSite.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText(AppLocal.getIntString("label.sitename")); // NOI18N

        javax.swing.GroupLayout jSitePanelLayout = new javax.swing.GroupLayout(jSitePanel);
        jSitePanel.setLayout(jSitePanelLayout);
        jSitePanelLayout.setHorizontalGroup(
            jSitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jSitePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jSite, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jSitePanelLayout.setVerticalGroup(
            jSitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jSitePanelLayout.createSequentialGroup()
                .addGroup(jSitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jSite, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jSitesLayout = new javax.swing.GroupLayout(jSites);
        jSites.setLayout(jSitesLayout);
        jSitesLayout.setHorizontalGroup(
            jSitesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jSitesLayout.createSequentialGroup()
                .addComponent(jSitePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 10, Short.MAX_VALUE))
        );
        jSitesLayout.setVerticalGroup(
            jSitesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jSitesLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jSitePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jProduct.setBorder(javax.swing.BorderFactory.createTitledBorder(AppLocal.getIntString("label.byproduct"))); // NOI18N
        jProduct.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jProduct.setMaximumSize(new java.awt.Dimension(697, 123));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText(AppLocal.getIntString("label.prodref")); // NOI18N
        jLabel6.setMaximumSize(new java.awt.Dimension(50, 20));
        jLabel6.setMinimumSize(new java.awt.Dimension(50, 20));
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 25));

        m_jReference1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jReference1.setPreferredSize(new java.awt.Dimension(150, 25));
        m_jReference1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jReference1ActionPerformed(evt);
            }
        });

        Enter1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/products24.png"))); // NOI18N
        Enter1.setToolTipText("Enter Product ID");
        Enter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Enter1ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText(AppLocal.getIntString("label.prodbarcode")); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(70, 25));

        m_jBarcode1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jBarcode1.setPreferredSize(new java.awt.Dimension(150, 25));
        m_jBarcode1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jBarcode1ActionPerformed(evt);
            }
        });

        Enter2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/barcode.png"))); // NOI18N
        Enter2.setToolTipText("Get Barcode");
        Enter2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Enter2ActionPerformed(evt);
            }
        });

        m_jSearch.setEditable(false);
        m_jSearch.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        m_jSearch.setFocusable(false);
        m_jSearch.setPreferredSize(new java.awt.Dimension(200, 25));
        m_jSearch.setRequestFocusEnabled(false);

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/search24.png"))); // NOI18N
        search.setToolTipText("Search Products");
        search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jProductLayout = new javax.swing.GroupLayout(jProduct);
        jProduct.setLayout(jProductLayout);
        jProductLayout.setHorizontalGroup(
            jProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jProductLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(search, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jProductLayout.createSequentialGroup()
                        .addComponent(m_jReference1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Enter1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jBarcode1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jProductLayout.createSequentialGroup()
                        .addComponent(m_jSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Enter2, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );
        jProductLayout.setVerticalGroup(
            jProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jProductLayout.createSequentialGroup()
                .addGroup(jProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jProductLayout.createSequentialGroup()
                        .addGroup(jProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(m_jReference1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(Enter1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(search)
                            .addComponent(m_jSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jProductLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(m_jBarcode1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(Enter2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 5, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jProductPanelLayout = new javax.swing.GroupLayout(jProductPanel);
        jProductPanel.setLayout(jProductPanelLayout);
        jProductPanelLayout.setHorizontalGroup(
            jProductPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jProductPanelLayout.createSequentialGroup()
                .addComponent(jSites, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 330, Short.MAX_VALUE))
            .addGroup(jProductPanelLayout.createSequentialGroup()
                .addComponent(jProduct, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jProductPanelLayout.setVerticalGroup(
            jProductPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jProductPanelLayout.createSequentialGroup()
                .addComponent(jSites, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 4, Short.MAX_VALUE))
        );

        add(jProductPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 710, 170));

        getAccessibleContext().setAccessibleName("rootpanel");
    }// </editor-fold>//GEN-END:initComponents

    private void m_jReference1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jReference1ActionPerformed
        this.assignProductByReference();
    }//GEN-LAST:event_m_jReference1ActionPerformed

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        assignProduct(JProductFinder.showMessage(this, m_dlSales, JProductFinder.PRODUCT_NORMAL, siteGuid));

}//GEN-LAST:event_searchActionPerformed

    private void Enter2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Enter2ActionPerformed
        this.assignProductByCode();
    }//GEN-LAST:event_Enter2ActionPerformed

    private void Enter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Enter1ActionPerformed
        this.assignProductByReference();
    }//GEN-LAST:event_Enter1ActionPerformed

    private void m_jBarcode1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jBarcode1ActionPerformed
        this.assignProductByCode();
    }//GEN-LAST:event_m_jBarcode1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Enter1;
    private javax.swing.JButton Enter2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jProduct;
    private javax.swing.JPanel jProductPanel;
    private javax.swing.JPanel jSitePanel;
    public javax.swing.JPanel jSites;
    private javax.swing.JTextField m_jBarcode1;
    private javax.swing.JTextField m_jReference1;
    private javax.swing.JTextField m_jSearch;
    public javax.swing.JComboBox m_jSite;
    private javax.swing.JButton search;
    // End of variables declaration//GEN-END:variables

}
