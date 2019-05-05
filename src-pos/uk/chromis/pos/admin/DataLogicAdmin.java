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
package uk.chromis.pos.admin;

import java.util.List;
import javax.management.relation.RoleList;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SentenceExec;
import uk.chromis.data.loader.SentenceFind;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerRead;
import uk.chromis.data.loader.SerializerReadBytes;
import uk.chromis.data.loader.SerializerReadClass;
import uk.chromis.data.loader.SerializerReadString;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.data.loader.SerializerWriteString;
import uk.chromis.data.loader.Session;
import uk.chromis.data.loader.StaticSentence;
import uk.chromis.data.loader.TableDefinition;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.BeanFactoryDataSingle;

/**
 *
 * @author adrianromero
 */
public class DataLogicAdmin extends BeanFactoryDataSingle {

    private Session s;
    private TableDefinition m_tpeople;
    private TableDefinition m_troles;
    private TableDefinition m_tresources;

    private String searchKey;
    protected SentenceList m_resources;
    protected SentenceList m_sectionList;
    protected SentenceList m_rolesList;
    protected SentenceList m_namesList;
    protected SentenceList m_getroleslistbyrightslevel;
    protected SentenceFind m_description;
    protected SentenceList m_displayList;
    protected SentenceFind m_roleID;
    protected SentenceFind m_roleRightsLevel;
    protected SentenceFind m_roleRightsLevelByID;
    protected SentenceFind m_roleRightsLevelByUserName;
    protected SentenceExec m_insertentry;
    private SentenceFind m_rolepermissions;
    protected SentenceExec m_rolepermissionsdelete;
    protected SentenceList m_permissionClassList;

    /**
     * Creates a new instance of DataLogicAdmin
     */
    public DataLogicAdmin() {
    }

