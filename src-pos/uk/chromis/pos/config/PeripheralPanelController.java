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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import uk.chromis.custom.controls.DeviceConfig;
import uk.chromis.custom.controls.LabeledComboBox;
import uk.chromis.custom.controls.LabeledTextField;
import uk.chromis.custom.controls.ScannerConfig;
import uk.chromis.custom.controls.LabeledToggleSwitch;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.util.StringParser;

/**
 * FXML Controller class
 *
 * @author John
 */
public class PeripheralPanelController implements Initializable, BaseController {

    public LabeledComboBox customerDisplay;
    public LabeledComboBox customerDisplayMode;
    public LabeledComboBox customerDisplayPort;
    public LabeledToggleSwitch customerDisplayToggle;
    public LabeledTextField customerDisplayJavaName;

    public LabeledComboBox printer;
    public ComboBox printerList;
    public LabeledToggleSwitch receiptToggle;
    public LabeledComboBox printerMode;
    public LabeledComboBox printerPort;
    public LabeledTextField oposPrinter;
    public LabeledTextField oposDrawer;

    public LabeledComboBox printer2;
    public ComboBox printerList2;
    public LabeledToggleSwitch receiptToggle2;
    public LabeledComboBox printerMode2;
    public LabeledComboBox printerPort2;
    public LabeledTextField oposPrinter2;
    public LabeledTextField oposDrawer2;

    public LabeledComboBox printer3;
    public ComboBox printerList3;
    public LabeledToggleSwitch receiptToggle3;
    public LabeledComboBox printerMode3;
    public LabeledComboBox printerPort3;
    public LabeledTextField oposPrinter3;
    public LabeledTextField oposDrawer3;

    public LabeledComboBox printer4;
    public ComboBox printerList4;
    public LabeledToggleSwitch receiptToggle4;
    public LabeledComboBox printerMode4;
    public LabeledComboBox printerPort4;
    public LabeledTextField oposPrinter4;
    public LabeledTextField oposDrawer4;

    public LabeledComboBox printer5;
    public ComboBox printerList5;
    public LabeledToggleSwitch receiptToggle5;
    public LabeledComboBox printerMode5;
    public LabeledComboBox printerPort5;
    public LabeledTextField oposPrinter5;
    public LabeledTextField oposDrawer5;

    public LabeledComboBox printer6;
    public ComboBox printerList6;
    public LabeledToggleSwitch receiptToggle6;
    public LabeledComboBox printerMode6;
    public LabeledComboBox printerPort6;
    public LabeledTextField oposPrinter6;
    public LabeledTextField oposDrawer6;

    public DeviceConfig scale;
    public ScannerConfig scanner;

    public LabeledComboBox reportPrinter;

    public BooleanProperty dirty = new SimpleBooleanProperty();
    private PrintService[] printServices;
    private ObservableList<String> printers;
    private ObservableList<String> displays;
    private ObservableList<String> mode;
    private ObservableList<String> ports;
    private ObservableList<String> systemprinters;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Build the liost for the comboboxs
        displays = FXCollections.observableArrayList("Not defined",
                "dual screen",
                "window",
                "javapos",
                "epson",
                "ld200",
                "surepost"
        );

        mode = FXCollections.observableArrayList("serial",
                "file",
                "raw",
                "usb");

        ports = FXCollections.observableArrayList("COM1",
                "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8",
                "COM9", "COM10", "COM11", "COM12", "/dev/ttyS0", "/dev/ttyS1",
                "/dev/ttyS2", "/dev/ttyS3", "/dev/ttyS4", "/dev/ttyS5");

        ObservableList<String> scanners = FXCollections.observableArrayList("Not defined", "scanpal2");

        ObservableList<String> scales = FXCollections.observableArrayList("Not defined",
                "screen",
                "casiopd1",
                "caspdii",
                "dialog1",
                "samsungesp",
                "Adam Equipment"
        );

        printers = FXCollections.observableArrayList("Not defined",
                "screen",
                "printer",
                "epson",
                "tmu220",
                "star",
                "ithaca",
                "surepos",
                "javapos");

