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

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.IRenderString;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.RenderStringStockChange;
import uk.chromis.data.loader.SentenceExec;
import uk.chromis.data.loader.SentenceExecTransaction;
import uk.chromis.data.loader.SerializerWrite;
import uk.chromis.data.loader.SerializerWriteBasicExt;
import uk.chromis.data.loader.Session;
import uk.chromis.data.model.Field;
import uk.chromis.data.model.Row;
import uk.chromis.format.Formats;

public class DataLogicStockChanges extends BeanFactoryDataSingle {
    
    protected Session m_session;
    
    protected Row m_changesRow;
    
    // These next arrays are order dependant.
    // Use the provided getIndexOf and namesToIndexes functions
    // to access the columns, do not hard code array indexes.
    // If you add a new field, all four arrays need a new entry.
    protected String[] m_FieldNames = new String[] {
                "ID", "LOCATION", "USERNAME", "UPLOADTIME", 
                "PRODUCTID", "CHANGETYPE", "CHANGES_PROCESSED",
                "FIELD", "TEXTVALUE", "BLOBVALUE",
                "PRODUCTNAME", "PRODUCTREF"
            };          
    
    private Datas[] m_FieldDataTypes = new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING,
                Datas.TIMESTAMP, Datas.STRING, Datas.INT, Datas.INT,
                Datas.STRING, Datas.STRING, Datas.IMAGE,
                Datas.STRING, Datas.STRING
            };
    
    private Formats[] m_fieldformat = 
            new Formats[] {Formats.STRING, Formats.STRING, Formats.STRING,
                Formats.TIMESTAMP, Formats.STRING, Formats.INT, Formats.INT,
                Formats.STRING, Formats.STRING, Formats.NULL,
                Formats.STRING, Formats.STRING
            };
    
    /** Creates a new instance of DataLogicStockChanges */
    public DataLogicStockChanges() {  
        m_changesRow = new Row(
                new Field(m_FieldNames[0], m_FieldDataTypes[0], m_fieldformat[0] ), 
                new Field(m_FieldNames[1], m_FieldDataTypes[1], m_fieldformat[1] ), 
                new Field(m_FieldNames[2], m_FieldDataTypes[2], m_fieldformat[2] ), 
                new Field(m_FieldNames[3], m_FieldDataTypes[3], m_fieldformat[3] ), 
                new Field(m_FieldNames[4], m_FieldDataTypes[4], m_fieldformat[4] ), 
                new Field(m_FieldNames[5], m_FieldDataTypes[5], m_fieldformat[5] ), 
                new Field(m_FieldNames[6], m_FieldDataTypes[6], m_fieldformat[6] ), 
                new Field(m_FieldNames[7], m_FieldDataTypes[7], m_fieldformat[7] ), 
                new Field(m_FieldNames[8], m_FieldDataTypes[8], m_fieldformat[8] ), 
                new Field(m_FieldNames[9], m_FieldDataTypes[9], m_fieldformat[9] ),
                new Field(m_FieldNames[10], m_FieldDataTypes[10], m_fieldformat[10] ), 
                new Field(m_FieldNames[11], m_FieldDataTypes[11], m_fieldformat[11] ) 
                );
    }
    
    /**
     *
     * @param session
     */
    @Override
    public void init(Session session){
 
        this.m_session = session;
        
    }
 
    // Find the column number of the given field
    public int getIndexOf( CharSequence fieldname ) {
        int i = m_FieldNames.length - 1;
        while( i > 0 && !m_FieldNames[i].contentEquals(fieldname)) {
            // Still looking
            --i;
        }
        return i;
    }
    
     // Find the column numbers of the given fields
    public int[] namesToIndexes( CharSequence[] fieldnames ) {
        int[] indexes = new int[ fieldnames.length ];
        
        for( int i = 0; i < fieldnames.length; ++i ){
            indexes[i] = getIndexOf( fieldnames[i] );
        }
        return indexes;
    }
    
     // Find the Datas of the given fields
    public Datas[] namesToDatas( CharSequence[] fieldnames ) {
        Datas[] datas = new Datas[ fieldnames.length ];
        
        for( int i = 0; i < fieldnames.length; ++i ){
            datas[i] = m_FieldDataTypes[ getIndexOf( fieldnames[i] ) ];
        }
        return datas;
    }
    
    // Find the format for the given field
    public Formats getFormatOf( int fieldindex ) {
        return m_fieldformat[fieldindex];
    }
    
        // Find the Datas for the given field
    public Datas getDatasOf( int fieldindex ) {
        return m_FieldDataTypes[fieldindex];
    }
    
      /**
     *
     * @return
     */
    public IRenderString getRenderStringChange() {
        return new RenderStringStockChange( this );
    }
       
     /**
     *
     * @return
     */
    public final Row getChangesRow() {
        return m_changesRow;
    }
    
    /**
     *
     * @return
     */
    public final int getFieldCount() {
        return m_FieldNames.length;
    }
    
     public final PreparedSentence getChangesListbyLocation() {
         
         String[] fields =  new String[] { "LOCATION" };
         
 	return new PreparedSentence(m_session,
            "SELECT C.ID, C.LOCATION, C.USERNAME, C.UPLOADTIME, "
            + "C.PRODUCTID, C.CHANGETYPE, C.CHANGES_PROCESSED, C.FIELD, "
            + "C.TEXTVALUE, C.BLOBVALUE, COALESCE(P.NAME,'***NEW PRODUCT') AS PRODUCTNAME, P.REFERENCE AS PRODUCTREF "
            + "FROM STOCKCHANGES C LEFT JOIN PRODUCTS P ON (C.PRODUCTID = P.ID) "
            + "WHERE C.LOCATION = ? "
            + "ORDER BY C.LOCATION, P.NAME, C.PRODUCTID",
            new SerializerWriteBasicExt( 
                   m_changesRow.getDatas(), namesToIndexes( fields ) ),
                m_changesRow.getSerializerRead()
        );
    }

     public final PreparedSentence getChangesListbyDate( SerializerWrite serializerWrite) {
        
 	return new PreparedSentence(m_session,
            "SELECT C.ID, C.LOCATION, C.USERNAME, C.UPLOADTIME, "
            + "C.PRODUCTID, C.CHANGETYPE, C.CHANGES_PROCESSED, C.FIELD, "
            + "C.TEXTVALUE, C.BLOBVALUE, COALESCE(P.NAME,'***NEW PRODUCT') AS PRODUCTNAME, P.REFERENCE AS PRODUCTREF "
            + "FROM STOCKCHANGES C LEFT JOIN PRODUCTS P ON (C.PRODUCTID = P.ID) "
            + "WHERE C.UPLOADTIME > ? AND C.UPLOADTIME < ? "
            + "ORDER BY C.LOCATION, P.NAME, C.PRODUCTID",
            serializerWrite, m_changesRow.getSerializerRead()
        );
    }
     
         /**
     *
     * @return
     */
    public final SentenceExec getChangesInsert() {
        return new SentenceExecTransaction(m_session) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                String[] fields = new String[] {
                            "ID", "LOCATION", "USERNAME", "UPLOADTIME",
                            "PRODUCTID", "CHANGETYPE", "CHANGES_PROCESSED",
                            "FIELD", "TEXTVALUE", "BLOBVALUE"
                        };
                
		return new PreparedSentence(m_session
                    , "INSERT INTO STOCKCHANGES ( "
                    + "ID, LOCATION, USERNAME, UPLOADTIME, "
                    + "PRODUCTID, CHANGETYPE, CHANGES_PROCESSED, "
                    + "FIELD, TEXTVALUE, BLOBVALUE "
                    + " ) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
                    new SerializerWriteBasicExt(
                            m_changesRow.getDatas(), namesToIndexes( fields ) )
                         ).exec(params);
            }
        };
    }

    /**
     *
     * @return
     */
    public final SentenceExec getChangesUpdate() {
        return new SentenceExecTransaction(m_session) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                String[] fields = new String[] {
                            "LOCATION", "USERNAME", "UPLOADTIME",
                            "PRODUCTID", "CHANGETYPE", "CHANGES_PROCESSED",
                            "FIELD", "TEXTVALUE", "BLOBVALUE", "ID"
                        };
                
		return new PreparedSentence(m_session
                   , "UPDATE STOCKCHANGES SET "
                    + "LOCATION = ?, USERNAME = ?, UPLOADTIME = ?, "
                    + "PRODUCTID = ?, CHANGETYPE = ?, CHANGES_PROCESSED = ?, "
                    + "FIELD = ?, TEXTVALUE = ?, BLOBVALUE = ? "
                    + "WHERE ID = ? ",
                    new SerializerWriteBasicExt(
                            m_changesRow.getDatas(), namesToIndexes( fields ) )
                         ).exec(params);
            }
        };
    }

    /**
     *
     * @return
     */
    public final SentenceExec getChangesDelete() {
        return new SentenceExecTransaction(m_session) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                String[] fields =  new String[] { "ID" };

                return new PreparedSentence(m_session
                    , "DELETE FROM STOCKCHANGES WHERE ID = ?"
                    , new SerializerWriteBasicExt(
                             m_changesRow.getDatas(), namesToIndexes( fields ) )
                    ).exec(params);
            }
        };
    }

    /**
     *
     * @return
     */
    public final void ActionSql( String sql ) throws BasicException {
        
       final String [] statements = sql.split(";");
        
        SentenceExec sentence = new SentenceExecTransaction(m_session) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                int r = 0;
                for( int i = 0; i < statements.length; ++i ) {
                    String blank = statements[i].replaceAll("\\s+","");
                    if( blank.length() > 0 ) {
                        r = new PreparedSentence(m_session, statements[i], null ).exec(params);
                    }
                }
                return r;
            }
        };
        sentence.exec();
    }

}
