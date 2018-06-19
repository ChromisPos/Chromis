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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Session;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.scripting.ScriptEngine;
import uk.chromis.pos.scripting.ScriptException;
import uk.chromis.pos.scripting.ScriptFactory;
import uk.chromis.pos.ticket.TaxInfo;
import uk.chromis.pos.ticket.TicketInfo;
import uk.chromis.pos.ticket.TicketLineInfo;

public class PromotionSupport {

    protected Session m_session;
    protected DataLogicSales m_salesLogic;
    protected DataLogicPromotions m_DataLogicPromotions;
    HashMap< String, PromotionInfo> m_promotionCache;
    List<String> m_AllProductPromotions;
    private Component m_Parent;

    public PromotionSupport(Component parent,
            DataLogicSales salesLogic,
            DataLogicPromotions logicPromotions) {
        m_Parent = parent;
        m_salesLogic = salesLogic;
        m_DataLogicPromotions = logicPromotions;
        m_promotionCache = new HashMap();
        m_AllProductPromotions = null;
    }

    public void clearPromotionCache() {
        m_promotionCache.clear();
        m_AllProductPromotions = null;
    }

    // Get promotions that are enabled and operate on all products
    private List<String> getAllProductPromotionsIds() {

        if (m_AllProductPromotions == null) {
            try {
                m_AllProductPromotions = new ArrayList<String>();

                List<PromotionInfo> promos = m_DataLogicPromotions.getAllProductPromotions();

                for (PromotionInfo p : promos) {
                    // Add to the cache and store the ID
                    m_AllProductPromotions.add(p.getID());
                    m_promotionCache.putIfAbsent(p.getID(), p);
                }
            } catch (BasicException ex) {
                Logger.getLogger(PromotionSupport.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return m_AllProductPromotions;
    }

    private PromotionInfo getCachedPromotion(String id) {

        PromotionInfo promotion = m_promotionCache.get(id);

        if (promotion == null) {
            try {
                promotion = m_DataLogicPromotions.getPromotionInfo(id);
            } catch (BasicException ex) {
                Logger.getLogger(PromotionSupport.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (promotion != null) {
                m_promotionCache.put(id, promotion);
            }
        }

        return promotion;
    }

    // Check to see if any promotion scripts need to run
    // Supported events: 
    //  Event                  
    //  promotion.addline  
    //  promotion.changeline 
    //  promotion.removeline
    //
    public Boolean checkPromotions(String event,
            Boolean bwithActions,
            TicketInfo ticket,
            int selectedindex,
            int effectedIndex,
            String productID) throws ScriptException {

        if (ticket == null || event == null) {
            return false;
        }

        Logger.getLogger(PromotionSupport.class.getName()).log(Level.INFO,
                "checkPromotions for event: {0}", event);

        HashMap< String, PromotionInfo> promotions = new HashMap();

        // Get product specific promotions for this ticket
        for (TicketLineInfo line : ticket.getLines()) {
            String id = line.getPromotionId();
            if (id != null) {
                // Only add if not already in the list
                if (promotions.get(id) == null) {
                    // Promotion on this product so need to run the promotion script
                    PromotionInfo p = getCachedPromotion(id);
                    if (p != null && p.getIsEnabled()) {
                        promotions.putIfAbsent(id, p);
                    }
                }
            }
        }

        // Now add promotions that are run for all products
        List<String> ids = getAllProductPromotionsIds();
        for (String id : ids) {
            PromotionInfo p = getCachedPromotion(id);
            if (p != null && p.getIsEnabled()) {
                promotions.putIfAbsent(id, p);
            }
        }

        if (!(promotions.isEmpty()) && bwithActions) {
            // Promotion scripts need running
            for (String promotionID : promotions.keySet()) {
                evalScript(promotions.get(promotionID), event,
                        ticket, selectedindex, effectedIndex, productID);
            }
        }

        return !(promotions.isEmpty());
    }

    public Object evalScript(PromotionInfo promotion, String event,
            TicketInfo ticket, int selectedindex,
            int effectedIndex, String productID) throws ScriptException {

        Logger.getLogger(PromotionSupport.class.getName()).log(Level.INFO, "Running script for: {0}", promotion.getName());

        ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.BEANSHELL);

        // These are the variables available inside the script
        script.put("ticket", ticket);
        script.put("dlsales", m_salesLogic);
        script.put("dlpromotions", m_DataLogicPromotions);
        script.put("support", this);
        script.put("promotion", promotion);
        script.put("event", event);
        script.put("selectedindex", selectedindex);
        script.put("effectedindex", effectedIndex);
        script.put("productid", productID);

        return script.eval(promotion.getScript());
    }

    // Script support functions
    // Display a message as a popup dialog
    public void ShowMessage(String title, String message) {

        // Get details of the original font before we change it otherwise all dialogboxes will use new settings
        JOptionPane pane = new JOptionPane();
        Font originalFont = pane.getFont();

        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("ARIAL", Font.PLAIN, 20)));
        JLabel FontText = new JLabel(message);

        JOptionPane newpane = new JOptionPane();
        newpane.setMessage(FontText);

        Dialog dlg = newpane.createDialog(title);
        dlg.setVisible(true);

        // Return to default settings
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font(originalFont.getName(), originalFont.getStyle(), originalFont.getSize())));
    }

