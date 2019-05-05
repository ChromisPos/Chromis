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

package uk.chromis.pos.promotion;

import java.util.List;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SentenceExec;
import uk.chromis.data.loader.SentenceExecTransaction;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerRead;
import uk.chromis.data.loader.SerializerReadString;
import uk.chromis.data.loader.SerializerWriteBasicExt;
import uk.chromis.data.loader.SerializerWriteString;
import uk.chromis.data.loader.Session;
import uk.chromis.data.loader.StaticSentence;
import uk.chromis.data.model.Field;
import uk.chromis.data.model.Row;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.BeanFactoryDataSingle;

public class DataLogicPromotions extends BeanFactoryDataSingle {
    
    protected Session m_session;
    
    protected Row m_PromotionRow;
    protected Row m_ProductRow;
    
    // These next arrays are order dependant.
    // Use the provided getIndexOf and namesToIndexes functions
    // to access the columns, do not hard code array indexes.
    // If you add a new field, all arrays need a new entry.
    protected String[] m_PromotionFieldNames = new String[] {
                "ID", "NAME", "CRITERIA", "SCRIPT", "ISENABLED", "ALLPRODUCTS", "SITEGUID"
            };          
    
    protected static int INDEX_ID = 0;
    protected static int INDEX_NAME = 1;
    protected static int INDEX_CRITERIA = 2;
    protected static int INDEX_SCRIPT = 3;
    protected static int INDEX_ISENABLED = 4;
    protected static int INDEX_ALLPRODUCTS = 5;
    protected static int INDEX_SITEGUID = 6;
    protected static int FIELD_COUNT = 7;
    
    private Datas[] m_PromotionFieldDataTypes = new Datas[] 
        {Datas.STRING, Datas.STRING, Datas.SERIALIZABLE,
                Datas.SERIALIZABLE, Datas.BOOLEAN,  Datas.BOOLEAN, Datas.STRING
            };
    
    private Formats[] m_PromotionFieldFormat = 
            new Formats[] {Formats.STRING, Formats.STRING, Formats.BYTEA,
                Formats.BYTEA, Formats.BOOLEAN, Formats.BOOLEAN, Formats.STRING
            };
    
