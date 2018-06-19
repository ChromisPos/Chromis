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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import uk.chromis.custom.controls.LabeledComboBox;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;

/**
 * FXML Controller class
 *
 * @author John.Lewis
 */
public class LocalePanelController implements Initializable, BaseController {

    @FXML
    private LabeledComboBox lcbLocale;
    @FXML
    private LabeledComboBox lcbInteger;
    @FXML
    private LabeledComboBox lcbDouble;
    @FXML
    private LabeledComboBox lcbCurrency;
    @FXML
    private LabeledComboBox lcbPercent;
    @FXML
    private LabeledComboBox lcbDate;
    @FXML
    private LabeledComboBox lcbTime;
    @FXML
    private LabeledComboBox lcbDateTime;

    public TextField txtDate;
    public TextField txtTime;
    public TextField txtDateTime;

    public BooleanProperty dirty = new SimpleBooleanProperty(false);
    private final static String DEFAULT_VALUE = "(Default)";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dirty.bindBidirectional(lcbLocale.dirty);
        dirty.bindBidirectional(lcbDate.dirty);
        dirty.bindBidirectional(lcbInteger.dirty);
        dirty.bindBidirectional(lcbDouble.dirty);
        dirty.bindBidirectional(lcbCurrency.dirty);
        dirty.bindBidirectional(lcbPercent.dirty);
        dirty.bindBidirectional(lcbDate.dirty);
        dirty.bindBidirectional(lcbTime.dirty);
        dirty.bindBidirectional(lcbDateTime.dirty);

        List<Locale> availablelocales = new ArrayList<Locale>();
        availablelocales.addAll(Arrays.asList(Locale.getAvailableLocales())); // Available java locales
        addLocale(availablelocales, new Locale("eu", "ES", "")); // Basque
        addLocale(availablelocales, new Locale("gl", "ES", "")); // Gallegan

        Collections.sort(availablelocales, new LocaleComparator());

        lcbLocale.getComboBox().getItems().add(new LocaleInfo(null));
        for (Locale l : availablelocales) {
            // only include locales that have a translation file available
            if (l.getLanguage().equals("en")
                    || l.getLanguage().equals("ar") // Arabic 
                    || l.getLanguage().equals("sq") // Albanian                       
                    || l.getLanguage().equals("es") // Spanish
                    || l.getLanguage().equals("hr") // Croatian
                    || l.getLanguage().equals("nl") // Dutch
                    || l.getLanguage().equals("et") // Estonian
                    || l.getLanguage().equals("fr") // French
                    || l.getLanguage().equals("de") // German
                    || l.getLanguage().equals("it") // Italian
                    ) {
                lcbLocale.getComboBox().getItems().add(new LocaleInfo(l));
            }
        }

        ObservableList<String> date = FXCollections.observableArrayList(
                "dd.MM.yy", "dd.MM.yyyy", "MM.dd.yy", "MM.dd.yyyy", "EEE, MMM d, yy", "EEE, MMM d, yyyy",
                "EEE, MMMM d, yy", "EEE, MMMM d, yyyy", "EEEE, MMMM d, yy", "EEEE, MMMM d, yyyy"
        );

        ObservableList<String> time = FXCollections.observableArrayList(
                "h:mm", "h:mm:ss", "h:mm a", "h:mm:ss a", "H:mm", "H:mm:ss", "H:mm a", "H:mm:ss a"
        );

        ObservableList<String> dateTime = FXCollections.observableArrayList(
                "dd.MM.yy, H:mm", "dd.MM.yy, H:mm", "MM.dd.yy, H:mm", "MM.dd.yy, H:mm",
                "dd.MM.yyyy, H:mm", "dd.MM.yyyy, H:mm", "MM.dd.yyyy, H:mm", "MM.dd.yyyy, H:mm",
                "EEE, MMMM d yyyy, H:mm", "EEEE, MMMM d yyyy, H:mm"
        );

        ObservableList<String> integers = FXCollections.observableArrayList(
                DEFAULT_VALUE, "#0", "#,##0"
        );

