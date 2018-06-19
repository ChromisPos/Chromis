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


package uk.chromis.pos.sales;

import java.util.ArrayList;
import java.util.List;
import uk.chromis.pos.ticket.TaxInfo;

/**
 *
 * @author adrianromero
 */
public class TaxesLogicElement {
    
    private TaxInfo tax;
    private List<TaxesLogicElement> taxsons;
    
    /**
     *
     * @param tax
     */
    public TaxesLogicElement(TaxInfo tax) {
        this.tax = tax;       
        this.taxsons = new ArrayList<>();
    }
    
    /**
     *
     * @return
     */
    public TaxInfo getTax() {
        return tax;
    }
    
    /**
     *
     * @return
     */
    public List<TaxesLogicElement> getSons() {
        return taxsons;
    }
}
