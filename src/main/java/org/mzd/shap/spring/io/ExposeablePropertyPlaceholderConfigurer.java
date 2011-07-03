package org.mzd.shap.spring.io;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class ExposeablePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	private Map<String, String> definedProps;

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, 
			Properties props) throws BeansException {
		
		super.processProperties(beanFactoryToProcess, props);
		definedProps = new HashMap<String, String>();
		for (Object key : props.keySet()) {
			String keyStr = key.toString();
			definedProps.put(keyStr, props.getProperty(keyStr));
		}
	}
	
	public Map<String, String> getDefinedProps() {
		return Collections.unmodifiableMap(definedProps);
	}
	
	public String getDefinedProperty(String name) {
		return definedProps.get(name);
	}

}
