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
import uk.chromis.pos.forms.DriverWrapper;

/**
 * @author John Lewis
 */
public class PopSitesTable implements liquibase.change.custom.CustomTaskChange {

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
        String Guid = null;
        PreparedStatement pstmt;
        ResultSet rs;

        try {
            cloader = new URLClassLoader(new URL[]{new File(AppConfig.getInstance().getProperty("db.driverlib")).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getInstance().getProperty("db.driver"), true, cloader).newInstance()));
            Session session = new Session(db_url, db_user, db_password);
            conn = session.getConnection();

        } catch (MalformedURLException | SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(PopSitesTable.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            pstmt = conn.prepareStatement("SELECT * FROM SITEGUID");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Guid = rs.getString("GUID");
            }

            //check if sites table is already populated
            Boolean exists = false;
            pstmt = conn.prepareStatement("SELECT * FROM SITES WHERE GUID = ? ");
            pstmt.setString(1, Guid);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                exists = true;
            }
            
            if (!exists){
            String SQL = "INSERT INTO SITES (GUID, NAME, SITEURL, SITEUSERNAME, SITEPASSWORD, SITEACTIVE) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(SQL);
            pstmt.setString(1, Guid);
            pstmt.setString(2, "Site Name");
            pstmt.setString(3, db_url);
            pstmt.setString(4, db_user);
            pstmt.setString(5, AppConfig.getInstance().getProperty("db.password"));
            pstmt.setBoolean(6, true);
            pstmt.executeUpdate();
            }
            pstmt.close();
            
            conn.close();

        } catch (SQLException ex) {
            Logger.getLogger(PopSitesTable.class.getName()).log(Level.SEVERE, null, ex);
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