    // Count products in this promotion currently on the ticket
    public int CountProductsInPromotion(PromotionInfo promotion,
            TicketInfo ticket,
            boolean bIncludePromotionAdded) {
        int count = 0;

        for (TicketLineInfo line : ticket.getLines()) {
            if (bIncludePromotionAdded || line.isPromotionAdded() == false) {
                String id = line.getPromotionId();
                if (id != null && id.contentEquals(promotion.getID())) {
                    ++count;
                }
            }
        }
        return count;
    }

    // Calculate the quantity of products in this promotion currently on the ticket
    public Double CountProductsInPromotionQty(PromotionInfo promotion,
            TicketInfo ticket,
            boolean bIncludePromotionAdded) {
        Double count = 0.0;

        for (TicketLineInfo line : ticket.getLines()) {
            if (bIncludePromotionAdded || line.isPromotionAdded() == false) {
                String id = line.getPromotionId();
                if (id != null && id.contentEquals(promotion.getID())) {
                    count += line.getMultiply();
                }
            }
        }
        return count;
    }

    public class LineList implements Comparable<LineList> {

        private int m_index;
        TicketLineInfo m_line;

        public LineList(int index, TicketLineInfo line) {
            m_index = index;
            m_line = line;
        }

        public int getIndex() {
            return m_index;
        }

        public void setIndex(int idx) {
            m_index = idx;
        }

        public TicketLineInfo getLine() {
            return m_line;
        }

        public Double getPrice() {
            return m_line.getPrice();
        }

        public boolean equals(Object o) {
            if (!(o instanceof LineList)) {
                return false;
            }
            LineList l = (LineList) o;
            return l.m_index == m_index;
        }

        public int hashCode() {
            return m_index;
        }

        public String toString() {
            return "Index: " + m_index + " Line: " + m_line;
        }

        // Compare on price
        public int compareTo(LineList l) {
            if (l.getPrice() == getPrice()) {
                return 0;
            }

            int diff = (int) intPart(l.getPrice() - getPrice());

            if (l.getPrice() < getPrice()) {
                return diff + 1;
            }

            return diff - 1;
        }
    }

    // Find products in this promotion currently on the ticket
    // returns an array of indexes into ticket.getLines()
    public List<LineList> FindProductsInPromotion(PromotionInfo promotion,
            TicketInfo ticket,
            boolean bIncludePromotionAdded) {
        List<LineList> aIndexes = new ArrayList<LineList>();

        for (int i = 0; i < ticket.getLinesCount(); ++i) {
            TicketLineInfo line = ticket.getLines().get(i);
            if (bIncludePromotionAdded || line.isPromotionAdded() == false) {
                if (promotion.getAllProducts()) {
                    aIndexes.add(new LineList(i, line));
                } else {
                    String id = line.getPromotionId();
                    if (id != null && id.contentEquals(promotion.getID())) {
                        aIndexes.add(new LineList(i, line));
                    }
                }
            }
        }

        return aIndexes;
    }

