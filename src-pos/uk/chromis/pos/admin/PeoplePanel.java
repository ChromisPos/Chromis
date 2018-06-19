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

package uk.chromis.pos.admin;

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
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.panels.JPanelTable2;
import uk.chromis.pos.sync.DataLogicSync;

public class PeoplePanel extends JPanelTable2 {

    private PeopleView jeditor;
    private DataLogicAdmin dlAdmin;
    private JParamsPeople m_paramsPeople;
    private DataLogicSync dlSync;
    private String localGuid = null;

    public PeoplePanel() {
    }

    @Override
    protected void init() {
        dlAdmin = (DataLogicAdmin) app.getBean("uk.chromis.pos.admin.DataLogicAdmin");
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        m_paramsPeople = new JParamsPeople();
        m_paramsPeople.setVisible(dlSync.isCentral());
        m_paramsPeople.init(app);
        m_paramsPeople.addActionListener(new PeoplePanel.ReloadActionListener());

        localGuid = dlSync.getSiteGuid();

        row = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field("NAME", Datas.STRING, Formats.STRING, true, true, true),
                new Field("APPPASSWORD", Datas.STRING, Formats.STRING),
                new Field("ROLE", Datas.STRING, Formats.STRING),
                new Field("VISIBLE", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("CARD", Datas.STRING, Formats.STRING),
                new Field("IMAGE", Datas.IMAGE, Formats.BYTEA),
                new Field("SITEGUID", Datas.STRING, Formats.STRING)
        );

        lpr = new ListProviderCreator(new PreparedSentence(app.getSession(),
                "SELECT P.ID, P.NAME, P.APPPASSWORD, P.ROLE, P.VISIBLE, P.CARD, P.IMAGE, P.SITEGUID "
                + "FROM PEOPLE P JOIN ROLES R "
                + "ON P.ROLE=R.ID "
                + "WHERE P.SITEGUID = ? AND R.RIGHTSLEVEL <= ? ORDER BY LOWER (P.NAME)",
                new SerializerWriteBasicExt(new Datas[]{Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.INT}, new int[]{1, 2}),
                new PeoplePanel.PeopleSerializerRead()),
                m_paramsPeople);

        SentenceExec updatesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                if (values[7] == null) {
                    // INSERT
                    values[7] = m_paramsPeople.getSelectKey();
                    return new PreparedSentence(app.getSession(), "INSERT INTO PEOPLE (ID, NAME, APPPASSWORD, ROLE, VISIBLE, CARD, IMAGE, SITEGUID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 1, 2, 3, 4, 5, 6, 7})).exec(params);
                } else {
                    // UPDATE
                    return new PreparedSentence(app.getSession(), "UPDATE PEOPLE SET NAME = ?, APPPASSWORD = ?, ROLE = ?, VISIBLE = ?, CARD = ?, IMAGE = ?  WHERE ID = ? AND SITEGUID = ? ", new SerializerWriteBasicExt(row.getDatas(), new int[]{1, 2, 3, 4, 5, 6, 0, 7})).exec(params);
                }
            }
        };

        SentenceExec insertsent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                // INSERT
                values[0] = UUID.randomUUID().toString();
                values[7] = m_paramsPeople.getSelectKey();
                return new PreparedSentence(app.getSession(), "INSERT INTO PEOPLE (ID, NAME, APPPASSWORD, ROLE, VISIBLE, CARD, IMAGE, SITEGUID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 1, 2, 3, 4, 5, 6, 7})).exec(params);
            }
        };

        SentenceExec deletesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                values[7] = m_paramsPeople.getSelectKey();
                return new PreparedSentence(app.getSession(), "DELETE FROM PEOPLE WHERE ID = ? AND SITEGUID = ? ", new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 7})).exec(params);
            }
        };

        spr = new SaveProvider(updatesent, insertsent, deletesent);
        jeditor = new PeopleView(dlAdmin, dirty, app, localGuid);
    }

    @Override
    public Component getFilter() {
            return m_paramsPeople.getComponent();
    }

    @Override
    public EditorRecord getEditor() {
        return jeditor;
    }

    @Override
    public void activate() throws BasicException {
        m_paramsPeople.activate();
        jeditor.activate();
        super.activate();
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Users");
    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                jeditor.reloadRoles(dlSync.getSiteGuid(), m_paramsPeople.getSelectKey());
                PeoplePanel.this.bd.actionLoad();
            } catch (BasicException w) {
            }
        }
    }

    private class PeopleSerializerRead implements SerializerRead {

        @Override
        public Object readValues(DataRead dr) throws BasicException {
            return new Object[]{
                dr.getString(1), // ID
                dr.getString(2), // NAME
                dr.getString(3), // PASSWORD
                dr.getString(4), // ROLE  
                dr.getBoolean(5), // VISIBLE
                dr.getString(6), // CARD                
                dr.getBytes(7), // IMAGE
                dr.getString(8), // SITEGUID
                ((Object[]) m_paramsPeople.createValue())[1]
            // dr.getString(9)
            };
        }
    }

}
