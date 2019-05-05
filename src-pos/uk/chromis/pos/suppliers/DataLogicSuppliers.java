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

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.*;
import uk.chromis.pos.forms.BeanFactoryDataSingle;

public class DataLogicSuppliers extends BeanFactoryDataSingle {

    private Session s;
    protected SentenceFind m_getSupplier;

    @Override
    public void init(Session s) {
        this.s = s;

        m_getSupplier = new PreparedSentence(s, "SELECT SUPPLIERNAME FROM SUPPLIERS WHERE ID = ? ", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);

    }

    public final String getSupplierName(String id) {
        try {
            return (String) m_getSupplier.find(id);
        } catch (BasicException ex) {
            return null;
        }
    }

    public final SentenceList getSupplier(String id) {
        return new StaticSentence(s, "SELECT "
                + "ID, "
                + "SUPPLIERNAME "
                + "FROM SUPPLIERS "
                + "WHERE ACTIVE = TRUE AND SITEGUID = '"
                + id
                + "' ORDER BY SUPPLIERNAME", SerializerWriteString.INSTANCE, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new SupplierInfo(dr.getString(1), dr.getString(2));
            }
        });
    }
    
    public final SentenceList getSuppliersList(String siteGuid) {
        return new StaticSentence(s, "SELECT "
                + "ID, "
                + "SUPPLIERNAME "
                + "FROM SUPPLIERS "
                + "WHERE ACTIVE = TRUE AND SITEGUID = '"
                + siteGuid
                + "' ORDER BY SUPPLIERNAME", null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new SupplierInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    
    
    
    
}
