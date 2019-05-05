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

package uk.chromis.pos.forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;
import uk.chromis.pos.instance.AppMessage;
import uk.chromis.pos.instance.InstanceManager;
import uk.chromis.pos.util.OSValidator;

public class JAdminFrame extends javax.swing.JFrame implements AppMessage {

    private InstanceManager m_instmanager = null;

    private JAdminApp m_rootapp;
    private AppProperties m_props;

    private OSValidator m_OS;
    public JPrincipalApp m_principalapp;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem logoffItem;
    private JMenuItem exitItem;
    private JMenuItem password;
    private final ResourceBundle bundle;

    /**
     * Creates new form JAdminFrame
     */
    public JAdminFrame() {
        bundle = java.util.ResourceBundle.getBundle("admin_messages");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        initComponents();
        createExitOnly();
    }

    private void addItem(JMenu menu, String itemMessage, String action) {
        JMenuItem tmpItem = new JMenuItem(itemMessage);
        menu.add(tmpItem);
        tmpItem.setActionCommand(action);
        tmpItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                m_rootapp.getPrincipalApp().showTask(action);
            }
        });
    }

    public void createExitOnly() {
        menuBar = new JMenuBar();
        fileMenu = new JMenu();
        fileMenu.setText(bundle.getString("Menu.file"));

        exitItem = new JMenuItem();
        exitItem.setText(bundle.getString("Menu.exit"));
        fileMenu.add(exitItem);
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                System.exit(1);
            }
        });
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    public void createMenu() {
        fileMenu.remove(exitItem);

        //JMenu registerMenu = new JMenu();
        JMenu stockMenu = new JMenu();
        JMenu customerMenu = new JMenu();
        JMenu reportsMenu = new JMenu();
        JMenu toolMenu = new JMenu();
        JMenu maintenanceMenu = new JMenu();
        JMenu helpMenu = new JMenu();
        JMenu pmMenu = new JMenu();

        
        password = new JMenuItem();       
        password.setText(AppLocal.getIntString("Menu.ChangePassword"));
        fileMenu.add(password);
        password.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                m_rootapp.getPrincipalApp().changePassword();
            }
        });        
        fileMenu.add(new JPopupMenu.Separator());
        logoffItem = new JMenuItem();
        logoffItem.setText(bundle.getString("Menu.logoff"));
        fileMenu.add(logoffItem);
        logoffItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                m_rootapp.exitToLogin();
            }
        });
        fileMenu.add(new JPopupMenu.Separator());
        fileMenu.add(exitItem);
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                System.exit(1);
            }
        });

