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
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SerializerRead;
import uk.chromis.data.loader.SerializerWriteBasicExt;
import uk.chromis.data.model.Column;
import uk.chromis.data.model.Field;
import uk.chromis.data.model.PrimaryKey;
import uk.chromis.data.model.Row;
import uk.chromis.data.model.Table;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.format.Formats;
import uk.chromis.pos.admin.JParamsSites;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.panels.JPanelTable2;
import uk.chromis.pos.sync.DataLogicSync;

public class AttributeSetsPanel extends JPanelTable2 {

    private DataLogicSync dlSync;
    private JParamsSites m_paramsSite;
    private EditorRecord editor;


    public AttributeSetsPanel() {
    }

    @Override
    protected void init() {
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        m_paramsSite = new JParamsSites(true);
        m_paramsSite.setVisible(dlSync.isCentral());
        m_paramsSite.init(app);
        m_paramsSite.addActionListener(new AttributeSetsPanel.ReloadActionListener());

        row = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("Label.Name"), Datas.STRING, Formats.STRING, true, true, true),
                new Field("SITEGUID", Datas.STRING, Formats.STRING)
        );

        Table table = new Table(
                "ATTRIBUTESET",
                new PrimaryKey("ID"),
                new Column("NAME"),
                new Column("SITEGUID"));

        lpr = new ListProviderCreator(new PreparedSentence(app.getSession(),
                "SELECT ID, NAME, SITEGUID "
                + "FROM ATTRIBUTESET "
                + "WHERE SITEGUID = ? "
                + "ORDER BY LOWER (NAME)",
                new SerializerWriteBasicExt(new Datas[]{Datas.OBJECT, Datas.STRING}, new int[]{1}),
                new AttributeSetsPanel.AttributeSetsPanelSerializerRead()
        ),
                m_paramsSite);

        spr = row.getSaveProvider(app.getSession(), table);

        editor = new AttributeSetsEditor(dirty, dlSync.getSiteGuid());
    }

    @Override
    public Component getFilter() {
            return m_paramsSite.getComponent();
    }

    @Override
    public void activate() throws BasicException {
        m_paramsSite.activate();
        super.activate();
    }

    @Override
    public EditorRecord getEditor() {
        return editor;
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.AttributeSets");
    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                editor.refreshGuid(m_paramsSite.getSelectKey());
                AttributeSetsPanel.this.bd.actionLoad();
            } catch (BasicException w) {
            }
        }
    }

    private class AttributeSetsPanelSerializerRead implements SerializerRead {

        @Override
        public Object readValues(DataRead dr) throws BasicException {
            return new Object[]{
                dr.getString(1),
                dr.getString(2),
                dr.getString(3),
                ((Object[]) m_paramsSite.createValue())[1]

            };
        }
    }
}