        ObservableList<String> currency = FXCollections.observableArrayList(
                DEFAULT_VALUE, "\u00A4 #0.00", "'$' #,##0.00"
        );

        ObservableList<String> doubles = FXCollections.observableArrayList(
                DEFAULT_VALUE, "#0.0", "#,##0.#"
        );

        ObservableList<String> percent = FXCollections.observableArrayList(
                DEFAULT_VALUE, "#,##0.##%"
        );

        lcbLocale.setLabel(AppLocal.getIntString("label.locale"));
        lcbLocale.setWidthSizes(120.0, 250.0);

        lcbInteger.setLabel(AppLocal.getIntString("label.integer"));
        lcbInteger.setWidthSizes(120.0, 250.0);
        lcbInteger.addItemList(integers);

        lcbDouble.setLabel(AppLocal.getIntString("label.double"));
        lcbDouble.setWidthSizes(120.0, 250.0);
        lcbDouble.addItemList(integers);

        lcbCurrency.setLabel(AppLocal.getIntString("label.currency"));
        lcbCurrency.setWidthSizes(120.0, 250.0);
        lcbCurrency.addItemList(currency);

        lcbPercent.setLabel(AppLocal.getIntString("label.percent"));
        lcbPercent.setWidthSizes(120.0, 250.0);
        lcbPercent.addItemList(percent);

        lcbDate.setLabel(AppLocal.getIntString("label.date"));
        lcbDate.setWidthSizes(120.0, 250.0);
        lcbDate.addItemList(date);

        lcbTime.setLabel(AppLocal.getIntString("label.time"));
        lcbTime.setWidthSizes(120.0, 250.0);
        lcbTime.addItemList(time);

        lcbDateTime.setLabel(AppLocal.getIntString("label.datetime"));
        lcbDateTime.setWidthSizes(120.0, 250.0);
        lcbDateTime.addItemList(dateTime);

        load();

        lcbDate.getComboBox().valueProperty().addListener((ov, t, t1) -> {
            if (!lcbDate.getComboBox().getSelectionModel().getSelectedItem().toString().equals("")) {
                Formats.setDatePattern(lcbDate.getComboBox().getSelectionModel().getSelectedItem().toString());
            }
            txtDate.setText(Formats.DATE.formatValue(new Date()));
        });

        lcbTime.getComboBox().valueProperty().addListener((ov, t, t1) -> {
            if (!lcbTime.getComboBox().getSelectionModel().getSelectedItem().toString().equals("")) {
                Formats.setDatePattern(lcbTime.getComboBox().getSelectionModel().getSelectedItem().toString());
            }
            txtTime.setText(Formats.DATE.formatValue(new Date()));
        });

