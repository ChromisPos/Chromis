/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c)2015-2018
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
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.sales.TaxesLogic;
import uk.chromis.pos.suppliers.DataLogicSuppliers;
import uk.chromis.pos.util.BarcodeValidator;
import uk.chromis.pos.ticket.ProductInfoExt;
import uk.chromis.pos.util.AutoCompleteComboBox;
import uk.chromis.data.loader.SessionFactory;

public final class ProductsEditor extends JPanel implements EditorRecord {

    private ComboBoxValModel m_SupplierModel;
    private SentenceList m_sentsuppliers;

    private SentenceList m_sentcat;
    private ComboBoxValModel m_CategoryModel;
    private SentenceList m_sentpromotion;
    private ComboBoxValModel m_PromotionModel;
    private SentenceList taxcatsent;
    private ComboBoxValModel taxcatmodel;
    private final SentenceList attsent;
    private ComboBoxValModel attmodel;
    private SentenceList taxsent;
    private TaxesLogic taxeslogic;
    private final ComboBoxValModel m_CodetypeModel;
    private SentenceList packproductsent;
    private ComboBoxValModel packproductmodel;
    private Object m_id;
    private Object pricesell;
    private boolean priceselllock = false;
    private boolean reportlock = false;
    private BarcodeValidator validate;
    private final DataLogicSales m_dlSales;
    private String siteGuid;
    private final DataLogicSales dlSales;
    private final DataLogicSuppliers dlSuppliers;

    public ProductsEditor(DataLogicSales dlSales, DirtyManager dirty, String localGuid) {
        initComponents();
        
        m_jPtrOverride.setVisible(false);
        m_jPtrOverride.setSelected(false);
        dlSuppliers = new DataLogicSuppliers();
        dlSuppliers.init(SessionFactory.getInstance().getSession());

        siteGuid = localGuid;
        this.dlSales = dlSales;

        m_dlSales = dlSales;
        m_sentsuppliers = dlSuppliers.getSuppliersList(siteGuid);

        taxsent = dlSales.getTaxList(siteGuid);
        m_sentcat = dlSales.getCategoriesList(siteGuid);
        m_CategoryModel = new ComboBoxValModel();
        m_SupplierModel = new ComboBoxValModel();
        m_sentpromotion = dlSales.getPromotionsList(siteGuid);
        m_PromotionModel = new ComboBoxValModel();

        taxcatsent = dlSales.getTaxCategoriesList(siteGuid);
        taxcatmodel = new ComboBoxValModel();
        attsent = dlSales.getAttributeSetList();
        attmodel = new ComboBoxValModel();
        m_CodetypeModel = new ComboBoxValModel();
        m_CodetypeModel.add(null);
        m_CodetypeModel.add(CodeType.EAN13);
        m_CodetypeModel.add(CodeType.CODE128);
        packproductsent = dlSales.getPackProductList(siteGuid);
        packproductmodel = new ComboBoxValModel();
        m_jRef.getDocument().addDocumentListener(dirty);
        m_jCode.getDocument().addDocumentListener(dirty);
        m_jName.getDocument().addDocumentListener(dirty);
        m_jComment.addActionListener(dirty);
        m_jScale.addActionListener(dirty);
        m_jCategory.addActionListener(dirty);
        m_jSuppliers.addActionListener(dirty);
        jComboBoxPromotion.addActionListener(dirty);
        m_jTax.addActionListener(dirty);
        m_jAtt.addActionListener(dirty);
        m_jPriceBuy.getDocument().addDocumentListener(dirty);
        m_jPriceSell.getDocument().addDocumentListener(dirty);
        mImage.addPropertyChangeListener("image", dirty);
        m_jstockcost.getDocument().addDocumentListener(dirty);
        m_jstockvolume.getDocument().addDocumentListener(dirty);
        m_jInCatalog.addActionListener(dirty);
        m_jCatalogOrder.getDocument().addDocumentListener(dirty);
        mProperties.getDocument().addDocumentListener(dirty);
        m_jKitchen.addActionListener(dirty);
        m_jService.addActionListener(dirty);
        m_jVprice.addActionListener(dirty);
        m_jVerpatrib.addActionListener(dirty);
        m_jTextTip.getDocument().addDocumentListener(dirty);
        m_jDisplay.getDocument().addDocumentListener(dirty);
        m_jStockUnits.getDocument().putProperty(dlSales, 24);
        FieldsManager fm = new FieldsManager();
        m_jPriceBuy.getDocument().addDocumentListener(fm);
        m_jTax.addActionListener(fm);
        m_jIsPack.addActionListener(dirty);
        m_jPackQuantity.getDocument().addDocumentListener(dirty);
        m_jPackProduct.addActionListener(dirty);
        m_jPackProduct.addActionListener(fm);
        m_jCheckWarrantyReceipt.addActionListener(dirty);
        m_jAlias.getDocument().addDocumentListener(dirty);
        m_jAlwaysAvailable.addActionListener(dirty);
        m_jDiscounted.addActionListener(dirty);
        m_jManageStock.addActionListener(dirty);
        m_jCommission.getDocument().addDocumentListener(dirty);
        m_jSuppliers.addActionListener(dirty);

        m_jPriceSell.getDocument().addDocumentListener(new PriceSellManager());
        m_jPriceSellTax.getDocument().addDocumentListener(new PriceTaxManager());
        m_jmargin.getDocument().addDocumentListener(new MarginManager());
        m_jGrossProfit.getDocument().addDocumentListener(new MarginManager());

        m_jRemoteDisplay.addActionListener(dirty);
        m_jDefaultPtr.addChangeListener(dirty);
        m_jDefaultScreen.addChangeListener(dirty);

        m_jPtrOverride.addActionListener(dirty);
      

        writeValueEOF();
    }

    public void activate() throws BasicException {
        List b;
        b = m_sentsuppliers.list();

        m_SupplierModel = new ComboBoxValModel(b);
        m_SupplierModel.add(0, null);
        m_jSuppliers.setModel(m_SupplierModel);

        // Load the taxes logic
        taxeslogic = new TaxesLogic(taxsent.list());

        m_CategoryModel = new ComboBoxValModel(m_sentcat.list());
        m_jCategory.setModel(m_CategoryModel);

        m_PromotionModel = new ComboBoxValModel(m_sentpromotion.list());
        jComboBoxPromotion.setModel(m_PromotionModel);

        taxcatmodel = new ComboBoxValModel(taxcatsent.list());
        m_jTax.setModel(taxcatmodel);

        attmodel = new ComboBoxValModel(attsent.list());
        attmodel.add(0, null);
        m_jAtt.setModel(attmodel);

        packproductmodel = new ComboBoxValModel(packproductsent.list());
        m_jPackProduct.setModel(packproductmodel);

        AutoCompleteComboBox.enable(m_jPackProduct);
    }

    protected void addFirst(List b) {
        // do nothing
    }

