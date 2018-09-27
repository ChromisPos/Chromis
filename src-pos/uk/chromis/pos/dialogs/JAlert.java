package uk.chromis.pos.dialogs;

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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

/**
 * @author John Lewis
 */
public class JAlert extends JDialog {

    static final Dimension SCREEN_DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();

    public static final int WARNING = 0;
    public static final int INFORMATION = 1;
    public static final int ERROR = 2;
    public static final int CONFIRMATION = 3;
    public static final int EXCEPTION = 4;

    public static final int NO = 6;
    public static final int YES = 5;
    public static final int CANCEL = 4;
    public static final int RETRY = 3;
    public static final int EXIT = 2;
    public static final int CONFIG = 1;
    public static final int OK = 0;
    public static int CHOICE = -1;

    //private MigLayout layout;
    private JPanel panel;
    private JPanel headerPanel;
    private JPanel contextPanel;
    private JPanel btnPanel;

    private Image img;
    private static BufferedImage icon;
    private int type;
    private String strTitle = "";
    private String strHeaderText = "";
    private JTextArea contextArea;
    private JTextArea headerTextArea;
    private String strContext = "";
    private JLabel headerText;
    private JLabel iconLabel;

    private int width = 350;
    private int height = 180;

    private JButton btnCancel;
    private JButton btnOK;
    private JButton btnExit;
    private JButton btnYes;
    private JButton btnNo;
    private Font headerFont;
    private Font contextFont;
    private Font dejaVuSans;

    public JAlert(int type) {
        super(new JFrame(), "");
        this.type = type;

        //create new btnpanel and set its size
        btnPanel = new JPanel();
        btnPanel.setSize(new Dimension(240, 25));

        //Create default set of buttons
        btnOK = new JButton("OK");
        btnOK.setPreferredSize(new Dimension(80, 25));
        btnOK.addActionListener((ActionEvent e) -> {
            CHOICE = OK;
            dispose();
        });

        btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(80, 25));
        btnCancel.addActionListener((ActionEvent e) -> {
            CHOICE = CANCEL;
            dispose();
        });

        btnExit = new JButton("Exit");
        btnExit.setPreferredSize(new Dimension(80, 25));
        btnExit.addActionListener((ActionEvent e) -> {
            CHOICE = EXIT;
            dispose();
        });

        btnYes = new JButton("Yes");
        btnYes.setPreferredSize(new Dimension(80, 25));
        btnYes.addActionListener((ActionEvent e) -> {
            CHOICE = YES;
            dispose();
        });

        btnNo = new JButton("No");
        btnNo.setPreferredSize(new Dimension(80, 25));
        btnNo.addActionListener((ActionEvent e) -> {
            CHOICE = NO;
            dispose();
        });