// *****************************************************************************
/*
        registerMenu.setText(bundle.getString("Menu.register"));
        addItem(registerMenu, AppLocal.getIntString("Menu.Ticket"), "uk.chromis.pos.sales.JPanelTicketSales");
        addItem(registerMenu, AppLocal.getIntString("Menu.TicketEdit"), "uk.chromis.pos.sales.JPanelTicketEdits");
        addItem(registerMenu, AppLocal.getIntString("Menu.CustomersPayment"), "uk.chromis.pos.customers.CustomersPayment");
        addItem(registerMenu, AppLocal.getIntString("Menu.Payments"), "uk.chromis.pos.panels.JPanelPayments");
        addItem(registerMenu, AppLocal.getIntString("Menu.CloseTPV"), "uk.chromis.pos.panels.JPanelCloseMoney");
*/
// *****************************************************************************
        stockMenu.setText(bundle.getString("Menu.stock"));
        addItem(stockMenu, AppLocal.getIntString("Menu.Products"), "uk.chromis.pos.inventory.ProductsPanel");
        stockMenu.add(new JPopupMenu.Separator());
        addItem(stockMenu, AppLocal.getIntString("Menu.AttributeValues"), "uk.chromis.pos.inventory.AttributeValuesPanel");
        addItem(stockMenu, AppLocal.getIntString("Menu.Attributes"), "uk.chromis.pos.inventory.AttributesPanel");
        addItem(stockMenu, AppLocal.getIntString("Menu.AttributeSets"), "uk.chromis.pos.inventory.AttributeSetsPanel");
        addItem(stockMenu, AppLocal.getIntString("Menu.AttributeUse"), "uk.chromis.pos.inventory.AttributeUsePanel");
        stockMenu.add(new JPopupMenu.Separator());
        addItem(stockMenu, AppLocal.getIntString("Menu.Auxiliar"), "uk.chromis.pos.inventory.AuxiliarPanel");
        stockMenu.add(new JPopupMenu.Separator());
        addItem(stockMenu, AppLocal.getIntString("Menu.Categories"), "uk.chromis.pos.inventory.CategoriesPanel");
        stockMenu.add(new JPopupMenu.Separator());
        addItem(stockMenu, AppLocal.getIntString("Menu.ProductsWarehouse"), "uk.chromis.pos.inventory.ProductsWarehousePanel");
        stockMenu.add(new JPopupMenu.Separator());
        addItem(stockMenu, AppLocal.getIntString("Menu.StockDiary"), "uk.chromis.pos.inventory.StockDiaryPanel");
        addItem(stockMenu, AppLocal.getIntString("Menu.StockMovement"), "uk.chromis.pos.inventory.StockManagement");
        stockMenu.add(new JPopupMenu.Separator());
        addItem(stockMenu, AppLocal.getIntString("Menu.Promotions"), "uk.chromis.pos.promotion.PromotionPanel");
        stockMenu.add(new JPopupMenu.Separator());
        addItem(stockMenu, AppLocal.getIntString("Menu.Recipe"), "uk.chromis.pos.inventory.RecipePanel");

// *****************************************************************************
        customerMenu.setText(bundle.getString("Menu.customers"));
        addItem(customerMenu, "Customers", "uk.chromis.pos.customers.CustomersPanel");

