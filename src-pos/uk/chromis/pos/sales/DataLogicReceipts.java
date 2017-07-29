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

import java.util.List;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SerializerReadBasic;
import uk.chromis.data.loader.SerializerReadClass;
import uk.chromis.data.loader.SerializerReadString;
import uk.chromis.data.loader.SerializerWriteBasicExt;
import uk.chromis.data.loader.SerializerWriteString;
import uk.chromis.data.loader.Session;
import uk.chromis.data.loader.StaticSentence;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.BeanFactoryDataSingle;
import uk.chromis.pos.ticket.TicketInfo;

/**
 *
 * @author adrianromero
 */
public class DataLogicReceipts extends BeanFactoryDataSingle {

    private Session s;

    /**
     * Creates a new instance of DataLogicReceipts
     */
    public DataLogicReceipts() {
    }

    /**
     *
     * @param s
     */
    @Override
    public void init(Session s) {
        this.s = s;
    }

    /**
     *
     * @param Id
     * @return
     * @throws BasicException
     */
    public final TicketInfo getSharedTicket(String Id) throws BasicException {

        if (Id == null) {
            return null;
        } else {
            Object[] record = (Object[]) new StaticSentence(s, "SELECT CONTENT FROM SHAREDTICKETS WHERE ID = ? ", SerializerWriteString.INSTANCE, new SerializerReadBasic(new Datas[]{Datas.SERIALIZABLE})).find(Id);
            return record == null ? null : (TicketInfo) record[0];
        }
    }

    /**
     *
     * @return @throws BasicException
     */
    public final List<SharedTicketInfo> getSharedTicketList() throws BasicException {

        return (List<SharedTicketInfo>) new StaticSentence(s //                , "SELECT ID, NAME, CONTENT PICKUPID FROM SHAREDTICKETS ORDER BY ID"                
                , "SELECT ID, NAME, CONTENT, PICKUPID FROM SHAREDTICKETS ORDER BY ID", null, new SerializerReadClass(SharedTicketInfo.class)).list();
    }

    /**
     * Get shared ticket list for current logged in user only.
     *
     * @return @throws BasicException
     */
    public final List<SharedTicketInfo> getSharedTicketListByUser(String User) throws BasicException {

        return (List<SharedTicketInfo>) new StaticSentence(s //                , "SELECT ID, NAME, CONTENT PICKUPID FROM SHAREDTICKETS ORDER BY ID"                
                , "SELECT ID, NAME, CONTENT, PICKUPID FROM SHAREDTICKETS WHERE NAME LIKE '%" + User + " -%' ", null, new SerializerReadClass(SharedTicketInfo.class)).list();
    }

    /**
     *
     * @param id
     * @param ticket
     * @param pickupid
     * @throws BasicException
     */
    public final void updateSharedTicket(final String id, final TicketInfo ticket, int pickupid) throws BasicException {

        Object[] values = new Object[]{
            id,
            ticket.getName(),
            ticket,
            pickupid
        };
        Datas[] datas = new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.SERIALIZABLE,
            Datas.INT
        };
        new PreparedSentence(s, "UPDATE SHAREDTICKETS SET "
                + "NAME = ?, "
                + "CONTENT = ?, "
                + "PICKUPID = ? "
                + "WHERE ID = ?", new SerializerWriteBasicExt(datas, new int[]{1, 2, 3, 0})).exec(values);
    }

    /**
     *
     * @param id
     * @param ticket
     * @param pickupid
     * @throws BasicException
     */
    public final void insertSharedTicket(final String id, final TicketInfo ticket, int pickupid) throws BasicException {

        Object[] values = new Object[]{
            id,
            ticket.getName(),
            ticket,
            pickupid,
            ticket.printUser()
        };
        Datas[] datas;
        datas = new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.SERIALIZABLE,
            Datas.INT,
            Datas.STRING
        };

        new PreparedSentence(s, "INSERT INTO SHAREDTICKETS ("
                + "ID, "
                + "NAME, "
                + "CONTENT, "
                + "PICKUPID, "
                + "APPUSER) "
                + "VALUES (?, ?, ?, ?, ?)", new SerializerWriteBasicExt(datas, new int[]{0, 1, 2, 3, 4})).exec(values);
    }

    public final void insertSharedTicketUsingPickUpID(final String id, final TicketInfo ticket, int pickupid) throws BasicException {
        Object[] values = new Object[]{
            id,
            "Pickup Id: " + getPickupString(pickupid),
            ticket,
            pickupid,
            ticket.getUser().getName()
        };
        Datas[] datas;
        datas = new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.SERIALIZABLE,
            Datas.INT,
            Datas.STRING
        };

        new PreparedSentence(s, "INSERT INTO SHAREDTICKETS ("
                + "ID, "
                + "NAME, "
                + "CONTENT, "
                + "PICKUPID, "
                + "APPUSER) "
                + "VALUES (?, ?, ?, ?, ?)", new SerializerWriteBasicExt(datas, new int[]{0, 1, 2, 3, 4})).exec(values);
    }

    /**
     *
     * @param id
     * @throws BasicException
     */
    public final void deleteSharedTicket(final String id) throws BasicException {
        new StaticSentence(s, "DELETE FROM SHAREDTICKETS WHERE ID = ?", SerializerWriteString.INSTANCE).exec(id);
    }

    /**
     *
     * @param Id
     * @return
     * @throws BasicException
     */
    public final Integer getPickupId(String Id) throws BasicException {

        if (Id == null) {
            return null;
        } else {
            Object[] record = (Object[]) new StaticSentence(s, "SELECT PICKUPID FROM SHAREDTICKETS WHERE ID = ?", SerializerWriteString.INSTANCE, new SerializerReadBasic(new Datas[]{Datas.INT})).find(Id);
            return record == null ? 0 : (Integer) record[0];
        }
    }

    public final String getAppUser(String Id) throws BasicException {
        if (Id == null) {
            return null;
        } else {
            Object[] record = (Object[]) new StaticSentence(s, "SELECT APPUSER FROM SHAREDTICKETS WHERE ID = ? ", SerializerWriteString.INSTANCE, new SerializerReadBasic(new Datas[]{Datas.STRING})).find(Id);
            return record == null ? "" : (String) record[0];
        }
    }

    public final String getTicketName(String Id) throws BasicException {
        if (Id == null) {
            return null;
        } else {
            Object[] record = (Object[]) new StaticSentence(s, "SELECT NAME FROM SHAREDTICKETS WHERE ID = ?", SerializerWriteString.INSTANCE, new SerializerReadBasic(new Datas[]{Datas.STRING})).find(Id);
            return record == null ? "" : (String) record[0];
        }
    }

    private String getPickupString(int pickup) {
        String tmpPickupId = Integer.toString(pickup);
        String pickupSize = (AppConfig.getInstance().getProperty("till.pickupsize"));
        if (pickupSize != null && (Integer.parseInt(pickupSize) >= tmpPickupId.length())) {
            while (tmpPickupId.length() < (Integer.parseInt(pickupSize))) {
                tmpPickupId = "0" + tmpPickupId;
            }
        }
        return (tmpPickupId);
    }

}
