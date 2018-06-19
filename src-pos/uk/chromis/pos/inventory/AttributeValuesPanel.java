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
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.model.Column;
import uk.chromis.data.model.Field;
import uk.chromis.data.model.PrimaryKey;
import uk.chromis.data.model.Row;
import uk.chromis.data.model.Table;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.panels.JPanelTable2;
import uk.chromis.pos.sync.DataLogicSync;

public class AttributeValuesPanel extends JPanelTable2 {

    private AttributeValuesEditor editor;
    private AttributeFilterWithSites filter;
    private DataLogicSync dlSync;

    @Override
    protected void init() {
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        filter = new AttributeFilterWithSites();
        filter.jSites.setVisible(dlSync.isCentral());
        filter.init(app);
        filter.addActionListener(new ReloadActionListener());
        filter.m_jLocation.addActionListener(new ReloadActionListener());

        row = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field("ATTRIBUTE_ID", Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.value"), Datas.STRING, Formats.STRING, true, true, true),
                new Field("SITEGUID", Datas.STRING, Formats.STRING)
        );

        Table table = new Table(
                "ATTRIBUTEVALUE",
                new PrimaryKey("ID"),
                new Column("ATTRIBUTE_ID"),
                new Column("VALUE"),
                new Column("SITEGUID"));

        lpr = row.getListProvider(app.getSession(),
                "SELECT ID, ATTRIBUTE_ID, VALUE, SITEGUID FROM ATTRIBUTEVALUE WHERE ATTRIBUTE_ID = ? ", filter);

        spr = row.getSaveProvider(app.getSession(), table);

        editor = new AttributeValuesEditor(dirty, dlSync.getSiteGuid());
    }

    @Override
    public void activate() throws BasicException {
        filter.activate();
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
        String attid = (String) filter.createValue();
        editor.setInsertId(attid); // must be set before load
        bd.setEditable(attid != null);
        bd.actionLoad();
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.AttributeValues");
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
}
