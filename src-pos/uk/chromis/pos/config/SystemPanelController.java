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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;

import uk.chromis.custom.controls.LabeledColourPicker;
import uk.chromis.custom.controls.LabeledTextField;
import uk.chromis.custom.controls.LabeledToggleSwitch;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;

public class SystemPanelController implements Initializable, BaseController {

    @FXML
    private LabeledToggleSwitch enableAutoLogoff;
    @FXML
    private LabeledToggleSwitch inactivityTimer;
    @FXML
    private LabeledToggleSwitch autologoffAfterSale;
    @FXML
    private LabeledToggleSwitch autoLogoffAfterKitchen;
    @FXML
    private LabeledToggleSwitch autoLogoffToTables;
    @FXML
    private LabeledTextField autoLogoffTime;
    @FXML
    private LabeledToggleSwitch autoLogoffAfterPrint;

    @FXML
    private LabeledToggleSwitch marineOpt;
    @FXML
    private LabeledToggleSwitch taxIncluded;
    @FXML
    private LabeledToggleSwitch consolidate;
    @FXML
    private LabeledToggleSwitch textOverlay;
    @FXML
    private LabeledToggleSwitch price00;
    @FXML
    private LabeledToggleSwitch disableDefaultProduct;
    @FXML
    private LabeledToggleSwitch closeCashbtn;
    @FXML
    private LabeledToggleSwitch moveAMountBoxToTop;
    @FXML
    private LabeledToggleSwitch changeSalesScreen;
    @FXML
    private LabeledToggleSwitch updatedbprice;
    @FXML
    private LabeledToggleSwitch categoiesBynumber;
    @FXML
    private LabeledToggleSwitch longNames;
    @FXML
    private LabeledToggleSwitch customSounds;
    @FXML
    private LabeledToggleSwitch maxChangeEnable;
    @FXML
    private LabeledTextField maxChange;
    @FXML
    private Spinner tableDays;
    @FXML
    private Spinner removedLineDays;
    @FXML
    private Spinner ticketLineSize;
    @FXML
    private Label retainLabel;
    @FXML
    private Label removedLinesLabel;
    @FXML
    private Label ticketLinesLabel;
    @FXML
    private Label autologoffpanel;
    @FXML
    private Label general;
    @FXML
    private Label ticketLines;
    @FXML
    private Label colouredLineMessage;
    @FXML
    private LabeledColourPicker normalMessage;
    @FXML
    private LabeledColourPicker waitingMessage;
    @FXML
    private LabeledColourPicker sentMessage;

    private String tableRetain;
    private String lineRemovedRetain;
    private String ticketLine;

    protected BooleanProperty dirty = new SimpleBooleanProperty();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        autologoffpanel.setText(AppLocal.getIntString("label.autologoffpanel"));
        general.setText(AppLocal.getIntString("label.general"));

        dirty.bindBidirectional(enableAutoLogoff.dirty);
        dirty.bindBidirectional(inactivityTimer.dirty);
        dirty.bindBidirectional(autologoffAfterSale.dirty);
        dirty.bindBidirectional(autoLogoffAfterKitchen.dirty);
        dirty.bindBidirectional(autoLogoffAfterPrint.dirty);
        dirty.bindBidirectional(autoLogoffTime.dirty);
        dirty.bindBidirectional(marineOpt.dirty);
        dirty.bindBidirectional(taxIncluded.dirty);
        dirty.bindBidirectional(consolidate.dirty);
        dirty.bindBidirectional(textOverlay.dirty);
        dirty.bindBidirectional(price00.dirty);
        dirty.bindBidirectional(closeCashbtn.dirty);
        dirty.bindBidirectional(moveAMountBoxToTop.dirty);
        dirty.bindBidirectional(changeSalesScreen.dirty);
        dirty.bindBidirectional(updatedbprice.dirty);
        dirty.bindBidirectional(longNames.dirty);
        dirty.bindBidirectional(customSounds.dirty);
        dirty.bindBidirectional(maxChange.dirty);
        ticketLineSize.valueProperty().addListener((obs, oldValue, newValue) -> dirty.setValue(true));
        tableDays.valueProperty().addListener((obs, oldValue, newValue) -> dirty.setValue(true));

