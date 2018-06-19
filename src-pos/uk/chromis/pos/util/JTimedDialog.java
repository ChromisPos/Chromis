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

package uk.chromis.pos.util;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.Timer;

// this is a dialog that will dispose of itself after a given amount of time
// work in progress

public class JTimedDialog extends JDialog {

    private int lifeTime = 0;

 public JTimedDialog() {
        super();   
 }
    
// if lifeTime is set to zero, this behaves like a normal dialog
    public void setLifeTime(int millis) {
        lifeTime = millis;
    }

    public void setVisible(boolean b) {
        System.out.println("setVisible(" + b + ")");
        super.setVisible(b);
        if (b && lifeTime != 0) {
            Action disposeAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("disposing");
                    dispose();
                }
            };
            Timer t = new Timer(lifeTime, disposeAction);
            t.setRepeats(false);
            t.start();
        }
    }
}
