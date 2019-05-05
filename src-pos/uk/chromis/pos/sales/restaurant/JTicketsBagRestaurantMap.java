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
import java.awt.CardLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.gui.NullIcon;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerReadClass;
import uk.chromis.data.loader.StaticSentence;
import uk.chromis.pos.customers.CustomerInfo;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.sales.DataLogicReceipts;
import uk.chromis.pos.sales.JTicketsBag;
import uk.chromis.pos.sales.SharedTicketInfo;
import uk.chromis.pos.sales.TicketsEditor;
import uk.chromis.pos.ticket.TicketInfo;
import uk.chromis.pos.ticket.TicketLineInfo;
import uk.chromis.pos.util.AutoLogoff;
import uk.chromis.pos.util.AutoRefresh;

/**
 *
 *
 */
public class JTicketsBagRestaurantMap extends JTicketsBag {

    /**
     *
     */
    private static class ServerCurrent {

        public ServerCurrent() {
        }
    }

    private java.util.List<Place> m_aplaces;
    private java.util.List<Floor> m_afloors;

    private JTicketsBagRestaurant m_restaurantmap;
    private JTicketsBagRestaurantRes m_jreservations;

    private Place m_PlaceCurrent;
    private ServerCurrent m_ServerCurrent;
    private Place m_PlaceClipboard;
    private CustomerInfo customer;

    private DataLogicReceipts dlReceipts = null;
    private DataLogicSales dlSales = null;
    private DataLogicSystem dlSystem = null;
    private final RestaurantDBUtils restDB;
    private static final Icon ICO_OCU_SM = new ImageIcon(Place.class.getResource("/uk/chromis/images/edit_group_sm.png"));
    private static final Icon ICO_WAITER = new NullIcon(1, 1);
    private static final Icon ICO_FRE = new NullIcon(22, 22);
    private String waiterDetails;
    private String customerDetails;
    private String tableName;
    private Boolean transparentButtons;
    private Boolean actionEnabled = true;
    private int newX;
    private int newY;
    private AppView m_app;

    /**
     * Creates new form JTicketsBagRestaurant
     *
     * @param app
     * @param panelticket
     */
    public JTicketsBagRestaurantMap(AppView app, TicketsEditor panelticket) {

        super(app, panelticket);
        m_app = app;

        // create a refresh timer action if required
        Action refreshTables = new refreshTables();
        if (AppConfig.getInstance().getBoolean("tables.autorefresh")) {
            AutoRefresh.getInstance().setTimer(5 * 1000, refreshTables);
            AutoRefresh.getInstance().activateTimer();
        }

        restDB = new RestaurantDBUtils();
        transparentButtons = AppConfig.getInstance().getBoolean("table.transparentbuttons");

        dlReceipts = (DataLogicReceipts) app.getBean("uk.chromis.pos.sales.DataLogicReceipts");
        dlSales = (DataLogicSales) m_App.getBean("uk.chromis.pos.forms.DataLogicSales");
        dlSystem = (DataLogicSystem) m_App.getBean("uk.chromis.pos.forms.DataLogicSystem");

        m_restaurantmap = new JTicketsBagRestaurant(app, this);
        m_PlaceCurrent = null;
        m_PlaceClipboard = null;
        customer = null;

        try {
            SentenceList sent = new StaticSentence(
                    app.getSession(),
                    "SELECT ID, NAME, IMAGE FROM FLOORS ORDER BY NAME",
                    null,
                    new SerializerReadClass(Floor.class));
            m_afloors = sent.list();

        } catch (BasicException eD) {
            m_afloors = new ArrayList<>();
        }
        try {
            SentenceList sent = new StaticSentence(
                    app.getSession(),
                    "SELECT ID, NAME, X, Y, FLOOR, CUSTOMER, WAITER, TICKETID, TABLEMOVED FROM PLACES ORDER BY FLOOR",
                    null,
                    new SerializerReadClass(Place.class));
            m_aplaces = sent.list();
        } catch (BasicException eD) {
            m_aplaces = new ArrayList<>();
        }

        initComponents();

        // add the Floors containers
        if (m_afloors.size() > 1) {
            // A tab container for 2 or more floors
            JTabbedPane jTabFloors = new JTabbedPane();
            jTabFloors.applyComponentOrientation(getComponentOrientation());
            jTabFloors.setBorder(new javax.swing.border.EmptyBorder(new Insets(5, 5, 5, 5)));
            jTabFloors.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            jTabFloors.setFocusable(false);
            jTabFloors.setRequestFocusEnabled(false);
            m_jPanelMap.add(jTabFloors, BorderLayout.CENTER);

            for (Floor f : m_afloors) {
                f.getContainer().applyComponentOrientation(getComponentOrientation());

                JScrollPane jScrCont = new JScrollPane();
                jScrCont.applyComponentOrientation(getComponentOrientation());
                JPanel jPanCont = new JPanel();
                jPanCont.applyComponentOrientation(getComponentOrientation());

                jTabFloors.addTab(f.getName(), f.getIcon(), jScrCont);
                jScrCont.setViewportView(jPanCont);
                jPanCont.add(f.getContainer());
            }
        } else if (m_afloors.size() == 1) {
            // Just a frame for 1 floor
            Floor f = m_afloors.get(0);
            f.getContainer().applyComponentOrientation(getComponentOrientation());

            JPanel jPlaces = new JPanel();
            jPlaces.applyComponentOrientation(getComponentOrientation());
            jPlaces.setLayout(new BorderLayout());
            jPlaces.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.EmptyBorder(new Insets(5, 5, 5, 5)),
                    new javax.swing.border.TitledBorder(f.getName())));

