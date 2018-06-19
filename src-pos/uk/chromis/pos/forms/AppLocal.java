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

package uk.chromis.pos.forms;

import uk.chromis.beans.LocaleResources;

public class AppLocal {

    public static final String APP_NAME = "Chromis POS";
    public static final String APP_ID = "chromispos";
    public static final String APP_VERSION = "0.94";
    public static final Integer APP_VERSIONINT = 20180609;
    public static final String APP_DEMO = "";
   
    private static final LocaleResources m_resources;
    public static String LIST_BY_RIGHTS = "";

    static {
        m_resources = new LocaleResources();
        m_resources.addBundleName("pos_messages");
        m_resources.addBundleName("erp_messages");
        m_resources.addBundleName("permissions_messages");
    }

    /**
     * Creates a new instance of AppLocal
     */
    private AppLocal() {
    }

    /**
     *
     * @param sKey
     * @return
     */
    public static String getIntString(String sKey) {
        return m_resources.getString(sKey);
    }

    /**
     *
     * @param sKey
     * @param sValues
     * @return
     */
    public static String getIntString(String sKey, Object... sValues) {
        return m_resources.getString(sKey, sValues);
    }
}
