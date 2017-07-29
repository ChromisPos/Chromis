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

package uk.chromis.pos.dbmanager;

import uk.chromis.pos.forms.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class RunRepair {

    public static void Process(String db_user, String db_url, String db_password) {        
        String s; // = new String();
        StringBuilder sb = new StringBuilder();
        Connection con = null;
        FileReader fr;
        File file;

        try {
            file = new File(System.getProperty("user.home") + "/repair.sql");
            fr = new FileReader(file);
        } catch (FileNotFoundException ex) {
            return;
        }

        try {
            ClassLoader cloader = new URLClassLoader(new URL[]{new File(AppConfig.getInstance().getProperty("db.driverlib")).toURI().toURL()});
            DriverManager.registerDriver(new DriverWrapper((Driver) Class.forName(AppConfig.getInstance().getProperty("db.driver"), true, cloader).newInstance()));
            Class.forName(AppConfig.getInstance().getProperty("db.driver"));
            con = DriverManager.getConnection(db_url, db_user, db_password);
            Statement stmt = (Statement) con.createStatement();

            BufferedReader br = new BufferedReader(fr);

            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            br.close();
            String[] inst = sb.toString().split(";");

            for (int i = 0; i < inst.length; i++) {
                if (!inst[i].trim().equals("")) {
                    System.out.println(">>" + inst[i]);
                    stmt.executeUpdate(inst[i]);
                }
            }
            file.delete();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException | IOException e) {
            System.out.println("*** Error : " + e.toString());
            System.out.println("*** ");
            System.out.println("*** Error : ");
            System.out.println("################################################");
            System.out.println(sb.toString());
        } finally {
            try {
                // do not try to close a nul connection
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {                
            }
        }
    }
}
