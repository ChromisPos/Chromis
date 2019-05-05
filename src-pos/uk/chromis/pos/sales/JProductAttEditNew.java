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

package uk.chromis.pos.sales;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import uk.chromis.basic.BasicException;
import uk.chromis.beans.JFlowPanel;
import uk.chromis.data.loader.*;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.inventory.AttributeSetInfo;

public class JProductAttEditNew extends javax.swing.JDialog {

    private SentenceFind attsetSent;
    private SentenceList attvaluesSent;
    private SentenceList attinstSent;
    private SentenceList attinstSent2;
    private SentenceFind attsetinstExistsSent;
    private SentenceExec attsetSave;
    private SentenceExec attinstSave;
    private List<JProductAttEditI> itemslist;
    private String attsetid;
    private String attInstanceId;
    private String attInstanceDescription;

    private boolean ok;

    private JProductAttEditNew(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }

    private JProductAttEditNew(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
    }

    private void init(Session s) {
        initComponents();

        attsetSave = new PreparedSentence(s,
                "INSERT INTO ATTRIBUTESETINSTANCE (ID, ATTRIBUTESET_ID, DESCRIPTION) VALUES (?, ?, ?)",
                new SerializerWriteBasic(Datas.STRING, Datas.STRING, Datas.STRING));
        attinstSave = new PreparedSentence(s,
                "INSERT INTO ATTRIBUTEINSTANCE(ID, ATTRIBUTESETINSTANCE_ID, ATTRIBUTE_ID, VALUE) VALUES (?, ?, ?, ?)",
                new SerializerWriteBasic(Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING));

        attsetSent = new PreparedSentence(s,
                "SELECT ID, NAME FROM ATTRIBUTESET WHERE ID = ?",
                SerializerWriteString.INSTANCE,
                new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new AttributeSetInfo(dr.getString(1), dr.getString(2));
            }
        });
        attsetinstExistsSent = new PreparedSentence(s,
                "SELECT ID FROM ATTRIBUTESETINSTANCE WHERE ATTRIBUTESET_ID = ? AND DESCRIPTION = ?",
                new SerializerWriteBasic(Datas.STRING, Datas.STRING),
                SerializerReadString.INSTANCE);

        attinstSent = new PreparedSentence(s, "SELECT A.ID, A.NAME, " + s.DB.CHAR_NULL() + ", " + s.DB.CHAR_NULL() + " "
                + "FROM ATTRIBUTEUSE AU JOIN ATTRIBUTE A ON AU.ATTRIBUTE_ID = A.ID "
                + "WHERE AU.ATTRIBUTESET_ID = ? "
                + "ORDER BY AU.LINENO",
                SerializerWriteString.INSTANCE,
                new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new AttributeInstInfo(dr.getString(1), dr.getString(2), dr.getString(3), dr.getString(4));
            }
        });
        attinstSent2 = new PreparedSentence(s, "SELECT A.ID, A.NAME, AI.ID, AI.VALUE "
                + "FROM ATTRIBUTEUSE AU JOIN ATTRIBUTE A ON AU.ATTRIBUTE_ID = A.ID LEFT OUTER JOIN ATTRIBUTEINSTANCE AI ON AI.ATTRIBUTE_ID = A.ID "
                + "WHERE AU.ATTRIBUTESET_ID = ? AND AI.ATTRIBUTESETINSTANCE_ID = ?"
                + "ORDER BY AU.LINENO",
                new SerializerWriteBasic(Datas.STRING, Datas.STRING),
                new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new AttributeInstInfo(dr.getString(1), dr.getString(2), dr.getString(3), dr.getString(4));
            }
        });
        attvaluesSent = new PreparedSentence(s, "SELECT VALUE FROM ATTRIBUTEVALUE WHERE ATTRIBUTE_ID = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadString.INSTANCE);
    }

    public static JProductAttEditNew getAttributesEditor(Component parent, Session s) {

        Window window = SwingUtilities.getWindowAncestor(parent);

        JProductAttEditNew myMsg;
        if (window instanceof Frame) {
            myMsg = new JProductAttEditNew((Frame) window, true);
        } else {
            myMsg = new JProductAttEditNew((Dialog) window, true);
        }

        myMsg.getRootPane().setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.BLACK));
        myMsg.setUndecorated(true);
        
        myMsg.init(s);
        myMsg.applyComponentOrientation(parent.getComponentOrientation());
        return myMsg;
    }

    public void editAttributes(String attsetid, String attsetinstid) throws BasicException {
        if (attsetid == null) {
            throw new BasicException(AppLocal.getIntString("message.cannotfindattributes"));
        } else {
            this.attsetid = attsetid;
            this.attInstanceId = null;
            this.attInstanceDescription = null;
            this.ok = false;

            AttributeSetInfo asi = (AttributeSetInfo) attsetSent.find(attsetid);

            if (asi == null) {
                throw new BasicException(AppLocal.getIntString("message.cannotfindattributes"));
            }

            setTitle(asi.getName());

            List<AttributeInstInfo> attinstinfo = attsetinstid == null
                    ? attinstSent.list(attsetid)
                    : attinstSent2.list(attsetid, attsetinstid);

            itemslist = new ArrayList<>();

            for (AttributeInstInfo aii : attinstinfo) {

                JProductAttEditI item;

                List<String> values = attvaluesSent.list(aii.getAttid());
                jPanel2 = new JFlowPanel();
                JScrollPane scroll = new JScrollPane(jPanel2);
                scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                scroll.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));
                add(scroll, BorderLayout.CENTER);

                for (String btnText : values) {
                    JButton btn = new JButton();
                    btn.applyComponentOrientation(getComponentOrientation());
                    btn.setFocusPainted(false);
                    btn.setText(btnText);
                    btn.setFocusable(false);
                    btn.setRequestFocusEnabled(false);
                    btn.setHorizontalTextPosition(SwingConstants.CENTER);
                    btn.setVerticalTextPosition(SwingConstants.BOTTOM);
                    btn.setMargin(new Insets(2, 2, 2, 2));
                    btn.setMaximumSize(new Dimension(120, 50));
                    btn.setPreferredSize(new Dimension(120, 50));
                    btn.setMinimumSize(new Dimension(120, 50));
                    AL al = new AL();
                    btn.setActionCommand(btnText);
                    btn.addActionListener(al);
                    jPanel2.add(btn);
                }
            }
        }
    }

    public class AL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            StringBuilder description = new StringBuilder();
            description.append(e.getActionCommand());
            String id;

            if (description.length() == 0) {
                id = null;
            } else {
                try {
                    id = (String) attsetinstExistsSent.find(attsetid, description.toString());
                } catch (BasicException ex) {
                    return;
                }
                if (id == null) {
                    id = UUID.randomUUID().toString();
                    try {
                        attsetSave.exec(id, attsetid, description.toString());
                        for (JProductAttEditI item : itemslist) {
                            attinstSave.exec(UUID.randomUUID().toString(), id, item.getAttribute(), item.getValue());
                        }
                    } catch (BasicException ex) {
                        return;
                    }
                }
            }
            ok = true;
            attInstanceId = id;
            attInstanceDescription = description.toString();
            dispose();
        }
    }

    public boolean isOK() {
        return ok;
    }

    public String getAttributeSetInst() {
        return attInstanceId;
    }

    public String getAttributeSetInstDescription() {
        return attInstanceDescription;
    }

    private static class AttributeInstInfo {

        private String attid;
        private String attname;
        private String id;
        private String value;

        public AttributeInstInfo(String attid, String attname, String id, String value) {
            this.attid = attid;
            this.attname = attname;
            this.id = id;
            this.value = value;
        }

        public String getAttid() {
            return attid;
        }

        public String getAttname() {
            return attname;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        JPanel1 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        JbtnPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.PAGE_AXIS));
        jPanel5.add(jPanel2, java.awt.BorderLayout.NORTH);

        JPanel1.setLayout(null);

        jPanel1.setAutoscrolls(true);
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JPanel1.add(jPanel1);
        jPanel1.setBounds(240, 5, 10, 10);
        JPanel1.add(JbtnPanel);
        JbtnPanel.setBounds(0, 0, 320, 170);

        jPanel5.add(JPanel1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel5, java.awt.BorderLayout.CENTER);

        jPanel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.Y_AXIS));
        jPanel3.add(jPanel4, java.awt.BorderLayout.NORTH);

        getContentPane().add(jPanel3, java.awt.BorderLayout.EAST);

        setSize(new java.awt.Dimension(334, 209));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JPanel1;
    private javax.swing.JPanel JbtnPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    // End of variables declaration//GEN-END:variables

}
