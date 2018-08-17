/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c) 2015-2018
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.DataWrite;
import uk.chromis.data.loader.SerializableRead;
import uk.chromis.data.loader.SerializableWrite;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.util.StringUtils;

public class TicketLineInfo implements SerializableWrite, SerializableRead, Serializable {

    private static final long serialVersionUID = 6608012948284450199L;
    private String m_sTicket;
    private int m_iLine;
    private double multiply;
    private double price;
    private TaxInfo tax;
    private Properties attributes;
    private String productid;
    private String attsetinstid;
    private Boolean updated = false;
    private Double refundQty;
    private Double orderQty;
    private String siteGuid;

    private Double taxRate;
    private Double taxAmount;
    private Double commission;

    public TicketLineInfo(String productid, double dMultiply, double dPrice, TaxInfo tax, Properties props) {
        init(productid, null, dMultiply, dPrice, tax, props, 0.0, 0.0);
    }

    public TicketLineInfo(String productid, double dMultiply, double dPrice, TaxInfo tax) {
        init(productid, null, dMultiply, dPrice, tax, new Properties(), 0.0, 0.0);
    }

    public TicketLineInfo(String productid, String productname, double dMultiply, double dPrice, String producttaxcategory, TaxInfo tax) {
        Properties props = new Properties();
        props.setProperty("product.name", productname);
        props.setProperty("product.taxcategoryid", producttaxcategory);
        init(productid, null, dMultiply, dPrice, tax, props, 0.0, 0.0);
    }

    public TicketLineInfo(String productid, String productname, String producttaxcategory, double dMultiply, double dPrice, TaxInfo tax) {
        Properties props = new Properties();
        props.setProperty("product.name", productname);
        props.setProperty("product.taxcategoryid", producttaxcategory);
        init(productid, null, dMultiply, dPrice, tax, props, 0.0, 0.0);
    }

    public TicketLineInfo(String productname, String producttaxcategory, double dMultiply, double dPrice, TaxInfo tax) {
        Properties props = new Properties();
        props.setProperty("product.name", productname);
        props.setProperty("product.taxcategoryid", producttaxcategory);
        init(null, null, dMultiply, dPrice, tax, props, 0.0, 0.0);
    }

    public TicketLineInfo() {
        init(null, null, 0.0, 0.0, null, new Properties(), 0.0, 0.0);
    }

    public TicketLineInfo(ProductInfoExt product, double dMultiply, double dPrice, TaxInfo tax, Properties attributes) {
        String pid;

        if (product == null) {
            pid = null;
        } else {
            pid = product.getID();

            attributes.setProperty("product.name", product.getName());
            attributes.setProperty("product.code", product.getCode() == null ? "" : product.getCode());
            attributes.setProperty("product.com", product.isCom() ? "true" : "false");
            attributes.setProperty("product.kitchen", product.isKitchen() ? "true" : "false");
            attributes.setProperty("product.service", product.isService() ? "true" : "false");
            attributes.setProperty("product.vprice", product.isVprice() ? "true" : "false");
            attributes.setProperty("product.verpatrib", product.isVerpatrib() ? "true" : "false");
            attributes.setProperty("product.alwaysavailable", product.getAlwaysAvailable() ? "true" : "false");
            attributes.setProperty("product.warranty", product.getWarranty() ? "true" : "false");
            attributes.setProperty("product.taxcategoryid", product.getTaxCategoryID());
            attributes.setProperty("product.discounted", product.getDiscounted() == null ? "no" : product.getDiscounted());
            attributes.setProperty("product.candiscount", product.getCanDiscount() ? "true" : "false");
            attributes.setProperty("product.ispack", product.getIsPack() ? "true" : "false");
            attributes.setProperty("product.promotionadded", "false");
            attributes.setProperty("product.nosc", "0");
            attributes.setProperty("product.siteGuid", "");
            attributes.setProperty("sendstatus", "No");
            attributes.setProperty("displayno", String.valueOf(product.getDefaultScreen()));
            attributes.setProperty("usedisplay", product.isRemoteDisplay() ? "true" : "false");
            attributes.setProperty("kitchen.printer", String.valueOf(product.getDefaultPtr()));
            attributes.setProperty("product.managestock", product.getManageStock() ? "true" : "false");

            if (product.getCategoryID() != null) {
                attributes.setProperty("product.categoryid", product.getCategoryID());
            }

            attributes.setProperty("product.alwaysavailable", product.getAlwaysAvailable() ? "true" : "false");

            if (product.getAlias() != null) {
                attributes.setProperty("product.alias", product.getAlias());
            }
            if (product.getTextTip() != null) {
                attributes.setProperty("product.texttip", product.getTextTip());
            }
            if (product.getAttributeSetID() != null) {
                attributes.setProperty("product.attsetid", product.getAttributeSetID());
            }
            attributes.setProperty("product.taxcategoryid", product.getTaxCategoryID());
            if (product.getCategoryID() != null) {
                attributes.setProperty("product.categoryid", product.getCategoryID());
            }

            attributes.setProperty("product.discounted", product.getDiscounted() == null ? "no" : product.getDiscounted());
            attributes.setProperty("product.candiscount", product.getCanDiscount() ? "true" : "false");

            attributes.setProperty("product.ispack", product.getIsPack() ? "true" : "false");

            if (product.getPackProduct() != null) {
                attributes.setProperty("product.packproduct", product.getPackProduct());
            }
            if (product.getPromotionID() != null) {
                attributes.setProperty("product.promotionid", product.getPromotionID());
            }
            attributes.setProperty("product.promotionadded", "false");
            attributes.setProperty("product.nosc", "0");
            attributes.setProperty("product.siteGuid", "");
            attributes.setProperty("product.ptroverride", product.getPtrOverride() ? "true" : "false");
        }
        init(pid, null, dMultiply, dPrice, tax, attributes, 0.0, product.getCommission());

        attributes.setProperty("product.managestock", product.getManageStock() ? "true" : "false");
    }

