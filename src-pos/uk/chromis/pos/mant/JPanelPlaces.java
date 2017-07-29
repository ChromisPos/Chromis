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

package uk.chromis.pos.mant;

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
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.panels.JPanelTable2;
import uk.chromis.pos.sync.DataLogicSync;

/**
 *
 * @author adrianromero
 */
public class JPanelPlaces extends JPanelTable2 {

    private PlacesEditor jeditor;
    private JParamsSites m_paramsSite;
    private DataLogicSync dlSync;
    private DataLogicSales dlSales;


    public JPanelPlaces() {
    }

    @Override
    protected void init() {
        dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        m_paramsSite = new JParamsSites(true);
        m_paramsSite.setVisible(dlSync.isCentral());
        m_paramsSite.init(app);
        m_paramsSite.addActionListener(new JPanelPlaces.ReloadActionListener());

        row = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field("NAME", Datas.STRING, Formats.STRING, true, true, true),
                new Field("X", Datas.INT, Formats.INT),
                new Field("Y", Datas.INT, Formats.INT),
                new Field("FLOOR", Datas.STRING, Formats.STRING),
                new Field("SITEGUID", Datas.STRING, Formats.STRING)
        );

        lpr = new ListProviderCreator(new PreparedSentence(app.getSession(),
                "SELECT ID, NAME, X, Y, FLOOR, SITEGUID "
                + "FROM PLACES "
                + "WHERE SITEGUID = ? "
                + "ORDER BY LOWER (NAME)",
                new SerializerWriteBasicExt(new Datas[]{Datas.OBJECT, Datas.STRING}, new int[]{1}),
                new PlacesSerializerRead()
        ),
                m_paramsSite);

                SentenceExec updatesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                if (values[5] == null) {
                    // INSERT
                    values[5] = m_paramsSite.getSelectKey();
                    return new PreparedSentence(app.getSession(), "INSERT INTO PLACES (ID, NAME, X, Y, FLOOR, SITEGUID) VALUES (?, ?, ?, ?, ?, ?)", new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 1, 2, 3, 4, 5})).exec(params);
                } else {
                    // UPDATE
                    return new PreparedSentence(app.getSession(), "UPDATE PLACES SET NAME = ?, X = ?, Y = ?, FLOOR = ? WHERE ID = ? AND SITEGUID = ? ", new SerializerWriteBasicExt(row.getDatas(), new int[]{1, 2, 3, 4, 0, 5})).exec(params);
                }
            }
        };

        SentenceExec insertsent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                // INSERT
                values[0] = UUID.randomUUID().toString();
                values[5] = m_paramsSite.getSelectKey();
                return new PreparedSentence(app.getSession(), "INSERT INTO PLACES (ID, NAME, X, Y, FLOOR, SITEGUID) VALUES (?, ?, ?, ?, ?, ?)", new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 1, 2, 3, 4, 5})).exec(params);
            }
        };

        SentenceExec deletesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                values[5] = m_paramsSite.getSelectKey();
                return new PreparedSentence(app.getSession(), "DELETE FROM PLACES WHERE ID = ? AND SITEGUID = ? ", new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 5})).exec(params);
            }
        };

        spr = new SaveProvider(updatesent, insertsent, deletesent);
        
        jeditor = new PlacesEditor(dlSales, dirty, dlSync.getSiteGuid());
    }

    @Override
    public EditorRecord getEditor() {
        return jeditor;
    }

    @Override
    public Component getFilter() {
        return m_paramsSite.getComponent();
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Tables");
    }

    @Override
    public void activate() throws BasicException {
        m_paramsSite.activate();
        jeditor.activate(); 
        super.activate();   
    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                jeditor.refreshGuid(m_paramsSite.getSelectKey());
                JPanelPlaces.this.bd.actionLoad();
            } catch (BasicException w) {
            }
        }
    }

    private class PlacesSerializerRead implements SerializerRead {

        @Override
        public Object readValues(DataRead dr) throws BasicException {
            return new Object[]{
                dr.getString(1),
                dr.getString(2),
                dr.getInt(3),
                dr.getInt(4),
                dr.getString(5),
                dr.getString(6)

            };
        }
    }
}
