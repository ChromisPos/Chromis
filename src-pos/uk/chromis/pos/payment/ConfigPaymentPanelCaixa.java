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
package uk.chromis.pos.payment;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import uk.chromis.custom.switches.ToggleSwitch;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.util.AltEncrypter;

public class ConfigPaymentPanelCaixa extends Pane implements PaymentConfiguration {

    @FXML
    private Label lbcMercantCode;
    @FXML
    private Label lblTerminal;
    @FXML
    private Label lblCommerceSign;
    @FXML
    private Label lblSHA;

    @FXML
    private ToggleSwitch sha;

    @FXML
    private TextField txtCommerceCode;
    @FXML
    private TextField txtCommerceTerminal;
    @FXML
    private PasswordField txtCommerceSign;

    public ConfigPaymentPanelCaixa() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfigPaymentPanelCaixa.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ConfigPaymentPanelCaixa.class.getName()).
                    log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

        lbcMercantCode.setText(AppLocal.getIntString("label.merchantcode"));
        lblTerminal.setText(AppLocal.getIntString("label.terminal"));
        lblCommerceSign.setText(AppLocal.getIntString("label.commercesign"));
        lblSHA.setText(AppLocal.getIntString("label.sha"));

        sha.setSwitchLabel("Ampliado");

        loadProperties();
    }

    @Override
    public void loadProperties() {
        String sCommerceID = AppConfig.getInstance().getProperty("payment.commerceid");
        String sCommerceTerminal = AppConfig.getInstance().getProperty("payment.terminal");
        String sCommerceSign = AppConfig.getInstance().getProperty("payment.commercesign");
        String sCommerceSHA = AppConfig.getInstance().getProperty("payment.SHA");

        if (sCommerceID != null && sCommerceTerminal != null && sCommerceSign != null && sCommerceSHA != null && sCommerceSign.startsWith("crypt:")) {
            txtCommerceCode.setText(AppConfig.getInstance().getProperty("payment.commerceid"));
            AltEncrypter cypher = new AltEncrypter("cypherkey");
            txtCommerceTerminal.setText(comboValue(AppConfig.getInstance().getProperty("payment.terminal")));
            txtCommerceSign.setText(cypher.decrypt(AppConfig.getInstance().getProperty("payment.commercesign").substring(6)));
            sha.setSelected(AppConfig.getInstance().getBoolean("payment.SHA"));
        }

    }

    @Override
    public Pane getFXComponent() {
        return this;
    }

    private String comboValue(Object value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public void saveProperties() {
        AppConfig.getInstance().setProperty("payment.commerceid", comboValue(txtCommerceCode.getText()));
        AppConfig.getInstance().setProperty("payment.terminal", comboValue(txtCommerceTerminal.getText()));
        AltEncrypter cypher = new AltEncrypter("cypherkey");
        AppConfig.getInstance().setProperty("payment.commercesign", "crypt:" + cypher.encrypt(new String(txtCommerceSign.getText())));
        AppConfig.getInstance().setProperty("payment.SHA", comboValue(sha.isSelected()));
    }

}
