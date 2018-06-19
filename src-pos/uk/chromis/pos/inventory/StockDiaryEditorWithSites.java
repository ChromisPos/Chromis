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
import uk.chromis.beans.DateUtils;
import uk.chromis.beans.JCalendarDialog;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.user.DirtyManager;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.format.Formats;
import uk.chromis.pos.catalog.CatalogSelector;
import uk.chromis.pos.catalog.JCatalog;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.panels.JProductFinder;
import uk.chromis.pos.sales.JProductAttEdit;
import uk.chromis.pos.ticket.ProductInfoExt;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.sync.SitesInfo;
import uk.chromis.pos.ticket.PlayWave;

public final class StockDiaryEditorWithSites extends javax.swing.JPanel implements EditorRecord { // JDlgEditProduct.CompletionCallback {

    private final CatalogSelector m_cat;

    private boolean m_canProductEdit = false;
    private String m_sID;
    private String productid;
    private String productref;
    private String productcode;
    private String productname;
    private String unitsinstock;
    private Double buyprice;
    private Double sellprice;
    private Double stocksecurity;
    private Double stockmaximum;
    private String attsetid;
    private String attsetinstid;
    private String attsetinstdesc;
    private String sAppUser;
    private String siteGuid;

    private final ComboBoxValModel m_ReasonModel;

    private SentenceList m_sentlocations;
    private ComboBoxValModel m_LocationsModel;
    private ComboBoxValModel m_SitesModel;

    private final AppView m_App;
    private final DataLogicSales m_dlSales;
    private DataLogicSync dlSync;
    private SentenceList m_sentSites;
    private DirtyManager m_Dirty;

