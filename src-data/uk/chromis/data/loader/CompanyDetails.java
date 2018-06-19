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

package uk.chromis.data.loader;


import java.io.File;
import uk.chromis.pos.forms.AppConfig;

/**
 *
 *   
 */
public class CompanyDetails {
    private String db_url;
    private String db_user;       
    private String db_password;
    private File m_config;
    private Session session;

    /**
     *
     */
    public CompanyDetails() {             
    
    
}

    /**
     *
     * @param config
     */
    public void loadProperties() {
         
        db_url=(AppConfig.getInstance().getProperty("db.url"));
        db_user=(AppConfig.getInstance().getProperty("db_user"));
        db_password=(AppConfig.getInstance().getProperty("db.password"));
       //catch (BasicException){
         // Session s = new Session(db_url,db_user,"epos");
        
}

    /**
     *
     * @return
     */
    public String getUser() {
        return db_user;
    }
}





