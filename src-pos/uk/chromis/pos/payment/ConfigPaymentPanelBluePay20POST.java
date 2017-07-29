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
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;

/**
 * FXML Controller class
 *
 * @author John
 */
public class ConfigPaymentPanelBluePay20POST extends Pane implements PaymentConfiguration {

    @FXML
    private Label url;
    @FXML
    private Label account;
    @FXML
    private Label secret;

    @FXML
    private TextField txtURL;
    @FXML
    private PasswordField txtAccountID;
    @FXML
    private TextField txtSecretKey;

    public ConfigPaymentPanelBluePay20POST() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfigPaymentPanelBluePay20POST.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ConfigPaymentPanelBluePay20POST.class.getName()).
                    log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

        url.setText(AppLocal.getIntString("label.storename"));
        account.setText(AppLocal.getIntString("label.certificatepwd"));
        secret.setText(AppLocal.getIntString("label.certificatepath"));
        loadProperties();
    }

    @Override
    public void loadProperties() {
        String sAccountID = AppConfig.getInstance().getProperty("payment.BluePay.accountID");
        String sSecretKey = AppConfig.getInstance().getProperty("payment.BluePay.secretKey");
        String sURL = AppConfig.getInstance().getProperty("payment.BluePay.URL");

        if (sAccountID != null && sSecretKey != null && sURL != null && sURL.startsWith("https://")) {
            txtURL.setText(AppConfig.getInstance().getProperty("payment.BluePay.URL"));
            txtAccountID.setText(AppConfig.getInstance().getProperty("payment.BluePay.accountID"));
            txtSecretKey.setText(AppConfig.getInstance().getProperty("payment.BluePay.secretKey"));
        }
    }

    @Override
    public void saveProperties() {
        AppConfig.getInstance().setProperty("payment.BluePay.accountID", comboValue(txtAccountID.getText()));
        AppConfig.getInstance().setProperty("payment.BluePay.secretKey", comboValue(txtSecretKey.getText()));
        AppConfig.getInstance().setProperty("payment.BluePay.URL", comboValue(txtURL.getText()));
    }

    private String comboValue(Object value) {
        return value == null ? "" : value.toString();
    }

    @Override
    public Pane getFXComponent() {
        return this;
    }

}
