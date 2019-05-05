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

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataParams;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.QBFBuilder;
import uk.chromis.data.loader.SentenceExec;
import uk.chromis.data.loader.SentenceExecTransaction;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerRead;
import uk.chromis.data.loader.SerializerReadBasic;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.data.loader.SerializerWriteBasicExt;
import uk.chromis.data.loader.SerializerWriteParams;
import uk.chromis.data.loader.Session;
import uk.chromis.data.loader.StaticSentence;
import uk.chromis.data.loader.TableDefinition;
import uk.chromis.pos.forms.BeanFactoryDataSingle;

/**
 *
 * @author adrianromero
 */
public class DataLogicCustomers extends BeanFactoryDataSingle {

    /**
     * Main Method for customer object
     */
    protected Session s;
    private TableDefinition tcustomers;
    private static final Datas[] customerdatas = new Datas[]{
        Datas.STRING,
        Datas.TIMESTAMP,
        Datas.TIMESTAMP,
        Datas.STRING,
        Datas.STRING,
        Datas.STRING,
        Datas.STRING,
        Datas.INT,
        Datas.BOOLEAN,
        Datas.STRING};

    @Override
    public void init(Session s) {
        this.s = s;

    }

    public SentenceList getCustomerList(String siteGuid) {
        if (siteGuid == null) {
            return new StaticSentence(s, new QBFBuilder("SELECT ID, TAXID, SEARCHKEY, NAME, POSTAL, EMAIL, "
                    + "PHONE FROM CUSTOMERS WHERE VISIBLE = " + s.DB.TRUE()
                    + " AND ?(QBF_FILTER) ORDER BY LOWER (NAME)",
                    new String[]{"TAXID", "SEARCHKEY", "NAME", "POSTAL", "PHONE", "EMAIL"}), new SerializerWriteBasic(new Datas[]{
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING}), new SerializerRead() {
                @Override
                public Object readValues(DataRead dr) throws BasicException {
                    CustomerInfo c = new CustomerInfo(dr.getString(1));
                    c.setTaxid(dr.getString(2));
                    c.setSearchkey(dr.getString(3));
                    c.setName(dr.getString(4));
                    c.setPostal(dr.getString(5));
                    c.setPhone(dr.getString(6));
                    c.setEmail(dr.getString(7));

                    return c;
                }
            });
        } else {
            return new StaticSentence(s, new QBFBuilder("SELECT ID, TAXID, SEARCHKEY, NAME, POSTAL, EMAIL, "
                    + "PHONE FROM CUSTOMERS WHERE VISIBLE = " + s.DB.TRUE() + " AND SITEGUID ='" + siteGuid + "'"
                    + " AND ?(QBF_FILTER) ORDER BY LOWER (NAME)",
                    new String[]{"TAXID", "SEARCHKEY", "NAME", "POSTAL", "PHONE", "EMAIL"}), new SerializerWriteBasic(new Datas[]{
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING,
                Datas.OBJECT, Datas.STRING}), new SerializerRead() {
                @Override
                public Object readValues(DataRead dr) throws BasicException {
                    CustomerInfo c = new CustomerInfo(dr.getString(1));
                    c.setTaxid(dr.getString(2));
                    c.setSearchkey(dr.getString(3));
                    c.setName(dr.getString(4));
                    c.setPostal(dr.getString(5));
                    c.setPhone(dr.getString(6));
                    c.setEmail(dr.getString(7));

                    return c;
                }
            });

        }

    }

    /**
     *
     * @param customer
     * @return
     * @throws BasicException
     */
    public int updateCustomerExt(final CustomerInfoExt customer) throws BasicException {

        return new PreparedSentence(s, "UPDATE CUSTOMERS SET NOTES = ? WHERE ID = ?", SerializerWriteParams.INSTANCE
        ).exec(new DataParams() {
            @Override
            public void writeValues() throws BasicException {
                setString(1, customer.getNotes());
                setString(2, customer.getId());
            }
        });
    }

    /**
     *
     * @return customer's existing reservation (restaurant mode)
     */
    public final SentenceList getReservationsList() {
        return new PreparedSentence(s, "SELECT R.ID, R.CREATED, R.DATENEW, C.CUSTOMER, CUSTOMERS.TAXID, CUSTOMERS.SEARCHKEY, COALESCE(CUSTOMERS.NAME, R.TITLE),  R.CHAIRS, R.ISDONE, R.DESCRIPTION "
                + "FROM RESERVATIONS R LEFT OUTER JOIN RESERVATION_CUSTOMERS C ON R.ID = C.ID LEFT OUTER JOIN CUSTOMERS ON C.CUSTOMER = CUSTOMERS.ID "
                + "WHERE R.DATENEW >= ? AND R.DATENEW < ?", new SerializerWriteBasic(new Datas[]{Datas.TIMESTAMP, Datas.TIMESTAMP}), new SerializerReadBasic(customerdatas));
    }

    /**
     *
     * @return create/update customer reservation (restaurant mode)
     */
    public final SentenceExec getReservationsUpdate() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {

                new PreparedSentence(s, "DELETE FROM RESERVATION_CUSTOMERS WHERE ID = ?", new SerializerWriteBasicExt(customerdatas, new int[]{0})).exec(params);
                if (((Object[]) params)[3] != null) {
                    new PreparedSentence(s, "INSERT INTO RESERVATION_CUSTOMERS (ID, CUSTOMER) VALUES (?, ?)", new SerializerWriteBasicExt(customerdatas, new int[]{0, 3})).exec(params);
                }
                return new PreparedSentence(s, "UPDATE RESERVATIONS SET ID = ?, CREATED = ?, DATENEW = ?, TITLE = ?, CHAIRS = ?, ISDONE = ?, DESCRIPTION = ? WHERE ID = ?", new SerializerWriteBasicExt(customerdatas, new int[]{0, 1, 2, 6, 7, 8, 9, 0})).exec(params);
            }
        };
    }

    /**
     *
     * @return delete customer reservation (restaurant mode)
     */
    public final SentenceExec getReservationsDelete() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {

                new PreparedSentence(s, "DELETE FROM RESERVATION_CUSTOMERS WHERE ID = ?", new SerializerWriteBasicExt(customerdatas, new int[]{0})).exec(params);
                return new PreparedSentence(s, "DELETE FROM RESERVATIONS WHERE ID = ?", new SerializerWriteBasicExt(customerdatas, new int[]{0})).exec(params);
            }
        };
    }

    /**
     *
     * @return insert a new customer reservation (restaurant mode)
     */
    public final SentenceExec getReservationsInsert() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {

                int i = new PreparedSentence(s, "INSERT INTO RESERVATIONS (ID, CREATED, DATENEW, TITLE, CHAIRS, ISDONE, DESCRIPTION) VALUES (?, ?, ?, ?, ?, ?, ?)", new SerializerWriteBasicExt(customerdatas, new int[]{0, 1, 2, 6, 7, 8, 9})).exec(params);

                if (((Object[]) params)[3] != null) {
                    new PreparedSentence(s, "INSERT INTO RESERVATION_CUSTOMERS (ID, CUSTOMER) VALUES (?, ?)", new SerializerWriteBasicExt(customerdatas, new int[]{0, 3})).exec(params);
                }
                return i;
            }
        };
    }

    /**
     *
     * @return assign a table to a customer reservation (restaurant mode)
     */
    public final TableDefinition getTableCustomers() {
        return tcustomers;
    }
}
