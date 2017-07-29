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


package uk.chromis.pos.thirdparties;

import javax.swing.ListCellRenderer;
import uk.chromis.data.gui.ListCellRendererBasic;
import uk.chromis.data.loader.ComparatorCreator;
import uk.chromis.data.loader.TableDefinition;
import uk.chromis.data.loader.Vectorer;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.ListProvider;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.panels.JPanelTable;

/**
 *
 *   
 */
public class ThirdPartiesPanel extends JPanelTable {
    
    private TableDefinition tthirdparties;
    private ThirdPartiesView jeditor;
    
    /** Creates a new instance of JPanelPeople */
    public ThirdPartiesPanel() {
    }
    
    /**
     *
     */
    protected void init() {
        DataLogicThirdParties dlThirdParties = (DataLogicThirdParties) app.getBean("uk.chromis.pos.thirdparties.DataLogicThirdParties");        
        tthirdparties = dlThirdParties.getTableThirdParties();        
        jeditor = new ThirdPartiesView(app, dirty);       
    }
    
    /**
     *
     * @return
     */
    public ListProvider getListProvider() {
        return new ListProviderCreator(tthirdparties);
    }
    
    /**
     *
     * @return
     */
    public SaveProvider getSaveProvider() {
        return new SaveProvider(tthirdparties);      
    }
    
    /**
     *
     * @return
     */
    public Vectorer getVectorer() {
        return tthirdparties.getVectorerBasic(new int[]{1, 2, 3, 4});
    }
    
    /**
     *
     * @return
     */
    public ComparatorCreator getComparatorCreator() {
        return tthirdparties.getComparatorCreator(new int[] {1, 2, 3, 4});
    }
    
    /**
     *
     * @return
     */
    public ListCellRenderer getListCellRenderer() {
        return new ListCellRendererBasic(tthirdparties.getRenderStringBasic(new int[]{1, 2}));
    }
    
    /**
     *
     * @return
     */
    public EditorRecord getEditor() {
        return jeditor;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return AppLocal.getIntString("Menu.ThirdPartiesManagement");
    }     
}
