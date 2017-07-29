//    Chromis POS  - The New Face of Open Source POS
//    Copyright (c) (c) 2015-2016
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
package uk.chromis.pos.liquibase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Session;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppViewConnection;
import uk.chromis.pos.util.AltEncrypter;

public class JResetLiquibase extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                String db_user = (AppConfig.getInstance().getProperty("db.user"));
                String db_url = (AppConfig.getInstance().getProperty("db.URL"));
                String db_password = (AppConfig.getInstance().getProperty("db.password"));

                if (db_user != null && db_password != null && db_password.startsWith("crypt:")) {
                    // the password is encrypted
                    AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
                    db_password = cypher.decrypt(db_password.substring(6));
                }

                try {
                    Session session = AppViewConnection.createSession();
                    Connection con = DriverManager.getConnection(db_url, db_user, db_password);
                    Statement stmt = (Statement) con.createStatement();
                    String SQL = "DELETE FROM DATABASECHANGELOG ";
                    stmt.execute(SQL);
                    SQL = "UPDATE APPLICATIONS SET VERSION = '0.00' WHERE NAME ='Chromis Pos' ";
                    stmt.execute(SQL);
                    JOptionPane.showMessageDialog(null, "Liquibase tables cleared ready for new attempt.", "Liquibase Reset", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                } catch (BasicException | SQLException e) {
                    System.out.println(e.getMessage());
                    JOptionPane.showMessageDialog(null, "Liquibase tables clear Failed.", "Liquibase Reset", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