        lcbDateTime.getComboBox().valueProperty().addListener((ov, t, t1) -> {
            if (!lcbDateTime.getComboBox().getSelectionModel().getSelectedItem().toString().equals("")) {
                Formats.setDatePattern(lcbDateTime.getComboBox().getSelectionModel().getSelectedItem().toString());
            }
            txtDateTime.setText(Formats.DATE.formatValue(new Date()));
        });

    }

    @Override
    public void load() {
        lcbInteger.setSelected(writeWithDefault(AppConfig.getInstance().getProperty("format.integer")));
        lcbDouble.setSelected(writeWithDefault(AppConfig.getInstance().getProperty("format.double")));
        lcbCurrency.setSelected(writeWithDefault(AppConfig.getInstance().getProperty("format.currency")));
        lcbPercent.setSelected(writeWithDefault(AppConfig.getInstance().getProperty("format.percent")));
        lcbDate.setSelected(writeWithDefault(AppConfig.getInstance().getProperty("format.date")));
        lcbTime.setSelected(writeWithDefault(AppConfig.getInstance().getProperty("format.time")));
        lcbDateTime.setSelected(writeWithDefault(AppConfig.getInstance().getProperty("format.datetime")));

        if ((!"".equals(lcbDate.getSelected())) && (!"(Default)".equals(lcbDate.getSelected()))) {
            Formats.setDatePattern(lcbDate.getSelected().toString());
        }
        txtDate.setText(Formats.DATE.formatValue(new Date()));
        if ((!"".equals(lcbTime.getSelected())) && (!"(Default)".equals(lcbTime.getSelected()))) {
            Formats.setDatePattern(lcbTime.getSelected().toString());
        }
        txtTime.setText(Formats.DATE.formatValue(new Date()));
        if ((!"".equals(lcbDateTime.getSelected())) && (!"(Default)".equals(lcbDateTime.getSelected()))) {
            Formats.setDatePattern(lcbDateTime.getSelected().toString());
        }
        txtDateTime.setText(Formats.DATE.formatValue(new Date()));

        String slang = AppConfig.getInstance().getProperty("user.language");
        String scountry = AppConfig.getInstance().getProperty("user.country");
        String svariant = AppConfig.getInstance().getProperty("user.variant");
        
        if (slang != null && !slang.equals("") && scountry != null && svariant != null) {
            Locale currentlocale = new Locale(slang, scountry, svariant);
            for (int i = 0; i < lcbLocale.getComboBox().getItems().size(); i++) {
                LocaleInfo l = (LocaleInfo) lcbLocale.getComboBox().getItems().get(i);
                if (currentlocale.equals(l.getLocale())) {
                    lcbLocale.getComboBox().getSelectionModel().select(i);
                    break;
                }
            }
        } else {
            lcbLocale.getComboBox().getSelectionModel().select(0);
        }
        
        dirty.setValue(false);
    }

    @Override
    public void save() {
   
        Locale l = ((LocaleInfo) lcbLocale.getSelected()).getLocale();
        if (l == null) {
            AppConfig.getInstance().setProperty("user.language", "");
            AppConfig.getInstance().setProperty("user.country", "");
            AppConfig.getInstance().setProperty("user.variant", "");
        } else {
            AppConfig.getInstance().setProperty("user.language", l.getLanguage());
            AppConfig.getInstance().setProperty("user.country", l.getCountry());
            AppConfig.getInstance().setProperty("user.variant", l.getVariant());
        }

        AppConfig.getInstance().setProperty("format.integer", readWithDefault(lcbInteger.getSelected()));
        AppConfig.getInstance().setProperty("format.double", readWithDefault(lcbDouble.getSelected()));
        AppConfig.getInstance().setProperty("format.currency", readWithDefault(lcbCurrency.getSelected()));
        AppConfig.getInstance().setProperty("format.percent", readWithDefault(lcbPercent.getSelected()));
        AppConfig.getInstance().setProperty("format.date", readWithDefault(lcbDate.getSelected()));
        AppConfig.getInstance().setProperty("format.time", readWithDefault(lcbTime.getSelected()));
        AppConfig.getInstance().setProperty("format.datetime", readWithDefault(lcbDateTime.getSelected()));

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

    private void addLocale(List<Locale> ll, Locale l) {
        if (!ll.contains(l)) {
            ll.add(l);
        }
    }

    private String readWithDefault(Object value) {
        if (DEFAULT_VALUE.equals(value)) {
            return "";
        } else {
            return value.toString();
        }
    }

    private String writeWithDefault(String value) {
        if (value == null || value.equals("") || value.equals(DEFAULT_VALUE)) {
            return DEFAULT_VALUE;
        } else {
            return value.toString();
        }
    }

    private static class LAFInfo {

        private final String name;
        private final String classname;

        public LAFInfo(String name, String classname) {
            this.name = name;
            this.classname = classname;
        }

        public String getName() {
            return name;
        }

        public String getClassName() {
            return classname;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class LocaleInfo {

        private Locale locale;

        public LocaleInfo(Locale locale) {
            this.locale = locale;
        }

        public Locale getLocale() {
            return locale;
        }

        @Override
        public String toString() {
            return locale == null
                    ? "(System default)"
                    : locale.getDisplayName();
        }
    }
}