    // Find all instances of the given product currently on the ticket
    // returns an array of indexes into ticket.getLines()
    public List<LineList> FindProductsInTicket(String promotionid, String productID,
            TicketInfo ticket,
            boolean bIncludePromotionAdded) {
        List<LineList> aIndexes = new ArrayList<LineList>();

        for (int i = 0; i < ticket.getLinesCount(); ++i) {
            TicketLineInfo line = ticket.getLines().get(i);
            if (bIncludePromotionAdded || line.isPromotionAdded() == false) {
                String id = line.getProductID();
                if (id != null && id.contentEquals(productID)) {
                    String promoid = line.getPromotionId();
                    if (promoid != null && promoid.contentEquals(promotionid)) {
                        aIndexes.add(new LineList(i, line));
                    }
                }
            }
        }

        return aIndexes;
    }

    // Discount all products in this ticket having the given promotion ID
    public void DiscountProducts(TicketInfo ticket,
            String promotionid,
            String sDiscountMessage, Double minQuantity,
            Double discountrate, Boolean bWithReset) {

        if (bWithReset) {
            for (int i = 0; i < ticket.getLines().size(); ++i) {
                TicketLineInfo line = ticket.getLine(i);
                String id = line.getPromotionId();
                if (id != null) {
                    if (id.contentEquals(promotionid)) {
                        RemovePromotionAddedLine(ticket, i + 1);
                        line.setDiscounted("no");
                    }
                }
            }
        }

        // Check quantity to ensure enough products on the ticket to qualify
        PromotionInfo p = getCachedPromotion(promotionid);
        if (minQuantity > CountProductsInPromotionQty(p, ticket, false)) {
            // Insufficient products - remove any discounts
            RemoveDiscountPromotion(ticket, promotionid);
        } else {

            for (int i = 0; i < ticket.getLines().size(); ++i) {
                TicketLineInfo line = ticket.getLine(i);
                String id = line.getPromotionId();
                if (id != null) {
                    if (id.contentEquals(promotionid)) {
                        DiscountProductPercent(ticket, i, sDiscountMessage, discountrate);
                    }
                }
            }
        }
    }

    private static String sYes = new String("yes");

    // Discount a product by adding a line below the product
    public void DiscountProductPercent(TicketInfo ticket, int lineIndex,
            String sDiscountMessage, Double discountrate) {

        TicketLineInfo productline = ticket.getLine(lineIndex);
        if (productline.isPromotionAdded() == false
                && sYes.contentEquals(productline.getDiscounted()) == false) {

            double discount = (intPart(productline.getPrice() * discountrate)
                    / -100d);

            TicketLineInfo discountline = new TicketLineInfo(
                    productline.getProductID(),
                    sDiscountMessage,
                    productline.getProductTaxCategoryID(),
                    productline.getMultiply(),
                    discount,
                    productline.getTaxInfo());
            discountline.setPromotionAdded(true);
            discountline.setProperty("product.promotionid", productline.getPromotionId());

            ticket.insertLine(lineIndex + 1, discountline);
            productline.setDiscounted("yes");
        }
    }

    // Discount a product by adding a line below the product
    // If fixedPrice is used to calculate a discount based on the original
    // price and qty is set to 1.
    // If not, the qty and discount rate are applied
    public void DiscountProductQty(TicketInfo ticket, int lineIndex,
            String sDiscountMessage, Double qty,
            Double discountrate ) {

        TicketLineInfo productline = ticket.getLine(lineIndex);
        if (productline.isPromotionAdded() == false
                && sYes.contentEquals(productline.getDiscounted()) == false) {

            Double setPrice;
            Double setQty;

            setPrice = productline.getPrice() * (discountrate / 100d) * -1;
            setQty = qty;

            TicketLineInfo discountline = new TicketLineInfo(
                    productline.getProductID(),
                    sDiscountMessage,
                    productline.getProductTaxCategoryID(),
                    setQty,
                    setPrice,
                    productline.getTaxInfo());
            discountline.setPromotionAdded(true);
            discountline.setProperty("product.promotionid", productline.getPromotionId());

            ticket.insertLine(lineIndex + 1, discountline);
            productline.setDiscounted("yes");
        }
    }

