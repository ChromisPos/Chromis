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
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProvider;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.panels.JPanelTable;
import uk.chromis.pos.sync.DataLogicSync;

public class StockDiaryPanel extends JPanelTable {

    private StockDiaryEditorWithSites jeditorws;
    // private StockDiaryEditor jeditor;
    private DataLogicSales m_dlSales;
    private DataLogicSync dlSync;

    public StockDiaryPanel() {
    }

    @Override
    protected void init() {
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");
        m_dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");
        jeditorws = new StockDiaryEditorWithSites(app, dirty, dlSync.getSiteGuid(), dlSync.isCentral());

    }

    @Override
    public ListProvider getListProvider() {
        return null;
    }

    @Override
    public SaveProvider getSaveProvider() {
        return new SaveProvider(null, m_dlSales.getStockDiaryInsert(), m_dlSales.getStockDiaryDelete());
    }

    @Override
    public EditorRecord getEditor() {
        return jeditorws;
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.StockDiary");
    }

    @Override
    public void activate() throws BasicException {
        jeditorws.activate();
        super.activate();
    }
}
