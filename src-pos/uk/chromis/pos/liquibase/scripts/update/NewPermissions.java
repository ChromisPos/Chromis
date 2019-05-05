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
package uk.chromis.pos.liquibase.scripts.update;

import uk.chromis.pos.util.*;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.ConnectionFactory;
import uk.chromis.pos.admin.DataLogicAdmin;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.sync.DataLogicSync;

public class NewPermissions implements liquibase.change.custom.CustomTaskChange {

    private static SAXParser m_sp = null;
    private Set<String> m_apermissions;
    private ArrayList<String> permParams;

    @Override
    public void execute(Database dtbs) throws CustomChangeException {
        Connection conn = null;
        PreparedStatement pstmt;

        conn = ConnectionFactory.getInstance().getConnection();

        DataLogicAdmin dlAdmin = new DataLogicAdmin();
        dlAdmin.init(SessionFactory.getInstance().getSession());

        DataLogicSystem dlSystem = new DataLogicSystem();
        dlSystem.init(SessionFactory.getInstance().getSession());

        DataLogicSync dlSync = new DataLogicSync();
        dlSync.init(SessionFactory.getInstance().getSession());

        List<String> roles = new ArrayList<>();

        try {
             roles = dlAdmin.getRoles();
            for (String s : roles) {
                String localGuid = dlSync.getSiteGuid();
                String sRolePermisions = dlSystem.findRolePermissions(s, localGuid);
                m_apermissions = new HashSet<>();
                m_apermissions.add("uk.chromis.pos.forms.JPanelMenu");
                m_apermissions.add("Menu.Exit");
                if (sRolePermisions != null) {
                    try {
                        if (m_sp == null) {
                            SAXParserFactory spf = SAXParserFactory.newInstance();
                            m_sp = spf.newSAXParser();
                        }
                        m_sp.parse(new InputSource(new StringReader(sRolePermisions)), new NewPermissions.ConfigurationHandler());
                    } catch (ParserConfigurationException | SAXException ePC) {
                    } catch (IOException ex) {
                        Logger.getLogger(NewPermissions.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    ArrayList<String> permParams = new ArrayList<>(m_apermissions);
                    dlSystem.updateNewPermissions(permParams, s);
                }
            }

        } catch (BasicException ex) {
            Logger.getLogger(NewPermissions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public void setUp() throws SetupException {

    }

    @Override
    public void setFileOpener(ResourceAccessor ra) {

    }

    @Override
    public ValidationErrors validate(Database dtbs) {
        return null;
    }

    private class ConfigurationHandler extends DefaultHandler {

        @Override
        public void startDocument() throws SAXException {
        }

        @Override
        public void endDocument() throws SAXException {
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("class".equals(qName)) {
                m_apermissions.add(attributes.getValue("name"));
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
        }
    }
}
