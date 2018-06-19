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

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.SerializerRead;


/**
 *
 * @author John
 */
public class RecipeKitInfo {
    private static final long serialVersionUID = 7587646873036L;
    
    protected String id;
    protected String productId;
    protected String productKitId;
    protected Double quantity;

    /**
     * 
     * @param id
     * @param product
     * @param productKit
     * @param quantity 
     */
    public RecipeKitInfo(String id, String productId, String productKitId, Double quantity) {
        this.id = id;
        this.productId = productId;
        this.productKitId = productKitId;
        this.quantity = quantity;
    }
    

    public void setM_ID(String id) {
        this.id = id;
    }

    public void setM_sProduct(String productId) {
        this.productId = productId;
    }

    public void setM_sProductKit(String productKitId) {
        this.productKitId = productKitId;
    }

    public void setM_dQuantity(Double m_dQuantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductKitId() {
        return productKitId;
    }

    public Double getQuantity() {
        return quantity;
    }
    
    
    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new RecipeKitInfo(dr.getString(1), dr.getString(2), dr.getString(3), dr.getDouble(4));
        }};
    }
    
}
    
