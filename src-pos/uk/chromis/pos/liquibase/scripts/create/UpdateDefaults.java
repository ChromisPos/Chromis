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
package uk.chromis.pos.liquibase.scripts.create;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.forms.DriverWrapper;
import uk.chromis.pos.util.AltEncrypter;
import uk.chromis.pos.util.SessionFactory;

/**
 * @author John Lewis
 */
public class UpdateDefaults implements liquibase.change.custom.CustomTaskChange {

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

        try {
            cloader = new URLClassLoader(new URL[]{new File(AppConfig.getInstance().getProperty("db.driverlib")).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getInstance().getProperty("db.driver"), true, cloader).newInstance()));
            Session session = new Session(db_url, db_user, db_password);
            conn = session.getConnection();

        } catch (MalformedURLException | SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(UpdateDefaults.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Get the database version
        DataLogicSystem m_dlSystem = new DataLogicSystem();
        m_dlSystem.init(SessionFactory.getInstance().getSession());
        String sdbmanager = m_dlSystem.getDBVersion();

        String newID;
        try {
            //Check the siteguid table
            pstmt = conn.prepareStatement("DELETE FROM SITEGUID");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("SELECT * FROM APPLICATIONS");
            ResultSet rs = pstmt.executeQuery();

            String siteGuid = "";
            while (rs.next()) {
                siteGuid = rs.getString("SITEGUID");
            }

            pstmt = conn.prepareStatement("INSERT INTO SITEGUID (GUID) VALUES (?)");
            pstmt.setString(1, siteGuid);
            pstmt.executeUpdate();

            /*
            if ("PostgreSQL".equals(sdbmanager)) {
                dropFKPostgresql(conn);
            } else if ("Derby".equals(sdbmanager)) {
                dropDerbyFK(conn);
            } else {
                dropFK(conn);
            }
            */
            
            
            // Update people table
            for (int j = 0; j < 4; j++) {
                newID = UUID.randomUUID().toString();
                pstmt = conn.prepareStatement("UPDATE PEOPLE SET ID = ? WHERE ID = ?");
                pstmt.setString(1, newID);
                pstmt.setString(2, String.valueOf(j));
                pstmt.executeUpdate();

                pstmt = conn.prepareStatement("UPDATE TICKETS SET PERSON = ? WHERE PERSON = ?");
                pstmt.setString(1, newID);
                pstmt.setString(2, String.valueOf(j));
                pstmt.executeUpdate();

            }

            //Update roles table          
            for (int j = 0; j < 4; j++) {
                newID = UUID.randomUUID().toString();
                //pstmt = conn.prepareStatement("UPDATE PEOPLE SET ROLEALIAS = ? WHERE ROLE = ? ");
                //pstmt.setString(1, String.valueOf(j));
                //pstmt.setString(2, String.valueOf(j));
                //pstmt.executeUpdate();

                pstmt = conn.prepareStatement("UPDATE PEOPLE SET ROLE = ? WHERE ROLE = ? ");
                pstmt.setString(1, newID);
                pstmt.setString(2, String.valueOf(j));
                pstmt.executeUpdate();

                pstmt = conn.prepareStatement("UPDATE ROLES SET ID = ? WHERE ID = ? ");
                pstmt.setString(1, newID);
                pstmt.setString(2, String.valueOf(j));
                pstmt.executeUpdate();

                pstmt = conn.prepareStatement("UPDATE TICKETS SET PERSON = ? WHERE PERSON = ? ");
                pstmt.setString(1, newID);
                pstmt.setString(2, String.valueOf(j));
                pstmt.executeUpdate();
            }

            // update categories table
            newID = UUID.randomUUID().toString();
            pstmt = conn.prepareStatement("UPDATE PRODUCTS SET CATEGORY = ? WHERE CATEGORY = ? ");
            pstmt.setString(1, newID);
            pstmt.setString(2, "000");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("UPDATE CATEGORIES SET ID = ? WHERE ID = ? ");
            pstmt.setString(1, newID);
            pstmt.setString(2, "000");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("UPDATE CATEGORIES SET PARENTID = ? WHERE PARENTID = ? ");
            pstmt.setString(1, newID);
            pstmt.setString(2, "000");
            pstmt.executeUpdate();

            newID = UUID.randomUUID().toString();
            pstmt = conn.prepareStatement("UPDATE PRODUCTS SET CATEGORY = ? WHERE CATEGORY = ? ");
            pstmt.setString(1, newID);
            pstmt.setString(2, "xxx999");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("UPDATE CATEGORIES SET PARENTID = ? WHERE PARENTID = ? ");
            pstmt.setString(1, newID);
            pstmt.setString(2, "xxx999");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("UPDATE CATEGORIES SET ID = ? WHERE ID = ? ");
            pstmt.setString(1, newID);
            pstmt.setString(2, "xxx999");
            pstmt.executeUpdate();

            // Update the taxes table
            for (int j = 0; j < 2; j++) {
                String tax;
                tax = (j == 0) ? "000" : "001";
                //update taxes table
                newID = UUID.randomUUID().toString();
                pstmt = conn.prepareStatement("UPDATE TAXES SET ID = ? WHERE ID = ? ");
                pstmt.setString(1, newID);
                pstmt.setString(2, tax);
                pstmt.executeUpdate();

                pstmt = conn.prepareStatement("UPDATE TAXES SET PARENTID = ? WHERE PARENTID = ? ");
                pstmt.setString(1, newID);
                pstmt.setString(2, tax);
                pstmt.executeUpdate();

                pstmt = conn.prepareStatement("UPDATE TICKETLINES SET TAXID = ? WHERE TAXID = ? ");
                pstmt.setString(1, newID);
                pstmt.setString(2, tax);
                pstmt.executeUpdate();

                pstmt = conn.prepareStatement("UPDATE TAXLINES SET TAXID = ? WHERE TAXID = ? ");
                pstmt.setString(1, newID);
                pstmt.setString(2, tax);
                pstmt.executeUpdate();

            }

            //Update the taxcategories table
            for (int j = 0; j < 2; j++) {
                String tax;
                tax = (j == 0) ? "000" : "001";
                //update taxcategories table
                newID = UUID.randomUUID().toString();
                pstmt = conn.prepareStatement("UPDATE TAXCATEGORIES SET ID = ? WHERE ID = ? ");
                pstmt.setString(1, newID);
                pstmt.setString(2, tax);
                pstmt.executeUpdate();

                pstmt = conn.prepareStatement("UPDATE TAXES SET CATEGORY = ? WHERE CATEGORY = ? ");
                pstmt.setString(1, newID);
                pstmt.setString(2, tax);
                pstmt.executeUpdate();

                pstmt = conn.prepareStatement("UPDATE PRODUCTS SET TAXCAT = ? WHERE TAXCAT = ? ");
                pstmt.setString(1, newID);
                pstmt.setString(2, tax);
                pstmt.executeUpdate();
            }

            // Update breaks
            for (int j = 0; j < 3; j++) {
                newID = UUID.randomUUID().toString();
                pstmt = conn.prepareStatement("UPDATE BREAKS SET ID = ? WHERE ID = ? ");
                pstmt.setString(1, newID);
                pstmt.setString(2, String.valueOf(j));
                pstmt.executeUpdate();

                pstmt = conn.prepareStatement("UPDATE SHIFT_BREAKS SET BREAKID = ? WHERE BREAKID = ? ");
                pstmt.setString(1, newID);
                pstmt.setString(2, String.valueOf(j));
                pstmt.executeUpdate();
            }

            newID = UUID.randomUUID().toString();
            pstmt = conn.prepareStatement("UPDATE FLOORS SET ID = ? WHERE ID = ? ");
            pstmt.setString(1, newID);
            pstmt.setString(2, "0");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("UPDATE PLACES SET FLOOR = ? WHERE FLOOR = ? ");
            pstmt.setString(1, newID);
            pstmt.setString(2, "0");
            pstmt.executeUpdate();

            for (int j = 1; j < 13; j++) {
                newID = UUID.randomUUID().toString();
                pstmt = conn.prepareStatement("UPDATE PLACES SET ID = ? WHERE ID = ? ");
                pstmt.setString(1, newID);
                pstmt.setString(2, String.valueOf(j));
                pstmt.executeUpdate();
            }

        } catch (SQLException ex) {
            Logger.getLogger(UpdateDefaults.class.getName()).log(Level.SEVERE, null, ex);
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