    // Discount a group of products by adding a single discount 
    // to the end of the ticket.
    public void DiscountProductGroup(TicketInfo ticket, PromotionInfo promotion,
            String sDiscountMessage,
            Double qty, Double price, TaxInfo tax) {

        String tcID = null;
        if (tax != null) {
            tcID = tax.getTaxCategoryID();
        }

        TicketLineInfo discountline = new TicketLineInfo(
                null,
                sDiscountMessage,
                tcID,
                qty, price, tax);

        discountline.setPromotionAdded(true);
        discountline.setProperty("product.promotionid", promotion.getID());

        ticket.addLine(discountline);
    }

    // Remove discount on a product
    // Pass in the id product, all promotion discount lines for that
    // product anywhere on the ticket will be removed
    public void RemoveDiscountProduct(TicketInfo ticket, String productID) {

        if (productID == null) {
            return;
        }

        // Start looking from the end of the ticket to cope with re-indexing 
        // after a line delete
        int index = ticket.getLines().size() - 1;
        while (index >= 0) {

            TicketLineInfo line = ticket.getLine(index);
            if (!line.isPromotionAdded() && productID.contentEquals(line.getProductID())) {
                RemovePromotionAddedLine(ticket, index + 1);
                line.setDiscounted("no");
            }
            --index;
        }
    }

    // Remove a coupon for a promotion
    // Pass in the id of the promotion, all promotion coupon lines for that
    // promotion anywhere on the ticket will be removed
    // If promotionID is null, ALL coupons in the ticket are removed
    public void RemoveCouponPromotion(TicketInfo ticket, String promotionID) {
        ticket.removeCoupon(promotionID);
    }

    // Remove discount on a promotion
    // Pass in the id of the promotion, all promotion discount lines for that
    // promotion anywhere on the ticket will be removed
    public void RemoveDiscountPromotion(TicketInfo ticket, String promotionID) {

        if (promotionID == null) {
            return;
        }

        // Start looking from the end of the ticket to cope with re-indexing 
        // after a line delete
        int index = ticket.getLines().size() - 1;
        while (index >= 0) {

            TicketLineInfo line = ticket.getLine(index);
            String p = line.getPromotionId();
            if (p != null && promotionID.contentEquals(p)) {
                if (line.isPromotionAdded()) {
                    RemovePromotionAddedLine(ticket, index);
                } else {
                    line.setDiscounted("no");
                }
            }
            --index;
        }
    }

    // Remove a line added by a promotion on the ticket line at the given index
    // Pass in the index of the line, subsequent promotion lines
    // will also be removed
    public void RemovePromotionAddedLine(TicketInfo ticket, int lineIndex) {

        int index = lineIndex;
        TicketLineInfo productline = null;

        if (index < ticket.getLines().size()) {
            productline = ticket.getLine(index);
        }

        while (productline != null && productline.isPromotionAdded()) {
            ticket.removeLine(index);
            if (index < ticket.getLines().size()) {
                productline = ticket.getLine(index);
            } else {
                productline = null;
            }
        }

    }

    // See if the indexed line is in the given promotion
    public Boolean isProductInPromotion(TicketInfo ticket,
            int index, PromotionInfo promotion) {

        if (promotion == null) {
            return false;
        }

        if (promotion.getAllProducts() == true) {
            return true;
        }

        TicketLineInfo productline = ticket.getLine(index);
        if (productline != null && promotion != null) {
            String id = productline.getPromotionId();
            if (id != null && promotion.getID().contentEquals(id)) {
                return true;
            }
        }
        return false;
    }