            JScrollPane jScrCont = new JScrollPane();
            jScrCont.applyComponentOrientation(getComponentOrientation());
            JPanel jPanCont = new JPanel();
            jPanCont.applyComponentOrientation(getComponentOrientation());

            // jPlaces.setLayout(new FlowLayout());           
            m_jPanelMap.add(jPlaces, BorderLayout.CENTER);
            jPlaces.add(jScrCont, BorderLayout.CENTER);
            jScrCont.setViewportView(jPanCont);
            jPanCont.add(f.getContainer());
        }

        // Add all the Table buttons.
        Floor currfloor = null;

        for (Place pl : m_aplaces) {
            int iFloor = 0;

            if (currfloor == null || !currfloor.getID().equals(pl.getFloor())) {
                // Look for a new floor
                do {
                    currfloor = m_afloors.get(iFloor++);
                } while (!currfloor.getID().equals(pl.getFloor()));
            }

            currfloor.getContainer().add(pl.getButton());
            pl.setButtonBounds();

            if (transparentButtons) {
                pl.getButton().setOpaque(false);
                pl.getButton().setContentAreaFilled(false);
                pl.getButton().setBorderPainted(false);
            }

            pl.getButton().addMouseMotionListener(new MouseAdapter() {

                public void mouseDragged(MouseEvent E) {
                    if (!actionEnabled) {
                        if (pl.getDiffX() == 0) {
                            pl.setDiffX(pl.getButton().getX() - pl.getX());
                            pl.setDiffY(pl.getButton().getY() - pl.getY());
                        }
                        newX = E.getX() + pl.getButton().getX();
                        newY = E.getY() + pl.getButton().getY();
                        pl.getButton().setBounds(newX + pl.getDiffX(), newY + pl.getDiffY(), pl.getButton().getWidth(), pl.getButton().getHeight());
                        // pl.setChanged(true);
                        pl.setX(newX);
                        pl.setY(newY);
                    }
                }
            }
            );

            pl.getButton().addActionListener(new MyActionListener(pl));
        }

        // Add the reservations panel
        m_jreservations = new JTicketsBagRestaurantRes(app, this);
        add(m_jreservations, "res");
        m_btnSavePlaces.setVisible(false);
        m_btnSetupMode.setVisible(AppConfig.getInstance().getBoolean("tables.redesign"));
    }

    private class refreshTables extends AbstractAction {

        public refreshTables() {
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            m_jbtnRefreshActionPerformed(null);
            //  AutoRefresh.getInstance().activateTimer();
        }
    }

    /**
     *
     */
    @Override
    public void activate() {
        // precondicion es que no tenemos ticket activado ni ticket en el panel
        m_PlaceClipboard = null;
        customer = null;
        loadTickets();
        printState();

        m_panelticket.setActiveTicket(null, null);
        m_restaurantmap.activate();

        showView("map"); // arrancamos en la vista de las mesas.
    }

    /**
     *
     * @return
     */
    @Override
    public boolean deactivate() {

        AutoRefresh.getInstance().deactivateTimer();
        // precondicion es que tenemos ticket activado aqui y ticket en el panel
        if (viewTables()) {
            m_PlaceClipboard = null;
            customer = null;

            if (m_PlaceCurrent != null) {

                try {
                    dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket(), m_panelticket.getActiveTicket().getPickupId());
                } catch (BasicException e) {
                    new MessageInf(e).show(this);
                }

                m_PlaceCurrent = null;
            }
            printState();
            m_panelticket.setActiveTicket(null, null);

            AutoLogoff.getInstance().deactivateTimer();

            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return
     */
    @Override
    protected JComponent getBagComponent() {
        return m_restaurantmap;
    }

    /**
     *
     * @return
     */
    @Override
    protected JComponent getNullComponent() {
        return this;
    }

    /**
     *
     * @return
     */
    public TicketInfo getActiveTicket() {
        return m_panelticket.getActiveTicket();
    }

    /**
     *
     */
    public void moveTicket() {
        if (m_PlaceCurrent != null) {

            try {
                dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket(), m_panelticket.getActiveTicket().getPickupId());
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }

            m_PlaceClipboard = m_PlaceCurrent;

            customer = null;
            m_PlaceCurrent = null;
        }

        printState();
        m_panelticket.setActiveTicket(null, null);
    }

    /**
     *
     * @param c
     * @return
     */
    public boolean viewTables(CustomerInfo c) {
        if (m_jreservations.deactivate()) {
            showView("map");
            m_PlaceClipboard = null;
            customer = c;
            printState();
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return
     */
    public boolean viewTables() {
        return viewTables(null);
    }

    /**
     *
     */
    public void newTicket() {
        AutoRefresh.getInstance().activateTimer();
        if (AppConfig.getInstance().getBoolean("till.createorder") && m_panelticket.getActiveTicket().getArticlesCount() == 0) {
            deleteTicket();
        } else if (m_PlaceCurrent != null) {
            try {
                dlReceipts.updateSharedTicket(m_PlaceCurrent.getId(), m_panelticket.getActiveTicket(), m_panelticket.getActiveTicket().getPickupId());
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }
            m_PlaceCurrent = null;
        }
        printState();
        m_panelticket.setActiveTicket(null, null);
    }

    /**
     *
     * @return
     */
    public String getTable() {
        String id = null;
        if (m_PlaceCurrent != null) {
            id = m_PlaceCurrent.getId();
        }
        return (id);
    }

    /**
     *
     * @return
     */
    public String getTableName() {
        String tableName = null;
        if (m_PlaceCurrent != null) {
            tableName = m_PlaceCurrent.getName();
        }
        return (tableName);
    }

    /**
     *
     */
    @Override
    public void deleteTicket() {

        if (m_PlaceCurrent != null) {
            String id = m_PlaceCurrent.getId();
            try {
                dlReceipts.deleteSharedTicket(id);
            } catch (BasicException e) {
                new MessageInf(e).show(this);
            }

            m_PlaceCurrent.setPeople(false);

            m_PlaceCurrent = null;
        }

        printState();
        m_panelticket.setActiveTicket(null, null);
    }

    /**
     *
     */
    public void changeServer() {

        if (m_ServerCurrent != null) {

//          Show list of Users
//          Allow Users - CurrentUsers select
//          Compare Users
//          If newServer equal.currentUser
//              Msg NoChange
//          else
//              m_ServerCurrent.setPeople(newServer);
//              Msg Changed to NewServer
        }
    }

    /**
     *
     */
    public void loadTickets() {

        AutoRefresh.getInstance().activateTimer();
        Set<String> atickets = new HashSet<>();

        try {
            java.util.List<SharedTicketInfo> l = dlReceipts.getSharedTicketList();
            for (SharedTicketInfo ticket : l) {
                atickets.add(ticket.getId());
            }
        } catch (BasicException e) {
            new MessageInf(e).show(this);
        }

        for (Place table : m_aplaces) {
            table.setPeople(atickets.contains(table.getId()));
        }
    }

    private void printState() {

        if (m_PlaceClipboard == null) {
            if (customer == null) {
                // Select a table
                m_jText.setText(null);
                // Enable all tables
                for (Place place : m_aplaces) {
                    place.getButton().setEnabled(true);
// get the customer details form the database
// We have set the option show details on table.  
                    if (AppConfig.getInstance().getProperty("table.tablecolour") == null) {
                        tableName = "<style=font-size:9px;font-weight:bold;><font color = black>" + place.getName() + "</font></style>";
                    } else {
                        tableName = "<style=font-size:9px;font-weight:bold;><font color =" + AppConfig.getInstance().getProperty("table.tablecolour") + ">" + place.getName() + "</font></style>";
                    }

                    if (AppConfig.getInstance().getBoolean("table.showwaiterdetails")) {
                        if (AppConfig.getInstance().getProperty("table.waitercolour") == null) {
                            waiterDetails = (restDB.getWaiterNameInTable(place.getName()) == null) ? "" : "<style=font-size:9px;font-weight:bold;><font color = red>"
                                    + restDB.getWaiterNameInTableById(place.getId()) + "</font></style><br>";
                        } else {
                            waiterDetails = (restDB.getWaiterNameInTable(place.getName()) == null) ? "" : "<style=font-size:9px;font-weight:bold;><font color ="
                                    + AppConfig.getInstance().getProperty("table.waitercolour") + ">" + restDB.getWaiterNameInTableById(place.getId()) + "</font></style><br>";
                        }
                        place.getButton().setIcon(ICO_OCU_SM);
                    } else {
                        waiterDetails = "";
                    }

                    if (AppConfig.getInstance().getBoolean("table.showcustomerdetails")) {
                        place.getButton().setIcon(((AppConfig.getInstance().getBoolean("table.showwaiterdetails")) && (restDB.getCustomerNameInTable(place.getName()) != null)) ? ICO_WAITER : ICO_OCU_SM);
                        if (AppConfig.getInstance().getProperty("table.customercolour") == null) {
                            customerDetails = (restDB.getCustomerNameInTable(place.getName()) == null) ? "" : "<style=font-size:9px;font-weight:bold;><font color = blue>"
                                    + restDB.getCustomerNameInTableById(place.getId()) + "</font></style><br>";
                        } else {
                            customerDetails = (restDB.getCustomerNameInTable(place.getName()) == null) ? "" : "<style=font-size:9px;font-weight:bold;><font color ="
                                    + AppConfig.getInstance().getProperty("table.customercolour") + ">" + restDB.getCustomerNameInTableById(place.getId()) + "</font></style><br>";
                        }
                    } else {
                        customerDetails = "";
                    }

                    if ((AppConfig.getInstance().getBoolean("table.showwaiterdetails"))
                            || (AppConfig.getInstance().getBoolean("table.showcustomerdetails"))) {
                        place.getButton().setText("<html><center>" + customerDetails + waiterDetails + tableName + "</html>");
                    } else {
                        if (AppConfig.getInstance().getProperty("table.tablecolour") == null) {
                            tableName = "<style=font-size:10px;font-weight:bold;><font color = black>" + place.getName() + "</font></style>";
                        } else {
                            tableName = "<style=font-size:10px;font-weight:bold;><font color =" + AppConfig.getInstance().getProperty("table.tablecolour") + ">" + place.getName() + "</font></style>";
                        }

                        place.getButton().setText("<html><center>" + tableName + "</html>");

                    }
                    if (!place.hasPeople()) {
                        place.getButton().setIcon(ICO_FRE);
                    }
                }

                m_jbtnReservations.setEnabled(true);
            } else {
                // receive a customer
                m_jText.setText(AppLocal.getIntString("label.restaurantcustomer", new Object[]{customer.getName()}));
                // Enable all tables
                for (Place place : m_aplaces) {
                    place.getButton().setEnabled(!place.hasPeople());
                }
                m_jbtnReservations.setEnabled(false);
            }
        } else {
            // Moving or merging the receipt to another table
            m_jText.setText(AppLocal.getIntString("label.restaurantmove", new Object[]{m_PlaceClipboard.getName()}));
            // Enable all empty tables and origin table.
            for (Place place : m_aplaces) {
                place.getButton().setEnabled(true);
            }
            m_jbtnReservations.setEnabled(false);
        }

    }

    private TicketInfo getTicketInfo(Place place) {

        try {
            return dlReceipts.getSharedTicket(place.getId());
        } catch (BasicException e) {
            new MessageInf(e).show(JTicketsBagRestaurantMap.this);
            return null;
        }
    }

    private void setActivePlace(Place place, TicketInfo ticket) {
        m_PlaceCurrent = place;
        m_panelticket.setActiveTicket(ticket, m_PlaceCurrent.getName());
    }

    private void showView(String view) {
        CardLayout cl = (CardLayout) (getLayout());
        cl.show(this, view);
    }

    private class MyActionListener implements ActionListener {

        private final Place m_place;

        public MyActionListener(Place place) {
            m_place = place;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (!restDB.getTableLock(m_place.getId()) || getDbVersion().equals("d")) {
                if (!actionEnabled) {
                    m_place.setDiffX(0);
                }
                // disable the action if edit mode
                if (actionEnabled) {
                    //disable table refresh
                    AutoRefresh.getInstance().deactivateTimer();

                    if (m_PlaceClipboard == null) {

                        if (customer == null) {
                            // check if the sharedticket is the same
                            TicketInfo ticket = getTicketInfo(m_place);

                            // check
                            if (ticket == null && !m_place.hasPeople()) {
                                // Empty table and checked

                                // table occupied
                                ticket = new TicketInfo();
                                try {
//Create a new pickup code because this is a new ticket                            
                                    dlReceipts.insertSharedTicket(m_place.getId(), ticket, ticket.getPickupId());
                                } catch (BasicException e) {
                                    new MessageInf(e).show(JTicketsBagRestaurantMap.this); // Glup. But It was empty.
                                }
                                m_place.setPeople(true);
                                setActivePlace(m_place, ticket);

                            } else if (ticket == null && m_place.hasPeople()) {
                                // The table is now empty
                                new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tableempty")).show(JTicketsBagRestaurantMap.this);
                                m_place.setPeople(false); // fixed  
                            } else if (ticket != null && !m_place.hasPeople()) {
                                // The table is now full
                                new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tablefull")).show(JTicketsBagRestaurantMap.this);
                                m_place.setPeople(true);

                            } else { // both != null
                                // Full table                
                                // m_place.setPeople(true); // already true                           
                                setActivePlace(m_place, ticket);

                            }
                        } else {
                            // receiving customer.

                            // check if the sharedticket is the same
                            TicketInfo ticket = getTicketInfo(m_place);
                            if (ticket == null) {
                                // receive the customer
                                // table occupied
                                ticket = new TicketInfo();

                                try {
                                    ticket.setCustomer(customer.getId() == null
                                            ? null
                                            : dlSales.loadCustomerExt(customer.getId()));
                                } catch (BasicException e) {
                                    MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindcustomer"), e);
                                    msg.show(JTicketsBagRestaurantMap.this);
                                }

                                try {
                                    dlReceipts.insertSharedTicket(m_place.getId(), ticket, ticket.getPickupId());
                                } catch (BasicException e) {
                                    new MessageInf(e).show(JTicketsBagRestaurantMap.this); // Glup. But It was empty.
                                }
                                m_place.setPeople(true);
                                m_PlaceClipboard = null;
                                customer = null;

                                setActivePlace(m_place, ticket);
                            } else {
                                // TODO: msg: The table is now full
                                new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tablefull")).show(JTicketsBagRestaurantMap.this);
                                m_place.setPeople(true);
                                m_place.getButton().setEnabled(false);
                            }
                        }
                    } else {
                        // check if the sharedticket is the same
                        TicketInfo ticketclip = getTicketInfo(m_PlaceClipboard);

                        if (ticketclip == null) {
                            new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tableempty")).show(JTicketsBagRestaurantMap.this);
                            m_PlaceClipboard.setPeople(false);
                            m_PlaceClipboard = null;
                            customer = null;
                            printState();
                        } else // tenemos que copiar el ticket del clipboard
                        {
                            if (m_PlaceClipboard == m_place) {
                                // the same button. Canceling.
                                Place placeclip = m_PlaceClipboard;
                                m_PlaceClipboard = null;
                                customer = null;
                                printState();
                                setActivePlace(placeclip, ticketclip);
                            } else if (!m_place.hasPeople()) {
                                // Moving the receipt to an empty table
                                TicketInfo ticket = getTicketInfo(m_place);
                                if (ticket == null) {
                                    try {
                                        dlReceipts.insertSharedTicket(m_place.getId(), ticketclip, ticketclip.getPickupId());//dlSales.getNextPickupIndex());
                                        m_place.setPeople(true);
                                        dlReceipts.deleteSharedTicket(m_PlaceClipboard.getId());
                                        m_PlaceClipboard.setPeople(false);
                                    } catch (BasicException e) {
                                        new MessageInf(e).show(JTicketsBagRestaurantMap.this); // Glup. But It was empty.
                                    }

                                    m_PlaceClipboard = null;
                                    customer = null;
                                    printState();

                                    // No hace falta preguntar si estaba bloqueado porque ya lo estaba antes
                                    // activamos el ticket seleccionado
                                    setActivePlace(m_place, ticketclip);

                                } else {
                                    // Full table
                                    new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tablefull")).show(JTicketsBagRestaurantMap.this);
                                    m_PlaceClipboard.setPeople(true);
                                    printState();
                                }
                            } else {
                                // Merge the lines with the receipt of the table
                                TicketInfo ticket = getTicketInfo(m_place);

                                if (ticket == null) {
                                    // The table is now empty
                                    new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.tableempty")).show(JTicketsBagRestaurantMap.this);
                                    m_place.setPeople(false); // fixed                        
                                } else //asks if you want to merge tables
                                {
                                    if (JOptionPane.showConfirmDialog(JTicketsBagRestaurantMap.this, AppLocal.getIntString("message.mergetablequestion"), AppLocal.getIntString("message.mergetable"), JOptionPane.YES_NO_OPTION)
                                            == JOptionPane.YES_OPTION) {
                                        // merge lines ticket

                                        try {
                                            dlReceipts.deleteSharedTicket(m_PlaceClipboard.getId());
                                            m_PlaceClipboard.setPeople(false);
                                            if (ticket.getCustomer() == null) {
                                                ticket.setCustomer(ticketclip.getCustomer());
                                            }
                                            for (TicketLineInfo line : ticketclip.getLines()) {
                                                ticket.addLine(line);
                                            }
                                            dlReceipts.updateSharedTicket(m_place.getId(), ticket, ticket.getPickupId());
                                            //restDB.clearTableLockByTicket(tableName);
                                        } catch (BasicException e) {
                                            new MessageInf(e).show(JTicketsBagRestaurantMap.this); // Glup. But It was empty.
                                        }

                                        m_PlaceClipboard = null;
                                        customer = null;
//clear the original table data
                                        restDB.clearCustomerNameInTable(restDB.getTableDetails(ticketclip.getId()));
                                        restDB.clearWaiterNameInTable(restDB.getTableDetails(ticketclip.getId()));
                                        restDB.clearTableMovedFlag(restDB.getTableDetails(ticketclip.getId()));
                                        restDB.clearTicketIdInTable(restDB.getTableDetails(ticketclip.getId()));
                                        printState();
                                        setActivePlace(m_place, ticket);
                                    } else {
                                        // Cancel merge operations
                                        Place placeclip = m_PlaceClipboard;
                                        m_PlaceClipboard = null;
                                        customer = null;
                                        printState();
                                        setActivePlace(placeclip, ticketclip);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, restDB.getTableOpenedBy(m_place.getId()) + AppLocal.getIntString("message.tableopen"), AppLocal.getIntString("message.tablelocked"), JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     *
     * @param btnText
     */
    public void setButtonTextBags(String btnText) {
        m_PlaceClipboard.setButtonText(btnText);
    }

    public String getDbVersion() {
        String sdbmanager = dlSystem.getDBVersion();
        if (("HSQL Database Engine".equals(sdbmanager)) | ("HSQLDB".equals(sdbmanager))) {
            return ("h");
        } else if ("MySQL".equals(sdbmanager)) {
            return ("m");
        } else if ("PostgreSQL".equals(sdbmanager)) {
            return ("p");
        } else if ("Oracle".equals(sdbmanager)) {
            return ("o");
        } else if ("Apache Derby".equals(sdbmanager)) {
            return ("d");
        } else if ("Derby".equals(sdbmanager)) {
            return ("d");
        } else {
            return ("x");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jPanelMap = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        m_jbtnReservations = new javax.swing.JButton();
        m_jbtnRefresh = new javax.swing.JButton();
        m_jText = new javax.swing.JLabel();
        m_btnSetupMode = new javax.swing.JButton();
        m_btnSavePlaces = new javax.swing.JButton();

        setLayout(new java.awt.CardLayout());

        m_jPanelMap.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jPanelMap.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        m_jbtnReservations.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jbtnReservations.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/date.png"))); // NOI18N
        m_jbtnReservations.setText(AppLocal.getIntString("button.reservations")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        m_jbtnReservations.setToolTipText(bundle.getString("tiptext.openreservationsscreen")); // NOI18N
        m_jbtnReservations.setFocusPainted(false);
        m_jbtnReservations.setFocusable(false);
        m_jbtnReservations.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnReservations.setMaximumSize(new java.awt.Dimension(133, 40));
        m_jbtnReservations.setMinimumSize(new java.awt.Dimension(133, 40));
        m_jbtnReservations.setPreferredSize(new java.awt.Dimension(150, 40));
        m_jbtnReservations.setRequestFocusEnabled(false);
        m_jbtnReservations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnReservationsActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnReservations);

        m_jbtnRefresh.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jbtnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/reload.png"))); // NOI18N
        m_jbtnRefresh.setText(AppLocal.getIntString("button.reloadticket")); // NOI18N
        m_jbtnRefresh.setToolTipText(bundle.getString("tiptext.reloadtabledata")); // NOI18N
        m_jbtnRefresh.setFocusPainted(false);
        m_jbtnRefresh.setFocusable(false);
        m_jbtnRefresh.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jbtnRefresh.setMaximumSize(new java.awt.Dimension(100, 40));
        m_jbtnRefresh.setMinimumSize(new java.awt.Dimension(100, 40));
        m_jbtnRefresh.setPreferredSize(new java.awt.Dimension(150, 40));
        m_jbtnRefresh.setRequestFocusEnabled(false);
        m_jbtnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtnRefreshActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtnRefresh);
        jPanel2.add(m_jText);

        m_btnSetupMode.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_btnSetupMode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/movetable.png"))); // NOI18N
        m_btnSetupMode.setText(AppLocal.getIntString("button.layout")); // NOI18N
        m_btnSetupMode.setToolTipText("");
        m_btnSetupMode.setFocusPainted(false);
        m_btnSetupMode.setFocusable(false);
        m_btnSetupMode.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_btnSetupMode.setMaximumSize(new java.awt.Dimension(100, 40));
        m_btnSetupMode.setMinimumSize(new java.awt.Dimension(100, 40));
        m_btnSetupMode.setPreferredSize(new java.awt.Dimension(150, 40));
        m_btnSetupMode.setRequestFocusEnabled(false);
        m_btnSetupMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_btnSetupModeActionPerformed(evt);
            }
        });
        jPanel2.add(m_btnSetupMode);

        m_btnSavePlaces.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_btnSavePlaces.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/filesave.png"))); // NOI18N
        m_btnSavePlaces.setText(AppLocal.getIntString("Button.Save")); // NOI18N
        m_btnSavePlaces.setToolTipText("");
        m_btnSavePlaces.setFocusPainted(false);
        m_btnSavePlaces.setFocusable(false);
        m_btnSavePlaces.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_btnSavePlaces.setMaximumSize(new java.awt.Dimension(100, 40));
        m_btnSavePlaces.setMinimumSize(new java.awt.Dimension(100, 40));
        m_btnSavePlaces.setPreferredSize(new java.awt.Dimension(100, 40));
        m_btnSavePlaces.setRequestFocusEnabled(false);
        m_btnSavePlaces.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_btnSavePlacesActionPerformed(evt);
            }
        });
        jPanel2.add(m_btnSavePlaces);

        jPanel1.add(jPanel2, java.awt.BorderLayout.LINE_START);

        m_jPanelMap.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        add(m_jPanelMap, "map");
    }// </editor-fold>//GEN-END:initComponents

    private void m_jbtnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnRefreshActionPerformed
        m_PlaceClipboard = null;
        customer = null;
        loadTickets();
        printState();
    }//GEN-LAST:event_m_jbtnRefreshActionPerformed

    private void m_jbtnReservationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtnReservationsActionPerformed
        showView("res");
        m_jreservations.activate();
    }//GEN-LAST:event_m_jbtnReservationsActionPerformed

    private void m_btnSetupModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_btnSetupModeActionPerformed
        if (java.util.ResourceBundle.getBundle("pos_messages").getString("button.layout").equals(m_btnSetupMode.getText())) {
            actionEnabled = false;
            m_btnSavePlaces.setVisible(true);
            m_btnSetupMode.setText(java.util.ResourceBundle.getBundle("pos_messages").getString("button.disablelayout"));

            for (Place pl : m_aplaces) {
                if (transparentButtons) {
                    pl.getButton().setOpaque(true);
                    pl.getButton().setContentAreaFilled(true);
                    pl.getButton().setBorderPainted(true);
                }
            }
        } else {
            actionEnabled = true;
            m_btnSavePlaces.setVisible(false);
            m_btnSetupMode.setText(java.util.ResourceBundle.getBundle("pos_messages").getString("button.layout"));

            for (Place pl : m_aplaces) {
                if (transparentButtons) {
                    pl.getButton().setOpaque(false);
                    pl.getButton().setContentAreaFilled(false);
                    pl.getButton().setBorderPainted(false);
                }
            }
        }
    }//GEN-LAST:event_m_btnSetupModeActionPerformed

    private void m_btnSavePlacesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_btnSavePlacesActionPerformed
        for (Place pl : m_aplaces) {
            try {
                //   if (pl.getChanged()) {
                //  dlSystem.updatePlaces(pl.getX() + (pl.getButton().getWidth() / 2), pl.getY() + (pl.getButton().getHeight() / 2), pl.getId());
                dlSystem.updatePlaces(pl.getX(), pl.getY(), pl.getId());
                //    pl.setChanged(false);
                // }
            } catch (BasicException ex) {
                Logger.getLogger(JTicketsBagRestaurantMap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_m_btnSavePlacesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton m_btnSavePlaces;
    private javax.swing.JButton m_btnSetupMode;
    private javax.swing.JPanel m_jPanelMap;
    private javax.swing.JLabel m_jText;
    private javax.swing.JButton m_jbtnRefresh;
    private javax.swing.JButton m_jbtnReservations;
    // End of variables declaration//GEN-END:variables

}
