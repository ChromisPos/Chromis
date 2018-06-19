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

/**
 *
 *   
 */
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 *
 *   
 */
public class URLTest {

    /**
     *
     * @param argv
     */
    public static void main(String[] argv) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                JEditorPane jep = new JEditorPane();
                jep.setContentType("text/html");//set content as html
                jep.setText("Welcome to <a href='http://www.chromis.co.uk/'>uniCenta</a>.");

                jep.setEditable(false);//so its not editable
                jep.setOpaque(false);//so we dont see whit background

                jep.addHyperlinkListener(new HyperlinkListener() {
                    @Override
                    public void hyperlinkUpdate(HyperlinkEvent hle) {
                        if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                            Desktop desktop = Desktop.getDesktop();
                            try {
                                desktop.browse(hle.getURL().toURI());
                            } catch (URISyntaxException | IOException ex) {
                            }
                        }
                    }
                });


                JFrame f = new JFrame("HyperlinkListener");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.add(jep);
                f.pack();
                f.setVisible(true);
            }
        });
    }
}