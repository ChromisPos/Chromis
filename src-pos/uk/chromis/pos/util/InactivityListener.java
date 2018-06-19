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

/*
 *
 *  KEY_EVENTS
 *  MOUSE_EVENTS - which includes mouse motion events
 *  USER_EVENTS - includes KEY_EVENTS and MOUSE_EVENT (this is the default)
 *
 */

package uk.chromis.pos.util;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.Timer;

/**
 *
 *   
 */
public class InactivityListener implements ActionListener, AWTEventListener
{

    /**
     *
     */
    public final static long KEY_EVENTS = AWTEvent.KEY_EVENT_MASK;

    /**
     *
     */
    public final static long MOUSE_EVENTS =
		AWTEvent.MOUSE_MOTION_EVENT_MASK + AWTEvent.MOUSE_EVENT_MASK;

    /**
     *
     */
    public final static long USER_EVENTS = KEY_EVENTS + MOUSE_EVENTS;

	private final Action action;
	private int interval;
	private final long eventMask;
	private final Timer timer = new Timer(0, this);


// Specify the inactivity interval and listen for USER_EVENTS

    /**
     *
     * @param action
     * @param seconds
     */
    	public InactivityListener(Action action, int seconds)
	{
                this.action = action;
                this.eventMask =USER_EVENTS;
                timer.setInitialDelay(seconds);                
	}

    /**
     *
     */
    public void start()
	{
		timer.setRepeats(false);
		timer.start();
		Toolkit.getDefaultToolkit().addAWTEventListener(this, eventMask);
	}

    /**
     *
     */
    public void stop()
	{
		Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		timer.stop();
	}

//  Implement ActionListener for the Timer
    @Override
	public void actionPerformed(ActionEvent e)
	{
		action.actionPerformed(e);
	}

//  Implement AWTEventListener, all events are dispatched via this
    @Override
	public void eventDispatched(AWTEvent e)
	{
		if (timer.isRunning())
			timer.restart();
	}
// Impement a manually triggered restart

    /**
     *
     */
            public void restart(){
			timer.restart();            
        }

    /**
     *
     */
    public void setRunning()
	{
		if (!timer.isRunning())
			//timer.start();
                        timer.restart();
	}
              
}

