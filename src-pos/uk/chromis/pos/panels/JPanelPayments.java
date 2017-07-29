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

package uk.chromis.pos.panels;

import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProvider;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicSales;

/**
 *
 * @author adrianromero
 */
public class JPanelPayments extends JPanelTable {
    
    private PaymentsEditor jeditor;    
    private DataLogicSales m_dlSales = null;
    
    /** Creates a new instance of JPanelPayments */
    public JPanelPayments() {
    }
    
    /**
     *
     */
    @Override
    protected void init() {
        m_dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");         
        jeditor = new PaymentsEditor(app, dirty);    
    }
    
    /**
     *
     * @return
     */
    @Override
    public ListProvider getListProvider() {
        return null;
    }
    
    /**
     *
     * @return
     */
    @Override
    public SaveProvider getSaveProvider() {
        return  new SaveProvider(null
                , m_dlSales.getPaymentMovementInsert()
                , m_dlSales.getPaymentMovementDelete());
    }
    
    /**
     *
     * @return
     */
    @Override
    public EditorRecord getEditor() {
        return jeditor;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Payments");
    }    
}
