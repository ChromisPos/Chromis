/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c) 2015-2018
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
import uk.chromis.data.loader.SerializerRead;

/**
 *
 * @author john
 */
public class DBPermissionsInfo implements IKeyed {

    private static final long serialVersionUID = 1L;
    private String m_displayName;
    private String m_className;
    private String m_section;
    private String m_description;
    private String m_siteGuid;

    public DBPermissionsInfo(String className, String section, String displayName, String description, String siteGuid) {
        m_className = className;
        m_section = section;
        m_displayName = displayName;
        m_description = description;
        m_siteGuid = siteGuid;
    }

    @Override
    public Object getKey() {
        return m_className;
    }

    public String getClassName() {
        return m_className;
    }

    public void setClassName(String m_className) {
        this.m_className = m_className;
    }

    public String getSiteGUID() {
        return m_siteGuid;
    }

    public void setSiteGUID(String guid) {
        this.m_siteGuid = guid;
    }    
    
    
    public String getSection() {
        return m_section;
    }

    public void setSection(String section) {
        this.m_section = section;
    }

    public String getDisplayName() {
        return m_displayName;
    }

    public void setDisplayName(String displayName) {
        this.m_displayName = displayName;
    }

    public String getDescription() {
        return m_description;
    }

    public void setDescription(String description) {
        this.m_description = description;
    }

    public static SerializerRead getSerializerRead() {
        return (DataRead dr) -> new DBPermissionsInfo(dr.getString(1), dr.getString(2), dr.getString(3), dr.getString(4), dr.getString(5));
    }

}
