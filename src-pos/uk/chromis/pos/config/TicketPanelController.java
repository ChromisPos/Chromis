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

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javax.swing.SpinnerNumberModel;
import uk.chromis.custom.controls.LabeledTextField;
import uk.chromis.custom.controls.TitledSeparator;
import uk.chromis.custom.switches.ToggleSwitch;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;

/**
 * FXML Controller class
 *
 * @author John
 */
public class TicketPanelController implements Initializable, BaseController {

    @FXML
    private TitledSeparator receiptSetup;
    @FXML
    private TitledSeparator serviceCharge;
    @FXML
    private TitledSeparator layawayDetails;
    @FXML
    private Label receiptLength;
    @FXML
    private Label percent;
    @FXML
    private Label pickupLength;
    @FXML
    private Spinner receiptSpinner;
    @FXML
    private Spinner pickupSpinner;

    @FXML
    private Label ticketExample;

    @FXML
    private ToggleSwitch receiptPrinterOff;
    @FXML
    private ToggleSwitch serviceChargeOff;
    @FXML
    private ToggleSwitch SCRestaurant;
    @FXML
    private ToggleSwitch layawayId;
    @FXML
    private ToggleSwitch createOnOrderOnly;
    @FXML
    private ToggleSwitch layawayPopup;

    @FXML
    private LabeledTextField textSCRate;
    @FXML
    private LabeledTextField receiptPrefix;

    @FXML
    private AnchorPane pane;

    private String receipt = "1";
    private Integer x = 0;
    private String receiptSize;
    private String pickupSize;
    private Integer ps = 0;

    public BooleanProperty dirty = new SimpleBooleanProperty(false);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        receiptSetup.setText(AppLocal.getIntString("label.configreceipt"));
        receiptSetup.setWidth(750.00);
        serviceCharge.setText(AppLocal.getIntString("label.SChargepanel"));
        serviceCharge.setWidth(750.00);
        layawayDetails.setText(AppLocal.getIntString("label.layawaydetails"));
        layawayDetails.setWidth(750.00);
        receiptLength.setText(AppLocal.getIntString("Label.ticketsetupnumber"));
        pickupLength.setText(AppLocal.getIntString("label.pickupcodesize"));
        receiptPrefix.setLabel(AppLocal.getIntString("Label.ticketsetupprefix"));
        receiptPrefix.setLabelWidth(250);
        receiptPrefix.setTextWidth(200);

        receiptPrinterOff.setSwitchLabel(AppLocal.getIntString("label.receiptprint"));
        serviceChargeOff.setSwitchLabel(AppLocal.getIntString("label.SCOnOff"));
        SCRestaurant.setSwitchLabel(AppLocal.getIntString("label.SCRestaurant"));
        textSCRate.setLabel(AppLocal.getIntString("label.SCRate"));
        textSCRate.setLabelWidth(200);
        textSCRate.setTextWidth(50);

        layawayId.setSwitchLabel(AppLocal.getIntString("label.layaway"));
        createOnOrderOnly.setSwitchLabel(AppLocal.getIntString("label.createonorder"));
        layawayPopup.setSwitchLabel(AppLocal.getIntString("label.layawaypopup"));

        updateServiceCharge();

        serviceChargeOff.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                updateServiceCharge();
            }
        });

        receiptPrefix.getTextField().textProperty().addListener((observable, oldValue, newValue)
                -> ticketExample.setText(receiptPrefix.getText() + receipt)
        );

        receiptSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            receipt = "";
            x = 1;
            while (x < (Integer) receiptSpinner.getValue()) {
                receipt += "0";
                x++;
            }
            receipt += "1";
            ticketExample.setText(receiptPrefix.getText() + receipt
            );
        });

        load();

    }

    private void updateServiceCharge() {
        if (serviceChargeOff.isSelected()) {
            SCRestaurant.setDisable(false);
            textSCRate.setDisable(false);;
        } else {
            SCRestaurant.setDisable(true);
            textSCRate.setDisable(true);
        }
    }

    @Override
    public void load() {
        receiptSize = (AppConfig.getInstance().getProperty("till.receiptsize"));
        if (receiptSize == null || "".equals(receiptSize)) {
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1);
            receiptSpinner.setValueFactory(valueFactory);
        } else {
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, Integer.parseInt(receiptSize));
            receiptSpinner.setValueFactory(valueFactory);
        }

        pickupSize = (AppConfig.getInstance().getProperty("till.pickupsize"));
        if (pickupSize == null || "".equals(pickupSize)) {
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1);
            pickupSpinner.setValueFactory(valueFactory);
        } else {
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, Integer.parseInt(pickupSize));
            pickupSpinner.setValueFactory(valueFactory);
        }

        receiptPrefix.setText(AppConfig.getInstance().getProperty("till.receiptprefix"));
        receipt = "";
        x = 1;
        while (x < (Integer) receiptSpinner.getValue()) {
            receipt += "0";
            x++;
        }

        receipt += "1";
        ticketExample.setText(receiptPrefix.getText() + receipt);
        receiptPrinterOff.setSelected(AppConfig.getInstance().getBoolean("till.receiptprintoff"));
        String SCCheck = (AppConfig.getInstance().getProperty("till.SCRate"));
        if (SCCheck == null || SCCheck.equals("")) {
            AppConfig.getInstance().setProperty("till.SCRate", "10");
            textSCRate.setText("10");
        } else {
            textSCRate.setText(AppConfig.getInstance().getProperty("till.SCRate").toString());
        }

        serviceChargeOff.setSelected(AppConfig.getInstance().getBoolean("till.SCOnOff"));
        SCRestaurant.setSelected(AppConfig.getInstance().getBoolean("till.SCRestaurant"));
        layawayId.setSelected(AppConfig.getInstance().getBoolean("till.usepickupforlayaway"));
        createOnOrderOnly.setSelected(AppConfig.getInstance().getBoolean("till.createorder"));
        layawayPopup.setSelected(AppConfig.getInstance().getBoolean("till.layawaypopup"));

        dirty.setValue(false);
    }

    @Override
    public void save() {
        AppConfig.getInstance().setProperty("till.receiptprefix", receiptPrefix.getText());
        AppConfig.getInstance().setProperty("till.receiptsize", receiptSpinner.getValue().toString());
        AppConfig.getInstance().setProperty("till.pickupsize", pickupSpinner.getValue().toString());
        AppConfig.getInstance().setBoolean("till.receiptprintoff", receiptPrinterOff.isSelected());
        AppConfig.getInstance().setBoolean("till.SCOnOff", serviceChargeOff.isSelected());
        AppConfig.getInstance().setProperty("till.SCRate", textSCRate.getText());
        AppConfig.getInstance().setBoolean("till.SCRestaurant", SCRestaurant.isSelected());
        AppConfig.getInstance().setBoolean("till.usepickupforlayaway", layawayId.isSelected());
        AppConfig.getInstance().setBoolean("till.createorder", createOnOrderOnly.isSelected());
        AppConfig.getInstance().setBoolean("till.layawaypopup", layawayPopup.isSelected());

        dirty.setValue(false);
    }

    @Override
    public Boolean isDirty() {
        return dirty.getValue();
    }

    @Override
    public void setDirty(Boolean value) {
        dirty.setValue(value);
    }

}
