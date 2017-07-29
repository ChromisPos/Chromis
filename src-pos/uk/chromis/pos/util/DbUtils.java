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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.StartupDialog;

/**
 *
 *
 */
public class DbUtils {

    private static AltEncrypter cypher;

    public static TableModel resultSetToTableModel(ResultSet rs) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            Vector columnNames = new Vector();

            // Get the column names
            for (int column = 0; column < numberOfColumns; column++) {
                columnNames.addElement(metaData.getColumnLabel(column + 1));
            }
            // Get all rows.
            Vector rows = new Vector();
            while (rs.next()) {
                Vector newRow = new Vector();

                for (int i = 1; i <= numberOfColumns; i++) {
                    newRow.addElement(rs.getObject(i));
                }

                rows.addElement(newRow);
            }
            return new DefaultTableModel(rows, columnNames);
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static Connection getRemoteConnection(String sDBUser, String sDBPassword, String sURL) {
        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
            cypher = new AltEncrypter("cypherkey" + sDBUser);
            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
        }
        try {
            return DriverManager.getConnection(sURL, sDBUser, sDBPassword);
        } catch (SQLException ex) {
            return null;
        }
    }

    public static Connection getConnection() {
        String sDBUser = AppConfig.getInstance().getProperty("db.user");
        String sDBPassword = AppConfig.getInstance().getProperty("db.password");
        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {
            cypher = new AltEncrypter("cypherkey" + sDBUser);
            sDBPassword = cypher.decrypt(sDBPassword.substring(6));
        }
        try {
            return DriverManager.getConnection(AppConfig.getInstance().getProperty("db.url"), sDBUser, sDBPassword);
        } catch (SQLException ex) {
            return null;
        }
    }

    public static void checkJava() {
        String sJavaVersion = System.getProperty("java.vm.specification.version");
        double dJavaVersion = Double.parseDouble(sJavaVersion);
        //String sJavaVersion = System.getProperty("java.version");
        //double dJavaVersion = Double.parseDouble(sJavaVersion.substring(0, sJavaVersion.indexOf('.', sJavaVersion.indexOf('.') + 1)));

        if (dJavaVersion < 1.8) {
            StartupDialog dialog = new StartupDialog();
            JFrame frame = new JFrame("");
            JPanel dialogPanel = new JPanel();

            dialogPanel.add(dialog);
            JOptionPane.showMessageDialog(frame,
                    dialogPanel,
                    "Incorrect Java version ",
                    JOptionPane.PLAIN_MESSAGE);
            System.exit(-1);
        }
    }
}
