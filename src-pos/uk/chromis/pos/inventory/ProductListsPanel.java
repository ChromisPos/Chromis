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

package uk.chromis.pos.inventory;

import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.pos.catalog.CatalogSelector;
import uk.chromis.pos.catalog.JCatalog;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.panels.JProductFinder;
import uk.chromis.pos.ticket.ProductInfoExt;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import uk.chromis.data.gui.ListValModel;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.BeanFactoryApp;
import uk.chromis.pos.forms.JPanelView;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.ticket.PlayWave;

public final class ProductListsPanel extends JPanel implements JPanelView, BeanFactoryApp {

    private CatalogSelector m_cat;

    private String productid;
    private String productcode;
    private String productref;
    private String productname;

    private ComboBoxValModel m_NameListModel;
    private SentenceList m_sentNameLists;

    private ListValModel m_ProductsListModel;
    private SentenceList m_sentProductsList;

    private AppView m_App;
    private DataLogicSales m_dlSales;
    private String siteGuid;
    private DataLogicSync dlSync;

    public ProductListsPanel() {
    }

    /**
     *
     * @param app
     */
    @Override
    public void init(AppView app) {
        m_App = app;
        initComponents();

        m_App = app;
        m_dlSales = (DataLogicSales) m_App.getBean("uk.chromis.pos.forms.DataLogicSales");
        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        siteGuid = dlSync.getSiteGuid();
        
        m_sentNameLists = m_dlSales.getProductListList();
        m_NameListModel = new ComboBoxValModel();
        m_jList.setModel(m_NameListModel);

        m_cat = new JCatalog(m_dlSales, siteGuid);
        m_cat.addActionListener(new CatalogListener());

        catcontainer.add(m_cat.getComponent(), BorderLayout.CENTER);

        setControls();

    }

    private void setControls() {

        jButtonNewList.setEnabled(true);
        jButtonImport.setEnabled(true);
        jListProducts.setEnabled(true);
        jproduct.setEnabled(false);

        if (m_jList.getItemCount() == 0 || m_jList.getSelectedIndex() == -1) {
            // List is empty or no item selected
            m_cat.setComponentEnabled(false);
            jButtonRemoveProduct.setEnabled(false);
            jButtonAddProduct.setEnabled(false);
            jButtonDeleteList.setEnabled(false);
            m_FindProduct.setEnabled(false);
            m_jEnter1.setEnabled(false);
            m_jbarcode.setEnabled(false);
            m_jreference.setEnabled(false);
        } else {
            // List has an entry selected  
            m_cat.setComponentEnabled(true);
            jButtonDeleteList.setEnabled(true);
            m_FindProduct.setEnabled(true);
            m_jEnter1.setEnabled(true);
            m_jbarcode.setEnabled(true);
            m_jreference.setEnabled(true);

            if (productid != null && !productid.isEmpty()) {
                jButtonAddProduct.setEnabled(true);
            } else {
                jButtonAddProduct.setEnabled(false);
            }

            if (jListProducts.getSelectedIndex() == -1) {
                jButtonRemoveProduct.setEnabled(false);
            } else {
                jButtonRemoveProduct.setEnabled(true);
            }
        }
    }

