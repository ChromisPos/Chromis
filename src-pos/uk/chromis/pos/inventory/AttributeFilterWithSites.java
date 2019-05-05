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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerWrite;
import uk.chromis.data.loader.SerializerWriteString;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.reports.ReportEditorCreator;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.sync.SitesInfo;

public class AttributeFilterWithSites extends javax.swing.JPanel implements ReportEditorCreator {

    private SentenceList attsent;
    private ComboBoxValModel attmodel;
    private SentenceList m_sentSites;
    public ComboBoxValModel m_LocationsModel;
    private DataLogicSync dlSync;
    private DataLogicSales dlSales;
    private String siteGuid;

    public AttributeFilterWithSites() {
        initComponents();
    }

    @Override
    public void init(AppView app) {
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");
        dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");

        try {
            String localGuid = dlSync.getSiteGuid();
            this.siteGuid = localGuid;
            attsent = dlSales.getAttributeList(localGuid);
        } catch (BasicException ex) {
            Logger.getLogger(AttributeValuesPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        m_sentSites = dlSync.getSitesList();
        m_LocationsModel = new ComboBoxValModel();
        m_jLocation.addActionListener(new ReloadActionListener());
        attmodel = new ComboBoxValModel();
    }

    @Override
    public void activate() throws BasicException {
        attsent = dlSales.getAttributeList(siteGuid);
        List a = attsent.list();
        attmodel = new ComboBoxValModel(a);
        attmodel.setSelectedFirst();
        jAttr.setModel(attmodel);

        List s;
        try {
            s = m_sentSites.list();
        } catch (BasicException ex) {
            s = dlSync.getSingleSite().list();
        }
        /*
        if (dlSync.getCentralGuid().equals(dlSync.getSiteGuid())) {
            SitesInfo tempSite = new SitesInfo(dlSync.getCentralGuid(), dlSync.getCentralName());
            s.add(0, tempSite);
        }
        */
        addFirst(s);
        m_LocationsModel = new ComboBoxValModel(s);
        m_LocationsModel.setSelectedFirst();
        m_jLocation.setModel(m_LocationsModel);
    }

    protected void addFirst(List a) {
        // do nothing
    }

    @Override
    public SerializerWrite getSerializerWrite() {
        return SerializerWriteString.INSTANCE;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    public void addActionListener(ActionListener l) {
        jAttr.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        jAttr.removeActionListener(l);
    }

    @Override
    public Object createValue() throws BasicException {
        refresh();
        AttributeInfo att = (AttributeInfo) attmodel.getSelectedItem();
        return att == null ? null : att.getId();
    }

    private void refresh() {
        try {
            if (!this.siteGuid.equals(m_LocationsModel.getSelectedKey().toString())) {
                siteGuid = m_LocationsModel.getSelectedKey().toString();
                attsent = dlSales.getAttributeList(siteGuid);
                List a = attsent.list();
                attmodel = new ComboBoxValModel(a);
                attmodel.setSelectedFirst();
                jAttr.setModel(attmodel);
            }
        } catch (BasicException ex) {
            Logger.getLogger(AttributeFilterWithSites.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void refreshGuid(String siteGuid) {
        this.siteGuid = siteGuid;        
        //rebuild the combo models
        refresh();

    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {           
            refreshGuid(m_LocationsModel.getSelectedKey().toString());

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

        jSites = new javax.swing.JPanel();
        m_jLocation = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jAttr = new javax.swing.JComboBox();

        jSites.setBorder(javax.swing.BorderFactory.createTitledBorder(null, AppLocal.getIntString("label.bystore"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        jSites.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jSites.setPreferredSize(new java.awt.Dimension(370, 60));

        m_jLocation.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText(AppLocal.getIntString("label.sitename")); // NOI18N

        javax.swing.GroupLayout jSitesLayout = new javax.swing.GroupLayout(jSites);
        jSites.setLayout(jSitesLayout);
        jSitesLayout.setHorizontalGroup(
            jSitesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jSitesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        jSitesLayout.setVerticalGroup(
            jSitesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jSitesLayout.createSequentialGroup()
                .addGroup(jSitesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("label.attribute"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        jAttr.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jAttr, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jAttr, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSites, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSites, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jAttr;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    public javax.swing.JPanel jSites;
    public javax.swing.JComboBox m_jLocation;
    // End of variables declaration//GEN-END:variables

}
