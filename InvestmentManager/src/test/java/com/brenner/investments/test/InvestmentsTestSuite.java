package com.brenner.investments.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.test.context.ActiveProfiles;

import com.brenner.investments.controller.integration.data.test.AccountDataServiceTests;
import com.brenner.investments.controller.integration.data.test.HoldingsDataServiceTests;
import com.brenner.investments.controller.integration.data.test.InvestmentsDataServiceTests;
import com.brenner.investments.controller.integration.test.HistoricalQuoteControllerTests;
import com.brenner.investments.controller.unit.test.AccountControllerTests;
import com.brenner.investments.controller.unit.test.HoldingsControllerTests;
import com.brenner.investments.controller.unit.test.InvestmentControllerTests;
import com.brenner.investments.controller.unit.test.TransactionControllerTests;
import com.brenner.investments.service.unit.test.AccountsDataServiceTest;
import com.brenner.investments.service.unit.test.InvestmentRepositoryTests;
import com.brenner.investments.service.unit.test.QuotesDataServiceTest;
import com.brenner.investments.service.unit.test.TransactionsDataServiceTest;

@RunWith(Suite.class)
@SuiteClasses({
	AccountsDataServiceTest.class,
	AccountDataServiceTests.class,
	AccountControllerTests.class,
	
	HoldingsDataServiceTests.class,
	HoldingsControllerTests.class,
	
	InvestmentsDataServiceTests.class,
	InvestmentControllerTests.class,
	InvestmentRepositoryTests.class,
	
	QuotesDataServiceTest.class,
	HistoricalQuoteControllerTests.class,
	TestHistoricalQuoteDeserializer.class,
	
	TransactionsDataServiceTest.class,
	TransactionControllerTests.class,
	
	CommonUtilsTests.class
	
})
@ActiveProfiles("test")
public class InvestmentsTestSuite {
}
