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


package uk.chromis.pos.ticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.DataWrite;
import uk.chromis.data.loader.SerializableRead;
import uk.chromis.data.loader.SerializableWrite;

/**
 *
 * @author John Barrett
 */
public class CouponSet implements SerializableWrite, SerializableRead, Serializable{
    Set<CouponLine> lines = new TreeSet<CouponLine>();

    public CouponLine findLine( String id, int linenumber ) {  
        Iterator<CouponLine> iterator = lines.iterator();

        while(iterator.hasNext()) {
            CouponLine line = iterator.next();
            if(line.getid().contentEquals(id) &&             
                line.getlinenumber() == linenumber ) {        
                return line;
            }
        }

        return null;
    }

    public void add( String id, int linenumber, String text ) {  
        lines.add( new CouponLine( id, linenumber, text ) );
    }

    public void clear( ) {  
        lines.clear();
    }

    // Remove a single line
    public void remove( String id, int linenumber ) {  
        Iterator<CouponLine> iterator = lines.iterator();

        while(iterator.hasNext()) {
            CouponLine line = iterator.next();
            if(line.getid().contentEquals(id) &&             
                line.getlinenumber() == linenumber ) {        
                iterator.remove();
            }
        }
    }

    // Remove all lines for this coupon
    public void remove( String id ) {  
        Iterator<CouponLine> iterator = lines.iterator();

        while(iterator.hasNext()) {
            CouponLine line = iterator.next();
            if(line.getid().contentEquals(id)) {        
                iterator.remove();
            }
        }
    }

    public void copyAll( CouponSet c ) {
        Iterator<CouponLine> iterator = c.lines.iterator();

        while(iterator.hasNext()) {
            CouponLine line = iterator.next();
            this.lines.add( new CouponLine( line ) );
        }            
    }

    public List<String> getCouponLines() {
        List<String> result = new ArrayList<>();
        Iterator<CouponLine> iterator = this.lines.iterator();

        while(iterator.hasNext()) {
            CouponLine line = iterator.next();
            result.add( line.gettext() );
        }            
        return result;
    }

    @Override
    public void writeValues(DataWrite dp) throws BasicException {

        dp.setObject(1, lines );
    }

    @Override
    public void readValues(DataRead dr) throws BasicException {
        lines = (Set<CouponLine>) dr.getObject(1);
    }
}
