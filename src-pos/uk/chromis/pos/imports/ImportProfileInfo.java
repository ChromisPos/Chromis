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

import java.io.Serializable;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.DataWrite;
import uk.chromis.data.loader.SerializableRead;
import uk.chromis.data.loader.SerializableWrite;

public class ImportProfileInfo implements Serializable {

    private static final long serialVersionUID = 6608012948284450199L;

    //private String separator;
    private String reference;
    private String barcode;
    private String name;
    private String shortName;
    private String buy;
    private String sell;
    private String security;
    private String maximum;

    private String category;
    private String defaultCategory;
    private String tax;

    private String warranty;
    private String varPrice;
    private String buttonText;
    private String prop;
    private String ispack;
    private String packOf;

    private String service;
    private String aux;
    private String remotePrint;
    private String textTip;
    private String packSize;

    private Boolean createCategory;
    private Boolean addStockLevels;
    private Boolean inCatalogue;
    private Boolean sellIncTax;
    private Boolean allowDiscount;

    public ImportProfileInfo(//String separator, 
            String reference, String barcode, String name, String shortName, String buy,
            String sell, String security, String maximum, String category, String defaultCategory, String tax, String warranty,
            String varPrice, String buttonText, String prop, String ispack, String packOf, String service, String aux, String remotePrint,
            String textTip, String packSize, Boolean createCategory, Boolean addStockLevels, Boolean inCatalogue, Boolean sellIncTax,
            Boolean allowDiscount) {

        //this.separator = separator;
        this.reference = reference;
        this.barcode = barcode;
        this.name = name;
        this.shortName = shortName;
        this.buy = buy;
        this.sell = sell;
        this.security = security;
        this.maximum = maximum;
        this.category = category;
        this.defaultCategory = defaultCategory;
        this.tax = tax;
        this.warranty = warranty;
        this.varPrice = varPrice;
        this.buttonText = buttonText;
        this.prop = prop;
        this.ispack = ispack;
        this.packOf = packOf;
        this.service = service;
        this.aux = aux;
        this.remotePrint = remotePrint;
        this.textTip = textTip;
        this.packSize = packSize;
        this.createCategory = createCategory;
        this.addStockLevels = addStockLevels;
        this.inCatalogue = inCatalogue;
        this.sellIncTax = sellIncTax;
        this.allowDiscount = allowDiscount;
    }

    public ImportProfileInfo() {

    }

  //  public String getSeparator() {
  //      return separator;
  //  }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getReference() {
        return reference;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getBuy() {
        return buy;
    }

    public String getSell() {
        return sell;
    }

    public String getSecurity() {
        return security;
    }

    public String getMaximum() {
        return maximum;
    }

    public String getCategory() {
        return category;
    }

    public String getDefaultCategory() {
        return defaultCategory;
    }

    public String getTax() {
        return tax;
    }

    public String getWarranty() {
        return warranty;
    }

    public String getVarPrice() {
        return varPrice;
    }

    public String getButtonText() {
        return buttonText;
    }

    public String getProp() {
        return prop;
    }

    public String getIspack() {
        return ispack;
    }

    public String getPackOf() {
        return packOf;
    }

    public String getService() {
        return service;
    }

    public String getAux() {
        return aux;
    }

    public String getRemotePrint() {
        return remotePrint;
    }

    public String getTextTip() {
        return textTip;
    }

    public String getPackSize() {
        return packSize;
    }

    public Boolean getCreateCategory() {
        return createCategory;
    }

    public Boolean getAddStockLevels() {
        return addStockLevels;
    }

    public Boolean getInCatalogue() {
        return inCatalogue;
    }

    public Boolean getSellIncTax() {
        return sellIncTax;
    }

    public Boolean getAllowDiscount() {
        return allowDiscount;
    }

}

//