        enableAutoLogoff.setLabelWidth(225.0);
        enableAutoLogoff.setText(AppLocal.getIntString("label.autologonoff"));
        inactivityTimer.setLabelWidth(225.0);
        inactivityTimer.setText(AppLocal.getIntString("label.inactivity"));
        autologoffAfterSale.setLabelWidth(225.0);
        autologoffAfterSale.setText(AppLocal.getIntString("label.autologoff"));
        autoLogoffAfterKitchen.setLabelWidth(225.0);
        autoLogoffAfterKitchen.setText(AppLocal.getIntString("label.logoffaftersendtokitchen"));
        autoLogoffToTables.setLabelWidth(225.0);
        autoLogoffToTables.setText(AppLocal.getIntString("label.autologoffrestaurant"));
        autoLogoffAfterPrint.setLabelWidth(225.0);
        autoLogoffAfterPrint.setText(AppLocal.getIntString("label.logoffafterprinting"));
        autoLogoffAfterPrint.setVisible(false);
        autoLogoffTime.setLabelWidth(225.0);
        autoLogoffTime.setLabel(AppLocal.getIntString("label.autologoffzero"));
        autoLogoffTime.setTextWidth(50);
        autoLogoffTime.setText("100");

        enableAutoLogoff.setSelected(AppConfig.getInstance().getBoolean("till.enableautologoff"));
        inactivityTimer.setSelected(AppConfig.getInstance().getBoolean("till.autologoffinactivitytimer"));
        autoLogoffTime.setText(AppConfig.getInstance().getProperty("till.autologofftimerperiod"));
        autologoffAfterSale.setSelected(AppConfig.getInstance().getBoolean("till.autologoffaftersale"));
        autoLogoffToTables.setSelected(AppConfig.getInstance().getBoolean("till.autologofftotables"));
        autoLogoffAfterKitchen.setSelected(AppConfig.getInstance().getBoolean("till.autologoffafterkitchen"));
        autoLogoffAfterPrint.setSelected(AppConfig.getInstance().getBoolean("till.autologoffafterprint"));

        enableAutoLogoff.switchedOn.addListener((arg, oldVal, newVal) -> updateAutoLogoff());

        updateAutoLogoff();

        marineOpt.setText(AppLocal.getIntString("label.marine"));
        taxIncluded.setLabelWidth(175.0);
        taxIncluded.setText(AppLocal.getIntString("label.taxincluded"));
        consolidate.setLabelWidth(175.0);
        consolidate.setText(AppLocal.getIntString("Label.ConsolidatedScreen"));
        textOverlay.setLabelWidth(175.0);
        textOverlay.setText(AppLocal.getIntString("label.currencybutton"));
        price00.setLabelWidth(175.0);
        price00.setText(AppLocal.getIntString("label.pricewith00"));
        disableDefaultProduct.setLabelWidth(175.0);
        disableDefaultProduct.setText(AppLocal.getIntString("label.default"));
        closeCashbtn.setLabelWidth(175.0);
        closeCashbtn.setText(AppLocal.getIntString("message.systemclosecash"));
        moveAMountBoxToTop.setLabelWidth(175.0);
        moveAMountBoxToTop.setText(AppLocal.getIntString("label.inputamount"));
        changeSalesScreen.setLabelWidth(175.0);
        changeSalesScreen.setText(AppLocal.getIntString("Label.ChangesSalesScreen"));
        updatedbprice.setLabelWidth(175.0);
        updatedbprice.setText(AppLocal.getIntString("label.updatepricefromedit"));
        categoiesBynumber.setLabelWidth(175.0);
        categoiesBynumber.setText(AppLocal.getIntString("label.categoryorder"));
        longNames.setLabelWidth(175.0);
        longNames.setText(AppLocal.getIntString("label.allowlongnames"));
        customSounds.setLabelWidth(175.0);
        customSounds.setText(AppLocal.getIntString("label.customerrorsounds"));
        maxChangeEnable.setLabelWidth(175.0);
        maxChangeEnable.setText(AppLocal.getIntString("message.enablechange"));

