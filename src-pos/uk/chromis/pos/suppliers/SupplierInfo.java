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
package uk.chromis.pos.suppliers;

import uk.chromis.data.loader.IKeyed;

public class SupplierInfo implements IKeyed {
   
    private String m_id;
    private String m_SupplierName;
    private Boolean m_SupplierActive;
    private String m_SupplierAccount;
    private String m_SupplierAddr1;
    private String m_SupplierAddr2;
    private String m_SupplierCity;
    private String m_SupplierComments;
    private String m_SupplierContact;
    private Double m_SupplierCredit;
    private String m_SupplierEmail;
    private String m_SupplierPostCode;
    private String m_SupplierTelephone;
    private String m_SupplierTerms;

    public SupplierInfo(String id, String supplierName, Boolean supplierActive, String supplierAccount, String supplierAddr1,
            String supplierAddr2, String supplierCity, String supplierComments, String supplierContact, Double supplierCredit,
            String supplierEmail, String supplierPostCode, String supplierTelephone, String supplierTerms) {

        m_id = id;
        m_SupplierName = supplierName;
        m_SupplierActive = supplierActive;
        m_SupplierAccount = supplierAccount;
        m_SupplierAddr1 = supplierAddr1;
        m_SupplierAddr2 = supplierAddr2;
        m_SupplierCity = supplierCity;
        m_SupplierComments = supplierComments;
        m_SupplierContact = supplierContact;
        m_SupplierCredit = supplierCredit;
        m_SupplierEmail = supplierEmail;
        m_SupplierPostCode = supplierPostCode;
        m_SupplierTelephone = supplierTelephone;
        m_SupplierTerms = supplierTerms;
    }

    public SupplierInfo(String id, String supplierName) {
        m_id = id;
        m_SupplierName = supplierName;
    }

    @Override
    public Object getKey() {        
        return m_id;
    }

    public String toString() {
        return m_SupplierName;
    }
    
    public String getid() {
        return m_id;
    }

    public void setid(String id) {
        m_id = id;
    }

    public Boolean getSupplierActive() {
        return m_SupplierActive;
    }

    public void setSupplierActive(Boolean supplierActive) {
        m_SupplierActive = supplierActive;
    }

    public String getSupplierAccount() {
        return m_SupplierAccount;
    }

    public void setSupplierAccount(String supplierAccount) {
        m_SupplierAccount = supplierAccount;
    }

    public String getSupplierAddr1() {
        return m_SupplierAddr1;
    }

    public void setSupplierAddr1(String supplierAddr1) {
        m_SupplierAddr1 = supplierAddr1;
    }

    public String getSupplierAddr2() {
        return m_SupplierAddr2;
    }

    public void setSupplierAddr2(String supplierAddr2) {
        m_SupplierAddr2 = supplierAddr2;
    }

    public String getM_SupplierCity() {
        return m_SupplierCity;
    }

    public void setSupplierCity(String supplierCity) {
        m_SupplierCity = supplierCity;
    }

    public String getSupplierComments() {
        return m_SupplierComments;
    }

    public void setSupplierComments(String supplierComments) {
        m_SupplierComments = supplierComments;
    }

    public String getSupplierContact() {
        return m_SupplierContact;
    }

    public void setSupplierContact(String supplierContact) {
        m_SupplierContact = supplierContact;
    }

    public Double getSupplierCredit() {
        return m_SupplierCredit;
    }

    public void setSupplierCredit(Double supplierCredit) {
        m_SupplierCredit = supplierCredit;
    }

    public String getSupplierEmail() {
        return m_SupplierEmail;
    }

    public void setSupplierEmail(String supplierEmail) {
        m_SupplierEmail = supplierEmail;
    }

    public String getSupplierName() {
        return m_SupplierName;
    }

    public void setSupplierName(String supplierName) {
        m_SupplierName = supplierName;
    }

    public String getupplierPostCode() {
        return m_SupplierPostCode;
    }

    public void setSupplierPostCode(String supplierPostCode) {
        m_SupplierPostCode = supplierPostCode;
    }

    public String getSupplierTelephone() {
        return m_SupplierTelephone;
    }

    public void setSupplierTelephone(String supplierTelephone) {
        m_SupplierTelephone = supplierTelephone;
    }

    public String getSupplierTerms() {
        return m_SupplierTerms;
    }

    public void setSupplierTerms(String supplierTerms) {
        m_SupplierTerms = supplierTerms;
    }

}