        printServices = PrintServiceLookup.lookupPrintServices(null, null);
        String[] printernames = getPrintNames();
        systemprinters = FXCollections.observableArrayList("(Default)",
                "(Show dialog)");
        systemprinters.addAll(printernames);

        //Build the options in the panel        
        customerDisplayToggle.setLabelWidth(175.0);
        createPrinter("Label.MachineDisplay", customerDisplay, null, customerDisplayToggle, customerDisplayMode, customerDisplayPort, customerDisplayJavaName, null);
        customerDisplay.addItemList(displays);
        customerDisplayToggle.setText(AppLocal.getIntString("label.customerscreen"));
        createPrinter("Label.MachinePrinter", printer, printerList, receiptToggle, printerMode, printerPort, oposPrinter, oposDrawer);
        createPrinter("Label.MachinePrinter2", printer2, printerList2, receiptToggle2, printerMode2, printerPort2, oposPrinter2, oposDrawer2);
        createPrinter("Label.MachinePrinter3", printer3, printerList3, receiptToggle3, printerMode3, printerPort3, oposPrinter3, oposDrawer3);
        createPrinter("Label.MachinePrinter4", printer4, printerList4, receiptToggle4, printerMode4, printerPort4, oposPrinter4, oposDrawer4);
        createPrinter("Label.MachinePrinter5", printer5, printerList5, receiptToggle5, printerMode5, printerPort5, oposPrinter5, oposDrawer5);
        createPrinter("Label.MachinePrinter6", printer6, printerList6, receiptToggle6, printerMode6, printerPort6, oposPrinter6, oposDrawer6);

        scale.setDeviceParameters(scales, ports);
        scale.setLabelText("deviceLabel", AppLocal.getIntString("label.scale"));
        scale.setLabelText("portLabel", AppLocal.getIntString("label.machinedisplayport"));

        scanner.setScannerParameters(scanners, ports);
        scanner.setLabelText("deviceLabel", AppLocal.getIntString("label.scanner"));
        scanner.setLabelText("portLabel", AppLocal.getIntString("label.machinedisplayport"));

        reportPrinter.setLabel(AppLocal.getIntString("label.receiptprinter"));
        reportPrinter.addItemList(systemprinters);