        maxChange.setLabel(AppLocal.getIntString("label.maxchange"));
        maxChange.setTextWidth(75);
        maxChange.setText("50.00");

        maxChangeEnable.dirty.addListener((arg, oldVal, newVal) -> maxChange.setDisable(!maxChangeEnable.isSelected()));

        retainLabel.setText(AppLocal.getIntString("label.cleardrawertable"));
        IntegerSpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(7, 90, 7);
        tableDays.setValueFactory(valueFactory);
        valueFactory.valueProperty().addListener((obs, oldValue, newValue)
                -> dirty.setValue(true));

        ticketLinesLabel.setText(AppLocal.getIntString("label.ticketlinesize"));
        IntegerSpinnerValueFactory ticketLineFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(28, 100, 7);
        ticketLineSize.setValueFactory(ticketLineFactory);
        ticketLineFactory.valueProperty().addListener((obs, oldValue, newValue)
                -> dirty.setValue(true));

        removedLinesLabel.setText(AppLocal.getIntString("label.removedlinedays"));
        IntegerSpinnerValueFactory lineRemovedFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(7, 60, 14);
        removedLineDays.setValueFactory(lineRemovedFactory);
        lineRemovedFactory.valueProperty().addListener((obs, oldValue, newValue)
                -> dirty.setValue(true));

