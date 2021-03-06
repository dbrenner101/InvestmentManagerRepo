package com.brenner.portfoliomgmt.domain;

public enum InvestmentTypeEnum {
	Stock("Stock", 0),
	ETF("ETF", 1),
	MutualFund("Mutual Fund", 2),
	Bond ("Bond", 3), 
	REIT("Real Estate Investment Trust", 4), 
	CD("Certificate of Deposit", 5),
	Crypto("Crypto", 6), 
	Cash("Cash", 7);
	
	private final String displayName;
	private final int index;
	
	InvestmentTypeEnum(String displayName, int index) {
		this.displayName = displayName;
		this.index = index;
	}

	public String getDisplayName() {
		return this.displayName;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public static InvestmentTypeEnum valueOf(int index) {
		
		switch(index) {
		case 0:
			return InvestmentTypeEnum.Stock;
		case 1:
			return InvestmentTypeEnum.ETF;
		case 2:
			return InvestmentTypeEnum.MutualFund;
		case 3:
			return InvestmentTypeEnum.Bond;
		case 4:
			return InvestmentTypeEnum.REIT;
		case 5:
			return InvestmentTypeEnum.CD;
		case 6:
			return InvestmentTypeEnum.Crypto;
		case 7:
			return InvestmentTypeEnum.Cash;
		default:
			return InvestmentTypeEnum.Stock;
		}
	}
}
