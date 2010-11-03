/**
 *
 * Copyright 2010 Matthew Z DeMaere.
 * 
 * This file is part of SHAP.
 *
 * SHAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SHAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SHAP.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.mzd.shap.util;

import java.util.List;
import java.util.Vector;

public class BaseObservable implements Observable {
	private Vector<Observer> observers = new Vector<Observer>();

	public void registerObserver(Observer obs) {
		synchronized (observers) {
			if (!observers.contains(obs)) {
				observers.add(obs);
			}
		}
	}

	public void removeObserver(Observer obs) {
		synchronized (observers) {
			if (observers.contains(obs)) {
				observers.remove(obs);
			}
		}
	}
	
	public void removeAllObservers() {
		synchronized (observers) {
			observers.clear();
		}
	}
	
	public int countObservers() {
		synchronized (observers) {
			return observers.size();
		}
	}

	public void notifyObservers(Notification notification) {
		synchronized (observers) {
			for (Observer obs : observers) {
				obs.update(notification);
			}
		}
	}
	
	/**
	 * Bean based setter for Spring, register a single observer.
	 * 
	 * @param obs observer to register
	 */
	public final void setObserver(Observer obs) {
		registerObserver(obs);
	}

	/**
	 * Bean based setter for Spring, register a list of observers.
	 *  
	 * @param obsList observers to register
	 */
	public final void setObservers(List<Observer> obsList) {
		for (Observer o : obsList) {
			registerObserver(o);
		}
	}
}
