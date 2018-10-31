/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.chromis.pos.dbmanager;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javax.swing.JFrame;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Session;
import uk.chromis.pos.dialogs.JAlert;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.util.SessionFactory;

/**
 *
 * @author John
 */
public class DatabaseManager {

    private Dimension screenSize;
    private int centreX;
    private int centreY;
    private JFrame frame;
    private JFrame uframe;
    private JFXPanel fxPanel;
    private DbDialogController rootController;
    private UpdateController updateController;
    private Connect connect;
    private DbUser dbUser;
    private Session session;
    private DataLogicSystem m_dlSystem;
    private String sDbVersion;
    public static CountDownLatch waitForUpdate;
    public static CountDownLatch waitForConnection;
    private JAlert alert;

    public DatabaseManager() {

    }

    public void checkDatabase() {
        waitForUpdate = new CountDownLatch(1);

        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        centreX = screenSize.width / 2;
        centreY = screenSize.height / 2;

        //Build the JFame to be used for the FX panel
        uframe = new JFrame();
        uframe.setUndecorated(true);
        fxPanel = new JFXPanel();
        uframe.add(fxPanel);
        uframe.setLocation(centreX - 157, centreY - 50);
        uframe.setSize(315, 100);
        uframe.setVisible(false);
        uframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create an instance of Connect
        connect = new Connect();

        //Show connect dialog on the screen
        connect.showPanel(true);

        // Create instance of databse user info       
        dbUser = new DbUser();

        //Get DbUser details
        Boolean dirty = dbUser.getUserDetails();

        //if new dbuser ie no properties file then open dbDialog boc
        if (dirty) {
            fxPanel.setScene(createDbDialog());
            uframe.setVisible(true);
            connect.showPanel(false);
            connect.dispose();
            //use countdownlatch to halt program flow
            try {
                waitForUpdate.await();
            } catch (InterruptedException ex) {

            }
        }

        //Lets test for a connection and wait for result
        waitForConnection = new CountDownLatch(1);
        Object[] result = DbUtils.connectionTest(dbUser, true);
        try {
            waitForConnection.await();
        } catch (InterruptedException ex) {

        }

        //Check what the connection test returned
        if ((Boolean) result[0]) {
            if (!(Boolean) result[3] && result[4].equals("")) {
                alert = new JAlert(1);
                alert.setTitle(AppLocal.getIntString("Message.titleConnectionTest"));
                alert.setHeaderText(AppLocal.getIntString("Message.titleConnectionTest"));
                alert.setContextText(AppLocal.getIntString("Message.databaseNotChromis")
                        + "\n" + AppLocal.getIntString("Message.openConfiguration"));
                alert.setYesNoButtons();
                alert.setVisible(true);
                connect.showPanel(false);
                connect.dispose();
                if (alert.getChoice() == 6) {
                    System.exit(0);
                } else {
                    fxPanel.setScene(createDbDialog());
                    uframe.setVisible(true);
                    connect.showPanel(false);
                    connect.dispose();
                    //use countdownlatch to halt main program flow DbDialog always exits the application
                    if (alert.getChoice() == 6) {
                        System.exit(0);
                    } else {
                        fxPanel.setScene(createDbDialog());
                        uframe.setVisible(true);

                        //use countdownlatch to halt main program flow DbDialog always exits the application
                        try {
                            waitForUpdate.await();
                        } catch (InterruptedException ex) {
                        }
                    }
                }
            } else if ((Boolean) result[3]) {
                alert = new JAlert(1);
                alert.setTitle(AppLocal.getIntString("Message.titleConnectionTest"));
                alert.setHeaderText(AppLocal.getIntString("Message.connectionHeader"));
                alert.setContextText(AppLocal.getIntString("Message.databaseEmpty")
                        + "\n" + AppLocal.getIntString("Message.openConfiguration"));
                alert.setYesNoButtons();
                alert.setVisible(true);
                connect.showPanel(false);
                connect.dispose();
                if (alert.getChoice() == 6) {
                    System.exit(0);
                } else {
                    fxPanel.setScene(createDbDialog());
                    uframe.setVisible(true);
                    //use countdownlatch to halt main program flow DbDialog always exits the application
                    try {
                        waitForUpdate.await();
                    } catch (InterruptedException ex) {
                    }
                }
            }
        } else {
            alert = new JAlert(4);
            alert.setTitle(AppLocal.getIntString("Message.titleConnectionTest"));
            alert.setHeaderText(AppLocal.getIntString("Message.connectionFailed"));
            alert.setContextText(AppLocal.getIntString("Message.unableToConnect"));
            alert.setYesNoButtons();
            alert.reSize(380, 180);
            alert.setVisible(true);
            connect.showPanel(false);
            connect.dispose();;
            if (alert.getChoice() == 6) {
                System.exit(0);
            } else {
                fxPanel.setScene(createDbDialog());
                uframe.setVisible(true);
                //use countdownlatch to halt main program flow DbDialog always exits the application
                try {
                    waitForUpdate.await();
                } catch (InterruptedException ex) {
                }
            }
        }

        //Check if we need to upgrade the database
        session = SessionFactory.getInstance().getSession();
        m_dlSystem = new DataLogicSystem();
        m_dlSystem.init(session);

        sDbVersion = readDataBaseVersion();
        int dbVersion;

        if (sDbVersion == null) {
            fxPanel.setScene(createDbDialog());
            uframe.setVisible(true);
            connect.showPanel(false);
            connect.dispose();
            //use countdownlatch to halt program flow
            try {
                waitForUpdate.await();
            } catch (InterruptedException ex) {
            }
        } 
        
        if (!sDbVersion.equals(AppLocal.APP_VERSION)) {
            try {
                // Ready for future release, this will prevent upgrade running against older version
                // if (AppLocal.APP_VERSIONINT > readDataBaseIntVersion()) {
                dbVersion = m_dlSystem.getVerisonInt();
            } catch (BasicException ex) {
                dbVersion = 1;
            }

            if (AppLocal.APP_VERSIONINT > dbVersion) {
                fxPanel.setScene(updateDB());
                uframe.setVisible(true);
                connect.showPanel(false);
                connect.dispose();
                try {
                    waitForUpdate.await();
                } catch (InterruptedException ex) {
                }
            }
        }

        connect.showPanel(false);
        connect.dispose();
        return;
    }

    private Scene createDbDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/chromis/pos/dbmanager/DbDialog.fxml"));
            Parent connectRoot = loader.load();
            rootController = loader.getController();
            uframe.setLocation(centreX - 345, centreY - 150);
            uframe.setSize(690, 300);
            return (new Scene(connectRoot, 690, 300));
        } catch (IOException ex) {
            System.out.println("Log error for bad scene - createDbDialog");
            System.exit(1);
        }
        return null;
    }

    private Scene updateDB() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/uk/chromis/pos/dbmanager/Update.fxml"));
            Parent root = loader.load();
            updateController = loader.getController();
            uframe.setLocation(centreX - 245, centreY - 163);
            uframe.setSize(490, 325);
            updateController.setDbVersion(sDbVersion);
            return (new Scene(root, 490, 325));
        } catch (IOException exc) {
            System.out.println("Log error for bad scene");
            System.exit(1);
        }
        return null;
    }

    private String readDataBaseVersion() {
        try {
            return m_dlSystem.findVersion();
        } catch (BasicException ed) {
            return null;
        }
    }

    private Integer readDataBaseIntVersion() {
        try {
            return m_dlSystem.getVerisonInt();
        } catch (BasicException ed) {
            return null;
        }
    }

}
