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

import uk.chromis.format.Formats;

/**
 *
 * @author  adrian
 */
public class RenderStringBasic implements IRenderString {
    
    private Formats[] m_aFormats;
    private int[] m_aiIndex;
    
    /** Creates a new instance of StringnizerBasic
     * @param fmts
     * @param aiIndex */
    public RenderStringBasic(Formats[] fmts, int[] aiIndex) {
        m_aFormats = fmts; 
        m_aiIndex = aiIndex;
    }

    /**
     *
     * @param value
     * @return
     */
    public String getRenderString(Object value) {
        
        if (value == null) {
            return null; 
        } else {
            Object [] avalue = (Object[]) value;
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < m_aiIndex.length; i++) {
                if (i > 0) {
                    sb.append(" - ");
                }
                sb.append(m_aFormats[m_aiIndex[i]].formatValue(avalue[m_aiIndex[i]]));
            }
            
            return sb.toString();
        }
    }  
   
}
