package com.github.zutherb.buildlight.application;

import com.github.zutherb.buildlight.arduino.driver.core.Arduino;
import com.github.zutherb.buildlight.common.driver.core.TrafficLight;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import static com.github.zutherb.buildlight.application.ConfigurationFile.*;
import static org.apache.commons.lang.StringUtils.isEmpty;


/**
 * @author zutherb
 */
public final class Runner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Runner.class);

    private Runner() {/* NOOP */}

    public static void main(String[] args) throws IOException {
        LOGGER.info("Build Light - Build Watch Application, Maintained by B. Zuther.\n" +
                "Send bug reports using https://github.com/zutherb/build-light/issues\n");
        if (CONFIGURATION_FILE.exists()) {
            lunchApplication();
        } else {
            printHelpMessage();
        }
    }

    private static void lunchApplication() throws IOException {
        Properties properties = loadPropertyFile();
        String builderServer = getBuildServerName(properties);

        System.setProperty("spring.profiles.active", builderServer.toLowerCase());
        System.setProperty("buildlight.property.file", CONFIGURATION_FILE.getAbsolutePath());

        stayAliveTillKeyboardInteractionIsProcessed(new ClassPathXmlApplicationContext("classpath:/com/github/zutherb/buildlight/application/spring-context.xml"));
    }

    private static String getBuildServerName(Properties properties) {
        String builderServer = properties.getProperty("build.server");
        LOGGER.info(String.format("Buildlight started in %s Mode", builderServer));
        printHelpMessageIfNoBuildServerIsConfigured(builderServer);
        return builderServer;
    }

    private static Properties loadPropertyFile() throws IOException {
        Properties properties = new Properties();
        properties.load(FileUtils.openInputStream(CONFIGURATION_FILE));
        return properties;
    }

    private static void stayAliveTillKeyboardInteractionIsProcessed(ClassPathXmlApplicationContext context) {
        Scanner scanner = new Scanner(System.in);
        String next = null;
        while (!"exit".equalsIgnoreCase(next)) {
            LOGGER.info("Type 'exit' and press enter to exit the application:");
            next = scanner.next();
        }
        TrafficLight trafficLight = context.getBean(TrafficLight.class);
        trafficLight.switchOffAllLeds();
        context.destroy();
        scanner.close();
        System.exit(0);
    }

    private static void printHelpMessageIfNoBuildServerIsConfigured(String builderServer) {
        if (isEmpty(builderServer)) {
            printHelpMessage();
            System.exit(1);
        }
    }

    private static void printHelpMessage() {
        LOGGER.error(String.format("Please create %s\n", CONFIGURATION_FILE.getAbsoluteFile()));
        LOGGER.error("Jenkins example:\n");
        LOGGER.error(JENKINS_DEFAULT_CONFIGURATION);
        LOGGER.error("Bamboo example:\n");
        LOGGER.error(BAMBOO_DEFAULT_CONFIGURATION);
        LOGGER.error("Driver Factory Beans:\n");
        LOGGER.error("buildlight.driver.factorybean=com.github.zutherb.buildlight.application.driver.ArduinoDriverFactoryBean\n");
        LOGGER.error("buildlight.driver.factorybean=com.github.zutherb.buildlight.application.driver.BlinkStickDriverFactoryBean\n");
        LOGGER.error("buildlight.driver.factorybean=com.github.zutherb.buildlight.application.driver.ClewareDriverFactoryBean\n");
        LOGGER.error("buildlight.driver.factorybean=com.github.zutherb.buildlight.application.driver.ConsoleTrafficLightFactoryBean [Default]\n");
        LOGGER.error("");
        LOGGER.error("Available Serial Port:\n");
        List<String> portList = Arduino.list();
        for (String port : portList) {
            LOGGER.error(port);
        }
    }
}
