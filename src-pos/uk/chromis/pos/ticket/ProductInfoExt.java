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

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.ImageUtils;
import uk.chromis.data.loader.SerializerRead;
import uk.chromis.format.Formats;
import java.awt.image.BufferedImage;
import java.util.Properties;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.util.SessionFactory;

/**
 *
 * @author adrianromero
 *
 */
public class ProductInfoExt {

    private static final long serialVersionUID = 7587696873037L;

    protected String m_ID;
    protected String m_sRef;
    protected String m_sCode;
    protected String m_sCodetype;
    protected String m_sName;
    protected Boolean m_bCom;
    protected Boolean m_bScale;
    protected Double m_dPriceBuy;
    protected Double m_dPriceSell;
    protected Double m_dCommission;
    protected String categoryid;
    protected String taxcategoryid;
    protected String attributesetid;
    protected BufferedImage m_Image;
    protected Double m_stockCost;
    protected Double m_stockVolume;
    protected Boolean m_bKitchen;
    private Boolean m_bService;
    protected Properties m_attributes;
    protected String m_sDisplay;
    protected Boolean m_bVprice;
    protected Boolean m_bVerpatrib;
    protected String m_sTextTip;
    protected Boolean m_bWarranty;
    protected Double m_dStockUnits;
    protected String m_sAlias;
    protected Boolean m_bAlwaysAvailable;
    protected Boolean m_canDiscount;
    protected String m_discounted;
    protected String m_promotionid;
    protected Boolean m_bCatalog;
    protected Double m_catorder;
    protected Boolean m_bPack;
    protected Double m_packquantity;
    protected String m_packproduct;
    protected Boolean m_manageStock;
    protected String m_supplier;
    protected String m_siteguid;

    public ProductInfoExt() {
        m_ID = null;
        m_sRef = "0000";
        m_sCode = "0000";
        m_sCodetype = null;
        m_sName = null;
        m_bCom = false;
        m_bScale = false;
        categoryid = null;
        taxcategoryid = null;
        attributesetid = null;
        m_dPriceBuy = 0.0;
        m_dPriceSell = 0.0;
        m_dCommission = 0.0;
        m_stockCost = 0.0;
        m_stockVolume = 0.0;
        m_Image = null;
        m_bKitchen = false;
        m_bService = false;
        m_sDisplay = null;
        m_attributes = new Properties();
        m_bVprice = false;
        m_bVerpatrib = false;
        m_sTextTip = null;
        m_bWarranty = false;
        m_dStockUnits = 0.0;
        m_sAlias = null;
        m_bAlwaysAvailable = false;
        m_canDiscount = true;
        m_discounted = "no";
        m_promotionid = null;
        m_bCatalog = true;
        m_catorder = 0.0;
        m_bPack = false;
        m_packquantity = 0.0;
        m_packproduct = null;
        m_manageStock = true;
        m_siteguid = null;
        m_supplier = null;
    }

    public String getSiteguid() {
        return m_siteguid;
    }

    public void setSiteguid(String siteguid) {
        this.m_siteguid = siteguid;
    }

    public final String getID() {
        return m_ID;
    }

    public final void setID(String id) {
        m_ID = id;
    }

    public final String getReference() {
        return m_sRef;
    }

    public final void setReference(String sRef) {
        m_sRef = sRef;
    }

    public final String getCode() {
        return m_sCode;
    }

    public final void setCode(String sCode) {
        m_sCode = sCode;
    }

    public final String getCodetype() {
        return m_sCodetype;
    }

    public String getSupplier() {
        return m_supplier;
    }

    public void setSupplier(String m_supplier) {
        this.m_supplier = m_supplier;
    }

    public final void setCodetype(String sCodetype) {
        m_sCodetype = sCodetype;
    }

    public final String getName() {
        return m_sName;
    }

    public final void setName(String sName) {
        m_sName = sName;
    }

    public final String getDisplay() {
        return m_sDisplay;
    }

    public final void setDisplay(String sDisplay) {
        m_sDisplay = sDisplay;
    }

    public final Boolean isCom() {
        return m_bCom;
    }

    public final void setCom(Boolean bValue) {
        m_bCom = bValue;
    }

    public final Boolean isScale() {
        return m_bScale;
    }

    public final void setScale(Boolean bValue) {
        m_bScale = bValue;
    }

    public final Boolean isKitchen() {
        return m_bKitchen;
    }

    public final void setKitchen(Boolean bValue) {
        m_bKitchen = bValue;
    }