    double intPart(double d) {
        int i = (int) d;
        return (double) i;
    }

    // Add discounts to the ticket for the products in aLine
    // The discounts are added as sufficient products are found while
    // going through the list. If you want cheapest products discounted first,
    // ensure you call Collections.sort(aLines) first
    public void addDiscounts(TicketInfo ticket,
            List<LineList> aLines,
            String sDiscountMessage,
            Double qtyBuy, Double qtyToDiscount,
            Double discountrate,
            Boolean bWholeUnitsOnly) {

        // Count the number of items in the ticket
        Double qty = 0.0;
        for (LineList l : aLines) {
            TicketLineInfo productline = ticket.getLine(l.getIndex());
            qty += productline.getMultiply();
        }
        if (bWholeUnitsOnly) {
            qty = intPart(qty);
        }

        // Calculate the total number of free items
        Double qtyFree = qty / qtyBuy;
                if (bWholeUnitsOnly) {
            qtyFree = intPart(qtyFree);
        }
        qtyFree = qtyFree * qtyToDiscount;

        if (qtyFree > 0 && discountrate > 0.0 ) {
            // Step through discounting items until qtyFree is reached
            Double totalDiscount = 0.0;
            Double qtyFound = 0.0;
            for (int i = 0; i < aLines.size(); ++i ) {
                LineList l = aLines.get(i);
                int itemIndex = l.getIndex();
                Double itemqty = ticket.getLine(itemIndex).getMultiply();

                qtyFound += itemqty;
                    
                Double qtyDiscount = itemqty;
                if (bWholeUnitsOnly) {
                    qtyDiscount = intPart(qtyDiscount);
                }
                if (totalDiscount + qtyDiscount > qtyFree) {
                    qtyDiscount = qtyFree - totalDiscount;
                }

                DiscountProductQty(ticket, itemIndex, sDiscountMessage, qtyDiscount, 100d );
                
                // A new line is added to the ticket so re-index ones after
                for( int j = 0; j< aLines.size(); ++j ) {
                    int index = aLines.get(j).getIndex();
                    if(index > itemIndex) {
                        aLines.get(j).setIndex( index+1 );
                    }
                }

                totalDiscount += qtyDiscount;
                qtyFound += qtyDiscount;

                if (totalDiscount >= qtyFree) {
                    break;
                }
            }
        }
    }

        // Add discounts to the ticket for the products in aLine
    // The discounts are added as sufficient products are found while
    // going through the list. If you want cheapest products discounted first,
    // ensure you call Collections.sort(aLines) first
    public void addDiscountFixedPrice(TicketInfo ticket,
            PromotionInfo promotion,
            List<LineList> aLines,
            String sDiscountMessage,
            Double qtyBuy,
            Double fixedPrice,
            Boolean bWholeUnitsOnly) {

        // Count the number of items in the ticket
        Double qty = 0.0;
        for (LineList l : aLines) {
            TicketLineInfo productline = ticket.getLine(l.getIndex());
            qty += productline.getMultiply();
        }
        if (bWholeUnitsOnly) {
            qty = intPart(qty);
        }

        // Calculate the total number of free items
        Double qtyFree = 0d;
        while( qtyFree + qtyBuy <= qty ){
            qtyFree = qtyFree + qtyBuy;
        }
        
        if (bWholeUnitsOnly) {
            qtyFree = intPart(qtyFree);
        }

        // Tax is chargable at the highest item tax rate so need to
        // keep track of that.
        TaxInfo tax = null;
        
        if (qtyFree > 0) {
            // Step through discounting items until qtyFree is reached
            Double totalDiscount = 0.0;
            Double qtyFound = 0.0;
            for (int i = 0; i < aLines.size(); ++i ) {
                LineList l = aLines.get(i);
                int itemIndex = l.getIndex();
                Double itemqty = ticket.getLine(itemIndex).getMultiply();

                qtyFound += itemqty;
                    
                Double qtyDiscount = itemqty;
                if (bWholeUnitsOnly) {
                    qtyDiscount = intPart(qtyDiscount);
                }
                if (totalDiscount + qtyDiscount > qtyFree) {
                    qtyDiscount = qtyFree - totalDiscount;
                }

                DiscountProductQty(ticket, itemIndex, sDiscountMessage, qtyDiscount, 100d );
                
                // A new line is added to the ticket so re-index ones after
                for( int j = 0; j< aLines.size(); ++j ) {
                    int index = aLines.get(j).getIndex();
                    if(index > itemIndex) {
                        aLines.get(j).setIndex( index+1 );
                    }
                }

                totalDiscount += qtyDiscount;
                qtyFound += qtyDiscount;

                TicketLineInfo info = ticket.getLine(itemIndex);
                // Save the highest tax rate - we must charge that rate
                // on the final price (UK tax laws)
                if (tax == null
                        || tax.getRate() < info.getTaxInfo().getRate()) {
                    tax = info.getTaxInfo();
                }

                if (totalDiscount >= qtyFree) {
                    break;
                }
            }
            
            // We have now discounted all products involved the deal to zero,
            // Now add a line to the end of the ticket for the
            // actual price, use the highest tax rate we found
            fixedPrice = fixedPrice / (1+tax.getRate());
            
            DiscountProductGroup(ticket, promotion, sDiscountMessage,
                    qtyFree/qtyBuy, fixedPrice, tax);
        }
    }

