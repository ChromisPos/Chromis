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

//
package uk.chromis.pos.ticket;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
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
import uk.chromis.pos.panels.ComboItemLocal;
import uk.chromis.pos.reports.JParamsLocationWithStockLevel;
import uk.chromis.pos.reports.ReportEditorCreator;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.sync.SitesInfo;
import uk.chromis.pos.util.SessionFactory;

/**
 *
 * @author John
 */
public class ProductFilterReportWithCat extends javax.swing.JPanel implements ReportEditorCreator {

    private SentenceList m_sentcat;
    private ComboBoxValModel m_CategoryModel;
    private ComboBoxValModel m_ProductNameModel;
    private final JPanel panel;
    private final JLabel jLabel1;
    private final JLabel jLabel2;
    private final JLabel jLabel3;
    private final JLabel jLabel4;
    private final JLabel jLabel5;
    private final JLabel jLabel6;
    private final JLabel jLabel7;
    private final JLabel jLabel8;
    private final JLabel jLabel9;
    private final JTextField m_jBarcode = new JTextField();
    private final JComboBox m_jCategory = new JComboBox();
    private final JComboBox m_jCboName = new JComboBox();
    private final JComboBox m_jCatalog = new JComboBox();
    private final JComboBox m_jCboPriceBuy = new JComboBox();
    private final JComboBox m_jCboPriceSell = new JComboBox();
    private ComboBoxValModel m_jIcatalogue = new ComboBoxValModel();
    private final JTextField m_jName = new JTextField();
    private final JTextField m_jPriceBuy = new JTextField();
    private final JTextField m_jPriceSell = new JTextField();
    private String siteGuid;
    private final JComboBox m_jLocation = new JComboBox();
    private final JComboBox m_jstock = new JComboBox();
    private JComboBox m_jSite = new JComboBox();
    private final JTextField jStockLevel = new JTextField();

    private SentenceList m_sentlocations;
    private SentenceList m_sentSites;
    private ComboBoxValModel m_LocationsModel;
    private ComboBoxValModel m_StockLevelModel;
    public ComboBoxValModel m_SiteModel;

    private DataLogicSales dlSales;
    private DataLogicSync dlSync;
    private JParamsLocationWithStockLevel jParams;
    private Boolean bLocation = true;
    private Boolean bStock = true;

    public ProductFilterReportWithCat() {
        this("", true, true);
    }

    public ProductFilterReportWithCat(Boolean bLocation, Boolean bStock) {
        this("", bLocation, bStock);
    }

