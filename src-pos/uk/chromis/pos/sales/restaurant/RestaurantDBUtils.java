/*
**    Chromis POS  - The New Face of Open Source POS
**    Copyright (c)2015-2018
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
package uk.chromis.pos.sales.restaurant;

import java.util.logging.Level;
import java.util.logging.Logger;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SerializerReadBoolean;
import uk.chromis.data.loader.SerializerReadInteger;
import uk.chromis.data.loader.SerializerReadString;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.data.loader.SerializerWriteString;
import uk.chromis.data.loader.Session;
import uk.chromis.data.loader.SessionFactory;
import uk.chromis.data.loader.StaticSentence;
import uk.chromis.pos.forms.DataLogicSystem;

/**
 *
 * @author JDL
 */
public class RestaurantDBUtils {

    public final static int TRUE = 1;
    public final static int FALSE = 0;
    private final Session m_s;
    private Object m_result;

    /**
     *
     */
    protected DataLogicSystem dlSystem;

    /**
     *
     */
    public RestaurantDBUtils() {
        m_s = SessionFactory.getInstance().getSession();
    }

    /**
     *
     * @param newTable
     * @param ticketID
     */
    public void moveCustomer(String newTable, String ticketID) {
        String oldTable = getTableDetails(ticketID);
        if (countTicketIdInTable(ticketID) > 1) {
            setCustomerNameInTable(getCustomerNameInTable(oldTable), newTable);
            setWaiterNameInTable(getWaiterNameInTable(oldTable), newTable);
            setTicketIdInTable(ticketID, newTable);
// remove the data for the old table 
            oldTable = getTableMovedName(ticketID);
            if ((oldTable != null) && (oldTable != newTable)) {
                clearCustomerNameInTable(oldTable);
                clearWaiterNameInTable(oldTable);
                clearTicketIdInTable(oldTable);
                clearTableMovedFlag(oldTable);
            } else {
                oldTable = getTableMovedName(ticketID);
                clearTableMovedFlag(oldTable);
            }
        }
    }

