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

package uk.chromis.pos.customers;

import uk.chromis.data.user.BrowsableEditableData;


public class CustomerInfoGlobal {

    private static CustomerInfoGlobal INSTANCE;
    private CustomerInfoExt customerInfoExt;
    private BrowsableEditableData editableData;

    //Singleton class
    private CustomerInfoGlobal() {
    }

    //Singleton constructor

    /**
     *
     * @return
     */
        public static CustomerInfoGlobal getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CustomerInfoGlobal();
        }

        return INSTANCE;
    }

    /**
     *
     * @return
     */
    public CustomerInfoExt getCustomerInfoExt() {
        return customerInfoExt;
    }

    /**
     *
     * @param customerInfoExt
     */
    public void setCustomerInfoExt(CustomerInfoExt customerInfoExt) {
        this.customerInfoExt = customerInfoExt;
    }

    /**
     *
     * @return
     */
    public BrowsableEditableData getEditableData() {
        return editableData;
}

    /**
     *
     * @param editableData
     */
    public void setEditableData(BrowsableEditableData editableData) {
        this.editableData = editableData;
    }
    

}
