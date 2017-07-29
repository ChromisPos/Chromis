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

package uk.chromis.pos.printer.screen;

import java.awt.Color;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.printer.DeviceDisplay;
import uk.chromis.pos.printer.DeviceDisplayAdvance;
import uk.chromis.pos.sales.JTicketLines;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author JohnL, Wildfox, ChrisJ & Fanzam
 */

public class DeviceDisplayScreen extends javax.swing.JFrame implements DeviceDisplay, DeviceDisplayAdvance {

    private final String m_sName;
    private final DeviceDisplayPanel m_display;
    
    JPanel m_jContainer = new JPanel(); 
    JLabel m_jImage = new JLabel();
    JPanel m_jListContainer = new JPanel(); 
     JFrame display_frame = new JFrame(); 
    
  
    /**
     * Creates new form DeviceDisplayWindow
     * FanZam FullScreen version Layout 01/03/16
     */
    
    public DeviceDisplayScreen() {

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] screens = ge.getScreenDevices();
    int n = screens.length;
    for (int i = 0; i < n; i++) {
   
            JFrame dualview = new JFrame(screens[i].getDefaultConfiguration());
            display_frame.setLocationRelativeTo(dualview);
            dualview.dispose();
   
    }
    
        display_frame.setUndecorated(true);
        m_sName = AppLocal.getIntString("Display.Window");
        m_display = new DeviceDisplayPanel(3.0);

        m_jContainer.setLayout(new MigLayout());
        m_jContainer.add(m_display.getDisplayComponent(), "pushx, growx");
        m_jContainer.add(m_jImage, "wrap");
        m_jListContainer.setLayout(new java.awt.BorderLayout());
        m_jContainer.add(m_jListContainer, "span, push, grow, alignx center");
  
        
       display_frame.add(m_jContainer); 
       display_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
       display_frame.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.black));
       display_frame.pack(); 
        
    
        display_frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        display_frame.setVisible(true);
    }
    
 

    /**
     *
     * @return
     */
    @Override
    public String getDisplayName() {
        return m_sName;
    }

    /**
     *
     * @return
     */
    @Override
    public String getDisplayDescription() {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public JComponent getDisplayComponent() {
        return null;
    }

    /**
     *
     * @param animation
     * @param sLine1
     * @param sLine2
     */
    @Override
    public void writeVisor(int animation, String sLine1, String sLine2) {
        m_display.writeVisor(animation, sLine1, sLine2);
    }

    /**
     *
     * @param sLine1
     * @param sLine2
     */
    @Override
    public void writeVisor(String sLine1, String sLine2) {
        m_display.writeVisor(sLine1, sLine2);
    }

    /**
     *
     */
    @Override
    public void clearVisor() {
        m_display.clearVisor();
    }

    @Override
    public boolean hasFeature(int feature) {
        // for support of product_image only
        //return(((DeviceDisplayAdvance.PRODUCT_IMAGE) & feature)>0); 

        // for support of product image and ticketlines list
        return (((DeviceDisplayAdvance.PRODUCT_IMAGE | DeviceDisplayAdvance.TICKETLINES) & feature) > 0);
    }

    @Override
    public boolean setProductImage(BufferedImage img) {
        BufferedImage resizeImg = resize(img, 160, 160);
        m_jImage.setIcon(img == null ? null : new ImageIcon(resizeImg));
        return true;
    }

    @Override
    public boolean setTicketLines(JTicketLines ticketlines) {
        m_jListContainer.add(ticketlines);
        return true;
    }

    private static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = (Graphics2D) bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }
}