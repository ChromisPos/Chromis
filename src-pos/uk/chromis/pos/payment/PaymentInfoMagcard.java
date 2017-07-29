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
public class PaymentInfoMagcard extends PaymentInfo {
     
    /**
     *
     */
    protected double m_dTotal;
    
    /**
     *
     */
    protected String m_sHolderName;

    /**
     *
     */
    protected String m_sCardNumber;

    /**
     *
     */
    protected String m_sExpirationDate;

    /**
     *
     */
    protected String track1;

    /**
     *
     */
    protected String track2;

    /**
     *
     */
    protected String track3;
    
    /**
     *
     */
    protected String m_sTransactionID;
    
    /**
     *
     */
    protected String m_sAuthorization;    

    /**
     *
     */
    protected String m_sErrorMessage;

    /**
     *
     */
    protected String m_sReturnMessage;

    /**
     *
     */
    protected String m_dCardName =null; 
  
    /** Creates a new instance of PaymentInfoMagcard
     * @param sHolderName
     * @param sCardNumber
     * @param track3
     * @param sExpirationDate
     * @param track2
     * @param track1
     * @param sTransactionID
     * @param dTotal */
    public PaymentInfoMagcard(
            String sHolderName, 
            String sCardNumber, 
            String sExpirationDate, 
            String track1, 
            String track2, 
            String track3, 
            String sTransactionID, 
            double dTotal) {
        
        m_sHolderName = sHolderName;
        m_sCardNumber = sCardNumber;
        m_sExpirationDate = sExpirationDate;
        this.track1 = track1;
        this.track2 = track2;
        this.track3 = track3;
        
        m_sTransactionID = sTransactionID;
        m_dTotal = dTotal;
        
        m_sAuthorization = null;
        m_sErrorMessage = null;
        m_sReturnMessage = null;
    }
    
    /** Creates a new instance of PaymentInfoMagcard
     * @param sHolderName
     * @param sCardNumber
     * @param sExpirationDate
     * @param dTotal
     * @param sTransactionID */
    public PaymentInfoMagcard(
            String sHolderName, 
            String sCardNumber, 
            String sExpirationDate, 
            String sTransactionID, 
            double dTotal) {
        this(sHolderName, sCardNumber, sExpirationDate, null, null, null, sTransactionID, dTotal);
    }
    
    /**
     *
     * @return
     */
    @Override
    public PaymentInfo copyPayment(){
        PaymentInfoMagcard p = new PaymentInfoMagcard(
                m_sHolderName, 
                m_sCardNumber, 
                m_sExpirationDate, 
                track1, 
                track2, 
                track3, 
                m_sTransactionID, 
                m_dTotal);
        
        p.m_sAuthorization = m_sAuthorization;
        p.m_sErrorMessage = m_sErrorMessage;
        return p;
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return "magcard";
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
    public boolean isPaymentOK() {
        return m_sAuthorization != null;
    }    

    /**
     *
     * @return
     */
    public String getHolderName() {
        return m_sHolderName;
    }
    
    /**
     *
     * @return
     */
    @Override
  public String getCardName() {
       return getCardType(m_sCardNumber);
   }
  
    /**
     *
     * @return
     */
    public String getCardNumber() {
        return m_sCardNumber;
    }

    /**
     *
     * @return
     */
    public String getExpirationDate() {
        return m_sExpirationDate;
    }    

    /**
     *
     * @return
     */
    @Override
    public String getTransactionID() {
        return m_sTransactionID;
    }

    /**
     *
     * @param sCardNumber
     * @return
     */
    public String getCardType(String sCardNumber){
        String c = "UNKNOWN";
        
       if (sCardNumber.startsWith("4")) {
           c = "VISA";
       } else if (sCardNumber.startsWith("6")) {
           c = "DISC";
       } else if (sCardNumber.startsWith("5")) {
           c = "MAST";
       } else if (sCardNumber.startsWith("34") || sCardNumber.startsWith("37")) {
           c = "AMEX";
       } else if (sCardNumber.startsWith("3528") || sCardNumber.startsWith("3589")) {
           c = "JCB";
       } else if (sCardNumber.startsWith("3")) {
           c = "DINE";
       }
       m_dCardName = c;
       return c;
   }
   
    /**
     * Get tracks of magnetic card.
     *   Framing characters: 
     *    - start sentinel (SS)
     *    - end sentinel (ES) 
     *    - LRC 
     * @param framingChar 
     *    true: including framing characters
     *    false: excluding framing characters
     * @return tracks of the magnetic card
     */
    public String getTrack1(boolean framingChar) {
        return (framingChar)
            ? track1
            : track1.substring(1, track1.length()-2);
    }

    /**
     *
     * @param framingChar
     * @return
     */
    public String getTrack2(boolean framingChar) {
        return (framingChar)
            ? track2
            : track2.substring(1, track2.length()-2);
    }

    /**
     *
     * @param framingChar
     * @return
     */
    public String getTrack3(boolean framingChar) {
        return (framingChar)
            ? track3
            : track3.substring(1, track3.length()-2);
    }
    
    /**
     *
     * @return
     */
    public String getAuthorization() {
        return m_sAuthorization;
    }

    /**
     *
     * @return
     */
    public String getMessage() {
        return m_sErrorMessage;
    }
    
    /**
     *
     * @param sMessage
     * @param moreInfo
     */
    public void paymentError(String sMessage, String moreInfo) {
        m_sAuthorization = null;
        m_sErrorMessage = sMessage + "\n" + moreInfo;
    }

    /**
     *
     * @param returnMessage
     */
    public void setReturnMessage(String returnMessage){
        m_sReturnMessage = returnMessage;
    }
    
    /**
     *
     * @return
     */
    public String getReturnMessage(){
        return m_sReturnMessage;
    }
    
    /**
     *
     * @param sAuthorization
     * @param sTransactionId
     * @param sReturnMessage
     */
    public void paymentOK(String sAuthorization, String sTransactionId, String sReturnMessage) {
        m_sAuthorization = sAuthorization;
        m_sTransactionID = sTransactionId;
        m_sReturnMessage = sReturnMessage;
        m_sErrorMessage = null;
    }  

    /**
     *
     * @return
     */
    public String printCardNumber() {
        // hide start numbers
        if (m_sCardNumber.length() > 4) {

            return m_sCardNumber.substring(0, m_sCardNumber.length()-4).replaceAll(".", "*") +
                    m_sCardNumber.substring(m_sCardNumber.length() - 4);
        } else {
            return "**** **** **** ****";
        }
    }

    /**
     *
     * @return
     */
    public String printExpirationDate() {
        return m_sExpirationDate;
    }

    /**
     *
     * @return
     */
    public String printAuthorization() {
        return m_sAuthorization;
    }

    /**
     *
     * @return
     */
    public String printTransactionID() {
        return m_sTransactionID;
    }

    /**
     *
     * @return
     */
    @Override
    public double getPaid() {
        return m_dTotal; 
    }

    /**
     *
     * @return
     */
    @Override
    public double getChange(){
       return 0.00;
   }   

    /**
     *
     * @return
     */
    @Override
    public double getTendered() {
        return 0.00;
    }
  
}