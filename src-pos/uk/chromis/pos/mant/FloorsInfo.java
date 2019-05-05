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

package uk.chromis.pos.mant;

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.IKeyed;
import uk.chromis.data.loader.SerializableRead;

public class FloorsInfo implements SerializableRead, IKeyed {

    private static final long serialVersionUID = 8906929819402L;
    private String m_sID;
    private String m_sName;
    private String m_sSiteGuid;
    private byte[] m_bImage;


    public FloorsInfo() {
        m_sID = null;
        m_sName = null;
        m_bImage = null;
        m_sSiteGuid = null;
    }

    public FloorsInfo(String id, String name){
        m_sID = id;
        m_sName = name;
    }
    
    
    public String getSiteGuid() {
        return m_sSiteGuid;
    }

    public byte[] getImage() {
        return m_bImage;
    }

    public void setImage(byte[] image) {
        this.m_bImage = image;
    }

    public void setSiteGuid(String siteGuid) {
        this.m_sSiteGuid = siteGuid;
    }

    @Override
    public Object getKey() {
        return m_sID;
    }

    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_sID = dr.getString(1);
        m_sName = dr.getString(2);
        m_bImage = dr.getBytes(3);
        m_sSiteGuid = dr.getString(4);
    }

    public void setID(String sID) {
        m_sID = sID;
    }

    public String getID() {
        return m_sID;
    }

    public String getName() {
        return m_sName;
    }

    public void setName(String sName) {
        m_sName = sName;
    }

    @Override
    public String toString() {
        return m_sName;
    }
}
