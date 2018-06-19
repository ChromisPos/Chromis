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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import uk.chromis.pos.util.SessionFactory;

/**
 * @author John Lewis
 */
public class SetDefaultSiteGUID implements liquibase.change.custom.CustomTaskChange {

    private String table;
    private String dbtype;
    private Connection conn = null;
    private PreparedStatement pstmt;
    private String guid;
    private Statement stmt;
    private String SQL;

    public void setDbtype(String dbtype) {
        this.dbtype = dbtype;
    }

    public void setTable(String table) {
        this.table = table.toLowerCase();
    }

    @Override
    public void execute(Database dtbs) throws CustomChangeException {
        try {
            conn = SessionFactory.getInstance().getSession().getConnection();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from siteguid");
            while (rs.next()) {
                if (dbtype.equals("MySQL")) {
                    SQL = "alter table " + table + " modify column siteguid varchar(50) default '" + rs.getString("guid") + "'";
                } else {
                    SQL = "alter table " + table + " alter column siteguid set default '" + rs.getString("guid") + "'";
                }
                pstmt = conn.prepareStatement(SQL);
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(SetDefaultSiteGUID.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(SetDefaultSiteGUID.class.getName()).log(Level.SEVERE, null, ex);
            }
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
