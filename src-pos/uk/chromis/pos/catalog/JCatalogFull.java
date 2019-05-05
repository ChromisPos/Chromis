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

package uk.chromis.pos.catalog;

import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.JMessageDialog;
import uk.chromis.data.gui.MessageInf;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.sales.TaxesLogic;
import uk.chromis.pos.ticket.ProductInfoExt;
import uk.chromis.pos.ticket.TaxInfo;
import uk.chromis.pos.util.ThumbNailBuilder;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.sync.DataLogicSync;
import uk.chromis.pos.util.SessionFactory;

/**
 *
 * @author adrianromero
 */
public class JCatalogFull extends JPanel implements ListSelectionListener, CatalogSelector {

    protected EventListenerList listeners = new EventListenerList();
    private DataLogicSales m_dlSales;
    private TaxesLogic taxeslogic;
    private boolean pricevisible;
    private boolean taxesincluded;
    private final Map<String, ProductInfoExt> m_productsset = new HashMap<>();
    private final Set<String> m_categoriesset = new HashSet<>();
    private ThumbNailBuilder tnbbutton;
    private Object newColour;
    private DataLogicSync dlSync;
    private String siteGuid;

    public JCatalogFull(DataLogicSales dlSales, String siteGuid) {      
        this(dlSales, false, false, 64, 54, siteGuid);        
    }

    public JCatalogFull(DataLogicSales dlSales, boolean pricevisible, boolean taxesincluded, int width, int height, String siteGuid) {      
        this.siteGuid = siteGuid;
        m_dlSales = dlSales;
        this.pricevisible = pricevisible;
        this.taxesincluded = taxesincluded;
        initComponents();
        tnbbutton = new ThumbNailBuilder(width, height, "uk/chromis/images/package.png");

        dlSync = new DataLogicSync();
        dlSync.init(SessionFactory.getInstance().getSession());

    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void showCatalogPanel(String id) {
    }

    @Override
    public void loadCatalog(String siteGuid) throws BasicException {        
        m_jProducts.removeAll();
        m_productsset.clear();
        m_categoriesset.clear();

        // Load the taxes logic
        taxeslogic = new TaxesLogic(m_dlSales.getTaxList(siteGuid).list());

        buildProductPanel();
    }

    @Override
    public void setComponentEnabled(boolean value) {

        m_jProducts.setEnabled(value);
        synchronized (m_jProducts.getTreeLock()) {
            int compCount = m_jProducts.getComponentCount();
            for (int i = 0; i < compCount; i++) {
                m_jProducts.getComponent(i).setEnabled(value);
            }
        }
        this.setEnabled(value);
    }

    @Override
    public void addActionListener(ActionListener l) {
        listeners.add(ActionListener.class, l);
    }

    @Override
    public void removeActionListener(ActionListener l) {
        listeners.remove(ActionListener.class, l);
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
    }

    protected void fireSelectedProduct(ProductInfoExt prod) {
        EventListener[] l = listeners.getListeners(ActionListener.class);
        ActionEvent e = null;
        for (EventListener l1 : l) {
            if (e == null) {
                e = new ActionEvent(prod, ActionEvent.ACTION_PERFORMED, prod.getID());
            }
            ((ActionListener) l1).actionPerformed(e);
        }
    }

    private void buildProductPanel() {       
        try {
            JCatalogTab jcurrTab = new JCatalogTab();
            m_jProducts.add(jcurrTab, "");

            java.util.List< ProductInfoExt> prods;

            if ((AppConfig.getInstance().getBoolean("sales.newscreen"))) {
                prods = m_dlSales.getAllProductCatalogByCatOrder(siteGuid);
            } else {
                prods = m_dlSales.getAllProductCatalog(siteGuid);
            }

            for (ProductInfoExt prod : prods) {
                newColour = m_dlSales.getCategoryColour(prod.getCategoryID(),dlSync.getSiteGuid().toString());
                String sColour = (String) newColour;
                if (sColour == null) {
                    sColour = "";
                }
                jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(prod.getImage(), getProductLabel(prod))), new SelectedAction(prod), prod.getTextTip(), sColour);
            }
        } catch (BasicException e) {
            JMessageDialog.showMessage(this, new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.notactive"), e));
        }
    }

    private String getProductLabel(ProductInfoExt product) {
        if (pricevisible) {
            if (taxesincluded) {
                TaxInfo tax = taxeslogic.getTaxInfo(product.getTaxCategoryID());
                return "<html><center>" + product.getName() + "<br>" + product.printPriceSellTax(tax);
            } else {
                return "<html><center>" + product.getDisplay() + "<br>" + product.printPriceSell();
            }
        } else {
            return product.getDisplay();
        }
    }

    private void showProductPanel(String id) {
        ProductInfoExt product = m_productsset.get(id);
        if (product == null) {
            if (m_productsset.containsKey(id)) {
            } else {
                try {
                    java.util.List<ProductInfoExt> products = m_dlSales.getProductComments(id, siteGuid);

                    if (products.isEmpty()) {
                        m_productsset.put(id, null);
                    } else {
                        product = m_dlSales.getProductInfo(id, siteGuid);
                        m_productsset.put(id, product);

                        JCatalogTab jcurrTab = new JCatalogTab();
                        jcurrTab.applyComponentOrientation(getComponentOrientation());
                        m_jProducts.add(jcurrTab, "PRODUCT." + id);

                        // Add products
                        for (ProductInfoExt prod : products) {
                            jcurrTab.addButton(new ImageIcon(tnbbutton.getThumbNailText(prod.getImage(), getProductLabel(prod))), new SelectedAction(prod), prod.getTextTip(), "");
                        }
                        CardLayout cl = (CardLayout) (m_jProducts.getLayout());
                        cl.show(m_jProducts, "PRODUCT." + id);
                    }
                } catch (BasicException eb) {
                    m_productsset.put(id, null);
                }
            }
        } else {
            CardLayout cl = (CardLayout) (m_jProducts.getLayout());
            cl.show(m_jProducts, "PRODUCT." + id);
        }
    }

    private class SelectedAction implements ActionListener {

        private final ProductInfoExt prod;

        public SelectedAction(ProductInfoExt prod) {
            this.prod = prod;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireSelectedProduct(prod);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m_jProducts = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        m_jProducts.setLayout(new java.awt.CardLayout());
        add(m_jProducts, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel m_jProducts;
    // End of variables declaration//GEN-END:variables

}
