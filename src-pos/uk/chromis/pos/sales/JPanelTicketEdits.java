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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import uk.chromis.basic.BasicException;
import uk.chromis.pos.ticket.ProductInfoExt;

/**
 *
 *
 */
public class JPanelTicketEdits extends JPanelTicket {

    private JTicketCatalogLines m_catandlines;

    /** Creates a new instance of JPanelTicketRefunds */
    public JPanelTicketEdits() {
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
    @Override
    public void activate() throws BasicException {
        super.activate();
        m_catandlines.loadCatalog();
    }

    @Override
    public void reLoadCatalog(){      
    }

    /**
     *
     */
    public void showCatalog() {
        m_jbtnconfig.setVisible(true);
        m_catandlines.showCatalog();
    }

    /**
     *
     * @param aRefundLines
     */
    public void showRefundLines(List aRefundLines) {
        // anado las lineas de refund
        // m_reflines.setLines(aRefundLines);
        m_jbtnconfig.setVisible(false);
        m_catandlines.showRefundLines(aRefundLines);
    }

    /**
     *
     * @return
     */
    @Override
    protected JTicketsBag getJTicketsBag() {
        return new JTicketsBagTicket(m_App, this);
    }

    /**
     *
     * @return
     */
    @Override
    protected Component getSouthComponent() {

        m_catandlines = new JTicketCatalogLines(m_App, this,
                "true".equals(m_jbtnconfig.getProperty("pricevisible")),
                "true".equals(m_jbtnconfig.getProperty("taxesincluded")),
                Integer.parseInt(m_jbtnconfig.getProperty("img-width", "64")),
                Integer.parseInt(m_jbtnconfig.getProperty("img-height", "54")));
        m_catandlines.setPreferredSize(new Dimension(
                0,
                Integer.parseInt(m_jbtnconfig.getProperty("cat-height", "245"))));
        m_catandlines.addActionListener(new CatalogListener());
        return m_catandlines;
    }

    /**
     *
     */
    @Override
    protected void resetSouthComponent() {
    }

    private class CatalogListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            buttonTransition((ProductInfoExt) e.getSource());
        }
    }

}
