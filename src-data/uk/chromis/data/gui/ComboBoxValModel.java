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

package uk.chromis.data.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import uk.chromis.data.loader.IKeyGetter;
import uk.chromis.data.loader.KeyGetterBuilder;

/**
 *
 * @author  adrian
 */
public class ComboBoxValModel extends AbstractListModel implements ComboBoxModel {  
   
    private List m_aData;
    private IKeyGetter m_keygetter;
    private Object m_selected;
    
    /** Creates a new instance of ComboBoxValModel
     * @param aData
     * @param keygetter */
    public ComboBoxValModel(List aData, IKeyGetter keygetter) {        
        m_aData = aData;
        m_keygetter = keygetter;        
        m_selected = null;
    }

    /**
     *
     * @param aData
     */
    public ComboBoxValModel(List aData) {
        this(aData, KeyGetterBuilder.INSTANCE);
    }

    /**
     *
     * @param keygetter
     */
    public ComboBoxValModel(IKeyGetter keygetter) {
        this(new ArrayList(), keygetter);
    }

    /**
     *
     */
    public ComboBoxValModel() {
        this(new ArrayList(), KeyGetterBuilder.INSTANCE);
    }
    
    /**
     *
     * @param c
     */
    public void add(Object c) {
        m_aData.add(c);
    }

    /**
     *
     * @param c
     */
    public void del(Object c) {
        m_aData.remove(c);
    }

    /**
     *
     * @param index
     * @param c
     */
    public void add(int index, Object c) {
        m_aData.add(index, c);
    }
    
    /**
     *
     * @param aData
     */
    public void refresh(List aData) {
        m_aData = aData;
        m_selected = null;
    }
    
    /**
     *
     * @return
     */
    public Object getSelectedKey() {
        if (m_selected == null) {
            return null;
        } else {
            return m_keygetter.getKey(m_selected);  // Si casca, excepcion parriba
        }
    }

    /**
     *
     * @return
     */
    public String getSelectedText() {
        if (m_selected == null) {
            return null;
        } else {
            return m_selected.toString();
        }
    }
    
    /**
     *
     * @param aKey
     */
    public void setSelectedKey(Object aKey) {
        setSelectedItem(getElementByKey(aKey));
    }
    
    /**
     *
     */
    public void setSelectedFirst() {
        m_selected = (m_aData.isEmpty()) ? null : m_aData.get(0);
    }
    
    /**
     *
     * @param aKey
     * @return
     */
    public Object getElementByKey(Object aKey) {
        if (aKey != null) {
            Iterator it = m_aData.iterator();
            while (it.hasNext()) {
                Object value = it.next();
                if (aKey.equals(m_keygetter.getKey(value))) {
                    return value;
                }
            }           
        }
        return null;
    }
    
    @Override
    public Object getElementAt(int index) {
        return m_aData.get(index);
    }
    
    @Override
    public Object getSelectedItem() {
        return m_selected;
    }
    
    @Override
    public int getSize() {
        return m_aData.size();
    }
    
    @Override
    public void setSelectedItem(Object anItem) {
        
        if ((m_selected != null && !m_selected.equals(anItem)) || m_selected == null && anItem != null) {
            m_selected = anItem;
            fireContentsChanged(this, -1, -1);
        }
    }

   
}