    public StockDiaryEditorWithSites(AppView app, DirtyManager dirty, String siteGuid, Boolean isCentral) {

        m_App = app;
        m_dlSales = (DataLogicSales) m_App.getBean("uk.chromis.pos.forms.DataLogicSales");
        m_Dirty = dirty;

        dlSync = (DataLogicSync) app.getBean("uk.chromis.pos.sync.DataLogicSync");

        this.siteGuid = siteGuid;

        m_sentSites = dlSync.getSitesList();
        m_SitesModel = new ComboBoxValModel();

        initComponents();
        if (!dlSync.isCentral()) {
            jPanel1.remove(jSitesPanel);
            jPanel1.remove(jProductPanel);
            jPanel1.add(jProductPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 720, 200));
            jPanel1.add(jSitesPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 0, 210, 200));
            jSites.setVisible(false);
        }

        m_canProductEdit = app.getAppUserView().getUser().hasPermission("uk.chromis.pos.inventory.ProductsPanel");
        m_sentlocations = m_dlSales.getLocationsList(siteGuid);
        m_LocationsModel = new ComboBoxValModel();

        m_ReasonModel = new ComboBoxValModel();
        m_ReasonModel.add(MovementReason.IN_PURCHASE);
        m_ReasonModel.add(MovementReason.OUT_SALE);
        m_ReasonModel.add(MovementReason.IN_STOCKCHANGE);
        m_ReasonModel.add(MovementReason.OUT_STOCKCHANGE);
        m_ReasonModel.add(MovementReason.OUT_BREAK);
        m_ReasonModel.add(MovementReason.IN_REFUND);
        m_ReasonModel.add(MovementReason.OUT_REFUND);
        m_ReasonModel.add(MovementReason.IN_MOVEMENT);
        m_ReasonModel.add(MovementReason.OUT_MOVEMENT);
        m_ReasonModel.add(MovementReason.IN_OPEN_PACK);
        m_ReasonModel.add(MovementReason.OUT_OPEN_PACK);

        m_jreason.setModel(m_ReasonModel);

        m_cat = new JCatalog(m_dlSales, siteGuid);
        m_cat.addActionListener(new CatalogListener());

        mainpanel.add(m_cat.getComponent(), BorderLayout.CENTER);

        m_jdate.getDocument().addDocumentListener(dirty);
        m_jreason.addActionListener(dirty);
        m_jLocation.addActionListener(dirty);
        jproduct.getDocument().addDocumentListener(dirty);
        jattributes.getDocument().addDocumentListener(dirty);
        m_junits.getDocument().addDocumentListener(dirty);
        m_jprice.getDocument().addDocumentListener(dirty);
        m_jminimum.getDocument().addDocumentListener(dirty);
        m_jmaximum.getDocument().addDocumentListener(dirty);

        writeValueEOF();
    }

    public void activate() throws BasicException {
        List s;
        try {
            s = m_sentSites.list();
        } catch (BasicException ex) {
            s = dlSync.getSingleSite().list();
        }

        addFirst(s);
        m_SitesModel = new ComboBoxValModel(s);
        m_jSites.setModel(m_SitesModel);
        m_jSites.addActionListener(new ReloadActionListener());

        m_cat.loadCatalog(siteGuid);

        m_LocationsModel = new ComboBoxValModel(m_sentlocations.list());
        m_jLocation.setModel(m_LocationsModel);
    }

    protected void addFirst(List a) {
        // do nothing
    }

    @Override
    public void refresh() {
    }

    @Override
    public void writeValueEOF() {
        m_sID = null;
        m_jdate.setText(null);
        m_ReasonModel.setSelectedKey(null);
        m_LocationsModel.setSelectedKey(m_App.getInventoryLocation());
        productid = null;
        productref = null;
        productcode = null;
        productname = null;
        unitsinstock = null;
        buyprice = null;
        sellprice = null;
        stocksecurity = null;
        stockmaximum = null;
        m_jreference.setText(null);
        m_jcodebar.setText(null);
        jproduct.setText(null);
        m_junitsinstock.setText(null);
        m_jbuyprice.setText(null);
        m_jsellprice.setText(null);
        m_jminimum.setText(null);
        m_jmaximum.setText(null);
        attsetid = null;
        attsetinstid = null;
        attsetinstdesc = null;
        jattributes.setText(null);
        m_junits.setText(null);
        m_jprice.setText(null);
        m_jdate.setEnabled(false);
        m_jbtndate.setEnabled(false);
        m_jreason.setEnabled(false);
        m_jreference.setEnabled(false);
        m_EditProduct.setEnabled(false);
        m_jcodebar.setEnabled(false);
        m_jEnter.setEnabled(false);
        m_jLocation.setEnabled(false);
        jproduct.setEnabled(false);
        m_junitsinstock.setEnabled(false);
        m_jbuyprice.setEnabled(false);
        m_jsellprice.setEnabled(false);
        m_jminimum.setEnabled(false);
        m_jmaximum.setEnabled(false);
        m_FindProduct.setEnabled(false);
        jattributes.setEnabled(false);
        jEditAttributes.setEnabled(false);
        m_junits.setEnabled(false);
        m_jprice.setEnabled(false);
        m_cat.setComponentEnabled(false);
        //m_EditProduct.setEnabled(false);
    }

    @Override
    public void writeValueInsert() {
        m_sID = UUID.randomUUID().toString();
        m_jdate.setText(Formats.TIMESTAMP.formatValue(DateUtils.getTodayMinutes()));
        m_ReasonModel.setSelectedItem(MovementReason.IN_PURCHASE);
        m_LocationsModel.setSelectedKey(m_App.getInventoryLocation());
        productid = null;
        productref = null;
        productcode = null;
        productname = null;
        unitsinstock = null;
        buyprice = null;
        sellprice = null;
        stocksecurity = null;
        stockmaximum = null;

        m_jreference.setText(null);
        m_jcodebar.setText(null);
        jproduct.setText(null);
        m_junitsinstock.setText(null);
        m_jbuyprice.setText(null);
        m_jsellprice.setText(null);
        m_jminimum.setText(null);
        m_jmaximum.setText(null);
        attsetid = null;
        attsetinstid = null;
        attsetinstdesc = null;
        jattributes.setText(null);
        m_jcodebar.setText(null);
        m_junits.setText(null);
        m_jprice.setText(null);
        m_jdate.setEnabled(true);
        m_jbtndate.setEnabled(true);
        m_jreason.setEnabled(true);
        m_jreference.setEnabled(true);
        m_jcodebar.setEnabled(true);
        m_jEnter.setEnabled(true);
        m_jLocation.setEnabled(true);
        jproduct.setEnabled(true);
        m_jminimum.setEnabled(true);
        m_jmaximum.setEnabled(true);
        m_FindProduct.setEnabled(true);
        jattributes.setEnabled(true);
        jEditAttributes.setEnabled(true);
        m_junits.setEnabled(true);
        m_jprice.setEnabled(true);
        m_cat.setComponentEnabled(true);
        getRootPane().setDefaultButton(m_jEnter);
        m_jcodebar.requestFocusInWindow();
    }

    @Override
    public void writeValueDelete(Object value) {
        Object[] diary = (Object[]) value;
        m_sID = (String) diary[0];
        m_jdate.setText(Formats.TIMESTAMP.formatValue(diary[1]));
        m_ReasonModel.setSelectedKey(diary[2]);
        m_LocationsModel.setSelectedKey(diary[3]);
        productid = (String) diary[4];
        productref = (String) diary[8];
        productcode = (String) diary[9];
        productname = (String) diary[10];
        unitsinstock = (String) diary[14];
        stocksecurity = (Double) diary[15];
        stockmaximum = (Double) diary[16];
        buyprice = (Double) diary[17];
        sellprice = (Double) diary[18];
        m_jreference.setText(productref);
        m_jcodebar.setText(productcode);
        jproduct.setText(productname);
        m_junitsinstock.setText(unitsinstock);
        m_jbuyprice.setText(Formats.CURRENCY.formatValue(buyprice));
        m_jsellprice.setText(Formats.CURRENCY.formatValue(sellprice));

        m_jminimum.setText(Formats.DOUBLE.formatValue(stocksecurity));
        m_jmaximum.setText(Formats.DOUBLE.formatValue(stockmaximum));
        attsetid = (String) diary[11];
        attsetinstid = (String) diary[5];
        attsetinstdesc = (String) diary[12];
        jattributes.setText(attsetinstdesc);
        m_junits.setText(Formats.DOUBLE.formatValue(signum((Double) diary[6], (Integer) diary[2])));
        m_jprice.setText(Formats.CURRENCY.formatValue(diary[7]));
        m_jdate.setEnabled(false);
        m_jbtndate.setEnabled(false);
        m_jreason.setEnabled(false);
        m_jreference.setEnabled(false);
        m_EditProduct.setEnabled(false);
        m_jcodebar.setEnabled(false);
        m_jEnter.setEnabled(false);
        m_jLocation.setEnabled(false);
        jproduct.setEnabled(false);
        m_junitsinstock.setEnabled(false);
        m_jbuyprice.setEnabled(false);
        m_jsellprice.setEnabled(false);

        m_jminimum.setEnabled(false);
        m_jmaximum.setEnabled(false);
        m_FindProduct.setEnabled(false);
        jattributes.setEnabled(false);
        jEditAttributes.setEnabled(false);
        m_junits.setEnabled(false);
        m_jprice.setEnabled(false);
        m_cat.setComponentEnabled(false);
    }

    /**
     *
     * @param value
     */
    @Override
    public void writeValueEdit(Object value) {
        Object[] diary = (Object[]) value;
        m_sID = (String) diary[0];
        m_jdate.setText(Formats.TIMESTAMP.formatValue(diary[1]));
        m_ReasonModel.setSelectedKey(diary[2]);
        m_LocationsModel.setSelectedKey(diary[3]);
        productid = (String) diary[4];
        sAppUser = (String) diary[8];
        productref = (String) diary[9];
        productcode = (String) diary[10];
        productname = (String) diary[11];
        unitsinstock = (String) diary[14];
        stocksecurity = (Double) diary[15];
        stockmaximum = (Double) diary[16];
        buyprice = (Double) diary[17];
        sellprice = (Double) diary[18];
        siteGuid = (String) diary[19];

        m_jreference.setText(productref);
        m_jcodebar.setText(productcode);
        jproduct.setText(productname);
        m_junitsinstock.setText(unitsinstock);
        m_jbuyprice.setText(Formats.CURRENCY.formatValue(buyprice));
        m_jsellprice.setText(Formats.CURRENCY.formatValue(sellprice));

        m_jminimum.setText(Formats.DOUBLE.formatValue(stocksecurity));
        m_jmaximum.setText(Formats.DOUBLE.formatValue(stockmaximum));
        attsetid = (String) diary[12];
        attsetinstid = (String) diary[5];
        attsetinstdesc = (String) diary[13];
        jattributes.setText(attsetinstdesc);
        m_junits.setText(Formats.DOUBLE.formatValue(signum((Double) diary[6], (Integer) diary[2])));
        m_jprice.setText(Formats.CURRENCY.formatValue(diary[7]));
        m_jdate.setEnabled(false);
        m_jbtndate.setEnabled(false);
        m_jreason.setEnabled(false);
        m_jreference.setEnabled(false);
        //m_EditProduct.setEnabled(false);
        m_EditProduct.setEnabled(m_canProductEdit);
        m_jcodebar.setEnabled(false);
        m_jEnter.setEnabled(false);
        m_jLocation.setEnabled(false);
        jproduct.setEnabled(true);
        m_jminimum.setEnabled(true);
        m_jmaximum.setEnabled(true);
        m_FindProduct.setEnabled(true);
        jattributes.setEnabled(false);
        jEditAttributes.setEnabled(false);
        m_junits.setEnabled(false);
        m_jprice.setEnabled(false);
        m_cat.setComponentEnabled(false);
        //m_EditProduct.setEnabled(true);
    }

    /**
     *
     * @return @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {
        stocksecurity = (Double) Formats.DOUBLE.parseValue(m_jminimum.getText());
        stockmaximum = (Double) Formats.DOUBLE.parseValue(m_jmaximum.getText());

        return new Object[]{
            m_sID,
            Formats.TIMESTAMP.parseValue(m_jdate.getText()),
            m_ReasonModel.getSelectedKey(),
            m_LocationsModel.getSelectedKey(),
            productid,
            attsetinstid,
            samesignum((Double) Formats.DOUBLE.parseValue(m_junits.getText()), (Integer) m_ReasonModel.getSelectedKey()),
            Formats.CURRENCY.parseValue(m_jprice.getText()),
            m_App.getAppUserView().getUser().getName(),
            productref,
            productcode,
            productname,
            attsetid,
            attsetinstdesc,
            unitsinstock,
            stocksecurity,
            stockmaximum,
            buyprice,
            sellprice,
            siteGuid
        };
    }

    /**
     *
     * @return
     */
    @Override
    public Component getComponent() {
        return this;
    }

    private Double signum(Double d, Integer i) {
        if (d == null || i == null) {
            return d;
        } else if (i < 0) {
            return -d;
        } else {
            return d;
        }
    }

    private Double samesignum(Double d, Integer i) {

        if (d == null || i == null) {
            return d;
        } else if ((i > 0 && d < 0.0)
                || (i < 0 && d > 0.0)) {
            return -d;
        } else {
            return d;
        }
    }

    private void assignProduct(ProductInfoExt prod) {

        if (jproduct.isEnabled()) {
            if (prod == null) {
                productid = null;
                productref = null;
                productcode = null;
                productname = null;
                unitsinstock = null;
                buyprice = null;
                sellprice = null;
                stocksecurity = null;
                stockmaximum = null;
                attsetid = null;
                attsetinstid = null;
                attsetinstdesc = null;
                jproduct.setText(null);
                m_jcodebar.setText(null);
                m_jreference.setText(null);
                jattributes.setText(null);
                m_EditProduct.setEnabled(false);
                m_junitsinstock.setText(null);
                m_jbuyprice.setText(null);
                m_jsellprice.setText(null);
                m_jminimum.setText(null);
                m_jmaximum.setText(null);
            } else {
                productid = prod.getID();
                productref = prod.getReference();
                productcode = prod.getCode();
                productname = prod.getName();
                attsetid = prod.getAttributeSetID();

                try {
                    Double dStock = m_dlSales.findProductStock(
                            (String) m_LocationsModel.getSelectedKey(),
                            productid, attsetid, siteGuid);
                    unitsinstock = Formats.DOUBLE.formatValue(dStock);

                    System.out.println("Stock = " + unitsinstock);
                    
                    buyprice = prod.getPriceBuy();
                    sellprice = prod.getPriceSell();

                    stocksecurity = m_dlSales.findProductStockSecurity(
                            (String) m_LocationsModel.getSelectedKey(),
                            productid, siteGuid);

                    stockmaximum = m_dlSales.findProductStockMaximum(
                            (String) m_LocationsModel.getSelectedKey(),
                            productid, siteGuid);

                } catch (BasicException ex) {
                    unitsinstock = null;
                    stockmaximum = null;
                    stocksecurity = null;
                    buyprice = null;
                    sellprice = null;
                }

                attsetinstid = null;
                attsetinstdesc = null;
                jproduct.setText(productname);
                m_jcodebar.setText(productcode);
                m_jreference.setText(productref);
                m_junitsinstock.setText(unitsinstock);
                m_jbuyprice.setText(Formats.CURRENCY.formatValue(buyprice));
                m_jsellprice.setText(Formats.CURRENCY.formatValue(sellprice));

                m_jminimum.setText(Formats.DOUBLE.formatValue(stocksecurity));
                m_jmaximum.setText(Formats.DOUBLE.formatValue(stockmaximum));
                jattributes.setText(null);
                //m_EditProduct.setEnabled(true);
                m_EditProduct.setEnabled(m_canProductEdit);

                // calculo el precio sugerido para la entrada.
                MovementReason reason = (MovementReason) m_ReasonModel.getSelectedItem();
                Double dPrice = reason.getPrice(prod.getPriceBuy(), prod.getPriceSell());
                m_jprice.setText(Formats.CURRENCY.formatValue(dPrice));

            }
        }
    }

    /*
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
     */
    private void assignProductByCode() {
        try {
            String code = m_jcodebar.getText();
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

            if (oProduct == null) {
                new PlayWave("error.wav").start(); // playing WAVE file 
/*
                if (JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.createproduct"),
                        AppLocal.getIntString("message.title"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                 */
                if (m_canProductEdit && JOptionPane.showConfirmDialog(this, AppLocal.getIntString("message.createproduct"),
                        AppLocal.getIntString("message.title"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    newProduct();
                }
            } else {
                assignProduct(oProduct);
            }
        } catch (BasicException eData) {
            assignProduct(null);
            MessageInf msg = new MessageInf(eData);
            msg.show(this);
        }
    }

    private void assignProductByReference() {
        try {
            ProductInfoExt oProduct = m_dlSales.getProductInfoByReference(m_jreference.getText(), siteGuid);
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

    private void editProduct() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        //JDlgEditProduct dlg = new JDlgEditProduct(topFrame, true);
        //dlg.init(m_dlSales, m_Dirty, productid, null, siteGuid);
        //dlg.setCallbacks(this);
        //dlg.setVisible(true);
        JDlgEditProduct productDialog = new JDlgEditProduct(topFrame, true);
        productDialog.init(m_dlSales, m_Dirty, productid, null, siteGuid);
        productDialog.setVisible(true);
        updatePanelInfo(productDialog);
    }

    private void newProduct() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        //JDlgEditProduct dlg = new JDlgEditProduct(topFrame, true);
        JDlgEditProduct productDialog = new JDlgEditProduct(topFrame, true);
        String code = m_jcodebar.getText();
        //dlg.init(m_dlSales, m_Dirty, null, code, siteGuid);
        //dlg.setCallbacks(this);
        //dlg.setVisible(true);
        productDialog.init(m_dlSales, m_Dirty, null, code, siteGuid);
        productDialog.setVisible(true);
        updatePanelInfo(productDialog);

    }

    //@Override
    //public void notifyCompletionOk(String reference) {
    public void updatePanelInfo(JDlgEditProduct productDialog) {
        // Try to assign product again
        //if (reference != null) {
        if (productDialog.getProductID() != null) {
            writeValueInsert();
            //m_jreference.setText(reference);
            m_jreference.setText(productDialog.getProductID());

            jproduct.setEnabled(true);
            m_jminimum.setEnabled(true);
            m_jmaximum.setEnabled(true);

            assignProductByReference();

            // reload catalog layout only if a product was inserted
            if (productDialog.getExecutionStatus() == JDlgEditProduct.STATUS.INSERTED) {
                try {
                    m_cat.loadCatalog(siteGuid);
                } catch (BasicException ex) {
                    Logger.getLogger(StockDiaryEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    //@Override
    //public void notifyCompletionCancel() {
    //}
    @Override
    public void refreshGuid(String siteGuid) {
        this.siteGuid = siteGuid;

        try {
            m_sentlocations = m_dlSales.getLocationsList(siteGuid);
            m_LocationsModel = new ComboBoxValModel();
            m_LocationsModel = new ComboBoxValModel(m_sentlocations.list());
            m_jLocation.setModel(m_LocationsModel);
            m_LocationsModel.setSelectedFirst();

            m_cat.loadCatalog(siteGuid);

        } catch (BasicException ex) {
            Logger.getLogger(StockDiaryEditorWithSites.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getSite() {
        return siteGuid;
    }

    private class CatalogListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            assignProduct((ProductInfoExt) e.getSource());
        }
    }

    private class ReloadActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            refreshGuid(m_SitesModel.getSelectedKey().toString());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainpanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jSitesPanel = new javax.swing.JPanel();
        jSites = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        m_jSites = new javax.swing.JComboBox();
        jProductPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        m_junits = new javax.swing.JTextField();
        m_jreference = new javax.swing.JTextField();
        m_jcodebar = new javax.swing.JTextField();
        jproduct = new javax.swing.JTextField();
        m_jdate = new javax.swing.JTextField();
        m_jbtndate = new javax.swing.JButton();
        m_FindProduct = new javax.swing.JButton();
        m_jEnter = new javax.swing.JButton();
        m_EditProduct = new javax.swing.JButton();
        m_jprice = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        m_jreason = new javax.swing.JComboBox();
        m_jLocation = new javax.swing.JComboBox();
        m_junitsinstock = new javax.swing.JTextField();
        m_jbuyprice = new javax.swing.JTextField();
        m_jminimum = new javax.swing.JTextField();
        jattributes = new javax.swing.JTextField();
        m_jmaximum = new javax.swing.JTextField();
        m_jsellprice = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jEditAttributes = new javax.swing.JButton();
        catcontainer = new javax.swing.JPanel();

        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setMinimumSize(new java.awt.Dimension(550, 250));
        setPreferredSize(new java.awt.Dimension(550, 270));
        setLayout(new java.awt.BorderLayout());

        mainpanel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        mainpanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        mainpanel.setMinimumSize(new java.awt.Dimension(0, 250));
        mainpanel.setPreferredSize(new java.awt.Dimension(0, 250));
        mainpanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel1.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        jPanel1.setMinimumSize(new java.awt.Dimension(780, 260));
        jPanel1.setPreferredSize(new java.awt.Dimension(780, 200));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jSites.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("label.bystore"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N
        jSites.setPreferredSize(new java.awt.Dimension(178, 92));

        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText(AppLocal.getIntString("label.sitename")); // NOI18N

        m_jSites.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        org.jdesktop.layout.GroupLayout jSitesLayout = new org.jdesktop.layout.GroupLayout(jSites);
        jSites.setLayout(jSitesLayout);
        jSitesLayout.setHorizontalGroup(
            jSitesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSitesLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(jSitesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jSitesLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(m_jSites, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 185, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jSitesLayout.setVerticalGroup(
            jSitesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSitesLayout.createSequentialGroup()
                .add(jLabel15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 44, Short.MAX_VALUE))
            .add(jSitesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jSitesLayout.createSequentialGroup()
                    .add(33, 33, 33)
                    .add(m_jSites, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        org.jdesktop.layout.GroupLayout jSitesPanelLayout = new org.jdesktop.layout.GroupLayout(jSitesPanel);
        jSitesPanel.setLayout(jSitesPanelLayout);
        jSitesPanelLayout.setHorizontalGroup(
            jSitesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSitesPanelLayout.createSequentialGroup()
                .add(jSites, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 210, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );
        jSitesPanelLayout.setVerticalGroup(
            jSitesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSitesPanelLayout.createSequentialGroup()
                .add(jSites, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 107, Short.MAX_VALUE))
        );

        jPanel1.add(jSitesPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 210, 200));

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText(AppLocal.getIntString("label.stockdate")); // NOI18N
        jLabel1.setMaximumSize(new java.awt.Dimension(23, 20));
        jLabel1.setMinimumSize(new java.awt.Dimension(23, 20));
        jLabel1.setPreferredSize(new java.awt.Dimension(23, 20));
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 25));

        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText(AppLocal.getIntString("label.warehouse")); // NOI18N
        jLabel8.setMaximumSize(new java.awt.Dimension(40, 20));
        jLabel8.setMinimumSize(new java.awt.Dimension(40, 20));
        jLabel8.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, 80, 25));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText(AppLocal.getIntString("label.prodbarcode")); // NOI18N
        jLabel7.setMaximumSize(new java.awt.Dimension(40, 20));
        jLabel7.setMinimumSize(new java.awt.Dimension(40, 20));
        jLabel7.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 80, 25));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText(AppLocal.getIntString("label.stockproduct")); // NOI18N
        jLabel10.setMaximumSize(new java.awt.Dimension(40, 20));
        jLabel10.setMinimumSize(new java.awt.Dimension(40, 20));
        jLabel10.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 80, 30));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText(AppLocal.getIntString("label.units")); // NOI18N
        jLabel4.setMaximumSize(new java.awt.Dimension(40, 20));
        jLabel4.setMinimumSize(new java.awt.Dimension(40, 20));
        jLabel4.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 80, 25));

        m_junits.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_junits.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel2.add(m_junits, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, 70, 25));

        m_jreference.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jreference.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jreferenceActionPerformed(evt);
            }
        });
        jPanel2.add(m_jreference, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 115, 200, 25));

        m_jcodebar.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jcodebar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jcodebarActionPerformed(evt);
            }
        });
        jPanel2.add(m_jcodebar, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 80, 200, 25));

        jproduct.setEditable(false);
        jproduct.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jproduct.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jPanel2.add(jproduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 45, 200, 25));

        m_jdate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jdate.setMinimumSize(new java.awt.Dimension(40, 20));
        m_jdate.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel2.add(m_jdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 200, 25));

        m_jbtndate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/date.png"))); // NOI18N
        m_jbtndate.setToolTipText(bundle.getString("tiptext.opencalendar")); // NOI18N
        m_jbtndate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtndateActionPerformed(evt);
            }
        });
        jPanel2.add(m_jbtndate, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 3, 40, -1));

        m_FindProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/search24.png"))); // NOI18N
        m_FindProduct.setToolTipText(bundle.getString("tiptext.searchproductlist")); // NOI18N
        m_FindProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_FindProductActionPerformed(evt);
            }
        });
        jPanel2.add(m_FindProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 38, 40, -1));

        m_jEnter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/barcode.png"))); // NOI18N
        m_jEnter.setToolTipText(bundle.getString("tiptext.getbarcode")); // NOI18N
        m_jEnter.setFocusPainted(false);
        m_jEnter.setFocusable(false);
        m_jEnter.setMaximumSize(new java.awt.Dimension(54, 33));
        m_jEnter.setMinimumSize(new java.awt.Dimension(54, 33));
        m_jEnter.setPreferredSize(new java.awt.Dimension(54, 33));
        m_jEnter.setRequestFocusEnabled(false);
        m_jEnter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jEnterActionPerformed(evt);
            }
        });
        jPanel2.add(m_jEnter, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 73, 40, -1));

        m_EditProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/sale_editline.png"))); // NOI18N
        m_EditProduct.setToolTipText(bundle.getString("tiptext.enterproductid")); // NOI18N
        m_EditProduct.setFocusPainted(false);
        m_EditProduct.setFocusable(false);
        m_EditProduct.setMaximumSize(new java.awt.Dimension(64, 33));
        m_EditProduct.setMinimumSize(new java.awt.Dimension(64, 33));
        m_EditProduct.setPreferredSize(new java.awt.Dimension(64, 33));
        m_EditProduct.setRequestFocusEnabled(false);
        m_EditProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_EditProductActionPerformed(evt);
            }
        });
        jPanel2.add(m_EditProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 110, 40, -1));

        m_jprice.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jprice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jPanel2.add(m_jprice, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 150, 70, 25));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText(AppLocal.getIntString("label.price")); // NOI18N
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 150, 40, 25));

        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText(AppLocal.getIntString("label.attributes")); // NOI18N
        jLabel9.setMaximumSize(new java.awt.Dimension(48, 20));
        jLabel9.setMinimumSize(new java.awt.Dimension(48, 20));
        jLabel9.setPreferredSize(new java.awt.Dimension(48, 20));
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 170, 70, 25));

        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setText(AppLocal.getIntString("label.minimum")); // NOI18N
        jLabel12.setMaximumSize(new java.awt.Dimension(40, 20));
        jLabel12.setMinimumSize(new java.awt.Dimension(40, 20));
        jLabel12.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 140, 60, 20));

        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText(AppLocal.getIntString("label.prodpricebuy")); // NOI18N
        jLabel13.setMaximumSize(new java.awt.Dimension(40, 20));
        jLabel13.setMinimumSize(new java.awt.Dimension(40, 20));
        jLabel13.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 110, 60, 20));

        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText(AppLocal.getIntString("label.stockunits")); // NOI18N
        jLabel11.setMaximumSize(new java.awt.Dimension(40, 20));
        jLabel11.setMinimumSize(new java.awt.Dimension(40, 20));
        jLabel11.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 80, 60, 20));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Location");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 45, 70, 25));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(AppLocal.getIntString("label.stockreason")); // NOI18N
        jLabel2.setMaximumSize(new java.awt.Dimension(36, 20));
        jLabel2.setMinimumSize(new java.awt.Dimension(36, 20));
        jLabel2.setPreferredSize(new java.awt.Dimension(36, 20));
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 10, 70, 25));

        m_jreason.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jPanel2.add(m_jreason, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 10, 200, 25));

        m_jLocation.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jPanel2.add(m_jLocation, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 45, 200, 25));

        m_junitsinstock.setEditable(false);
        m_junitsinstock.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_junitsinstock.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_junitsinstock.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        m_junitsinstock.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        jPanel2.add(m_junitsinstock, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 80, 60, 20));

        m_jbuyprice.setEditable(false);
        m_jbuyprice.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jbuyprice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jbuyprice.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        m_jbuyprice.setDoubleBuffered(true);
        m_jbuyprice.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        jPanel2.add(m_jbuyprice, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 110, 60, 20));

        m_jminimum.setEditable(false);
        m_jminimum.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jminimum.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jminimum.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jPanel2.add(m_jminimum, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 140, 60, 25));

        jattributes.setEditable(false);
        jattributes.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel2.add(jattributes, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 170, 210, 25));

        m_jmaximum.setEditable(false);
        m_jmaximum.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jmaximum.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jmaximum.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jPanel2.add(m_jmaximum, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 140, 70, 25));

        m_jsellprice.setEditable(false);
        m_jsellprice.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        m_jsellprice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        m_jsellprice.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        m_jsellprice.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        jPanel2.add(m_jsellprice, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 110, 60, 20));

        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText(AppLocal.getIntString("label.prodpricesell")); // NOI18N
        jLabel14.setMaximumSize(new java.awt.Dimension(40, 20));
        jLabel14.setMinimumSize(new java.awt.Dimension(40, 20));
        jLabel14.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 110, 60, 20));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText(AppLocal.getIntString("label.maximum")); // NOI18N
        jLabel3.setMaximumSize(new java.awt.Dimension(40, 20));
        jLabel3.setMinimumSize(new java.awt.Dimension(40, 20));
        jLabel3.setPreferredSize(new java.awt.Dimension(40, 20));
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 140, 60, 20));

        jEditAttributes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/attributes.png"))); // NOI18N
        jEditAttributes.setToolTipText(bundle.getString("tiptext.productattributes")); // NOI18N
        jEditAttributes.setMaximumSize(new java.awt.Dimension(65, 33));
        jEditAttributes.setMinimumSize(new java.awt.Dimension(65, 33));
        jEditAttributes.setPreferredSize(new java.awt.Dimension(65, 33));
        jEditAttributes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEditAttributesActionPerformed(evt);
            }
        });
        jPanel2.add(jEditAttributes, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 160, 40, -1));

        org.jdesktop.layout.GroupLayout jProductPanelLayout = new org.jdesktop.layout.GroupLayout(jProductPanel);
        jProductPanel.setLayout(jProductPanelLayout);
        jProductPanelLayout.setHorizontalGroup(
            jProductPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
        );
        jProductPanelLayout.setVerticalGroup(
            jProductPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jProductPanelLayout.createSequentialGroup()
                .add(0, 0, Short.MAX_VALUE)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 200, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jProductPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 0, 720, 200));

        mainpanel.add(jPanel1, java.awt.BorderLayout.PAGE_START);
        mainpanel.add(catcontainer, java.awt.BorderLayout.CENTER);

        add(mainpanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void m_EditProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_EditProductActionPerformed

        editProduct();
    }//GEN-LAST:event_m_EditProductActionPerformed

    private void m_jreferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jreferenceActionPerformed

        assignProductByReference();

    }//GEN-LAST:event_m_jreferenceActionPerformed

    private void m_jcodebarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jcodebarActionPerformed

        assignProductByCode();

    }//GEN-LAST:event_m_jcodebarActionPerformed

    private void m_jEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jEnterActionPerformed

        assignProductByCode();

    }//GEN-LAST:event_m_jEnterActionPerformed

    private void jEditAttributesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEditAttributesActionPerformed

        if (productid == null) {
            // first select the product.
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.productnotselected"));
            msg.show(this);
        } else {
            try {
                JProductAttEdit attedit = JProductAttEdit.getAttributesEditor(this, m_App.getSession());
                attedit.editAttributes(attsetid, attsetinstid);
                attedit.setVisible(true);

                if (attedit.isOK()) {
                    // The user pressed OK
                    attsetinstid = attedit.getAttributeSetInst();
                    attsetinstdesc = attedit.getAttributeSetInstDescription();
                    jattributes.setText(attsetinstdesc);
                }
            } catch (BasicException ex) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindattributes"), ex);
                msg.show(this);
            }
        }
}//GEN-LAST:event_jEditAttributesActionPerformed

    private void m_jbtndateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtndateActionPerformed

        Date date;
        try {
            date = (Date) Formats.TIMESTAMP.parseValue(m_jdate.getText());
        } catch (BasicException e) {
            date = null;
        }
        date = JCalendarDialog.showCalendarTime(this, date);
        if (date != null) {
            m_jdate.setText(Formats.TIMESTAMP.formatValue(date));
        }

    }//GEN-LAST:event_m_jbtndateActionPerformed

    private void m_FindProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_FindProductActionPerformed
        assignProduct(JProductFinder.showMessage(this, m_dlSales, siteGuid));

}//GEN-LAST:event_m_FindProductActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel catcontainer;
    private javax.swing.JButton jEditAttributes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jProductPanel;
    private javax.swing.JPanel jSites;
    private javax.swing.JPanel jSitesPanel;
    private javax.swing.JTextField jattributes;
    private javax.swing.JTextField jproduct;
    private javax.swing.JButton m_EditProduct;
    private javax.swing.JButton m_FindProduct;
    private javax.swing.JButton m_jEnter;
    private javax.swing.JComboBox m_jLocation;
    public javax.swing.JComboBox m_jSites;
    private javax.swing.JButton m_jbtndate;
    private javax.swing.JTextField m_jbuyprice;
    private javax.swing.JTextField m_jcodebar;
    private javax.swing.JTextField m_jdate;
    private javax.swing.JTextField m_jmaximum;
    private javax.swing.JTextField m_jminimum;
    private javax.swing.JTextField m_jprice;
    private javax.swing.JComboBox m_jreason;
    private javax.swing.JTextField m_jreference;
    private javax.swing.JTextField m_jsellprice;
    private javax.swing.JTextField m_junits;
    private javax.swing.JTextField m_junitsinstock;
    private javax.swing.JPanel mainpanel;
    // End of variables declaration//GEN-END:variables

}
