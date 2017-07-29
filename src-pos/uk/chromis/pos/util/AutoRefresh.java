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

package uk.chromis.pos.util;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.Timer;

public class AutoRefresh implements ActionListener, AWTEventListener {

    private final static long KEY_EVENTS = AWTEvent.KEY_EVENT_MASK;
    private final static long MOUSE_EVENTS = AWTEvent.MOUSE_MOTION_EVENT_MASK
            + AWTEvent.MOUSE_EVENT_MASK;
    private final static long USER_EVENTS = KEY_EVENTS + MOUSE_EVENTS;

    private Action action;
    private final long eventMask;
    private Boolean running = false;
    private Timer LogoffTimer;

    private static AutoRefresh INSTANCE = new AutoRefresh();
    public static Boolean timer = false;

    // create a basic timer instance
    private AutoRefresh() {
        LogoffTimer = new Timer(5000, action);
        this.eventMask = 0;
        LogoffTimer.setInitialDelay(100);
    }

    public static AutoRefresh getInstance() {
        if (INSTANCE == null) {
            synchronized (AutoRefresh.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AutoRefresh();
                }
            }
        }
        return INSTANCE;
    }

    /*
     * 
     * Routines to control the timer
     * start() manually starts the timer
     * stop() manually stops the timer
     * restart() manually restart the timer
     * isRunning() returns the state of the timer
     * setTimer(Integer period, action ) set the interval rate of the timer and action event
     * 
     * 
     */
    public void start() {
        if (this.timer) {
            this.running = true;
            LogoffTimer.setRepeats(false);
            LogoffTimer.start();
            Toolkit.getDefaultToolkit().addAWTEventListener(this, eventMask);
        }
    }

    public void stop() {
        if (this.timer) {
            this.running = false;
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
            LogoffTimer.stop();
        }
    }

    // Implement ActionListener for the Timer
    @Override
    public void actionPerformed(ActionEvent e) {
        action.actionPerformed(e);
    }

    // Implement AWTEventListener, all events are dispatched via this
    @Override
    public void eventDispatched(AWTEvent e) { 
        if ((this.timer) && this.isTimerRunning()) {
            LogoffTimer.restart();
        }
    }

 

    // returns the timer state
    public boolean isTimerRunning() {
        if (this.timer) {
            return (this.running);
        } else {
            return false;
        }
    }

    // set the timer interval in seconds
    public void setTimer(Integer period, Action action) {
        this.timer = true;
        if (isTimerRunning()) {
            this.stop();
            LogoffTimer = new Timer(period, action);
            LogoffTimer.start();
        } else {
            LogoffTimer = new Timer(period, action);
            LogoffTimer.start();
        }
    }

    public void activateTimer() {
      //  System.out.println("activate");
        this.timer = true; 
        this.running = true;
        this.start();
    }

    public void deactivateTimer() {
     //   System.out.println("deactivate");
        this.stop();
        this.running = false;
        this.timer = false;
    }
}
