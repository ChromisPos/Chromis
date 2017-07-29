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

package uk.chromis.pos.admin;

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;


public class RoleExtInfo extends RoleInfo {
    
    /**
     *
     */
    protected byte[] m_aPermissions;
    
    /** Creates a new instance of RoleExtInfo */
    public RoleExtInfo() {
        super();
        m_aPermissions = null;
    }
    
    /**
     *
     * @param dr
     * @throws BasicException
     */
    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_sName = dr.getString(1);
        m_aPermissions = dr.getBytes(2);
    }   
    
    //  implements Vectorer, ComparatorCreator

    /**
     *
     * @return
     */
        public static String[] getHeaders() {
        return new String[] {"Name"};
    }
    
    /**
     *
     * @return
     */
    public String[] toStringArray() {
        return new String[] {m_sName};
    } 

    /**
     *
     * @return
     */
    public Comparable[] toComparableArray() {
        return new Comparable[] {m_sName};
    }
}