// *****************************************************************************
        reportsMenu.setText(bundle.getString("Menu.reports"));
        JMenu stockReportsMenu = new JMenu();
        JMenu salesReportsMenu = new JMenu();
        JMenu customerReportsMenu = new JMenu();
        JMenu maintenanceReportsMenu = new JMenu();
        JMenu employeeReportsMenu = new JMenu();
        JMenu otherReportsMenu = new JMenu();

        stockReportsMenu.setText(bundle.getString("Menu.stockreports"));
        salesReportsMenu.setText(bundle.getString("Menu.salesreports"));
        customerReportsMenu.setText(bundle.getString("Menu.customerreports"));
        maintenanceReportsMenu.setText(bundle.getString("Menu.maintenancereports"));
        employeeReportsMenu.setText(bundle.getString("Menu.presencereports"));
        otherReportsMenu.setText(bundle.getString("Menu.reportsother"));

        reportsMenu.add(customerReportsMenu);
        reportsMenu.add(employeeReportsMenu);
        reportsMenu.add(salesReportsMenu);
        reportsMenu.add(stockReportsMenu);
        reportsMenu.add(maintenanceReportsMenu);
        reportsMenu.add(otherReportsMenu);

        addItem(customerReportsMenu, AppLocal.getIntString("Menu.CustomersReport"), "/uk/chromis/reports/customers.bs");
        addItem(customerReportsMenu, AppLocal.getIntString("Menu.CustomersBReport"), "/uk/chromis/reports/customersb.bs");
        addItem(customerReportsMenu, AppLocal.getIntString("Menu.CustomersDebtors"), "/uk/chromis/reports/customersdebtors.bs");
        addItem(customerReportsMenu, AppLocal.getIntString("Menu.CustomersDiary"), "/uk/chromis/reports/customersdiary.bs");
        addItem(customerReportsMenu, AppLocal.getIntString("Menu.CustomersList"), "/uk/chromis/reports/customers_list.bs");
        addItem(customerReportsMenu, AppLocal.getIntString("Menu.SalesByCustomer"), "/uk/chromis/reports/salebycustomer.bs");

        addItem(salesReportsMenu, AppLocal.getIntString("Menu.Closing"), "/uk/chromis/reports/closedpos.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.Closing1"), "/uk/chromis/reports/closedpos_1.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.CashRegisterLog"), "/uk/chromis/reports/cashregisterlog.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.ExtendedCashRegisterLog"), "/uk/chromis/reports/extendedcashregisterlog.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.CashFlow"), "/uk/chromis/reports/cashflow.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.PaymentReport"), "/uk/chromis/reports/paymentreport.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.CategorySales"), "/uk/chromis/reports/categorysales.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.ClosedProducts"), "/uk/chromis/reports/closedproducts.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.ClosedProducts1"), "/uk/chromis/reports/closedproducts_1.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.ExtendedByProducts"), "/uk/chromis/reports/extproducts.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.SaleTaxes"), "/uk/chromis/reports/saletaxes.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.TaxCatSales"), "/uk/chromis/reports/taxcatsales.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.ReportTaxes"), "/uk/chromis/reports/taxes.bs");
        salesReportsMenu.add(new JPopupMenu.Separator());
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.ProductSales"), "/uk/chromis/reports/productsales.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.SalesChart"), "/uk/chromis/reports/chartsales.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.TimeSeriesProduct"), "/uk/chromis/reports/timeseriesproduct.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.Top10Sales"), "/uk/chromis/reports/top10sales.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.SalesProfit"), "/uk/chromis/reports/productsalesprofit.bs");
        addItem(salesReportsMenu, AppLocal.getIntString("Menu.ProductCategorySalesPieChart"), "/uk/chromis/reports/piesalescat.bs");

        addItem(stockReportsMenu, AppLocal.getIntString("Menu.BarcodeSheet"), "/uk/chromis/reports/barcodesheet.bs");
        addItem(stockReportsMenu, AppLocal.getIntString("Menu.ShelfEdgeLabels"), "/uk/chromis/reports/shelfedgelabels.bs");
        addItem(stockReportsMenu, AppLocal.getIntString("Menu.ProductLabels"), "/uk/chromis/reports/productlabels.bs");
        stockReportsMenu.add(new JPopupMenu.Separator());
        addItem(stockReportsMenu, AppLocal.getIntString("Menu.Inventory"), "/uk/chromis/reports/inventory.bs");
        addItem(stockReportsMenu, AppLocal.getIntString("Menu.Inventory2"), "/uk/chromis/reports/inventoryb.bs");
        addItem(stockReportsMenu, AppLocal.getIntString("Menu.InventoryBroken"), "/uk/chromis/reports/inventorybroken.bs");
        addItem(stockReportsMenu, AppLocal.getIntString("Menu.InventoryDiff"), "/uk/chromis/reports/inventorydiff.bs");
        addItem(stockReportsMenu, AppLocal.getIntString("Menu.InventoryDiffDetail"), "/uk/chromis/reports/inventorydiffdetail.bs");
        addItem(stockReportsMenu, AppLocal.getIntString("Menu.InventoryListDetail"), "/uk/chromis/reports/inventorylistdetail.bs");
        addItem(stockReportsMenu, AppLocal.getIntString("Menu.InventoryReOrder"), "/uk/chromis/reports/inventoryreorder.bs");

        addItem(stockReportsMenu, AppLocal.getIntString("Menu.ProductCatalog"), "/uk/chromis/reports/productscatalog.bs");
        addItem(stockReportsMenu, AppLocal.getIntString("Menu.Products"), "/uk/chromis/reports/products.bs");
        addItem(stockReportsMenu, AppLocal.getIntString("Menu.SaleCatalog"), "/uk/chromis/reports/salecatalog.bs");

        addItem(employeeReportsMenu, AppLocal.getIntString("Menu.DailyPresenceReport"), "/uk/chromis/reports/dailypresencereport.bs");
        addItem(employeeReportsMenu, AppLocal.getIntString("Menu.DailyScheduleReport"), "/uk/chromis/reports/dailyschedulereport.bs");
        addItem(employeeReportsMenu, AppLocal.getIntString("Menu.PerformanceReport"), "/uk/chromis/reports/performancereport.bs");

        addItem(maintenanceReportsMenu, AppLocal.getIntString("Menu.UsersReport"), "/u0k/chromis/reports/people.bs");
        addItem(maintenanceReportsMenu, AppLocal.getIntString("Menu.UserSells"), "/uk/chromis/reports/usersales.bs");

        addItem(otherReportsMenu, AppLocal.getIntString("Menu.UpdatedPrices"), "/uk/chromis/reports/updatedprices.bs");
        addItem(otherReportsMenu, AppLocal.getIntString("Menu.NewProducts"), "/uk/chromis/reports/newproducts.bs");
        addItem(otherReportsMenu, AppLocal.getIntString("Menu.MissingData"), "/uk/chromis/reports/missingdata.bs");
        addItem(otherReportsMenu, AppLocal.getIntString("Menu.InvalidData"), "/uk/chromis/reports/invaliddata.bs");
        addItem(otherReportsMenu, AppLocal.getIntString("Menu.StockChangesReport"), "/uk/chromis/reports/stockchanges.bs");

