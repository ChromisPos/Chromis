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

import uk.chromis.data.model.Row;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicStockChanges;

/**
 *
 * @author  adrian
 */
public class RenderStringStockChange implements IRenderString {
    
    private DataLogicStockChanges m_dlChanges;
    private Formats[] m_aFormats;
    private int[] m_aiIndex;
    
    /** Creates a new instance of StringnizerBasic
     * @param fmts
     */
    public RenderStringStockChange(DataLogicStockChanges dlChanges ) {
        m_dlChanges = dlChanges;
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
            
            int index = m_dlChanges.getIndexOf( "PRODUCTREF");
            sb.append( m_dlChanges.getFormatOf(index).formatValue( avalue[index]) );
            
            sb.append( " - " );
            
            index = m_dlChanges.getIndexOf( "PRODUCTNAME");
            sb.append( m_dlChanges.getFormatOf(index).formatValue( avalue[index]) );
                        
            sb.append( " - " );
            
            index = m_dlChanges.getIndexOf( "CHANGETYPE");
            int type = (Integer) avalue[index];
            
            switch( type ) {
                case 1:
                    sb.append( AppLocal.getIntString("label.stockchangeadjust") );
                    break;
                case 2:
                case 3:
                    sb.append( AppLocal.getIntString("label.stockchangeset") );
                    break;
 
                case 4:
                case 5:
                    sb.append( AppLocal.getIntString("label.stockchangenewvalue") );
                    break;
                    
                case 6:
                    sb.append( AppLocal.getIntString("label.stockchangenewrecord") );
                    break;
            }

            sb.append(" ");
            index = m_dlChanges.getIndexOf( "FIELD");
            sb.append( m_dlChanges.getFormatOf(index).formatValue( avalue[index]) );
            
            return sb.toString();
        }
    }  
   
}
