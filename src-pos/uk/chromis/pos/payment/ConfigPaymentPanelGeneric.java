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
import javax.swing.JPanel;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.util.AltEncrypter;

public class ConfigPaymentPanelGeneric extends Pane implements PaymentConfiguration {

    @FXML
    private Label lblCommerceID;
    @FXML
    private Label lblCommercePWD;

    @FXML
    private TextField txtCommerceID;
    @FXML
    private PasswordField txtCommercePWD;

    public ConfigPaymentPanelGeneric() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfigPaymentPanelGeneric.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ConfigPaymentPanelGeneric.class.getName()).
                    log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

        lblCommerceID.setText(AppLocal.getIntString("label.commerceid"));
        lblCommercePWD.setText(AppLocal.getIntString("label.commercepwd"));

        loadProperties();

    }

    @Override
    public void loadProperties() {
        String sCommerceID = AppConfig.getInstance().getProperty("payment.commerceid");
        String sCommercePass = AppConfig.getInstance().getProperty("payment.commercepassword");

        if (sCommerceID != null && sCommercePass != null && sCommercePass.startsWith("crypt:")) {
            txtCommerceID.setText(AppConfig.getInstance().getProperty("payment.commerceid"));
            AltEncrypter cypher = new AltEncrypter("cypherkey" + AppConfig.getInstance().getProperty("payment.commerceid"));
            txtCommercePWD.setText(cypher.decrypt(AppConfig.getInstance().getProperty("payment.commercepassword").substring(6)));
        }
    }

    @Override
    public Pane getFXComponent() {
        return this;
    }

    @Override
    public void saveProperties() {
        AppConfig.getInstance().setProperty("payment.commerceid", txtCommerceID.getText());
        AltEncrypter cypher = new AltEncrypter("cypherkey" + txtCommerceID.getText());
        AppConfig.getInstance().setProperty("payment.commercepassword", "crypt:" + cypher.encrypt(new String(txtCommercePWD.getText())));

    }

}