    // For every forEvery products you buy, you get qtyDiscounted products discounted
    // This version is for when any product in the promotion can be used in the
    // calculation, i.e. mix and match from a range.
    // bWholeUnitsOnly should be true to calculate the discounts on whole units
    // only or false to calculate the discounts on partial products such as weighed
    // items.
    public void UpdateMutibuyGroup(
            TicketInfo ticket,
            PromotionInfo promotion,
            String sDiscountMessage, Double forEvery, Double qtyDiscounted,
            Double discountrate,
            Boolean bWholeUnitsOnly) {

        // Remove existing discount
        RemoveDiscountPromotion(ticket, promotion.getID());

        // Get list of products in this promotion
        List<LineList> aLines = FindProductsInPromotion(promotion, ticket, false);

        if (aLines.size() > 0) {
            // Convert to a price ordered list so cheapest items discounted first
            Collections.sort(aLines);

            addDiscounts(ticket, aLines, sDiscountMessage, forEvery, qtyDiscounted,
                    discountrate, bWholeUnitsOnly);
        }
    }

    // For every forEvery products you buy, you get qtyDiscounted products discounted
    // This version is for when all products must be the same.
    // bWholeUnitsOnly should be true to calculate the discounts on whole units
    // only or false to calculate the discounts on partial products such as weighed
    // items.
    public void UpdateMutibuySingleProduct(
            TicketInfo ticket, String productID,
            PromotionInfo promotion,
            String sDiscountMessage, Double forEvery, Double qtyDiscounted,
            Double discountrate,
            Boolean bWholeUnitsOnly) {
        // Remove existing discount
        RemoveDiscountProduct(ticket, productID);

        // Get list of products in this ticket
        List<LineList> aLines = FindProductsInTicket( promotion.getID(), productID, ticket, false);

        if (aLines.size() > 0) {
            addDiscounts(ticket, aLines, sDiscountMessage, forEvery, qtyDiscounted,
                    discountrate, bWholeUnitsOnly);
        }
    }

    // For every qtyBuy products you buy, you get qtySomeFree products free
    // This version is for when all products must be the same.
    // bWholeUnitsOnly should be true to calculate the discounts on whole units
    // only or false to calculate the discounts on partial products such as weighed
    // items.
    public void UpdateBuySomeGetSomeFreeSingleProduct(
            TicketInfo ticket, String productID,
            PromotionInfo promotion,
            String sDiscountMessage,
            Double qtyBuy, Double qtySomeFree,
            Boolean bWholeUnitsOnly) {
        // Remove existing discount
        RemoveDiscountProduct(ticket, productID);

        // Get list of products in this ticket
        List<LineList> aLines = FindProductsInTicket( promotion.getID(), productID, ticket, false);

        if (aLines.size() > 0) {
            addDiscounts(ticket, aLines, sDiscountMessage, qtyBuy, qtySomeFree,
                    100d, bWholeUnitsOnly);
        }
    }

