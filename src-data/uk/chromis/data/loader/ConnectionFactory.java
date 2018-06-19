/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c)2015-2018
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
package uk.chromis.data.loader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.DriverWrapper;
import uk.chromis.pos.util.AltEncrypter;

/**
 *
 * @author John
 */
public class ConnectionFactory {

    private static ConnectionFactory INSTANCE = new ConnectionFactory();
    private static Connection dataSource;
    private static Boolean dbValid = false;
    private static Connection connection;
    private static String db_user;
    private static String db_password;  
    private static String db_url;

    private ConnectionFactory() {
        db_user = (AppConfig.getInstance().getProperty("db.user"));
        db_url = (AppConfig.getInstance().getProperty("db.URL"));
        db_password = (AppConfig.getInstance().getProperty("db.password"));
        if (db_user != null && db_password != null && db_password.startsWith("crypt:")) {
            // the password is encrypted
            AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
            db_password = cypher.decrypt(db_password.substring(6));
        }
    }

    public static ConnectionFactory getInstance() {
        if (INSTANCE == null) {
            synchronized (ConnectionFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ConnectionFactory();
                }
            }
        }
        return INSTANCE;
    }

    public static Connection getConnection() {
        if (connection != null) {
            return connection;
        }
        try {
            ClassLoader cloader = new URLClassLoader(new URL[]{new File(AppConfig.getInstance().getProperty("db.driverlib")).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getInstance().getProperty("db.driver"), true, cloader).newInstance()));
            connection = (Connection) DriverManager.getConnection(db_url, db_user, db_password);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select name from applications where id = 'chromispos' ");
            while (rs.next()){
                dbValid = (rs.getString("name").equals("Chromis POS")?true:false);
            }
            return connection;
        } catch (SQLException ex) {

        } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ConnectionFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Boolean isDbValid(){
        getConnection();
        return dbValid;
    }
}
