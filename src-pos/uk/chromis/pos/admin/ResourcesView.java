/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c) 2015-2018
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
package uk.chromis.pos.admin;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.UUID;
import javax.swing.JPanel;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.loader.ImageUtils;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.util.Base64Encoder;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

public final class ResourcesView extends JPanel implements EditorRecord {

    private Object m_oId;
    private ComboBoxValModel m_ResourceModel;
    private String siteGuid;
    private RSyntaxTextArea m_textArea;
    private RTextScrollPane sp;

    public ResourcesView(DirtyManager dirty) {
        initComponents();

        JPanel cp = new JPanel(new BorderLayout());
        m_textArea = new RSyntaxTextArea(20, 60);
        m_textArea.setCodeFoldingEnabled(true);
        sp = new RTextScrollPane(m_textArea);
        cp.add(sp);
        m_jContainer.add(cp, "text");

        m_ResourceModel = new ComboBoxValModel();
        m_ResourceModel.add(ResourceType.TEXT);
        m_ResourceModel.add(ResourceType.IMAGE);
        m_ResourceModel.add(ResourceType.BINARY);
        m_jType.setModel(m_ResourceModel);

        m_jName.getDocument().addDocumentListener(dirty);
        m_jType.addActionListener(dirty);
        m_textArea.getDocument().addDocumentListener(dirty);
        m_jImage.addPropertyChangeListener("image", dirty);

        writeValueEOF();
    }

    @Override
    public void writeValueEOF() {
        m_oId = null;
        m_jName.setText(null);
        m_ResourceModel.setSelectedItem(null);
        m_textArea.setText(null);
        m_jImage.setImage(null);
        m_jName.setEnabled(false);
        m_jType.setEnabled(false);
        m_textArea.setEnabled(false);
        m_jImage.setEnabled(false);
    }

    @Override
    public void writeValueInsert() {
        m_oId = null;
        m_jName.setText(null);
        m_ResourceModel.setSelectedItem(ResourceType.TEXT);
        m_textArea.setText(null);
        m_jImage.setImage(null);     
        m_jName.setEnabled(true);
        m_jType.setEnabled(true);
        m_textArea.setEnabled(true);
        m_jImage.setEnabled(true);
    }

    @Override
    public void writeValueDelete(Object value) {
        Object[] resource = (Object[]) value;
        m_oId = resource[0];
        m_jName.setText((String) resource[1]);
        m_ResourceModel.setSelectedKey(resource[2]);

        ResourceType restype = (ResourceType) m_ResourceModel.getSelectedItem();
        if (restype == ResourceType.TEXT) {
            m_textArea.setText(Formats.BYTEA.formatValue(resource[3]));
            m_textArea.setSyntaxEditingStyle(this.getResourceSyntaxStyle(m_textArea.getText()));
            m_textArea.setCaretPosition(0);
            m_jImage.setImage(null);
        } else if (restype == ResourceType.IMAGE) {
            m_textArea.setText(null);
            m_jImage.setImage(ImageUtils.readImage((byte[]) resource[3]));
        } else if (restype == ResourceType.BINARY) {
            m_textArea.setText(resource[3] == null
                    ? null
                    : Base64Encoder.encodeChunked((byte[]) resource[3]));
            m_textArea.setCaretPosition(0);
            m_jImage.setImage(null);
        } else {
            m_textArea.setText(null);
            m_jImage.setImage(null);
        }
        m_jName.setEnabled(false);
        m_jType.setEnabled(false);
        m_textArea.setEnabled(false);
        m_jImage.setEnabled(false);
    }

