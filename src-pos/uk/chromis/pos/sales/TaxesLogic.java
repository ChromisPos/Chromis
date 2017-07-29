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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.chromis.pos.customers.CustomerInfoExt;
import uk.chromis.pos.inventory.TaxCategoryInfo;
import uk.chromis.pos.ticket.TaxInfo;
import uk.chromis.pos.ticket.TicketInfo;
import uk.chromis.pos.ticket.TicketLineInfo;
import uk.chromis.pos.ticket.TicketTaxInfo;

/**
 *
 * @author adrianromero
 */
public class TaxesLogic {
    
    private List<TaxInfo> taxlist;
    
    private Map<String, TaxesLogicElement> taxtrees;
    
    /**
     *
     * @param taxlist
     */
    public TaxesLogic(List<TaxInfo> taxlist) {
        this.taxlist = taxlist;
      

        taxtrees = new HashMap<>();
                
        List<TaxInfo> taxlistordered = new ArrayList<>();
        taxlistordered.addAll(taxlist);
        Collections.sort(taxlistordered, new Comparator<TaxInfo>() {
            @Override
            public int compare(TaxInfo o1, TaxInfo o2) {
                if (o1.getApplicationOrder() < o2.getApplicationOrder()) {
                    return -1;
                } else if (o1.getApplicationOrder() == o2.getApplicationOrder()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        
        // Generate the taxtrees
        HashMap<String, TaxesLogicElement> taxorphans = new HashMap<>();
        
        for (TaxInfo t : taxlistordered) {
                       
            TaxesLogicElement te = new TaxesLogicElement(t);
            
            // get the parent
            TaxesLogicElement teparent = taxtrees.get(t.getParentID());
            if (teparent == null) {
                // orphan node
                teparent = taxorphans.get(t.getParentID());
                if (teparent == null) {
                    teparent = new TaxesLogicElement(null);
                    taxorphans.put(t.getParentID(), teparent);
                } 
            } 
            
            teparent.getSons().add(te);

            // Does it have orphans ?
            teparent = taxorphans.get(t.getId());
            if (teparent != null) {
                // get all the sons
                te.getSons().addAll(teparent.getSons());
                // remove the orphans
                taxorphans.remove(t.getId());
            }          
            
            // Add it to the tree...
            taxtrees.put(t.getId(), te);
        }
    }
    
    /**
     *
     * @param ticket
     * @throws TaxesException
     */
    public void calculateTaxes(TicketInfo ticket) throws TaxesException {
  
        List<TicketTaxInfo> tickettaxes = new ArrayList<>(); 
        
        for (TicketLineInfo line: ticket.getLines()) {
            tickettaxes = sumLineTaxes(tickettaxes, calculateTaxes(line));
        }
        
        ticket.setTaxes(tickettaxes);
    }
    
    /**
     *
     * @param line
     * @return
     * @throws TaxesException
     */
    public List<TicketTaxInfo> calculateTaxes(TicketLineInfo line) throws TaxesException {
        
        TaxesLogicElement taxesapplied = getTaxesApplied(line.getTaxInfo());
        return calculateLineTaxes(line.getSubValue(), taxesapplied);
    }
    
    private List<TicketTaxInfo> calculateLineTaxes(double base, TaxesLogicElement taxesapplied) {
 
        List<TicketTaxInfo> linetaxes = new ArrayList<>();
        
        if (taxesapplied.getSons().isEmpty()) {           
            TicketTaxInfo tickettax = new TicketTaxInfo(taxesapplied.getTax());
            tickettax.add(base);
            linetaxes.add(tickettax);
        } else {
            double acum = base;
            
            for (TaxesLogicElement te : taxesapplied.getSons()) {
                
                List<TicketTaxInfo> sublinetaxes = calculateLineTaxes(
                        te.getTax().isCascade() ? acum : base, 
                        te);
                linetaxes.addAll(sublinetaxes);
                acum += sumTaxes(sublinetaxes);
            }
        }
        
        return linetaxes;       
    }
    
    private TaxesLogicElement getTaxesApplied(TaxInfo t) throws TaxesException {
        
        if (t == null) {
            throw new TaxesException(new java.lang.NullPointerException());
        }
        
        return taxtrees.get(t.getId());
    }
        
    private double sumTaxes(List<TicketTaxInfo> linetaxes) {
        
        double taxtotal = 0.0;
        
        for (TicketTaxInfo tickettax : linetaxes) {
            taxtotal += tickettax.getTax();
            
        }
        return  taxtotal;
    }
    
    private List<TicketTaxInfo> sumLineTaxes(List<TicketTaxInfo> list1, List<TicketTaxInfo> list2) {
     
        for (TicketTaxInfo tickettax : list2) {
            TicketTaxInfo i = searchTicketTax(list1, tickettax.getTaxInfo().getId());
            if (i == null) {
                list1.add(tickettax);
            } else {
                i.add(tickettax.getSubTotal());
            }
        }
        return list1;
    }
    
    private TicketTaxInfo searchTicketTax(List<TicketTaxInfo> l, String id) {
        
        for (TicketTaxInfo tickettax : l) {
            if (id.equals(tickettax.getTaxInfo().getId())) {
                return tickettax;
            }
        }    
        return null;
    }
    
    /**
     *
     * @param tcid
     * @return
     */
    public double getTaxRate(String tcid) {
        return getTaxRate(tcid, null);
    }
    
    /**
     *
     * @param tc
     * @return
     */
    public double getTaxRate(TaxCategoryInfo tc) {
        return getTaxRate(tc, null);
    }
    
    /**
     *
     * @param tc
     * @param customer
     * @return
     */
    public double getTaxRate(TaxCategoryInfo tc, CustomerInfoExt customer) {
        
        if (tc == null) {
            return 0.0;
        } else {
            return getTaxRate(tc.getID(), customer);          
        }
    }
    
    /**
     *
     * @param tcid
     * @param customer
     * @return
     */
    public double getTaxRate(String tcid, CustomerInfoExt customer) {
        
        if (tcid == null) {
            return 0.0;
        } else {
            TaxInfo tax = getTaxInfo(tcid, customer);
            if (tax == null) {
                return 0.0;
            } else {
                return tax.getRate();
            }            
        }
    }
    
    /**
     *
     * @param tcid
     * @return
     */
    public TaxInfo getTaxInfo(String tcid) {
        return getTaxInfo(tcid, null);
    }
    
    /**
     *
     * @param tc
     * @return
     */
    public TaxInfo getTaxInfo(TaxCategoryInfo tc) {
        return getTaxInfo(tc.getID(), null);
    }
    
    /**
     *
     * @param tc
     * @param customer
     * @return
     */
    public TaxInfo getTaxInfo(TaxCategoryInfo tc, CustomerInfoExt customer) {  
        return getTaxInfo(tc.getID(), customer);
    }

    /**
     *
     * @param tcid
     * @param customer
     * @return
     */
    public TaxInfo getTaxInfo(String tcid, CustomerInfoExt customer) {
        
        
        TaxInfo defaulttax = null;
        
        for (TaxInfo tax : taxlist) {
            if (tax.getParentID() == null && tax.getTaxCategoryID().equals(tcid)) {
                if ((customer == null || customer.getTaxCustCategoryID() == null) && tax.getTaxCustCategoryID() == null) {
                    return tax;
                } else if (customer != null && customer.getTaxCustCategoryID() != null && customer.getTaxCustCategoryID().equals(tax.getTaxCustCategoryID())) {
                    return tax;
                }
                
                if (tax.getTaxCustCategoryID() == null) {
                    defaulttax = tax;
                }
            }
        }
        
        // No tax found
        return defaulttax;
    }
}