    @Override
    public void refreshGuid(String guid) {
        siteGuid = guid;

        taxsent = dlSales.getTaxList(siteGuid);
        m_sentcat = dlSales.getCategoriesList(siteGuid);
        m_sentpromotion = dlSales.getPromotionsList(siteGuid);
        taxcatsent = dlSales.getTaxCategoriesList(siteGuid);
        packproductsent = dlSales.getPackProductList(siteGuid);
        try {
            activate();
        } catch (BasicException ex) {
            Logger.getLogger(ProductsEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

// Set the product to be edited.  
    public void setProduct(String productID, String barcode) {
        try {
            if (productID != null) {
                ProductInfoExt info = m_dlSales.getProductInfo(productID, siteGuid);

                Object[] myprod = new Object[DataLogicSales.FIELD_COUNT];

                m_id = productID;
                m_jRef.setText(info.getReference());
                m_jCode.setText(info.getCode());
                m_jName.setText(info.getName());
                m_jComment.setSelected(info.isCom());
                m_jScale.setSelected(info.isScale());
                m_jPriceBuy.setText(Formats.CURRENCY.formatValue(info.getPriceBuy()));
                m_jPriceSell.setText(Formats.CURRENCY.formatValue(info.getPriceSell()));
                m_jCommission.setText(Formats.DOUBLE.formatValue(info.getCommission()));
                setPriceSell(info.getPriceSell());
                m_CategoryModel.setSelectedKey(info.getCategoryID());
                jComboBoxPromotion.setEnabled(true);

                String promID = info.getPromotionID();
                if (promID != null && !promID.isEmpty()) {
                    jCheckBoxPromotion.setSelected(true);
                } else {
                    jCheckBoxPromotion.setSelected(false);
                }
                m_PromotionModel.setSelectedKey(promID);

                m_PromotionModel.setSelectedKey(info.getPromotionID());
                taxcatmodel.setSelectedKey(info.getTaxCategoryID());
                attmodel.setSelectedKey(info.getAttributeSetID());
                mImage.setImage(info.getImage());
                m_jstockcost.setText(Formats.CURRENCY.formatValue(info.getStockCost()));
                m_jstockvolume.setText(Formats.DOUBLE.formatValue(info.getStockVolume()));
                m_jInCatalog.setSelected(info.getInCatalog());
                m_jCatalogOrder.setText(Formats.INT.formatValue(info.getCatOrder()));
                m_jKitchen.setSelected(info.isKitchen());
                m_jService.setSelected(info.isService());
                m_jDisplay.setText(info.getDisplay());
                m_jVprice.setSelected(info.isVprice());
                m_jVerpatrib.setSelected(info.isVerpatrib());
                m_jTextTip.setText(info.getTextTip());
                m_jCheckWarrantyReceipt.setSelected(info.getWarranty());
                m_jStockUnits.setText(Formats.DOUBLE.formatValue(info.getStockUnits()));
                m_jAlias.setText(info.getAlias());
                m_jAlwaysAvailable.setSelected(info.getAlwaysAvailable());
                m_jDiscounted.setSelected(info.getCanDiscount());
                m_jManageStock.setSelected(info.getManageStock());
                m_jIsPack.setSelected(info.getIsPack());
                m_jPackQuantity.setText(Formats.DOUBLE.formatValue(info.getPackQuantity()));
                packproductmodel.setSelectedKey(info.getPromotionID());
                m_SupplierModel.setSelectedKey(info.getSupplier());
                m_jPtrOverride.setSelected(info.getPtrOverride());         
            } else if (barcode != null) {
                m_jRef.setText(barcode);
                m_jCode.setText(barcode);

            }

        } catch (BasicException ex) {
            Logger.getLogger(ProductsEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getID() {
        return (String) m_id;
    }

    @Override
    public void refresh() {
    }

    @Override
    public void writeValueEOF() {

        reportlock = true;

        m_jTitle.setText(AppLocal.getIntString("label.recordeof"));
        m_id = null;
        m_jRef.setText(null);
        m_jCode.setText(null);
        m_jName.setText(null);
        m_jComment.setSelected(false);
        m_jScale.setSelected(false);
        m_CategoryModel.setSelectedKey(null);
        m_PromotionModel.setSelectedKey(null);
        jCheckBoxPromotion.setSelected(false);
        taxcatmodel.setSelectedKey(null);
        attmodel.setSelectedKey(null);
        m_jPriceBuy.setText(null);
        m_jCommission.setText(null);
        setPriceSell(null);
        mImage.setImage(null);
        m_jstockcost.setText(null);
        m_jstockvolume.setText(null);
        m_jInCatalog.setSelected(false);
        m_jCatalogOrder.setText(null);
        mProperties.setText(null);
        m_jKitchen.setSelected(false);
        m_jService.setSelected(false);
        m_jDisplay.setText(null);
        m_jVprice.setSelected(false);
        m_jVerpatrib.setSelected(false);
        m_jTextTip.setText(null);
        m_jCheckWarrantyReceipt.setSelected(false);
        m_jStockUnits.setVisible(false);
        m_jAlias.setText(null);
        m_jAlwaysAvailable.setSelected(false);
        m_jDiscounted.setSelected(false);
        m_jManageStock.setSelected(false);
        m_SupplierModel.setSelectedKey(null);

        reportlock = false;

        m_jRef.setEnabled(false);
        m_jCode.setEnabled(false);
        m_jName.setEnabled(false);
        m_jComment.setEnabled(false);
        m_jScale.setEnabled(false);
        m_jCategory.setEnabled(false);
        jComboBoxPromotion.setEnabled(false);
        jCheckBoxPromotion.setEnabled(false);
        m_jTax.setEnabled(false);
        m_jAtt.setEnabled(false);
        m_jPriceBuy.setEnabled(false);
        m_jPriceSell.setEnabled(false);
        m_jCommission.setEnabled(false);
        m_jPriceSellTax.setEnabled(false);
        m_jmargin.setEnabled(false);
        mImage.setEnabled(false);
        m_jstockcost.setEnabled(false);
        m_jstockvolume.setEnabled(false);
        m_jInCatalog.setEnabled(false);
        m_jCatalogOrder.setEnabled(false);
        mProperties.setEnabled(false);
        m_jKitchen.setEnabled(false);
        m_jService.setEnabled(false);
        m_jDisplay.setEnabled(false);
        m_jVprice.setEnabled(false);
        m_jVerpatrib.setEnabled(false);
        m_jTextTip.setEnabled(false);
        m_jCheckWarrantyReceipt.setEnabled(false);
        m_jStockUnits.setVisible(false);
        m_jAlias.setEnabled(false);
        m_jAlwaysAvailable.setEnabled(false);
        m_jIsPack.setEnabled(false);
        m_jPackQuantity.setEnabled(false);
        packproductmodel.setSelectedKey(null);
        m_jPackProduct.setEnabled(false);
        jLabelPackQuantity.setEnabled(false);
        jLabelPackProduct.setEnabled(false);
        m_jSuppliers.setEnabled(false);

        m_jDiscounted.setEnabled(false);
        m_jManageStock.setEnabled(false);

        m_jRemoteDisplay.setSelected(false);
        m_jRemoteDisplay.setEnabled(false);

        m_jDefaultPtr.setModel(new SpinnerNumberModel(2, 1, 6, 1));
        m_jDefaultScreen.setModel(new SpinnerNumberModel(0, 0, 20, 1));

        m_jDefaultPtr.setValue(2);
        m_jDefaultPtr.setEnabled(false);
        m_jDefaultScreen.setValue(1);
        m_jDefaultScreen.setEnabled(false);

        m_jPtrOverride.setSelected(false);
        m_jPtrOverride.setEnabled(false);

        calculateMargin();
        calculatePriceSellTax();
        calculateGP();
    }

    @Override
    public void writeValueInsert() {
        reportlock = true;

        m_jTitle.setText(AppLocal.getIntString("label.recordnew"));
        m_id = UUID.randomUUID().toString();
        m_jRef.setText(null);
        m_jCode.setText(null);
        m_jName.setText(null);
        m_jComment.setSelected(false);
        m_jScale.setSelected(false);
        m_CategoryModel.setSelectedKey(null);
        m_PromotionModel.setSelectedKey(null);
        jCheckBoxPromotion.setSelected(false);
        taxcatmodel.setSelectedKey(null);
        attmodel.setSelectedKey(null);
        m_jPriceBuy.setText("0.00");
        m_jCommission.setText("0.00");
        setPriceSell(null);
        mImage.setImage(null);
        m_jstockcost.setText("0.00");
        m_jstockvolume.setText("0.00");
        m_jInCatalog.setSelected(true);
        m_jCatalogOrder.setText(null);
        mProperties.setText("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">");
        m_jKitchen.setSelected(false);
        m_jService.setSelected(false);
        m_jDisplay.setText(null);
        m_jVprice.setSelected(false);
        m_jVerpatrib.setSelected(false);
        m_jTextTip.setText(null);
        m_jCheckWarrantyReceipt.setSelected(false);
        m_jStockUnits.setVisible(false);
        m_jAlias.setText(null);
        m_jAlwaysAvailable.setSelected(false);
        m_jDiscounted.setSelected(true);
        m_jManageStock.setSelected(true);
        m_SupplierModel.setSelectedKey(null);

        reportlock = false;

        // Los habilitados
        m_jRef.setEnabled(true);
        m_jCode.setEnabled(true);
        m_jName.setEnabled(true);
        m_jComment.setEnabled(true);
        m_jScale.setEnabled(true);
        m_jCategory.setEnabled(true);
        jCheckBoxPromotion.setEnabled(true);
        m_jTax.setEnabled(true);
        m_jAtt.setEnabled(true);
        m_jPriceBuy.setEnabled(true);
        m_jCommission.setEnabled(true);
        m_jPriceSell.setEnabled(true);
        m_jPriceSellTax.setEnabled(true);
        m_jmargin.setEnabled(true);
        mImage.setEnabled(true);
        m_jstockcost.setEnabled(true);
        m_jstockvolume.setEnabled(true);
        m_jInCatalog.setEnabled(true);
        m_jCatalogOrder.setEnabled(false);
        mProperties.setEnabled(true);
        m_jKitchen.setEnabled(true);
        m_jService.setEnabled(true);
        m_jDisplay.setEnabled(true);
        m_jVprice.setEnabled(true);
        m_jVerpatrib.setEnabled(true);
        m_jTextTip.setEnabled(true);
        m_jCheckWarrantyReceipt.setEnabled(true);
        m_jStockUnits.setVisible(false);
        m_jAlias.setEnabled(true);
        m_jAlwaysAvailable.setEnabled(true);
        m_jIsPack.setEnabled(true);
        m_jPackQuantity.setEnabled(false);
        m_jPackProduct.setEnabled(false);
        jLabelPackQuantity.setEnabled(false);
        jLabelPackProduct.setEnabled(false);
        m_jSuppliers.setEnabled(true);

        m_jDiscounted.setEnabled(true);
        m_jManageStock.setEnabled(true);

        m_jIsPack.setSelected(false);
        m_jPackQuantity.setText(null);
        packproductmodel.setSelectedKey(null);

        m_jRemoteDisplay.setSelected(false);
        m_jRemoteDisplay.setEnabled(true);
        m_jDefaultPtr.setValue(2);
        m_jDefaultPtr.setEnabled(true);
        m_jDefaultScreen.setValue(1);
        m_jDefaultScreen.setEnabled(true);

        m_jPtrOverride.setSelected(false);
       // m_jPtrOverride.setEnabled(true);

         m_jPtrOverride.setEnabled(m_jKitchen.isSelected());
        calculateMargin();
        calculatePriceSellTax();
        calculateGP();

    }

    private void extractValues(Object[] myprod) {
        m_jTitle.setText(Formats.STRING.formatValue(myprod[DataLogicSales.INDEX_NAME]));
        m_id = myprod[DataLogicSales.INDEX_ID];
        m_jRef.setText(Formats.STRING.formatValue(myprod[DataLogicSales.INDEX_REFERENCE]));
        m_jCode.setText(Formats.STRING.formatValue(myprod[DataLogicSales.INDEX_CODE]));
        m_jName.setText(Formats.STRING.formatValue(myprod[DataLogicSales.INDEX_NAME]));
        m_jComment.setSelected(((Boolean) myprod[DataLogicSales.INDEX_ISCOM]));
        m_jScale.setSelected(((Boolean) myprod[DataLogicSales.INDEX_ISSCALE]));
        m_jPriceBuy.setText(Formats.CURRENCY.formatValue(myprod[DataLogicSales.INDEX_PRICEBUY]));
        setPriceSell(myprod[DataLogicSales.INDEX_PRICESELL]);
        m_jCommission.setText(Formats.DOUBLE.formatValue(myprod[DataLogicSales.INDEX_COMMISSION]));
        m_CategoryModel.setSelectedKey(myprod[DataLogicSales.INDEX_CATEGORY]);

        Object prom = myprod[DataLogicSales.INDEX_PROMOTIONID];
        if (prom == null) {
            jComboBoxPromotion.setEnabled(false);
            jCheckBoxPromotion.setSelected(false);
        } else {
            jComboBoxPromotion.setEnabled(true);
            jCheckBoxPromotion.setSelected(true);
        }
        m_PromotionModel.setSelectedKey(prom);

        taxcatmodel.setSelectedKey(myprod[DataLogicSales.INDEX_TAXCAT]);
        attmodel.setSelectedKey(myprod[DataLogicSales.INDEX_ATTRIBUTESET_ID]);
        mImage.setImage((BufferedImage) myprod[DataLogicSales.INDEX_IMAGE]);
        m_jstockcost.setText(Formats.CURRENCY.formatValue(myprod[DataLogicSales.INDEX_STOCKCOST]));
        m_jstockvolume.setText(Formats.DOUBLE.formatValue(myprod[DataLogicSales.INDEX_STOCKVOLUME]));
        m_jInCatalog.setSelected(((Boolean) myprod[DataLogicSales.INDEX_ISCATALOG]));
        m_jCatalogOrder.setText(Formats.INT.formatValue(myprod[DataLogicSales.INDEX_CATORDER]));
        mProperties.setText(Formats.BYTEA.formatValue(myprod[DataLogicSales.INDEX_ATTRIBUTES]));
        m_jKitchen.setSelected(((Boolean) myprod[DataLogicSales.INDEX_ISKITCHEN]));
        m_jService.setSelected(((Boolean) myprod[DataLogicSales.INDEX_ISSERVICE]));
        m_jDisplay.setText(Formats.STRING.formatValue(myprod[DataLogicSales.INDEX_DISPLAY]));
        m_jVprice.setSelected(((Boolean) myprod[DataLogicSales.INDEX_ISVPRICE]));
        m_jVerpatrib.setSelected(((Boolean) myprod[DataLogicSales.INDEX_ISVERPATRIB]));
        m_jTextTip.setText(Formats.STRING.formatValue(myprod[DataLogicSales.INDEX_TEXTTIP]));
        m_jCheckWarrantyReceipt.setSelected(((Boolean) myprod[DataLogicSales.INDEX_WARRANTY]));
        m_jStockUnits.setText(Formats.DOUBLE.formatValue(myprod[DataLogicSales.INDEX_STOCKUNITS]));
        m_jAlias.setText(Formats.STRING.formatValue(myprod[DataLogicSales.INDEX_ALIAS]));
        m_jAlwaysAvailable.setSelected(((Boolean) myprod[DataLogicSales.INDEX_ALWAYSAVAILABLE]));
        m_jDiscounted.setSelected(((Boolean) myprod[DataLogicSales.INDEX_CANDISCOUNT]));
        m_jIsPack.setSelected(((Boolean) myprod[DataLogicSales.INDEX_ISPACK]));
        m_jPackQuantity.setText(Formats.DOUBLE.formatValue(myprod[DataLogicSales.INDEX_PACKQUANTITY]));
        packproductmodel.setSelectedKey(myprod[DataLogicSales.INDEX_PACKPRODUCT]);
        m_jManageStock.setSelected(((Boolean) myprod[DataLogicSales.INDEX_MANAGESTOCK]));
        m_SupplierModel.setSelectedKey(myprod[DataLogicSales.INDEX_SUPPLIER]);
        m_jDefaultPtr.setValue(myprod[DataLogicSales.INDEX_DEFAULTPTR]);
        m_jRemoteDisplay.setSelected(((Boolean) myprod[DataLogicSales.INDEX_REMOTEDISPLAY]));
        m_jDefaultScreen.setValue(myprod[DataLogicSales.INDEX_DEFAULTSCREEN]);
        m_jPtrOverride.setSelected(((Boolean) myprod[DataLogicSales.INDEX_PTROVERRIDE]));
        siteGuid = myprod[DataLogicSales.INDEX_SITEGUID].toString();

    }

    @Override
    public void writeValueDelete(Object value) {

        reportlock = true;
        Object[] myprod = (Object[]) value;
        extractValues(myprod);
        m_jTitle.setText(Formats.STRING.formatValue(myprod[DataLogicSales.INDEX_REFERENCE]) + " - "
                + Formats.STRING.formatValue(myprod[DataLogicSales.INDEX_NAME]) + " "
                + AppLocal.getIntString("label.recorddeleted"));
        mProperties.setCaretPosition(0);

        reportlock = false;
		
        m_jRef.setEnabled(false);
        m_jCode.setEnabled(false);
        m_jName.setEnabled(false);
        m_jComment.setEnabled(false);
        m_jScale.setEnabled(false);
        m_jCategory.setEnabled(false);
        jComboBoxPromotion.setEnabled(false);
        jCheckBoxPromotion.setEnabled(false);
        m_jTax.setEnabled(false);
        m_jAtt.setEnabled(false);
        m_jPriceBuy.setEnabled(false);
        m_jPriceSell.setEnabled(false);
        m_jCommission.setEnabled(false);
        m_jPriceSellTax.setEnabled(false);
        m_jmargin.setEnabled(false);
        mImage.setEnabled(false);
        m_jstockcost.setEnabled(false);
        m_jstockvolume.setEnabled(false);
        m_jInCatalog.setEnabled(false);
        m_jCatalogOrder.setEnabled(false);
        mProperties.setEnabled(false);
        m_jKitchen.setEnabled(false);
        m_jService.setEnabled(true);
        m_jDisplay.setEnabled(false);
        m_jVprice.setEnabled(false);
        m_jVerpatrib.setEnabled(false);
        m_jTextTip.setEnabled(false);
        m_jCheckWarrantyReceipt.setEnabled(false);
        m_jStockUnits.setVisible(false);
        m_jAlias.setEnabled(false);
        m_jAlwaysAvailable.setEnabled(false);
        m_jDiscounted.setEnabled(false);
        m_jIsPack.setEnabled(true);
        m_jManageStock.setEnabled(false);
        m_jSuppliers.setEnabled(false);

        m_jPackQuantity.setEnabled(m_jIsPack.isSelected());
        m_jPackProduct.setEnabled(m_jIsPack.isSelected());
        jLabelPackQuantity.setEnabled(m_jIsPack.isSelected());
        jLabelPackProduct.setEnabled(m_jIsPack.isSelected());

        m_jRemoteDisplay.setEnabled(false);
        m_jDefaultPtr.setEnabled(false);
        m_jDefaultScreen.setEnabled(false);

        m_jPtrOverride.setEnabled(m_jKitchen.isSelected());

        calculateMargin();
        calculatePriceSellTax();
        calculateGP();
    }

    @Override
    public void writeValueEdit(Object value) {

        reportlock = true;
        Object[] myprod = (Object[]) value;
        extractValues(myprod);

        mProperties.setCaretPosition(0);
        reportlock = false;

        // Los habilitados
        m_jRef.setEnabled(true);
        m_jCode.setEnabled(true);
        m_jName.setEnabled(true);
        m_jComment.setEnabled(true);
        m_jScale.setEnabled(true);
        m_jCategory.setEnabled(true);
        jCheckBoxPromotion.setEnabled(true);
        m_jTax.setEnabled(true);
        m_jAtt.setEnabled(true);
        m_jPriceBuy.setEnabled(true);
        m_jPriceSell.setEnabled(true);
        m_jCommission.setEnabled(true);
        m_jPriceSellTax.setEnabled(true);
        m_jmargin.setEnabled(true);
        mImage.setEnabled(true);
        m_jstockcost.setEnabled(true);
        m_jstockvolume.setEnabled(true);
        m_jInCatalog.setEnabled(true);
        m_jCatalogOrder.setEnabled(m_jInCatalog.isSelected());
        mProperties.setEnabled(true);
        m_jKitchen.setEnabled(true);
        m_jService.setEnabled(true);
        m_jDisplay.setEnabled(true);
        setButtonHTML();
        m_jVprice.setEnabled(true);
        m_jVerpatrib.setEnabled(true);
        m_jTextTip.setEnabled(true);
        m_jCheckWarrantyReceipt.setEnabled(true);
        m_jStockUnits.setVisible(false);
        m_jAlias.setEnabled(true);
        m_jAlwaysAvailable.setEnabled(true);
        m_jSuppliers.setEnabled(true);

        m_jDiscounted.setEnabled(true);

        m_jPackQuantity.setEnabled(m_jIsPack.isSelected());
        m_jPackProduct.setEnabled(m_jIsPack.isSelected());
        jLabelPackQuantity.setEnabled(m_jIsPack.isSelected());
        jLabelPackProduct.setEnabled(m_jIsPack.isSelected());

        m_jRemoteDisplay.setEnabled(true);
        m_jDefaultPtr.setEnabled(true);
        m_jDefaultScreen.setEnabled(true);

        m_jPtrOverride.setEnabled(m_jKitchen.isSelected());

        calculateMargin();
        calculatePriceSellTax();
        calculateGP();
        //siteGuid = (String) m_LocationsModel.getSelectedKey();

    }

    @Override
    public Object createValue() throws BasicException {
        Object[] myprod = new Object[DataLogicSales.FIELD_COUNT];
        myprod[DataLogicSales.INDEX_ID] = m_id;
        myprod[DataLogicSales.INDEX_REFERENCE] = m_jRef.getText();

        String code = m_jCode.getText();
        myprod[DataLogicSales.INDEX_CODE] = code;
        myprod[DataLogicSales.INDEX_CODETYPE] = BarcodeValidator.BarcodeValidate(m_jCode.getText());
        myprod[DataLogicSales.INDEX_NAME] = m_jName.getText();
        myprod[DataLogicSales.INDEX_ISCOM] = m_jComment.isSelected();
        myprod[DataLogicSales.INDEX_ISSCALE] = m_jScale.isSelected();
        myprod[DataLogicSales.INDEX_PRICEBUY] = Formats.CURRENCY.parseValue(m_jPriceBuy.getText());
        myprod[DataLogicSales.INDEX_PRICESELL] = pricesell;
        myprod[DataLogicSales.INDEX_COMMISSION] = Formats.DOUBLE.parseValue(m_jCommission.getText());
        myprod[DataLogicSales.INDEX_CATEGORY] = m_CategoryModel.getSelectedKey();
        myprod[DataLogicSales.INDEX_PROMOTIONID] = m_PromotionModel.getSelectedKey();
        myprod[DataLogicSales.INDEX_TAXCAT] = taxcatmodel.getSelectedKey();
        myprod[DataLogicSales.INDEX_ATTRIBUTESET_ID] = attmodel.getSelectedKey();
        myprod[DataLogicSales.INDEX_IMAGE] = mImage.getImage();
        myprod[DataLogicSales.INDEX_STOCKCOST] = Formats.CURRENCY.parseValue(m_jstockcost.getText());
        myprod[DataLogicSales.INDEX_STOCKVOLUME] = Formats.DOUBLE.parseValue(m_jstockvolume.getText());
        myprod[DataLogicSales.INDEX_ISCATALOG] = m_jInCatalog.isSelected();
        myprod[DataLogicSales.INDEX_CATORDER] = Formats.INT.parseValue(m_jCatalogOrder.getText());
        myprod[DataLogicSales.INDEX_ATTRIBUTES] = Formats.BYTEA.parseValue(mProperties.getText());
        myprod[DataLogicSales.INDEX_ISKITCHEN] = m_jKitchen.isSelected();
        myprod[DataLogicSales.INDEX_ISSERVICE] = m_jService.isSelected();
        myprod[DataLogicSales.INDEX_DISPLAY] = m_jDisplay.getText();
        myprod[DataLogicSales.INDEX_ISVPRICE] = m_jVprice.isSelected();
        myprod[DataLogicSales.INDEX_ISVERPATRIB] = m_jVerpatrib.isSelected();
        myprod[DataLogicSales.INDEX_TEXTTIP] = m_jTextTip.getText();
        myprod[DataLogicSales.INDEX_WARRANTY] = m_jCheckWarrantyReceipt.isSelected();
        myprod[DataLogicSales.INDEX_STOCKUNITS] = Formats.DOUBLE.parseValue(m_jStockUnits.getText());
        myprod[DataLogicSales.INDEX_ALIAS] = m_jAlias.getText();
        myprod[DataLogicSales.INDEX_ALWAYSAVAILABLE] = m_jAlwaysAvailable.isSelected();
        myprod[DataLogicSales.INDEX_DISCOUNTED] = "no";
        myprod[DataLogicSales.INDEX_CANDISCOUNT] = m_jDiscounted.isSelected();
        myprod[DataLogicSales.INDEX_ISPACK] = m_jIsPack.isSelected();
        myprod[DataLogicSales.INDEX_PACKQUANTITY] = Formats.DOUBLE.parseValue(m_jPackQuantity.getText());
        myprod[DataLogicSales.INDEX_PACKPRODUCT] = packproductmodel.getSelectedKey();
        myprod[DataLogicSales.INDEX_MANAGESTOCK] = m_jManageStock.isSelected();
        myprod[DataLogicSales.INDEX_SUPPLIER] = m_SupplierModel.getSelectedKey();
        myprod[DataLogicSales.INDEX_DEFAULTPTR] = m_jDefaultPtr.getValue();
        myprod[DataLogicSales.INDEX_REMOTEDISPLAY] = m_jRemoteDisplay.isSelected();
        myprod[DataLogicSales.INDEX_DEFAULTSCREEN] = m_jDefaultScreen.getValue();
        myprod[DataLogicSales.INDEX_PTROVERRIDE] = m_jPtrOverride.isSelected();
        myprod[DataLogicSales.INDEX_SITEGUID] = siteGuid;
        return myprod;

    }

    @Override
    public Component getComponent() {
        return this;
    }

    private void setCode() {
        Long lDateTime = new Date().getTime(); // USED FOR RANDOM CODE DETAILS

        if (!reportlock) {
            reportlock = true;
            if (m_jRef == null) {
                m_jCode.setText(Long.toString(lDateTime));
            } else if (m_jCode.getText() == null || "".equals(m_jCode.getText())) {
                m_jCode.setText(m_jRef.getText());
            }
            reportlock = false;
        }
    }

    private void setDisplay() {
        String str = (m_jName.getText());
        int length = str.length();
        if (!reportlock) {
            reportlock = true;

            if (length == 0) {
                m_jDisplay.setText(m_jName.getText());
            } else if (m_jDisplay.getText() == null || "".equals(m_jDisplay.getText())) {
                m_jDisplay.setText("<html>" + m_jName.getText());
            }
            reportlock = false;
        }
    }

    private void setButtonHTML() {

        String str = (m_jDisplay.getText());
        int length = str.length();

        if (!reportlock) {
            reportlock = true;

            if (length == 0) {
                jButtonHTML.setText("Click Me");
            } else {
                jButtonHTML.setText(m_jDisplay.getText());
            }
            reportlock = false;
        }
    }

    private void setTextHTML() {
// TODO - expand m_jDisplay HTML functionality        
    }

    private void calculateMargin() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceBuy = readCurrency(m_jPriceBuy.getText());
            Double dPriceSell = (Double) pricesell;

            if (dPriceBuy == null || dPriceSell == null) {
                m_jmargin.setText(null);
            } else {
                m_jmargin.setText(Formats.CURRENCY.formatValue(dPriceSell - dPriceBuy));
            }
            reportlock = false;
        }
    }

    private void calculatePriceSellTax() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceSell = (Double) pricesell;

            if (dPriceSell == null) {
                m_jPriceSellTax.setText(null);
            } else {
                double dTaxRate = taxeslogic.getTaxRate((TaxCategoryInfo) taxcatmodel.getSelectedItem());
                m_jPriceSellTax.setText(Formats.CURRENCY.formatValue(dPriceSell * (1.0 + dTaxRate)));
            }
            reportlock = false;
        }
    }

    private void calculateGP() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceBuy = readCurrency(m_jPriceBuy.getText());
            Double dPriceSell = (Double) pricesell;

            if (dPriceBuy == null || dPriceSell == null) {
                m_jGrossProfit.setText(null);
            } else {
                m_jGrossProfit.setText(Formats.PERCENT.formatValue((dPriceSell - dPriceBuy) / dPriceSell));
            }
            reportlock = false;
        }
    }

    private void calculatePriceSellfromMargin() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceBuy = readCurrency(m_jPriceBuy.getText());
            Double dMargin = readPercent(m_jmargin.getText());

            if (dMargin == null || dPriceBuy == null) {
                setPriceSell(null);
            } else {
                setPriceSell(dPriceBuy * (1.0 + dMargin));
            }

            reportlock = false;
        }

    }

    private void calculatePriceSellfromPST() {

        if (!reportlock) {
            reportlock = true;

            Double dPriceSellTax = readCurrency(m_jPriceSellTax.getText());

            if (dPriceSellTax == null) {
                setPriceSell(null);
            } else {
                double dTaxRate = taxeslogic.getTaxRate((TaxCategoryInfo) taxcatmodel.getSelectedItem());
                setPriceSell(dPriceSellTax / (1.0 + dTaxRate));
            }

            reportlock = false;
        }
    }

    private void setPriceSell(Object value) {

        if (!priceselllock) {
            priceselllock = true;
            pricesell = value;
            m_jPriceSell.setText(Formats.CURRENCY.formatValue(pricesell));
            priceselllock = false;
        }
    }

    private class PriceSellManager implements DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            if (!priceselllock) {
                priceselllock = true;
                pricesell = readCurrency(m_jPriceSell.getText());
                priceselllock = false;
            }
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            if (!priceselllock) {
                priceselllock = true;
                pricesell = readCurrency(m_jPriceSell.getText());
                priceselllock = false;
            }
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (!priceselllock) {
                priceselllock = true;
                pricesell = readCurrency(m_jPriceSell.getText());
                priceselllock = false;
            }
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();
        }
    }

    private class FieldsManager implements DocumentListener, ActionListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();

        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            calculateMargin();
            calculatePriceSellTax();
            calculateGP();
        }
    }

    private class PriceTaxManager implements DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            calculatePriceSellfromPST();
            calculateMargin();
            calculateGP();

        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            calculatePriceSellfromPST();
            calculateMargin();
            calculateGP();

        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            calculatePriceSellfromPST();
            calculateMargin();
            calculateGP();

        }
    }

    private class MarginManager implements DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            calculatePriceSellfromMargin();
            calculatePriceSellTax();
            calculateGP();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            calculatePriceSellfromMargin();
            calculatePriceSellTax();
            calculateGP();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            calculatePriceSellfromMargin();
            calculatePriceSellTax();
            calculateGP();
        }
    }

    private static Double readCurrency(String sValue) {
        try {
            return (Double) Formats.CURRENCY.parseValue(sValue);
        } catch (BasicException e) {
            return null;
        }
    }

    private static Double readPercent(String sValue) {
        try {
            return (Double) Formats.PERCENT.parseValue(sValue);
        } catch (BasicException e) {
            return null;
        }
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
        m_jTitle = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        mGeneral = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        m_jRef = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        m_jCode = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        m_jCategory = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        m_jAtt = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        m_jTax = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        m_jPriceSellTax = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        m_jPriceSell = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        m_jmargin = new javax.swing.JTextField();
        jLabel322 = new javax.swing.JLabel();
        m_jPriceBuy = new javax.swing.JTextField();
        m_jTextTip = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        m_jGrossProfit = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        m_jAlias = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        m_jVerpatrib = new eu.hansolo.custom.SteelCheckBox();
        m_jCheckWarrantyReceipt = new eu.hansolo.custom.SteelCheckBox();
        jLabel36 = new javax.swing.JLabel();
        jComboBoxPromotion = new javax.swing.JComboBox();
        jCheckBoxPromotion = new eu.hansolo.custom.SteelCheckBox();
        jLabel3 = new javax.swing.JLabel();
        m_jCommission = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        m_jSuppliers = new javax.swing.JComboBox<>();
        mStock = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        m_jstockcost = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        m_jstockvolume = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        m_jStockUnits = new javax.swing.JTextField();
        m_jPackQuantity = new javax.swing.JTextField();
        m_jPackProduct = new javax.swing.JComboBox();
        jLabelPackQuantity = new javax.swing.JLabel();
        jLabelPackProduct = new javax.swing.JLabel();
        m_jInCatalog = new eu.hansolo.custom.SteelCheckBox();
        m_jKitchen = new eu.hansolo.custom.SteelCheckBox();
        m_jIsPack = new eu.hansolo.custom.SteelCheckBox();
        m_jAlwaysAvailable = new eu.hansolo.custom.SteelCheckBox();
        m_jScale = new eu.hansolo.custom.SteelCheckBox();
        m_jDiscounted = new eu.hansolo.custom.SteelCheckBox();
        m_jVprice = new eu.hansolo.custom.SteelCheckBox();
        m_jService = new eu.hansolo.custom.SteelCheckBox();
        m_jComment = new eu.hansolo.custom.SteelCheckBox();
        m_jManageStock = new eu.hansolo.custom.SteelCheckBox();
        m_jRemoteDisplay = new eu.hansolo.custom.SteelCheckBox();
        m_jCatalogOrder = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        m_jDefaultPtr = new javax.swing.JSpinner();
        m_jDefaultScreen = new javax.swing.JSpinner();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel15 = new javax.swing.JLabel();
        m_jPtrOverride = new eu.hansolo.custom.SteelCheckBox();
        mImage = new uk.chromis.data.gui.JImageEditor();
        mButton = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        m_jDisplay = new javax.swing.JTextPane();
        jButtonHTML = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel5 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mProperties = new javax.swing.JTextArea();

        jLabel24.setText("jLabel24");

        jLabel27.setText("jLabel27");

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        m_jTitle.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        m_jTitle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        add(m_jTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 0, 330, 30));

        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        mGeneral.setLayout(null);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("label.prodref")); // NOI18N
        mGeneral.add(jLabel1);
        jLabel1.setBounds(10, 10, 65, 25);

        m_jRef.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jRef.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                m_jRefFocusLost(evt);
            }
        });
        mGeneral.add(m_jRef);
        m_jRef.setBounds(130, 10, 170, 25);

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText(AppLocal.getIntString("label.prodbarcode")); // NOI18N
        mGeneral.add(jLabel6);
        jLabel6.setBounds(320, 10, 80, 25);

        m_jCode.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        mGeneral.add(m_jCode);
        m_jCode.setBounds(410, 10, 170, 25);

        jLabel34.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel34.setText(AppLocal.getIntString("Label.Alias")); // NOI18N
        mGeneral.add(jLabel34);
        jLabel34.setBounds(10, 70, 100, 25);

        m_jName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                m_jNameFocusLost(evt);
            }
        });
        mGeneral.add(m_jName);
        m_jName.setBounds(130, 40, 270, 25);

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText(AppLocal.getIntString("label.prodcategory")); // NOI18N
        mGeneral.add(jLabel5);
        jLabel5.setBounds(10, 100, 110, 25);

        m_jCategory.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        mGeneral.add(m_jCategory);
        m_jCategory.setBounds(130, 100, 170, 25);

        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText(AppLocal.getIntString("label.attributes")); // NOI18N
        mGeneral.add(jLabel13);
        jLabel13.setBounds(10, 130, 110, 25);

        m_jAtt.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        mGeneral.add(m_jAtt);
        m_jAtt.setBounds(130, 130, 170, 25);

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText(AppLocal.getIntString("label.taxcategory")); // NOI18N
        mGeneral.add(jLabel7);
        jLabel7.setBounds(10, 160, 110, 25);

        m_jTax.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        mGeneral.add(m_jTax);
        m_jTax.setBounds(130, 160, 170, 25);

        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText(AppLocal.getIntString("label.prodpriceselltax")); // NOI18N
        mGeneral.add(jLabel16);
        jLabel16.setBounds(10, 190, 90, 25);

        m_jPriceSellTax.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jPriceSellTax.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jPriceSellTax.setText("0.00");
        mGeneral.add(m_jPriceSellTax);
        m_jPriceSellTax.setBounds(130, 190, 80, 25);

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(AppLocal.getIntString("label.prodpricesell")); // NOI18N
        mGeneral.add(jLabel4);
        jLabel4.setBounds(220, 190, 90, 25);

        m_jPriceSell.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jPriceSell.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mGeneral.add(m_jPriceSell);
        m_jPriceSell.setBounds(320, 190, 70, 25);

        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLabel19.setText(bundle.getString("label.margin")); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(48, 15));
        mGeneral.add(jLabel19);
        jLabel19.setBounds(400, 190, 80, 25);

        m_jmargin.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jmargin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jmargin.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        m_jmargin.setEnabled(false);
        mGeneral.add(m_jmargin);
        m_jmargin.setBounds(490, 190, 70, 25);

        jLabel322.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel322.setText(AppLocal.getIntString("label.commision")); // NOI18N
        mGeneral.add(jLabel322);
        jLabel322.setBounds(10, 250, 120, 25);

        m_jPriceBuy.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jPriceBuy.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jPriceBuy.setText("0.00");
        mGeneral.add(m_jPriceBuy);
        m_jPriceBuy.setBounds(130, 220, 80, 25);

        m_jTextTip.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        mGeneral.add(m_jTextTip);
        m_jTextTip.setBounds(130, 320, 220, 25);

        jLabel21.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel21.setText(bundle.getString("label.texttip")); // NOI18N
        mGeneral.add(jLabel21);
        jLabel21.setBounds(10, 320, 100, 25);

        m_jGrossProfit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jGrossProfit.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jGrossProfit.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        m_jGrossProfit.setEnabled(false);
        mGeneral.add(m_jGrossProfit);
        m_jGrossProfit.setBounds(490, 220, 70, 25);

        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText(bundle.getString("label.grossprofit")); // NOI18N
        mGeneral.add(jLabel22);
        jLabel22.setBounds(390, 220, 90, 20);

        m_jAlias.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        mGeneral.add(m_jAlias);
        m_jAlias.setBounds(130, 70, 170, 25);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("label.prodname")); // NOI18N
        mGeneral.add(jLabel2);
        jLabel2.setBounds(10, 40, 100, 25);

        m_jVerpatrib.setText(bundle.getString("label.mandatory")); // NOI18N
        mGeneral.add(m_jVerpatrib);
        m_jVerpatrib.setBounds(320, 130, 180, 30);

        m_jCheckWarrantyReceipt.setText(bundle.getString("label.productreceipt")); // NOI18N
        mGeneral.add(m_jCheckWarrantyReceipt);
        m_jCheckWarrantyReceipt.setBounds(370, 317, 260, 30);

        jLabel36.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel36.setText("Promotion");
        mGeneral.add(jLabel36);
        jLabel36.setBounds(10, 280, 90, 30);

        jComboBoxPromotion.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        mGeneral.add(jComboBoxPromotion);
        jComboBoxPromotion.setBounds(160, 280, 380, 30);

        jCheckBoxPromotion.setText(" ");
        jCheckBoxPromotion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxPromotionActionPerformed(evt);
            }
        });
        mGeneral.add(jCheckBoxPromotion);
        jCheckBoxPromotion.setBounds(130, 280, 30, 30);

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("label.prodpricebuy")); // NOI18N
        mGeneral.add(jLabel3);
        jLabel3.setBounds(10, 220, 80, 25);

        m_jCommission.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jCommission.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jCommission.setText("0");
        mGeneral.add(m_jCommission);
        m_jCommission.setBounds(130, 250, 80, 25);

        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("%");
        jLabel8.setPreferredSize(new java.awt.Dimension(11, 25));
        mGeneral.add(jLabel8);
        jLabel8.setBounds(220, 250, 30, 25);

        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText(bundle.getString("label.supplier")); // NOI18N
        mGeneral.add(jLabel11);
        jLabel11.setBounds(10, 350, 120, 20);

        m_jSuppliers.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        mGeneral.add(m_jSuppliers);
        m_jSuppliers.setBounds(130, 350, 410, 30);

        jTabbedPane1.addTab(AppLocal.getIntString("label.prodgeneral"), mGeneral); // NOI18N

        mStock.setLayout(null);

        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText(AppLocal.getIntString("label.orderdisplayno")); // NOI18N
        mStock.add(jLabel9);
        jLabel9.setBounds(230, 323, 110, 25);

        m_jstockcost.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        m_jstockcost.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jstockcost.setText("0.00");
        mStock.add(m_jstockcost);
        m_jstockcost.setBounds(370, 14, 80, 25);

        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText(AppLocal.getIntString("label.prodstockvol")); // NOI18N
        mStock.add(jLabel10);
        jLabel10.setBounds(250, 44, 120, 25);

        m_jstockvolume.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        m_jstockvolume.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jstockvolume.setText("0.00");
        mStock.add(m_jstockvolume);
        m_jstockvolume.setBounds(370, 44, 80, 25);

        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText(AppLocal.getIntString("label.prodorder")); // NOI18N
        jLabel18.setToolTipText("");
        mStock.add(jLabel18);
        jLabel18.setBounds(250, 74, 120, 25);

        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText(bundle.getString("label.prodminmax")); // NOI18N
        jLabel23.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        mStock.add(jLabel23);
        jLabel23.setBounds(250, 110, 270, 60);

        m_jStockUnits.setEditable(false);
        m_jStockUnits.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jStockUnits.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jStockUnits.setText("0");
        m_jStockUnits.setBorder(null);
        mStock.add(m_jStockUnits);
        m_jStockUnits.setBounds(370, 160, 80, 25);

        m_jPackQuantity.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        m_jPackQuantity.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jPackQuantity.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                m_jPackQuantityFocusLost(evt);
            }
        });
        mStock.add(m_jPackQuantity);
        m_jPackQuantity.setBounds(370, 200, 80, 25);

        m_jPackProduct.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        mStock.add(m_jPackProduct);
        m_jPackProduct.setBounds(370, 230, 220, 25);

        jLabelPackQuantity.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelPackQuantity.setText("Pack Quantity");
        mStock.add(jLabelPackQuantity);
        jLabelPackQuantity.setBounds(250, 200, 90, 20);

        jLabelPackProduct.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelPackProduct.setText("of Product");
        mStock.add(jLabelPackProduct);
        jLabelPackProduct.setBounds(270, 227, 80, 30);

        m_jInCatalog.setSelected(true);
        m_jInCatalog.setText(bundle.getString("label.prodincatalog")); // NOI18N
        m_jInCatalog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jInCatalogActionPerformed(evt);
            }
        });
        mStock.add(m_jInCatalog);
        m_jInCatalog.setBounds(20, 10, 200, 30);

        m_jKitchen.setText("Print to Remote Printer");
        m_jKitchen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jKitchenActionPerformed(evt);
            }
        });
        mStock.add(m_jKitchen);
        m_jKitchen.setBounds(20, 290, 190, 30);

        m_jIsPack.setText("Multi Pack");
        m_jIsPack.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        m_jIsPack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jIsPackActionPerformed(evt);
            }
        });
        mStock.add(m_jIsPack);
        m_jIsPack.setBounds(250, 160, 110, 30);

        m_jAlwaysAvailable.setText(bundle.getString("Label.AlwaysAvailable")); // NOI18N
        m_jAlwaysAvailable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jAlwaysAvailableActionPerformed(evt);
            }
        });
        mStock.add(m_jAlwaysAvailable);
        m_jAlwaysAvailable.setBounds(20, 160, 210, 30);

        m_jScale.setText(bundle.getString("label.prodscale")); // NOI18N
        mStock.add(m_jScale);
        m_jScale.setBounds(20, 100, 200, 30);

        m_jDiscounted.setText(bundle.getString("label.discounted")); // NOI18N
        mStock.add(m_jDiscounted);
        m_jDiscounted.setBounds(20, 190, 200, 30);

        m_jVprice.setText(bundle.getString("label.variableprice")); // NOI18N
        mStock.add(m_jVprice);
        m_jVprice.setBounds(20, 130, 200, 30);

        m_jService.setText("Service Item");
        mStock.add(m_jService);
        m_jService.setBounds(20, 40, 210, 30);

        m_jComment.setText(bundle.getString("label.prodaux")); // NOI18N
        mStock.add(m_jComment);
        m_jComment.setBounds(20, 70, 200, 30);

        m_jManageStock.setText(bundle.getString("label.managestock")); // NOI18N
        mStock.add(m_jManageStock);
        m_jManageStock.setBounds(20, 220, 200, 30);

        m_jRemoteDisplay.setText(bundle.getString("label.sendtorderscreen")); // NOI18N
        mStock.add(m_jRemoteDisplay);
        m_jRemoteDisplay.setBounds(20, 320, 200, 30);

        m_jCatalogOrder.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        m_jCatalogOrder.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        mStock.add(m_jCatalogOrder);
        m_jCatalogOrder.setBounds(370, 74, 80, 25);

        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText(AppLocal.getIntString("label.prodstockcost")); // NOI18N
        mStock.add(jLabel12);
        jLabel12.setBounds(250, 14, 120, 25);

        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText(AppLocal.getIntString("label.defaultkitchenptr")); // NOI18N
        mStock.add(jLabel14);
        jLabel14.setBounds(230, 293, 130, 25);

        m_jDefaultPtr.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        m_jDefaultPtr.setMinimumSize(new java.awt.Dimension(25, 25));
        m_jDefaultPtr.setPreferredSize(new java.awt.Dimension(29, 25));
        mStock.add(m_jDefaultPtr);
        m_jDefaultPtr.setBounds(360, 290, 35, 25);

        m_jDefaultScreen.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        m_jDefaultScreen.setMinimumSize(new java.awt.Dimension(30, 25));
        m_jDefaultScreen.setPreferredSize(new java.awt.Dimension(30, 25));
        mStock.add(m_jDefaultScreen);
        m_jDefaultScreen.setBounds(360, 320, 35, 25);
        mStock.add(jSeparator2);
        jSeparator2.setBounds(10, 270, 650, 10);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel15.setText(bundle.getString("Label.remoteorder")); // NOI18N
        mStock.add(jLabel15);
        jLabel15.setBounds(10, 270, 250, 20);

        m_jPtrOverride.setText(bundle.getString("label.ptroverride")); // NOI18N
        mStock.add(m_jPtrOverride);
        m_jPtrOverride.setBounds(420, 290, 190, 30);

        jTabbedPane1.addTab(AppLocal.getIntString("label.prodstock"), mStock); // NOI18N
        jTabbedPane1.addTab("Image", mImage);

        mButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        mButton.setLayout(null);

        jLabel28.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel28.setText(bundle.getString("label.prodbuttonhtml")); // NOI18N
        mButton.add(jLabel28);
        jLabel28.setBounds(10, 10, 270, 20);

        m_jDisplay.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jScrollPane2.setViewportView(m_jDisplay);

        mButton.add(jScrollPane2);
        jScrollPane2.setBounds(10, 40, 480, 40);

        jButtonHTML.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButtonHTML.setText(bundle.getString("button.htmltest")); // NOI18N
        jButtonHTML.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButtonHTML.setMaximumSize(new java.awt.Dimension(96, 72));
        jButtonHTML.setMinimumSize(new java.awt.Dimension(96, 72));
        jButtonHTML.setPreferredSize(new java.awt.Dimension(96, 72));
        jButtonHTML.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonHTMLMouseClicked(evt);
            }
        });
        mButton.add(jButtonHTML);
        jButtonHTML.setBounds(205, 90, 110, 70);

        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText(bundle.getString("label.producthtmlguide")); // NOI18N
        jLabel17.setToolTipText("");
        jLabel17.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        mButton.add(jLabel17);
        jLabel17.setBounds(10, 200, 330, 100);
        mButton.add(jSeparator1);
        jSeparator1.setBounds(150, 300, 0, 2);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel32.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel32.setText(bundle.getString("label.fontexample")); // NOI18N
        jLabel32.setToolTipText(bundle.getString("tooltip.fontexample")); // NOI18N

        jLabel25.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel25.setText(bundle.getString("label.fontcolour")); // NOI18N
        jLabel25.setToolTipText(bundle.getString("tooltip.fontcolour")); // NOI18N
        jLabel25.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel29.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel29.setText(bundle.getString("label.fontsizelarge")); // NOI18N
        jLabel29.setToolTipText(bundle.getString("tooltip.fontsizelarge")); // NOI18N
        jLabel29.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel29.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel26.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel26.setText(bundle.getString("label.fontsize")); // NOI18N
        jLabel26.setToolTipText(bundle.getString("tooltip.fontsizesmall")); // NOI18N
        jLabel26.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel31.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel31.setText(bundle.getString("label.fontitalic")); // NOI18N
        jLabel31.setToolTipText(bundle.getString("tooltip.fontitalic")); // NOI18N
        jLabel31.setPreferredSize(new java.awt.Dimension(160, 30));

        jLabel30.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel30.setText(bundle.getString("label.fontweight")); // NOI18N
        jLabel30.setToolTipText(bundle.getString("tooltip.fontbold")); // NOI18N
        jLabel30.setPreferredSize(new java.awt.Dimension(160, 30));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(6, 6, 6))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)
                .addContainerGap())
        );

        mButton.add(jPanel5);
        jPanel5.setBounds(360, 110, 180, 220);

        jTabbedPane1.addTab("Button", mButton);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setLayout(new java.awt.BorderLayout());

        mProperties.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        jScrollPane1.setViewportView(mProperties);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab(AppLocal.getIntString("label.properties"), jPanel3); // NOI18N

        add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, 680, 450));
    }// </editor-fold>//GEN-END:initComponents

    private void m_jRefFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_m_jRefFocusLost
        setCode();
    }//GEN-LAST:event_m_jRefFocusLost

    private void m_jNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_m_jNameFocusLost
        setDisplay();
    }//GEN-LAST:event_m_jNameFocusLost

    private void jButtonHTMLMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonHTMLMouseClicked
        setButtonHTML();
    }//GEN-LAST:event_jButtonHTMLMouseClicked


    private void m_jAlwaysAvailableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jAlwaysAvailableActionPerformed
        if (m_jAlwaysAvailable.isSelected()) {
            m_jInCatalog.setSelected(false);
        }
    }//GEN-LAST:event_m_jAlwaysAvailableActionPerformed

    private void m_jInCatalogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jInCatalogActionPerformed
        if (m_jInCatalog.isSelected()) {
            m_jCatalogOrder.setEnabled(true);
        } else {
            m_jCatalogOrder.setEnabled(false);
            m_jCatalogOrder.setText(null);
        }

        if (m_jInCatalog.isSelected()) {
            m_jAlwaysAvailable.setSelected(false);
        }
    }//GEN-LAST:event_m_jInCatalogActionPerformed

    private void m_jIsPackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jIsPackActionPerformed
        m_jPackQuantity.setEnabled(m_jIsPack.isSelected());
        m_jPackProduct.setEnabled(m_jIsPack.isSelected());
        jLabelPackQuantity.setEnabled(m_jIsPack.isSelected());
        jLabelPackProduct.setEnabled(m_jIsPack.isSelected());
    }//GEN-LAST:event_m_jIsPackActionPerformed

    private void jCheckBoxPromotionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxPromotionActionPerformed
        if (jCheckBoxPromotion.isSelected()) {
            jComboBoxPromotion.setEnabled(true);
        } else {
            jComboBoxPromotion.setEnabled(false);
            jComboBoxPromotion.getModel().setSelectedItem(null);
        }
    }//GEN-LAST:event_jCheckBoxPromotionActionPerformed

    private void m_jPackQuantityFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_m_jPackQuantityFocusLost
        Object selectItem = m_jPackProduct.getSelectedItem();
        Object selectIndex = m_jPackProduct.getSelectedItem();
        try {
            packproductmodel = new ComboBoxValModel(packproductsent.list());
        } catch (BasicException ex) {
            Logger.getLogger(ProductsEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        m_jPackProduct.setModel(packproductmodel);
        if (selectItem != null) {
            m_jPackProduct.setSelectedItem(selectItem);
            m_jPackProduct.setSelectedItem(selectIndex);
        }
    }//GEN-LAST:event_m_jPackQuantityFocusLost

    private void m_jKitchenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jKitchenActionPerformed
        m_jPtrOverride.setEnabled(m_jKitchen.isSelected());
    }//GEN-LAST:event_m_jKitchenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonHTML;
    private eu.hansolo.custom.SteelCheckBox jCheckBoxPromotion;
    private javax.swing.JComboBox jComboBoxPromotion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel322;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelPackProduct;
    private javax.swing.JLabel jLabelPackQuantity;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel mButton;
    private javax.swing.JPanel mGeneral;
    private uk.chromis.data.gui.JImageEditor mImage;
    private javax.swing.JTextArea mProperties;
    private javax.swing.JPanel mStock;
    private javax.swing.JTextField m_jAlias;
    private eu.hansolo.custom.SteelCheckBox m_jAlwaysAvailable;
    private javax.swing.JComboBox m_jAtt;
    private javax.swing.JTextField m_jCatalogOrder;
    private javax.swing.JComboBox m_jCategory;
    private eu.hansolo.custom.SteelCheckBox m_jCheckWarrantyReceipt;
    private javax.swing.JTextField m_jCode;
    private eu.hansolo.custom.SteelCheckBox m_jComment;
    private javax.swing.JTextField m_jCommission;
    private javax.swing.JSpinner m_jDefaultPtr;
    private javax.swing.JSpinner m_jDefaultScreen;
    private eu.hansolo.custom.SteelCheckBox m_jDiscounted;
    private javax.swing.JTextPane m_jDisplay;
    private javax.swing.JTextField m_jGrossProfit;
    private eu.hansolo.custom.SteelCheckBox m_jInCatalog;
    private eu.hansolo.custom.SteelCheckBox m_jIsPack;
    private eu.hansolo.custom.SteelCheckBox m_jKitchen;
    private eu.hansolo.custom.SteelCheckBox m_jManageStock;
    private javax.swing.JTextField m_jName;
    private javax.swing.JComboBox m_jPackProduct;
    private javax.swing.JTextField m_jPackQuantity;
    private javax.swing.JTextField m_jPriceBuy;
    private javax.swing.JTextField m_jPriceSell;
    private javax.swing.JTextField m_jPriceSellTax;
    private eu.hansolo.custom.SteelCheckBox m_jPtrOverride;
    private javax.swing.JTextField m_jRef;
    private eu.hansolo.custom.SteelCheckBox m_jRemoteDisplay;
    private eu.hansolo.custom.SteelCheckBox m_jScale;
    private eu.hansolo.custom.SteelCheckBox m_jService;
    private javax.swing.JTextField m_jStockUnits;
    private javax.swing.JComboBox<String> m_jSuppliers;
    private javax.swing.JComboBox m_jTax;
    private javax.swing.JTextField m_jTextTip;
    private javax.swing.JLabel m_jTitle;
    private eu.hansolo.custom.SteelCheckBox m_jVerpatrib;
    private eu.hansolo.custom.SteelCheckBox m_jVprice;
    private javax.swing.JTextField m_jmargin;
    private javax.swing.JTextField m_jstockcost;
    private javax.swing.JTextField m_jstockvolume;
    // End of variables declaration//GEN-END:variables

}
