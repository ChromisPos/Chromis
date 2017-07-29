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

package uk.chromis.pos.epm;


public class Break {

    private String m_sId;
    private String m_sName;
    private String m_sNotes;
    private boolean m_sVisible;
    private String m_sSiteGuid;

    /**
     *
     * @param id
     * @param name
     * @param notes
     * @param visible
     */
    public Break(String id, String name, String notes,  boolean visible) {
        m_sId = id;
        m_sName = name;
        m_sNotes = notes;
        m_sVisible = visible;
    }

        public Break(String id, String name, String notes,  boolean visible, String siteguid) {
        m_sId = id;
        m_sName = name;
        m_sNotes = notes;
        m_sVisible = visible;
        m_sSiteGuid = siteguid;
    }

    public String getSiteGuid() {
        return m_sSiteGuid;
    }

    public void setSiteGuid(String siteGuid) {
        this.m_sSiteGuid = siteGuid;
    }
    
    public String getId() {
        return m_sId;
    }

    public void setId(String Id) {
        this.m_sId = Id;
    }

    public String getName() {
        return m_sName;
    }

    public void setName(String Name) {
        this.m_sName = Name;
    }

    public String getNotes() {
        return m_sNotes;
    }

    public void setNotes(String Notes) {
        this.m_sNotes = Notes;
    }

    public boolean isVisible() {
        return m_sVisible;
    }

    public void setVisible(boolean Visible) {
        this.m_sVisible = Visible;
    }
}
