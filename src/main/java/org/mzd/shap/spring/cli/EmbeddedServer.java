package org.mzd.shap.spring.cli;

import org.mzd.shap.spring.io.ExposeablePropertyPlaceholderConfigurer;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EmbeddedServer {

	public static void main(String[] args) {
		AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("jetty-context.xml");
		ctx.registerShutdownHook();
		
		ExposeablePropertyPlaceholderConfigurer phcfg = 
			(ExposeablePropertyPlaceholderConfigurer)ctx.getBean("placeholderConfig");
		
		System.out.println(
				String.format("##\n## Server starting on http://localhost:%s%s\n##\n", 
						phcfg.getDefinedProperty("embedded.server.port"),
						phcfg.getDefinedProperty("embedded.server.path")));
	}
	
}