    public TicketLineInfo(ProductInfoExt oProduct, double dPrice, TaxInfo tax, Properties attributes) {
        this(oProduct, 1.0, dPrice, tax, attributes);
    }

    public TicketLineInfo(TicketLineInfo line) {
        init(line.productid, line.attsetinstid, line.multiply, line.price,
                line.tax, (Properties) line.attributes.clone(), line.refundQty, line.commission);
    }

    private void init(String productid, String attsetinstid, double dMultiply,
            double dPrice, TaxInfo tax, Properties attributes, Boolean isProductKitchen, Boolean isProductDisplay,
            int displayNumber, int defaultptr, double refund, double commission) {

        this.productid = productid;
        this.attsetinstid = attsetinstid;
        this.tax = tax;
        this.attributes = attributes;
        this.commission = commission;
        multiply = dMultiply;
        price = dPrice;
        m_sTicket = null;
        m_iLine = -1;
        refundQty = refund;

        attributes.setProperty("sendstatus", "No");
        attributes.setProperty("displayno", Integer.toString(displayNumber));
        attributes.setProperty("usedisplay", String.valueOf(isProductDisplay));
        attributes.setProperty("product.kitchen", String.valueOf(isProductKitchen));
        attributes.setProperty("kitchen.printer", String.valueOf(defaultptr));

        // add total commission override here
        // this.commission = 25.0;
    }

    private void init(String productid, String attsetinstid, double dMultiply,
            double dPrice, TaxInfo tax, Properties attributes, double refund, double commission) {

        this.productid = productid;
        this.attsetinstid = attsetinstid;
        multiply = dMultiply;
        price = dPrice;
        this.tax = tax;
        this.attributes = attributes;
        m_sTicket = null;
        m_iLine = -1;
        refundQty = refund;
        this.commission = commission;

        // add total commission override here
        // this.commission = 25.0;
    }

    public String getDefaultDisplay() {
        return attributes.getProperty("displayno");
    }

    public String getDefaultPrinter() {
        return attributes.getProperty("kitchen.printer");
    }

    public void setDefaultPrinter(String ptr) {
        attributes.setProperty("kitchen.printer", ptr);
    }

    public void setDefaultDisplay(String display) {
        attributes.setProperty("displayno", display);
    }

    public Boolean getPtrOverride() {
        return "true".equals(attributes.getProperty("product.ptroverride"));
    }

    void setTicket(String ticket, int line) {
        m_sTicket = ticket;
        m_iLine = line;
    }

