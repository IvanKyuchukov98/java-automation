package com.qa.automation.runner;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Entry point for Maven/IDE runs. Cucumber options live in junit-platform.properties;
 * filter by tag with -Dcucumber.filter.tags="@api".
 */
@Suite
@IncludeEngines("cucumber")
@SelectPackages("features")
public class RunCucumberTest {
}
