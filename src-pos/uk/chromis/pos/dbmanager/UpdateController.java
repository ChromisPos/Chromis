
/*
 Chromis  - The future of Point Of Sale
 Copyright (c) 2015 chromis.co.uk (John Lewis)
 http://www.chromis.co.uk

 kitchen Screen v1.42

 This file is part of chromis & its associated programs

 chromis is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 chromis is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with chromis.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.chromis.pos.dbmanager;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;

/**
 * FXML Controller class
 *
 */
public class UpdateController implements Initializable {

    public Button exit;
    public Button updateDB;

    public Label lblDbVersion;
    public Label lblAppVersion;
    public Label lblProgressMsg;

    public TextArea dbUpdateMsg;
    public TextField txtDbVersion;
    public TextField txtAppVersion;

    public ProgressBar pb;

    private String dbVersion;
    private Boolean dbUpdate = false;
    private Task worker;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblProgressMsg.setText("");
        txtAppVersion.setText(AppLocal.APP_VERSION);
        //txtAppVersion.setText("0.60");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtDbVersion.setText(dbVersion);
                dbUpdateMsg.setText(AppLocal.getIntString("message.updatedatabase"));
            }
        });
    }

    public void setLocale() {
        AppConfig config = AppConfig.getInstance();
        String slang = AppConfig.getInstance().getProperty("user.language");
        String scountry = AppConfig.getInstance().getProperty("user.country");
        String svariant = AppConfig.getInstance().getProperty("user.variant");
        if (slang != null && !slang.equals("") && scountry != null && svariant != null) {
            Locale.setDefault(new Locale(slang, scountry, svariant));
        }
    }

    public void handleUpdateClick() throws IOException {
        // lets update the database here
        worker = updatedb();
        pb.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        lblProgressMsg.setText("Updating Database. Please wait.");
        lblProgressMsg.setTextFill(Color.web("#0000FF"));
        exit.setDisable(true);
        new Thread(worker).start();
        worker.setOnCancelled(e -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //UpdateDB.stopWorking();
                }
            });
        });

    }

    private Task updatedb() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                String changelog = "uk/chromis/pos/liquibase/upgradeorig/updatesystem.xml";
                ProcessLiquibase db = new ProcessLiquibase();
                if (db.performAction(changelog)) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            pb.setProgress(100);
                            lblProgressMsg.setText("Database updated and ready for use.");
                            lblProgressMsg.setTextFill(Color.web("#000000"));
                            updateDB.setDisable(true);
                            exit.setDisable(false);
                            dbUpdate = true;

                        }
                    });
                    worker.cancel(true);
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            pb.setProgress(0);
                            lblProgressMsg.setText("Error updating database, check 'liquibase.log' for error messages");
                            lblProgressMsg.setTextFill(Color.web("#FF0000"));
                            exit.setDisable(false);
                            updateDB.setDisable(true);
                        }
                    });
                }
                return null;
            }
        };
    }

    public void handleExitClick() {
        System.exit(0);
    }

    public void setDBVersion(String version) {
        this.dbVersion = version;
    }

    public class HeadersNoDate extends SimpleFormatter {

        @Override
        public String format(LogRecord record) {
            if (record.getLevel() == Level.INFO) {
                return record.getMessage() + "\r\n";
            } else {
                return super.format(record);
            }
        }
    }

}
