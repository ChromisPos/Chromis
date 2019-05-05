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

package uk.chromis.pos.inventory;

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
import uk.chromis.data.model.Column;
import uk.chromis.data.model.Field;
import uk.chromis.data.model.PrimaryKey;
import uk.chromis.data.model.Row;
import uk.chromis.data.model.Table;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.panels.JPanelTable2;
import uk.chromis.pos.sync.DataLogicSync;

public class AttributeUsePanel extends JPanelTable2 {

    private DataLogicSync dlSync;
    private AttributeUseEditor editor;
    private AttributeSetFilterWithSites filter;

    @Override
    protected void init() {
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        filter = new AttributeSetFilterWithSites();
        filter.jSites.setVisible(dlSync.isCentral());
        filter.init(app);
        filter.addActionListener(new ReloadActionListener());
        filter.m_jSite.addActionListener(new ReloadActionListener());

        row = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field("ATRIBUTESET_ID", Datas.STRING, Formats.STRING),
                new Field("ATTRIBUTE_ID", Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.order"), Datas.INT, Formats.INT, false, true, true),                
                new Field("SITEGUID", Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.name"), Datas.STRING, Formats.STRING, true, true, true)
                
        );

        Table table = new Table(
                "ATTRIBUTEUSE",
                new PrimaryKey("ID"),
                new Column("ATTRIBUTESET_ID"),
                new Column("ATTRIBUTE_ID"),
                new Column("LINENO"),
                new Column("SITEGUID")
        );
       
        lpr = row.getListProvider(app.getSession(),
                "SELECT ATTUSE.ID, ATTUSE.ATTRIBUTESET_ID, ATTUSE.ATTRIBUTE_ID, ATTUSE.LINENO, ATTUSE.SITEGUID, ATT.NAME "
                + "FROM ATTRIBUTEUSE ATTUSE, ATTRIBUTE ATT "
                + "WHERE ATTUSE.ATTRIBUTE_ID = ATT.ID AND ATTUSE.ATTRIBUTESET_ID = ? ORDER BY LINENO", filter);
         

        SentenceExec updatesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                if (values[5] == null) {
                    // INSERT
                    values[5] = filter.getSelectKey();
                    return new PreparedSentence(app.getSession(), "INSERT INTO ATTRIBUTEUSE (ID, ATTRIBUTESET_ID, ATTRIBUTE_ID, LINENO, SITEGUID) VALUES (?, ?, ?, ?, ?)", new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 1, 2, 3, 5})).exec(params);
                } else {
                    // UPDATE
                    return new PreparedSentence(app.getSession(), "UPDATE ATTRIBUTEUSE SET ATTRIBUTESET_ID = ?, ATTRIBUTE_ID = ?, LINENO = ? WHERE ID = ? AND SITEGUID = ? ", new SerializerWriteBasicExt(row.getDatas(), new int[]{1, 2, 3, 0, 5})).exec(params);
                }
            }
        };

        SentenceExec insertsent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                // INSERT
                values[0] = UUID.randomUUID().toString();
                values[5] = filter.getSelectKey();
                return new PreparedSentence(app.getSession(), "INSERT INTO ATTRIBUTEUSE (ID, ATTRIBUTESET_ID, ATTRIBUTE_ID, LINENO, SITEGUID) VALUES (?, ?, ?, ?, ?)",
                        new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 1, 2, 3, 5})).exec(params);
            }
        };

        SentenceExec deletesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                values[5] = filter.getSelectKey();
                return new PreparedSentence(app.getSession(), "DELETE FROM ATTRIBUTEUSE WHERE ID = ? AND SITEGUID = ? ", new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 4})).exec(params);
            }
        };

        spr = new SaveProvider(updatesent, insertsent, deletesent);

        editor = new AttributeUseEditor(app, dirty, dlSync.getSiteGuid());
    }

    @Override
    public void activate() throws BasicException {
        filter.activate();
        editor.activate();
        startNavigation();
        reload();
    }

    @Override
    public Component getFilter() {
        return filter.getComponent();
    }

    @Override
    public EditorRecord getEditor() {
        return editor;
    }

    private void reload() throws BasicException {
        String attsetid = (String) filter.createValue();
        editor.setInsertId(attsetid); // must be set before load
        bd.setEditable(attsetid != null);
        bd.actionLoad();
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.AttributeUse");
    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                reload();
            } catch (BasicException w) {
            }
        }
    }

    private class AttributeUseSerializerRead implements SerializerRead {

        @Override
        public Object readValues(DataRead dr) throws BasicException {
            return new Object[]{
                dr.getString(1), // ID
                dr.getString(2), // Attributeset
                dr.getString(3), // attribute
                dr.getInt(4), // lineNo                 
                dr.getString(5),
                ((Object[]) filter.createValue())[1]

            };
        }
    }

}
