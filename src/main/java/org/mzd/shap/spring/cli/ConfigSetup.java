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
package org.mzd.shap.spring.cli;

import java.util.List;

import org.mzd.shap.analysis.Annotator;
import org.mzd.shap.analysis.AnnotatorDao;
import org.mzd.shap.analysis.Detector;
import org.mzd.shap.analysis.DetectorDao;
import org.mzd.shap.domain.authentication.Role;
import org.mzd.shap.domain.authentication.RoleDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Simple tool for initial configuration of a new database.
 * 
 */
public class ConfigSetup {
	private List<Annotator> annotators;
	private List<Detector> detectors;

	public List<Annotator> getAnnotators() {
		return annotators;
	}

	public void setAnnotators(List<Annotator> annotators) {
		this.annotators = annotators;
	}

	public List<Detector> getDetectors() {
		return detectors;
	}

	public void setDetectors(List<Detector> detectors) {
		this.detectors = detectors;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: [local|grid]");
			System.exit(1);
		}
	
		try {
			String[] paths = new String[] {
						"war/WEB-INF/spring/datasource-context.xml",
						"war/WEB-INF/spring/analyzer-config.xml",
						"war/WEB-INF/spring/orm-context.xml" };
	
			ApplicationContext ctx = new FileSystemXmlApplicationContext(paths);
	
			AnnotatorDao annotatorDao = (AnnotatorDao) ctx.getBean("annotatorDao");
			DetectorDao detectorDao = (DetectorDao) ctx.getBean("detectorDao");
	
			ConfigSetup config = (ConfigSetup) ctx.getBean("configuration");
	
			for (Annotator an : config.getAnnotators()) {
				System.out.println("Adding annotator: " + an.getName());
				annotatorDao.saveOrUpdate(an);
			}
	
			for (Detector dt : config.getDetectors()) {
				System.out.println("Adding detector: " + dt.getName());
				detectorDao.saveOrUpdate(dt);
			}
	
			RoleDao roleDao = (RoleDao) ctx.getBean("roleDao");
			roleDao.saveOrUpdate(new Role("admin", "ROLE_ADMIN"));
			roleDao.saveOrUpdate(new Role("user", "ROLE_USER"));
	
			System.exit(0);
		}
		catch (Throwable t) {
			System.err.println(t.getMessage());
			System.exit(1);
		}
	}
}
