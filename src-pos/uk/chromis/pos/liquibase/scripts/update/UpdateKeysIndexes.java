//    Chromis POS  - The New Face of Open Source POS
//    Copyright (c) 2016 - John Lewis
//    http://www.chromis.co.uk
//
//    This file is part of Chromis POS
//
//     Chromis POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Chromis POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>.
//
package uk.chromis.pos.liquibase.scripts.update;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import uk.chromis.data.loader.Session;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.DriverWrapper;
import uk.chromis.pos.util.AltEncrypter;

/**
 * @author John Lewis
 */
public class UpdateKeysIndexes implements liquibase.change.custom.CustomTaskChange {

    private DatabaseMetaData md;
    private String sdbmanager;
    private List<String> fkList;
    private List<String> indexList;
    private List<String> pkList;

    @Override
    public void execute(Database dtbs) throws CustomChangeException {

        fkList = new ArrayList<>();
        indexList = new ArrayList<>();
        pkList = new ArrayList<>();

        String db_user = (AppConfig.getInstance().getProperty("db.user"));
        String db_url = (AppConfig.getInstance().getProperty("db.URL"));
        String db_password = (AppConfig.getInstance().getProperty("db.password"));

        if (db_user != null && db_password != null && db_password.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
            db_password = cypher.decrypt(db_password.substring(6));
        }

        ClassLoader cloader;
        Connection conn = null;
        PreparedStatement pstmt;

        try {
            cloader = new URLClassLoader(new URL[]{new File(AppConfig.getInstance().getProperty("db.driverlib")).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getInstance().getProperty("db.driver"), true, cloader).newInstance()));
            Session session = new Session(db_url, db_user, db_password);
            conn = session.getConnection();

            md = conn.getMetaData();

            ResultSet rsTables = null;
            Statement stmtTables;
            ResultSet rs = null;
            String tableName;
            fkList.clear();
            pkList.clear();

            sdbmanager = conn.getMetaData().getDatabaseProductName();
            switch (sdbmanager) {
                case "MySQL":
                    rsTables = md.getTables(null, null, "%", null);
                    break;
                case "PostgreSQL":
                    stmtTables = conn.createStatement();
                    rsTables = stmtTables.executeQuery("SELECT * FROM information_schema.tables WHERE table_type = 'BASE TABLE' AND table_schema = 'public' ORDER BY table_type, table_name");
                    break;
                default:
                    stmtTables = conn.createStatement();
                    rsTables = stmtTables.executeQuery("SELECT * FROM sys.systables WHERE tabletype='T' ORDER BY tablename");
            }
            while (rsTables.next()) {
                switch (sdbmanager) {
                    case "MySQL":
                        tableName = rsTables.getString(3).toUpperCase();
                        break;
                    case "PostgreSQL":
                        tableName = rsTables.getString("table_name").toUpperCase();
                        break;
                    default:
                        tableName = rsTables.getString("tablename").toUpperCase();
                }

                if (sdbmanager.equals("PostgreSQL")) {
                    rs = md.getImportedKeys(conn.getCatalog(), null, tableName.toLowerCase());
                } else {
                    rs = md.getImportedKeys(conn.getCatalog(), null, tableName);
                }

                while (rs.next()) {
                    switch (sdbmanager) {
                        case "MySQL":
                            fkList.add("ALTER TABLE " + tableName + " DROP FOREIGN KEY " + rs.getString("FK_NAME"));
                            break;
                        case "PostgreSQL":
                            fkList.add("ALTER TABLE " + tableName + " DROP CONSTRAINT " + rs.getString("FK_NAME"));
                            break;
                        default:
                            fkList.add("ALTER TABLE " + tableName + " DROP CONSTRAINT " + rs.getString("FK_NAME"));
                    }
                }
                if (sdbmanager.equals("PostgreSQL")) {
                    rs = md.getPrimaryKeys(conn.getCatalog(), null, tableName.toLowerCase());;
                } else {
                    rs = md.getPrimaryKeys(conn.getCatalog(), null, tableName);
                }

                while (rs.next()) {
                    switch (sdbmanager) {
                        case "PostgreSQL":
                            pkList.add("ALTER TABLE " + tableName + " DROP CONSTRAINT " + rs.getString("PK_NAME"));
                            break;
                        default:
                            pkList.add("ALTER TABLE " + tableName + " DROP PRIMARY KEY ");
                            break;
                    }
                }
            }
        } catch (MalformedURLException | SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(UpdateKeysIndexes.class.getName()).log(Level.SEVERE, null, ex);
        }

