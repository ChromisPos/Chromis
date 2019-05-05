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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.chromis.pos.util.AltEncrypter;

/**
 *
 * @author John
 */
public class UpdateTicketType {

    private static String db_user;
    private static String db_url;
    private static String db_password;
    private static Connection con;
    private static ResultSet rs;
    private static Statement stmt;
    private static PreparedStatement stmt2;
    private static String SQL;
    private static String SQL2;

    public UpdateTicketType() {

    }

    public static void updateTicketType() {

        db_user = (AppConfig.getInstance().getProperty("db.user"));
        db_url = (AppConfig.getInstance().getProperty("db.URL"));
        db_password = (AppConfig.getInstance().getProperty("db.password"));
        if (db_user != null && db_password != null && db_password.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
            db_password = cypher.decrypt(db_password.substring(6));
        }

        try {
            ClassLoader cloader = new URLClassLoader(new URL[]{new File(AppConfig.getInstance().getProperty("db.driverlib")).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getInstance().getProperty("db.driver"), true, cloader).newInstance()));
            Class.forName(AppConfig.getInstance().getProperty("db.driver"));
            con = DriverManager.getConnection(db_url, db_user, db_password);
            stmt = (Statement) con.createStatement();

            // Convert the resourse pointers
            SQL = "SELECT * FROM RESOURCES WHERE RESTYPE = 0 ";
            rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                String decodedData;
                if (rs.getBytes("CONTENT") != null) {
                    byte[] bytesData = rs.getBytes("CONTENT");
                    if (!"49".equals(rs.getString("ID"))) {
                        decodedData = new String(bytesData);

                        decodedData = decodedData.replaceAll(".ticketType} *== *0", ".ticketType} == \"NORMAL\"");
                        decodedData = decodedData.replaceAll(".ticketType} *== *1", ".ticketType} == \"REFUND\"");
                        decodedData = decodedData.replaceAll(".ticketType} *== *2", ".ticketType} == \"PAYMENT\"");
                        decodedData = decodedData.replaceAll(".ticketType} *== *3", ".ticketType} == \"NOSALE\"");

                        decodedData = decodedData.replaceAll(".ticketType} *!= *0", ".ticketType} != \"NORMAL\"");
                        decodedData = decodedData.replaceAll(".ticketType} *!= *1", ".ticketType} != \"REFUND\"");
                        decodedData = decodedData.replaceAll(".ticketType} *!= *2", ".ticketType} != \"PAYMENT\"");
                        decodedData = decodedData.replaceAll(".ticketType} *!= *3", ".ticketType} != \"NOSALE\"");

                        bytesData = decodedData.getBytes();
                        SQL2 = "DELETE FROM RESOURCES WHERE ID = ? ";
                        stmt2 = con.prepareStatement(SQL2);
                        stmt2.setString(1, rs.getString("ID"));
                        stmt2.executeUpdate();

                        SQL2 = "INSERT INTO RESOURCES (ID, NAME, RESTYPE, CONTENT) VALUES (?, ?, ?, ?)";
                        stmt2 = con.prepareStatement(SQL2);
                        stmt2.setString(1, rs.getString("ID"));
                        stmt2.setString(2, rs.getString("NAME"));
                        stmt2.setInt(3, rs.getInt("RESTYPE"));
                        stmt2.setBytes(4, bytesData);
                        stmt2.executeUpdate();
                        ;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("*******************************************************");
            System.out.println("Error : = " + e);
            System.out.println("");
        }
        AppConfig.getInstance().setBoolean("chromis.tickettype", true);
        try {
            AppConfig.getInstance().save();
        } catch (IOException ex) {
            Logger.getLogger(UpdateTicketType.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
