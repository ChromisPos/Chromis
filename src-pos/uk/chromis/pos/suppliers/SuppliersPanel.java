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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SentenceExec;
import uk.chromis.data.loader.SentenceExecTransaction;
import uk.chromis.data.loader.SerializerRead;
import uk.chromis.data.loader.SerializerWriteBasicExt;
import uk.chromis.data.model.Field;
import uk.chromis.data.model.Row;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.format.Formats;
import uk.chromis.pos.admin.JParamsSites;
import uk.chromis.pos.panels.JPanelTable2;
import uk.chromis.pos.sync.DataLogicSync;

public class SuppliersPanel extends JPanelTable2 {

    private JParamsSites m_paramsSite;
    private DataLogicSync dlSync;
    private SuppliersEditor jeditor;


    /**
     *
     */
    @Override
    protected void init() {
        //DataLogicSuppliers dlSuppliers = (DataLogicSuppliers) app.getBean("uk.chromis.pos.suppliers.DataLogicSuppliers");
        
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        m_paramsSite = new JParamsSites(true);
        m_paramsSite.setVisible(dlSync.isCentral());
        m_paramsSite.init(app);
        m_paramsSite.addActionListener(new SuppliersPanel.ReloadActionListener());

        row = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field("SUPPLIERNAME", Datas.STRING, Formats.STRING, true, true, true),
                new Field("ACCOUNTNUMBER", Datas.STRING, Formats.STRING),
                new Field("ADDRESS", Datas.STRING, Formats.STRING),
                new Field("ADDRESS2", Datas.STRING, Formats.STRING),
                new Field("CITY", Datas.STRING, Formats.STRING),
                new Field("POSTCODE", Datas.STRING, Formats.STRING),
                new Field("EMAIL", Datas.STRING, Formats.STRING),
                new Field("PHONE", Datas.STRING, Formats.STRING),
                new Field("CREDITLIMIT", Datas.DOUBLE, Formats.DOUBLE),
                new Field("CREDITTERMS", Datas.STRING, Formats.STRING),
                new Field("ACTIVE", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("CONTACT", Datas.STRING, Formats.STRING),
                new Field("NOTES", Datas.STRING, Formats.STRING),
                new Field("SITEGUID", Datas.STRING, Formats.STRING)
        );

        lpr = new ListProviderCreator(new PreparedSentence(app.getSession(),
                "SELECT ID, SUPPLIERNAME, ACCOUNTNUMBER, ADDRESS, ADDRESS2, CITY, POSTCODE, EMAIL, PHONE, "
                + "CREDITLIMIT, CREDITTERMS, ACTIVE, CONTACT, NOTES, SITEGUID "
                + "FROM SUPPLIERS "
                + "WHERE SITEGUID = ? "
                + "ORDER BY LOWER (SUPPLIERNAME)",
                new SerializerWriteBasicExt(new Datas[]{Datas.OBJECT, Datas.STRING}, new int[]{1}),
                new SuppliersPanel.SuppliersSerializerRead()
        ),
                m_paramsSite);

        SentenceExec updatesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                if (values[14] == null) {
                    // INSERT
                    values[14] = m_paramsSite.getSelectKey();
                    return new PreparedSentence(app.getSession(), "INSERT INTO SUPPLIERS (ID, SUPPLIERNAME, ACCOUNTNUMBER, ADDRESS, ADDRESS2, CITY, "
                        + "POSTCODE, EMAIL, PHONE, CREDITLIMIT, CREDITTERMS, ACTIVE, CONTACT, NOTES, SITEGUID)"
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14})).exec(params);
                } else {
                    // UPDATE
                    return new PreparedSentence(app.getSession(), "UPDATE SUPPLIERS SET  SUPPLIERNAME = ?, ACCOUNTNUMBER = ?, ADDRESS = ?, ADDRESS2 = ?, CITY = ?, "
                            +"POSTCODE = ?, EMAIL = ?, PHONE = ?, CREDITLIMIT = ?, CREDITTERMS = ?, ACTIVE = ?, CONTACT = ?, NOTES = ? WHERE ID = ? AND SITEGUID = ?",                            
                            new SerializerWriteBasicExt(row.getDatas(), new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 0, 14})).exec(params);
                }
            }
        };

        SentenceExec insertsent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                // INSERT
                values[0] = UUID.randomUUID().toString();
                values[14] = m_paramsSite.getSelectKey();            
                return new PreparedSentence(app.getSession(), "INSERT INTO SUPPLIERS (ID, SUPPLIERNAME, ACCOUNTNUMBER, ADDRESS, ADDRESS2, CITY, "
                        + "POSTCODE, EMAIL, PHONE, CREDITLIMIT, CREDITTERMS, ACTIVE, CONTACT, NOTES, SITEGUID)"
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14})).exec(params);
            }
        };

        SentenceExec deletesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                values[14] = m_paramsSite.getSelectKey();
                return new PreparedSentence(app.getSession(), "DELETE FROM SUPPLIERS WHERE ID = ? AND SITEGUID = ? ", new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 14})).exec(params);
            }
        };

        spr = new SaveProvider(updatesent, insertsent, deletesent);

        //jeditor = new SuppliersView(app, dirty, dlSync.getSiteGuid());
        jeditor = new SuppliersEditor(dirty, dlSync.getSiteGuid());
    }

    /**
     *     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {
        m_paramsSite.activate();
        super.activate();
    }

    @Override
    public Component getFilter() {
        return m_paramsSite.getComponent();
    }

    /**
     *
     * @return
     */
    @Override
    public EditorRecord getEditor() {
        return jeditor;
    }

    /**
     *
     * @return
     */
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.SuppliersManagement");
    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                jeditor.refreshGuid(m_paramsSite.getSelectKey());
                SuppliersPanel.this.bd.actionLoad();
            } catch (BasicException w) {
            }
        }
    }

    private class SuppliersSerializerRead implements SerializerRead {

        @Override
        public Object readValues(DataRead dr) throws BasicException {
            return new Object[]{
                dr.getString(1),
                dr.getString(2),
                dr.getString(3),
                dr.getString(4),
                dr.getString(5),
                dr.getString(6),
                dr.getString(7),
                dr.getString(8),
                dr.getString(9),
                dr.getDouble(10),
                dr.getString(11),
                dr.getBoolean(12),
                dr.getString(13),
                dr.getString(14),
                ((Object[]) m_paramsSite.createValue())[1]

            };
        }
    }
}
