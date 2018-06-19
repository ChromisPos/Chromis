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

/**
 *
 * @author  adrian
 */
public class KeyGetterBasic implements IKeyGetter {
    
    private int [] m_aElems;
    
    /** Creates a new instance of KeyGetterBasic
     * @param aElems */
    public KeyGetterBasic(int[] aElems) {
        m_aElems = aElems;
    }
    
    /**
     *
     * @param value
     * @return
     */
    public Object getKey(Object value) {
        if (value == null) {
            return null;
        } else {
            Object[] avalue = (Object []) value;
            Object[] akey = new Object[m_aElems.length];
            for (int i = 0; i < m_aElems.length; i++) {
                akey[i] = avalue[m_aElems[i]];
            }
            return akey;
        }
    }   
}
