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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.data.loader.SerializerWriteBasicExt;
import uk.chromis.data.user.EditorListener;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.panels.JPanelTable2;
import uk.chromis.pos.suppliers.DataLogicSuppliers;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.ticket.ProductFilter;
import uk.chromis.pos.ticket.ProductFilterWithSite;

public class ProductsPanel extends JPanelTable2 implements EditorListener {

    private ProductsEditor jeditor;
    private ProductFilterWithSite jproductfilterws;
    private ProductFilter jproductfilter;
    private String m_initialFilter = "";
    private DataLogicSales m_dlSales = null;
    private DataLogicSync dlSync;
    private DataLogicSuppliers dlSuppliers;

    public ProductsPanel() {
    }

    public ProductsPanel(String szFilter) {
        m_initialFilter = szFilter;
    }

    @Override
    protected void init() {
 
        m_dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");

        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        dlSuppliers = (DataLogicSuppliers) app.getBean("uk.chromis.pos.suppliers.DataLogicSuppliers");
        
        
        jproductfilterws = new ProductFilterWithSite(dlSync.isCentral());
        //jproductfilterws = new ProductFilterWithSite(true);
        jproductfilterws.init(app);
        jproductfilterws.addActionListener(new ReloadActionListener());


        lpr = new ListProviderCreator(m_dlSales.getProductCatQBF(), jproductfilterws);
       
        row = m_dlSales.getProductsRow();
        
        spr = new SaveProvider(
                m_dlSales.getProductCatUpdate(),
                m_dlSales.getProductCatInsert(),
                m_dlSales.getProductCatDelete());

        jeditor = new ProductsEditor(m_dlSales, dirty, dlSync.getSiteGuid());

        if (AppConfig.getInstance().getBoolean("display.longnames")) {
            setListWidth(300);
        }
    }

    @Override
    public EditorRecord getEditor() {
        return jeditor;
    }

    @Override
    public Component getFilter() {
        return jproductfilterws.getComponent();
    }

    @Override
    public Component getToolbarExtras() {

        JButton btnScanPal = new JButton();
        btnScanPal.setText("ScanPal");
        btnScanPal.setVisible(app.getDeviceScanner() != null);
        btnScanPal.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnScanPalActionPerformed(evt);
            }
        });

        return btnScanPal;
    }

    private void btnScanPalActionPerformed(java.awt.event.ActionEvent evt) {
        JDlgUploadProducts.showMessage(this, app.getDeviceScanner(), bd);
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Products");
    }

    @Override
    public void activate() throws BasicException {
        jeditor.activate();
        jproductfilterws.activate();

        // Speed up loading with large product sets - only load after refresh  
        // is hit which is usually after a filter is set up  
        setLoadOnActivation(false);

        super.activate();
    }

    @Override
    public void updateValue(Object value) {
    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            
            try {
                String siteGuid = jproductfilterws.m_LocationsModel.getSelectedKey().toString();
                jeditor.refreshGuid(siteGuid);
                jproductfilterws.refreshGuid(siteGuid);
                ProductsPanel.this.bd.actionLoad();
            } catch (BasicException w) {
            }
        }
    }

}
