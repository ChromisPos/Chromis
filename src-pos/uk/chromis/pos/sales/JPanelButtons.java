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

package uk.chromis.pos.sales;

import java.awt.Component;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.chromis.data.loader.LocalRes;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppUser;
import uk.chromis.pos.util.ThumbNailBuilder;

/**
 *
 *
 */
public class JPanelButtons extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger("uk.chromis.pos.sales.JPanelButtons");

    private static SAXParser m_sp = null;

    private Properties props;
    private Map<String, String> events;

    private ThumbNailBuilder tnbmacro;

    private JPanelTicket panelticket;

    /**
     * Creates new form JPanelButtons
     *
     * @param sConfigKey
     * @param panelticket
     */
    public JPanelButtons(String sConfigKey, JPanelTicket panelticket) {
        initComponents();

        // Load categories default thumbnail
        tnbmacro = new ThumbNailBuilder(18, 18, "uk/chromis/images/run_script.png");

        this.panelticket = panelticket;

        props = new Properties();
        events = new HashMap<>();

        // get the ticket.buttons resource from the resource table
        String sConfigRes = panelticket.getResourceAsXML(sConfigKey);

        if (sConfigRes != null) {
            try {
                if (m_sp == null) {
                    SAXParserFactory spf = SAXParserFactory.newInstance();
                    m_sp = spf.newSAXParser();
                }
                m_sp.parse(new InputSource(new StringReader(sConfigRes)), new ConfigurationHandler());

            } catch (ParserConfigurationException ePC) {
                logger.log(Level.WARNING, LocalRes.getIntString("exception.parserconfig"), ePC);
            } catch (SAXException eSAX) {
                logger.log(Level.WARNING, LocalRes.getIntString("exception.xmlfile"), eSAX);
            } catch (IOException eIO) {
                logger.log(Level.WARNING, LocalRes.getIntString("exception.iofile"), eIO);
            }
        }
    }

    /**
     *
     * @param user
     */
    public void setPermissions(AppUser user) {
        for (Component c : this.getComponents()) {
            String sKey = c.getName();
            if (sKey == null || sKey.equals("")) {
                c.setEnabled(true);
            } else {
                c.setEnabled(user.hasPermission(c.getName()));
            }
        }
    }

    /**
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        return props.getProperty(key);
    }

    /**
     *
     * @param key
     * @param defaultvalue
     * @return
     */
    public String getProperty(String key, String defaultvalue) {
        return props.getProperty(key, defaultvalue);
    }

    /**
     *
     * @param key
     * @return
     */
    public String getEvent(String key) {
        return events.get(key);
    }

    private class ConfigurationHandler extends DefaultHandler {

        @Override
        public void startDocument() throws SAXException {
        }

        @Override
        public void endDocument() throws SAXException {
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            switch (qName) {                
                case "button":
                    // The button title text
                    String titlekey = attributes.getValue("titlekey");
                    if (titlekey == null) {
                        titlekey = attributes.getValue("name");
                    }
                    String title = titlekey == null
                            ? attributes.getValue("title")
                            : AppLocal.getIntString(titlekey);
                    // adding the button to the panel                  
                    JButton btn = new JButtonFunc(attributes.getValue("key"),
                            attributes.getValue("image"),
                            title);
                    // The template resource or the code resource
                    final String template = attributes.getValue("template");
                    if (template == null) {
                        final String code = attributes.getValue("code");
                        btn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                panelticket.evalScriptAndRefresh(code);
                            }
                        });
                    } else {
                        btn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                panelticket.printTicket(template);
                            }
                        });
                    }
                    add(btn);
                    break;
                case "event":
                    events.put(attributes.getValue("key"), attributes.getValue("code"));
                    break;
                default:
                    String value = attributes.getValue("value");
                    if (value != null) {
                        props.setProperty(qName, attributes.getValue("value"));
                    }
                    break;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
        }
    }

    //java.net.URL imgURL = getClass().getResource("/images/image.jpg");
    //ImageIcon image = new ImageIcon(imgURL);
    private class JButtonFunc extends JButton {

        public JButtonFunc(String sKey, String sImage, String title) {
            setName(sKey);
            setText(title);
            java.net.URL imgURL=null;
            // allows for the use of a images from the image class file to be used
            if (sImage != null){
            imgURL = getClass().getResource(sImage);
            }            
            if (imgURL == null) {
                setIcon(new ImageIcon(tnbmacro.getThumbNail(panelticket.getResourceAsImage(sImage))));
            } else {
                Image image = new ImageIcon(imgURL).getImage();
                setIcon(new ImageIcon(image.getScaledInstance(18,18, java.awt.Image.SCALE_SMOOTH)));
            }
            setFocusPainted(false);
            setFocusable(false);
            setRequestFocusEnabled(false);
            setMargin(new Insets(8, 14, 8, 14));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
