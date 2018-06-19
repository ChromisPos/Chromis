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

package uk.chromis.pos.forms;

import java.util.HashMap;

/**
 *
 * @author John L July 2013
 */
public class Payments {
    private Double amount;
    private Double tendered;
    private HashMap paymentPaid;
    private HashMap paymentTendered;
    private HashMap rtnMessage;
    private String name;

    /**
     *
     */
    public Payments() {
    paymentPaid =  new HashMap();
    paymentTendered =  new HashMap();
    rtnMessage = new HashMap();
     
    }

    /**
     *
     * @param pName
     * @param pAmountPaid
     * @param pTendered
     * @param rtnMsg
     */
    public void addPayment (String pName, Double pAmountPaid, Double pTendered, String rtnMsg){
        if (paymentPaid.containsKey(pName)){
            paymentPaid.put(pName,Double.parseDouble(paymentPaid.get(pName).toString()) + pAmountPaid);
            paymentTendered.put(pName,Double.parseDouble(paymentTendered.get(pName).toString()) + pTendered); 
            rtnMessage.put(pName, rtnMsg);
        }else {    
            paymentPaid.put(pName, pAmountPaid);
            paymentTendered.put(pName,pTendered);
            rtnMessage.put(pName, rtnMsg);
        }        
}

    /**
     *
     * @param pName
     * @return
     */
    public Double getTendered (String pName){
    return(Double.parseDouble(paymentTendered.get(pName).toString()));
}

    /**
     *
     * @param pName
     * @return
     */
    public Double getPaidAmount (String pName){
    return(Double.parseDouble(paymentPaid.get(pName).toString()));
}

    /**
     *
     * @return
     */
    public Integer getSize(){
    return (paymentPaid.size());
}

    /**
     *
     * @param pName
     * @return
     */
    public String getRtnMessage(String pName){
    return (rtnMessage.get(pName).toString());
}

    /**
     *
     * @return
     */
    public String getFirstElement(){
    String rtnKey= paymentPaid.keySet().iterator().next().toString();
    return(rtnKey);
}

    /**
     *
     * @param pName
     */
    public void removeFirst (String pName){
   paymentPaid.remove(pName);
   paymentTendered.remove(pName);
   rtnMessage.remove(pName);
}

}