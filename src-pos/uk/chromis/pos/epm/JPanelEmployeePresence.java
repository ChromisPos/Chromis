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


package uk.chromis.pos.epm;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import uk.chromis.basic.BasicException;
import uk.chromis.beans.JFlowPanel;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.BeanFactoryApp;
import uk.chromis.pos.forms.BeanFactoryException;
import uk.chromis.pos.forms.JPanelView;
import uk.chromis.pos.sync.DataLogicSync;


public class JPanelEmployeePresence extends javax.swing.JPanel implements JPanelView, BeanFactoryApp {

    private AppView app;
    private DataLogicPresenceManagement dlpresencemanagement;
    private JFlowPanel jBreaks;
    private DataLogicSync dlSync;
    
    public JPanelEmployeePresence() {
        initComponents();
        this.setVisible(true);
    }

    @Override
    public void init(AppView app) throws BeanFactoryException {
        this.app = app;
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync"); 
        dlpresencemanagement = (DataLogicPresenceManagement) app.getBean("uk.chromis.pos.epm.DataLogicPresenceManagement");
    }

    private void listBreaks()
    {
        try {
            jScrollPane1.getViewport().setView(null);
            jBreaks = new JFlowPanel();
            jBreaks.applyComponentOrientation(getComponentOrientation());
            java.util.List breaks = dlpresencemanagement.listBreaksVisible(dlSync.getSiteGuid());
            for (int i = 0; i < breaks.size(); i++) {

                Break m_break = (Break) breaks.get(i);

                JButton btn = new JButton(new BreakAction(m_break));
                btn.applyComponentOrientation(getComponentOrientation());
                btn.setFocusPainted(false);
                btn.setFocusable(false);
                btn.setRequestFocusEnabled(false);
                btn.setHorizontalAlignment(SwingConstants.LEADING);
                btn.setMaximumSize(new Dimension(190, 50));
                btn.setPreferredSize(new Dimension(190, 50));
                btn.setMinimumSize(new Dimension(190, 50));

                jBreaks.add(btn);
            }
            jScrollPane1.getViewport().setView(jBreaks);

        } catch (BasicException ee) {
        }
    }

    private class BreakAction extends AbstractAction {

        private Break m_break;

        public BreakAction(Break aBreak) {
            m_break = aBreak;
            putValue(Action.NAME, m_break.getName());
        }

