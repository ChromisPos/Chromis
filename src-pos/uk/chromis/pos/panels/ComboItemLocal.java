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

package uk.chromis.pos.panels;

import uk.chromis.data.loader.IKeyed;
import uk.chromis.pos.forms.AppLocal;

/**
 *
 * @author adrianromero
 * Created on February 12, 2007, 10:49 PM
 *
 */
public class ComboItemLocal implements IKeyed {

    /**
     *
     */
    protected Integer m_iKey;

    /**
     *
     */
    protected String m_sKeyValue;

    /**
     *
     * @param iKey
     * @param sKeyValue
     */
    public ComboItemLocal(Integer iKey, String sKeyValue) {
        m_iKey = iKey;
        m_sKeyValue = sKeyValue;
    }

    /**
     *
     * @return
     */
    @Override
    public Object getKey() {
        return m_iKey;
    }

    /**
     *
     * @return
     */
    public Object getValue() {
        return m_sKeyValue;
    }
    @Override
    public String toString() {
        return AppLocal.getIntString(m_sKeyValue);
    }
} 
