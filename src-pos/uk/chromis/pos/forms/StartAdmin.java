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

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Locale;
import java.util.logging.Logger;
import uk.chromis.format.Formats;
import uk.chromis.pos.dbmanager.DatabaseManager;
import uk.chromis.pos.dbmanager.RunRepair;
import uk.chromis.pos.ticket.TicketInfo;
import uk.chromis.pos.util.AltEncrypter;
import uk.chromis.pos.util.DbUtils;

public class StartAdmin {

    private static final Logger logger = Logger.getLogger("uk.chromis.pos.forms.StartAdmin");
    private static ServerSocket serverSocket;

    private StartAdmin() {
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
        if (!registerApp()) {
            System.exit(0);
        }

        DbUtils.checkJava();

        DatabaseManager dbMan = new DatabaseManager();

        dbMan.checkDatabase();

        startApp();

    }

    public static void startApp() {
        // check if there are any repair scripts to run       
        String db_password = (AppConfig.getInstance().getProperty("db.password"));
        if (AppConfig.getInstance().getProperty("db.user") != null && db_password != null && db_password.startsWith("crypt:")) {
            AltEncrypter cypher = new AltEncrypter("cypherkey" + AppConfig.getInstance().getProperty("db.user"));
            db_password = cypher.decrypt(db_password.substring(6));
        }

       // RunRepair.Process(AppConfig.getInstance().getProperty("db.user"), AppConfig.getInstance().getProperty("db.URL"), db_password);

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                AppConfig config = AppConfig.getInstance();
                String slang = AppConfig.getInstance().getProperty("user.language");
                String scountry = AppConfig.getInstance().getProperty("user.country");
                String svariant = AppConfig.getInstance().getProperty("user.variant");
                if (slang != null && !slang.equals("") && scountry != null && svariant != null) {
                    Locale.setDefault(new Locale(slang, scountry, svariant));
                }

                Formats.setIntegerPattern(AppConfig.getInstance().getProperty("format.integer"));
                Formats.setDoublePattern(AppConfig.getInstance().getProperty("format.double"));
                Formats.setCurrencyPattern(AppConfig.getInstance().getProperty("format.currency"));
                Formats.setPercentPattern(AppConfig.getInstance().getProperty("format.percent"));
                Formats.setDatePattern(AppConfig.getInstance().getProperty("format.date"));
                Formats.setTimePattern(AppConfig.getInstance().getProperty("format.time"));
                Formats.setDateTimePattern(AppConfig.getInstance().getProperty("format.datetime"));

                String hostname = AppConfig.getInstance().getProperty("machine.hostname");
                TicketInfo.setHostname(hostname);

                JAdminFrame adminframe = new JAdminFrame();
                adminframe.initFrame(config);

            }
        });
    }
}