// *****************************************************************************
        pmMenu.setText(bundle.getString("Menu.presencemanagement"));
        addItem(pmMenu, AppLocal.getIntString("Menu.Breaks"), "uk.chromis.pos.epm.BreaksPanel");
        addItem(pmMenu, AppLocal.getIntString("Menu.Leaves"), "uk.chromis.pos.epm.LeavesPanel");

// *****************************************************************************
        maintenanceMenu.setText(bundle.getString("Menu.maintenance"));
        addItem(maintenanceMenu, AppLocal.getIntString("Menu.Users"), "uk.chromis.pos.admin.PeoplePanel");
        addItem(maintenanceMenu, AppLocal.getIntString("Menu.Roles"), "uk.chromis.pos.admin.RolesPanel");
        maintenanceMenu.add(new JPopupMenu.Separator());
        addItem(maintenanceMenu, AppLocal.getIntString("Menu.Taxes"), "uk.chromis.pos.inventory.TaxPanel");
        addItem(maintenanceMenu, AppLocal.getIntString("Menu.TaxCategories"), "uk.chromis.pos.inventory.TaxCategoriesPanel");
        addItem(maintenanceMenu, AppLocal.getIntString("Menu.TaxCustCategories"), "uk.chromis.pos.inventory.TaxCustCategoriesPanel");
        maintenanceMenu.add(new JPopupMenu.Separator());
        addItem(maintenanceMenu, AppLocal.getIntString("Menu.Resources"), "uk.chromis.pos.admin.ResourcesPanel");
        maintenanceMenu.add(new JPopupMenu.Separator());
        addItem(maintenanceMenu, AppLocal.getIntString("Menu.Locations"), "uk.chromis.pos.inventory.LocationsPanel");
        maintenanceMenu.add(new JPopupMenu.Separator());
        addItem(maintenanceMenu, AppLocal.getIntString("Menu.Floors"), "uk.chromis.pos.mant.JPanelFloors");
        addItem(maintenanceMenu, AppLocal.getIntString("Menu.Tables"), "uk.chromis.pos.mant.JPanelPlaces");

// *****************************************************************************
        toolMenu.setText(bundle.getString("Menu.tools"));

        addItem(toolMenu, AppLocal.getIntString("Menu.CSVImport"), "uk.chromis.pos.imports.JPanelCSVImport");
        addItem(toolMenu, AppLocal.getIntString("Menu.CSVReset"), "uk.chromis.pos.imports.JPanelCSVCleardb");
        // addItem(toolMenu, AppLocal.getIntString("Menu.StockChanges"), "uk.chromis.pos.imports.StockChangesPanel");
        toolMenu.add(new JPopupMenu.Separator());
        addItem(toolMenu, AppLocal.getIntString("Menu.Resetpickup"), "uk.chromis.pos.sales.JPanelResetPickupId");
        toolMenu.add(new JPopupMenu.Separator());
        addItem(toolMenu, AppLocal.getIntString("Menu.Configuration"), "uk.chromis.pos.config.JPanelConfiguration");
        toolMenu.add(new JPopupMenu.Separator());
        addItem(toolMenu, AppLocal.getIntString("Menu.Printer"), "uk.chromis.pos.panels.JPanelPrinter");
        toolMenu.add(new JPopupMenu.Separator());
        addItem(toolMenu, AppLocal.getIntString("Menu.CheckInCheckOut"), "uk.chromis.pos.epm.JPanelEmployeePresence");