    public final Boolean isService() {
        return m_bService;
    }

    public final void setService(Boolean bValue) {
        m_bService = bValue;
    }

    public final Boolean isVprice() {
        return m_bVprice;
    }

    public final Boolean isVerpatrib() {
        return m_bVerpatrib;
    }

    public final String getTextTip() {
        return m_sTextTip;
    }

    public final Boolean getWarranty() {
        return m_bWarranty;
    }

    public final void setWarranty(Boolean bValue) {
        m_bWarranty = bValue;
    }

    public final String getCategoryID() {
        return categoryid;
    }

    public final void setCategoryID(String sCategoryID) {
        categoryid = sCategoryID;
    }

    public final String getTaxCategoryID() {
        return taxcategoryid;
    }

    public final void setTaxCategoryID(String value) {
        taxcategoryid = value;
    }

    public final String getAttributeSetID() {
        return attributesetid;
    }

    public final void setAttributeSetID(String value) {
        attributesetid = value;
    }

    public final Double getPriceBuy() {
        return m_dPriceBuy;
    }

    public final void setPriceBuy(Double dPrice) {
        m_dPriceBuy = dPrice;
    }

    public final Double getCommission() {
        return m_dCommission;
    }

    public final void setCommission(Double dCommission) {
        m_dCommission = dCommission;
    }

    public final Double getPriceSell() {
        return m_dPriceSell;
    }

    public final void setPriceSell(Double dPrice) {
        m_dPriceSell = dPrice;
    }

    public final Double getStockUnits() {
        return m_dStockUnits;
    }

    public final void setStockUnits(Double dStockUnits) {
        m_dStockUnits = dStockUnits;
    }

    public final Double getStockVolume() {
        return m_stockVolume;
    }

    public final void setStockVolume(Double dStockVolume) {
        m_stockVolume = dStockVolume;
    }

    public final Double getStockCost() {
        return m_stockCost;
    }

    public final void setStockCost(Double dPrice) {
        m_stockCost = dPrice;
    }

    public final void setTextTip(String value) {
        m_sTextTip = value;
    }

    public final Double getPriceSellTax(TaxInfo tax) {
        return m_dPriceSell * (1.0 + tax.getRate());
    }

    public final String getPromotionID() {
        return m_promotionid;
    }

    public final Boolean getInCatalog() {
        return m_bCatalog;
    }

    public final Double getCatOrder() {
        return m_catorder;
    }

    public String printPriceSell() {
        return Formats.CURRENCY.formatValue(new Double(getPriceSell()));
    }

    public String printPriceSellTax(TaxInfo tax) {
        return Formats.CURRENCY.formatValue(new Double(getPriceSellTax(tax)));
    }

    public BufferedImage getImage() {
        return m_Image;
    }

    public void setImage(BufferedImage img) {
        m_Image = img;
    }

    public String getProperty(String key) {
        return m_attributes.getProperty(key);
    }

    public String getProperty(String key, String defaultvalue) {
        return m_attributes.getProperty(key, defaultvalue);
    }

    public void setProperty(String key, String value) {
        m_attributes.setProperty(key, value);
    }

    public Properties getProperties() {
        return m_attributes;
    }

    public final String getAlias() {
        return m_sAlias;
    }

    public final void setAlias(String alias) {
        m_sAlias = alias;
    }

    public final boolean getAlwaysAvailable() {
        return m_bAlwaysAvailable;
    }

    public final Boolean getManageStock() {
        return m_manageStock;
    }

    public final void setManageStock(Boolean bValue) {
        m_manageStock = bValue;
    }

    public final void setAlwaysAvailable(Boolean bValue) {
        m_bAlwaysAvailable = bValue;
    }

    public final Boolean getCanDiscount() {
        return m_canDiscount;
    }

    public final String getDiscounted() {
        return m_discounted;
    }

    public void setDiscounted(String discount) {
        m_discounted = discount;
    }

    public final boolean getIsPack() {
        return m_bPack;
    }

    public void setIsPack(Boolean ispack) {
        m_bPack = ispack;
    }

    public final double getPackQuantity() {
        if (m_packquantity == null) {
            m_packquantity = 0.0;
        }

        return m_packquantity;
    }

    public final void setPackQuantity(double packQuantity) {
        m_packquantity = packQuantity;
    }

    public final String getPackProduct() {
        return m_packproduct;
    }

