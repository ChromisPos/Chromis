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

package uk.chromis.pos.reports;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.BaseSentence;
import uk.chromis.data.loader.DataResultSet;
import uk.chromis.pos.forms.AppLocal;

/**
 *
 *   
 */
public class JRDataSourceBasic implements JRDataSource {
    
    private BaseSentence sent;
    private DataResultSet SRS = null;
    private Object current = null;
    
    private ReportFields m_fields = null;
    
    /** Creates a new instance of JRDataSourceBasic
     * @param sent
     * @param fields
     * @param params
     * @throws uk.chromis.basic.BasicException */
    public JRDataSourceBasic(BaseSentence sent, ReportFields fields, Object params) throws BasicException  {   
        
        this.sent = sent;
        SRS = sent.openExec(params);
        m_fields = fields;
    }
    
    /**
     *
     * @param jrField
     * @return
     * @throws JRException
     */
    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        
        try {
            return m_fields.getField(current,  jrField.getName());
        } catch (ReportException er) {
            throw new JRException(er);
        }
    }
    
    /**
     *
     * @return
     * @throws JRException
     */
    @Override
    public boolean next() throws JRException {
        
        if (SRS == null) {
            throw new JRException(AppLocal.getIntString("exception.unavailabledataset"));
        }
        
        try {
            if (SRS.next()) {
                current = SRS.getCurrent();
                return true;
            } else {                
                current = null;
                SRS = null;
                sent.closeExec();
                sent = null;
                return false;
            }                
        } catch (BasicException e) {
            throw new JRException(e);
        }      
    }
}
