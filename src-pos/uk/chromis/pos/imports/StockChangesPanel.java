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

package uk.chromis.pos.imports;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ListCellRenderer;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.gui.ListCellRendererBasic;
import uk.chromis.data.loader.ComparatorCreator;
import uk.chromis.data.loader.ComparatorCreatorBasic;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerWriteBasicExt;
import uk.chromis.data.loader.Vectorer;
import uk.chromis.data.loader.VectorerBasic;
import uk.chromis.data.user.EditorListener;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProvider;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.forms.DataLogicStockChanges;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.panels.JPanelTable;
import uk.chromis.pos.reports.JParamsDatesInterval;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.sync.SitesInfo;

public class StockChangesPanel extends JPanelTable implements EditorListener {

    private StockChangesEditor jeditor;
    private JParamsDatesInterval m_params;

    private DataLogicStockChanges m_dataLogic = null;
    private DataLogicSales m_dlSales = null;
    private DataLogicSystem m_dlSystem = null;
    private SaveProvider m_spr = null;
    private DataLogicSync dlSync;
    private String siteGuid;

    private SentenceList m_sentSites;
    private ComboBoxValModel m_LocationsModel;

    /**
     * Creates a new instance of StockChangesPanel
     */
    public StockChangesPanel() {
    }

    @Override
    protected void init() {
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");
        m_dataLogic = (DataLogicStockChanges) app.getBean("uk.chromis.pos.forms.DataLogicStockChanges");
        m_dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");
        m_dlSystem = (DataLogicSystem) app.getBean("uk.chromis.pos.forms.DataLogicSystem");

        m_spr = new SaveProvider(
                m_dataLogic.getChangesUpdate(),
                null,
                m_dataLogic.getChangesDelete());

        m_params = new JParamsDatesInterval();
        m_params.jSites.setVisible(dlSync.isCentral());
        m_params.init(app);
        m_params.setStartDate(uk.chromis.beans.DateUtils.getToday());
        m_params.setEndDate(uk.chromis.beans.DateUtils.getTodayMinutes());
        m_params.jSites.addActionListener(new ReloadActionListener());

        m_sentSites = dlSync.getSitesList();
        m_LocationsModel = new ComboBoxValModel();

        // el panel del editor
        jeditor = new StockChangesEditor(m_dataLogic, m_dlSales, m_dlSystem, dlSync, dirty);

        setListWidth(400);
    }

    @Override
    public void activate() throws BasicException {
        jeditor.activate();
        m_params.activate();
        m_params.jSites.init(app);
        m_params.jSites.activate();

        super.activate();
    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            siteGuid = m_params.jSites.getSelectKey();
            jeditor.refreshGuid(siteGuid);
        }
    }

    @Override
    public EditorRecord getEditor() {
        return jeditor;
    }

    @Override
    public Component getFilter() {
        return m_params.getComponent();
    }

    @Override
    public Component getToolbarExtras() {
        return null;
    }

    @Override
    public ListProvider getListProvider() {

        // This is the filter format returned by JParamsDatesInterval
        SerializerWriteBasicExt serializerWrite = new SerializerWriteBasicExt(
                new Datas[]{Datas.OBJECT, Datas.TIMESTAMP, Datas.OBJECT, Datas.TIMESTAMP}, new int[]{1, 3});

        return new ListProviderCreator(
                m_dataLogic.getChangesListbyDate(serializerWrite),
                m_params);
    }

    @Override
    public SaveProvider getSaveProvider() {
        return m_spr;
    }

    @Override
    public Vectorer getVectorer() {
        String[] names = new String[2];
        Formats[] formats = new Formats[2];

        names[0] = "LOCATION";
        formats[0] = m_dataLogic.getFormatOf(m_dataLogic.getIndexOf(names[0]));

        names[1] = "PRODUCTNAME";
        formats[1] = m_dataLogic.getFormatOf(m_dataLogic.getIndexOf(names[1]));

        return new VectorerBasic(names, formats, new int[]{0, 1});
    }

    @Override
    public ComparatorCreator getComparatorCreator() {
        String[] names = new String[2];
        Datas[] datas = new Datas[2];

        names[0] = "LOCATION";
        datas[0] = m_dataLogic.getDatasOf(m_dataLogic.getIndexOf(names[0]));

        names[1] = "PRODUCTNAME";
        datas[1] = m_dataLogic.getDatasOf(m_dataLogic.getIndexOf(names[1]));

        return new ComparatorCreatorBasic(names, datas, new int[]{0, 1});
    }

    @Override
    public ListCellRenderer getListCellRenderer() {
        return new ListCellRendererBasic(m_dataLogic.getRenderStringChange());
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.StockChanges");
    }

    @Override
    public void updateValue(Object value) {
    }

}
