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

import java.awt.Color;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.border.MatteBorder;

/**
 *
 * @author adrianromero
 */
public class MenuTitleDefinition implements MenuElement {
    
    /**
     *
     */
    public String KeyText;
    
    /**
     *
     * @param menu
     */
    @Override
    public void addComponent(JPanelMenu menu) {
       
//        if (p.x > 20) {
//            p.x = 20;
//            p.y += 55;
//        }
//        p.y += 20; // 20 puntitos extra de separacion.
        
        JLabel lbl = new JLabel(AppLocal.getIntString(KeyText));
        lbl.applyComponentOrientation(menu.getComponentOrientation());
        lbl.setBorder(new MatteBorder(new Insets(0, 0, 1, 0), new Color(0, 0, 0)));
//        lbl.setSize(670, 20);
//        lbl.setLocation(p);
//        p.y += 35;
//        comp.add(lbl);  
        
        menu.addTitle(lbl);
    }  
}
