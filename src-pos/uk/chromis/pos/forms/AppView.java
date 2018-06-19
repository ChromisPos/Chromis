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

package uk.chromis.pos.forms;

import java.util.Date;
import uk.chromis.data.loader.Session;
import uk.chromis.pos.printer.DeviceTicket;
import uk.chromis.pos.scale.DeviceScale;
import uk.chromis.pos.scanpal2.DeviceScanner;

/**
 *
 * @author adrianromero
 */
public interface AppView {
    
    /**
     *
     * @return
     */
    public DeviceScale getDeviceScale();

    /**
     *
     * @return
     */
    public DeviceTicket getDeviceTicket();

    /**
     *
     * @return
     */
    public DeviceScanner getDeviceScanner();
      
    /**
     *
     * @return
     */
    public Session getSession();

    /**
     *
     * @return
     */
    public AppProperties getProperties();

    /**
     *
     * @param beanfactory
     * @return
     * @throws BeanFactoryException
     */
    public Object getBean(String beanfactory) throws BeanFactoryException;
     
    /**
     *
     * @param value
     * @param iSeq
     * @param dStart
     * @param dEnd
     */
    public void setActiveCash(String value, int iSeq, Date dStart, Date dEnd);

    /**
     *
     * @return
     */
    public String getActiveCashIndex();

    /**
     *
     * @return
     */
    public int getActiveCashSequence();

    /**
     *
     * @return
     */
    public Date getActiveCashDateStart();

    /**
     *
     * @return
     */
    public Date getActiveCashDateEnd();
    
    /**
     *
     * @return
     */
    public String getInventoryLocation();
    
    /**
     *
     */
    public void waitCursorBegin();

    /**
     *
     */
    public void waitCursorEnd();
    
    /**
     *
     * @return
     */
    public AppUserView getAppUserView();
}

