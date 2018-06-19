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


package uk.chromis.pos.sales.restaurant;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;
import uk.chromis.basic.BasicException;
import uk.chromis.beans.DateUtils;
import uk.chromis.beans.JCalendarPanel;
import uk.chromis.beans.JTimePanel;
import uk.chromis.data.gui.JCounter;
import uk.chromis.data.gui.JLabelDirty;
import uk.chromis.data.gui.JListNavigator;
import uk.chromis.data.gui.JNavigator;
import uk.chromis.data.gui.JSaver;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.loader.LocalRes;
import uk.chromis.data.user.BrowsableEditableData;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.data.user.EditorCreator;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProvider;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.format.Formats;
import uk.chromis.pos.customers.CustomerInfo;
import uk.chromis.pos.customers.DataLogicCustomers;
import uk.chromis.pos.customers.JCustomerFinder;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.sync.DataLogicSync;

/**
 *
 *   
 */
public class JTicketsBagRestaurantRes extends javax.swing.JPanel implements EditorRecord {

    private JTicketsBagRestaurantMap m_restaurantmap;    
    private DataLogicCustomers dlCustomers = null;    
    private DirtyManager m_Dirty;
    private Object m_sID;
    private CustomerInfo customer;
    private Date m_dCreated;
    private JTimePanel m_timereservation;
    private boolean m_bReceived;
    private BrowsableEditableData m_bd;        
    private Date m_dcurrentday;    
    private JCalendarPanel m_datepanel;    
    private JTimePanel m_timepanel;
    private boolean m_bpaintlock = false;
 //   private DataLogicSync dlSync;
 //  private String siteGuid;

    // private Date dinitdate = new GregorianCalendar(1900, 0, 0, 12, 0).getTime();
    
