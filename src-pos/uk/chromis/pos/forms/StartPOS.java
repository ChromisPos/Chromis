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
import uk.chromis.pos.dbmanager.RunRepair;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import uk.chromis.format.Formats;
import uk.chromis.pos.ticket.TicketInfo;
import javax.swing.JFrame;
import uk.chromis.pos.dbmanager.DbManager;
import uk.chromis.pos.util.AltEncrypter;
import uk.chromis.pos.util.DbUtils;
import uk.chromis.pos.util.OSValidator;

public class StartPOS {

    private static final Logger logger = Logger.getLogger("uk.chromis.pos.forms.StartPOS");
    private static ServerSocket serverSocket;
    public static final int INIT_SUCCESS = 0;
    public static final int INIT_FAIL_CONFIG = 1;
    public static final int INIT_FAIL_EXIT = 2;
    public static final int INIT_FAIL_RETRY = 3;

    private StartPOS() {
    }

    public static boolean registerApp() {
        // prevent multiple instances running on same machine, Socket is never used in app
        try {
            serverSocket = new ServerSocket(65326);
        } catch (IOException ex) {
            return false;
        }
        return true;

    }

    public static void main(final String args[]) {
        String currentPath = null;
        if (!registerApp()) {
            System.out.println("Already Running");
            System.exit(0);
        }
        currentPath = System.getProperty("user.dir");
        System.out.println("Current Directory : " + currentPath);

        File newIcons = null;
        if (AppConfig.getInstance().getProperty("icon.colour") == null || AppConfig.getInstance().getProperty("icon.colour").equals("")) {
            newIcons = new File(currentPath + "/Icon sets/blue/images.jar");
        } else {
            newIcons = new File(currentPath + "/Icon sets/" + AppConfig.getInstance().getProperty("icon.colour") + "/images.jar");
        }

        try {
            URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method m = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            m.setAccessible(true);
            m.invoke(urlClassLoader, newIcons.toURI().toURL());
            String cp = System.getProperty("java.class.path");
            if (cp != null) {
                cp += File.pathSeparatorChar + newIcons.getCanonicalPath();
            } else {
                cp = newIcons.toURI().getPath();
            }
            System.setProperty("java.class.path", cp);
        } catch (Exception ex) {
        }

        /*
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException localInterruptedException) {
            }
           /*
            if (AppConfig.getInstance().getProperty("icon.colour") != null) {
                try {
                   // FileUtils.copyFileToDirectory(newIcons, icons);
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }*/
        // }
        DbUtils.checkJava();

        DbManager manager = new DbManager(false);

        if (!manager.DBChecks()) {
            System.exit(0);
        }

        startApp();

    }

    public static void startApp() {
        // check if there are any repair scripts to run       
        String db_password = (AppConfig.getInstance().getProperty("db.password"));
        if (AppConfig.getInstance().getProperty("db.user") != null && db_password != null && db_password.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + AppConfig.getInstance().getProperty("db.user"));
            db_password = cypher.decrypt(db_password.substring(6));
        }
        RunRepair.Process(AppConfig.getInstance().getProperty("db.user"), AppConfig.getInstance().getProperty("db.URL"), db_password);

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                AppConfig config = AppConfig.getInstance();
                // set Locale.
                String slang = AppConfig.getInstance().getProperty("user.language");
                String scountry = AppConfig.getInstance().getProperty("user.country");
                String svariant = AppConfig.getInstance().getProperty("user.variant");
                if (slang != null && !slang.equals("") && scountry != null && svariant != null) {
                    Locale.setDefault(new Locale(slang, scountry, svariant));
                }

                // Set the format patterns
                Formats.setIntegerPattern(AppConfig.getInstance().getProperty("format.integer"));
                Formats.setDoublePattern(AppConfig.getInstance().getProperty("format.double"));
                Formats.setCurrencyPattern(AppConfig.getInstance().getProperty("format.currency"));
                Formats.setPercentPattern(AppConfig.getInstance().getProperty("format.percent"));
                Formats.setDatePattern(AppConfig.getInstance().getProperty("format.date"));
                Formats.setTimePattern(AppConfig.getInstance().getProperty("format.time"));
                Formats.setDateTimePattern(AppConfig.getInstance().getProperty("format.datetime"));

                // Set the look and feel.
                try {

                    Object laf = Class.forName(AppConfig.getInstance().getProperty("swing.defaultlaf")).newInstance();
                    if (laf instanceof LookAndFeel) {
                        UIManager.setLookAndFeel((LookAndFeel) laf);
                    } else if (laf instanceof SubstanceSkin) {
                        SubstanceLookAndFeel.setSkin((SubstanceSkin) laf);
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                    logger.log(Level.WARNING, "Cannot set Look and Feel", e);
                }
                String hostname = AppConfig.getInstance().getProperty("machine.hostname");
                TicketInfo.setHostname(hostname);

                String screenmode = AppConfig.getInstance().getProperty("machine.screenmode");
                if ("fullscreen".equals(screenmode)) {
                    JRootKiosk rootkiosk = new JRootKiosk();
                    rootkiosk.initFrame(config);
                } else if ("windowmaximised".equals(screenmode)) {
                    JRootFrame rootframe = new JRootFrame();
                    rootframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    rootframe.initFrame(config);
                } else {
                    JRootFrame rootframe = new JRootFrame();
                    rootframe.initFrame(config);
                }
            }
        });
    }
}
