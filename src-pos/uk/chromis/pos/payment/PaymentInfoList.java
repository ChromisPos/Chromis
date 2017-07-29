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

import java.util.Iterator;
import java.util.LinkedList;

public class PaymentInfoList {

    private final LinkedList<PaymentInfo> m_apayment;
    private LinkedList<PaymentInfo> tmp_apayment;

    public PaymentInfoList() {
        m_apayment = new LinkedList<>();
    }

    public double getTotal() {

        double dTotal = 0.0;
        Iterator i = m_apayment.iterator();
        while (i.hasNext()) {
            PaymentInfo p = (PaymentInfo) i.next();
            dTotal += p.getTotal();
        }

        return dTotal;
    }

    public boolean isEmpty() {
        return m_apayment.isEmpty();
    }

    public void add(PaymentInfo p) {
        m_apayment.addLast(p);
    }

    public void removeLast() {
        m_apayment.removeLast();
    }

    public void sortPayments(Double m_dTotal) {
        tmp_apayment = (LinkedList<PaymentInfo>) m_apayment.clone();
        m_apayment.clear();
        double dPaidOther = 0.0;
        double dPaidCash = 0.0;
        Boolean cash = false;
        for (PaymentInfo p : tmp_apayment) {
            if (!p.getName().equals("cash")) {
                m_apayment.add(p);
                dPaidOther = dPaidOther + p.getTotal();
            } else {
                cash = true;
                dPaidCash = dPaidCash + p.getPaid();
            }
        }
        if (cash) {
            //if (dPaidOther == 0.00) {
            if (dPaidCash < m_dTotal - dPaidOther) {
                // m_apayment.add(new PaymentInfoCash_original(m_dTotal, dPaidCash));
                m_apayment.add(new PaymentInfoCash_original(dPaidCash, dPaidCash));
            } else {
                m_apayment.add(new PaymentInfoCash_original(m_dTotal - dPaidOther, dPaidCash));
            }
        }

    }

    public LinkedList<PaymentInfo> getPayments() {
        return m_apayment;
    }
}
