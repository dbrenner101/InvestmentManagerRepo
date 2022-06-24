package com.brenner.investments;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Properties configuration. Primarily containing MVC attribute key properties.
 * 
 * @author dbrenner
 *
 */
@Configuration
@PropertySource("classpath:configuration.properties")
public class InvestmentsProperties {
	
	
	@Value("${model.attribute.total.change.marketvalue}")
	private String totalMarketValueChangeAttribute;
	
	@Value("${model.attribute.holdings.by.marketvalue}")
	private String holdingsByMarketValueAttributeKey;
     
    @Value("${model.attribute.holdings.list}")
    private String holdingsListAttributeKey;
    
    @Value("${model.attribute.accounts.list}")
    private String accountsListAttributeKey;
    
    @Value("${model.attribute.account}")
    private String accountAttributeKey;
    
    @Value("${model.attribute.accountsandcash}")
    private String accountsAndCashAttributeKey;
    
    @Value("${model.attribute.accountId}")
    private String accountIdAttributeKey;
    
    @Value("${model.attribue.quotes.list}")
    private String quotesListAttributeKey;
    
    @Value("${model.attribute.investments.list}")
    private String investmentsListAttributeKey;
    
    @Value("${model.attribute.investment}")
    private String investmentAttributeKey;
    
    @Value("${model.attribut.transactions.list}")
    private String transactionsListAttributeKey;
    
    @Value("${model.attribute.transaction}")
    private String transactionAttributeKey;
    
    @Value("${model.attribute.holding}")
    private String holdingAttributeKey;
    
    @Value("${map.key.nearest.weekday}")
    private String nearestWeekdayKey;
    
    @Value("${map.key.weekday.six.month.old}")
    private String sixMonthsAgoWeekdayKey;
    
    
    

    public String getHoldingsListAttributeKey() {
        return holdingsListAttributeKey;
    }

    public void setHoldingsListAttributeKey(String holdingsListAttributeKey) {
        this.holdingsListAttributeKey = holdingsListAttributeKey;
    }

    public String getAccountsListAttributeKey() {
        return accountsListAttributeKey;
    }

    public void setAccountsListAttributeKey(String accountsListAttributeKey) {
        this.accountsListAttributeKey = accountsListAttributeKey;
    }

    public String getAccountAttributeKey() {
        return accountAttributeKey;
    }

    public void setAccountAttributeKey(String accountAttributeKey) {
        this.accountAttributeKey = accountAttributeKey;
    }

    public String getQuotesListAttributeKey() {
        return quotesListAttributeKey;
    }

    public void setQuotesListAttributeKey(String quotesListAttributeKey) {
        this.quotesListAttributeKey = quotesListAttributeKey;
    }

    public String getInvestmentsListAttributeKey() {
        return investmentsListAttributeKey;
    }

    public void setInvestmentsListAttributeKey(String investmentsListAttributeKey) {
        this.investmentsListAttributeKey = investmentsListAttributeKey;
    }

    public String getInvestmentAttributeKey() {
        return investmentAttributeKey;
    }

    public void setInvestmentAttributeKey(String investmentAttributeKey) {
        this.investmentAttributeKey = investmentAttributeKey;
    }

    public String getAccountIdAttributeKey() {
        return accountIdAttributeKey;
    }

    public void setAccountIdAttributeKey(String accountIdAttributeKey) {
        this.accountIdAttributeKey = accountIdAttributeKey;
    }

    public String getHoldingAttributeKey() {
        return holdingAttributeKey;
    }

    public void setHoldingAttributeKey(String holdingAttributeKey) {
        this.holdingAttributeKey = holdingAttributeKey;
    }

    public String getNearestWeekdayKey() {
        return nearestWeekdayKey;
    }

    public void setNearestWeekdayKey(String nearestWeekdayKey) {
        this.nearestWeekdayKey = nearestWeekdayKey;
    }

    public String getSixMonthsAgoWeekdayKey() {
        return sixMonthsAgoWeekdayKey;
    }

    public void setSixMonthsAgoWeekdayKey(String sixMonthsAgoWeekdayKey) {
        this.sixMonthsAgoWeekdayKey = sixMonthsAgoWeekdayKey;
    }

    public String getAccountsAndCashAttributeKey() {
        return accountsAndCashAttributeKey;
    }

    public void setAccountsAndCashAttributeKey(String accountsAndCashAttributeKey) {
        this.accountsAndCashAttributeKey = accountsAndCashAttributeKey;
    }

	public String getTransactionsListAttributeKey() {
		return transactionsListAttributeKey;
	}

	public void setTransactionsListAttributeKey(String transactionsListAttributeKey) {
		this.transactionsListAttributeKey = transactionsListAttributeKey;
	}

	public String getTransactionAttributeKey() {
		return transactionAttributeKey;
	}

	public void setTransactionAttributeKey(String transactionAttributeKey) {
		this.transactionAttributeKey = transactionAttributeKey;
	}

	public String getHoldingsByMarketValueAttributeKey() {
		return holdingsByMarketValueAttributeKey;
	}

	public void setHoldingsByMarketValueAttributeKey(String holdingsByMarketValueAttributeKey) {
		this.holdingsByMarketValueAttributeKey = holdingsByMarketValueAttributeKey;
	}

	public String getTotalMarketValueChangeAttribute() {
		return totalMarketValueChangeAttribute;
	}

	public void setTotalMarketValueChangeAttribute(String totalMarketValueChangeAttribute) {
		this.totalMarketValueChangeAttribute = totalMarketValueChangeAttribute;
	}
}
