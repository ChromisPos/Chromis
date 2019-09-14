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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import uk.chromis.data.loader.ConnectionFactory;

/**
 * @author John Lewis
 */
public class StockCurrentGUID implements liquibase.change.custom.CustomTaskChange {

    @Override
    public void execute(Database dtbs) throws CustomChangeException {
        Connection conn;
        PreparedStatement pstmt;
        String guid = UUID.randomUUID().toString();
        ResultSet rs;

        conn = ConnectionFactory.getInstance().getConnection();

        try {
            
            
            Statement stmt = (Statement) conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            
            rs = stmt.executeQuery("SELECT * FROM STOCKCURRENT");
            while (rs.next()) {
               rs.updateString("ID", UUID.randomUUID().toString());
               rs.updateRow();
            }            

            pstmt = conn.prepareStatement("DELETE FROM STOCKCURRENT WHERE ID =''");
            pstmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(SiteGUID.class.getName()).log(Level.SEVERE, null, ex);
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
