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

import uk.chromis.pos.panels.ComboItemLocal;

/**
 *
 * @author adrianromero
 */
public class MovementReason extends ComboItemLocal {

    // The numeric key indicates whether this is an increase or decrease in stock level
    // It is important to get the sign right.
    public static final MovementReason IN_PURCHASE = new MovementReason(+1, "stock.in.purchase");
    public static final MovementReason IN_REFUND = new MovementReason(+2, "stock.in.refund");
    public static final MovementReason IN_MOVEMENT = new MovementReason(+4, "stock.in.movement");
    public static final MovementReason IN_OPEN_PACK = new MovementReason(+5, "stock.in.openpack");
    public static final MovementReason IN_STOCKCHANGE = new MovementReason(+7, "stock.in.stockchange");
    public static final MovementReason IN_RECIPES = new MovementReason(+8, "stock.in.recipes");

    public static final MovementReason OUT_SALE = new MovementReason(-1, "stock.out.sale");
    public static final MovementReason OUT_REFUND = new MovementReason(-2, "stock.out.refund");
    public static final MovementReason OUT_BREAK = new MovementReason(-3, "stock.out.break");
    public static final MovementReason OUT_MOVEMENT = new MovementReason(-4, "stock.out.movement");
    public static final MovementReason OUT_OPEN_PACK = new MovementReason(-5, "stock.out.openpack");
    public static final MovementReason OUT_STOCKCHANGE = new MovementReason(-7, "stock.out.stockchange");
    public static final MovementReason OUT_RECIPES = new MovementReason(-8, "stock.out.recipes");
    /**
     *
     */
    public static final MovementReason OUT_CROSSING = new MovementReason(1000, "stock.out.crossing");

    private MovementReason(Integer iKey, String sKeyValue) {
        super(iKey, sKeyValue);
    }

    /**
     *
     * @return
     */
    public boolean isInput() {
        return m_iKey > 0;
    }

    /**
     *
     * @param d
     * @return
     */
    public Double samesignum(Double d) {

        if (d == null || m_iKey == null) {
            return d;
        } else if ((m_iKey > 0 && d < 0.0)
                || (m_iKey < 0 && d > 0.0)) {
            return -d;
        } else {
            return d;
        }
    }

    /**
     *
     * @param dBuyPrice
     * @param dSellPrice
     * @return
     */
    public Double getPrice(Double dBuyPrice, Double dSellPrice) {

        if (this == IN_PURCHASE || this == OUT_REFUND || this == OUT_BREAK
                || this == IN_OPEN_PACK || this == OUT_OPEN_PACK || this == OUT_BREAK
                || this == IN_STOCKCHANGE || this == OUT_STOCKCHANGE) {
            return dBuyPrice;
        } else if (this == OUT_SALE || this == IN_REFUND) {
            return dSellPrice;
        } else {
            return null;
        }
    }
}
