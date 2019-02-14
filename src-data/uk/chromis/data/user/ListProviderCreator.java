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

package uk.chromis.data.user;

import java.util.List;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.TableDefinition;

/**
 *
 *
 */
public class ListProviderCreator implements ListProvider {

    private SentenceList sent;
    private EditorCreator prov;
    private Object params;

    /**
     * Creates a new instance of ListProviderEditor
     *
     * @param sent
     * @param prov
     */
    public ListProviderCreator(SentenceList sent, EditorCreator prov) {
        this.sent = sent;
        this.prov = prov;
        params = null;
    }

    /**
     *
     * @param sent
     */
    public ListProviderCreator(SentenceList sent) {
        this(sent, null);
    }

    /**
     *
     * @param table
     */
    public ListProviderCreator(TableDefinition table) {
        this(table.getListSentence(), null);
    }


    /**
     *
     * @return @throws BasicException
     */
    @Override
    public List loadData() throws BasicException {
        params = (prov == null) ? null : prov.createValue();
        return refreshData();
    }

    /**
     *
     * @return @throws BasicException
     */
    @Override
    public List refreshData() throws BasicException {
        return sent.list(params);
    }
}
