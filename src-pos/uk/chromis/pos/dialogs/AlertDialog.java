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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

/**
 * @author John Lewis
 */
public class AlertDialog extends JDialog {

    private JPanel panel;
    private JPanel btnPanel;

    private Image img;
    private JTextArea contextArea;
    private JTextArea headerTextArea;
    private JLabel headerText;
    private JLabel iconLabel = new JLabel();

    private ExtendedJButton btn = null;
    private static int CHOICE = -1;
    private JSeparator separator = new JSeparator();
    private Font font;

    protected AlertDialog(int type, String strTitle, String strHeaderText, String strContextText, int buttons) {
        super(new JFrame(), strTitle);
        separator.setOrientation(JSeparator.HORIZONTAL);
        alertDialog(type, strHeaderText, strContextText, buttons);
        pack();
    }

    protected AlertDialog(String strTitle, String strHeaderText, String messageText, String strExceptionText) {
        super(new JFrame(), strTitle);
        separator.setOrientation(JSeparator.HORIZONTAL);
        exceptionDialog(strHeaderText, messageText, strExceptionText);
        pack();
    }

    class ExtendedJButton extends JButton {

        private int btnChoice = -1;

        private ExtendedJButton(String text) {
            super(text);
        }

        private ExtendedJButton(String text, int choice) {
            super(text);
            this.btnChoice = choice;
        }

        private int getBtnChoice() {
            return btnChoice;
        }
    }

    private void alertDialog(int type, String headerText, String contextText, int buttons) {
        btnPanel = new JPanel();

        for (int i = 0; i < 8; i++) {
            switch (buttons & (int) Math.pow(2, i)) {
                case 1:
                    btn = new ExtendedJButton("Yes", JAlertPane.YES);
                    break;
                case 2:
                    btn = new ExtendedJButton("No", JAlertPane.NO);
                    break;
                case 4:
                    btn = new ExtendedJButton("OK", JAlertPane.OK);
                    break;
                case 8:
                    btn = new ExtendedJButton("Save", JAlertPane.SAVE);
                    break;
                case 16:
                    btn = new ExtendedJButton("Retry", JAlertPane.RETRY);
                    break;
                case 32:
                    btn = new ExtendedJButton("Exit", JAlertPane.EXIT);
                    break;
                case 64:
                    btn = new ExtendedJButton("Configuration", JAlertPane.CONFIGURE);
                    break;
                case 128:
                    btn = new ExtendedJButton("Cancel", JAlertPane.CANCEL);
                    break;
            }
            if (btn != null) {
                btn.addActionListener((ActionEvent e) -> {
                    ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
                    CHOICE = extBtn.getBtnChoice();
                    dispose();
                });
                btnPanel.add(btn);
            }
        }

        //Set the dialog defaults - icon 
        try {
            switch (type) {
                case 0:
                    img = ImageIO.read(AlertDialog.class.getResource("warning.png"));
                    break;
                case 1:
                    img = ImageIO.read(AlertDialog.class.getResource("information.png"));
                    break;
                case 3:
                    img = ImageIO.read(AlertDialog.class.getResource("confirmation.png"));
                    break;
                case 5:
                    img = ImageIO.read(AlertDialog.class.getResource("success.png"));
                    break;
                default:
                    img = ImageIO.read(AlertDialog.class.getResource("error.png"));
                    break;
            }
        } catch (IOException ex) {
            img = null;
        }

        //Set the dialog with no minimize or expand icons on title bar
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                dispose();
            }
        });

        //Create the main panel layout
        if (headerText != null) {
            panel = new JPanel(new MigLayout("insets 10 4 5 2", "[][]", "[]0[][]"));
            headerTextArea = new JTextArea(1, 1);
            setTextAreaParameters(headerTextArea);
            panel.add(headerTextArea, "left,  pushx, growx, split 2");
            iconLabel.setIcon(new ImageIcon(img));
            panel.add(iconLabel, "wrap, width :45:, align right");
            panel.add(separator, "span, center, gapy 10, growx, wrap");
            contextArea = new JTextArea(1, 1);
            setTextAreaParameters(contextArea);
            panel.add(contextArea, "left, span, width 350:350:, pushx, growx, split 2, wrap");
            headerTextArea.setText(headerText);
        } else {
            panel = new JPanel(new MigLayout("insets 10 4 5 2", "[]", "[top]10[]"));
            iconLabel.setIcon(new ImageIcon(img));
            panel.add(iconLabel, "width :55:,  align left");
            contextArea = new JTextArea(1, 1);
            setTextAreaParameters(contextArea);
            panel.getPreferredSize();
            panel.add(contextArea, "width :350:, wrap");
        }
        contextArea.setText(contextText);
        panel.add(btnPanel, "span, gapy 5, height 32:32:32, right");

        setAlwaysOnTop(true);
        setResizable(false);
        setModal(true);
        getContentPane().add(panel);
    }

    private void exceptionDialog(String headerText, String messageText, String exceptionStack) {
        try {
            img = ImageIO.read(AlertDialog.class.getResource("error.png"));
        } catch (IOException ex) {
            img = null;
        }

        btnPanel = new JPanel();
        btn = new ExtendedJButton("OK", JAlertPane.OK);
        btn.addActionListener((ActionEvent e) -> {
            ExtendedJButton extBtn = (ExtendedJButton) e.getSource();
            CHOICE = extBtn.getBtnChoice();
            dispose();
        });
        btnPanel.add(btn);

        //Set the dialog with no minimize or expand icons on title bar
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                dispose();
            }
        });

        panel = new JPanel(new MigLayout("insets 2 4 5 2", "[450:450:450][]", "[]0[][][][]"));

        headerTextArea = new JTextArea(1, 1);
        setTextAreaParameters(headerTextArea);
        font = headerTextArea.getFont();
        float size = font.getSize() + 2.0f;
        headerTextArea.setFont(font.deriveFont(size));
        panel.add(headerTextArea, "left,  pushx, growx, split 2");
        iconLabel.setIcon(new ImageIcon(img));
        panel.add(iconLabel, "wrap, width :45:, align right");
        panel.add(separator, "span, center, gapy 5, growx, wrap");

        JTextArea messageArea = new JTextArea(2, 40);
        setTextAreaParameters(messageArea);
        panel.add(messageArea, "left,  pushx, growx, wrap");
        font = messageArea.getFont();
        size = font.getSize() - 1.0f;
        messageArea.setFont(font.deriveFont(size));
        messageArea.setText(messageText);

        JLabel stackText = new JLabel();
        panel.add(stackText, "left,  pushx, growx, wrap");
        font = stackText.getFont();
        size = font.getSize() - 1.0f;
        stackText.setFont(new Font(font.getName(), Font.PLAIN, (int) size));
        stackText.setText("The exception stacktrace was:");

        contextArea = new JTextArea(10, 42);
        setTextAreaParameters(contextArea);

        JScrollPane sPane = new JScrollPane(contextArea);
        panel.add(sPane, "span");
        headerTextArea.setText(headerText);
        contextArea.setText(exceptionStack);
        panel.add(btnPanel, "span, gapy 5, height 32:32:32, right");

        setAlwaysOnTop(true);
        setResizable(false);
        setModal(true);
        getContentPane().add(panel);

    }

    private void setTextAreaParameters(JTextArea textArea) {
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        textArea.setEnabled(false);
        textArea.setFocusable(false);
        textArea.setOpaque(false);
        textArea.setRequestFocusEnabled(false);
    }

    protected int getChoice() {
        return CHOICE;
    }
}