// *****************************************************************************

        //helpMenu.setText(bundle.getString("Menu.help"));
        //JMenuItem jMenuItem3 = new JMenuItem();
        //jMenuItem3.setText(AppLocal.getIntString("Menu.about"));
        //helpMenu.add(jMenuItem3, aboutClicked());
        //addItem(helpMenu, AppLocal.getIntString("Button.Factory"), "uk.chromis.pos.sites.SitesPanel");

        
        
// *****************************************************************************
/*      
        JMenuItem jMenuItem5 = new JMenuItem();
        jMenuItem5.setText(AppLocal.getIntString("Menu.stockchanges"));
        toolMenu.add(jMenuItem5);

        JMenuItem jMenuItem7 = new JMenuItem();
        jMenuItem7.setText(AppLocal.getIntString("Menu.resetpermissions"));
        toolMenu.add(jMenuItem7);

        JMenuItem jMenuItem8 = new JMenuItem();
        jMenuItem8.setText(AppLocal.getIntString("Menu.resetresources"));
        toolMenu.add(jMenuItem8);

        JMenuItem jMenuItem9 = new JMenuItem();
        jMenuItem9.setText(AppLocal.getIntString("Menu.backup"));
        toolMenu.add(jMenuItem9);
*/
        menuBar.add(fileMenu);
       // menuBar.add(registerMenu);
        menuBar.add(stockMenu);
        menuBar.add(customerMenu);
        menuBar.add(reportsMenu);
        menuBar.add(pmMenu);
        menuBar.add(maintenanceMenu);
        menuBar.add(toolMenu);
        menuBar.add(helpMenu);

    }

    public void initFrame(AppProperties props) {
        m_OS = new OSValidator();
        m_props = props;
        m_rootapp = new JAdminApp();

        if (m_rootapp.initApp(m_props, this)) {
            if ("true".equals(AppConfig.getInstance().getProperty("machine.uniqueinstance"))) {
                try {
                    m_instmanager = new InstanceManager(this);
                } catch (RemoteException | AlreadyBoundException e) {
                }
            }
            add(m_rootapp);
            try {
                this.setIconImage(ImageIO.read(JAdminFrame.class
                        .getResourceAsStream("/uk/chromis/fixedimages/smllogo.png")));
            } catch (IOException e) {
            }
            setTitle(AppLocal.APP_NAME + " - V" + AppLocal.APP_VERSION + AppLocal.APP_DEMO);
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        } else {
            /* this is now redundant should never get to this stage new dbmanager class handles checking
            new JFrmConfig(props).setVisible(true); // Show the configuration window.
            */
        }
    }

    @Override
    public void restoreWindow() throws RemoteException {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (getExtendedState() == JFrame.ICONIFIED) {
                    setExtendedState(JFrame.NORMAL);
                }
                requestFocus();
            }
        });
    }

    
    private int aboutClicked() {                                       

        JFrame sampleFrame = new JFrame();
        final Action exit = new AbstractAction("Exit") {
            @Override
            public final void actionPerformed(final ActionEvent e) {
                sampleFrame.setVisible(false);
                sampleFrame.dispose();
            }
        };

        String currentPath = null;

        if (OSValidator.isMac()) {
            try {
                currentPath = new File(JRootApp.class
                        .getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).toString();
            } catch (URISyntaxException ex) {
            }
        } else {
            currentPath = System.getProperty("user.dir") + "\\chromispos.jar";
        }

        String md5 = null;
        try {
            FileInputStream fis = new FileInputStream(new File(currentPath));
            md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
            fis.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(JRootApp.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(JRootApp.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        m_rootapp.tryToClose();
    }//GEN-LAST:event_formWindowClosing

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        System.exit(0);
    }//GEN-LAST:event_formWindowClosed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
