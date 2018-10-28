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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import uk.chromis.custom.controls.DeviceConfig;
import uk.chromis.custom.controls.DisplayConfig;
import uk.chromis.custom.controls.LabeledComboBox;
import uk.chromis.custom.controls.PrinterConfig;
import uk.chromis.custom.controls.ScannerConfig;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;

/**
 * FXML Controller class
 *
 * @author John
 */
public class PeripheralPanelController implements Initializable, BaseController {

    public DisplayConfig customerDisplay;
    public PrinterConfig printer1;
    public PrinterConfig printer2;
    public PrinterConfig printer3;
    public PrinterConfig printer4;
    public PrinterConfig printer5;
    public PrinterConfig printer6;
    public DeviceConfig scale;
    public ScannerConfig scanner;
    public LabeledComboBox reportPrinter;
  //  public LabeledComboBox overridePrinter;

    public BooleanProperty dirty = new SimpleBooleanProperty();
    private PrintService[] printServices;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> scanners = FXCollections.observableArrayList("Not defined", "scanpal2");
        ObservableList<String> scales = FXCollections.observableArrayList("Not defined",
                "screen",
                "casiopd1",
                "caspdii",
                "dialog1",
                "samsungesp",
                "Adam Equipment"
        );

        ObservableList<String> displays = FXCollections.observableArrayList("Not defined",
                "dual screen",
                "window",
                "javapos",
                "epson",
                "ld200",
                "surepost"
        );

        ObservableList<String> printers = FXCollections.observableArrayList("Not defined",
                "screen",
                "printer",
                "epson",
                "tmu220",
                "star",
                "ithaca",
                "surepos",
                "javapos");

        ObservableList<String> mode = FXCollections.observableArrayList("serial",
                "file",
                "raw",
                "usb");

        ObservableList<String> ports = FXCollections.observableArrayList("COM1",
                "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8",
                "COM9", "COM10", "COM11", "COM12", "/dev/ttyS0", "/dev/ttyS1",
                "/dev/ttyS2", "/dev/ttyS3", "/dev/ttyS4", "/dev/ttyS5");

        printServices = PrintServiceLookup.lookupPrintServices(null, null);
        String[] printernames = getPrintNames();

        ObservableList<String> systemprinters = FXCollections.observableArrayList("(Default)",
                "(Show dialog)");
        systemprinters.addAll(printernames);

        ObservableList<String> overridePtrs = FXCollections.observableArrayList("Printer 1",
                "Printer 2",
                "Printer 3",
                "Printer 4",
                "Printer 5",
                "Printer 6");

        dirty.bindBidirectional(customerDisplay.dirty);
        customerDisplay.setDisplayParameters(displays, ports, mode);
        customerDisplay.setLabelText("deviceLabel", AppLocal.getIntString("Label.MachineDisplay"));
        customerDisplay.setLabelText("portLabel", AppLocal.getIntString("label.machinedisplayport"));
        customerDisplay.setLabelText("modeLabel", AppLocal.getIntString("label.machinedisplayconn"));
        customerDisplay.setLabelText("toggleswitch", AppLocal.getIntString("label.customerscreen"));
        customerDisplay.setLabelText("javaOposLabel", AppLocal.getIntString("Label.Name"));

        dirty.bindBidirectional(printer1.dirty);
        printer1.setPrinterParameters(printers, ports, mode, systemprinters);
        printer1.setLabelText("mainPrinterLabel", AppLocal.getIntString("Label.MachinePrinter"));
        printer1.setLabelText("portLabel", AppLocal.getIntString("label.machinedisplayport"));
        printer1.setLabelText("modeLabel", AppLocal.getIntString("label.machinedisplayconn"));
        printer1.setLabelText("drawerLabel", AppLocal.getIntString("label.javapos.drawer"));
        printer1.setLabelText("printerLabel", AppLocal.getIntString("label.javapos.printer"));
        printer1.setLabelText("receiptprinter", AppLocal.getIntString("label.receiptprinter"));

        dirty.bindBidirectional(printer2.dirty);
        printer2.setPrinterParameters(printers, ports, mode, systemprinters);
        printer2.setLabelText("mainPrinterLabel", AppLocal.getIntString("Label.MachinePrinter2"));
        printer2.setLabelText("portLabel", AppLocal.getIntString("label.machinedisplayport"));
        printer2.setLabelText("modeLabel", AppLocal.getIntString("label.machinedisplayconn"));
        printer2.setLabelText("drawerLabel", AppLocal.getIntString("label.javapos.drawer"));
        printer2.setLabelText("printerLabel", AppLocal.getIntString("label.javapos.printer"));
        printer2.setLabelText("receiptprinter", AppLocal.getIntString("label.receiptprinter"));

        dirty.bindBidirectional(printer3.dirty);
        printer3.setPrinterParameters(printers, ports, mode, systemprinters);
        printer3.setLabelText("mainPrinterLabel", AppLocal.getIntString("Label.MachinePrinter3"));
        printer3.setLabelText("portLabel", AppLocal.getIntString("label.machinedisplayport"));
        printer3.setLabelText("modeLabel", AppLocal.getIntString("label.machinedisplayconn"));
        printer3.setLabelText("drawerLabel", AppLocal.getIntString("label.javapos.drawer"));
        printer3.setLabelText("printerLabel", AppLocal.getIntString("label.javapos.printer"));
        printer3.setLabelText("receiptprinter", AppLocal.getIntString("label.receiptprinter"));

