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

import java.util.concurrent.ArrayBlockingQueue;

public class WindowedAverage {
	private ArrayBlockingQueue<Double> samples;
	
	public WindowedAverage(int windowSize) {
		this.samples = new ArrayBlockingQueue<Double>(windowSize);
	}
	
	public void addSample(Double sample) {
		synchronized (samples) {
			if (samples.remainingCapacity() == 0) {
				samples.remove();
			}
			if (samples.offer(sample) == false) {
				throw new RuntimeException("Failed to add new sample, queue was full");
			}
		}
	}
	
	public double sampleAverage() {
		synchronized (samples) {
			double sum = 0;
			for (Double d : samples) {
				sum += d;
			}
			return sum / (double)samples.size();
		}
	}
	
}