    public ProductFilterReportWithCat(String siteGuid, Boolean bLocation, Boolean bStock) {
        this.bLocation = bLocation;
        this.bStock = bStock;
        if (siteGuid.equals("")) {
            dlSync = new DataLogicSync();
            dlSync.init(SessionFactory.getInstance().getSession());
            this.siteGuid = dlSync.getSiteGuid();
        } else {
            this.siteGuid = siteGuid;
        }

        Font font = new Font("Arial", 0, 12);
        setFont(font);
        panel = new JPanel(new MigLayout());

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(319, Short.MAX_VALUE)
                ));

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jLabel1 = new JLabel(AppLocal.getIntString("label.prodcategory"));
        jLabel1.setFont(font);

        jLabel2 = new JLabel(AppLocal.getIntString("label.prodname"));
        jLabel2.setFont(font);

        jLabel3 = new JLabel(AppLocal.getIntString("label.prodpricesellexcludingtax"));
        jLabel3.setFont(font);

        jLabel4 = new JLabel(AppLocal.getIntString("label.prodpricebuy"));
        jLabel4.setFont(font);

        jLabel5 = new JLabel(AppLocal.getIntString("label.prodbarcode"));
        jLabel5.setFont(font);

        jLabel6 = new JLabel(AppLocal.getIntString("label.CatalogueStatus"));
        jLabel6.setFont(font);

        jLabel7 = new JLabel(AppLocal.getIntString("label.sitename"));
        jLabel7.setFont(font);

        jLabel8 = new JLabel(AppLocal.getIntString("label.warehouse"));
        jLabel8.setFont(font);

        jLabel9 = new JLabel(AppLocal.getIntString("label.stocklevel"));
        jLabel9.setFont(font);

        m_jBarcode.setFont(font);
        m_jBarcode.setPreferredSize(new Dimension(200, 25));
        m_jCategory.setFont(font);
        m_jCboName.setFont(font);
        m_jName.setFont(font);
        m_jName.setPreferredSize(new Dimension(200, 25));

        m_jPriceBuy.setFont(font);
        m_jPriceBuy.setPreferredSize(new Dimension(60, 25));

        m_jCboPriceBuy.setFont(font);
        m_jCboPriceSell.setFont(font);

        m_jPriceSell.setFont(font);
        m_jPriceSell.setPreferredSize(new Dimension(60, 25));

        jStockLevel.setFont(font);
        jStockLevel.setPreferredSize(new Dimension(60, 25));

        jLabel2.setFont(font);
        jLabel2.setText(AppLocal.getIntString("label.prodname"));

        panel.add(jLabel1, "w 80");
        panel.add(m_jCategory, "w 220, gapright 5");
        panel.add(jLabel4, "w 90");

        panel.add(m_jCboPriceBuy, "w 120, split 2");
        panel.add(m_jPriceBuy, "w 50, gapright 10");

        panel.add(jLabel5, "w 80");
        panel.add(m_jBarcode, "w 220, wrap");

        panel.add(jLabel2, "w 80");
        panel.add(m_jCboName, "w 220");

        panel.add(jLabel3, "w 90");
        panel.add(m_jCboPriceSell, "w 120,split 2 ");
        panel.add(m_jPriceSell, "w 50, gapright 10");

        panel.add(jLabel6, "w 90");
        panel.add(m_jCatalog, "wrap");

        panel.add(new JLabel(), "w 80");
        panel.add(m_jName, "w 220,  wrap");

        if (dlSync.isCentral()) {
            panel.add(jLabel7, "w 80");
            panel.add(m_jSite, "w 220, gapright 5");
        }

        if (bLocation) {
            panel.add(jLabel8, "w 90");
            panel.add(jLabel8, "w 80");
            panel.add(m_jLocation, "w 220, gapright 5");
        }

        if (bStock) {           
            panel.add(jLabel9, "w 90");
            panel.add(m_jstock, "w 120, split 2");
            panel.add(jStockLevel, "w 50, gapright 10");
        }
    }

    @Override
    public void init(AppView app) {

        dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        m_sentSites = dlSync.getSitesList();
        m_SiteModel = new ComboBoxValModel();

        m_jSite.addActionListener(new ReloadActionListener());
        m_sentcat = dlSales.getCategoriesList(siteGuid);
        m_CategoryModel = new ComboBoxValModel();

        m_sentlocations = dlSales.getLocationsList(dlSync.getSiteGuid());
        m_LocationsModel = new ComboBoxValModel();

        m_jCboName.setModel(ListQBFModelNumber.getNonMandatoryProduct());
        m_jCboPriceBuy.setModel(ListQBFModelNumber.getNonMandatoryPrice());
        m_jCboPriceSell.setModel(ListQBFModelNumber.getNonMandatoryPrice());
        m_jIcatalogue.add(null);
        m_jIcatalogue.add(new ComboItemLocal(1, AppLocal.getIntString("label.CatalogueStatusYes")));
        m_jIcatalogue.add(new ComboItemLocal(2, AppLocal.getIntString("label.CatalogueStatusNo")));
        m_jCatalog.setModel(m_jIcatalogue);
    }

    @Override
    public void activate() throws BasicException {
        List a;
        try {
            a = m_sentSites.list();
        } catch (BasicException ex) {
            a = dlSync.getSingleSite().list();
        }
        if (dlSync.isCentral()) {
            SitesInfo tempSite = new SitesInfo(null, "All Sites");
            a.add(0, tempSite);
        }
        addFirst(a);
        m_SiteModel = new ComboBoxValModel(a);
        m_SiteModel.setSelectedFirst();
        m_jSite.setModel(m_SiteModel);

        List catlist = m_sentcat.list();
        catlist.add(0, null);
        m_CategoryModel = new ComboBoxValModel(catlist);
        m_jCategory.setModel(m_CategoryModel);

        List locList = m_sentlocations.list();
        locList.add(0, null);
        m_LocationsModel = new ComboBoxValModel(locList);
        m_LocationsModel.setSelectedFirst();
        m_jLocation.setModel(m_LocationsModel);

        m_StockLevelModel = new ComboBoxValModel();
        m_StockLevelModel.add(null);
        m_StockLevelModel.add(QBFCompareEnum.COMP_EQUALS);
        m_StockLevelModel.add(QBFCompareEnum.COMP_GREATER);
        m_StockLevelModel.add(QBFCompareEnum.COMP_LESS);
        m_jstock.setModel(m_StockLevelModel);

    }

    protected void addFirst(List a) {
        // do nothing
    }

    @Override
    public SerializerWrite getSerializerWrite() {
        return new SerializerWriteBasic(
                new Datas[]{Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.DOUBLE,
                    Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING,
                    Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.BOOLEAN,
                    Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.INT,
                    Datas.OBJECT, Datas.STRING});
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Object createValue() throws BasicException {

        Integer key = (Integer) m_jIcatalogue.getSelectedKey();
        Boolean bVal = (key == null || key == 2) ? false : true;

        if (m_jBarcode.getText() == null || m_jBarcode.getText().equals("")) {
       
            return new Object[]{
                //Name
                m_jCboName.getSelectedItem() == null ? QBFCompareEnum.COMP_NONE : m_jCboName.getSelectedItem(), m_jName.getText(),
                //Buy price
                m_jCboPriceBuy.getSelectedItem() == null ? QBFCompareEnum.COMP_NONE : m_jCboPriceBuy.getSelectedItem(), Formats.CURRENCY.parseValue(m_jPriceBuy.getText()), 
                //Sell price
                m_jCboPriceSell.getSelectedItem() == null ? QBFCompareEnum.COMP_NONE : m_jCboPriceSell.getSelectedItem(), Formats.CURRENCY.parseValue(m_jPriceSell.getText()), 
                //Category
                m_CategoryModel.getSelectedKey() == null ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_EQUALS, m_CategoryModel.getSelectedKey(), 
                //Barcode
                QBFCompareEnum.COMP_NONE, null, 
                //Is in catalogue
                m_jIcatalogue.getSelectedItem() == null ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_EQUALS, bVal,
                //Location details
                m_LocationsModel.getSelectedKey() == null ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_EQUALS, m_LocationsModel.getSelectedKey(),
                //Stock level
                m_StockLevelModel.getSelectedItem() == null ? QBFCompareEnum.COMP_NONE : m_StockLevelModel.getSelectedItem(), Formats.INT.parseValue(jStockLevel.getText()),
                //SiteGUID
                m_SiteModel.getSelectedText().equals("All Sites") ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_EQUALS, m_SiteModel.getSelectedKey()
            };
        } else {                        
            
            return new Object[]{
                //Name
                QBFCompareEnum.COMP_NONE, null, 
                //Buy Price
                QBFCompareEnum.COMP_NONE, null, 
                //Sell Price
                QBFCompareEnum.COMP_NONE, null, 
                //Category
                QBFCompareEnum.COMP_NONE, null,
                //Barcode
                QBFCompareEnum.COMP_RE, "%" + m_jBarcode.getText() + "%", //Barcode
                //Is in catalogue
                QBFCompareEnum.COMP_NONE, null,
                //Location details
                m_LocationsModel.getSelectedKey() == null ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_EQUALS, m_LocationsModel.getSelectedKey(),
               // m_StockLevelModel.getSelectedItem() == null ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_EQUALS, m_StockLevelModel.getSelectedKey(),
                //Stock Level
                m_StockLevelModel.getSelectedItem() == null ? QBFCompareEnum.COMP_NONE : m_StockLevelModel.getSelectedItem(), Formats.INT.parseValue(jStockLevel.getText()),
                //SiteGuid
                m_SiteModel.getSelectedText().equals("All Sites") ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_EQUALS, m_SiteModel.getSelectedKey()

            };
        }
    }

    private void refresh(String siteGuid) {
        try {
            if (!this.siteGuid.equals(siteGuid)) {
                m_sentcat = dlSales.getCategoriesList(siteGuid);
                m_CategoryModel = new ComboBoxValModel();

                List catlist = m_sentcat.list();
                catlist.add(0, null);
                m_CategoryModel = new ComboBoxValModel(catlist);
                m_jCategory.setModel(m_CategoryModel);

                m_sentlocations = dlSales.getLocationsList(siteGuid);
                List locList = m_sentlocations.list();
                locList.add(0, null);
                m_LocationsModel = new ComboBoxValModel(locList);
                m_jLocation.setModel(m_LocationsModel);

            }
        } catch (BasicException ex) {
            Logger.getLogger(JParamsLocationWithStockLevel.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        this.siteGuid = siteGuid;

    }

    private class ReloadActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {           
            if (m_SiteModel.getSelectedKey() == null) {
                refresh(dlSync.getSiteGuid());
            } else {
                refresh(m_SiteModel.getSelectedKey().toString());
            }
        }
    }

}
