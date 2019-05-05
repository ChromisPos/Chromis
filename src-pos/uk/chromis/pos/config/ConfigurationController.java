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
package uk.chromis.pos.config;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;

/**
 *
 * @author John
 */
public class ConfigurationController implements Initializable {

    public TabPane tabPane;
    public AnchorPane anchor;
    public Tab configureDatabase;
    public Tab configureTicket;
    public Tab configureGeneral;
    public Tab configurePayment;
    public Tab configurePeripheral;
    public Tab configureRestaurant;
    public Tab configureSystem;
    public Tab configureLocale;
    public Tab configureSync;
    public Tab configureSalesScreen;

    public ButtonBar btnBar;
    public Button btnExit;
    public Button btnSave;
    public Button btnRestore;

    //Comboboxes
    public ComboBox cbxCardReader;
    public ComboBox cbxPaymentGateway;

    public Pane bpane;

    private DatabasePanelController dbController;
    private GeneralPanelController genController;
    private LocalePanelController localeController;
    private PeripheralPanelController peripheralController;
    private PaymentPanelController paymentController;
    private SystemPanelController systemController;
    private SalesScreenPanelController salesScreenController;
    private RestaurantPanelController restuarantController;
    private TicketPanelController ticketController;

    protected BooleanProperty dirty = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbController = (DatabasePanelController) getController(configureDatabase, "/uk/chromis/pos/config/DatabasePanel.fxml");
        genController = (GeneralPanelController) getController(configureGeneral, "/uk/chromis/pos/config/GeneralPanel.fxml");
        localeController = (LocalePanelController) getController(configureLocale, "/uk/chromis/pos/config/LocalePanel.fxml");
        paymentController = (PaymentPanelController) getController(configurePayment, "/uk/chromis/pos/config/PaymentPanel.fxml");
        peripheralController = (PeripheralPanelController) getController(configurePeripheral, "/uk/chromis/pos/config/PeripheralPanel.fxml");
        systemController = (SystemPanelController) getController(configureSystem, "/uk/chromis/pos/config/SystemPanel.fxml");
        ticketController = (TicketPanelController) getController(configureTicket, "/uk/chromis/pos/config/TicketPanel.fxml");
        restuarantController = (RestaurantPanelController) getController(configureRestaurant, "/uk/chromis/pos/config/RestaurantPanel.fxml");
        salesScreenController = (SalesScreenPanelController) getController(configureSalesScreen, "/uk/chromis/pos/config/SalesScreenPanel.fxml");
        // sync

        dirty.bindBidirectional(dbController.dirty);
        dirty.bindBidirectional(genController.dirty);
        dirty.bindBidirectional(localeController.dirty);
        dirty.bindBidirectional(paymentController.dirty);
        dirty.bindBidirectional(peripheralController.dirty);
        dirty.bindBidirectional(systemController.dirty);
        dirty.bindBidirectional(ticketController.dirty);
        dirty.bindBidirectional(restuarantController.dirty);
        dirty.bindBidirectional(salesScreenController.dirty);

        // setup the pane pain
        tabPane.setStyle("-fx-border-color: black; -fx-border-width: 0px 0px 2px 0px");
        tabPane.setPrefSize(980, 590);

       
        // remove sync licence tab if not required
        tabPane.getTabs().remove(configureSync);
        
    }

    private Object getController(Tab tab, String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            tab.setContent(root);
            tab.setUserData(loader.getController());
            return (loader.getController());
        } catch (IOException ex) {
            System.out.println("Error loading tab data");
            System.out.println("Error loading FXML : " + fxml);
            System.out.println(ex);
        }
        return null;
    }

    public void restore() {
        dbController.load();
        genController.load();
        localeController.load();
        peripheralController.load();
        systemController.load();
        restuarantController.load();
        salesScreenController.load();
        ticketController.load();
        paymentController.load();

    }

    public void save() {
        dbController.save();
        genController.save();
        localeController.save();
        peripheralController.save();
        systemController.save();
        salesScreenController.save();
        restuarantController.save();
        ticketController.save();
        paymentController.save();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        try {
            AppConfig.getInstance().save();
            alert.setTitle("Save");
            alert.setHeaderText(null);
            alert.setContentText(AppLocal.getIntString("message.restartchanges"));
            ButtonType buttonExit = new ButtonType("OK");
            alert.getButtonTypes().setAll(buttonExit);
            Optional<ButtonType> result = alert.showAndWait();
        } catch (IOException e) {
            alert.setTitle("Save");
            alert.setHeaderText(null);
            alert.setContentText(AppLocal.getIntString("message.cannotsaveconfig"));
            ButtonType buttonExit = new ButtonType("OK");
            alert.getButtonTypes().setAll(buttonExit);
            Optional<ButtonType> result = alert.showAndWait();
            // JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotsaveconfig"), e));
        }

    }

    public void handleExit() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (dirty.getValue()) {
            alert.setTitle("Unsaved Data");
            alert.setHeaderText(null);
            alert.setContentText(AppLocal.getIntString("message.wannasave"));
            ButtonType buttonYes = new ButtonType("Yes");
            ButtonType buttonNo = new ButtonType("No");
            ButtonType buttonCancel = new ButtonType("Cancel");
            alert.getButtonTypes().setAll(buttonYes, buttonNo, buttonCancel);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get().getText().equals("No")) {
                System.exit(0);
            } else if ((result.get().getText().equals("Yes"))) {
                save();
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    public Boolean isDirty() {
        return dirty.getValue();
    }

    public void setDirty(Boolean value) {
        dirty.setValue(value);
    }
}