    // For every qtyBuy products you buy, you get qtySomeFree products free
    // This version is for when any product in the promotion can be used in the
    // calculation, i.e. mix and match from a range.
    // bWholeUnitsOnly should be true to calculate the discounts on whole units
    // only or false to calculate the discounts on partial products such as weighed
    // items.
    public void UpdateBuySomeGetSomeGroup(
            TicketInfo ticket,
            PromotionInfo promotion,
            String sDiscountMessage,
            Double qtyBuy, Double qtySomeFree,
            Boolean bWholeUnitsOnly) {
        // Remove existing discount
        RemoveDiscountPromotion(ticket, promotion.getID());

        // Get list of products in this promotion
        List<LineList> aLines = FindProductsInPromotion(promotion, ticket, false);

        if (aLines.size() > 0) {

            // Convert to a price ordered list so cheapest items discounted first
            Collections.sort(aLines);

            addDiscounts(ticket, aLines, sDiscountMessage, qtyBuy, qtySomeFree,
                    100d, bWholeUnitsOnly);
        }
    }

    // Buy qtyBuy products at the given price
    // This version is for when all products must be the same.
    // bWholeUnitsOnly should be true to calculate the discounts on whole units
    // only or false to calculate the discounts on partial products such as weighed
    // items.
    public void UpdateFixedPriceSingleProduct(
            TicketInfo ticket, String productID,
            PromotionInfo promotion,
            String sDiscountMessage,
            Double qtyBuy, Double price,
            Boolean bWholeUnitsOnly) {
        // Remove existing discount
        RemoveDiscountProduct(ticket, productID);

        // Get list of products in this ticket
        List<LineList> aLines = FindProductsInTicket( promotion.getID(), productID, ticket, false);

        if (aLines.size() > 0) {
            addDiscountFixedPrice(ticket, promotion, aLines, sDiscountMessage, qtyBuy,
                    price, bWholeUnitsOnly);
        }
    }

    // Buy qtyBuy products at the given price
    // This version is for when any product in the promotion can be used in the
    // calculation, i.e. mix and match from a range.
    // bWholeUnitsOnly should be true to calculate the discounts on whole units
    // only or false to calculate the discounts on partial products such as weighed
    // items.
    public void UpdateFixedPriceGroup(
            TicketInfo ticket,
            PromotionInfo promotion,
            String sDiscountMessage,
            Double qtyBuy, Double price,
            Boolean bWholeUnitsOnly) {
        // Remove existing discount
        RemoveDiscountPromotion(ticket, promotion.getID());

        // Get list of products in this promotion
        List<LineList> aLines = FindProductsInPromotion(promotion, ticket, false);

        if (aLines.size() > 0) {

            // Convert to a price ordered list so cheapest items discounted first
            Collections.sort(aLines);

            // Give discount on products qualifying
            addDiscountFixedPrice(ticket, promotion, aLines, sDiscountMessage, qtyBuy,
                    price, bWholeUnitsOnly);
        }
    }

