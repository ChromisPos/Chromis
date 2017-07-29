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
import uk.chromis.data.loader.*;
import uk.chromis.data.model.Field;
import uk.chromis.data.model.Row;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.panels.JPanelTable2;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.UUID;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.reports.JParamsLocationwithSites;
import uk.chromis.pos.sync.DataLogicSync;

public class ProductPacksPanel extends JPanelTable2 implements ProductPacksEditor.SplitNotify {

    private JParamsLocationwithSites m_paramsSite;
    private DataLogicSales m_dlSales;
    private DataLogicSync dlSync;
    private ProductPacksEditor jeditor;
    private String siteGuid;

    //JParamsLocationwithSites
    public ProductPacksPanel() {
    }

    @Override
    protected void init() {
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        m_paramsSite = new JParamsLocationwithSites();
       // m_paramsSite.showSites(true);
        m_paramsSite.showSites(dlSync.isCentral());
        siteGuid = dlSync.getSiteGuid();
        m_paramsSite.init(app);
        m_paramsSite.addActionListener(new ProductPacksPanel.ReloadActionListener());
        m_paramsSite.m_jLocation.addActionListener(new ProductPacksPanel.ReloadActionListener());

        row = new Row(
                new Field("PRODUCT_ID", Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.prodref"), Datas.STRING, Formats.STRING, true, true, true),
                new Field(AppLocal.getIntString("label.prodname"), Datas.STRING, Formats.STRING, true, true, true),
                new Field("LOCATION", Datas.STRING, Formats.STRING),
                new Field("PACKPRODUCTREFERENCE", Datas.STRING, Formats.STRING),
                new Field("PACKPRODUCTNAME", Datas.STRING, Formats.STRING),
                new Field("PACKQUANTITY", Datas.DOUBLE, Formats.DOUBLE),
                new Field("UNITS", Datas.DOUBLE, Formats.DOUBLE),
                new Field("PACKPRODUCTID", Datas.STRING, Formats.STRING),
                new Field("PACKPRICE", Datas.DOUBLE, Formats.CURRENCY),
                new Field("ATTRIBUTESETINSTANCE_ID", Datas.STRING, Formats.STRING),
                new Field("SITEGUID", Datas.STRING, Formats.STRING)
        );

        lpr = new ListProviderCreator(new PreparedSentence(app.getSession(),
                "SELECT P.ID, P.REFERENCE, P.NAME,  "
                + "PK.REFERENCE, PK.NAME, P.PACKQUANTITY, COALESCE(S.SUMUNITS, 0), PK.ID, P.PRICEBUY, S.ATTRIBUTESETINSTANCE_ID, SITEGUID  "
                + "FROM PRODUCTS P "
                + "LEFT OUTER JOIN (SELECT ID, NAME, REFERENCE FROM PRODUCTS ) PK ON P.PACKPRODUCT = PK.ID "
                + "LEFT OUTER JOIN (SELECT PRODUCT, ATTRIBUTESETINSTANCE_ID, SUM(UNITS) AS SUMUNITS FROM STOCKCURRENT WHERE LOCATION = ? GROUP BY PRODUCT) S ON P.ID = S.PRODUCT "
                + "WHERE P.SITEGUID = ? AND P.ISPACK = TRUE ORDER BY P.NAME",
                new SerializerWriteBasicExt(new Datas[]{Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING}, new int[]{0, 1}),
                new PacksSerializerRead()
        ),
                m_paramsSite);

        SentenceExec updatesent = new SentenceExecTransaction(app.getSession()) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                return 0;
            }
        };

        spr = new SaveProvider(updatesent, null, null);

        jeditor = new ProductPacksEditor(dirty,dlSync.getSiteGuid());
        jeditor.setSplitNotify(this);
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
    public void activate() throws BasicException {
        m_paramsSite.activate();
        super.activate();
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.ProductPacks");
    }

    public void SplitNotify(
            String Location, String prodID, Double packQuantity, Double unitsInPack, Double unitsToSplit, String packProdID,
            Double prodprice, String atrSetId, String siteGuid
    ) throws BasicException {

        int updateresult = 0;

        Datas[] datas = new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.DOUBLE,
            Datas.DOUBLE,
            Datas.STRING,
            Datas.DOUBLE,
            Datas.DOUBLE,
            Datas.INT,
            Datas.INT,
            Datas.STRING,
            Datas.TIMESTAMP,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING
        };

