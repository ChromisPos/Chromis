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
package uk.chromis.pos.reports;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerWrite;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.sync.DataLogicSync;

public class JParamsLocationwithSites extends javax.swing.JPanel implements ReportEditorCreator {
    
    private SentenceList m_sentlocations;
    private ComboBoxValModel m_LocationsModel;
    private String siteGuid;
    private SentenceList m_sentSites;
    private ComboBoxValModel m_SitesModel;
    private DataLogicSync dlSync;
    private DataLogicSales dlSales;
    
    public JParamsLocationwithSites() {
        initComponents();
    }
    
    @Override
    public void init(AppView app) {
        
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");
        dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");
        
        siteGuid = dlSync.getSiteGuid();
        
        m_sentSites = dlSync.getSitesList();
        m_jSite.addActionListener(new ReloadActionListener());
        
        m_sentlocations = dlSales.getLocationsList(siteGuid);
        m_LocationsModel = new ComboBoxValModel();
    }
    
    @Override
    public void activate() throws BasicException {
        List a = m_sentlocations.list();
        addFirst(a);
        m_LocationsModel = new ComboBoxValModel(a);
        m_LocationsModel.setSelectedFirst();
        m_jLocation.setModel(m_LocationsModel);
        
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
        m_SitesModel = new ComboBoxValModel(s);
        m_SitesModel.setSelectedFirst();
        m_jSite.setModel(m_SitesModel);
        
    }
    
    @Override
    public SerializerWrite getSerializerWrite() {
        return new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING});
    }
    
    @Override
    public Component getComponent() {
        return this;
    }
    
    protected void addFirst(List a) {
        // do nothing
    }

    public void showSites(Boolean hide) {
        jPanel2.setVisible(hide);
    }
    
    private void refresh() {
        try {
            if (!this.siteGuid.equals(m_SitesModel.getSelectedKey().toString())) {
                siteGuid = m_SitesModel.getSelectedKey().toString();
                m_sentlocations = dlSales.getLocationsList(siteGuid);
                List a = m_sentlocations.list();
                addFirst(a);
                m_LocationsModel = new ComboBoxValModel(a);
                m_LocationsModel.setSelectedFirst();
                m_jLocation.setModel(m_LocationsModel);
            }
        } catch (BasicException ex) {
            Logger.getLogger(JParamsLocationwithSites.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void refreshGuid(String siteGuid) {
        this.siteGuid = siteGuid;
        //rebuild the combo models
        refresh();
        
    }
    
    public void addActionListener(ActionListener l) {
        m_jSite.addActionListener(l);
    }
    
    public void removeActionListener(ActionListener l) {
        m_jSite.removeActionListener(l);
    }
    
    private class ReloadActionListener implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            refreshGuid(m_SitesModel.getSelectedKey().toString());
            refresh();
            
        }
    }
    
    public String getGuid() {
        return this.siteGuid;
    }
  
    
    @Override
    public Object createValue() throws BasicException {
        refresh();
        return new Object[]{
            m_LocationsModel.getSelectedKey().toString(), m_SitesModel.getSelectedKey().toString()
        };
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        m_jSite = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        m_jLocation = new javax.swing.JComboBox();

        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setPreferredSize(new java.awt.Dimension(370, 60));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, AppLocal.getIntString("label.bystore"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        jPanel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(370, 60));

        m_jSite.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText(AppLocal.getIntString("label.sitename")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jSite, 0, 220, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jSite, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("label.locationname"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        m_jLocation.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        m_jLocation.setMinimumSize(new java.awt.Dimension(30, 23));
        m_jLocation.setPreferredSize(new java.awt.Dimension(30, 23));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_jLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(m_jLocation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    public javax.swing.JComboBox m_jLocation;
    public javax.swing.JComboBox m_jSite;
    // End of variables declaration//GEN-END:variables

}
