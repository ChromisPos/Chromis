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

package uk.chromis.pos.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
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

/**
 * @author John Lewis
 */
public class SiteGUID implements liquibase.change.custom.CustomTaskChange {

        
    @Override
    public void execute(Database dtbs) throws CustomChangeException {
        String db_user = (AppConfig.getInstance().getProperty("db.user"));
        String db_url = (AppConfig.getInstance().getProperty("db.URL"));
        String db_password = (AppConfig.getInstance().getProperty("db.password"));

        if (db_user != null && db_password != null && db_password.startsWith("crypt:")) {
            // the password is encrypted
            AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
            db_password = cypher.decrypt(db_password.substring(6));
        }

        ClassLoader cloader;
        Connection conn = null;
        PreparedStatement pstmt;
        String guid = UUID.randomUUID().toString();

        try {
            cloader = new URLClassLoader(new URL[]{new File(AppConfig.getInstance().getProperty("db.driverlib")).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getInstance().getProperty("db.driver"), true, cloader).newInstance()));
            Session session = new Session(db_url, db_user, db_password);
            conn = session.getConnection();

        } catch (MalformedURLException | SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(SiteGUID.class.getName()).log(Level.SEVERE, null, ex);            
        }

        try {
            pstmt = conn.prepareStatement("INSERT INTO SITEGUID (GUID) VALUES (?)");
            pstmt.setString(1, guid);
            pstmt.executeUpdate();

            String SQL = "ALTER TABLE APPLICATIONS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE ATTRIBUTE ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE ATTRIBUTEINSTANCE ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE ATTRIBUTESET ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE ATTRIBUTESETINSTANCE ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE ATTRIBUTEUSE ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE ATTRIBUTEVALUE ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE BREAKS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE CATEGORIES ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE CLOSEDCASH ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE CUSTOMERS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE DBPERMISSIONS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE DRAWEROPENED ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE FLOORS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE LEAVES ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE LINEREMOVED ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE LOCATIONS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE MOORERS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE PAYMENTS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE PEOPLE ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE PLACES ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE PRODUCTS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE PRODUCTS_COM ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE PRODUCTS_KIT ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE PROMOTIONS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE RECEIPTS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE RESERVATION_CUSTOMERS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE RESERVATIONS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE RESOURCES ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE ROLES ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE SHIFT_BREAKS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE SHIFTS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE STOCKCHANGES ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE STOCKCURRENT ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE STOCKDIARY ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE STOCKLEVEL ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE TAXCATEGORIES ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE TAXCUSTCATEGORIES ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE TAXES ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE TAXLINES ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE THIRDPARTIES ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE TICKETLINES ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE TICKETS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();

            SQL = "ALTER TABLE VOUCHERS ADD COLUMN SITEGUID VARCHAR(50) NOT NULL DEFAULT '" + guid + "'";
            pstmt = conn.prepareStatement(SQL);
            pstmt.executeUpdate();
             
            conn.close();

        } catch (SQLException ex) {
            Logger.getLogger(SiteGUID.class.getName()).log(Level.SEVERE, null, ex);
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

}
