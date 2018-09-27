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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class RunRepair {

    public static void Process(Connection con) {        
        String s; // = new String();
        StringBuilder sb = new StringBuilder();        
        FileReader fr;
        File file;

        try {
            file = new File(System.getProperty("user.dir") + "/repair.sql");
            fr = new FileReader(file);
        } catch (FileNotFoundException ex) {
            return;
        }

        try {

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
        } catch (SQLException | IOException e) {
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