    /**
     *
     * @param s
     */
    @Override
    public void init(Session s) {
        this.s = s;

        m_sectionList = new StaticSentence(s, "SELECT DISTINCT SECTION FROM DBPERMISSIONS WHERE SITEGUID = ? ORDER BY SECTION",
                SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);

        m_roleID = new StaticSentence(s, "SELECT ID FROM ROLES WHERE NAME = ? AND SITEGUID = ? ", new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING}), SerializerReadString.INSTANCE);

        m_roleRightsLevel = new StaticSentence(s, "SELECT RIGHTSLEVEL FROM ROLES WHERE NAME = ? AND SITEGUID = ? ", new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING}), SerializerReadString.INSTANCE);

        m_roleRightsLevelByID = new StaticSentence(s, "SELECT RIGHTSLEVEL FROM ROLES WHERE ID = ? AND SITEGUID = ? ", new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING}), SerializerReadString.INSTANCE);

        m_rolepermissions = new PreparedSentence(s, "SELECT PERMISSIONS FROM ROLES WHERE ID = ? AND SITEGUID = ? ", new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING}), SerializerReadBytes.INSTANCE);

        m_getroleslistbyrightslevel = new PreparedSentence(s, "SELECT ID, NAME FROM ROLES WHERE RIGHTSLEVEL <= ? AND SITEGUID = ? ORDER BY NAME ", new SerializerWriteBasic(new Datas[]{
            Datas.INT,
            Datas.STRING}), new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new RoleInfo(dr.getString(1), dr.getString(2));
            }
        });

        m_roleRightsLevelByUserName = new StaticSentence(s, "SELECT ROLES.RIGHTSLEVEL FROM ROLES INNER JOIN PEOPLE ON PEOPLE.ROLE=ROLES.ID WHERE PEOPLE.NAME= ? AND SITEGUID = ?", new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING}), SerializerReadString.INSTANCE);

        //Above the lines have been modified with siteguid
        m_resources = new StaticSentence(s, "SELECT ID, NAME, RESTYPE, CONTENT FROM RESOURCES",
                null, SerializerReadString.INSTANCE);

        m_description = new StaticSentence(s, "SELECT DESCRIPTION FROM DBPERMISSIONS WHERE CLASSNAME = ? ", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);

        m_displayList = new StaticSentence(s, "SELECT DISPLAYNAME FROM DBPERMISSIONS WHERE SECTION = ? ORDER BY DISPLAYNAME", SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);

        m_rolesList = new StaticSentence(s, "SELECT ID FROM ROLES ", null, SerializerReadString.INSTANCE);

        m_namesList = new StaticSentence(s, "SELECT NAME FROM ROLES ", null, SerializerReadString.INSTANCE);

        m_permissionClassList = new StaticSentence(s, "SELECT CLASSNAME FROM DBPERMISSIONS ", null, SerializerReadString.INSTANCE);

        m_insertentry = new PreparedSentence(s, "INSERT INTO DBPERMISSIONS (CLASSNAME, SECTION, DISPLAYNAME, DESCRIPTION, SITEGUID) "
                + "VALUES (?, ?, ?, ?, ?)", new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING}));

        m_rolepermissionsdelete = new StaticSentence(s, "DELETE * FROM DBPERMISSIONS WHERE CLASSNAME = ?", SerializerWriteString.INSTANCE, null);

    }

    public final List<DBPermissionsInfo> getAlldbPermissions(String siteGuid) throws BasicException {
        return new PreparedSentence(s, "SELECT CLASSNAME, SECTION, DISPLAYNAME, DESCRIPTION, SITEGUID FROM DBPERMISSIONS WHERE SITEGUID = ? ORDER BY DISPLAYNAME",
                SerializerWriteString.INSTANCE, DBPermissionsInfo.getSerializerRead()).list(siteGuid);
    }

    public final List<String> getSectionsList(String siteGuid) throws BasicException {
        return m_sectionList.list(siteGuid);
    }

    public final List<RoleList> getRolesList(int userLevel, String siteGuid) throws BasicException {
        return m_getroleslistbyrightslevel.list(userLevel, siteGuid);
    }

    public final TableDefinition getTablePeople() {
        return m_tpeople;
    }

    public final TableDefinition getTableRoles() {
        return m_troles;
    }

    public final TableDefinition getTableResources() {
        return m_tresources;
    }

    public final String findRolePermissions(String sRole, String guid) {
        try {
            return Formats.BYTEA.formatValue(m_rolepermissions.find(sRole, guid));
        } catch (BasicException e) {
            return null;
        }
    }

    public final SentenceList getPeopleList() {
        return new StaticSentence(s, "SELECT ID, NAME FROM PEOPLE ORDER BY NAME", null, new SerializerReadClass(PeopleInfo.class));
    }

    public final List<String> getDisplayList(String section) throws BasicException {
        return m_displayList.list(section);
    }

    public final String getDescription(String className) throws BasicException {
        return m_description.find(className).toString();
    }

    public final List<String> getRoles() throws BasicException {
        return m_rolesList.list();
    }

    public final List<String> getNames() throws BasicException {
        return m_namesList.list();
    }
    
    public final List<String> getClassNames() throws BasicException {
        return m_permissionClassList.list();
    }

    public final String getRoleID(String roleName, String siteGuid) throws BasicException {
        return m_roleID.find(roleName, siteGuid).toString();
    }

    public final Integer getRightsLevel(String roleName, String siteGuid) throws BasicException {
        return Integer.parseInt(m_roleRightsLevel.find(roleName, siteGuid).toString());
    }

    public final String getRightsLevelByID(String roleName, String siteGuid) throws BasicException {
        return m_roleRightsLevelByID.find(roleName, siteGuid).toString();
    }

    public final String getRightsLevelByUserName(String userName, String siteGuid) throws BasicException {
        return m_roleRightsLevelByUserName.find(userName, siteGuid).toString();
    }

    public final void insertEntry(Object[] entry) throws BasicException {
        m_insertentry.exec(entry);
    }

    public final void deleteEntry(String entry) throws BasicException {
        m_rolepermissionsdelete.exec(entry);
    }

}
