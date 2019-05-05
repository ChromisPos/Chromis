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

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.model.*;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.panels.JPanelTable2;
import uk.chromis.pos.ticket.ProductInfoExt;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SentenceExec;
import uk.chromis.data.loader.SentenceExecTransaction;
import uk.chromis.data.loader.SerializerRead;
import uk.chromis.data.loader.SerializerWriteBasicExt;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.pos.panels.RecipeFilterWithSites;
import uk.chromis.pos.sync.DataLogicSync;

public class RecipePanel extends JPanelTable2 {

    private DataLogicSync dlSync;
    private RecipeEditor editor;
    private RecipeFilterWithSites filterws;

    @Override
    protected void init() {
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        filterws = new RecipeFilterWithSites();
        filterws.jSites.setVisible(dlSync.isCentral());
        filterws.setPreferredSize(new Dimension(713, 100 + (dlSync.isCentral() ? 70 : 0)));
        filterws.init(app);
        filterws.addActionListener(new ReloadActionListener());
        filterws.m_jSite.addActionListener(new ReloadActionListener());

        row = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field("PRODUCT", Datas.STRING, Formats.STRING),
                new Field("PRODUCT_KIT", Datas.STRING, Formats.STRING),
                new Field("QUANTITY", Datas.DOUBLE, Formats.DOUBLE),
                new Field(AppLocal.getIntString("label.prodref"), Datas.STRING, Formats.STRING, true, true, true),
                new Field(AppLocal.getIntString("label.prodbarcode"), Datas.STRING, Formats.STRING, false, true, true),
                new Field(AppLocal.getIntString("label.prodname"), Datas.STRING, Formats.STRING, true, true, true),
                new Field("SITEGUID", Datas.STRING, Formats.STRING)
        );
        Table table = new Table(
                "PRODUCTS_KIT",
                new PrimaryKey("ID"),
                new Column("PRODUCT"),
                new Column("PRODUCT_KIT"),
                new Column("QUANTITY"),
                new Column("SITEGUID"));

        lpr = new ListProviderCreator(new PreparedSentence(app.getSession(),
                "SELECT KIT.ID, KIT.PRODUCT, KIT.PRODUCT_KIT, KIT.QUANTITY, P.REFERENCE, P.CODE, P.NAME, P.SITEGUID "
                + "FROM PRODUCTS_KIT KIT, PRODUCTS P "
                + "WHERE P.SITEGUID =? AND KIT.PRODUCT_KIT = P.ID AND KIT.PRODUCT = ?",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING}, new int[]{0, 1}),
                new RecipeSerializerRead()),
                filterws);

        
          SentenceExec updatesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                if (values[5] == null) {
                    // INSERT
                    values[5] = filterws.getSelectKey();
                    return new PreparedSentence(app.getSession(), "INSERT INTO PRODUCTS_KIT (ID, PRODUCT, PRODUCT_KIT, QUANTITY, SITEGUID) VALUES (?, ?, ?, ?, ?)", new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 1, 2, 3, 5})).exec(params);
                } else {
                    // UPDATE
                    return new PreparedSentence(app.getSession(), "UPDATE PRODUCTS_KIT SET PRODUCT = ?, PRODUCT_KIT = ?, QUANTITY = ? WHERE ID = ? AND SITEGUID = ? ", new SerializerWriteBasicExt(row.getDatas(), new int[]{1, 2, 3, 0, 5})).exec(params);
                }
            }
        };

        SentenceExec insertsent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                // INSERT
                values[0] = UUID.randomUUID().toString();
                values[5] = filterws.getSelectKey();
                return new PreparedSentence(app.getSession(), "INSERT INTO PRODUCTS_KIT (ID, PRODUCT, PRODUCT_KIT, QUANTITY, SITEGUID) VALUES (?, ?, ?, ?, ?)",
                        new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 1, 2, 3, 5})).exec(params);
            }
        };

        SentenceExec deletesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                values[5] = filterws.getSelectKey();
                return new PreparedSentence(app.getSession(), "DELETE FROM PRODUCTS_KIT WHERE ID = ? AND SITEGUID = ? ", new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 4})).exec(params);
            }
        };

        spr = new SaveProvider(updatesent, insertsent, deletesent);
  
        //spr = row.getSaveProvider(app.getSession(), table);
        editor = new RecipeEditor(app, dirty, dlSync.getSiteGuid());
    }

    @Override
    public void activate() throws BasicException {
        filterws.activate();
        startNavigation();
        reload();
    }

    @Override
    public Component getFilter() {
        return filterws.getComponent();
    }

    @Override
    public EditorRecord getEditor() {
        return editor;
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Recipe");
    }

    private void reload() throws BasicException {
        ProductInfoExt prod = filterws.getProductInfoExt();
        editor.setInsertProduct(prod); // must be set before load
        bd.setEditable(prod != null);
        bd.actionLoad();
    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                editor.refreshGuid(filterws.getGuid());
                reload();
            } catch (BasicException w) {
            }
        }
    }

    private class RecipeSerializerRead implements SerializerRead {

        @Override
        public Object readValues(DataRead dr) throws BasicException {
            return new Object[]{
                dr.getString(1),
                dr.getString(2),
                dr.getString(3),
                dr.getDouble(4),
                dr.getString(5),
                dr.getString(6),
                dr.getString(7),
                dr.getString(8)
            };
        }
    }
}
