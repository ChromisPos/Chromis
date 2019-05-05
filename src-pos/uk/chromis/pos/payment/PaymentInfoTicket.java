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

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.SerializableRead;
import uk.chromis.format.Formats;

/**
 *
 *
 */
public class PaymentInfoTicket extends PaymentInfo implements SerializableRead {

    private static final long serialVersionUID = 8865238639097L;
    private double m_dTicket;
    private String m_sName;
    private String m_transactionID;
    private double m_dTendered;
    //  private double m_change;
    private double m_dChange;
    private String m_dCardName = null;

    /**
     * Creates a new instance of PaymentInfoCash
     *
     * @param dTicket
     * @param sName
     */
    public PaymentInfoTicket(double dTicket, String sName) {
        m_sName = sName;
        m_dTicket = dTicket;
    }

    /**
     *
     * @param dTicket
     * @param sName
     * @param transactionID
     */
    public PaymentInfoTicket(double dTicket, String sName, String transactionID) {
        m_sName = sName;
        m_dTicket = dTicket;
        m_transactionID = transactionID;
    }

    /**
     *
     */
    public PaymentInfoTicket() {
        m_sName = null;
        m_dTicket = 0.0;
        m_transactionID = null;
        m_dTendered = 0.00;
    }

    /**
     *
     * @param dr
     * @throws BasicException
     */
    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_sName = dr.getString(1);
        m_dTicket = dr.getDouble(2);
        m_transactionID = dr.getString(3);
        if (dr.getDouble(4) != null) {
            m_dTendered = dr.getDouble(4);
        }
        m_dCardName = dr.getString(5);
        m_dChange = m_dTendered - m_dTicket;
    }

    /**
     *
     * @return
     */
    @Override
    public PaymentInfo copyPayment() {
        return new PaymentInfoTicket(m_dTicket, m_sName);
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return m_sName;
    }

    /**
     *
     * @return
     */
    @Override
    public double getTotal() {
        return m_dTicket;
    }

    /**
     *
     * @return
     */
    @Override
    public String getTransactionID() {
        return m_transactionID;
    }

    /**
     *
     * @return
     */
    @Override
    public double getPaid() {
        //  return (0.0); 
        if (m_dTendered != 0) {
            return m_dTendered;
        } else {
            return m_dTicket;
        }

    }

    /**
     *
     * @return
     */
    @Override
    public double getChange() {
        return m_dTendered - m_dTicket;
    }

    /**
     *
     * @return
     */
    @Override
    public double getTendered() {
        return (0.00);
    }

    /**
     *
     * @return
     */
    @Override
    public String getCardName() {
        return m_dCardName;
    }

    /**
     *
     * @return
     */
    public String printPaid() {
     //   return Formats.CURRENCY.formatValue(m_dTicket);
        return Formats.CURRENCY.formatValue(getPaid());
    }

    // Especificas
    /**
     *
     * @return
     */
    public String printPaperTotal() {
        // En una devolucion hay que cambiar el signo al total
        return Formats.CURRENCY.formatValue(-m_dTicket);
    }

    /**
     *
     * @return
     */
    public String printChange() {
        return Formats.CURRENCY.formatValue(m_dTendered - m_dTicket);
    }

    /**
     *
     * @return
     */
    public String printTendered() {
        return Formats.CURRENCY.formatValue(m_dTendered);
    }

}
