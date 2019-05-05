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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import uk.chromis.custom.controls.LabeledColourPicker;
import uk.chromis.custom.controls.LabeledToggleSwitch;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;

/**
 * FXML Controller class
 *
 * @author John
 */
public class RestaurantPanelController implements Initializable, BaseController {

    @FXML
    private LabeledToggleSwitch tsCustomerDetails;
    @FXML
    private LabeledToggleSwitch tsWaiterDetails;
    @FXML
    private LabeledToggleSwitch tsTransparent;
    @FXML
    private LabeledToggleSwitch tsTableRefresh;
    @FXML
    private LabeledToggleSwitch tsTableRelocation;

    @FXML
    private Label sectionLabel;

    @FXML
    private LabeledColourPicker cpCustomerColour;
    @FXML
    private LabeledColourPicker cpWaiterColour;
    @FXML
    private LabeledColourPicker cpTableColour;

    protected BooleanProperty dirty = new SimpleBooleanProperty();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dirty.bindBidirectional(cpCustomerColour.dirty);
        dirty.bindBidirectional(cpWaiterColour.dirty);
        dirty.bindBidirectional(cpTableColour.dirty);
        dirty.bindBidirectional(tsCustomerDetails.dirty);
        dirty.bindBidirectional(tsWaiterDetails.dirty);
        dirty.bindBidirectional(tsTransparent.dirty);
        dirty.bindBidirectional(tsTableRefresh.dirty);
        dirty.bindBidirectional(tsTableRelocation.dirty);

        sectionLabel.setText(AppLocal.getIntString("label.tabledisplayoptions"));

        tsCustomerDetails.setLabelWidth(225.0);
        tsCustomerDetails.setText(AppLocal.getIntString("label.tableshowcustomerdetails"));
        tsWaiterDetails.setLabelWidth(225.0);
        tsWaiterDetails.setText(AppLocal.getIntString("label.tableshowwaiterdetails"));
        tsTransparent.setLabelWidth(225.0);
        tsTransparent.setText(AppLocal.getIntString("label.tablebuttons"));
        tsTableRefresh.setLabelWidth(225.0);
        tsTableRefresh.setText(AppLocal.getIntString("label.autorefresh"));
        tsTableRelocation.setLabelWidth(225.0);
        tsTableRelocation.setText(AppLocal.getIntString("label.enabletablepositions"));
       

        cpCustomerColour.setText(AppLocal.getIntString("label.textcolourcustomer"));
        cpCustomerColour.setLabelWidth(250.0);
        cpWaiterColour.setText(AppLocal.getIntString("label.textcolourwaiter"));
        cpWaiterColour.setLabelWidth(250.0);
        cpTableColour.setText(AppLocal.getIntString("label.textclourtablename"));
        cpTableColour.setLabelWidth(250.0);

        load();

    }

    @Override
    public void load() {
        tsCustomerDetails.setSelected(AppConfig.getInstance().getBoolean("table.showcustomerdetails"));
        tsWaiterDetails.setSelected(AppConfig.getInstance().getBoolean("table.showwaiterdetails"));
        tsTransparent.setSelected(AppConfig.getInstance().getBoolean("table.transparentbuttons"));
        tsTableRefresh.setSelected(AppConfig.getInstance().getBoolean("tables.autorefresh"));
        tsTableRelocation.setSelected(AppConfig.getInstance().getBoolean("tables.redesign"));

        if (AppConfig.getInstance().getProperty("table.customercolour") == null) {
            cpCustomerColour.setColour(Color.BLUE);
        } else {
            cpCustomerColour.setColour(AppConfig.getInstance().getProperty("table.customercolour"));
        }
        if (AppConfig.getInstance().getProperty("table.waitercolour") == null) {
            cpWaiterColour.setColour(Color.RED);
        } else {
            cpWaiterColour.setColour(AppConfig.getInstance().getProperty("table.waitercolour"));
        }
        if (AppConfig.getInstance().getProperty("table.tablecolour") == null) {
            cpTableColour.setColour(Color.BLACK);
        } else {
            cpTableColour.setColour(AppConfig.getInstance().getProperty("table.tablecolour"));
        }

    }

    @Override
    public void save() {
        ColorPicker cp = cpCustomerColour.getPicker();
        AppConfig.getInstance().setBoolean("table.showcustomerdetails", tsCustomerDetails.isSelected());
        AppConfig.getInstance().setBoolean("table.showwaiterdetails", tsWaiterDetails.isSelected());
        AppConfig.getInstance().setProperty("table.customercolour", cpCustomerColour.getColour());
        AppConfig.getInstance().setProperty("table.waitercolour", cpWaiterColour.getColour());
        AppConfig.getInstance().setProperty("table.tablecolour", cpTableColour.getColour());
        AppConfig.getInstance().setBoolean("table.transparentbuttons", tsTransparent.isSelected());
        AppConfig.getInstance().setBoolean("tables.autorefresh", tsTableRefresh.isSelected());
        AppConfig.getInstance().setBoolean("tables.redesign", tsTableRelocation.isSelected());

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