        Object[] params = new Object[]{
            Location,
            prodID,
            atrSetId,
            unitsToSplit * -1, // Number of products subtracted
            unitsToSplit * unitsInPack, // Number of unpacked products added
            packProdID,
            prodprice,
            prodprice / packQuantity,
            MovementReason.IN_OPEN_PACK.getKey(),
            MovementReason.OUT_OPEN_PACK.getKey(),
            app.getAppUserView().getUser().getName(),
            new Date(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            siteGuid
        };

        // Reduce pack quantity 
        if (atrSetId == null) {
            updateresult = new PreparedSentence(app.getSession(),
                    "UPDATE STOCKCURRENT SET UNITS = (UNITS + ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID IS NULL AND SITEGUID = ? ",
                    new SerializerWriteBasicExt(datas, new int[]{3, 0, 1, 14})).exec(params);
        } else {
            updateresult = new PreparedSentence(app.getSession(),
                    "UPDATE STOCKCURRENT SET UNITS = (UNITS + ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID = ? AND SITEGUID = ? ",
                    new SerializerWriteBasicExt(datas, new int[]{3, 0, 1, 2, 14})).exec(params);
        }

        if (updateresult == 0) {
            // No stock record for pack - should not happen
            throw new BasicException(AppLocal.getIntString("exception.noupdate") + " No STOCKCURRENT for pack");
        }

        // increase product quantity 
        if (atrSetId == null) {            
            updateresult = new PreparedSentence(app.getSession(),
                    "UPDATE STOCKCURRENT SET UNITS = (UNITS + ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID IS NULL AND SITEGUID = ? ",
                    new SerializerWriteBasicExt(datas, new int[]{4, 0, 5, 14})).exec(params);
        } else {
            updateresult = new PreparedSentence(app.getSession(),
                    "UPDATE STOCKCURRENT SET UNITS = (UNITS + ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID = ? AND SITEGUID = ? ",
                    new SerializerWriteBasicExt(datas, new int[]{4, 0, 5, 2, 14})).exec(params);
        }

        if (updateresult == 0) {
            // No stock record for product - could happen so create one 
            new PreparedSentence(app.getSession(),
                    "INSERT INTO STOCKCURRENT (LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS, SITEGUID) VALUES (?, ?, ?, ?, ?)",
                    new SerializerWriteBasicExt(datas, new int[]{0, 5, 2, 4, 14})).exec(params);
        }

        // Two diary entries needed one for pack and one for products unpacked
        updateresult = new PreparedSentence(app.getSession(),
                "INSERT INTO STOCKDIARY (ID, DATENEW, REASON, LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS, PRICE, AppUser, SITEGUID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new SerializerWriteBasicExt(datas, new int[]{12, 11, 9, 0, 1, 2, 3, 6, 10, 14})).exec(params);

        // Two diary entries needed one for pack and one for products unpacked
        updateresult = new PreparedSentence(app.getSession(),
                "INSERT INTO STOCKDIARY (ID, DATENEW, REASON, LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS, PRICE, AppUser, SITEGUID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ? )",
                new SerializerWriteBasicExt(datas, new int[]{13, 11, 8, 0, 5, 2, 4, 7, 10, 14})).exec(params);

    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                System.out.println("Here");
                //jeditor.refreshGuid(m_paramsSite.getSelectKey().toString());
                System.out.println( m_paramsSite.getGuid());
               
                ProductPacksPanel.this.bd.actionLoad();
            } catch (BasicException w) {
            }
        }
    }

    private class PacksSerializerRead implements SerializerRead {

        @Override
        public Object readValues(DataRead dr) throws BasicException {
            return new Object[]{
                dr.getString(1), // ID - String
                dr.getString(2), // Prodref - String
                dr.getString(3), // Prodname - STring     
                ((Object[]) m_paramsSite.createValue())[0],
                dr.getString(4), // PackPordReference - String
                dr.getString(5), // PackPordname - String
                dr.getDouble(6), // PackQuantity - Double
                dr.getDouble(7), // Units - Double
                dr.getString(8), // PackProdID - String
                dr.getDouble(9), // packPrice - Double
                dr.getDouble(10), // Atributesetinstance - String
                dr.getString(11)//, // Siteguid - String


            };
        }
    }
}
