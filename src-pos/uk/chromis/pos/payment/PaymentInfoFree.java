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

package uk.chromis.pos.payment;

/**
 *
 *   
 */
public class PaymentInfoFree extends PaymentInfo {
    
    private double m_dTotal;
    private double m_dTendered;
    private String m_dCardName =null;
   
    /** Creates a new instance of PaymentInfoFree
     * @param dTotal */
    public PaymentInfoFree(double dTotal) {
        m_dTotal = dTotal;
    }

    /**
     *
     * @return
     */
    @Override
    public PaymentInfo copyPayment(){
        return new PaymentInfoFree(m_dTotal);
    }    

    /**
     *
     * @return
     */
    @Override
    public String getTransactionID(){
        return "no ID";
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return "free";
    }   

    /**
     *
     * @return
     */
    @Override
    public double getTotal() {
        return m_dTotal;
    }

    /**
     *
     * @return
     */
    @Override
    public double getPaid() {
        return (0.0); 
    }
    
    /**
     *
     * @return
     */
    @Override
    public double getChange(){
       return (0.00);
   }
    
    /**
     *
     * @return
     */
    @Override
    public double getTendered() {
       return m_dTendered;
   }

    /**
     *
     * @return
     */
    @Override
    public String getCardName() {
       return m_dCardName;
   } 

}
