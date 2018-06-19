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

import uk.chromis.data.loader.IKeyed;

/**
 *
 * @author adrianromero
 * Created on February 13, 2007, 10:13 AM
 *
 */
public class PromotionInfo implements IKeyed {
    
    private static final long serialVersionUID = 9032683595235L;
    private String m_sID;
    private String m_sName;
    private String m_sCriteria;
    private String m_sScript;
    private Boolean m_bIsEnabled;
    private Boolean m_bAllProducts;

    /** Creates a new instance of PromotionInfo */
    public PromotionInfo( String sID, String sName, String sCriteria,
            String sScript, Boolean bIsEnabled, Boolean bAllProducts )
    {
        m_sID = sID;
        m_sName = sName;
        m_sCriteria = sCriteria;
        m_sScript = sScript;
        m_bIsEnabled = bIsEnabled;
        m_bAllProducts = bAllProducts;
    }
    
    /**
     *
     * @return
     */
    public Object getKey() {
        return m_sID;
    }

    /**
     *
     * @param sID
     */
    public void setID(String sID) {
        m_sID = sID;
    }
    
    /**
     *
     * @return
     */
    public String getID() {
        return m_sID;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return m_sName;
    }
    
    /**
     *
     * @param sName
     */
    public void setName(String sName) {
        m_sName = sName;
    }  

    /**
     *
     * @return
     */
    public String getCriteria() {
        return m_sCriteria;
    }
    
    /**
     *
     * @param sCriteria
     */
    public void setCriteria(String sCriteria) {
        m_sCriteria = sCriteria;
    } 

    /**
     *
     * @return
     */
    public String getScript() {
        return m_sScript;
    }
    
    /**
     *
     * @param sScript
     */
    public void setScript(String sScript) {
        m_sScript = sScript;
    } 

    /**
     *
     * @return
     */
    public Boolean getIsEnabled() {
        return m_bIsEnabled;
    }
    
    /**
     *
     * @param sIsEnabled
     */
    public void setIsEnabled(Boolean sIsEnabled) {
        m_bIsEnabled = sIsEnabled;
    } 
    
    /**
     *
     * @return
     */
    public Boolean getAllProducts() {
        return m_bAllProducts;
    }
    
    /**
     *
     * @param bAllProducts
     */
    public void setAllProducts(Boolean bAllProducts) {
        m_bAllProducts = bAllProducts;
    } 
    
         
    public String toString(){
        return m_sName;
    }    
}