    // Buy two or more different products at a fixed price
    // An array or properties is passed in, at least one product with each
    // property must be in the ticket to qualify. i.e
    //  List<String> properties = new String [] = {"meal.snack", "meal.drink", "meal.sandwich"};
    //
    //
    public void UpdateMealDeal(
            TicketInfo ticket,
            PromotionInfo promotion,
            String sDiscountMessage,
            List<String> properties,
            String productKeyName,
            Double price) {
        // Remove existing discount
        RemoveDiscountPromotion(ticket, promotion.getID());

        // Get list of products in this promotion
        List<LineList> aLines = FindProductsInPromotion(promotion, ticket, false);

        // Lowest price items first
        Collections.sort(aLines);

        if (aLines.size() > 0) {

            // Count how many products of each type in the ticket
            Double[] count = new Double[properties.size()];

            for (int i = 0; i < properties.size(); ++i) {
                count[i] = 0d;
            }

            // Go through products counting how many of each type
            for (LineList l : aLines) {
                // Get the product property and compare it with the ones we are looking for
                String prop = l.getLine().getProperty(productKeyName);
                if (prop != null) {
                    for (int j = 0; j < properties.size(); ++j) {
                        if (prop.contentEquals(properties.get(j))) {
                            // Found a match - count it
                            count[j] += l.getLine().getMultiply();
                        }
                    }
                }
            }

            // Number of discounts to give is the smallest number of the product
            // type counters
            Double smallest = -1d;
            for (int j = 0; j < properties.size(); ++j) {
                if (smallest == -1 || count[j] < smallest) {
                    smallest = intPart(count[j]);
                }
            }

            if (smallest > 0) {
                // So now discount the correct number of each type of product
                // starting at the highest value items
                for (int j = 0; j < properties.size(); ++j) {
                    count[j] = smallest;
                }

                // Tax is chargable at the highest item tax rate so need to
                // keep track of that.
                TaxInfo tax = null;

                int itemIndex = 0;

                // Go through products discounting each type in turn
                for (int j = 0; j < properties.size(); ++j) {
                    itemIndex = aLines.size() - 1; // Start at the end (highest value)
                    while (itemIndex >= 0 && count[j] > 0) {
                        int ticketIndex = aLines.get(itemIndex).getIndex();
                        TicketLineInfo info = ticket.getLine(ticketIndex);
                        String prop = info.getProperty(productKeyName);

                        if (!info.isPromotionAdded() && prop != null) {

                            if (prop.contentEquals(properties.get(j))) {

                                // Discount this item
                                Double qty = count[j];
                                if (qty > info.getMultiply()) {
                                    qty = info.getMultiply();
                                }
                                count[j] -= qty;

                                DiscountProductQty(ticket, ticketIndex, sDiscountMessage, qty,
                                        100d );

                                // ticketindexes need adjusting to accomodate the new line
                                for (int l = 0; l < aLines.size(); ++l) {
                                    if (aLines.get(l).getIndex() > ticketIndex) {
                                        aLines.get(l).setIndex(aLines.get(l).getIndex() + 1);
                                    }
                                }

                                // Save the highest tax rate - we must charge that rate
                                // on the final price (UK tax laws)
                                if (tax == null
                                        || tax.getRate() < info.getTaxInfo().getRate()) {
                                    tax = info.getTaxInfo();
                                }
                            }
                        }
                        --itemIndex;
                    }
                }

                // We have now discounted all products involved the deal to zero,
                // Now add a line to the end of the ticket for the meal deal's
                // actual price, use the highest tax rate we found
                DiscountProductGroup(ticket, promotion, sDiscountMessage,
                        smallest, price, tax);
            }
        }
    }

    public void AddCoupon(
            TicketInfo ticket,
            PromotionInfo promotion,
            List<String> couponLines) {
        int line = 0;

        for (String text : couponLines) {
            ticket.addCouponLine(promotion.getID(), line++, text);
        }
    }

    // To debug scripts in a proper debugging environment,
    // cut and paste the entire script into the function below, removing
    // any existing function code first, then edit your script in the
    // database using the resource editor to change DEBUGMODE to true
    // The script will now divert to the function below.
    //  Note that DEBUGMODE needs to be set to false in the function below.
    //
    // When you have it all working, you can cut and paste your code back into
    // the script. Make sure you remove the code from the function below and
    // that DEBUGMODE is false in the resource script.
    public void testcode(PromotionInfo promotion, String event,
            TicketInfo ticket, int selectedindex,
            DataLogicSales dlsales,
            DataLogicPromotions dlpromotions,
            PromotionSupport support,
            int effectedindex, String productid) {

    }
}