        //Build the fonts to be used using DeJaVu is file is found otherwise use Dialog
        dejaVuSans = null;
        try {
            InputStream istream = JAlert.class.getResourceAsStream("/uk/chromis/fonts/DejaVuSansCondensed.ttf");
            dejaVuSans = Font.createFont(Font.TRUETYPE_FONT, istream);
            headerFont = dejaVuSans.deriveFont(Font.PLAIN, 13);
            contextFont = dejaVuSans.deriveFont(Font.PLAIN, 9);
        } catch (FontFormatException | IOException e) {
            System.err.println(e.getMessage());
            headerFont = null;
        }
        if (headerFont == null) {
            headerFont = new Font("Dialog", Font.PLAIN, 14);
        }
        contextFont = new Font("Dialog", Font.PLAIN, 12);
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value != null && value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, headerFont);
            }
        }

        //Set the dialog defaults - icon and button set
        try {
            switch (type) {
                case 0:
                    img = ImageIO.read(JAlert.class.getResource("warning.png"));
                    btnPanel.add(btnOK);
                    break;
                case 1:
                    img = ImageIO.read(JAlert.class.getResource("information.png"));
                    btnPanel.add(btnOK);
                    break;
                case 2:
                    img = ImageIO.read(JAlert.class.getResource("error.png"));
                    btnPanel.add(btnOK);
                    break;
                case 3:
                    img = ImageIO.read(JAlert.class.getResource("confirmation.png"));
                    btnPanel.add(btnOK);
                    btnPanel.add(btnCancel);
                    break;
                case 4:
                    img = ImageIO.read(JAlert.class.getResource("error.png"));
                    btnPanel.add(btnOK);
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
        panel = new JPanel(new MigLayout("", "[]", "[]0[][]"));
        panel.setPreferredSize(new Dimension(width, height));

        //Create sub panels and populate main panel
        headerPanel = new JPanel(new MigLayout("", "[230][50]", "[]"));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.decode("#bcbcb7")));
        contextPanel = new JPanel(new MigLayout("", "[]", "[]"));

        panel.add(headerPanel, "wrap, gapx 0, growx, pushx");
        panel.add(contextPanel, "wrap, growx, pushx");
        panel.add(btnPanel, "gapy 5, height 32:32:32, align right");

        //Build the headertext panel
        headerTextArea = new JTextArea(2, 32);
        headerTextArea.setLineWrap(true);
        headerTextArea.setWrapStyleWord(true);
        headerTextArea.setEditable(false);
        headerTextArea.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        headerTextArea.setEnabled(false);
        headerTextArea.setFocusable(false);
        headerTextArea.setOpaque(false);
        headerTextArea.setRequestFocusEnabled(false);
        if (headerFont.getFontName().equals("Dialog.plain")) {
            headerPanel.add(headerTextArea, "gapx 0, height 38:38:38, growx, pushx");
        } else {
            headerPanel.add(headerTextArea, "gapx 0,  height 35:35:35, growx, pushx");
        }
        headerTextArea.setFont(headerFont);

        //Add the icon
        iconLabel = new JLabel();
        iconLabel.setIcon(new ImageIcon(img));
        headerPanel.add(iconLabel, "wrap, align right");

        contextArea = new JTextArea();
        contextArea.setColumns(28);
        contextArea.setLineWrap(true);
        contextArea.setWrapStyleWord(true);
        contextArea.setEditable(false);
        contextArea.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        contextArea.setEnabled(false);
        contextArea.setFocusable(false);
        contextArea.setOpaque(false);
        contextArea.setRequestFocusEnabled(false);
        contextArea.setFont(contextFont);
        contextPanel.add(contextArea, "gapx 3, wrap");

        //Set the dialogbox to always be on top
        setAlwaysOnTop(true);

        //Stop it being resized
        setResizable(false);

        //set to Modal mode
        setModal(true);

        // Add panel to the dialog box
        getContentPane().add(panel);
        setLocation((SCREEN_DIMENSION.width - width) / 2, (SCREEN_DIMENSION.height - height) / 2);

    }   
    
    public void reSize(int width, int height){
        setSize(width,height);
        setLocation((SCREEN_DIMENSION.width - width) / 2, (SCREEN_DIMENSION.height - height) / 2);
    }
    
    
    public void setTitle(String strTitle) {
        super.setTitle(strTitle);
    }
    
    public void setHeaderText(String strHeaderText) {
        headerTextArea.setText(strHeaderText);
        pack();
    }

    public void setContextText(String strContext) {
        contextArea.setText(strContext);
        pack();
    }

    public void setHeaderFont(Font headerFont) {
        this.headerFont = headerFont;
    }

    public void setContextFont(Font contextFont) {
        this.contextFont = contextFont;
    }

    public void setYesNoButtons() {
        btnPanel.removeAll();
        btnPanel.setSize(new Dimension(240, 25));
        btnPanel.add(btnYes);
        btnPanel.add(btnNo);
    }

    public int getChoice() {
        return CHOICE;
    }
}