        public Break getBreak() {
            return m_break;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                dlpresencemanagement.StartBreak(app.getAppUserView().getUser().getId(), m_break.getId());
                message.setText(app.getAppUserView().getUser().getName()+" "+AppLocal.getIntString("message.leavefor")+" "+ m_break.getName()+" "+AppLocal.getIntString("message.at")+" "+Formats.TIMESTAMP.formatValue(new Date()));
                BreakAction();
            } catch (BasicException ex) {
                message.setText(AppLocal.getIntString("message.probleminbreak"));
            }
        }
    }

    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.CheckInCheckOut");
    }

    @Override
    public void activate() throws BasicException {
        boolean isOnLeave = dlpresencemanagement.IsOnLeave(app.getAppUserView().getUser().getId());
        listBreaks();
        if (isOnLeave) {
            message.setText(app.getAppUserView().getUser().getName()+" "+AppLocal.getIntString("message.leavecontrol"));
            LeaveAction();
        } else {
            boolean isCheckedIn = dlpresencemanagement.IsCheckedIn(app.getAppUserView().getUser().getId());
            if (isCheckedIn) {
                Date lastCheckIn = dlpresencemanagement.GetLastCheckIn(app.getAppUserView().getUser().getId());
                message.setText(app.getAppUserView().getUser().getName()+" "+AppLocal.getIntString("message.checkedin")+" "+Formats.TIMESTAMP.formatValue(lastCheckIn));
                CheckInAction();
            } else {
                Date lastCheckOut = dlpresencemanagement.GetLastCheckOut(app.getAppUserView().getUser().getId());
                if (lastCheckOut != null) {
                    message.setText(app.getAppUserView().getUser().getName()+" "+AppLocal.getIntString("message.checkedout")+" "+Formats.TIMESTAMP.formatValue(lastCheckOut));
                } else {
                    message.setText(app.getAppUserView().getUser().getName()+" "+AppLocal.getIntString("message.noshift"));
                }
                CheckOutAction();
            }
            boolean isOnBreak = dlpresencemanagement.IsOnBreak(app.getAppUserView().getUser().getId());
            if (isOnBreak) {
                Object[] LastBreak = (Object[]) dlpresencemanagement.GetLastBreak(app.getAppUserView().getUser().getId());
                message.setText(app.getAppUserView().getUser().getName()+" "+AppLocal.getIntString("message.leavefor")+" "+(String) LastBreak[0] +" "+AppLocal.getIntString("message.at")+" "+ Formats.TIMESTAMP.formatValue((Date) LastBreak[1]));
                BreakAction();
            }
        }
    }

    @Override
    public boolean deactivate() {
        return true;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public Object getBean() {
        return this;
    }

    private void CheckInAction() {
        btnCheckIn.setEnabled(false);
        btnCheckOut.setEnabled(true);
        jBreaks.setEnabled(true);
    }

    private void CheckOutAction() {
        btnCheckIn.setEnabled(true);
        btnCheckOut.setEnabled(false);
        jBreaks.setEnabled(false);
    }
    
    private void BreakAction() {
        btnCheckIn.setEnabled(true);
        btnCheckOut.setEnabled(true);
        jBreaks.setEnabled(false);
    }

    private void LeaveAction() {
        btnCheckIn.setEnabled(false);
        btnCheckOut.setEnabled(false);
        jBreaks.setEnabled(false);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCheckIn = new javax.swing.JButton();
        btnCheckOut = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        message = new javax.swing.JLabel();

        btnCheckIn.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnCheckIn.setText("Check In");
        btnCheckIn.setMaximumSize(new java.awt.Dimension(85, 23));
        btnCheckIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckInActionPerformed(evt);
            }
        });

        btnCheckOut.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnCheckOut.setText("Check Out");
        btnCheckOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckOutActionPerformed(evt);
            }
        });

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(570, 120));

        message.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        message.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        message.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(javax.swing.UIManager.getDefaults().getColor("Button.darkShadow")), javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
        message.setOpaque(true);
        message.setPreferredSize(new java.awt.Dimension(160, 25));
        message.setRequestFocusEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(message, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(message, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCheckOut, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCheckInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckInActionPerformed
        try {
            boolean isOnBreak = dlpresencemanagement.IsOnBreak(app.getAppUserView().getUser().getId());
            if (isOnBreak) {
                dlpresencemanagement.EndBreak(app.getAppUserView().getUser().getId());
                message.setText(app.getAppUserView().getUser().getName()+AppLocal.getIntString("message.breakoverandcheckedin")+" "+Formats.TIMESTAMP.formatValue(new Date()));
            } else {
                dlpresencemanagement.CheckIn(app.getAppUserView().getUser().getId());
                message.setText(app.getAppUserView().getUser().getName()+" "+AppLocal.getIntString("message.checkedin")+" "+Formats.TIMESTAMP.formatValue(new Date()));
            }
        } catch (BasicException ex) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotcheckin"));
            msg.show(this);
        }
        CheckInAction();
}//GEN-LAST:event_btnCheckInActionPerformed

    private void btnCheckOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckOutActionPerformed
        try {
            boolean isOnBreak = dlpresencemanagement.IsOnBreak(app.getAppUserView().getUser().getId());
            if (isOnBreak) {
                dlpresencemanagement.EndBreak(app.getAppUserView().getUser().getId());
                message.setText(app.getAppUserView().getUser().getName()+" "+AppLocal.getIntString("message.breakoverandcheckedout")+" "+Formats.TIMESTAMP.formatValue(new Date()));
            } else {
                message.setText(app.getAppUserView().getUser().getName()+" "+AppLocal.getIntString("message.checkedout")+" "+Formats.TIMESTAMP.formatValue(new Date()));
            }
            dlpresencemanagement.CheckOut(app.getAppUserView().getUser().getId());
        } catch (BasicException ex) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotcheckout"));
            msg.show(this);
        }
        CheckOutAction();
}//GEN-LAST:event_btnCheckOutActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheckIn;
    private javax.swing.JButton btnCheckOut;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel message;
    // End of variables declaration//GEN-END:variables
}
