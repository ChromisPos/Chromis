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
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import uk.chromis.pos.forms.AppConfig;

/**
 * FXML Controller class
 *
 * @author John.Lewis
 */
public class SalesScreenPanelController implements Initializable, BaseController {

    public ImageView image0;
    public ImageView image1;
    public ImageView image2;
    public ImageView image3;

    public ToggleGroup screenLayout;

    public RadioButton rbtnLayout0;
    public RadioButton rbtnLayout1;
    public RadioButton rbtnLayout2;
    public RadioButton rbtnLayout3;

    public Pane paneImage0;
    public Pane paneImage1;
    public Pane paneImage2;
    public Pane paneImage3;

    private Image image;
    private String strLayout;

    protected BooleanProperty dirty = new SimpleBooleanProperty();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        load();

        image = new Image(getClass().getResourceAsStream("/uk/chromis/fixedimages/default.png"));
        image0.setImage(image);
        image = new Image(getClass().getResourceAsStream("/uk/chromis/fixedimages/layout1.png"));
        image1.setImage(image);
        image = new Image(getClass().getResourceAsStream("/uk/chromis/fixedimages/layout2.png"));
        image2.setImage(image);
        image = new Image(getClass().getResourceAsStream("/uk/chromis/fixedimages/layout3.png"));
        image3.setImage(image);

        paneImage0.setOnMouseClicked((MouseEvent event) -> rbtnLayout0.fire());

        paneImage1.setOnMouseClicked((MouseEvent event) -> rbtnLayout1.fire());

        paneImage2.setOnMouseClicked((MouseEvent event) -> rbtnLayout2.fire());

        paneImage3.setOnMouseClicked((MouseEvent event) -> rbtnLayout3.fire());

        rbtnLayout0.setOnAction(event -> strLayout = "Layout0");
        
        rbtnLayout1.setOnAction(event -> strLayout = "Layout1");
        
        rbtnLayout2.setOnAction(event -> strLayout = "Layout2");
        
        rbtnLayout3.setOnAction(event -> strLayout = "Layout3");
                 
        screenLayout.selectedToggleProperty().addListener((obs, oldValue, newValue) -> dirty.setValue(true));

        load();

    }

           
            
    @Override
    public void load() {
        strLayout = (AppConfig.getInstance().getProperty("machine.saleslayout") == null ? "" : AppConfig.getInstance().getProperty("machine.saleslayout"));
        switch (strLayout) {
            case "Layout1":
                screenLayout.selectToggle(rbtnLayout1);
                break;
            case "Layout2":
                screenLayout.selectToggle(rbtnLayout2);
                break;
            case "Layout3":
                screenLayout.selectToggle(rbtnLayout3);
                break;
            default:
                screenLayout.selectToggle(rbtnLayout0);
                break;
        }
    }

    @Override
    public void save() {
        AppConfig.getInstance().setProperty("machine.saleslayout", strLayout);
        dirty.set(false);
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
