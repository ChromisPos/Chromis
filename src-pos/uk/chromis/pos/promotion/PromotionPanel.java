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

package uk.chromis.pos.promotion;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SerializerRead;
import uk.chromis.data.loader.SerializerWriteBasicExt;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.pos.admin.JParamsSites;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.panels.JPanelTable2;
import uk.chromis.pos.sync.DataLogicSync;

public class PromotionPanel extends JPanelTable2 {

    private PromotionEditor m_Editor;
    DataLogicPromotions m_dlPromotions;
    private JParamsSites m_paramsSite;
    private DataLogicSync dlSync;

    public PromotionPanel() {
    }

    protected void init() {
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        m_paramsSite = new JParamsSites(true);
        m_paramsSite.setVisible(dlSync.isCentral());
        m_paramsSite.init(app);
        m_paramsSite.addActionListener(new PromotionPanel.ReloadActionListener());

        m_dlPromotions = new DataLogicPromotions();
        m_dlPromotions.init(app);

        row = m_dlPromotions.getRow();

        lpr = new ListProviderCreator(new PreparedSentence(app.getSession(), "SELECT ID, NAME, CRITERIA, SCRIPT, ISENABLED, ALLPRODUCTS, SITEGUID FROM PROMOTIONS WHERE SITEGUID = ? ",
                new SerializerWriteBasicExt(new Datas[]{Datas.OBJECT, Datas.STRING}, new int[]{1}),
                m_dlPromotions.m_PromotionRow.getSerializerRead()
        ), m_paramsSite);

        spr = new SaveProvider(m_dlPromotions.getUpdateSentence(),
                m_dlPromotions.getInsertSentence(),
                m_dlPromotions.getDeleteSentence());

        m_Editor = new PromotionEditor(app, dirty);

    }

    public void activate() throws BasicException {
        m_paramsSite.activate();
        m_Editor.activate();
        super.activate();
    }

    public EditorRecord getEditor() {
        return m_Editor;
    }

    @Override
    public Component getFilter() {
        return m_paramsSite.getComponent();
    }

    // public ListProvider getListProvider() {
    //     return new ListProviderCreator(m_dlPromotions.getListSentence());
    // }
    public String getTitle() {
        return AppLocal.getIntString("Menu.Promotions");
    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                PromotionPanel.this.bd.actionLoad();
            } catch (BasicException w) {
            }
        }
    }

    private class ResourceSerializerRead implements SerializerRead {

        @Override
        public Object readValues(DataRead dr) throws BasicException {
            return new Object[]{
                dr.getString(1),
                dr.getString(2),
                dr.getInt(3),
                dr.getBytes(4),
                dr.getString(5)

            };
        }
    }
}
