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
package org.mzd.shap.hibernate.search.view;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.proxy.HibernateProxyHelper;
import org.mzd.shap.domain.Annotation;
import org.mzd.shap.domain.Feature;
import org.mzd.shap.domain.Project;
import org.mzd.shap.domain.Sample;
import org.mzd.shap.domain.Sequence;

/**
 * Factory for all domain object report builders. This factory's registry of builders 
 * is currently programmatically populated but could easily be converted to IoC.
 *
 */
public class ReportBuilderFactory {
	private String cssClass;
	private String delim;
	private Map<String, Class<? extends ReportBuilder>> registry = 
		new HashMap<String, Class<? extends ReportBuilder>>();
	
	/**
	 * Factory class for generating reports from various domain objects.
	 * 
	 * @param cssClass
	 * @param delim
	 */
	public ReportBuilderFactory(String cssClass, String delim) {
		this.cssClass = cssClass;
		this.delim = delim;
		this.registry.put(Annotation.class.getName(), AnnotationReportBuilder.class);
		this.registry.put(Feature.class.getName(), FeatureReportBuilder.class);
		this.registry.put(Sequence.class.getName(), SequenceReportBuilder.class);
		this.registry.put(Sample.class.getName(), SampleReportBuilder.class);
		this.registry.put(Project.class.getName(), ProjectReportBuilder.class);
	}
	
	/**
	 * Build a report from a target object. 
	 * <p>
	 * The method looks up the associated builder from the registry and then invokes its build method on
	 * the same target object to produce a report.
	 * 
	 * @param score
	 * @param targetObject
	 * 
	 * @return report for targetObject
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Report buildReport(Float score, Object targetObject) {
		String className = HibernateProxyHelper
			.getClassWithoutInitializingProxy(targetObject)
				.getName();
		
		return getBuilder(className).build(score, targetObject);
	}
	
	/**
	 * Get an instantiated builder for a given target object class.
	 * 
	 * @param targetClass the class for which we ultimately want to generate a report.
	 * @throws IllegalArgumentException when no builder is found for a target class.
	 * @return
	 */
	public ReportBuilder getBuilder(String targetClassName) {
		Class<? extends ReportBuilder> builderClass = registry.get(targetClassName);
		
		if (builderClass == null) {
			throw new IllegalArgumentException("No builder is registered for class [" + targetClassName + "]");
		}

		try {
			return builderClass.getConstructor(String.class, String.class).newInstance(cssClass,delim);
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}