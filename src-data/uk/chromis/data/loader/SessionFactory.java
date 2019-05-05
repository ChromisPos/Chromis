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

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.util.AltEncrypter;

public class SessionFactory {

    private static SessionFactory INSTANCE = new SessionFactory();
    private Session session;
    private final String db_user;
    private final String db_url;
    private String db_password;

// Used so that we are not creating multiple sessions for the user
    private SessionFactory() {
        db_user = (AppConfig.getInstance().getProperty("db.user"));        
        db_url = (AppConfig.getInstance().getProperty("db.URL"));
        db_password = (AppConfig.getInstance().getProperty("db.password"));

        if (db_user != null && db_password != null && db_password.startsWith("crypt:")) {
            // the password is encrypted
            AltEncrypter cypher = new AltEncrypter("cypherkey" + db_user);
            db_password = cypher.decrypt(db_password.substring(6));
        }
    }

    public static SessionFactory getInstance() {
        if (INSTANCE == null) {
            synchronized (SessionFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SessionFactory();
                }
            }
        }
        return INSTANCE;
    }

    public Session getSession() {
        if (session != null) {           
            return session;
        }
        try {
            session = new Session(db_url, db_user, db_password);
            return session;
        } catch (SQLException ex) {
            Logger.getLogger(SessionFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
