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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.util.AltEncrypter;

public class ConfigPaymentPanelLinkPoint extends Pane implements PaymentConfiguration {

    @FXML
    private Label url;
    @FXML
    private Label account;
    @FXML
    private Label secret;

    @FXML
    private TextField txtStoreName;
    @FXML
    private TextField txtCertificatePath;
    @FXML
    private PasswordField txtCertificatePass;

    @FXML
    private Button fileBtn;

    public ConfigPaymentPanelLinkPoint() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ConfigPaymentPanelLinkPoint.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ConfigPaymentPanelLinkPoint.class.getName()).
                    log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

        url.setText(AppLocal.getIntString("label.storename"));
        account.setText(AppLocal.getIntString("label.certificatepwd"));
        secret.setText(AppLocal.getIntString("label.certificatepath"));

        Image image = new Image(getClass().getResourceAsStream("/uk/chromis/images/fileopen.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(18);
        imageView.setFitWidth(18);
        fileBtn.setGraphic(imageView);
        loadProperties();

    }

    @Override
    public void loadProperties() {
        String sCommerceID = AppConfig.getInstance().getProperty("payment.commerceid");
        String sCertificatePass = AppConfig.getInstance().getProperty("payment.certificatePassword");
        String sCertificatePath = AppConfig.getInstance().getProperty("payment.certificatePath");

        if (sCommerceID != null && sCertificatePath != null && sCertificatePass != null && sCertificatePass.startsWith("crypt:")) {
            txtStoreName.setText(AppConfig.getInstance().getProperty("payment.commerceid"));
            AltEncrypter cypher = new AltEncrypter("cypherkey");
            txtCertificatePass.setText(cypher.decrypt(AppConfig.getInstance().getProperty("payment.certificatePassword").substring(6)));
            txtCertificatePath.setText(AppConfig.getInstance().getProperty("payment.certificatePath"));
        }
    }

    @Override
    public Pane getFXComponent() {
        return this;
    }

    @Override
    public void saveProperties() {
        System.out.println("in save");
        AltEncrypter cypher = new AltEncrypter("cypherkey");
        AppConfig.getInstance().setProperty("payment.commerceid", comboValue(txtStoreName.getText()));
        AppConfig.getInstance().setProperty("payment.certificatePath", comboValue(txtCertificatePath.getText()));
        AppConfig.getInstance().setProperty("payment.certificatePassword", "crypt:" + cypher.encrypt(new String(txtCertificatePass.getText())));
    }

    private String comboValue(Object value) {
        return value == null ? "" : value.toString();
    }

    public void fileOpen() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PKCS12 certificates", "p12");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            txtCertificatePath.setText(selectedFile.getAbsoluteFile().toString());
        }

    }

    private static class ExtensionsFilter extends FileFilter {

        private final String message;
        private final String[] extensions;

        public ExtensionsFilter(String message, String... extensions) {
            this.message = message;
            this.extensions = extensions;
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            } else {
                String sFileName = f.getName();
                int ipos = sFileName.lastIndexOf('.');
                if (ipos >= 0) {
                    String sExt = sFileName.substring(ipos + 1);
                    for (String s : extensions) {
                        if (s.equalsIgnoreCase(sExt)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }

        @Override
        public String getDescription() {
            return message;
        }
    }

}
