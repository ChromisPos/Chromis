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
import uk.chromis.data.loader.IKeyed;
import uk.chromis.data.loader.SerializableRead;

public class ResourcesInfo implements SerializableRead, IKeyed {

    private static final long serialVersionUID = 9110127845966L;
    private String m_sID;
    protected String m_sName;
    private Integer m_iRestype;
    private byte[] m_bContent;
    private String m_siteGuid;





    public ResourcesInfo(String id, String name, Integer restype, byte[] content, String siteGuid ) {
        this.m_sID = id;
        this.m_sName = name;
        this.m_iRestype = restype;
        this.m_bContent = content;
    }

    /**
     *
     * @return
     */
    @Override
    public Object getKey() {
        return m_sID;
    }

    /**
     *     
     * @param dr
     * @throws BasicException
     */
    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_sID = dr.getString(1);
        m_sName = dr.getString(2);
        m_iRestype = dr.getInt(3);
        m_bContent = dr.getBytes(4);
        m_siteGuid = dr.getString(5);
    }

    public String getID() {
        return m_sID;
    }

    public void setID(String m_sID) {
        this.m_sID = m_sID;
    }

    public String getName() {
        return m_sName;
    }

    public void setName(String m_sName) {
        this.m_sName = m_sName;
    }

    public Integer getRestype() {
        return m_iRestype;
    }

    public void setRestype(Integer m_iRestype) {
        this.m_iRestype = m_iRestype;
    }

    public byte[] getContent() {
        return m_bContent;
    }

    public void setContent(byte[] m_bContent) {
        this.m_bContent = m_bContent;
    }
    public String getSiteGuid() {
        return m_siteGuid;
    }

    public void setSiteGuid(String m_siteGuid) {
        this.m_siteGuid = m_siteGuid;
    }

}