        load();

    }

    private void updateAutoLogoff() {
        if (enableAutoLogoff.isSelected()) {
            autoLogoffToTables.setSwitchDisable(false);
            autoLogoffTime.setDisable(false);
            autoLogoffTime.setDisable(false);
            autoLogoffAfterKitchen.setSwitchDisable(false);
            autoLogoffAfterPrint.setSwitchDisable(false);
            inactivityTimer.setSwitchDisable(false);
            autologoffAfterSale.setSwitchDisable(false);
        } else {
            autoLogoffToTables.setSwitchDisable(true);
            autoLogoffTime.setDisable(true);
            autoLogoffTime.setDisable(true);
            autoLogoffAfterKitchen.setSwitchDisable(true);
            autoLogoffAfterPrint.setSwitchDisable(true);
            inactivityTimer.setSwitchDisable(true);
            autologoffAfterSale.setSwitchDisable(true);
        }
    }

    @Override
    public void load() {
        marineOpt.setSelected(AppConfig.getInstance().getBoolean("till.marineoption"));
        textOverlay.setSelected(AppConfig.getInstance().getBoolean("payments.textoverlay"));
        moveAMountBoxToTop.setSelected(AppConfig.getInstance().getBoolean("till.taxincluded"));
        price00.setSelected(AppConfig.getInstance().getBoolean("till.pricewith00"));
        moveAMountBoxToTop.setSelected(AppConfig.getInstance().getBoolean("till.amountattop"));
        closeCashbtn.setSelected(AppConfig.getInstance().getBoolean("screen.600800"));
        updatedbprice.setSelected(AppConfig.getInstance().getBoolean("db.productupdate"));
        changeSalesScreen.setSelected(AppConfig.getInstance().getBoolean("sales.newscreen"));
        consolidate.setSelected(AppConfig.getInstance().getBoolean("display.consolidated"));
        disableDefaultProduct.setSelected(AppConfig.getInstance().getBoolean("product.hidedefaultproductedit"));
        taxIncluded.setSelected(AppConfig.getInstance().getBoolean("till.taxincluded"));
        categoiesBynumber.setSelected(AppConfig.getInstance().getBoolean("till.categoriesbynumberorder"));
        maxChange.setText(AppConfig.getInstance().getProperty("till.changelimit"));
        maxChangeEnable.setSelected(AppConfig.getInstance().getBoolean("till.enablechangelimit"));
        longNames.setSelected(AppConfig.getInstance().getBoolean("display.longnames"));
        customSounds.setSelected(AppConfig.getInstance().getBoolean("till.customsounds"));
        maxChange.setDisable(!maxChangeEnable.isSelected());

        tableRetain = (AppConfig.getInstance().getProperty("dbtable.retaindays"));
        if (tableRetain == null || "".equals(tableRetain)) {
            tableDays.getValueFactory().setValue(7);
        } else {
            tableDays.getValueFactory().setValue(Integer.parseInt(tableRetain));
        }

        lineRemovedRetain = (AppConfig.getInstance().getProperty("dbtable.lineremoveddays"));
        if (lineRemovedRetain == null || "".equals(removedLineDays)) {
            removedLineDays.getValueFactory().setValue(14);
        } else {
            removedLineDays.getValueFactory().setValue(Integer.parseInt(lineRemovedRetain));
        }

        ticketLine = (AppConfig.getInstance().getProperty("sales.linesize"));
        if (ticketLine == null || "".equals(ticketLine)) {
            ticketLineSize.getValueFactory().setValue(40);
        } else {
            ticketLineSize.getValueFactory().setValue(Integer.parseInt(ticketLine));
        }
		
		enableAutoLogoff.switchedOn.addListener((arg, oldVal, newVal) -> updateAutoLogoff());
        updateAutoLogoff();
		
        dirty.setValue(false);
    }

    @Override
    public void save() {
        AppConfig.getInstance().setBoolean("till.enableautologoff", enableAutoLogoff.isSelected());
        AppConfig.getInstance().setBoolean("till.autologoffinactivitytimer", inactivityTimer.isSelected());
        AppConfig.getInstance().setProperty("till.autologofftimerperiod", autoLogoffTime.getText());
        AppConfig.getInstance().setBoolean("till.autologoffaftersale", autologoffAfterSale.isSelected());
        AppConfig.getInstance().setBoolean("till.autologofftotables", autoLogoffToTables.isSelected());
        AppConfig.getInstance().setBoolean("till.autologoffafterkitchen", autoLogoffAfterKitchen.isSelected());
        AppConfig.getInstance().setBoolean("till.autologoffafterprint", autoLogoffAfterPrint.isSelected());
        AppConfig.getInstance().setBoolean("till.marineoption", marineOpt.isSelected());
        AppConfig.getInstance().setBoolean("payments.textoverlay", textOverlay.isSelected());
        AppConfig.getInstance().setBoolean("till.taxincluded", moveAMountBoxToTop.isSelected());
        AppConfig.getInstance().setBoolean("till.pricewith00", price00.isSelected());
        AppConfig.getInstance().setBoolean("till.amountattop", moveAMountBoxToTop.isSelected());
        AppConfig.getInstance().setBoolean("screen.600800", closeCashbtn.isSelected());
        AppConfig.getInstance().setProperty("dbtable.retaindays", tableDays.getValue().toString());
        AppConfig.getInstance().setBoolean("db.productupdate", updatedbprice.isSelected());
        AppConfig.getInstance().setBoolean("sales.newscreen", changeSalesScreen.isSelected());
        AppConfig.getInstance().setBoolean("display.consolidated", consolidate.isSelected());
        AppConfig.getInstance().setBoolean("product.hidedefaultproductedit", disableDefaultProduct.isSelected());
        AppConfig.getInstance().setBoolean("till.taxincluded", taxIncluded.isSelected());
        AppConfig.getInstance().setBoolean("till.categoriesbynumberorder", categoiesBynumber.isSelected());
        AppConfig.getInstance().setProperty("till.changelimit", maxChange.getText());
        AppConfig.getInstance().setBoolean("till.enablechangelimit", maxChangeEnable.isSelected());
        AppConfig.getInstance().setBoolean("display.longnames", longNames.isSelected());
        AppConfig.getInstance().setBoolean("till.customsounds", customSounds.isSelected());
        AppConfig.getInstance().setProperty("sales.linesize", ticketLineSize.getValue().toString());
        AppConfig.getInstance().setProperty("dbtable.lineremoveddays", removedLineDays.getValue().toString());

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
