package com.sabre.qa.nimbus;

import org.apache.commons.lang.SystemUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;

/**
 * Hello world!
 *
 */
public class NimbusWebDriver {
    private WebDriver webDriver;
    private String browserName;

    public NimbusWebDriver(String browserName) {
        this.browserName = browserName;
        this.webDriver = createDriver(browserName);
    }

    private WebDriver createDriver(String browserName) {
        if (browserName.equalsIgnoreCase("CHROME")) {
            return chromeDriver();
        }
    }

    private WebDriver chromeDriver() {
        String driverName = "";
        String os = getOperatingSystem();
        switch (os) {
            case "mac" :

            case "linux" :
                driverName = "chromedriver";
            break;

            case "windows" : driverName = "chromedriver.exe";
            break;
        }
        String driverPath = System.getProperty("user.dir") + "/drivers/" +driverName;
        if (!new File(driverPath).exists())
            throw new RuntimeException("chromedriver does not exist!!!");

        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/drivers/" + driverName);
        return new ChromeDriver();
    }

    private String getOperatingSystem() {
        String os = SystemUtils.OS_NAME.toLowerCase();
        return os;
    }
}
