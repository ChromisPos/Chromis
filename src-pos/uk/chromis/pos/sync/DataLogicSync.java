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
package uk.chromis.pos.sync;

import java.util.logging.Level;
import java.util.logging.Logger;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SentenceExec;
import uk.chromis.data.loader.SentenceFind;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerRead;
import uk.chromis.data.loader.SerializerReadBoolean;
import uk.chromis.data.loader.SerializerReadString;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.data.loader.SerializerWriteString;
import uk.chromis.data.loader.Session;
import uk.chromis.data.loader.StaticSentence;
import uk.chromis.pos.forms.BeanFactoryDataSingle;

public class DataLogicSync extends BeanFactoryDataSingle {

    private Session s;
    protected SentenceFind m_getMappedValue;
    protected SentenceFind m_getLocation;
    protected SentenceList m_centralGuid;
    protected SentenceList m_centralName;
    protected SentenceList m_siteGuid;
    private SentenceExec m_updateLicences;

    public DataLogicSync() {
    }

    @Override
    public void init(Session s) {
        this.s = s;

        m_getLocation = new PreparedSentence(s, "SELECT ID FROM LOCATIONS WHERE ALIAS = '0' AND SITEGUID = ? ", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);
        m_getMappedValue = new PreparedSentence(s, "SELECT NEWVALUE FROM MAPPING WHERE NAME=? AND ORIGINAL=? ", new SerializerWriteBasic(new Datas[]{Datas.STRING, Datas.STRING}), SerializerReadString.INSTANCE);
        m_centralGuid = new PreparedSentence(s, "SELECT GUID FROM CENTRALSERVER ", null, SerializerReadString.INSTANCE);
        m_centralName = new PreparedSentence(s, "SELECT SERVERNAME FROM CENTRALSERVER ", null, SerializerReadString.INSTANCE);
        m_siteGuid = new PreparedSentence(s, "SELECT GUID FROM SITEGUID ", null, SerializerReadString.INSTANCE);       
        
        m_updateLicences = new StaticSentence(s, "UPDATE APPLICATIONS SET LICENCEKEY1 = ?, LICENCEKEY2 = ?, REGISTEREMAIL = ?, SYSTEMKEY = ?   ", new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING
        }));

    }

    public final String getNewValue(String sName, String original) {
        try {
            return (String) m_getMappedValue.find(sName, original);
        } catch (BasicException ex) {
            return "0";
        }
    }

    public final String getLocation(String siteGuid) {
        try {
            return (String) m_getLocation.find(siteGuid);
        } catch (BasicException ex) {
            return "0";
        }
    }

    public final String getCentralGuid() {
        try {
            return (String) m_centralGuid.list().get(0);
        } catch (BasicException ex) {
            return "";
        }
    }

    public final String getCentralName() throws BasicException {
        return (String) m_centralName.list().get(0);
    }

    public final String getSiteGuid() {
        try {

            return (String) m_siteGuid.list().get(0);
        } catch (BasicException ex) {
            Logger.getLogger(DataLogicSync.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public final SentenceList getSingleSiteFlag() {
        return new StaticSentence(s, "SELECT SINGLESITEFLAG FROM SINGELSITE",
                null, SerializerReadBoolean.INSTANCE);

    }

    public final SentenceList getSitesList() {
        return new StaticSentence(s, "SELECT "
                + "GUID, "
                + "NAME "
                + "FROM SITES "
                + "ORDER BY NAME", null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new SitesInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    public final SentenceList getSingleSite() {
        return new StaticSentence(s, "SELECT "
                + "GUID, '' "
                + "FROM SITEGUID ", null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new SitesInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    public Boolean isCentral() {
        System.out.println("the central guid : " + getCentralGuid());
        return (getCentralGuid().equals(getSiteGuid()));
    }

    public final void updateLicences(String lk1, String lk2, String email, String sk) throws BasicException {
        m_updateLicences.exec(lk1, lk2, email, sk);
    }
}
