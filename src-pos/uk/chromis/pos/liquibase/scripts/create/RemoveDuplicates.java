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
public class RemoveDuplicates implements liquibase.change.custom.CustomTaskChange {

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
            Logger.getLogger(RemoveDuplicates.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Get the database version
        DataLogicSystem m_dlSystem = new DataLogicSystem();
        m_dlSystem.init(SessionFactory.getInstance().getSession());
        String sdbmanager = m_dlSystem.getDBVersion();

        String newID;
        try {
            //Check the siteguid table
            pstmt = conn.prepareStatement("SELECT ID, ORDEREXECUTED, COUNT(*)  FROM DATABASECHANGELOG GROUP BY ID HAVING COUNT(*) > 1");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
               // String first = rs.getString("ORDEREXECUTED");
                pstmt = conn.prepareStatement("DELETE FROM DATABASECHANGELOG WHERE ID = ? AND ORDEREXECUTED <> ? ");
                pstmt.setString(1, rs.getString("ID"));
                pstmt.setString(2, rs.getString("ORDEREXECUTED"));
                pstmt.executeUpdate();
            }
           
        } catch (SQLException ex) {
            Logger.getLogger(RemoveDuplicates.class.getName()).log(Level.SEVERE, null, ex);
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
