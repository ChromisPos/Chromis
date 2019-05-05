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

package uk.chromis.pos.epm;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.QBFBuilder;
import uk.chromis.data.loader.SentenceExec;
import uk.chromis.data.loader.SentenceFind;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerRead;
import uk.chromis.data.loader.SerializerReadDate;
import uk.chromis.data.loader.SerializerReadString;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.data.loader.SerializerWriteString;
import uk.chromis.data.loader.Session;
import uk.chromis.data.loader.StaticSentence;
import uk.chromis.data.loader.TableDefinition;
import uk.chromis.pos.forms.BeanFactoryDataSingle;

/**
 *
 * @author Ali Safdar & Aneeqa Baber
 */
public class DataLogicPresenceManagement extends BeanFactoryDataSingle {

    /**
     *
     */
    protected Session s;

    private SentenceExec m_checkin;
    private SentenceExec m_checkout;
    private SentenceFind m_checkdate;

    private SentenceList m_breaksvisible;
    private SentenceExec m_startbreak;
    private SentenceExec m_endbreak;

    private SentenceFind m_isonbreak;
    private SentenceFind m_isonleave;
    private SentenceFind m_shiftid;

    private SentenceFind m_lastcheckin;
    private SentenceFind m_lastcheckout;
    private SentenceFind m_startbreaktime;

    private SentenceFind m_lastbreakid;
    private SentenceFind m_breakname;
    
    private SerializerRead breakread;
    private TableDefinition tbreaks;
    private TableDefinition tleaves;

    /**
     *
     */
    public DataLogicPresenceManagement() {
    }
    
