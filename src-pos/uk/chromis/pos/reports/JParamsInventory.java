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

package uk.chromis.pos.reports;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import uk.chromis.basic.BasicException;
import uk.chromis.beans.JCalendarDialog;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.QBFCompareEnum;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerWrite;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.util.SessionFactory;
import uk.chromis.pos.inventory.MovementReason;
import uk.chromis.pos.sync.SitesInfo;

public class JParamsInventory extends javax.swing.JPanel implements ReportEditorCreator {

    private final JPanel panel;
    private final JLabel jLabel1;
    private final JLabel jLabel2;
    private final JLabel jLabel3;
    private final JLabel jLabel4;
    private final JLabel jLabel5;
    private final JTextField jTxtStartDate = new JTextField();
    private final JTextField jTxtEndDate = new JTextField();
    private final JButton btnDateStart;
    private final JButton btnDateEnd;

    private final JComboBox m_jSite = new JComboBox();
    public ComboBoxValModel m_SiteModel;
    private final JComboBox m_jLocation = new JComboBox();
    private final JComboBox m_jreason = new JComboBox();
    private SentenceList m_sentlocations;
    private SentenceList m_sentSites;
    public ComboBoxValModel m_LocationsModel;
    private final ComboBoxValModel m_ReasonModel;

    private String siteGuid = "";
    private DataLogicSync dlSync;
    //private DataLogicSystem dlSystem;
    private DataLogicSales dlSales;

    private Boolean allSites = true;