    @Override
    public Object getBean() {
        return this;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    public String getTitle() {
        return null;
    }

    /**
     *
     * @throws BasicException
     */
    public void activate() throws BasicException {
        m_cat.loadCatalog(siteGuid);

        m_NameListModel = new ComboBoxValModel(m_sentNameLists.list());
        m_jList.setModel(m_NameListModel);

        setControls();
    }

    @Override
    public boolean deactivate() {
        return true;
    }

    private void assignProduct(ProductInfoExt prod) {

        if (prod == null) {
            productid = null;
            productref = null;
            productcode = null;
            productname = null;
            jproduct.setText(null);
            m_jreference.setText(null);
            m_jbarcode.setText(null);
        } else {
            productid = prod.getID();
            productref = prod.getReference();
            productcode = prod.getCode();
            productname = prod.getName();
            jproduct.setText(productname);
            m_jreference.setText(productref);
            m_jbarcode.setText(productcode);

        }

        setControls();
    }

    private void assignProductById(String Id) {
        try {
            ProductInfoExt oProduct = m_dlSales.getProductInfo(Id, siteGuid);
            if (oProduct == null) {
                assignProduct(null);
                new PlayWave("error.wav").start(); // playing WAVE file 
            } else {
                assignProduct(oProduct);
            }
        } catch (BasicException eData) {
            assignProduct(null);
            MessageInf msg = new MessageInf(eData);
            msg.show(this);
        }
    }

    private void assignProductByCode() {
        try {
            String code = m_jreference.getText();
            ProductInfoExt oProduct = m_dlSales.getProductInfoByCode(code, siteGuid);
            if (oProduct == null && code.startsWith("977")) {
                // This is an ISSN barcode (news and magazines)
                // the first 3 digits correspond to the 977 prefix assigned to serial publications,
                // the next 7 digits correspond to the ISSN of the publication
                // Anything after that is publisher dependant - we strip everything after 
                // the 10th character
                code = code.substring(0, 10);
                oProduct = m_dlSales.getProductInfoByCode(code, siteGuid);
            }

            if (oProduct != null) {
                assignProduct(oProduct);
            }
        } catch (BasicException eData) {
            assignProduct(null);
            MessageInf msg = new MessageInf(eData);
            msg.show(this);
        }
    }

    private class CatalogListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            assignProduct((ProductInfoExt) e.getSource());
            addProduct();
        }
    }

    private void updateProductList() {
        ProductListInfo info = (ProductListInfo) m_jList.getSelectedItem();
        if (info != null) {
            String name = info.getName();
            if (name != null && !name.isEmpty()) {
                m_sentProductsList = m_dlSales.getProductListItems(name);
                try {
                    m_ProductsListModel = new ListValModel(m_sentProductsList.list());
                } catch (BasicException ex) {
                    MessageInf msg = new MessageInf(ex);
                    msg.show(this);
                    m_ProductsListModel = new ListValModel();
                }
            } else {
                m_ProductsListModel = new ListValModel();
            }
        } else {
            m_ProductsListModel = new ListValModel();
        }
        jListProducts.setModel(m_ProductsListModel);
    }

    private void addProduct() {
        ProductListInfo info = (ProductListInfo) m_jList.getSelectedItem();
        String name = info.getName();

        if (name != null && !name.isEmpty() && productid != null && !productid.isEmpty()) {
            try {
                m_dlSales.addProductListItem(name, productid);
                updateProductList();
            } catch (BasicException ex) {
                MessageInf msg = new MessageInf(ex);
                msg.show(this);
            }
        }
    }

    private boolean startNewList() {
        boolean bResult = false;
        String name = JOptionPane.showInputDialog(this, AppLocal.getIntString("message.asklistname"));

        if (name != null && !name.isEmpty()) {
            // Find out if already exists
            int n = m_jList.getItemCount();
            boolean bExists = false;
            for (int i = 0; !bExists && i < m_jList.getItemCount(); ++i) {
                ProductListInfo info = (ProductListInfo) m_jList.getItemAt(i);
                if (name.equalsIgnoreCase(info.getName())) {
                    bExists = true;
                    m_jList.setSelectedItem(info);
                }
            }

            if (bExists) {
                JOptionPane.showMessageDialog(null,
                        AppLocal.getIntString("message.nameexists"),
                        AppLocal.getIntString("Menu.ProductLists"),
                        JOptionPane.WARNING_MESSAGE);
            } else {
                ProductListInfo info = new ProductListInfo(name);
                m_NameListModel.add(info);
                m_jList.setSelectedItem(info);
                bResult = true;
            }

            setControls();
        }

        return bResult;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jproduct = new javax.swing.JTextField();
        m_FindProduct = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        m_jreference = new javax.swing.JTextField();
        m_jList = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListProducts = new javax.swing.JList<>();
        jButtonRemoveProduct = new javax.swing.JButton();
        jButtonAddProduct = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jButtonNewList = new javax.swing.JButton();
        jButtonDeleteList = new javax.swing.JButton();
        jButtonImport = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        m_jbarcode = new javax.swing.JTextField();
        m_jEnter1 = new javax.swing.JButton();
        catcontainer = new javax.swing.JPanel();

        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setMinimumSize(new java.awt.Dimension(550, 250));
        setPreferredSize(new java.awt.Dimension(550, 270));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel1.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jPanel1.setMinimumSize(new java.awt.Dimension(780, 260));
        jPanel1.setPreferredSize(new java.awt.Dimension(780, 200));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText(AppLocal.getIntString("Label.List")); // NOI18N
        jLabel8.setMaximumSize(new java.awt.Dimension(40, 20));
        jLabel8.setMinimumSize(new java.awt.Dimension(40, 20));
        jLabel8.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 25));

        jproduct.setEditable(false);
        jproduct.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel1.add(jproduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, 270, 25));

        m_FindProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/search24.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        m_FindProduct.setToolTipText(bundle.getString("tiptext.searchproductlist")); // NOI18N
        m_FindProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_FindProductActionPerformed(evt);
            }
        });
        jPanel1.add(m_FindProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 120, 40, -1));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText(AppLocal.getIntString("label.prodref")); // NOI18N
        jLabel7.setMaximumSize(new java.awt.Dimension(40, 20));
        jLabel7.setMinimumSize(new java.awt.Dimension(40, 20));
        jLabel7.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 70, 25));

        m_jreference.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jreference.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jreferenceActionPerformed(evt);
            }
        });
        jPanel1.add(m_jreference, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 120, 170, 25));

        m_jList.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        m_jList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jListActionPerformed(evt);
            }
        });
        jPanel1.add(m_jList, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 330, 25));

        jListProducts.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListProductsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jListProducts);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 0, 310, 200));

        jButtonRemoveProduct.setText("<--");
        jButtonRemoveProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveProductActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonRemoveProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 70, -1, -1));

        jButtonAddProduct.setText("-->");
        jButtonAddProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddProductActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonAddProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 40, -1, -1));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText(AppLocal.getIntString("label.warehouse")); // NOI18N
        jLabel10.setMaximumSize(new java.awt.Dimension(40, 20));
        jLabel10.setMinimumSize(new java.awt.Dimension(40, 20));
        jLabel10.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 70, 25));

        jButtonNewList.setText(bundle.getString("label.newlist")); // NOI18N
        jButtonNewList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewListActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonNewList, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, -1, -1));

        jButtonDeleteList.setText(bundle.getString("label.deletelist")); // NOI18N
        jButtonDeleteList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteListActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonDeleteList, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 170, -1, -1));

        jButtonImport.setText(bundle.getString("label.importlist")); // NOI18N
        jButtonImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonImportActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonImport, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 170, -1, -1));

        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText(AppLocal.getIntString("label.prodbarcode")); // NOI18N
        jLabel9.setMaximumSize(new java.awt.Dimension(40, 20));
        jLabel9.setMinimumSize(new java.awt.Dimension(40, 20));
        jLabel9.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 70, 25));

        m_jbarcode.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jbarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbarcodeActionPerformed(evt);
            }
        });
        jPanel1.add(m_jbarcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 80, 170, 25));

        m_jEnter1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/barcode.png"))); // NOI18N
        m_jEnter1.setToolTipText(bundle.getString("tiptext.getbarcode")); // NOI18N
        m_jEnter1.setFocusPainted(false);
        m_jEnter1.setFocusable(false);
        m_jEnter1.setMaximumSize(new java.awt.Dimension(54, 33));
        m_jEnter1.setMinimumSize(new java.awt.Dimension(54, 33));
        m_jEnter1.setPreferredSize(new java.awt.Dimension(54, 33));
        m_jEnter1.setRequestFocusEnabled(false);
        m_jEnter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jEnter1ActionPerformed(evt);
            }
        });
        jPanel1.add(m_jEnter1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 80, 40, -1));

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        catcontainer.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        catcontainer.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        catcontainer.setMinimumSize(new java.awt.Dimension(0, 250));
        catcontainer.setPreferredSize(new java.awt.Dimension(0, 250));
        catcontainer.setLayout(new java.awt.BorderLayout());
        add(catcontainer, java.awt.BorderLayout.CENTER);
        catcontainer.getAccessibleContext().setAccessibleParent(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void m_jreferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jreferenceActionPerformed

        assignProductById(m_jreference.getText());

    }//GEN-LAST:event_m_jreferenceActionPerformed

    private void m_FindProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_FindProductActionPerformed
        assignProduct(JProductFinder.showMessage(this, m_dlSales, siteGuid));

}//GEN-LAST:event_m_FindProductActionPerformed

    private void jButtonImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonImportActionPerformed

        String last_folder = AppConfig.getInstance().getProperty("CSV.last_folder");
        JFileChooser chooser = new JFileChooser(last_folder == null ? "C:\\" : last_folder);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("csv files", "csv");
        chooser.setFileFilter(filter);
        chooser.showOpenDialog(null);
        File csvFile = chooser.getSelectedFile();

        // check if a file was selected        
        if (csvFile == null) {
            return;
        }

        File current_folder = chooser.getCurrentDirectory();
        // If we have a file lets save the directory for later use if it's different from the old
        if (last_folder == null || !last_folder.equals(current_folder.getAbsolutePath())) {
            AppConfig.getInstance().setProperty("CSV.last_folder", current_folder.getAbsolutePath());
            try {
                AppConfig.getInstance().save();
            } catch (IOException ex) {
                MessageInf msg = new MessageInf(ex);
                msg.show(this);
            }
        }

        String csv = csvFile.getName();
        if (!(csv.trim().equals(""))) {
            String csvFileName = csvFile.getAbsolutePath();
            if (startNewList()) {
                try {
                    // Get the data and store in the list
                    FileReader fr = new FileReader(csvFileName);
                    BufferedReader br = new BufferedReader(fr);

                    String line;
                    while ((line = br.readLine()) != null) {
                        line.trim();
                        m_jreference.setText(line);
                        assignProductByCode();
                        addProduct();
                    }
                } catch (FileNotFoundException ex) {
                    MessageInf msg = new MessageInf(ex);
                    msg.show(this);
                } catch (IOException ex) {
                    MessageInf msg = new MessageInf(ex);
                    msg.show(this);
                }

            }
        }

    }//GEN-LAST:event_jButtonImportActionPerformed

    private void m_jbarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbarcodeActionPerformed
        assignProductByCode();
    }//GEN-LAST:event_m_jbarcodeActionPerformed

    private void m_jEnter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jEnter1ActionPerformed
        assignProductByCode();
    }//GEN-LAST:event_m_jEnter1ActionPerformed

    private void jButtonNewListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewListActionPerformed
        startNewList();
    }//GEN-LAST:event_jButtonNewListActionPerformed

    private void jButtonAddProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddProductActionPerformed
        addProduct();
    }//GEN-LAST:event_jButtonAddProductActionPerformed

    private void jButtonRemoveProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveProductActionPerformed
        ProductListInfo info = (ProductListInfo) m_jList.getSelectedItem();
        String name = info.getName();
        if (name != null && !name.isEmpty()) {
            int[] list = jListProducts.getSelectedIndices();
            for (int entry : list) {
                ProductListItem item = (ProductListItem) m_ProductsListModel.getElementAt(entry);
                if (item != null) {
                    try {
                        m_dlSales.removeProductListItem(name, item.getID());
                        updateProductList();
                    } catch (BasicException ex) {
                        MessageInf msg = new MessageInf(ex);
                        msg.show(this);
                    }
                }
            }
        }
    }//GEN-LAST:event_jButtonRemoveProductActionPerformed

    private void m_jListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jListActionPerformed
        updateProductList();
        setControls();
    }//GEN-LAST:event_m_jListActionPerformed

    private void jListProductsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListProductsValueChanged
        setControls();
    }//GEN-LAST:event_jListProductsValueChanged

    private void jButtonDeleteListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteListActionPerformed
        ProductListInfo info = (ProductListInfo) m_jList.getSelectedItem();
        String name = info.getName();
        if (name != null && !name.isEmpty()) {
            if (JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.confirmdeletelist"),
                    AppLocal.getIntString("Menu.ProductLists"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                try {
                    m_dlSales.removeProductList(name);
                    m_NameListModel.del(info);
                    m_jList.setSelectedIndex(-1);
                } catch (BasicException ex) {
                    MessageInf msg = new MessageInf(ex);
                    msg.show(this);
                }
                updateProductList();
                setControls();
            }
        }
    }//GEN-LAST:event_jButtonDeleteListActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel catcontainer;
    private javax.swing.JButton jButtonAddProduct;
    private javax.swing.JButton jButtonDeleteList;
    private javax.swing.JButton jButtonImport;
    private javax.swing.JButton jButtonNewList;
    private javax.swing.JButton jButtonRemoveProduct;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jListProducts;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jproduct;
    private javax.swing.JButton m_FindProduct;
    private javax.swing.JButton m_jEnter1;
    private javax.swing.JComboBox m_jList;
    private javax.swing.JTextField m_jbarcode;
    private javax.swing.JTextField m_jreference;
    // End of variables declaration//GEN-END:variables

}
