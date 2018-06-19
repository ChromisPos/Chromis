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
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.QBFCompareEnum;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerWrite;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.pos.customers.CustomerInfo;
import uk.chromis.pos.customers.DataLogicCustomers;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.sync.SitesInfo;
import uk.chromis.pos.util.SessionFactory;

public class JParamsSupplier extends javax.swing.JPanel implements ReportEditorCreator {

    private DataLogicCustomers dlCustomers;
    private CustomerInfo currentcustomer;

    private final JPanel panel;
    private final JLabel jLabel1;
    private final JLabel jLabel2;

    private String siteGuid;
    private final JComboBox m_jLocation = new JComboBox();
    private final JComboBox m_supplierStatus = new JComboBox();
    
    
    private SentenceList m_sentSites;
    private ComboBoxValModel m_LocationsModel;
    private ComboBoxValModel m_SiteModel;
    private ComboBoxValModel m_supplierStatusModel;
    
    
    
    private JTextField jCustomer;   
    private DataLogicSync dlSync;
    private Boolean bSite;

    public JParamsSupplier() {
        this(false);
    }

    public JParamsSupplier(Boolean bSite) {

        this.bSite = bSite;

        dlSync = new DataLogicSync();
        dlSync.init(SessionFactory.getInstance().getSession());

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

        jLabel2 = new JLabel(AppLocal.getIntString("label.supplierstatus"));
        jLabel2.setFont(font);

        m_jLocation.setFont(font);
        m_jLocation.setPreferredSize(new Dimension(200, 25));

        m_jLocation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {               
                if (m_LocationsModel.getSelectedKey() != null) {
                    siteGuid = m_LocationsModel.getSelectedKey().toString();
                } else {
                    siteGuid = null;

                }
            }
        });
        if (!bSite) {
            if (dlSync.isCentral()) {
                panel.add(jLabel1, "gapright 5");
                panel.add(m_jLocation, "w 220, gapright 20");
            }
        }

        panel.add(jLabel2, "w 75,gapright 5");
        panel.add(m_supplierStatus, "w 220, gapright 5");

    }

    @Override
    public void init(AppView app) {
        dlCustomers = (DataLogicCustomers) app.getBean("uk.chromis.pos.customers.DataLogicCustomers");
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");
        m_sentSites = dlSync.getSitesList();
        m_LocationsModel = new ComboBoxValModel();
    }

    @Override
    public void activate() throws BasicException {
        currentcustomer = null;
      
        List a;
        try {
            a = m_sentSites.list();
        } catch (BasicException ex) {
            a = dlSync.getSingleSite().list();
        }

        if (dlSync.getCentralGuid().equals(dlSync.getSiteGuid())) {
            SitesInfo tempSite = new SitesInfo(null, "All Sites");
            a.add(0, tempSite);
        }

        addFirst(a);
        m_LocationsModel = new ComboBoxValModel(a);
        m_LocationsModel.setSelectedFirst();
        m_jLocation.setModel(m_LocationsModel);
        
        
        m_supplierStatusModel = new ComboBoxValModel();
        m_supplierStatusModel.add("All Suppliers");
        m_supplierStatusModel.add("Active Suppliers");
        m_supplierStatusModel.add("Inactive Suppliers");       
        m_supplierStatus.setModel(m_supplierStatusModel);
        
        m_supplierStatusModel.setSelectedFirst();
        
        
    }

    protected void addFirst(List a) {
        // do nothing
    }

    @Override
    public SerializerWrite getSerializerWrite() {
        return new SerializerWriteBasic(new Datas[]{Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.STRING});
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public Object createValue() throws BasicException {
        
            if (m_supplierStatusModel.getSelectedItem().equals("All Suppliers")) {
                return new Object[]{QBFCompareEnum.COMP_NONE, null, QBFCompareEnum.COMP_NONE, null,
                    m_LocationsModel.getSelectedKey() == null ? QBFCompareEnum.COMP_ISNOTNULL : QBFCompareEnum.COMP_EQUALS,
                    m_LocationsModel.getSelectedKey()
                };
            }
            
            if (m_supplierStatusModel.getSelectedItem().equals("Active Suppliers")) {;
                return new Object[]{QBFCompareEnum.COMP_ISTRUE, null,
                    m_LocationsModel.getSelectedKey() == null ? QBFCompareEnum.COMP_ISNOTNULL : QBFCompareEnum.COMP_EQUALS,
                    m_LocationsModel.getSelectedKey()
                };
            }

            return new Object[]{QBFCompareEnum.COMP_ISFALSE, null, 
                m_LocationsModel.getSelectedKey() == null ? QBFCompareEnum.COMP_ISNOTNULL : QBFCompareEnum.COMP_EQUALS,
                m_LocationsModel.getSelectedKey()
            };
        
    

    }
    
}