    public String getTicket() {
        return m_sTicket;
    }

    public void setRefundTicket(String ticket, int line) {
        m_sTicket = ticket;
        m_iLine = line;
    }

    @Override
    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, m_sTicket);
        dp.setInt(2, m_iLine);
        dp.setString(3, productid);
        dp.setString(4, attsetinstid);
        dp.setDouble(5, multiply);
        dp.setDouble(6, price);
        dp.setString(7, tax.getId());

        try {
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            attributes.storeToXML(o, AppLocal.APP_NAME, "UTF-8");
            dp.setBytes(8, o.toByteArray());
        } catch (IOException e) {
            dp.setBytes(8, null);
        }
        dp.setDouble(9, refundQty);
        dp.setDouble(10, tax.getRate());
        dp.setDouble(11, tax.getRate() * price * multiply);
        dp.setDouble(12, commission);

    }

    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_sTicket = dr.getString(1);
        m_iLine = dr.getInt(2);
        productid = dr.getString(3);
        attsetinstid = dr.getString(4);
        multiply = dr.getDouble(5);
        price = dr.getDouble(6);
        tax = new TaxInfo(
                dr.getString(7),
                dr.getString(8),
                dr.getString(9),
                dr.getString(10),
                dr.getString(11),
                dr.getDouble(12),
                dr.getBoolean(13),
                dr.getInt(14));//,
        // dr.getString(15));
        attributes = new Properties();
        try {
            byte[] img = dr.getBytes(15);
            if (img != null) {
                attributes.loadFromXML(new ByteArrayInputStream(img));
            }
        } catch (IOException e) {
        }
        refundQty = dr.getDouble(16);
    }

    public TicketLineInfo copyTicketLine() {
        TicketLineInfo l = new TicketLineInfo();
        // l.m_sTicket = null;
        // l.m_iLine = -1;
        l.productid = productid;
        l.attsetinstid = attsetinstid;
        l.multiply = multiply;
        l.price = price;
        l.tax = tax;
        l.attributes = (Properties) attributes.clone();
        return l;
    }

    // getPromotionId is the Id of the promotion who's script is to be 
    // run if this item is added/removed or changed in a ticket. 
    // The promotion script is also called when the ticket is totaled or
    // subtotaled.
    public String getPromotionId() {
        return attributes.getProperty("product.promotionid");
    }

    // PromotionAdded is a flag indicating this item has been 
    // automatically added to a ticket by a promotion script
    // This item may subsequently be removed from the ticket, for example if it
    // is a free item from a Buy One Get One free script and the original item
    // is subsequently removed.
    public Boolean isPromotionAdded() {
        return "true".equals(attributes.getProperty("product.promotionadded"));
    }

    public void setPromotionAdded(Boolean value) {
        attributes.setProperty("product.promotionadded", value ? "true" : "false");
    }

    // ManageStock is a flag indicating whether stock diary entries are to  
    // be created on sales or refunds - it is used on products that do not get  
    // entered as new stock but do get sold through the till  
    // This flag prevents stock quantities going negative  
    public Boolean getManageStock() {
        return "true".equals(attributes.getProperty("product.managestock"));
    }

    public void setManageStock(Boolean value) {
        attributes.setProperty("product.managestock", value ? "true" : "false");
    }

    public int getTicketLine() {
        return m_iLine;
    }

    public void setTicketLine(int line) {
        m_iLine = line;
    }

    public Double getRefundQty() {
        return refundQty;
    }

    public void setRefundQty(double qty) {
        refundQty = qty;
    }

    public String getProductID() {
        return productid;
    }

    public Double getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(double qty) {
        orderQty = qty;
    }

    public void setProductID(String value) {
        productid = value;
    }

    public String getProductName() {
        return attributes.getProperty("product.name");
    }

    public String getProductAttSetId() {
        return attributes.getProperty("product.attsetid");
    }

    public String getProductAttSetInstDesc() {
        return attributes.getProperty("product.attsetdesc", "");
    }

    public void setProductAttSetInstDesc(String value) {
        if (value == null) {
            attributes.remove(value);
        } else {
            attributes.setProperty("product.attsetdesc", value);
        }
    }

    public String getProductAttSetInstId() {
        return attsetinstid;
    }

    public void setProductAttSetInstId(String value) {
        attsetinstid = value;
    }

    public boolean isProductCom() {
        return "true".equals(attributes.getProperty("product.com"));
    }

    public String getProductTaxCategoryID() {
        return (attributes.getProperty("product.taxcategoryid"));
    }

    public void setProductTaxCategoryID(String taxID) {
        attributes.setProperty("product.taxcategoryid", taxID);
    }

    public String getProductCategoryID() {
        return (attributes.getProperty("product.categoryid"));
    }

    public double getMultiply() {
        if (multiply < 0) {
            return multiply * -1;
        } else {
            return multiply;
        }
    }

    public void setMultiply(double dValue) {
        multiply = dValue;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double dValue) {
        price = dValue;
    }

    public double getPriceTax() {
        if (price < 0) {
            return price * -1 * (1.0 + getTaxRate());
        } else {
            return price * (1.0 + getTaxRate());
        }
    }

    public void setPriceTax(double dValue) {
        price = dValue / (1.0 + getTaxRate());
    }

    public TaxInfo getTaxInfo() {
        return tax;
    }

    public void setTaxInfo(TaxInfo value) {
        tax = value;
    }

    public String getProperty(String key) {
        return attributes.getProperty(key);
    }

    public String getProperty(String key, String defaultvalue) {
        return attributes.getProperty(key, defaultvalue);
    }

    public void setProperty(String key, String value) {
        attributes.setProperty(key, value);
    }

    public Properties getProperties() {
        return attributes;
    }

    public double getTaxRate() {
        return tax == null ? 0.0 : tax.getRate();
    }

    public double getSubValue() {
        return price * multiply;
    }

    public double getTax() {
        return price * multiply * getTaxRate();
    }

    public double getValue() {
        return price * multiply * (1.0 + getTaxRate());
    }

    public String printName() {
        return StringUtils.encodeXML(attributes.getProperty("product.name"));
    }

    public String printBarcode() {
        return StringUtils.encodeXML(attributes.getProperty("product.code"));
    }

    public String getBarcode() {
        return attributes.getProperty("product.code");
    }

    public String printMultiply() {
        return Formats.DOUBLE.formatValue(getMultiply());
    }

    public String printRefundQty() {
        return Formats.DOUBLE.formatValue(refundQty);
    }

    public String printPrice() {
        return Formats.CURRENCY.formatValue(getPrice());
    }

    public String printPriceTax() {
        return Formats.CURRENCY.formatValue(getPriceTax());
    }

    public String printTax() {
        return Formats.CURRENCY.formatValue(getTax());
    }

    public String printTaxRate() {
        return Formats.PERCENT.formatValue(getTaxRate());
    }

    public String printSubValue() {
        return Formats.CURRENCY.formatValue(getSubValue());
    }

    public String printValue() {
        return Formats.CURRENCY.formatValue(getValue());
    }

    public boolean isProductKitchen() {
        return "true".equals(attributes.getProperty("product.kitchen"));
    }

    public boolean isProductDisplay() {
        return "true".equals(attributes.getProperty("usedisplay"));
    }

    public boolean isProductService() {
        return "true".equals(attributes.getProperty("product.service"));
    }

    public boolean isProductVprice() {
        return "true".equals(attributes.getProperty("product.vprice"));
    }

    public boolean isProductVerpatrib() {
        return "true".equals(attributes.getProperty("product.verpatrib"));
    }

    public String printTextTip() {
        return attributes.getProperty("product.texttip");
    }

    public boolean isProductWarranty() {
        return "true".equals(attributes.getProperty("product.warranty"));
    }

    public boolean isAlwaysAvailable() {
        return "true".equals(attributes.getProperty("product.alwaysavailable"));
    }

    public boolean canDiscount() {
        return "true".equals(attributes.getProperty("product.candiscount"));
    }

    public String getDiscounted() {
        return (attributes.getProperty("product.discounted"));
    }

    public void setDiscounted(String value) {
        attributes.setProperty("product.discounted", value);
    }

    public String printAlias() {
        return StringUtils.encodeXML(attributes.getProperty("product.alias"));
    }

    public void setUpdated(Boolean value) {
        updated = value;
    }

    public boolean getUpdated() {
        return updated;
    }

    void incMultiply() {
    }
}
