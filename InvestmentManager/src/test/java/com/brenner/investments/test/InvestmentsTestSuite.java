package com.brenner.investments.test;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
	"com.brenner.investments.service",
	 "com.brenner.investments.controller"})
@IncludeClassNamePatterns(".*Tests")
public class InvestmentsTestSuite {
}
