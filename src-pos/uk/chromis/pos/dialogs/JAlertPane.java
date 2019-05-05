/*
**    Chromis POS  - The New Dynamic Open Source POS
**    Copyright (c) 2015-2019 Chromis and previous contributing parties (Unicenta & Openbravo)
**    http://www.chromis.co.uk
**
**    This file is part of Chromis POS Version v1.00 1902-15
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
package uk.chromis.pos.dialogs;

import javax.swing.*;

/**
 * @author John Lewis
 */
public class JAlertPane extends JDialog {

    public static final int WARNING = 0;
    public static final int INFORMATION = 1;
    public static final int ERROR = 2;
    public static final int CONFIRMATION = 3;
    public static final int EXCEPTION = 4;
    public static final int SUCCESS = 5;

    public static final int YES_NO_OPTION = 3;
    public static final int OK_OPTION = 4;
    public static final int YES_NO_CANCEL_OPTION = 131;
    public static final int OK_CANCEL_OPTION = 132;
    public static final int EXIT_CANCEL_OPTION = 160;
    public static final int CONFIGURE_CANCEL_OPTION = 192;
    public static final int SAVE_CANCEL_OPTION = 136;
    public static final int RETRY_CANCEL_OPTION = 144;

    protected static final int SAVE = 7;
    protected static final int NO = 6;
    protected static final int YES = 5;
    protected static final int CANCEL = 4;
    protected static final int RETRY = 3;
    protected static final int EXIT = 2;
    protected static final int CONFIGURE = 1;
    protected static final int OK = 0;
    protected static int CHOICE = -1;

    public static int showAlertDialog(int type, String strTitle, String strHeaderText, String strContext, int buttons) {
        AlertDialog jAlert = new AlertDialog(type,
                strTitle,
                strHeaderText,
                strContext,
                buttons);
        jAlert.setLocationRelativeTo(null);
        jAlert.setVisible(true);
        return jAlert.getChoice();
    }

    public static int showAlertDialog(int type, String strTitle, String strHeaderText, String strContext, int buttons, JFrame frame) {
        AlertDialog jAlert = new AlertDialog(type,
                strTitle,
                strHeaderText,
                strContext,
                buttons);
        jAlert.setLocationRelativeTo(frame);
        jAlert.setVisible(true);
        return jAlert.getChoice();
    }

    public static int showExceptionStackDialog(String strTitle, String strHeaderText, String messageText, String exceptionStack, JFrame frame) {
        AlertDialog jAlert = new AlertDialog(
                strTitle,
                strHeaderText,
                messageText,
                exceptionStack);
        jAlert.setLocationRelativeTo(frame);
        jAlert.setVisible(true);
        return jAlert.getChoice();

    }

}
