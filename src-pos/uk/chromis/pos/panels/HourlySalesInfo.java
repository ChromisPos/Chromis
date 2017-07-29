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
package uk.chromis.pos.panels;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.DataWrite;
import uk.chromis.data.loader.SerializerRead;

public class HourlySalesInfo {

    private String hourRange;
    private Double hourTotal;
    private Integer sales;
    private Date salesDate;
    private String hourStr;

    public HourlySalesInfo(Date salesDate, String hourRate, Double hourTotal, Integer sales, String hourStr) {
        this.salesDate = salesDate;
        this.hourRange = hourRate;
        this.hourTotal = hourTotal;
        this.sales = sales;
        this.hourStr = hourStr;
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                Date salesDate = dr.getTimestamp(1);
                String hourRange = dr.getString(2);
                Double hourTotal = dr.getDouble(3);
                Integer sales = dr.getInt(4);

                StringBuilder sb = new StringBuilder();
                sb.append((hourRange.length() == 1) ? "0" + hourRange : hourRange);
                sb.append(":00-");
                sb.append((hourRange.length() == 1) ? "0" + hourRange : hourRange);
                sb.append(":59");

                return new HourlySalesInfo(salesDate, hourRange, hourTotal, sales, sb.toString());
            }
        };
    }

    public void writeValues(DataWrite dp) throws BasicException {
        dp.setTimestamp(1, salesDate);
        dp.setString(2, hourRange);
        dp.setDouble(3, hourTotal);
        dp.setInt(4, sales);
    }

    public String getHourRange() {
        return hourRange;
    }

    public String getHourTotal() {
        BigDecimal bd = new BigDecimal(hourTotal);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.toString();
    }

    public Integer getSales() {
        return sales;
    }

    public String getSalesDate() {
        SimpleDateFormat sdfr = new SimpleDateFormat("dd-mm-yyyy");
        return sdfr.format(salesDate);
    }

    public String getHourStr() {
        return hourStr;
    }

}