    /** Creates a new instance of DataLogicPromotions */
    public DataLogicPromotions() {  
        m_PromotionRow = new Row(
                new Field(m_PromotionFieldNames[0], m_PromotionFieldDataTypes[0], m_PromotionFieldFormat[0] ), 
                new Field(m_PromotionFieldNames[1], m_PromotionFieldDataTypes[1], m_PromotionFieldFormat[1], true, true, true ), 
                new Field(m_PromotionFieldNames[2], m_PromotionFieldDataTypes[2], m_PromotionFieldFormat[2] ), 
                new Field(m_PromotionFieldNames[3], m_PromotionFieldDataTypes[3], m_PromotionFieldFormat[3] ), 
                new Field(m_PromotionFieldNames[4], m_PromotionFieldDataTypes[4], m_PromotionFieldFormat[4] ),
                new Field(m_PromotionFieldNames[5], m_PromotionFieldDataTypes[5], m_PromotionFieldFormat[5] ),
                new Field(m_PromotionFieldNames[6], m_PromotionFieldDataTypes[6], m_PromotionFieldFormat[6] ) 
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
        int i = m_PromotionFieldNames.length - 1;
        while( i > 0 && !m_PromotionFieldNames[i].contentEquals(fieldname)) {
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
            datas[i] = m_PromotionFieldDataTypes[ getIndexOf( fieldnames[i] ) ];
        }
        return datas;
    }
    
    // Find the format for the given field
    public Formats getFormatOf( int fieldindex ) {
        return m_PromotionFieldFormat[fieldindex];
    }
    
        // Find the Datas for the given field
    public Datas getDatasOf( int fieldindex ) {
        return m_PromotionFieldDataTypes[fieldindex];
    }
 
     /**
     *
     * @return
     */
    public final Row getRow() {
        return m_PromotionRow;
    }
    
    /**
     *
     * @return
     */
    public final int getFieldCount() {
        return m_PromotionFieldNames.length;
    }
    
         
    public final SentenceList getResourceScriptListSentence(String siteGuid) {

        return new StaticSentence(m_session,
                "SELECT NAME FROM RESOURCES WHERE SITEGUID = '"
                + siteGuid
                + "' AND NAME LIKE 'promotion.%' ORDER BY NAME ", null, SerializerReadString.INSTANCE);         
             
    }
    
    public final PreparedSentence getListSentence() {
 	return new PreparedSentence(m_session,
            "SELECT ID, NAME, CRITERIA, SCRIPT, ISENABLED, ALLPRODUCTS FROM PROMOTIONS",
            null,
            m_PromotionRow.getSerializerRead()
        );
    }

    static int INDEX_PROMOTEDPRODUCT_ID = 0;
    static int INDEX_PROMOTEDPRODUCT_REFERENCE = 1;
    static int INDEX_PROMOTEDPRODUCT_NAME = 2;
    static int INDEX_PROMOTEDPRODUCT_PROMOTIONID = 3;
            
    public final PreparedSentence getPromotedProductsSentence( String PromotionID, String sqlWhere ) {
        
        if( m_ProductRow == null ) { 
           m_ProductRow = new Row(
                new Field("PRODUCTID", Datas.STRING, Formats.STRING ), 
                new Field("REFERENCE", Datas.STRING, Formats.STRING ),
                new Field("NAME", Datas.STRING, Formats.STRING ),
                new Field("PROMOTIONID", Datas.STRING, Formats.STRING ) );
        }
        
        String sql =  "SELECT PRODUCTS.ID AS PRODUCTID, PRODUCTS.REFERENCE, PRODUCTS.NAME, PRODUCTS.PROMOTIONID " + 
            "FROM PRODUCTS " +
            "LEFT JOIN CATEGORIES ON (PRODUCTS.CATEGORY = CATEGORIES.ID) " +
            "LEFT JOIN TAXCATEGORIES ON (PRODUCTS.TAXCAT = TAXCATEGORIES.ID) "
            + "WHERE PRODUCTS.PROMOTIONID='" + PromotionID + "'";
        
        if( sqlWhere != null && !sqlWhere.isEmpty() ) {
            sql = sql + " OR (" + sqlWhere + ")";
        }
        
 	return new PreparedSentence(m_session, sql, null,
            m_ProductRow.getSerializerRead()
        );
    }

    /**
     *
     * @return
     */
    public final SentenceExec getInsertSentence() {
        return new SentenceExecTransaction(m_session) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                String[] fields = new String[] {
                            "ID", "NAME", "CRITERIA", "SCRIPT",
                            "ISENABLED", "ALLPRODUCTS"
                        };
                
		return new PreparedSentence(m_session
                    , "INSERT INTO PROMOTIONS ( "
                    + "ID, NAME, CRITERIA, SCRIPT, ISENABLED, ALLPRODUCTS ) "
                    + "VALUES (?, ?, ?, ?, ?, ?)", 
                    new SerializerWriteBasicExt(
                            m_PromotionRow.getDatas(), namesToIndexes( fields ) )
                         ).exec(params);
            }
        };
    }
    
    /**
     *
     * @return
     */
    public final SentenceExec getUpdateSentence() {
        return new SentenceExecTransaction(m_session) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                String[] fields = new String[] {
                            "NAME", "CRITERIA", "SCRIPT",
                            "ISENABLED", "ALLPRODUCTS", "ID"
                        };
                
		return new PreparedSentence(m_session
                   , "UPDATE PROMOTIONS SET "
                    + "NAME = ?, CRITERIA = ?, "
                    + "SCRIPT = ?, ISENABLED = ?, ALLPRODUCTS = ? "
                    + "WHERE ID = ? ",
                    new SerializerWriteBasicExt(
                            m_PromotionRow.getDatas(), namesToIndexes( fields ) )
                         ).exec(params);
            }
        };
    }

    /**
     *
     * @return
     */
    public final SentenceExec getDeleteSentence() {
        return new SentenceExecTransaction(m_session) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                String[] fields =  new String[] { "ID" };

                return new PreparedSentence(m_session
                    , "DELETE FROM PROMOTIONS WHERE ID = ?"
                    , new SerializerWriteBasicExt(
                             m_PromotionRow.getDatas(), namesToIndexes( fields ) )
                    ).exec(params);
            }
        };
    }

    public void resetPromotionID( String promotionID, List<String> aProductIDs ) throws BasicException {
        
        new PreparedSentence( m_session,
           "UPDATE PRODUCTS SET PROMOTIONID = NULL WHERE PROMOTIONID = '"
           + promotionID + "'",
           null).exec();

       if( aProductIDs.size() > 0 ) {
           String sql = "UPDATE PRODUCTS SET PROMOTIONID = '" 
                + promotionID + "' WHERE ID IN ( ";   
           for( String s : aProductIDs ) {
               sql = sql + s + ",";
           }
           sql = sql + "'-' ) ";   
           new PreparedSentence( m_session, sql, null).exec();
       }
    }
 
    /**
     *
     * @return
     */
    public static SerializerRead getPromotionsSerializerRead() {
        return new SerializerRead() {@Override
        public Object readValues(DataRead dr) throws BasicException {
            
            return new PromotionInfo(
                    dr.getString(INDEX_ID+1), dr.getString(INDEX_NAME+1),
                    Formats.BYTEA.formatValue( Datas.SERIALIZABLE.getValue(dr, INDEX_CRITERIA+1) ),
                    Formats.BYTEA.formatValue( Datas.SERIALIZABLE.getValue(dr, INDEX_SCRIPT+1) ),
                    dr.getBoolean(INDEX_ISENABLED+1), dr.getBoolean(INDEX_ALLPRODUCTS+1) );
            }
        };
    }    

    // Get the promotion IDs for all enabled promotions that act on all products
    public List<PromotionInfo> getAllProductPromotions() throws BasicException {
        return new PreparedSentence( m_session,
                "SELECT ID, NAME, CRITERIA, SCRIPT, ISENABLED, ALLPRODUCTS FROM PROMOTIONS "
                + "WHERE ISENABLED = " + m_session.DB.TRUE()
                + " AND ALLPRODUCTS = " + m_session.DB.TRUE(),
                null,
                getPromotionsSerializerRead() ).list();
    }
       
    /**
        *
        * @param id
        * @return
        * @throws BasicException
        */
       public final PromotionInfo getPromotionInfo(String id) throws BasicException {
           return (PromotionInfo) new PreparedSentence( m_session, "SELECT "
                    + "ID, NAME, CRITERIA, SCRIPT, ISENABLED, ALLPRODUCTS "
                    + "FROM PROMOTIONS "
                    + "WHERE ID = ?",
                   SerializerWriteString.INSTANCE, 
                   getPromotionsSerializerRead())
                   .find(id);
       }

}

