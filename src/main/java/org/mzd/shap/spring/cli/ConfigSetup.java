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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.mzd.shap.analysis.Annotator;
import org.mzd.shap.analysis.AnnotatorDao;
import org.mzd.shap.analysis.Detector;
import org.mzd.shap.analysis.DetectorDao;
import org.mzd.shap.domain.authentication.Role;
import org.mzd.shap.domain.authentication.RoleDao;
import org.mzd.shap.domain.authentication.User;
import org.mzd.shap.domain.authentication.UserDao;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

/**
 * Simple tool for initial configuration of a new database.
 * <p>
 * Running this tool will cause the database to by initialized. In doing
 * so, tables will be dropped and recreated. It is important that this tool
 * not be run on a pre-existing system.
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

	private final static String USAGE_MSG = "Usage: [analyzer_configuration]";
	public static void exitOnError(int value, String message) {
		if (message != null) {
			System.err.println(message);
		}
		System.out.println(USAGE_MSG);
		System.exit(value);
	}
	
	public static void main(String[] args) {
		// check args
		if (args.length != 1) {
			exitOnError(1,null);
		}
		
		// check file existance
		File analyzerXML = new File(args[0]);
		if (!analyzerXML.exists()) {
			exitOnError(1, analyzerXML.getPath() + " did not exist\n");
		}
		
		// prompt user whether existing data should be purged
		String ormContext = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Do you wish to purge the database before running setup?");
			System.out.println("WARNING: all existing data in SHAP will be lost!");
			System.out.println("Really purge? yes/[NO]");
			String ans = br.readLine();
			if (ans.toLowerCase().equals("yes")) {
				System.out.println("Purging enabled");
				ormContext = "orm-purge-context.xml";
				
			}
			else {
				System.out.println("Purging disabled");
				ormContext = "orm-context.xml";
			}
		}
		catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		
		// run tool
		try {
			String[] paths = new String[] {
						"datasource-context.xml",
						ormContext,
						analyzerXML.getPath()};
	
			GenericApplicationContext ctx = new GenericApplicationContext();

			XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
			xmlReader.loadBeanDefinitions("classpath:datasource-context.xml");
			xmlReader.loadBeanDefinitions(new ClassPathResource(ormContext));
			xmlReader.loadBeanDefinitions(new FileSystemResource(analyzerXML));
			ctx.refresh();			
			
			/*
			 * Create an base admin user.
			 */
			RoleDao roleDao = (RoleDao)ctx.getBean("roleDao");
			Role adminRole = roleDao.saveOrUpdate(new Role("admin","ROLE_ADMIN"));
			Role userRole = roleDao.saveOrUpdate(new Role("user","ROLE_USER"));
			UserDao userDao = (UserDao)ctx.getBean("userDao");
			userDao.saveOrUpdate(new User("admin","admin","shap01",adminRole,userRole));
			
			/*
			 * Create some predefined analyzers. Users should have modified
			 * the configuration file to suit their environment.
			 */
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
	
			System.exit(0);
		}
		catch (Throwable t) {
			System.err.println(t.getMessage());
			System.exit(1);
		}
	}
}