    /**
     *
     * @param custName
     * @param tableName
     */
    public void setCustomerNameInTable(String custName, String tableName) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET CUSTOMER=? WHERE NAME=?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(custName, tableName);

        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param custName
     * @param tableID
     */
    public void setCustomerNameInTableById(String custName, String tableID) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET CUSTOMER=? WHERE ID=?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(custName, tableID);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param custName
     * @param ticketID
     */
    public void setCustomerNameInTableByTicketId(String custName, String ticketID) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET CUSTOMER=? WHERE TICKETID=?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(custName, ticketID);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param tableName
     * @return
     */
    public String getCustomerNameInTable(String tableName) {
        try {
            m_result = new PreparedSentence(m_s,
                    "SELECT CUSTOMER FROM PLACES WHERE NAME = ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(tableName);
            return (m_result == null ? "" : (String) m_result);
        } catch (BasicException e) {

        }
        return "";
    }

    /**
     *
     * @param tableId
     * @return
     */
    public String getCustomerNameInTableById(String tableId) {
        try {
            m_result = new PreparedSentence(m_s,
                    "SELECT CUSTOMER FROM PLACES WHERE ID = ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(tableId);
            return (m_result == null ? "" : (String) m_result);
        } catch (BasicException e) {

        }
        return "";
    }

    /**
     *
     * @param tableName
     */
    public void clearCustomerNameInTable(String tableName) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET CUSTOMER=null WHERE NAME=?", SerializerWriteString.INSTANCE).exec(tableName);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param tableID
     */
    public void clearCustomerNameInTableById(String tableID) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET CUSTOMER=null WHERE ID=?", SerializerWriteString.INSTANCE).exec(tableID);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param waiterName
     * @param tableName
     */
    public void setWaiterNameInTable(String waiterName, String tableName) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET WAITER=? WHERE NAME=?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(waiterName, tableName);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param waiterName
     * @param tableID
     */
    public void setWaiterNameInTableById(String waiterName, String tableID) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET WAITER=? WHERE ID=?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(waiterName, tableID);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param tableName
     * @return
     */
    public String getWaiterNameInTable(String tableName) {
        try {
            m_result = new PreparedSentence(m_s,
                    "SELECT WAITER FROM PLACES WHERE NAME = ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(tableName);
            return (m_result == null ? "" : (String) m_result);
        } catch (BasicException e) {

        }
        return "";
    }

    /**
     *
     * @param tableID
     * @return
     */
    public String getWaiterNameInTableById(String tableID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "SELECT WAITER FROM PLACES WHERE ID= ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(tableID);
            return (m_result == null ? "" : (String) m_result);
        } catch (BasicException e) {
        }
        return "";
    }

    /**
     *
     * @param tableName
     */
    public void clearWaiterNameInTable(String tableName) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET WAITER=null WHERE NAME=?", SerializerWriteString.INSTANCE).exec(tableName);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param tableID
     */
    public void clearWaiterNameInTableById(String tableID) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET WAITER=null WHERE ID=?", SerializerWriteString.INSTANCE).exec(tableID);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param tableID
     * @return
     */
    public String getTicketIdInTable(String tableID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "SELECT TICKETID FROM PLACES WHERE ID = ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(tableID);
            return (m_result == null ? "" : (String) m_result);
        } catch (BasicException e) {
        }
        return "";
    }

    /**
     *
     * @param ticketID
     * @param tableName
     */
    public void setTicketIdInTable(String ticketID, String tableName) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET TICKETID=? WHERE NAME=?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(ticketID, tableName);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param tableName
     */
    public void clearTicketIdInTable(String tableName) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET TICKETID=null WHERE NAME=?", SerializerWriteString.INSTANCE).exec(tableName);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param tableID
     */
    public void clearTicketIdInTableById(String tableID) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET TICKETID=null WHERE ID=?", SerializerWriteString.INSTANCE).exec(tableID);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public Integer countTicketIdInTable(String ticketID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "SELECT COUNT(*) AS RECORDCOUNT FROM PLACES WHERE TICKETID = ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadInteger.INSTANCE).find(ticketID);
            return (Integer) m_result;
        } catch (BasicException e) {

        }

        return 0;
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public String getTableDetails(String ticketID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "SELECT NAME FROM PLACES WHERE TICKETID = ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(ticketID);
            return (m_result == null ? "" : (String) m_result);
        } catch (BasicException e) {

        }
        return "";
    }

    /**
     *
     * @param tableID
     */
    public void setTableMovedFlag(String tableID) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET TABLEMOVED=" + TRUE + " WHERE ID=?", SerializerWriteString.INSTANCE).exec(tableID);
        } catch (BasicException e) {

        }
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public String getTableMovedName(String ticketID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "SELECT NAME FROM PLACES WHERE TICKETID = ? AND TABLEMOVED = " + TRUE,
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(ticketID);
            return (String) m_result;
        } catch (BasicException e) {
            Logger.getLogger(RestaurantDBUtils.class.getName()).log(Level.WARNING, null, e);
        }
        return null;
    }

    /**
     *
     * @param ticketID
     * @return
     */
    public Boolean getTableMovedFlag(String ticketID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "SELECT TABLEMOVED FROM PLACES WHERE TICKETID = ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadBoolean.INSTANCE).find(ticketID);
            return (m_result == null) ? false : (Boolean) m_result;
        } catch (BasicException e) {

        }
        return (false);
    }

    /**
     *
     * @param tableName
     */
    public void clearTableMovedFlag(String tableName) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET TABLEMOVED=" + FALSE + " WHERE NAME=?", SerializerWriteString.INSTANCE).exec(tableName);
        } catch (BasicException e) {

        }
    }

    public void clearTableLockByTicket(String ticketID) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET LOCKED = false, OPENEDBY = null WHERE TICKETID = ?", SerializerWriteString.INSTANCE).exec(ticketID);
        } catch (BasicException e) {
            System.out.println("clear lock error");
        }
    }

    public void clearTableLockByName(String tableName) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET LOCKED = false, OPENEDBY = null WHERE NAME = ?", SerializerWriteString.INSTANCE).exec(tableName);
        } catch (BasicException e) {
            System.out.println("clear lock error");
        }
    }

    public void setTableLock(String tableID, String user) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET LOCKED = true, OPENEDBY = ? WHERE TICKETID = ?", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(tableID, user);
        } catch (BasicException e) {
            System.out.println("clear lock error");
        }
    }

    public void clearTableLock(String tableID) {
        try {
            new StaticSentence(m_s, "UPDATE PLACES SET LOCKED = false, OPENEDBY = null WHERE ID = ?", SerializerWriteString.INSTANCE).exec(tableID);
        } catch (BasicException e) {
            System.out.println("clear lock error");
        }
    }

    public Boolean getTableLock(String tableID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "SELECT LOCKED FROM PLACES WHERE ID= ?",
                    SerializerWriteString.INSTANCE,
                    SerializerReadInteger.INSTANCE).find(tableID);
            return ((Integer) m_result == 1);
        } catch (BasicException e) {

        }
        return (false);
    }

    public String getTableOpenedBy(String tableID) {
        try {
            m_result = new PreparedSentence(m_s,
                    "SELECT OPENEDBY FROM PLACES WHERE ID = ? ",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(tableID);
            return (String) m_result;
        } catch (BasicException e) {

        }
        return null;

    }

}