        load();
        dirty.bindBidirectional(customerDisplay.dirty);
        dirty.bindBidirectional(scale.dirty);
        dirty.bindBidirectional(scanner.dirty);
        dirty.bindBidirectional(reportPrinter.dirty);
    }

    private void createPrinter(String message, LabeledComboBox tPrinter, ComboBox tPrinterList, LabeledToggleSwitch tReceiptToggle,
            LabeledComboBox tPrinterMode, LabeledComboBox tPrinterPort, LabeledTextField tOposPrinter, LabeledTextField tOposDrawer) {

        tPrinter.setLabel(AppLocal.getIntString(message));
        tPrinter.addItemList(printers);
        tReceiptToggle.setVisible(false);
        tReceiptToggle.setText(AppLocal.getIntString("label.receiptprinter"));
        tOposPrinter.setVisible(false);
        tOposPrinter.setLayoutX(340);
        tOposPrinter.setWidthSizes(70.0, 100.0);
        tOposPrinter.setLabel(AppLocal.getIntString("label.javapos.printer"));
        if (tOposDrawer != null) {
            tOposDrawer.setVisible(false);
            tOposDrawer.setLayoutX(530);
            tOposDrawer.setWidthSizes(55.0, 100.0);
            tOposDrawer.setLabel(AppLocal.getIntString("label.javapos.drawer"));
        }
        tPrinterMode.setVisible(false);
        tPrinterMode.setLayoutX(340);
        tPrinterMode.setWidthSizes(70.0, 100.0);
        tPrinterMode.setLabel(AppLocal.getIntString("label.machinedisplayconn"));
        tPrinterMode.addItemList(mode);
        tPrinterMode.getComboBox().getSelectionModel().selectFirst();
        tPrinterPort.setVisible(false);
        tPrinterPort.setLayoutX(530);
        tPrinterPort.setWidthSizes(55.0, 200.0);
        tPrinterPort.setLabel(AppLocal.getIntString("label.machinedisplayport"));
        tPrinterPort.addItemList(ports);
        tPrinterPort.getComboBox().getSelectionModel().selectFirst();
        if (tPrinterList != null) {
            tPrinterList.setVisible(false);
            tPrinterList.getItems().addAll(systemprinters);
            tPrinterList.getSelectionModel().selectFirst();
        }

        tPrinter.getComboBox().getSelectionModel().selectedItemProperty().addListener((new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                switch (newValue.toString()) {
                    case "Not defined":
                    case "screen":
                        if (tPrinterList != null) {
                            tPrinterList.setVisible(false);
                        }
                        tReceiptToggle.setVisible(false);
                        tOposPrinter.setVisible(false);
                        if (tOposDrawer != null) {
                            tOposDrawer.setVisible(false);
                        }
                        tPrinterMode.setVisible(false);
                        tPrinterPort.setVisible(false);
                        break;
                    case "printer":
                        if (tPrinterList != null) {
                            tPrinterList.setVisible(true);
                        }
                        tReceiptToggle.setVisible(true);
                        tOposPrinter.setVisible(false);
                        if (tOposDrawer != null) {
                            tOposDrawer.setVisible(false);
                        }
                        tPrinterMode.setVisible(false);
                        tPrinterPort.setVisible(false);
                        tPrinterPort.setWidthSizes(55.0, 200.0);
                        tPrinterPort.getComboBox().setEditable(true);
                        break;
                    case "javapos":
                        if (tPrinterList != null) {
                            tPrinterList.setVisible(false);
                        }
                        tReceiptToggle.setVisible(false);
                        tOposPrinter.setVisible(true);
                        if (tOposDrawer != null) {
                            tOposDrawer.setVisible(true);
                        }
                        tPrinterMode.setVisible(false);
                        tPrinterPort.setVisible(false);
                        break;
                    case "dual screen":
                    case "window":
                        if (tPrinterList != null) {
                            tPrinterList.setVisible(false);
                        }
                        tOposPrinter.setVisible(false);
                        if (tOposDrawer != null) {
                            tOposDrawer.setVisible(false);
                        }
                        tPrinterMode.setVisible(false);
                        tPrinterPort.setVisible(false);
                        tReceiptToggle.setVisible(true);
                        break;
                    default:
                        if (tPrinterList != null) {
                            tPrinterList.setVisible(false);
                        }
                        tReceiptToggle.setVisible(false);
                        tOposPrinter.setVisible(false);
                        if (tOposDrawer != null) {
                            tOposDrawer.setVisible(false);
                        }
                        tPrinterMode.setVisible(true);
                        tPrinterPort.setVisible(true);
                        tPrinterPort.setWidthSizes(55.0, 200.0);
                        tPrinterPort.getComboBox().setEditable(true);
                        break;
                }
            }
        }));

        tPrinterMode.getComboBox().getSelectionModel().selectedItemProperty().addListener((new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                switch (newValue.toString()) {
                    case "serial":
                    case "file":
                        tPrinterPort.setLayoutX(530);
                        tPrinterPort.setWidthSizes(55.0, 100.0);
                        tPrinterPort.setLabel(AppLocal.getIntString("label.machinedisplayport"));
                        tPrinterPort.addItemList(ports);
                        break;
                    case "raw":
                    case "usb":
                        tPrinterPort.setLayoutX(530);
                        tPrinterPort.setWidthSizes(55.0, 200.0);
                        tPrinterPort.setLabel(AppLocal.getIntString("label.javapos.printer"));
                        tPrinterPort.addItemList(systemprinters);
                        break;
                }
                dirty.set(true);
            }
        }));

        tPrinterPort.getComboBox().getSelectionModel().selectedItemProperty().addListener((new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                dirty.set(true);
            }
        }));

        if (tPrinterList != null) {
            tPrinterList.getSelectionModel().selectedItemProperty().addListener((new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    dirty.setValue(true);
                }
            }));
        }

        tOposPrinter.getTextField().textProperty().addListener((new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                dirty.setValue(true);

            }
        }));

        if (tOposDrawer != null) {
            tOposDrawer.getTextField().textProperty().addListener((new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    dirty.set(true);
                }
            }));
        }
        dirty.bindBidirectional(tPrinter.dirty);
        dirty.bindBidirectional(tReceiptToggle.dirty);
    }

    public static String[] getPrintNames() {
        PrintService[] pservices = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);
        String printers[] = new String[pservices.length];
        for (int i = 0; i < pservices.length; i++) {
            printers[i] = pservices[i].getName();
        }
        return printers;
    }

    @Override
    public void save() {
        AppConfig.getInstance().setProperty("machine.display", getParams(customerDisplay, null, null, customerDisplayMode, customerDisplayPort, customerDisplayJavaName, null));
        AppConfig.getInstance().setBoolean("machine.customerdisplay", customerDisplayToggle.isSelected());
        AppConfig.getInstance().setProperty("machine.printer", getParams(printer, printerList, receiptToggle, printerMode, printerPort, oposPrinter, oposDrawer));
        AppConfig.getInstance().setProperty("machine.printer.2", getParams(printer2, printerList2, receiptToggle2, printerMode2, printerPort2, oposPrinter2, oposDrawer2));
        AppConfig.getInstance().setProperty("machine.printer.3", getParams(printer3, printerList3, receiptToggle3, printerMode3, printerPort3, oposPrinter3, oposDrawer3));
        AppConfig.getInstance().setProperty("machine.printer.4", getParams(printer4, printerList4, receiptToggle4, printerMode4, printerPort4, oposPrinter4, oposDrawer4));
        AppConfig.getInstance().setProperty("machine.printer.5", getParams(printer5, printerList5, receiptToggle5, printerMode5, printerPort5, oposPrinter5, oposDrawer5));
        AppConfig.getInstance().setProperty("machine.printer.6", getParams(printer6, printerList6, receiptToggle6, printerMode6, printerPort6, oposPrinter6, oposDrawer6));
        AppConfig.getInstance().setProperty("machine.scale", scale.getDeviceParams());
        AppConfig.getInstance().setProperty("machine.scanner", scanner.getScannerParams());
        AppConfig.getInstance().setProperty("machine.printername", comboValue(reportPrinter.getSelected()));
        //  AppConfig.getInstance().setProperty("machine.overrideprinter", comboValue(overridePrinter.getSelected()));
        dirty.setValue(false);
    }

    private String comboValue(Object value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public Boolean isDirty() {
        return dirty.getValue();
    }

    @Override
    public void setDirty(Boolean value) {
        dirty.setValue(true);
    }

    @Override
    public void load() {
        customerDisplayToggle.setSelected(AppConfig.getInstance().getBoolean("machine.customerdisplay"));
        setParameters(AppConfig.getInstance().getProperty("machine.display"), customerDisplay, null, null, customerDisplayMode, customerDisplayPort, customerDisplayJavaName, null);
        setParameters(AppConfig.getInstance().getProperty("machine.printer"), printer, printerList, receiptToggle, printerMode, printerPort, oposPrinter, oposDrawer);
        setParameters(AppConfig.getInstance().getProperty("machine.printer.2"), printer2, printerList2, receiptToggle2, printerMode2, printerPort2, oposPrinter2, oposDrawer2);
        setParameters(AppConfig.getInstance().getProperty("machine.printer.3"), printer3, printerList3, receiptToggle3, printerMode3, printerPort3, oposPrinter3, oposDrawer3);
        setParameters(AppConfig.getInstance().getProperty("machine.printer.4"), printer4, printerList4, receiptToggle4, printerMode4, printerPort4, oposPrinter4, oposDrawer4);
        setParameters(AppConfig.getInstance().getProperty("machine.printer.5"), printer5, printerList5, receiptToggle5, printerMode5, printerPort5, oposPrinter5, oposDrawer5);
        setParameters(AppConfig.getInstance().getProperty("machine.printer.6"), printer6, printerList6, receiptToggle6, printerMode6, printerPort6, oposPrinter6, oposDrawer6);
        scale.setDevice(AppConfig.getInstance().getProperty("machine.scale"));
        scanner.setScanner(AppConfig.getInstance().getProperty("machine.scanner"));
        reportPrinter.setSelected(AppConfig.getInstance().getProperty("machine.printername"));
        dirty.setValue(false);
    }

    public void setParameters(String params, LabeledComboBox tPrinter, ComboBox tPrinterList, LabeledToggleSwitch tReceiptToggle,
            LabeledComboBox tPrinterMode, LabeledComboBox tPrinterPort, LabeledTextField tOposPrinter, LabeledTextField tOposDrawer) {
        StringParser p = new StringParser(params);
        String sparam = unifySerialInterface(p.nextToken(':'));
        switch (sparam) {
            case "serial":
            case "file":
                tPrinter.getComboBox().getSelectionModel().select("epson");
                tPrinterMode.getComboBox().getSelectionModel().select(sparam);
                tPrinterPort.getComboBox().getSelectionModel().select(p.nextToken(','));
                break;
            case "javapos":
                tPrinter.getComboBox().getSelectionModel().select(sparam);
                tOposPrinter.setText(p.nextToken(','));
                if (tOposDrawer != null) {
                    tOposDrawer.setText(p.nextToken(','));
                }
                break;
            case "printer":
                tPrinter.getComboBox().getSelectionModel().select(sparam);
                tPrinterList.getSelectionModel().select(p.nextToken(','));
                if ("receipt".equals(p.nextToken(','))) {
                    tReceiptToggle.setSelected(true);
                } else {
                    tReceiptToggle.setSelected(false);
                }
                break;
            default:
                tPrinter.getComboBox().getSelectionModel().select(sparam);
                tPrinterMode.getComboBox().getSelectionModel().select(unifySerialInterface(p.nextToken(',')));
                tPrinterPort.getComboBox().getSelectionModel().select(p.nextToken(','));
        }
    }

    private String unifySerialInterface(String sparam) {
        // for backward compatibility
        return ("rxtx".equals(sparam))
                ? "serial"
                : sparam;
    }

    public String getParams(LabeledComboBox tPrinter, ComboBox tPrinterList, LabeledToggleSwitch tReceiptToggle,
            LabeledComboBox tPrinterMode, LabeledComboBox tPrinterPort, LabeledTextField tOposPrinter, LabeledTextField tOposDrawer) {
        String printer = tPrinter.getComboBox().getSelectionModel().getSelectedItem().toString();
        StringBuilder tmp;
        switch (printer) {
            case "Not defined":
            case "screen":
            case "window":
            case "dual screen":
                return printer;
            case "javapos":
                tmp = new StringBuilder();
                tmp.append(printer);
                tmp.append(":");
                tmp.append(tOposPrinter.getText());
                if (tOposDrawer != null) {
                    tmp.append(",");
                    tmp.append(tOposDrawer.getText());
                }
                return tmp.toString();
            case "printer":
                tmp = new StringBuilder();
                tmp.append(printer);
                tmp.append(":");
                if (tPrinterList.getSelectionModel().getSelectedItem() != null) {
                    tmp.append(tPrinterList.getSelectionModel().getSelectedItem().toString());
                    tmp.append(",");
                }

                if (tReceiptToggle.isSelected()) {
                    tmp.append("receipt");
                } else {
                    tmp.append("standard");
                }
                return tmp.toString();
            default:
                tmp = new StringBuilder();
                tmp.append(printer);
                tmp.append(":");
                tmp.append(tPrinterMode.getComboBox().getSelectionModel().getSelectedItem().toString());
                tmp.append(",");
                tmp.append(tPrinterPort.getComboBox().getSelectionModel().getSelectedItem().toString());
                return tmp.toString();
        }
    }

}
