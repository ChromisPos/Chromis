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
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.DataWrite;
import uk.chromis.data.loader.SerializableRead;
import uk.chromis.data.loader.SerializableWrite;

/**
 *
 * @author John Barrett
 */

public class CouponLine implements Comparable<CouponLine>, SerializableWrite, SerializableRead, Serializable{

    protected String m_Id;
    protected int m_LineNumber;
    protected String m_LineText;

    public CouponLine() {
        super();            
    }

    public CouponLine(String id, int linenumber, String text) {
        super();
        this.m_Id = id;
        this.m_LineNumber = linenumber;
        this.m_LineText = text;
    }

    public CouponLine(CouponLine o) {
        super();
        this.m_Id = o.m_Id;
        this.m_LineNumber = o.m_LineNumber;
        this.m_LineText = o.m_LineText;
    }

    public String getid() {
        return m_Id;
    }
    public int getlinenumber() {
        return m_LineNumber;
    }
    public String gettext() {
        return m_LineText;
    }

    public int compareTo(CouponLine o) {
        int c = o.m_Id.compareTo(m_Id);

        if( c==0 ) {
            // Same id so compare line numbers
            c = m_LineNumber - o.m_LineNumber;
        }
        return c;
    }        

    @Override
    public void writeValues(DataWrite dp) throws BasicException {
        dp.setString(1, m_Id);
        dp.setInt(2, m_LineNumber);
        dp.setString(3, m_LineText);
   }

    @Override
    public void readValues(DataRead dr) throws BasicException {
        m_Id = dr.getString(1);
        m_LineNumber = dr.getInt(2);
        m_LineText = dr.getString(3);
    }
}    