        //drop all foreign keys
        Iterator<String> fk = fkList.iterator();
        String fkName;
        while (fk.hasNext()) {
            fkName = fk.next();
            try {
                pstmt = conn.prepareStatement(fkName);
                pstmt.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("FK " + fkName + " not found!");
            }
        }

        // remove all the indexes
        createIndexList();
        Iterator<String> index = indexList.iterator();
        String indexName;
        while (index.hasNext()) {
            switch (sdbmanager) {
                case "MySQL":
                    indexName = index.next();
                    break;
                default:
                    String tmp = index.next();
                    indexName = tmp.substring(0, tmp.indexOf(" ON "));
            }

            try {
                pstmt = conn.prepareStatement(indexName);
                pstmt.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("Index " + indexName + " not found!");
            }
        }
         
        // now drop the primary keys
        Iterator<String> pk = pkList.iterator();
        String pkName;
        while (pk.hasNext()) {
            pkName = pk.next();
            try {
                pstmt = conn.prepareStatement(pkName);
                pstmt.executeUpdate();
            } catch (SQLException ex) {
                System.out.println("PK " + pkName + " not found!");
            }
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

    private void createIndexList() {
        indexList.clear();

        indexList.add("DROP INDEX CLOSEDCASH_INX_SEQ ON CLOSEDCASH");
        indexList.add("DROP INDEX THIRDPARTIES_CIF_INX ON THIRDPARTIES");
        indexList.add("DROP INDEX THIRDPARTIES_NAME_INX ON THIRDPARTIES");
        indexList.add("DROP INDEX PRODUCTS_INX_0 ON PRODUCTS");
        indexList.add("DROP INDEX PRODUCTS_INX_1 ON PRODUCTS");
        indexList.add("DROP INDEX PKIT_INX_PROD ON PRODUCTS_KIT");
        indexList.add("DROP INDEX RESOURCES_NAME_INX ON RESOURCES");
        indexList.add("DROP INDEX ROLES_NAME_INX ON ROLES");
        indexList.add("DROP INDEX STOCKCURRENT_INX ON STOCKCURRENT");
        indexList.add("DROP INDEX TAXCAT_NAME_INX ON TAXCATEGORIES");
        indexList.add("DROP INDEX TAXCUSTCAT_NAME_INX ON TAXCUSTCATEGORIES");
        indexList.add("DROP INDEX TAXES_NAME_INX ON TAXES");
        indexList.add("DROP INDEX PLACES_NAME_INX ON PLACES");
        indexList.add("DROP INDEX PEOPLE_NAME_INX ON PEOPLE");
        indexList.add("DROP INDEX LOCATIONS_NAME_INX ON LOCATIONS");
        indexList.add("DROP INDEX FLOORS_NAME_INX ON FLOORS");
        indexList.add("DROP INDEX PCOM_INX_PROD ON PRODUCTS_COM");
        indexList.add("DROP INDEX ATTUSE_LINE ON ATTRIBUTEUSE");
        indexList.add("DROP INDEX CUSTOMERS_SKEY_INX ON CUSTOMERS");
        indexList.add("DROP INDEX CATEGORIES_NAME_INX ON CATEGORIES");
        indexList.add("DROP INDEX RESERVATIONS_INX_1 ON RESERVATIONS");
        indexList.add("DROP INDEX ATTUSE_ATT ON ATTRIBUTEUSE");
        indexList.add("DROP INDEX DBPERMISSIONS_CLASSNAME_INX ON DBPERMISSIONS");
        indexList.add("DROP INDEX ATTSETINST_SET ON ATTRIBUTESETINSTANCE");
        indexList.add("DROP INDEX ATTVAL_ATT ON ATTRIBUTEVALUE");
        indexList.add("DROP INDEX CLOSEDCASH_INX_1 ON CLOSEDCASH");
        indexList.add("DROP INDEX PLACES_FK_1 ON PLACES");
        indexList.add("DROP INDEX RECEIPTS_FK_MONEY ON RECEIPTS");
        indexList.add("DROP INDEX RECEIPTS_INX_1 ON RECEIPTS");
        indexList.add("DROP INDEX ATTINST_ATT ON ATTRIBUTEINSTANCE");
        indexList.add("DROP INDEX ATTINST_SET ON ATTRIBUTEINSTANCE");
        indexList.add("DROP INDEX CUSTOMERS_CARD_INX ON CUSTOMERS");
        indexList.add("DROP INDEX CUSTOMERS_NAME_INX ON CUSTOMERS");
        indexList.add("DROP INDEX CUSTOMERS_TAXCAT ON CUSTOMERS");
        indexList.add("DROP INDEX CUSTOMERS_TAXID_INX ON CUSTOMERS");
        indexList.add("DROP INDEX PAYMENTS_FK_RECEIPT ON PAYMENTS");
        indexList.add("DROP INDEX PAYMENTS_INX_1 ON PAYMENTS");
        indexList.add("DROP INDEX PEOPLE_CARD_INX ON PEOPLE");
        indexList.add("DROP INDEX PEOPLE_FK_1 ON PEOPLE");
        indexList.add("DROP INDEX RES_CUST_FK_2 ON RESERVATION_CUSTOMERS");
        indexList.add("DROP INDEX SHIFT_BREAKS_BREAKID ON SHIFT_BREAKS");
        indexList.add("DROP INDEX SHIFT_BREAKS_SHIFTID ON SHIFT_BREAKS");
        indexList.add("DROP INDEX TICKETS_CUSTOMERS_FK ON TICKETS");
        indexList.add("DROP INDEX TICKETS_FK_2 ON TICKETS");
        indexList.add("DROP INDEX TICKETS_TICKETID ON TICKETS");
        indexList.add("DROP INDEX LEAVES_PPLID ON LEAVES");
        indexList.add("DROP INDEX CATEGORIES_FK_1 ON CATEGORIES");
        indexList.add("DROP INDEX FK_PRODUCT_PROMOTIONID ON PRODUCTS");
        indexList.add("DROP INDEX PRODUCTS_ATTRSET_FK ON PRODUCTS");
        indexList.add("DROP INDEX PRODUCTS_FK_1 ON PRODUCTS");
        indexList.add("DROP INDEX PRODUCTS_PACKPRODUCT_FK ON PRODUCTS");
        indexList.add("DROP INDEX PRODUCTS_TAXCAT_FK ON PRODUCTS");
        indexList.add("DROP INDEX PRODUCTS_COM_FK_2 ON PRODUCTS_COM");
        indexList.add("DROP INDEX PRODUCTS_KIT_FK_2 ON PRODUCTS_KIT");
        indexList.add("DROP INDEX STOCKCURRENT_ATTSETINST ON STOCKCURRENT");
        indexList.add("DROP INDEX STOCKCURRENT_FK_1 ON STOCKCURRENT");
        indexList.add("DROP INDEX STOCKDIARY_ATTSETINST ON STOCKDIARY");
        indexList.add("DROP INDEX STOCKDIARY_FK_1 ON STOCKDIARY");
        indexList.add("DROP INDEX STOCKDIARY_FK_2 ON STOCKDIARY");
        indexList.add("DROP INDEX STOCKDIARY_INX_1 ON STOCKDIARY");
        indexList.add("DROP INDEX STOCKLEVEL_LOCATION ON STOCKLEVEL");
        indexList.add("DROP INDEX STOCKLEVEL_PRODUCT ON STOCKLEVEL");
        indexList.add("DROP INDEX TAXES_CAT_FK ON TAXES");
        indexList.add("DROP INDEX TAXES_CUSTCAT_FK ON TAXES");
        indexList.add("DROP INDEX TAXES_TAXES_FK ON TAXES");
        indexList.add("DROP INDEX TAXLINES_RECEIPT ON TAXLINES");
        indexList.add("DROP INDEX TAXLINES_TAX ON TAXLINES");
        indexList.add("DROP INDEX TICKETLINES_FK_2 ON TICKETLINES");
        indexList.add("DROP INDEX TICKETLINES_FK_3 ON TICKETLINES");
        indexList.add("DROP INDEX PRODUCTS_NAME_INX ON PRODUCTS");
        indexList.add("DROP INDEX TICKETLINES_ATTSETINST ON TICKETLINES");        
        indexList.add("DROP INDEX PRODUCTS_COM_FK_1 ON PRODUCTS_COM");
        indexList.add("DROP INDEX PRODUCTS_KIT_FK_1 ON PRODUCTS_KIT");
        indexList.add("DROP INDEX STOCKCURRENT_FK_2 ON STOCKCURRENT");
        

    }
}
