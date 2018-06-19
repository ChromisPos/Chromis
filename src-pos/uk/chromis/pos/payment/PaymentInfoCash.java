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

import uk.chromis.format.Formats;

/**
 *
 *
 */
public class PaymentInfoCash extends PaymentInfo {

    private double prePayAmount = 0.0;
    private double m_dPaid;
    private double m_dTotal;
//    private double m_dTendered;
    private String m_dCardName = null;

    /**
     * Creates a new instance of PaymentInfoCash
     *
     * @param dTotal
     * @param dPaid
     * @param dTendered
     */
    public PaymentInfoCash(double dTotal, double dPaid, double dTendered) {
        m_dTotal = dTotal;
        m_dPaid = dPaid;
        //      m_dTendered = dTendered;
    }

    /**
     * Creates a new instance of PaymentInfoCash
     *
     * @param dTotal
     * @param dPaid
     * @param dTendered
     * @param prePayAmount
     */
    public PaymentInfoCash(double dTotal, double dPaid, double dTendered, double prePayAmount) {
        this(dTotal, dTendered, dPaid);
        this.prePayAmount = prePayAmount;
    }

    /**
     *
     * @return
     */
    @Override
    public PaymentInfo copyPayment() {
        //    return new PaymentInfoCash(m_dTotal, m_dPaid, m_dTendered, prePayAmount);
        return new PaymentInfoCash(m_dTotal, m_dPaid, prePayAmount);
    }

    /**
     *
     * @return
     */
    @Override
    public String getTransactionID() {
        return "no ID";
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return "cash";
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
        return m_dPaid;
    }

    /**
     *
     * @return
     */
    @Override
    public double getTendered() {
        //   return m_dTendered;
        return m_dPaid;
    }

    /**
     *
     * @return
     */
    @Override
    public double getChange() {
        return m_dPaid - m_dTotal;
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
    public boolean hasPrePay() {
        return prePayAmount > 0;
    }

    /**
     *
     * @return
     */
    public double getPrePaid() {
        return prePayAmount;
    }

    /**
     *
     * @return
     */
    public String printTendered() {
        //     return Formats.CURRENCY.formatValue(m_dTendered);
        return printPaid();
    }

    /**
     *
     * @return
     */
    public String printPaid() {
        return Formats.CURRENCY.formatValue(m_dPaid);
    }

    /**
     *
     * @return
     */
    public String printChange() {
        return Formats.CURRENCY.formatValue(new Double(m_dPaid - m_dTotal));
    }

}