        dirty.bindBidirectional(printer4.dirty);
        printer4.setPrinterParameters(printers, ports, mode, systemprinters);
        printer4.setLabelText("mainPrinterLabel", AppLocal.getIntString("Label.MachinePrinter4"));
        printer4.setLabelText("portLabel", AppLocal.getIntString("label.machinedisplayport"));
        printer4.setLabelText("modeLabel", AppLocal.getIntString("label.machinedisplayconn"));
        printer4.setLabelText("drawerLabel", AppLocal.getIntString("label.javapos.drawer"));
        printer4.setLabelText("printerLabel", AppLocal.getIntString("label.javapos.printer"));
        printer4.setLabelText("receiptprinter", AppLocal.getIntString("label.receiptprinter"));

        dirty.bindBidirectional(printer5.dirty);
        printer5.setPrinterParameters(printers, ports, mode, systemprinters);
        printer5.setLabelText("mainPrinterLabel", AppLocal.getIntString("Label.MachinePrinter5"));
        printer5.setLabelText("portLabel", AppLocal.getIntString("label.machinedisplayport"));
        printer5.setLabelText("modeLabel", AppLocal.getIntString("label.machinedisplayconn"));
        printer5.setLabelText("drawerLabel", AppLocal.getIntString("label.javapos.drawer"));
        printer5.setLabelText("printerLabel", AppLocal.getIntString("label.javapos.printer"));
        printer5.setLabelText("receiptprinter", AppLocal.getIntString("label.receiptprinter"));

        dirty.bindBidirectional(printer6.dirty);
        printer6.setPrinterParameters(printers, ports, mode, systemprinters);
        printer6.setLabelText("mainPrinterLabel", AppLocal.getIntString("Label.MachinePrinter6"));
        printer6.setLabelText("portLabel", AppLocal.getIntString("label.machinedisplayport"));
        printer6.setLabelText("modeLabel", AppLocal.getIntString("label.machinedisplayconn"));
        printer6.setLabelText("drawerLabel", AppLocal.getIntString("label.javapos.drawer"));
        printer6.setLabelText("printerLabel", AppLocal.getIntString("label.javapos.printer"));
        printer6.setLabelText("receiptprinter", AppLocal.getIntString("label.receiptprinter"));

        dirty.bindBidirectional(reportPrinter.dirty);
        reportPrinter.addItemList(systemprinters);
        reportPrinter.setLabel(AppLocal.getIntString("label.reportsprinter"));
/*
        dirty.bindBidirectional(overridePrinter.dirty);
        overridePrinter.addItemList(overridePtrs);
        overridePrinter.setLabel("Override Printer");
*/
        dirty.bindBidirectional(scale.dirty);
        scale.setDeviceParameters(scales, ports);
        scale.setLabelText("deviceLabel", AppLocal.getIntString("label.scale"));
        scale.setLabelText("portLabel", AppLocal.getIntString("label.machinedisplayport"));

        dirty.bindBidirectional(scanner.dirty);
        scanner.setScannerParameters(scanners, ports);
        scanner.setLabelText("deviceLabel", AppLocal.getIntString("label.scanner"));
        scanner.setLabelText("portLabel", AppLocal.getIntString("label.machinedisplayport"));

        load();
    }

    public static String[] getPrintNames() {
        PrintService[] pservices
                = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);

        String printers[] = new String[pservices.length];
        for (int i = 0; i < pservices.length; i++) {
            printers[i] = pservices[i].getName();
        }

        return printers;
    }

    @Override
    public void load() {
        printer1.setPrinter(AppConfig.getInstance().getProperty("machine.printer"));
        printer2.setPrinter(AppConfig.getInstance().getProperty("machine.printer.2"));
        printer3.setPrinter(AppConfig.getInstance().getProperty("machine.printer.3"));
        printer4.setPrinter(AppConfig.getInstance().getProperty("machine.printer.4"));
        printer5.setPrinter(AppConfig.getInstance().getProperty("machine.printer.5"));
        printer6.setPrinter(AppConfig.getInstance().getProperty("machine.printer.6"));
  /*
        if (AppConfig.getInstance().getProperty("machine.overrideprinter") == null) {
            overridePrinter.setSelected("Printer 2");
        } else {
            overridePrinter.setSelected(AppConfig.getInstance().getProperty("machine.overrideprinter"));
        }
*/
        customerDisplay.setDisplay(AppConfig.getInstance().getProperty("machine.display"), false);
        customerDisplay.setDisplay(AppConfig.getInstance().getProperty("machine.display"), AppConfig.getInstance().getBoolean("machine.customerdisplay"));
        reportPrinter.setSelected(AppConfig.getInstance().getProperty("machine.printername"));
        scale.setDevice(AppConfig.getInstance().getProperty("machine.scale"));
        scanner.setScanner(AppConfig.getInstance().getProperty("machine.scanner"));

        dirty.setValue(false);
    }

    @Override
    public void save() {
        AppConfig.getInstance().setProperty("machine.printer", printer1.getPrinterParams());
        AppConfig.getInstance().setProperty("machine.printer.2", printer2.getPrinterParams());
        AppConfig.getInstance().setProperty("machine.printer.3", printer3.getPrinterParams());
        AppConfig.getInstance().setProperty("machine.printer.4", printer4.getPrinterParams());
        AppConfig.getInstance().setProperty("machine.printer.5", printer5.getPrinterParams());
        AppConfig.getInstance().setProperty("machine.printer.6", printer6.getPrinterParams());
        AppConfig.getInstance().setProperty("machine.scale", scale.getDeviceParams());
        AppConfig.getInstance().setProperty("machine.scanner", scanner.getScannerParams());
        AppConfig.getInstance().setProperty("machine.display", customerDisplay.getDisplayParams());
        AppConfig.getInstance().setBoolean("machine.customerdisplay", customerDisplay.isToggleSelected());
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

}
