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

import java.sql.Connection;
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
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.util.SessionFactory;

/**
 * @author John Lewis
 */
public class RemoveDuplicates implements liquibase.change.custom.CustomTaskChange {

    @Override
    public void execute(Database dtbs) throws CustomChangeException {
        Connection conn = null;
        PreparedStatement pstmt;
        PreparedStatement pstmt2;
       
        try {
            conn = SessionFactory.getInstance().getSession().getConnection();

        } catch (SQLException ex) {
            Logger.getLogger(RemoveDuplicates.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Get the database version
        DataLogicSystem m_dlSystem = new DataLogicSystem();
        m_dlSystem.init(SessionFactory.getInstance().getSession());
        String sdbmanager = m_dlSystem.getDBVersion();

        String newID;
        try {
            //Check the siteguid table
            pstmt = conn.prepareStatement("SELECT * FROM DATABASECHANGELOG ");
            ResultSet rs = pstmt.executeQuery();
            String insertSQL = "insert into DATABASECHANGELOG  (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, EXECTYPE, MD5SUM, DESCRIPTION, COMMENTS, TAG, LIQUIBASE, CONTEXTS, LABELS) values "
                    + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            while (rs.next()) {
                pstmt2 = conn.prepareStatement("DELETE From DATABASECHANGELOG WHERE ID = ? ");
                pstmt2.setString(1, rs.getString("ID"));
                pstmt2.executeUpdate();

                pstmt2 = conn.prepareStatement(insertSQL);
                pstmt2.setString(1, rs.getString("ID"));
                pstmt2.setString(2, rs.getString("AUTHOR"));
                pstmt2.setString(3, rs.getString("FILENAME"));
                pstmt2.setTimestamp(4, rs.getTimestamp("DATEEXECUTED"));
                pstmt2.setInt(5, rs.getInt("ORDEREXECUTED"));
                pstmt2.setString(6, rs.getString("EXECTYPE"));
                pstmt2.setString(7, rs.getString("MD5SUM"));
                pstmt2.setString(8, rs.getString("DESCRIPTION"));
                pstmt2.setString(9, rs.getString("COMMENTS"));
                pstmt2.setString(10, rs.getString("TAG"));
                pstmt2.setString(11, rs.getString("LIQUIBASE"));
                pstmt2.setString(12, rs.getString("CONTEXTS"));
                pstmt2.setString(13, rs.getString("LABELS"));
                pstmt2.executeUpdate();
               
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