    /** Creates new form JPanelReservations
     * @param oApp
     * @param restaurantmap */
    public JTicketsBagRestaurantRes(AppView oApp, JTicketsBagRestaurantMap restaurantmap) {
     //   dlSync = (DataLogicSync) oApp.getBean("uk.chromis.pos.sync.DataLogicSync");
     //   siteGuid = dlSync.getSiteGuid();
        
        
        m_restaurantmap = restaurantmap;       
        dlCustomers = (DataLogicCustomers) oApp.getBean("uk.chromis.pos.customers.DataLogicCustomers");

        m_dcurrentday = null;
        
        initComponents();
        
        m_datepanel = new JCalendarPanel();
        jPanelDate.add(m_datepanel, BorderLayout.CENTER);
        m_datepanel.addPropertyChangeListener("Date", new DateChangeCalendarListener());
        
        m_timepanel = new JTimePanel(null, JTimePanel.BUTTONS_HOUR);
        m_timepanel.setPeriod(3600000L); // Los milisegundos que tiene una hora.
        jPanelTime.add(m_timepanel, BorderLayout.CENTER);
        m_timepanel.addPropertyChangeListener("Date", new DateChangeTimeListener());
        
        m_timereservation = new JTimePanel(null, JTimePanel.BUTTONS_MINUTE);
        m_jPanelTime.add(m_timereservation, BorderLayout.CENTER);   
            
        txtCustomer.addEditorKeys(m_jKeys);
        m_jtxtChairs.addEditorKeys(m_jKeys);
        m_jtxtDescription.addEditorKeys(m_jKeys);

        m_Dirty = new DirtyManager();
        m_timereservation.addPropertyChangeListener("Date", m_Dirty);
        txtCustomer.addPropertyChangeListener("Text", m_Dirty);
        txtCustomer.addPropertyChangeListener("Text", new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                customer = new CustomerInfo(null);
                customer.setTaxid(null);
                customer.setSearchkey(null);
                customer.setName(txtCustomer.getText());            
            }
        });
        m_jtxtChairs.addPropertyChangeListener("Text", m_Dirty);
        m_jtxtDescription.addPropertyChangeListener("Text", m_Dirty);
        
        writeValueEOF();
        
        ListProvider lpr = new ListProviderCreator(dlCustomers.getReservationsList(), new MyDateFilter());            
        SaveProvider spr = new SaveProvider(dlCustomers.getReservationsUpdate(), dlCustomers.getReservationsInsert(), dlCustomers.getReservationsDelete());        
        
        m_bd = new BrowsableEditableData(lpr, spr, new CompareReservations(), this, m_Dirty);           
        
        JListNavigator nl = new JListNavigator(m_bd, true);
        nl.setCellRenderer(new JCalendarItemRenderer());  
        m_jPanelList.add(nl, BorderLayout.CENTER);
        
        // La Toolbar
        m_jToolbar.add(new JLabelDirty(m_Dirty));
        m_jToolbar.add(new JCounter(m_bd));
        m_jToolbar.add(new JNavigator(m_bd));
        m_jToolbar.add(new JSaver(m_bd));       
    }

    @Override
    public void refreshGuid(String siteGuid) {        
    }
    
    private class MyDateFilter implements EditorCreator {
        @Override
        public Object createValue() throws BasicException {           
            return new Object[] {m_dcurrentday, new Date(m_dcurrentday.getTime() + 3600000L)};   // m_dcurrentday ya no tiene ni minutos, ni segundos.             
        }
    }
    
    /**
     *
     */
    public void activate() {
        reload(DateUtils.getTodayHours(new Date()));
    }
    
    /**
     *
     */
    @Override
    public void refresh() {
    }

    /**
     *
     * @return
     */
    public boolean deactivate() {
        try {
            return m_bd.actionClosingForm(this);
        } catch (BasicException eD) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, AppLocal.getIntString("message.CannotMove"), eD);
            msg.show(this);
            return false;
        }
    }
    
    /**
     *
     */
    @Override
    public void writeValueEOF() {
        m_sID = null;
        m_dCreated = null;
        m_timereservation.setDate(null);
        assignCustomer(new CustomerInfo(null));
        m_jtxtChairs.reset();
        m_bReceived = false;
        m_jtxtDescription.reset();
        m_timereservation.setEnabled(false);
        txtCustomer.setEnabled(false);
        m_jtxtChairs.setEnabled(false);
        m_jtxtDescription.setEnabled(false);
        m_jKeys.setEnabled(false);
        
        m_jbtnReceive.setEnabled(false);
    }    

    /**
     *
     */
    public void writeValueInsert() {
        m_sID = null;
        m_dCreated = null;
        m_timereservation.setCheckDates(m_dcurrentday, new Date(m_dcurrentday.getTime() + 3600000L));
        m_timereservation.setDate(m_dcurrentday);
        assignCustomer(new CustomerInfo(null));
        m_jtxtChairs.setValueInteger(2);
        m_bReceived = false;
        m_jtxtDescription.reset();
        m_timereservation.setEnabled(true);
        txtCustomer.setEnabled(true);
        m_jtxtChairs.setEnabled(true);
        m_jtxtDescription.setEnabled(true);
        m_jKeys.setEnabled(true);
        
        m_jbtnReceive.setEnabled(true);
        
        txtCustomer.activate();
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueDelete(Object value) {
        Object[] res = (Object[]) value;
        m_sID = res[0];
        m_dCreated = (Date) res[1];
        m_timereservation.setCheckDates(m_dcurrentday, new Date(m_dcurrentday.getTime() + 3600000L));
        m_timereservation.setDate((Date) res[2]);       
        CustomerInfo c = new CustomerInfo((String) res[3]);
        c.setTaxid((String) res[4]);
        c.setSearchkey((String) res[5]);
        c.setName((String) res[6]);
        assignCustomer(c);        
        m_jtxtChairs.setValueInteger(((Integer)res[7]).intValue());
        m_bReceived = ((Boolean)res[8]).booleanValue();
        m_jtxtDescription.setText(Formats.STRING.formatValue(res[9]));
        m_timereservation.setEnabled(false);
        txtCustomer.setEnabled(false);
        m_jtxtChairs.setEnabled(false);
        m_jtxtDescription.setEnabled(false);
        m_jKeys.setEnabled(false);
        
        m_jbtnReceive.setEnabled(false); 
    }  

    /**
     *
     * @param value
     */
    @Override
    public void writeValueEdit(Object value) {
        Object[] res = (Object[]) value;
        m_sID = res[0];
        m_dCreated = (Date) res[1];
        m_timereservation.setCheckDates(m_dcurrentday, new Date(m_dcurrentday.getTime() + 3600000L));
        m_timereservation.setDate((Date) res[2]);
        CustomerInfo c = new CustomerInfo((String) res[3]);
        c.setTaxid((String) res[4]);
        c.setSearchkey((String) res[5]);
        c.setName((String) res[6]);
        assignCustomer(c);  
        m_jtxtChairs.setValueInteger(((Integer)res[7]).intValue());
        m_bReceived = ((Boolean)res[8]).booleanValue();
        m_jtxtDescription.setText(Formats.STRING.formatValue(res[9]));
        m_timereservation.setEnabled(true);
        txtCustomer.setEnabled(true);
        m_jtxtChairs.setEnabled(true);
        m_jtxtDescription.setEnabled(true);
        m_jKeys.setEnabled(true);

        m_jbtnReceive.setEnabled(!m_bReceived); // se habilita si no se ha recibido al cliente

        txtCustomer.activate();
    }    

    /**
     *
     * @return
     * @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {
        
        Object[] res = new Object[10];
        
        res[0] = m_sID == null ? UUID.randomUUID().toString() : m_sID; 
        res[1] = m_dCreated == null ? new Date() : m_dCreated; 
        res[2] = m_timereservation.getDate();
        res[3] = customer.getId();
        res[4] = customer.getTaxid();
        res[5] = customer.getSearchkey();
        res[6] = customer.getName();
        res[7] = new Integer(m_jtxtChairs.getValueInteger());
        res[8] = new Boolean(m_bReceived);
        res[9] = m_jtxtDescription.getText();

        return res;
    }

    /**
     *
     * @return
     */
    @Override
    public Component getComponent() {
        return this;
    }  
    
    private static class CompareReservations implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            Object[] a1 = (Object[]) o1;
            Object[] a2 = (Object[]) o2;
            Date d1 = (Date) a1[2];
            Date d2 = (Date) a2[2];
            int c = d1.compareTo(d2);
            if (c == 0) {
                d1 = (Date) a1[1];
                d2 = (Date) a2[1];
                return d1.compareTo(d2);
            } else {
                return c;
            }
        }
    }
    
    private void reload(Date dDate) {
        
        if (!dDate.equals(m_dcurrentday)) {
   
            Date doldcurrentday = m_dcurrentday;
            m_dcurrentday = dDate;
            try {
                m_bd.actionLoad();
            } catch (BasicException eD) {
                MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, LocalRes.getIntString("message.noreload"), eD);
                msg.show(this);
                m_dcurrentday = doldcurrentday; // nos retractamos...
            }
        }    
        
        // pinto la fecha del filtro...
        paintDate();
    }
    
    private void paintDate() {
        
        m_bpaintlock = true;
        m_datepanel.setDate(m_dcurrentday);
        m_timepanel.setDate(m_dcurrentday);
        m_bpaintlock = false;
    }
       
    private void assignCustomer(CustomerInfo c) {
        
        txtCustomer.setText(c.getName());
        customer = c;
    }
    
    private class DateChangeCalendarListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (!m_bpaintlock) {
                reload(DateUtils.getTodayHours(DateUtils.getDate(m_datepanel.getDate(), m_timepanel.getDate())));
            }
        }        
    }
        
    private class DateChangeTimeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (!m_bpaintlock) {
                reload(DateUtils.getTodayHours(DateUtils.getDate(m_datepanel.getDate(), m_timepanel.getDate())));
            }
        }        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanelDate = new javax.swing.JPanel();
        jPanelTime = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        m_jToolbarContainer = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        m_jbtnTables = new javax.swing.JButton();
        m_jbtnReceive = new javax.swing.JButton();
        m_jToolbar = new javax.swing.JPanel();
        m_jPanelList = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        m_jPanelTime = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        m_jtxtDescription = new uk.chromis.editor.JEditorString();
        m_jtxtChairs = new uk.chromis.editor.JEditorIntegerPositive();
        txtCustomer = new uk.chromis.editor.JEditorString();
        jButton1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        m_jKeys = new uk.chromis.editor.JEditorKeys();

        setLayout(new java.awt.BorderLayout());

        jPanel3.setPreferredSize(new java.awt.Dimension(10, 210));

        jPanelDate.setPreferredSize(new java.awt.Dimension(410, 190));
        jPanelDate.setLayout(new java.awt.BorderLayout());
        jPanel3.add(jPanelDate);

        jPanelTime.setPreferredSize(new java.awt.Dimension(310, 190));
        jPanelTime.setLayout(new java.awt.BorderLayout());
        jPanel3.add(jPanelTime);

        add(jPanel3, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.BorderLayout());

        m_jToolbarContainer.setLayout(new java.awt.BorderLayout());

        m_jbtnTables.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jbtnTables.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/tables.png"))); // NOI18N
        m_jbtnTables.setText(AppLocal.getIntString("button.tables")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        m_jbtnTables.setToolTipText(bundle.getString("tiptext.gototableplan")); // NOI18N
        m_jbtnTables.setFocusPainted(false);
        m_jbtnTables.setFocusable(false);
        m_jbtnTables.setRequestFocusEnabled(false);
        m_jbtnTables.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnTablesActionPerformed(evt);
            }
        });
        jPanel4.add(m_jbtnTables);

        m_jbtnReceive.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jbtnReceive.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/receive.png"))); // NOI18N
        m_jbtnReceive.setText(AppLocal.getIntString("button.receive")); // NOI18N
        m_jbtnReceive.setToolTipText(bundle.getString("tiptext.prebooked")); // NOI18N
        m_jbtnReceive.setFocusPainted(false);
        m_jbtnReceive.setFocusable(false);
        m_jbtnReceive.setRequestFocusEnabled(false);
        m_jbtnReceive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnReceiveActionPerformed(evt);
            }
        });
        jPanel4.add(m_jbtnReceive);

        m_jToolbarContainer.add(jPanel4, java.awt.BorderLayout.LINE_START);
        m_jToolbarContainer.add(m_jToolbar, java.awt.BorderLayout.CENTER);

        jPanel2.add(m_jToolbarContainer, java.awt.BorderLayout.NORTH);

        m_jPanelList.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        m_jPanelList.setLayout(new java.awt.BorderLayout());
        jPanel2.add(m_jPanelList, java.awt.BorderLayout.LINE_START);

        jPanel1.setLayout(null);

        m_jPanelTime.setLayout(new java.awt.BorderLayout());
        jPanel1.add(m_jPanelTime);
        m_jPanelTime.setBounds(90, 0, 270, 155);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("rest.label.date")); // NOI18N
        jPanel1.add(jLabel1);
        jLabel1.setBounds(10, 10, 80, 20);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("rest.label.customer")); // NOI18N
        jPanel1.add(jLabel2);
        jLabel2.setBounds(10, 160, 80, 25);

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("rest.label.chairs")); // NOI18N
        jPanel1.add(jLabel3);
        jLabel3.setBounds(10, 190, 80, 25);

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("rest.label.notes")); // NOI18N
        jPanel1.add(jLabel4);
        jLabel4.setBounds(10, 220, 80, 20);

        m_jtxtDescription.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jtxtDescription.setMaximumSize(new java.awt.Dimension(180, 25));
        jPanel1.add(m_jtxtDescription);
        m_jtxtDescription.setBounds(90, 220, 350, 30);

        m_jtxtChairs.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jtxtChairs.setMaximumSize(new java.awt.Dimension(50, 25));
        m_jtxtChairs.setMinimumSize(new java.awt.Dimension(50, 25));
        m_jtxtChairs.setPreferredSize(new java.awt.Dimension(50, 25));
        jPanel1.add(m_jtxtChairs);
        m_jtxtChairs.setBounds(90, 190, 90, 25);

        txtCustomer.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtCustomer.setMaximumSize(new java.awt.Dimension(200, 25));
        txtCustomer.setMinimumSize(new java.awt.Dimension(200, 25));
        txtCustomer.setPreferredSize(new java.awt.Dimension(232, 25));
        jPanel1.add(txtCustomer);
        txtCustomer.setBounds(90, 160, 220, 25);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/customer_add_sml.png"))); // NOI18N
        jButton1.setToolTipText(bundle.getString("tiptext.showcustomers")); // NOI18N
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.setMaximumSize(new java.awt.Dimension(40, 33));
        jButton1.setMinimumSize(new java.awt.Dimension(40, 33));
        jButton1.setPreferredSize(new java.awt.Dimension(40, 33));
        jButton1.setRequestFocusEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);
        jButton1.setBounds(320, 160, 40, 33);

        jPanel2.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel5.setLayout(new java.awt.BorderLayout());
        jPanel5.add(m_jKeys, java.awt.BorderLayout.NORTH);

        jPanel2.add(jPanel5, java.awt.BorderLayout.LINE_END);

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtnReceiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnReceiveActionPerformed
             
        
        // marco el cliente como recibido...
        m_bReceived = true;
        m_Dirty.setDirty(true);
        
        try {
            m_bd.saveData();
            m_restaurantmap.viewTables(customer);      
        } catch (BasicException eD) {
            MessageInf msg = new MessageInf(MessageInf.SGN_NOTICE, LocalRes.getIntString("message.nosaveticket"), eD);
            msg.show(this);
        }       
        
    }//GEN-LAST:event_m_jbtnReceiveActionPerformed

    private void m_jbtnTablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnTablesActionPerformed

        m_restaurantmap.viewTables();
        
    }//GEN-LAST:event_m_jbtnTablesActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        JCustomerFinder finder = JCustomerFinder.getCustomerFinder(this, dlCustomers);
        finder.search(customer);
        finder.setVisible(true);
        
        CustomerInfo c = finder.getSelectedCustomer(); 
        
        if (c == null) {       
            assignCustomer(new CustomerInfo(null));
        } else {
            assignCustomer(c);
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelDate;
    private javax.swing.JPanel jPanelTime;
    private uk.chromis.editor.JEditorKeys m_jKeys;
    private javax.swing.JPanel m_jPanelList;
    private javax.swing.JPanel m_jPanelTime;
    private javax.swing.JPanel m_jToolbar;
    private javax.swing.JPanel m_jToolbarContainer;
    private javax.swing.JButton m_jbtnReceive;
    private javax.swing.JButton m_jbtnTables;
    private uk.chromis.editor.JEditorIntegerPositive m_jtxtChairs;
    private uk.chromis.editor.JEditorString m_jtxtDescription;
    private uk.chromis.editor.JEditorString txtCustomer;
    // End of variables declaration//GEN-END:variables
    
}
