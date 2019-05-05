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
import uk.chromis.data.model.Field;
import uk.chromis.data.model.Row;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.panels.JPanelTable2;
import uk.chromis.pos.reports.JParamsLocationwithSites;
import uk.chromis.pos.sync.DataLogicSync;

public class ProductsWarehousePanel extends JPanelTable2 {

    private DataLogicSync dlSync;
    private JParamsLocationwithSites m_paramsSites;
    private ProductsWarehouseEditor jeditor;

    public ProductsWarehousePanel() {
    }

    @Override
    protected void init() {
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");
        m_paramsSites = new JParamsLocationwithSites();
       // m_paramsSites.showSites(true);
        m_paramsSites.showSites(dlSync.isCentral());
        m_paramsSites.init(app);
        m_paramsSites.addActionListener(new ReloadActionListener());
        m_paramsSites.m_jLocation.addActionListener(new ReloadActionListener());

        row = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field("PRODUCT_ID", Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.prodref"), Datas.STRING, Formats.STRING, true, true, true),
                new Field(AppLocal.getIntString("label.prodname"), Datas.STRING, Formats.STRING, true, true, true),
                new Field("LOCATION", Datas.STRING, Formats.STRING),
                // new Field("STOCKSECURITY", Datas.DOUBLE, Formats.DOUBLE),
                // new Field("STOCKMAXIMUM", Datas.DOUBLE, Formats.DOUBLE),
                // new Field("UNITS", Datas.DOUBLE, Formats.DOUBLE),
                new Field(AppLocal.getIntString("label.minimum"), Datas.DOUBLE, Formats.DOUBLE, false, false, true),
                new Field(AppLocal.getIntString("label.maximum"), Datas.DOUBLE, Formats.DOUBLE, false, false, true),
                new Field(AppLocal.getIntString("label.stockunits"), Datas.DOUBLE, Formats.DOUBLE, false, false, true),
                new Field(AppLocal.getIntString("label.prodbarcode"), Datas.STRING, Formats.STRING, false, true, true),
                new Field("SITEGUID", Datas.STRING, Formats.STRING)
        );

        lpr = new ListProviderCreator(new PreparedSentence(app.getSession(),
                "SELECT L.ID, P.ID, P.REFERENCE, P.NAME,"
                + "L.STOCKSECURITY, L.STOCKMAXIMUM, COALESCE(S.SUMUNITS, 0), P.CODE, P.SITEGUID "
                + "FROM PRODUCTS P "
                + "LEFT OUTER JOIN (SELECT ID, PRODUCT, LOCATION, STOCKSECURITY, STOCKMAXIMUM FROM STOCKLEVEL WHERE LOCATION = ?) L ON P.ID = L.PRODUCT "
                + "LEFT OUTER JOIN (SELECT PRODUCT, SUM(UNITS) AS SUMUNITS FROM STOCKCURRENT WHERE LOCATION = ? GROUP BY PRODUCT) S ON P.ID = S.PRODUCT "
                + "WHERE P.SITEGUID = ? "
                + "ORDER BY P.NAME",
                new SerializerWriteBasicExt(new Datas[]{Datas.STRING, Datas.STRING}, new int[]{0, 0, 1}),
                new WarehouseSerializerRead()
        ),
                m_paramsSites);

        SentenceExec updatesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                if (values[0] == null) {
                    // INSERT
                    values[0] = UUID.randomUUID().toString();
                    return new PreparedSentence(app.getSession(), "INSERT INTO STOCKLEVEL (ID, LOCATION, PRODUCT, STOCKSECURITY, STOCKMAXIMUM) VALUES (?, ?, ?, ?, ?)", new SerializerWriteBasicExt(row.getDatas(), new int[]{0, 4, 1, 5, 6})).exec(params);
                } else {
                    // UPDATE
                    return new PreparedSentence(app.getSession(), "UPDATE STOCKLEVEL SET STOCKSECURITY = ?, STOCKMAXIMUM = ? WHERE ID = ?", new SerializerWriteBasicExt(row.getDatas(), new int[]{5, 6, 0})).exec(params);
                }
            }
        };

        spr = new SaveProvider(updatesent, null, null);

        jeditor = new ProductsWarehouseEditor(dirty, dlSync.getSiteGuid());
    }

    @Override
    public Component getFilter() {
        return m_paramsSites.getComponent();
    }

    @Override
    public EditorRecord getEditor() {
        return jeditor;
    }

    @Override
    public void activate() throws BasicException {
        m_paramsSites.activate();
        super.activate();
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.ProductsWarehouse");
    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                ProductsWarehousePanel.this.bd.actionLoad();
            } catch (BasicException w) {
            }
        }
    }

    private class WarehouseSerializerRead implements SerializerRead {

        @Override
        public Object readValues(DataRead dr) throws BasicException {
            return new Object[]{
                dr.getString(1),
                dr.getString(2),
                dr.getString(3),
                dr.getString(4),
                ((Object[]) m_paramsSites.createValue())[0],
                dr.getDouble(5),
                dr.getDouble(6),
                dr.getDouble(7),
                dr.getString(8),
                dr.getString(9)
            };
        }
    }
}
