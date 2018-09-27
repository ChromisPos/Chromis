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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author John
 */
public class AppConfig implements AppProperties {

    private static AppConfig instance = null;
    private final Properties m_propsconfig;
    private final File configFile;
    private static final Logger logger = Logger.getLogger("uk.chromis.pos.forms.AppConfig");
    private DataLogicSystem dlSystem;

    class SortedProperties extends Properties {

        public Enumeration keys() {
            Enumeration keysEnum = super.keys();
            Vector<String> keyList = new Vector<String>();
            while (keysEnum.hasMoreElements()) {
                keyList.add((String) keysEnum.nextElement());
            }
            Collections.sort(keyList);
            return keyList.elements();
        }
    }

    protected AppConfig(File configFile) {
        this.configFile = configFile;
        m_propsconfig = new Properties();
        load();
        logger.log(Level.INFO, "Reading configuration file: {0}", configFile.getAbsolutePath());
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig(new File(System.getProperty("user.home"), AppLocal.APP_ID + ".properties"));
        }
        return instance;
    }

    private File getDefaultConfig() {
        return new File(new File(System.getProperty("user.home")), AppLocal.APP_ID + ".properties");
    }

    public String getDirPath() {
        String dirname = System.getProperty("dirname.path");
        return (dirname == null ? "./" : dirname);
    }

    public void setProperty(String sKey, String sValue) {
        if (sValue == null) {
            m_propsconfig.remove(sKey);
        } else {
            m_propsconfig.setProperty(sKey, sValue);
        }
    }

    public String getProperty(String sKey) {
        return m_propsconfig.getProperty(sKey);
    }

    public void setBoolean(String sKey, Boolean sValue) {
        if (sValue == null) {
            m_propsconfig.remove(sKey);
        } else if (sValue) {
            m_propsconfig.setProperty(sKey, "true");
        } else {
            m_propsconfig.setProperty(sKey, "false");
        }
    }

    public Boolean getBoolean(String sKey) {
        return Boolean.valueOf(m_propsconfig.getProperty(sKey));
    }

    public Double getDouble(String sKey) {
        if (getProperty(sKey) != null) {
            return Double.valueOf(m_propsconfig.getProperty(sKey));
        } else {
            return 0.00;
        }
    }

    public boolean delete() {
        loadDefault();
        return configFile.delete();
    }

    public void load() {
        loadDefault();
        try {
            InputStream in = new FileInputStream(configFile);
            if (in != null) {
                m_propsconfig.load(in);
                in.close();
            }
        } catch (IOException e) {
            loadDefault();
        }
    }

    public void save() throws IOException {
        Set set = m_propsconfig.entrySet();
        SortedProperties sortedpropsconfig = new SortedProperties();

        Iterator i = set.iterator();
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            sortedpropsconfig.put(me.getKey().toString(), me.getValue().toString());
        }

        OutputStream out = new FileOutputStream(configFile);
        if (out != null) {
            //m_propsconfig.store(out, AppLocal.APP_NAME + ". Configuration file.");
            sortedpropsconfig.store(out, AppLocal.APP_NAME + ". Configuration file.");
            out.close();
        }
    }

    /**
     *
     * @return 0 or 00 number keypad boolean true/false
     */
    public Boolean isPriceWith00() {
        String prop = getProperty("pricewith00");
        if (prop == null) {
            return false;
        } else {
            return prop.equals("true");
        }
    }

    private String getLocalHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (java.net.UnknownHostException eUH) {
            return "localhost";
        }
    }

    public String getHost() {
        return getProperty("machine.hostname");
    }

    private void loadDefault() {
/*
        String dirname = System.getProperty("dirname.path");
        dirname = dirname == null ? "./" : dirname;

        m_propsconfig.setProperty("db.driverlib", new File(new File(dirname), "lib/derby.jar").getAbsolutePath());
        m_propsconfig.setProperty("db.driver", "org.apache.derby.jdbc.EmbeddedDriver");
        m_propsconfig.setProperty("db.URL", "jdbc:derby:" + new File(new File(System.getProperty("user.home")), AppLocal.APP_ID + "-database").getAbsolutePath() + ";create=true");
        m_propsconfig.setProperty("db.user", "");
        m_propsconfig.setProperty("db.password", "");
*/
        /**
         *
         * Default component settings
         */
        m_propsconfig.setProperty("machine.hostname", getLocalHostName());

        Locale l = Locale.getDefault();
        m_propsconfig.setProperty("user.language", l.getLanguage());
        m_propsconfig.setProperty("user.country", l.getCountry());
        m_propsconfig.setProperty("user.variant", l.getVariant());
        m_propsconfig.setProperty("swing.defaultlaf", System.getProperty("swing.defaultlaf", "javax.swing.plaf.metal.MetalLookAndFeel"));
        m_propsconfig.setProperty("machine.printer", "screen");
        m_propsconfig.setProperty("machine.printer.2", "Not defined");
        m_propsconfig.setProperty("machine.printer.3", "Not defined");
        m_propsconfig.setProperty("machine.printer.4", "Not defined");
        m_propsconfig.setProperty("machine.printer.5", "Not defined");
        m_propsconfig.setProperty("machine.printer.6", "Not defined");

        m_propsconfig.setProperty("machine.display", "Not defined");
        m_propsconfig.setProperty("machine.scale", "Not defined");
        m_propsconfig.setProperty("machine.screenmode", "window"); // fullscreen / window
        m_propsconfig.setProperty("machine.ticketsbag", "standard");
        m_propsconfig.setProperty("machine.scanner", "Not defined");

        m_propsconfig.setProperty("payment.gateway", "external");
        m_propsconfig.setProperty("payment.magcardreader", "Not defined");
        m_propsconfig.setProperty("payment.testmode", "false");
        m_propsconfig.setProperty("payment.commerceid", "");
        m_propsconfig.setProperty("payment.commercepassword", "password");

        m_propsconfig.setProperty("machine.printername", "(Default)");

        // Receipt printer paper set to 72mmx200mm
        m_propsconfig.setProperty("paper.receipt.x", "10");
        m_propsconfig.setProperty("paper.receipt.y", "10");
//        m_propsconfig.setProperty("paper.receipt.x", "10");
//        m_propsconfig.setProperty("paper.receipt.y", "287");
        m_propsconfig.setProperty("paper.receipt.width", "190");
        m_propsconfig.setProperty("paper.receipt.height", "546");
        m_propsconfig.setProperty("paper.receipt.mediasizename", "A4");

        // Normal printer paper for A4
        m_propsconfig.setProperty("paper.standard.x", "72");
        m_propsconfig.setProperty("paper.standard.y", "72");
        m_propsconfig.setProperty("paper.standard.width", "451");
        m_propsconfig.setProperty("paper.standard.height", "698");
        m_propsconfig.setProperty("paper.standard.mediasizename", "A4");

        m_propsconfig.setProperty("machine.uniqueinstance", "false");
        m_propsconfig.setProperty("screen.receipt.columns", "42");

    }

    @Override
    public File getConfigFile() {
        return configFile;
    }

}