    @Override
    public void writeValueEdit(Object value) {
        Object[] resource = (Object[]) value;
        m_oId = resource[0];
        m_jName.setText((String) resource[1]);
        m_ResourceModel.setSelectedKey(resource[2]);

        ResourceType restype = (ResourceType) m_ResourceModel.getSelectedItem();
        if (restype == ResourceType.TEXT) {
            m_textArea.setText(Formats.BYTEA.formatValue(resource[3]));
            m_textArea.setSyntaxEditingStyle(this.getResourceSyntaxStyle(m_textArea.getText()));
            m_textArea.setCaretPosition(0);
            m_jImage.setImage(null);
        } else if (restype == ResourceType.IMAGE) {
            m_textArea.setText(null);
            m_jImage.setImage(ImageUtils.readImage((byte[]) resource[3]));
        } else if (restype == ResourceType.BINARY) {
            m_textArea.setText(resource[2] == null
                    ? null
                    : Base64Encoder.encodeChunked((byte[]) resource[3]));
            m_textArea.setCaretPosition(0);
            m_jImage.setImage(null);
        } else {
            m_textArea.setText(null);
            m_jImage.setImage(null);
        }
        m_jName.setEnabled(true);
        m_jType.setEnabled(true);
        m_textArea.setEnabled(true);
        m_jImage.setEnabled(true);
        siteGuid = resource[4].toString();
    }

    @Override
    public Object createValue() throws BasicException {
        Object[] resource = new Object[5];

        resource[0] = m_oId == null ? UUID.randomUUID().toString() : m_oId;
        resource[1] = m_jName.getText();

        ResourceType restype = (ResourceType) m_ResourceModel.getSelectedItem();
        resource[2] = restype.getKey();
        if (restype == ResourceType.TEXT) {
            resource[3] = Formats.BYTEA.parseValue(m_textArea.getText());
        } else if (restype == ResourceType.IMAGE) {
            resource[3] = ImageUtils.writeImage(m_jImage.getImage());
        } else if (restype == ResourceType.BINARY) {
            resource[3] = Base64Encoder.decode(m_textArea.getText());
        } else {
            resource[3] = null;
        }
        resource[4] = siteGuid;
        return resource;
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void refreshGuid(String selectKey) {
    }

    @Override
    public void refresh() {
    }

    private void showView(String view) {
        CardLayout cl = (CardLayout) (m_jContainer.getLayout());
        cl.show(m_jContainer, view);
    }

    private String getResourceSyntaxStyle(String resource) {

        if (resource.indexOf("<?xml") != -1) {
            return SyntaxConstants.SYNTAX_STYLE_XML;
        } else if (resource.indexOf("SELECT") != -1 || resource.indexOf("UPDATE") != -1) {
            return SyntaxConstants.SYNTAX_STYLE_SQL;
        } else {
            return SyntaxConstants.SYNTAX_STYLE_JAVA;
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jGroupType = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        m_jContainer = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        m_jText = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        m_jImage = new uk.chromis.data.gui.JImageEditor();
        jLabel2 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        m_jType = new javax.swing.JComboBox();

        jPanel3.setLayout(new java.awt.BorderLayout());

        m_jContainer.setLayout(new java.awt.CardLayout());

        m_jText.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(m_jText);

        m_jContainer.add(jScrollPane1, "text");
        m_jContainer.add(jPanel1, "null");
        m_jContainer.add(m_jImage, "image");

        jPanel3.add(m_jContainer, java.awt.BorderLayout.CENTER);

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("label.resname")); // NOI18N

        m_jName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        m_jType.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jTypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_jType, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_jType, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void m_jTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jTypeActionPerformed

        ResourceType restype = (ResourceType) m_ResourceModel.getSelectedItem();
        if (restype == ResourceType.TEXT) {
            showView("text");
        } else if (restype == ResourceType.IMAGE) {
            showView("image");
        } else if (restype == ResourceType.BINARY) {
            showView("text");
        } else {
            showView("null");
        }

    }//GEN-LAST:event_m_jTypeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel m_jContainer;
    private javax.swing.ButtonGroup m_jGroupType;
    private uk.chromis.data.gui.JImageEditor m_jImage;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextArea m_jText;
    private javax.swing.JComboBox m_jType;
    // End of variables declaration//GEN-END:variables

    void activate() {

    }

}
