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

import java.util.Timer;
import java.util.TimerTask;

public class Refresh {
		
	private Timer refreshTimer;
	//private TimerTask task;
        private Integer period = 10000;
        private Boolean running = false;
	
	private static Refresh INSTANCE = new Refresh();

        
	public static Refresh getInstance() {		
		if (INSTANCE == null){
			synchronized (Refresh.class){
				if (INSTANCE == null){
					INSTANCE = new Refresh();
				}
			}
		}			
		return INSTANCE;
	}
	
	public void pause(){
            if (running)
		refreshTimer.cancel();
                running = false;
	}
	
        public void stop(){                        
            if (running)
		refreshTimer.cancel();
                running = false;
        }
        
        public void start(TimerTask task){
            if (running)stop();
            refreshTimer = new Timer();
            refreshTimer.scheduleAtFixedRate(task,100, this.period);            
            running = true;
        }
               
        public void setTimer(TimerTask task, Integer period){
            if (running) stop();
            this.period = period;
            refreshTimer = new Timer();
            refreshTimer.scheduleAtFixedRate(task, 100, period);
            running = true;
	}		
      
        public Boolean isRunning(){
            return this.running;
        }
}

