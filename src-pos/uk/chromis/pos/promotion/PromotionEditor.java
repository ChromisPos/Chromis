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

package uk.chromis.pos.promotion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.commons.lang.StringUtils;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.gui.JMessageDialog;
import uk.chromis.data.gui.ListValModel;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.LocalRes;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.pos.admin.DataLogicAdmin;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.sync.DataLogicSync;

/**
 *
 * @author John
 */
public class PromotionEditor extends javax.swing.JPanel
        implements EditorRecord, ListSelectionListener {

    private final DirtyManager m_Dirty;
    DataLogicPromotions m_dlPromotions;
    DataLogicAdmin m_dlAdmin;
    DataLogicSystem m_dlSystem;
    DataLogicSync m_dlSync;
    private final AppView m_App;
    private String m_ID;
    private String m_criteria;
    private String siteGuid;

    private final int m_IndexID;
    private final int m_IndexName;
    private final int m_IndexCriteria;
    private final int m_IndexScript;
    private final int m_IndexEnabled;
    private final int m_IndexAllProducts;

    private SentenceList m_SentenceProducts;
    private ListValModel m_ModelProducts;

    private SentenceList m_SentenceResource;
    private ComboBoxValModel m_ModelResource;

    @Override
    public void refreshGuid(String siteGuid) {
        this.siteGuid = siteGuid;
        m_SentenceResource = m_dlPromotions.getResourceScriptListSentence(siteGuid);
        try {
            m_ModelResource = new ComboBoxValModel(m_SentenceResource.list());
        } catch (BasicException ex) {
            Logger.getLogger(PromotionEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        jComboBoxResources.setModel(m_ModelResource);
    }

    class ProductsListCellRenderer extends JComponent
            implements ListCellRenderer {

        Color listForeground, listBackground,
                listSelectionForeground,
                listSelectionBackground;

        DefaultListCellRenderer m_defaultRenderer;
        JCheckBox m_checkbox;
        JLabel m_Label;

        public ProductsListCellRenderer() {
            setLayout(new BorderLayout());
            m_defaultRenderer = new DefaultListCellRenderer();
            m_checkbox = new JCheckBox();
            add(m_checkbox, BorderLayout.WEST);
            add(m_defaultRenderer, BorderLayout.CENTER);

            UIDefaults uid = UIManager.getLookAndFeel().getDefaults();
            listForeground = uid.getColor("List.foreground");
            listBackground = uid.getColor("List.background");
            listSelectionForeground = uid.getColor("List.selectionForeground");
            listSelectionBackground = uid.getColor("List.selectionBackground");
        }

        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            Object[] avalues = (Object[]) value;
            String name = (String) avalues[DataLogicPromotions.INDEX_PROMOTEDPRODUCT_NAME];
            String ref = (String) avalues[DataLogicPromotions.INDEX_PROMOTEDPRODUCT_REFERENCE];
            String product = ref + "-" + name;

            m_defaultRenderer.getListCellRendererComponent(list, product, index,
                    isSelected, cellHasFocus);

            m_checkbox.setSelected(isSelected);

            Component[] comps = getComponents();
            for (Component comp : comps) {
                comp.setForeground(listForeground);
                comp.setBackground(listBackground);
            }

            return this;
        }
    }

    /**
     * Creates new form
     *
     * @param app
     * @param dirty
     */
    public PromotionEditor(AppView app, DirtyManager dirty) {
        initComponents();

        m_dlPromotions = (DataLogicPromotions) app.getBean("uk.chromis.pos.promotion.DataLogicPromotions");
        m_dlAdmin = (DataLogicAdmin) app.getBean("uk.chromis.pos.admin.DataLogicAdmin");
        m_dlSystem = (DataLogicSystem) app.getBean("uk.chromis.pos.forms.DataLogicSystem");
        m_dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        siteGuid = m_dlSync.getSiteGuid();

        m_App = app;
        m_Dirty = dirty;
        m_criteria = null;
        m_ID = null;

        m_IndexID = m_dlPromotions.getIndexOf("ID");
        m_IndexName = m_dlPromotions.getIndexOf("NAME");
        m_IndexCriteria = m_dlPromotions.getIndexOf("CRITERIA");
        m_IndexScript = m_dlPromotions.getIndexOf("SCRIPT");
        m_IndexEnabled = m_dlPromotions.getIndexOf("ISENABLED");
        m_IndexAllProducts = m_dlPromotions.getIndexOf("ALLPRODUCTS");

        m_ModelResource = new ComboBoxValModel();
        jComboBoxResources.setModel(m_ModelResource);

        ListCellRenderer renderer = new ProductsListCellRenderer();
        jListProducts.setCellRenderer(renderer);
        jListProducts.addListSelectionListener(this);
        jListProducts.setSelectionModel(new DefaultListSelectionModel() {
            boolean gestureStarted = false;

            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (!gestureStarted) {
                    if (isSelectedIndex(index0)) {
                        super.removeSelectionInterval(index0, index1);
                    } else {
                        super.addSelectionInterval(index0, index1);
                    }
                }
                gestureStarted = true;
            }

            @Override
            public void setValueIsAdjusting(boolean isAdjusting) {
                if (isAdjusting == false) {
                    gestureStarted = false;
                }
            }

        });

        m_ModelProducts = new ListValModel();
        jListProducts.setModel(m_ModelProducts);

        m_jName.getDocument().addDocumentListener(m_Dirty);
        m_jTextCriteria.getDocument().addDocumentListener(m_Dirty);
        m_jTextScript.getDocument().addDocumentListener(m_Dirty);
        jCheckBoxEnabled.addActionListener(m_Dirty);
        jCheckBoxAllProducts.addActionListener(m_Dirty);

    }

    /**
     *
     * @throws BasicException
     */
    public void activate() throws BasicException {

        m_SentenceResource = m_dlPromotions.getResourceScriptListSentence(siteGuid);
        m_ModelResource = new ComboBoxValModel(m_SentenceResource.list());
        jComboBoxResources.setModel(m_ModelResource);
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting() == false) {
            if (m_ID != null) {
                m_Dirty.setDirty(true);
            }
        }
    }

    /**
     *
     */
    @Override
    public void refresh() {

        if (m_ID != null) {
            // Set Promotionid in the selected products
            List<Object> selected = jListProducts.getSelectedValuesList();
            List<String> aProducts = new ArrayList<String>();

            for (Object item : selected) {
                Object[] values = (Object[]) item;
                aProducts.add(Datas.STRING.toString(values[DataLogicPromotions.INDEX_PROMOTEDPRODUCT_ID]));
            }

            try {
                m_dlPromotions.resetPromotionID(m_ID, aProducts);
            } catch (BasicException ex) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotexecute"), ex));
            }
        }
    }

    private void SelectAllProducts(Boolean bSelect) {
        if (!bSelect) {
            jListProducts.clearSelection();
        } else {
            int end = m_ModelProducts.getSize() - 1;
            if (end >= 0) {
                jListProducts.addSelectionInterval(0, end);
            }
        }
    }

    private void enableAll(boolean b) {
        m_jName.setEnabled(b);
        jCheckBoxEnabled.setEnabled(b);
        m_jTextScript.setEnabled(b);
        jComboBoxResources.setEnabled(b);
        jCheckBoxAllProducts.setEnabled(b);
    }

    /**
     *
     */
    @Override
    public void writeValueEOF() {
        m_ID = null;
        m_criteria = null;
        showProducts();

        m_jName.setText(null);
        m_jTextCriteria.setText(null);
        m_jTextScript.setText(null);
        jCheckBoxEnabled.setSelected(false);
        m_ModelResource.setSelectedKey(null);
        jCheckBoxAllProducts.setSelected(false);

        enableAll(false);
        jButtonScript.setEnabled(false);
    }

    /**
     *
     */
    @Override
    public void writeValueInsert() {
        m_ID = UUID.randomUUID().toString();
        m_criteria = null;
        showProducts();

        m_jName.setText(null);
        m_jTextCriteria.setText(null);
        m_jTextScript.setText(null);
        jCheckBoxEnabled.setSelected(false);
        m_ModelResource.setSelectedKey(null);
        jCheckBoxAllProducts.setSelected(false);
        SelectAllProducts(false);
        enableAll(true);
        jButtonScript.setEnabled(false);
    }

    private void showProducts() {

        boolean bDirtyFlag = m_Dirty.isDirty();

        if (m_ID == null) {
            m_ModelProducts = new ListValModel();
            jListProducts.setModel(m_ModelProducts);
        } else {
            m_SentenceProducts = m_dlPromotions.getPromotedProductsSentence(m_ID, m_criteria);
            try {
                m_ModelProducts = new ListValModel(m_SentenceProducts.list());
            } catch (BasicException ex) {
                JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotexecute"), ex));
            }
            jListProducts.setModel(m_ModelProducts);

            int count = m_ModelProducts.getSize();
            List<Integer> aIndexes = new ArrayList<Integer>();

            for (int i = 0; i < count; ++i) {
                Object[] avalues = (Object[]) m_ModelProducts.getElementAt(i);
                String pid = (String) avalues[DataLogicPromotions.INDEX_PROMOTEDPRODUCT_PROMOTIONID];
                if (pid != null && pid.contentEquals(m_ID)) {
                    aIndexes.add(i);
                }
            }

            int list[] = new int[aIndexes.size()];
            for (int i = 0; i < aIndexes.size(); ++i) {
                list[i] = aIndexes.get(i);
            }

            jListProducts.setSelectedIndices(list);
        }
        m_Dirty.setDirty(bDirtyFlag);
    }

    private void valuesToControls(Object[] attrset) {

        m_ID = m_dlPromotions.getFormatOf(m_IndexID).formatValue(attrset[m_IndexID]);
        m_jName.setText(m_dlPromotions.getFormatOf(m_IndexName).formatValue(attrset[m_IndexName]));
        m_jTextCriteria.setText(m_dlPromotions.getFormatOf(m_IndexCriteria).formatValue(attrset[m_IndexCriteria]));

        m_jTextCriteria.setCaretPosition(0);
        m_criteria = m_jTextCriteria.getText();

        m_jTextScript.setText(m_dlPromotions.getFormatOf(m_IndexScript).formatValue(attrset[m_IndexScript]));
        m_jTextScript.setCaretPosition(0);
        jCheckBoxEnabled.setSelected((Boolean) (attrset[m_IndexEnabled]));
        m_ModelResource.setSelectedKey(null);

        Boolean bAll = (Boolean) (attrset[m_IndexAllProducts]);
        jCheckBoxAllProducts.setSelected(bAll);
        SetProductSelection(bAll == false);
        showProducts();
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueDelete(Object value) {
        Object[] attrset = (Object[]) value;

        valuesToControls(attrset);

        enableAll(false);
        jButtonScript.setEnabled(false);

    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueEdit(Object value) {
        Object[] attrset = (Object[]) value;

        valuesToControls(attrset);

        enableAll(true);
        jButtonScript.setEnabled(false);

    }

    /**
     *
     * @return @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {
        Object[] attrset = new Object[m_dlPromotions.getFieldCount()];

        attrset[m_IndexID] = m_ID;
        attrset[m_IndexName] = m_jName.getText();
        attrset[m_IndexScript] = m_dlPromotions.getFormatOf(m_IndexScript).parseValue(m_jTextScript.getText());
        attrset[m_IndexEnabled] = jCheckBoxEnabled.isSelected();
        if (jCheckBoxAllProducts.isSelected()) {
            attrset[m_IndexAllProducts] = true;
            attrset[m_IndexCriteria] = null;
            SelectAllProducts(false);
            refresh();
        } else {
            attrset[m_IndexAllProducts] = false;
            attrset[m_IndexCriteria] = m_dlPromotions.getFormatOf(m_IndexCriteria).parseValue(m_jTextCriteria.getText());
        }
        return attrset;
    }

    /**
     *
     * @return
     */
    @Override
    public Component getComponent() {
        return this;
    }

    private boolean confirmOverWrite() {
        boolean result = false;
        if (JOptionPane.showConfirmDialog(this,
                LocalRes.getIntString("message.changeslost"),
                LocalRes.getIntString("Menu.Promotions"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            result = true;
        }
        return result;
    }

    private void SetProductSelection(Boolean bEnable) {
        // Enable/Disable the other product selection controls
        jLabel1.setEnabled(bEnable);
        jButtonHelp.setEnabled(bEnable);
        m_jTextCriteria.setEnabled(bEnable);
        jButtonTest.setEnabled(bEnable);
        jLabel5.setEnabled(bEnable);
        jButtonSelect.setEnabled(bEnable);
        jButtonDeselect.setEnabled(bEnable);
        jListProducts.setEnabled(bEnable);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        m_jName = new javax.swing.JTextField();
        jPanelProducts = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPaneCriteria = new javax.swing.JScrollPane();
        m_jTextCriteria = new javax.swing.JTextArea();
        jButtonHelp = new javax.swing.JButton();
        jButtonTest = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jButtonSelect = new javax.swing.JButton();
        jButtonDeselect = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListProducts = new javax.swing.JList();
        jPanelScript = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jComboBoxResources = new javax.swing.JComboBox();
        jScrollPaneScript = new javax.swing.JScrollPane();
        m_jTextScript = new javax.swing.JTextArea();
        jButtonScript = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jCheckBoxAllProducts = new eu.hansolo.custom.SteelCheckBox();
        jCheckBoxEnabled = new eu.hansolo.custom.SteelCheckBox();

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("Label.Name")); // NOI18N

        m_jName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel1.setText("Criteria");

        m_jTextCriteria.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jTextCriteria.setLineWrap(true);
        m_jTextCriteria.setMaximumSize(null);
        jScrollPaneCriteria.setViewportView(m_jTextCriteria);
        m_jTextCriteria.getAccessibleContext().setAccessibleParent(jScrollPaneCriteria);

        jButtonHelp.setText("Help");
        jButtonHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHelpActionPerformed(evt);
            }
        });

        jButtonTest.setText("Run Query");
        jButtonTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTestActionPerformed(evt);
            }
        });

        jLabel5.setText("Products included");

        jButtonSelect.setText("All");
        jButtonSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectActionPerformed(evt);
            }
        });

        jButtonDeselect.setText("None");
        jButtonDeselect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeselectActionPerformed(evt);
            }
        });

        jScrollPane1.setMaximumSize(null);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(175, 130));
        jScrollPane1.setRequestFocusEnabled(false);

        jListProducts.setMinimumSize(new java.awt.Dimension(104, 19));
        jScrollPane1.setViewportView(jListProducts);

        javax.swing.GroupLayout jPanelProductsLayout = new javax.swing.GroupLayout(jPanelProducts);
        jPanelProducts.setLayout(jPanelProductsLayout);
        jPanelProductsLayout.setHorizontalGroup(
            jPanelProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProductsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelProductsLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonHelp))
                    .addComponent(jScrollPaneCriteria, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelProductsLayout.createSequentialGroup()
                        .addComponent(jButtonTest)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelProductsLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(48, 48, 48)
                        .addComponent(jButtonSelect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDeselect))))
        );
        jPanelProductsLayout.setVerticalGroup(
            jPanelProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProductsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonDeselect)
                        .addComponent(jButtonSelect)
                        .addComponent(jLabel5)
                        .addComponent(jButtonHelp))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelProductsLayout.createSequentialGroup()
                        .addGroup(jPanelProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPaneCriteria, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonTest))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jLabel3.setText("Script");

        jLabel4.setText("Available script resources:");

        jComboBoxResources.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxResourcesActionPerformed(evt);
            }
        });

        m_jTextScript.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jTextScript.setLineWrap(true);
        m_jTextScript.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                m_jTextScriptKeyTyped(evt);
            }
        });
        jScrollPaneScript.setViewportView(m_jTextScript);
        m_jTextScript.getAccessibleContext().setAccessibleParent(jScrollPaneScript);

        jButtonScript.setText("Copy");
        jButtonScript.setName(""); // NOI18N
        jButtonScript.setRolloverEnabled(false);
        jButtonScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonScriptActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelScriptLayout = new javax.swing.GroupLayout(jPanelScript);
        jPanelScript.setLayout(jPanelScriptLayout);
        jPanelScriptLayout.setHorizontalGroup(
            jPanelScriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelScriptLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelScriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneScript)
                    .addGroup(jPanelScriptLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxResources, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonScript)))
                .addContainerGap())
        );
        jPanelScriptLayout.setVerticalGroup(
            jPanelScriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelScriptLayout.createSequentialGroup()
                .addGroup(jPanelScriptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonScript)
                    .addComponent(jComboBoxResources, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneScript, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.setPreferredSize(new java.awt.Dimension(143, 32));

        jCheckBoxAllProducts.setText("All Products");
        jCheckBoxAllProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxAllProductsActionPerformed(evt);
            }
        });

        jCheckBoxEnabled.setText("Enabled");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxAllProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxEnabled, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jCheckBoxAllProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBoxEnabled, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelScript, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
                            .addComponent(jPanelProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(m_jName, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanelProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelScript, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxResourcesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxResourcesActionPerformed
        String name = m_ModelResource.getSelectedText();
        if (name != null && name.length() > 0) {
            jButtonScript.setEnabled(true);
        } else {
            jButtonScript.setEnabled(false);
        }
    }//GEN-LAST:event_jComboBoxResourcesActionPerformed

    private void jButtonScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonScriptActionPerformed
        String scriptname = m_ModelResource.getSelectedText();
        if (scriptname != null && scriptname.length() > 0) {
            String script = m_dlSystem.getResourceAsText(scriptname);

            if (script != null && script.length() > 0) {
                boolean replace = true;

                if (m_jTextScript.getText().length() > 0) {
                    replace = confirmOverWrite();
                }
                if (replace) {
                    m_jTextScript.setText(script);
                    if (StringUtils.isEmpty(m_jName.getText())) {
                        m_jName.setText(scriptname);
                    }
                }
            }
        }
    }//GEN-LAST:event_jButtonScriptActionPerformed

    private void m_jTextScriptKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_m_jTextScriptKeyTyped
        m_ModelResource.setSelectedKey(null);
    }//GEN-LAST:event_m_jTextScriptKeyTyped

    private void jButtonHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHelpActionPerformed
        JOptionPane.showMessageDialog(this,
                m_dlSystem.getResourceAsText("help.promotion"));
    }//GEN-LAST:event_jButtonHelpActionPerformed

    private void jButtonTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTestActionPerformed
        m_criteria = m_jTextCriteria.getText();
        showProducts();
    }//GEN-LAST:event_jButtonTestActionPerformed

    private void jButtonSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectActionPerformed
        SelectAllProducts(true);
    }//GEN-LAST:event_jButtonSelectActionPerformed

    private void jButtonDeselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeselectActionPerformed
        SelectAllProducts(false);
    }//GEN-LAST:event_jButtonDeselectActionPerformed

    private void jCheckBoxAllProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxAllProductsActionPerformed
        SetProductSelection(!(jCheckBoxAllProducts.isSelected()));
    }//GEN-LAST:event_jCheckBoxAllProductsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDeselect;
    private javax.swing.JButton jButtonHelp;
    private javax.swing.JButton jButtonScript;
    private javax.swing.JButton jButtonSelect;
    private javax.swing.JButton jButtonTest;
    private eu.hansolo.custom.SteelCheckBox jCheckBoxAllProducts;
    private eu.hansolo.custom.SteelCheckBox jCheckBoxEnabled;
    private javax.swing.JComboBox jComboBoxResources;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JList jListProducts;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelProducts;
    private javax.swing.JPanel jPanelScript;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneCriteria;
    private javax.swing.JScrollPane jScrollPaneScript;
    private javax.swing.JTextField m_jName;
    private javax.swing.JTextArea m_jTextCriteria;
    private javax.swing.JTextArea m_jTextScript;
    // End of variables declaration//GEN-END:variables
}