    public void setPackProduct(String packproduct) {
        m_packproduct = packproduct;
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {

                // If this assert fails it is likely a coding error
                // Look at the number of fields fetched by the SELECT statement
                // and cross check for a mismatch in the INDEX_xxx fields in
                // DataLogicSales
                assert (dr.getDataField().length == DataLogicSales.FIELD_COUNT);

                ProductInfoExt product = new ProductInfoExt();
                product.m_ID = dr.getString(DataLogicSales.INDEX_ID + 1);
                product.m_sRef = dr.getString(DataLogicSales.INDEX_REFERENCE + 1);
                product.m_sCode = dr.getString(DataLogicSales.INDEX_CODE + 1);
                product.m_sCodetype = dr.getString(DataLogicSales.INDEX_CODETYPE + 1);
                product.m_sName = dr.getString(DataLogicSales.INDEX_NAME + 1);
                product.m_bCom = dr.getBoolean(DataLogicSales.INDEX_ISCOM + 1);
                product.m_bScale = dr.getBoolean(DataLogicSales.INDEX_ISSCALE + 1);
                product.m_dPriceBuy = dr.getDouble(DataLogicSales.INDEX_PRICEBUY + 1);
                product.m_dPriceSell = dr.getDouble(DataLogicSales.INDEX_PRICESELL + 1);
                product.m_dCommission = dr.getDouble(DataLogicSales.INDEX_COMMISSION + 1);
                product.categoryid = dr.getString(DataLogicSales.INDEX_CATEGORY + 1);
                product.taxcategoryid = dr.getString(DataLogicSales.INDEX_TAXCAT + 1);
                product.attributesetid = dr.getString(DataLogicSales.INDEX_ATTRIBUTESET_ID + 1);
                product.m_Image = ImageUtils.readImage(dr.getBytes(DataLogicSales.INDEX_IMAGE + 1));
                product.m_attributes = ImageUtils.readProperties(dr.getBytes(DataLogicSales.INDEX_ATTRIBUTES + 1));
                product.m_stockCost = dr.getDouble(DataLogicSales.INDEX_STOCKCOST + 1);
                product.m_stockVolume = dr.getDouble(DataLogicSales.INDEX_STOCKVOLUME + 1);
                product.m_bCatalog = dr.getBoolean(DataLogicSales.INDEX_ISCATALOG + 1);
                product.m_catorder = dr.getDouble(DataLogicSales.INDEX_CATORDER + 1);
                product.m_bKitchen = dr.getBoolean(DataLogicSales.INDEX_ISKITCHEN + 1);
                product.m_bService = dr.getBoolean(DataLogicSales.INDEX_ISSERVICE + 1);
                product.m_sDisplay = dr.getString(DataLogicSales.INDEX_DISPLAY + 1);
                product.m_bVprice = dr.getBoolean(DataLogicSales.INDEX_ISVPRICE + 1);
                product.m_bVerpatrib = dr.getBoolean(DataLogicSales.INDEX_ISVERPATRIB + 1);
                product.m_sTextTip = dr.getString(DataLogicSales.INDEX_TEXTTIP + 1);
                product.m_bWarranty = dr.getBoolean(DataLogicSales.INDEX_WARRANTY + 1);
                product.m_dStockUnits = dr.getDouble(DataLogicSales.INDEX_STOCKUNITS + 1);
                product.m_sAlias = dr.getString(DataLogicSales.INDEX_ALIAS + 1);
                product.m_bAlwaysAvailable = dr.getBoolean(DataLogicSales.INDEX_ALWAYSAVAILABLE + 1);
                product.m_discounted = dr.getString(DataLogicSales.INDEX_DISCOUNTED + 1);
                product.m_canDiscount = dr.getBoolean(DataLogicSales.INDEX_CANDISCOUNT + 1);
                product.m_bPack = dr.getBoolean(DataLogicSales.INDEX_ISPACK + 1);
                product.m_packquantity = dr.getDouble(DataLogicSales.INDEX_PACKQUANTITY + 1);
                product.m_packproduct = dr.getString(DataLogicSales.INDEX_PACKPRODUCT + 1);
                product.m_promotionid = dr.getString(DataLogicSales.INDEX_PROMOTIONID + 1);
                product.m_manageStock = dr.getBoolean(DataLogicSales.INDEX_MANAGESTOCK + 1);
                product.m_siteguid = dr.getString(DataLogicSales.INDEX_SITEGUID + 1);

                return product;
            }
        };
    }

    @Override
    public final String toString() {
        return m_sRef + " - " + m_sName;
    }
}