    public JParamsInventory() {

        dlSync = new DataLogicSync();
        dlSync.init(SessionFactory.getInstance().getSession());
        siteGuid = dlSync.getSiteGuid();

        Font font = new Font("Arial", 0, 12);
        setFont(font);
        panel = new JPanel(new MigLayout());

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(319, Short.MAX_VALUE)
                ));

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jLabel1 = new JLabel(AppLocal.getIntString("label.sitename"));
        jLabel1.setFont(font);

        jLabel2 = new JLabel(AppLocal.getIntString("Label.StartDate"));
        jLabel2.setFont(font);

        jLabel3 = new JLabel(AppLocal.getIntString("Label.EndDate"));
        jLabel3.setFont(font);

        jLabel4 = new JLabel(AppLocal.getIntString("label.warehouse"));
        jLabel4.setFont(font);

        jLabel5 = new JLabel(AppLocal.getIntString("label.stockreason"));
        jLabel5.setFont(font);

        jTxtStartDate.setFont(font);
        jTxtStartDate.setPreferredSize(new Dimension(60, 25));

        jTxtEndDate.setFont(font);
        jTxtEndDate.setPreferredSize(new Dimension(60, 25));

        btnDateStart = new JButton();
        btnDateStart.setIcon(new ImageIcon(getClass().getResource("/uk/chromis/images/date.png")));
        btnDateStart.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDateStartActionPerformed();
            }
        });

        btnDateEnd = new JButton();
        btnDateEnd.setIcon(new ImageIcon(getClass().getResource("/uk/chromis/images/date.png")));
        btnDateEnd.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDateStartActionPerformed();
            }
        });

        m_ReasonModel = new ComboBoxValModel();
        m_ReasonModel.add(null);
        m_ReasonModel.add(MovementReason.IN_PURCHASE);
        m_ReasonModel.add(MovementReason.IN_REFUND);
        m_ReasonModel.add(MovementReason.IN_MOVEMENT);
        m_ReasonModel.add(MovementReason.OUT_SALE);
        m_ReasonModel.add(MovementReason.OUT_REFUND);
        m_ReasonModel.add(MovementReason.OUT_BREAK);
        m_ReasonModel.add(MovementReason.OUT_MOVEMENT);
        m_ReasonModel.add(MovementReason.IN_OPEN_PACK);
        m_ReasonModel.add(MovementReason.OUT_OPEN_PACK);
        m_ReasonModel.add(MovementReason.IN_STOCKCHANGE);
        m_ReasonModel.add(MovementReason.OUT_STOCKCHANGE);
        m_jreason.setModel(m_ReasonModel);

        if (dlSync.isCentral()) {
            panel.add(jLabel1, "w 70, al right, gapright 5");
            panel.add(m_jSite, "w 220, gapright 5");
        }

        panel.add(jLabel2, "w 70, al right");
        panel.add(jTxtStartDate, "w 160, split 2");
        panel.add(btnDateStart, "w 50, gapright 15");

        panel.add(jLabel3, "w 70, al right");
        panel.add(jTxtEndDate, "w 160, split 2");
        panel.add(btnDateEnd, "w 50, gapright 15, wrap");

        panel.add(jLabel4, "w 70, al right, gapright 5");
        panel.add(m_jLocation, "w 220, gapright 5");

        panel.add(jLabel5, "w 70, al right, gapright 5");
        panel.add(m_jreason, "w 220, gapright 5");

    }

    public void setStartDate(Date d) {
        jTxtStartDate.setText(Formats.TIMESTAMP.formatValue(d));
    }

    public void setEndDate(Date d) {
        jTxtEndDate.setText(Formats.TIMESTAMP.formatValue(d));
    }

    private void btnDateStartActionPerformed() {
        Date date;
        try {
            date = (Date) Formats.TIMESTAMP.parseValue(jTxtStartDate.getText());
        } catch (BasicException e) {
            date = null;
        }
        date = JCalendarDialog.showCalendarTimeHours(this, date);
        if (date != null) {
            jTxtStartDate.setText(Formats.TIMESTAMP.formatValue(date));
        }
    }

    private void btnDateEndActionPerformed() {
        Date date;
        try {
            date = (Date) Formats.TIMESTAMP.parseValue(jTxtEndDate.getText());
        } catch (BasicException e) {
            date = null;
        }
        date = JCalendarDialog.showCalendarTimeHours(this, date);
        if (date != null) {
            jTxtEndDate.setText(Formats.TIMESTAMP.formatValue(date));
        }
    }

    @Override
    public void init(AppView app) {
        dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");
        m_sentSites = dlSync.getSitesList();
        m_SiteModel = new ComboBoxValModel();

        m_jSite.addActionListener(new ReloadActionListener());
        m_sentlocations = dlSales.getLocationsList(dlSync.getSiteGuid());
        m_LocationsModel = new ComboBoxValModel();
    }

    @Override
    public void activate() throws BasicException {
        List a;
        try {
            a = m_sentSites.list();
        } catch (BasicException ex) {
            a = dlSync.getSingleSite().list();
        }

        if (dlSync.isCentral() && allSites) {
            SitesInfo tempSite = new SitesInfo(null, "All Sites");
            a.add(0, tempSite);
        }

        addFirst(a);
        m_SiteModel = new ComboBoxValModel(a);
        m_SiteModel.setSelectedFirst();
        m_jSite.setModel(m_SiteModel);

        List locList = m_sentlocations.list();
        locList.add(0, null);
        m_LocationsModel = new ComboBoxValModel(locList);
        m_LocationsModel.setSelectedFirst();
        m_jLocation.setModel(m_LocationsModel);
    }

    protected void addFirst(List a) {
        // do nothing
    }

    @Override
    public SerializerWrite getSerializerWrite() {
        return new SerializerWriteBasic(new Datas[]{Datas.OBJECT, Datas.TIMESTAMP, Datas.OBJECT, Datas.TIMESTAMP,
            Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.INT});
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Object createValue() throws BasicException {
        Object startdate = Formats.TIMESTAMP.parseValue(jTxtStartDate.getText());
        Object enddate = Formats.TIMESTAMP.parseValue(jTxtEndDate.getText());

        return new Object[]{
            startdate == null ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_GREATEROREQUALS,
            startdate,
            enddate == null ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_LESS,
            enddate,
            m_SiteModel.getSelectedText().equals("All Sites") ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_EQUALS, m_SiteModel.getSelectedKey(),
            m_LocationsModel.getSelectedKey() == null ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_EQUALS, m_LocationsModel.getSelectedKey(),
            m_ReasonModel.getSelectedItem() == null ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_EQUALS, m_ReasonModel.getSelectedKey()
        };
    }

    private void refresh(String siteGuid) {
        try {
            if (!this.siteGuid.equals(siteGuid)) {

                m_sentlocations = dlSales.getLocationsList(siteGuid);
                List locList = m_sentlocations.list();
                locList.add(0, null);
                m_LocationsModel = new ComboBoxValModel(locList);
                m_jLocation.setModel(m_LocationsModel);

            }
        } catch (BasicException ex) {
            Logger.getLogger(JParamsLocationWithStockLevel.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        this.siteGuid = siteGuid;

    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (m_SiteModel.getSelectedKey() == null) {
                refresh(dlSync.getSiteGuid());
            } else {
                refresh(m_SiteModel.getSelectedKey().toString());
            }
        }
    }

}
