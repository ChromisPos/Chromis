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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SentenceExec;
import uk.chromis.data.loader.SentenceExecTransaction;
import uk.chromis.data.loader.SerializerRead;
import uk.chromis.data.loader.SerializerWriteBasicExt;
import uk.chromis.data.model.Field;
import uk.chromis.data.model.Row;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.format.Formats;
import uk.chromis.pos.admin.JParamsSites;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.panels.JPanelTable2;
import uk.chromis.pos.sync.DataLogicSync;

public class CustomersPanel extends JPanelTable2 {

    private JParamsSites m_paramsSite;
    private DataLogicSync dlSync;
    private CustomersView jeditor;

    public CustomersPanel() {
        CustomerInfoGlobal.getInstance().setEditableData(bd);
    }

    @Override
    protected void init() {
        DataLogicCustomers dlCustomers = (DataLogicCustomers) app.getBean("uk.chromis.pos.customers.DataLogicCustomers");
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        m_paramsSite = new JParamsSites(true);
        m_paramsSite.setVisible(dlSync.isCentral());
        m_paramsSite.init(app);
        m_paramsSite.addActionListener(new CustomersPanel.ReloadActionListener());

        row = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field("TAXID", Datas.STRING, Formats.STRING),
                new Field("SEARCHKEY", Datas.STRING, Formats.STRING),
                new Field("NAME", Datas.STRING, Formats.STRING, true, true, true),
                new Field("NOTES", Datas.STRING, Formats.STRING),
                new Field("VISIBLE", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("CARD", Datas.STRING, Formats.STRING),
                new Field("MAXDEBT", Datas.DOUBLE, Formats.DOUBLE),
                new Field("CURDATE", Datas.TIMESTAMP, Formats.TIMESTAMP),
                new Field("CURDEBT", Datas.DOUBLE, Formats.DOUBLE),
                new Field("FIRSTNAME", Datas.STRING, Formats.STRING),
                new Field("LASTNAME", Datas.STRING, Formats.STRING),
                new Field("EMAIL", Datas.STRING, Formats.STRING),
                new Field("PHONE", Datas.STRING, Formats.STRING),
                new Field("PHONE2", Datas.STRING, Formats.STRING),
                new Field("FAX", Datas.STRING, Formats.STRING),
                new Field("ADDRESS", Datas.STRING, Formats.STRING),
                new Field("ADDRESS2", Datas.STRING, Formats.STRING),
                new Field("POSTAL", Datas.STRING, Formats.STRING),
                new Field("CITY", Datas.STRING, Formats.STRING),
                new Field("REGION", Datas.STRING, Formats.STRING),
                new Field("COUNTRY", Datas.STRING, Formats.STRING),
                new Field("TAXCATEGORY", Datas.STRING, Formats.STRING),
                new Field("IMAGE", Datas.IMAGE, Formats.NULL),
                new Field("DOB", Datas.TIMESTAMP, Formats.TIMESTAMP),
                new Field("DISCOUNT", Datas.DOUBLE, Formats.DOUBLE),
                new Field("SITEGUID", Datas.STRING, Formats.STRING)
        );

        lpr = new ListProviderCreator(new PreparedSentence(app.getSession(),
                "SELECT ID, TAXID, SEARCHKEY, NAME, NOTES, VISIBLE, CARD, MAXDEBT, CURDATE, CURDEBT, FIRSTNAME, LASTNAME, EMAIL, PHONE, PHONE2, "
                + "FAX, ADDRESS, ADDRESS2, POSTAL, CITY, REGION, COUNTRY, TAXCATEGORY, IMAGE, DOB, DISCOUNT, SITEGUID "
                + "FROM CUSTOMERS "
                + "WHERE SITEGUID = ? "
                + "ORDER BY LOWER (NAME)",
                new SerializerWriteBasicExt(new Datas[]{Datas.OBJECT, Datas.STRING}, new int[]{1}),
                new CustomersPanel.CustomersSerializerRead()
        ),
                m_paramsSite);

        SentenceExec updatesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                if (values[26] == null) {
                    // INSERT
                    values[26] = m_paramsSite.getSelectKey();
                    return new PreparedSentence(app.getSession(), "INSERT INTO CUSTOMERS (ID, TAXID, SEARCHKEY, NAME, NOTES, VISIBLE, CARD, MAXDEBT, FIRSTNAME, LASTNAME, EMAIL, PHONE, PHONE2, "
                            + "FAX, ADDRESS, ADDRESS2, POSTAL, CITY, REGION, COUNTRY, TAXCATEGORY, IMAGE, DOB, DISCOUNT, SITEGUID )"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26})).exec(params);
                } else {
                    // UPDATE
                    return new PreparedSentence(app.getSession(), "UPDATE CUSTOMERS SET TAXID = ?, SEARCHKEY = ?, NAME = ?, NOTES = ?, VISIBLE = ?, CARD = ?, MAXDEBT = ?, FIRSTNAME = ?, LASTNAME = ?, EMAIL = ?, PHONE = ?, PHONE2 = ?, FAX = ?, ADDRESS = ?, ADDRESS2 = ?, "
                            + "POSTAL = ?, CITY = ?, REGION = ?, COUNTRY = ?, TAXCATEGORY = ?, IMAGE = ?, DOB = ?, DISCOUNT = ?  WHERE ID = ? AND SITEGUID = ? ",
                            new SerializerWriteBasicExt(row.getDatas(), new int[]{1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 0, 26})).exec(params);
                }
            }
        };

        SentenceExec insertsent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                // INSERT
                values[0] = UUID.randomUUID().toString();
                values[26] = m_paramsSite.getSelectKey();
                return new PreparedSentence(app.getSession(), "INSERT INTO CUSTOMERS (ID, TAXID, SEARCHKEY, NAME, NOTES, VISIBLE, CARD, MAXDEBT, FIRSTNAME, LASTNAME, EMAIL, PHONE, PHONE2, "
                        + "FAX, ADDRESS, ADDRESS2, POSTAL, CITY, REGION, COUNTRY, TAXCATEGORY, IMAGE, DOB, DISCOUNT, SITEGUID )"
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26})).exec(params);
            }
        };

        SentenceExec deletesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                values[26] = m_paramsSite.getSelectKey();
                return new PreparedSentence(app.getSession(), "DELETE FROM CUSTOMERS WHERE ID = ? AND SITEGUID = ? ", new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 26})).exec(params);
            }
        };

        spr = new SaveProvider(updatesent, insertsent, deletesent);

        if (AppConfig.getInstance().getBoolean("display.longnames")) {
            setListWidth(300);
        }

        jeditor = new CustomersView(app, dirty, dlSync.getSiteGuid());
    }

    @Override
    public void activate() throws BasicException {
        m_paramsSite.activate();
        jeditor.activate();
        super.activate();
    }

    @Override
    public Component getFilter() {
        if (dlSync.isCentral()) {
            return m_paramsSite.getComponent();
        } else {
            return null;
        }
    }

    @Override
    public EditorRecord getEditor() {
        return jeditor;
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.CustomersManagement");
    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                jeditor.refreshGuid(m_paramsSite.getSelectKey().toString());
                CustomersPanel.this.bd.actionLoad();
            } catch (BasicException w) {
            }
        }
    }

    private class CustomersSerializerRead implements SerializerRead {

        @Override
        public Object readValues(DataRead dr) throws BasicException {
            return new Object[]{
                dr.getString(1), // ID - STRING
                dr.getString(2), //TAXID - STRING
                dr.getString(3), //SEARCHKEY - STRING
                dr.getString(4), //NAME - STRING
                dr.getString(5), //NOTES - STRING
                dr.getBoolean(6), // VISIBLE - BOOLEAN
                dr.getString(7), //CARD - STRING
                dr.getDouble(8), //MAXDEBT - DOUBLE
                dr.getTimestamp(9), //CURDATE - TIMESTAMP
                dr.getDouble(10), //CURDEBT - DOUBLE
                dr.getString(11), //FIRSTNAME - STRING
                dr.getString(12), //LASTNAME - STRING
                dr.getString(13), //EMAIL - STRING
                dr.getString(14), //PHONE - STRING
                dr.getString(15), //PHONE2 - STRING
                dr.getString(16), //FAX - STRING
                dr.getString(17), //ADDR - STRING               
                dr.getString(18), //ADDR2 - STRING
                dr.getString(19), //POSTAL - STRING
                dr.getString(20), //CITY - STRING
                dr.getString(21), //REGION - STRING
                dr.getString(22), //COUNTRY - STRING
                dr.getString(23), //TAXCATEGORY - STRING                
                dr.getBytes(24), //IMAGE
                dr.getTimestamp(25), //DOB - TIMESTAMP
                dr.getDouble(26), //DISCOUNT - DOUBLE                
                dr.getString(27), //SITEGUID - STRING
                ((Object[]) m_paramsSite.createValue())[1]
            };
        }
    }
}
