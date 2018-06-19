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

package uk.chromis.pos.mant;

import java.awt.Component;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.sync.DataLogicSync;

/**
 *
 * @author adrianromero
 */
public final class PlacesEditor extends JPanel implements EditorRecord {

    private SentenceList m_sentfloor;
    private ComboBoxValModel m_FloorModel;
    private String siteGuid;
    private String m_sID;
    private DataLogicSync dlSync;
    private DataLogicSales dlSales;

    public PlacesEditor(DataLogicSales dlSales, DirtyManager dirty, String siteGuid) {
        initComponents();
        this.dlSales = dlSales;
        this.siteGuid = siteGuid;

        m_sentfloor = dlSales.getFloorsList(siteGuid);
        m_FloorModel = new ComboBoxValModel();

        m_jName.getDocument().addDocumentListener(dirty);
        m_jFloor.addActionListener(dirty);
        m_jX.getDocument().addDocumentListener(dirty);
        m_jY.getDocument().addDocumentListener(dirty);

        writeValueEOF();
    }

    public void activate() throws BasicException {

        m_FloorModel = new ComboBoxValModel(m_sentfloor.list());
        m_jFloor.setModel(m_FloorModel);
    }

    @Override
    public void refresh() {
    }

    //m_jFloor
    
    @Override
    public void refreshGuid(String siteGuid) {
        this.siteGuid = siteGuid;
        m_sentfloor = dlSales.getFloorsList(siteGuid);
        try {
            m_FloorModel = new ComboBoxValModel(m_sentfloor.list());
            m_jFloor.setModel(m_FloorModel);
        } catch (BasicException ex) {
            Logger.getLogger(PlacesEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void writeValueEOF() {

        m_sID = null;
        m_jName.setText(null);
        m_FloorModel.setSelectedKey(null);
        m_jX.setText(null);
        m_jY.setText(null);

        m_jName.setEnabled(false);
        m_jFloor.setEnabled(false);
        m_jX.setEnabled(false);
        m_jY.setEnabled(false);
    }

    @Override
    public void writeValueInsert() {

        m_sID = UUID.randomUUID().toString();
        m_jName.setText(null);
        m_FloorModel.setSelectedKey(null);
        m_jX.setText(null);
        m_jY.setText(null);

        m_jName.setEnabled(true);
        m_jFloor.setEnabled(true);
        m_jX.setEnabled(true);
        m_jY.setEnabled(true);
    }

    @Override
    public void writeValueDelete(Object value) {

        Object[] place = (Object[]) value;
        m_sID = Formats.STRING.formatValue(place[0]);
        m_jName.setText(Formats.STRING.formatValue(place[1]));
        m_jX.setText(Formats.INT.formatValue(place[2]));
        m_jY.setText(Formats.INT.formatValue(place[3]));
        m_FloorModel.setSelectedKey(place[4]);

        m_jName.setEnabled(false);
        m_jFloor.setEnabled(false);
        m_jX.setEnabled(false);
        m_jY.setEnabled(false);
    }

    @Override
    public void writeValueEdit(Object value) {

        Object[] place = (Object[]) value;
        m_sID = Formats.STRING.formatValue(place[0]);
        m_jName.setText(Formats.STRING.formatValue(place[1]));
        m_jX.setText(Formats.INT.formatValue(place[2]));
        m_jY.setText(Formats.INT.formatValue(place[3]));
        m_FloorModel.setSelectedKey(place[4]);
        siteGuid = place[5].toString();
        m_jName.setEnabled(true);
        m_jFloor.setEnabled(true);
        m_jX.setEnabled(true);
        m_jY.setEnabled(true);
    }

    @Override
    public Object createValue() throws BasicException {
        Object[] place = new Object[6];
        place[0] = m_sID;
        place[1] = m_jName.getText();
        place[2] = Formats.INT.parseValue(m_jX.getText());
        place[3] = Formats.INT.parseValue(m_jY.getText());
        place[4] = m_FloorModel.getSelectedKey();
        place[5] = siteGuid;
        return place;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        m_jX = new javax.swing.JTextField();
        m_jY = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        m_jFloor = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        setLayout(null);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("Label.Name")); // NOI18N
        add(jLabel2);
        jLabel2.setBounds(20, 20, 90, 25);

        m_jName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        add(m_jName);
        m_jName.setBounds(110, 20, 200, 25);

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Down");
        add(jLabel3);
        jLabel3.setBounds(220, 90, 40, 25);

        m_jX.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        add(m_jX);
        m_jX.setBounds(160, 90, 50, 25);

        m_jY.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        add(m_jY);
        m_jY.setBounds(260, 90, 50, 25);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("label.placefloor")); // NOI18N
        add(jLabel1);
        jLabel1.setBounds(20, 50, 90, 25);

        m_jFloor.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        add(m_jFloor);
        m_jFloor.setBounds(110, 50, 200, 25);

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Across");
        add(jLabel5);
        jLabel5.setBounds(110, 90, 50, 25);

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText(AppLocal.getIntString("label.placeposition")); // NOI18N
        add(jLabel6);
        jLabel6.setBounds(20, 90, 60, 25);

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("<html>Position buttons in your Table plan graphic set in the Floor option <br><br>The values specify the Centre of the button <br> <br> Start Position 0, 0 is Top Left");
        jLabel7.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel7.setMinimumSize(new java.awt.Dimension(50, 40));
        jLabel7.setPreferredSize(new java.awt.Dimension(489, 40));
        add(jLabel7);
        jLabel7.setBounds(10, 140, 410, 90);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 102, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/restaurant_floor_sml.png"))); // NOI18N
        jLabel4.setText(" ");
        jLabel4.setBorder(new javax.swing.border.MatteBorder(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/restaurant_floor_sml.png")))); // NOI18N
        add(jLabel4);
        jLabel4.setBounds(110, 250, 200, 140);

        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setForeground(javax.swing.UIManager.getDefaults().getColor("textHighlight"));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Down");
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        add(jLabel8);
        jLabel8.setBounds(30, 300, 70, 30);

        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setForeground(javax.swing.UIManager.getDefaults().getColor("ToolBar.dockingForeground"));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Across");
        add(jLabel9);
        jLabel9.setBounds(110, 230, 200, 20);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox m_jFloor;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextField m_jX;
    private javax.swing.JTextField m_jY;
    // End of variables declaration//GEN-END:variables

}
