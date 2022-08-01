package org.dstadler.commoncrawl;

import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public class LoggingUtils {

	/**
	 * Initialize log4j 2 programmatically to avoid the hassle of ensuring that
	 * the proper log4j2.xml is availabe in the classpath.
	 */
	public static void configureLog4j2() throws IOException {
		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		AppenderComponentBuilder console = builder.newAppender("stdout", "Console");

		LayoutComponentBuilder standard = builder.newLayout("PatternLayout");
		standard.addAttribute("pattern", "%d [%t] [%-5level] %msg%n%throwable");

		console.add(standard);

		builder.add(console);

		RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.FATAL);
		rootLogger.add(builder.newAppenderRef("stdout"));

		builder.add(rootLogger);

		builder.writeXmlConfiguration(System.out);
		System.out.println();

		Configurator.initialize(builder.build());
	}
}
