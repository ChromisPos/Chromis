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
package uk.chromis.pos.liquibase.scripts.update;

import uk.chromis.pos.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.ConnectionFactory;
import uk.chromis.pos.forms.DataLogicSystem;

public class CreatePermissions implements liquibase.change.custom.CustomTaskChange {

    private ArrayList<String> permParams;
    private HashSet<String> startPerms;

    @Override
    public void execute(Database dtbs) throws CustomChangeException {
        Connection conn = null;
        PreparedStatement pstmt;

        conn = ConnectionFactory.getInstance().getConnection();

        DataLogicSystem dlSystem = new DataLogicSystem();
        dlSystem.init(SessionFactory.getInstance().getSession());

        startPerms = new HashSet<>();
        //Guest permissions
        startPerms.add("uk.chromis.pos.forms.JPanelMenu");
        startPerms.add("Menu.Exit");
        startPerms.add("uk.chromis.pos.sales.JPanelTicketSales");

        String permissionsID = UUID.randomUUID().toString();
        String personID = UUID.randomUUID().toString();

        try {
            ArrayList<String> permParams = new ArrayList<>(startPerms);
            dlSystem.insertNewPermissions(permissionsID, "Guest Role2", permParams, 1);
            dlSystem.insertNewPerson(personID, "Guest2", permissionsID);
        } catch (BasicException ex) {
            Logger.getLogger(CreatePermissions.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Employee permissions
        startPerms.add("/uk/chromis/reports/barcodesheet.bs");
        startPerms.add("/uk/chromis/reports/customers.bs");
        startPerms.add("/uk/chromis/reports/customersb.bs");
        startPerms.add("/uk/chromis/reports/customersdiary.bs");
        startPerms.add("/uk/chromis/reports/productlabels.bs");
        startPerms.add("/uk/chromis/reports/products.bs");
        startPerms.add("/uk/chromis/reports/productsales.bs");
        startPerms.add("/uk/chromis/reports/productscatalog.bs");
        startPerms.add("/uk/chromis/reports/salecatalog.bs");
        startPerms.add("/uk/chromis/reports/shelfedgelabels.bs");
        startPerms.add("button.linediscount");
        startPerms.add("button.opendrawer");
        startPerms.add("button.print");
        startPerms.add("button.sendorder");
        startPerms.add("button.totaldiscount");
        startPerms.add("payment.bank");
        startPerms.add("payment.cash");
        startPerms.add("payment.cheque");
        startPerms.add("payment.debt");
        startPerms.add("payment.magcard");
        startPerms.add("payment.paper");
        startPerms.add("sales.ChangeTaxOptions");
        startPerms.add("sales.EditLines");
        startPerms.add("sales.EditTicket");
        startPerms.add("sales.PrintKitchen");
        startPerms.add("sales.PrintTicket");
        startPerms.add("sales.Total");
        startPerms.add("uk.chromis.pos.customers.CustomersPanel");
        startPerms.add("uk.chromis.pos.forms.MenuCustomers");
        startPerms.add("uk.chromis.pos.forms.MenuSalesManagement");
        startPerms.add("uk.chromis.pos.forms.MenuStockManagement");
        startPerms.add("uk.chromis.pos.inventory.AuxiliarPanel");
        startPerms.add("uk.chromis.pos.inventory.CategoriesPanel");
        startPerms.add("uk.chromis.pos.inventory.ProductsPanel");

        permissionsID = UUID.randomUUID().toString();
        personID = UUID.randomUUID().toString();

        try {
            ArrayList<String> permParams = new ArrayList<>(startPerms);
            dlSystem.insertNewPermissions(permissionsID, "Employee Role2", permParams, 3);
            dlSystem.insertNewPerson(personID, "Employee2", permissionsID);
        } catch (BasicException ex) {
            Logger.getLogger(CreatePermissions.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Manager
        startPerms.add("/uk/chromis/reports/badprice.bs");
        startPerms.add("/uk/chromis/reports/cashflow.bs");
        startPerms.add("/uk/chromis/reports/cashregisterlog.bs");
        startPerms.add("/uk/chromis/reports/categorysales.bs");
        startPerms.add("/uk/chromis/reports/chartsales.bs");
        startPerms.add("/uk/chromis/reports/closedpos.bs");
        startPerms.add("/uk/chromis/reports/closedpos_1.bs");
        startPerms.add("/uk/chromis/reports/closedproducts.bs");
        startPerms.add("/uk/chromis/reports/closedproducts_1.bs");
        startPerms.add("/uk/chromis/reports/customers_list.bs");
        startPerms.add("/uk/chromis/reports/customersdebtors.bs");
        startPerms.add("/uk/chromis/reports/extendedcashregisterlog.bs");
        startPerms.add("/uk/chromis/reports/extproducts.bs");
        startPerms.add("/uk/chromis/reports/invalidcategory.bs");
        startPerms.add("/uk/chromis/reports/invaliddata.bs");
        startPerms.add("/uk/chromis/reports/inventory.bs");
        startPerms.add("/uk/chromis/reports/inventoryb.bs");
        startPerms.add("/uk/chromis/reports/inventorybroken.bs");
        startPerms.add("/uk/chromis/reports/inventorydiff.bs");
        startPerms.add("/uk/chromis/reports/inventorydiffdetail.bs");
        startPerms.add("/uk/chromis/reports/inventorylistdetail.bs");
        startPerms.add("/uk/chromis/reports/inventoryreorder.bs");
        startPerms.add("/uk/chromis/reports/missingdata.bs");
        startPerms.add("/uk/chromis/reports/newproducts.bs");
        startPerms.add("/uk/chromis/reports/paymentreport.bs");
        startPerms.add("/uk/chromis/reports/people.bs");
        startPerms.add("/uk/chromis/reports/piesalescat.bs");
        startPerms.add("/uk/chromis/reports/productsalesprofit.bs");
        startPerms.add("/uk/chromis/reports/promotions.bs");
        startPerms.add("/uk/chromis/reports/salebycustomer.bs");
        startPerms.add("/uk/chromis/reports/saletaxes.bs");
        startPerms.add("/uk/chromis/reports/stockchanges.bs");
        startPerms.add("/uk/chromis/reports/taxcatsales.bs");
        startPerms.add("/uk/chromis/reports/taxes.bs");
        startPerms.add("/uk/chromis/reports/timeseriesproduct.bs");
        startPerms.add("/uk/chromis/reports/top10sales.bs");
        startPerms.add("/uk/chromis/reports/updatedprices.bs");
        startPerms.add("/uk/chromis/reports/usersales.bs");
        startPerms.add("Menu.ChangePassword");
        startPerms.add("button.refundit");
        startPerms.add("button.scharge");
        startPerms.add("payment.free");
        startPerms.add("refund.cash");
        startPerms.add("refund.cheque");
        startPerms.add("refund.magcard");
        startPerms.add("refund.paper");
        startPerms.add("sales.RefundTicket");
        startPerms.add("uk.chromis.pos.admin.PeoplePanel");
        startPerms.add("uk.chromis.pos.admin.ResourcesPanel");
        startPerms.add("uk.chromis.pos.admin.RolesPanel");
        startPerms.add("uk.chromis.pos.config.JPanelConfiguration");
        startPerms.add("uk.chromis.pos.customers.CustomersPayment");
        startPerms.add("uk.chromis.pos.epm.BreaksPanel");
        startPerms.add("uk.chromis.pos.epm.JPanelEmployeePresence");
        startPerms.add("uk.chromis.pos.epm.LeavesPanel");
        startPerms.add("uk.chromis.pos.forms.MenuEmployees");
        startPerms.add("uk.chromis.pos.forms.MenuMaintenance");
        startPerms.add("uk.chromis.pos.imports.JPanelCSV");
        startPerms.add("uk.chromis.pos.imports.JPanelCSVCleardb");
        startPerms.add("uk.chromis.pos.imports.JPanelCSVImport");
        startPerms.add("uk.chromis.pos.imports.StockChangesPanel");
        startPerms.add("uk.chromis.pos.inventory.AttributeSetsPanel");
        startPerms.add("uk.chromis.pos.inventory.AttributeUsePanel");
        startPerms.add("uk.chromis.pos.inventory.AttributeValuesPanel");
        startPerms.add("uk.chromis.pos.inventory.AttributesPanel");
        startPerms.add("uk.chromis.pos.inventory.LocationsPanel");
        startPerms.add("uk.chromis.pos.inventory.ProductListsPanel");
        startPerms.add("uk.chromis.pos.inventory.ProductPacksPanel");
        startPerms.add("uk.chromis.pos.inventory.ProductsWarehousePanel");
        startPerms.add("uk.chromis.pos.inventory.StockDiaryPanel");
        startPerms.add("uk.chromis.pos.inventory.StockManagement");
        startPerms.add("uk.chromis.pos.inventory.TaxCategoriesPanel");
        startPerms.add("uk.chromis.pos.inventory.TaxCustCategoriesPanel");
        startPerms.add("uk.chromis.pos.inventory.TaxPanel");
        startPerms.add("uk.chromis.pos.mant.JPanelFloors");
        startPerms.add("uk.chromis.pos.mant.JPanelPlaces");
        startPerms.add("uk.chromis.pos.panels.JPanelCloseMoney");
        startPerms.add("uk.chromis.pos.panels.JPanelPayments");
        startPerms.add("uk.chromis.pos.panels.JPanelPrinter");
        startPerms.add("uk.chromis.pos.promotion.PromotionPanel");
        startPerms.add("uk.chromis.pos.sales.JPanelTicketEdits");

        permissionsID = UUID.randomUUID().toString();
        personID = UUID.randomUUID().toString();

        try {
            ArrayList<String> permParams = new ArrayList<>(startPerms);
            dlSystem.insertNewPermissions(permissionsID, "Manager Role2", permParams, 5);
            dlSystem.insertNewPerson(personID, "Manager2", permissionsID);
        } catch (BasicException ex) {
            Logger.getLogger(CreatePermissions.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Administrator
        startPerms.add("/uk/chromis/reports/bestsellers.bs");
        startPerms.add("/uk/chromis/reports/dailypresencereport.bs");
        startPerms.add("/uk/chromis/reports/dailyschedulereport.bs");
        startPerms.add("/uk/chromis/reports/performancereport.bs");
        startPerms.add("button.refundeditsale");
        startPerms.add("db.updatedatabase");
        startPerms.add("kitchen.DeleteLine");
        startPerms.add("payment.custom");
        startPerms.add("refund.custom");
        startPerms.add("sales.EditLines");
        startPerms.add("sales.EditTicket");
        startPerms.add("uk.chromis.pos.admin.RolesPanelNew");
        startPerms.add("uk.chromis.pos.forms.MenuSuppliers");
        startPerms.add("uk.chromis.pos.inventory.RecipePanel");
        startPerms.add("uk.chromis.pos.sales.JPanelResetPickupId");
        startPerms.add("uk.chromis.pos.sales.JPanelTicketSales");
        startPerms.add("uk.chromis.pos.sales.JPanelUnlockTables");
        startPerms.add("uk.chromis.pos.suppliers.SuppliersPanel");
        startPerms.add("/uk/chromis/reports/employeetimesheet.bs");
        startPerms.add("/uk/chromis/reports/closedproducts_ean.bs");
        startPerms.add("/uk/chromis/reports/hourlysales.bs");
        startPerms.add("/uk/chromis/reports/itemsremoved.bs");
        startPerms.add("/uk/chromis/reports/supplierproducts.bs");
        startPerms.add("/uk/chromis/reports/suppliers.bs");
        startPerms.add("/uk/chromis/reports/stockvalue.bs");
        startPerms.add("/uk/chromis/reports/commissionreport.bs");
        startPerms.add("/uk/chromis/reports/inventoryb_ean.bs");       
        startPerms.add("/uk/chromis/reports/commissionreport.bs");
        startPerms.add("/uk/chromis/reports/inventoryb_ean.bs");
        startPerms.add("/uk/chromis/reports/employeetimesheet.bs");
        startPerms.add("/uk/chromis/reports/currentproductsales.bs");
        
        
        permissionsID = UUID.randomUUID().toString();
        personID = UUID.randomUUID().toString();

        try {
            ArrayList<String> permParams = new ArrayList<>(startPerms);
            dlSystem.insertNewPermissions(permissionsID, "Administrator Role2", permParams, 9);
            dlSystem.insertNewPerson(personID, "Administrator2", permissionsID);
        } catch (BasicException ex) {
            Logger.getLogger(CreatePermissions.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public void setUp() throws SetupException {

    }

    @Override
    public void setFileOpener(ResourceAccessor ra) {

    }

    @Override
    public ValidationErrors validate(Database dtbs) {
        return null;
    }

    private class ConfigurationHandler extends DefaultHandler {

        @Override
        public void startDocument() throws SAXException {
        }

        @Override
        public void endDocument() throws SAXException {
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
        }
    }
}
