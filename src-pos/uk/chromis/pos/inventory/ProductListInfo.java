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

package uk.chromis.pos.inventory;

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.IKeyed;
import uk.chromis.data.loader.SerializableRead;

/**
 *
 * @author adrianromero
 * Created on February 13, 2007, 10:13 AM
 *
 */
public class ProductListInfo implements SerializableRead, IKeyed {
    
    private static final long serialVersionUID = 9032683595244L;
    private String m_sName;
    
    /** Creates a new instance of LocationInfo */
    public ProductListInfo() {
        m_sName = null;
    }
    
    /** Creates a new instance of LocationInfo */
    public ProductListInfo( String name ) {
        m_sName = name;
    }

    /**
     *
     * @return
     */
    public Object getKey() {
        return m_sName;
    }

    /**
     *
     * @param dr
     * @throws BasicException
     */
    public void readValues(DataRead dr) throws BasicException {
        m_sName = dr.getString(1);
    }

    /**
     *
     * @param sID
     */
    public void setID(String sID) {
        m_sName = sID;
    }
    
    /**
     *
     * @return
     */
    public String getID() {
        return m_sName;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return m_sName;
    }
    
    /**
     *
     * @param sName
     */
    public void setName(String sName) {
        m_sName = sName;
    }  

    public String toString(){
        return m_sName;
    }    
}