    /**
     *
     * @param s
     */
    @Override
    public void init(Session s){

        this.s = s;
        breakread = new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new Break(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getString(3),
                        dr.getBoolean(4));
            }
        };

        m_breaksvisible = new StaticSentence(s
            , "SELECT ID, NAME, NOTES, VISIBLE FROM BREAKS WHERE VISIBLE = " + s.DB.TRUE() + " AND SITEGUID = ? "
            , SerializerWriteString.INSTANCE
            , breakread);

        m_checkin =  new PreparedSentence(s
                , "INSERT INTO SHIFTS(ID, STARTSHIFT, PPLID) VALUES (?, ?, ?)"
                , new SerializerWriteBasic(new Datas[] {Datas.STRING, Datas.TIMESTAMP, Datas.STRING}));

        m_checkout = new StaticSentence(s
                , "UPDATE SHIFTS SET ENDSHIFT = ?, STARTSHIFT = STARTSHIFT WHERE ENDSHIFT IS NULL AND PPLID = ?"
                ,new SerializerWriteBasic(new Datas[] {Datas.TIMESTAMP, Datas.STRING}));

        m_checkdate = new StaticSentence(s
            , "SELECT COUNT(*) FROM SHIFTS WHERE ENDSHIFT IS NULL AND PPLID = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadString.INSTANCE);

        m_startbreak =  new PreparedSentence(s
                , "INSERT INTO SHIFT_BREAKS(ID, SHIFTID, BREAKID, STARTTIME) VALUES (?, ?, ?, ?)"
                , new SerializerWriteBasic(new Datas[] {Datas.STRING, Datas.STRING, Datas.STRING, Datas.TIMESTAMP}));

        m_endbreak = new StaticSentence(s
                , "UPDATE SHIFT_BREAKS SET ENDTIME = ?, STARTTIME = STARTTIME WHERE ENDTIME IS NULL AND SHIFTID = ?"
                ,new SerializerWriteBasic(new Datas[] {Datas.TIMESTAMP, Datas.STRING}));

        m_isonbreak = new StaticSentence(s
//            , "SELECT COUNT(*) FROM SHIFT_BREAKS WHERE ENDTIME IS NULL AND SHIFTID = ?"
            , "SELECT COUNT(*) FROM SHIFT_BREAKS WHERE ENDTIME IS NULL"                
            , SerializerWriteString.INSTANCE
            , SerializerReadString.INSTANCE);

        m_shiftid = new StaticSentence(s
            , "SELECT ID FROM SHIFTS WHERE ENDSHIFT IS NULL AND PPLID = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadString.INSTANCE);
        
        m_isonleave = new StaticSentence(s
            , "SELECT COUNT(*) FROM LEAVES WHERE STARTDATE < ? AND ENDDATE > ? AND PPLID = ?"
            , new SerializerWriteBasic(new Datas[] {Datas.TIMESTAMP, Datas.TIMESTAMP, Datas.STRING})
            , SerializerReadString.INSTANCE);

        m_lastcheckin = new StaticSentence(s
            , "SELECT STARTSHIFT FROM SHIFTS WHERE ENDSHIFT IS NULL AND PPLID = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadDate.INSTANCE);

        m_lastcheckout = new StaticSentence(s
            , "SELECT MAX(ENDSHIFT) FROM SHIFTS WHERE PPLID = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadDate.INSTANCE);

        m_startbreaktime = new StaticSentence(s
            , "SELECT STARTTIME FROM SHIFT_BREAKS WHERE ENDTIME IS NULL AND SHIFTID = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadDate.INSTANCE);

        m_lastbreakid = new StaticSentence(s
            , "SELECT BREAKID FROM SHIFT_BREAKS WHERE ENDTIME IS NULL AND SHIFTID = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadString.INSTANCE);

        m_breakname = new StaticSentence(s
            , "SELECT NAME FROM BREAKS WHERE ID = ?"
            , SerializerWriteString.INSTANCE
            , SerializerReadString.INSTANCE);
    }

    /**
     *
     * @return
     */
    public final SentenceList getBreaksList() {
        return new StaticSentence(s
            , "SELECT ID, NAME FROM BREAKS ORDER BY NAME"
            , null
            , new SerializerRead() {@Override
 public Object readValues(DataRead dr) throws BasicException {
                return new BreaksInfo(dr.getString(1), dr.getString(2));
            }});
    }

    /**
     *
     * @return
     */
    public final SentenceList getLeavesList() {
        return new StaticSentence(s
            , "SELECT ID, PPLID, NAME, STARTDATE, ENDDATE, NOTES FROM LEAVES ORDER BY NAME"
            , null
            , new SerializerRead() {@Override
 public Object readValues(DataRead dr) throws BasicException {
                return new LeavesInfo(dr.getString(1), dr.getString(2), dr.getString(3), dr.getString(4), dr.getString(5), dr.getString(6));
            }});
    }

    /**
     *
     * @return
     * @throws BasicException
     */
    public final List listBreaksVisible(String siteGuid)throws BasicException {
        return m_breaksvisible.list(siteGuid);
    }      

    /**
     *
     * @param user
     * @throws BasicException
     */
    public final void CheckIn(String user) throws BasicException {
        Object[] value = new Object[] {UUID.randomUUID().toString(), new Date(), user};
        m_checkin.exec(value);
    }

    /**
     *
     * @param user
     * @throws BasicException
     */
    public final void CheckOut(String user) throws BasicException {
        Object[] value = new Object[] {new Date(), user};
        m_checkout.exec(value);
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final boolean IsCheckedIn(String user) throws BasicException {
        String Data = (String) m_checkdate.find(user);
        // "0" rows shows user is not checked in
        if (Data.equals("0")) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param UserID
     * @param BreakID
     * @throws BasicException
     */
    public final void StartBreak(String UserID,  String BreakID) throws BasicException {
        String ShiftID = GetShiftID(UserID);
        Object[] value = new Object[] {UUID.randomUUID().toString(), ShiftID, BreakID, new Date()};
        m_startbreak.exec(value);
    }

    /**
     *
     * @param UserID
     * @throws BasicException
     */
    public final void EndBreak(String UserID) throws BasicException {
        String ShiftID = GetShiftID(UserID);
        Object[] value = new Object[] {new Date(), ShiftID};
        m_endbreak.exec(value);
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final boolean IsOnBreak(String user) throws BasicException {
        String ShiftID = GetShiftID(user);
        String Data = (String) m_isonbreak.find(ShiftID);
        // "0" rows shows user is not on break
        if (Data.equals("0")) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final String GetShiftID(String user) throws BasicException {
        return (String) m_shiftid.find(user);
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final Date GetLastCheckIn(String user) throws BasicException {
        return (Date) m_lastcheckin.find(user);
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final Date GetLastCheckOut(String user) throws BasicException {
        return (Date) m_lastcheckout.find(user);
    }

    /**
     *
     * @param ShiftID
     * @return
     * @throws BasicException
     */
    public final Date GetStartBreakTime(String ShiftID) throws BasicException {
        return (Date) m_startbreaktime.find(ShiftID);
    }

    /**
     *
     * @param ShiftID
     * @return
     * @throws BasicException
     */
    public final String GetLastBreakID(String ShiftID) throws BasicException {
        return (String) m_lastbreakid.find(ShiftID);
    }

    /**
     *
     * @param ShiftID
     * @return
     * @throws BasicException
     */
    public final String GetLastBreakName(String ShiftID) throws BasicException {
        String BreakID = GetLastBreakID(ShiftID);
        return (String) m_breakname.find(BreakID);
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final Object [] GetLastBreak(String user) throws BasicException {
        String ShiftID = GetShiftID(user);
        Date StartBreakTime = GetStartBreakTime(ShiftID);
        String BreakName = GetLastBreakName(ShiftID);
        return new Object[] {BreakName, StartBreakTime};
    }

    /**
     *
     * @param user
     * @return
     * @throws BasicException
     */
    public final boolean IsOnLeave(String user) throws BasicException {
        Object[] value = new Object[] {new Date(), new Date(), user};
        String Data = (String) m_isonleave.find(value);
        // "0" rows shows user is not on leave
        if (Data.equals("0")) {
            return false;
        }
        return true;
    }

    // EmployeeList list
    // Changed ='4' to !='0' --it lists all the users except admin who doesn´t clock in

    /**
     *
     * @return
     */
        public SentenceList getEmployeeList(String siteGuid) {
        return new StaticSentence(s
            , new QBFBuilder("SELECT ID, NAME FROM PEOPLE WHERE SITEGUID = '" + siteGuid + "' AND ROLE != '0' AND VISIBLE = " + s.DB.TRUE() + " AND ?(QBF_FILTER) ORDER BY NAME", new String[] {"NAME"})
            , new SerializerWriteBasic(new Datas[] {Datas.OBJECT, Datas.STRING})
            , new SerializerRead() {
            @Override
                    public Object readValues(DataRead dr) throws BasicException {
                        EmployeeInfo c = new EmployeeInfo(dr.getString(1));
                        c.setName(dr.getString(2));
                        return c;
                    }
                });
    }

    /**
     *
     * @param user
     * @throws BasicException
     */
    public void BlockEmployee(String user) throws BasicException {
        boolean isOnBreak = IsOnBreak(user);
        if (isOnBreak) {
            EndBreak(user);
        }
        CheckOut(user);
    }

    TableDefinition getTableBreaks() {
        return tbreaks;
    }

    TableDefinition getTableLeaves() {
        return tleaves;
    }

    /**
     *
     * @param id
     * @return
     * @throws BasicException
     */
    public EmployeeInfoExt loadEmployeeExt(String id) throws BasicException {
        return (EmployeeInfoExt) new PreparedSentence(s
                , "SELECT ID, NAME FROM PEOPLE WHERE ID = ?"
                , SerializerWriteString.INSTANCE
                , new EmployeeExtRead()).find(id);
    }

    /**
     *
     */
    protected static class EmployeeExtRead implements SerializerRead {

        /**
         *
         * @param dr
         * @return
         * @throws BasicException
         */
        @Override
        public Object readValues(DataRead dr) throws BasicException {
            EmployeeInfoExt c = new EmployeeInfoExt(dr.getString(1));
            c.setName(dr.getString(2));
            return c;
        }
    }
}
