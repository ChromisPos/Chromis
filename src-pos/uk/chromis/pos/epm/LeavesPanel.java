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
package uk.chromis.pos.epm;

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
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.panels.JPanelTable2;
import uk.chromis.pos.sync.DataLogicSync;

public class LeavesPanel extends JPanelTable2 {

    private LeavesView jeditor;
    private JParamsSites m_paramsSite;
    private DataLogicSync dlSync;

    /**
     * Creates a new instance of LeavesPanel
     */
    public LeavesPanel() {
    }

    @Override
    protected void init() {
        DataLogicPresenceManagement dlPresenceManagement = (DataLogicPresenceManagement) app.getBean("uk.chromis.pos.epm.DataLogicPresenceManagement");
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        m_paramsSite = new JParamsSites();
        m_paramsSite.setVisible(dlSync.isCentral());
        m_paramsSite.init(app);
        m_paramsSite.addActionListener(new LeavesPanel.ReloadActionListener());

        row = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field("PPLID", Datas.STRING, Formats.STRING),
                new Field("NAME", Datas.STRING, Formats.STRING, true, true, true),
                new Field("STARTDATE", Datas.TIMESTAMP, Formats.TIMESTAMP),
                new Field("ENDDATE", Datas.TIMESTAMP, Formats.TIMESTAMP),
                new Field("NOTES", Datas.STRING, Formats.STRING),
                new Field("SITEGUID", Datas.STRING, Formats.STRING)
        );

        lpr = new ListProviderCreator(new PreparedSentence(app.getSession(),
                "SELECT ID, PPLID, NAME, STARTDATE, ENDDATE, NOTES, SITEGUID "
                + "FROM LEAVES "
                + "WHERE SITEGUID = ? "
                + "ORDER BY LOWER (NAME)",
                new SerializerWriteBasicExt(new Datas[]{Datas.OBJECT, Datas.STRING}, new int[]{1}),
                new LeavesPanel.LeavesSerializerRead()
        ),
                m_paramsSite);

        SentenceExec updatesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                if (values[6] == null) {
                    // INSERT
                    if (values[3] != null && values[4] != null) {
                        values[6] = m_paramsSite.getSelectKey();
                        return new PreparedSentence(app.getSession(), "INSERT INTO LEAVES (ID, PPLID, NAME, STARTDATE, ENDDATE, NOTES, SITEGUID) VALUES (?, ?, ?, ?, ?, ?, ?)", new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 1, 2, 3, 4, 5, 6})).exec(params);
                    } else {
                        return 0;
                    }
                } else if (values[3] != null && values[4] != null) {
                    //UPDATE
                    return new PreparedSentence(app.getSession(), "UPDATE LEAVES SET PPLID = ?, NAME = ?, STARTDATE = ?, ENDDATE = ?, NOTES = ? WHERE ID = ? AND SITEGUID = ? ", new SerializerWriteBasicExt(row.getDatas(), new int[]{1, 2, 3, 4, 5, 0, 6})).exec(params);
                } else {
                    return 0;
                }
            }
        };

        SentenceExec insertsent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                // INSERT
                if (values[3] != null && values[4] != null) {
                    values[0] = UUID.randomUUID().toString();
                    values[6] = m_paramsSite.getSelectKey();
                    return new PreparedSentence(app.getSession(), "INSERT INTO LEAVES (ID, PPLID, NAME, STARTDATE, ENDDATE, NOTES, SITEGUID) VALUES (?, ?, ?, ?, ?, ?, ?)", new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 1, 2, 3, 4, 5, 6})).exec(params);
                } else {
                    return 0;
                }
            }
        };

        SentenceExec deletesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                values[6] = m_paramsSite.getSelectKey();
                return new PreparedSentence(app.getSession(), "DELETE FROM LEAVES WHERE ID = ? AND SITEGUID = ? ", new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 6})).exec(params);
            }
        };

        spr = new SaveProvider(updatesent, insertsent, deletesent);
        jeditor = new LeavesView(app, dirty, dlSync.getSiteGuid());

    }

    @Override

    public void activate() throws BasicException {
        m_paramsSite.activate();
        jeditor.activate();
        super.activate();
    }

    @Override
    public Component getFilter() {
        return m_paramsSite.getComponent();
    }

    @Override
    public EditorRecord getEditor() {
        return jeditor;
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Leaves");
    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                jeditor.refreshGuid(m_paramsSite.getSelectKey());
                LeavesPanel.this.bd.actionLoad();
            } catch (BasicException w) {
            }
        }
    }

    private class LeavesSerializerRead implements SerializerRead {

        @Override
        public Object readValues(DataRead dr) throws BasicException {
            return new Object[]{
                dr.getString(1),
                dr.getString(2),
                dr.getString(3),
                dr.getTimestamp(4),
                dr.getTimestamp(5),
                dr.getString(6),
                dr.getString(7),
                ((Object[]) m_paramsSite.createValue())[1]
            };
        }
    }
}
