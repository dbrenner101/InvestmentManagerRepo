package com.brenner.portfoliomgmt.test;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
	"com.brenner.portfoliomgmt.accounts",
	 "com.brenner.portfoliomgmt.holdings",
	 "com.brenner.portfoliomgmt.investments", 
	 "com.brenner.portfoliomgmt.transasctions"})
@IncludeClassNamePatterns(".*Tests")
public class InvestmentsTestSuite {
}
