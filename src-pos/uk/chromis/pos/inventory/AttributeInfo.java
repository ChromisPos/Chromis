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

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.IKeyed;
import uk.chromis.data.loader.SerializerRead;

public class AttributeInfo implements IKeyed {

    private String id;
    private String name;
    private String siteGuid;

    public AttributeInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public AttributeInfo(String id, String name, String siteGuid) {
        this.id = id;
        this.name = name;
        this.siteGuid = siteGuid;
    }

    @Override
    public Object getKey() {
        return id;
    }

    public String getSiteGuid() {
        return siteGuid;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                String id = dr.getString(1);
                String name = dr.getString(2);
                String siteGuid = dr.getString(3);
                return new AttributeInfo(id, name, siteGuid);
            }
        };
    }

}
