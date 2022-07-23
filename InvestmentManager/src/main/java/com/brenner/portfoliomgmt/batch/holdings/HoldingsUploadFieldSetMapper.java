package com.brenner.portfoliomgmt.batch.holdings;

import java.math.BigDecimal;
import java.text.ParseException;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.brenner.portfoliomgmt.batch.PortfolioManagementFieldSetMapper;
import com.brenner.portfoliomgmt.util.CommonUtils;

public class HoldingsUploadFieldSetMapper extends PortfolioManagementFieldSetMapper implements FieldSetMapper<NewHoldingsUploadRowInstance> {
    
    @Override
    public NewHoldingsUploadRowInstance mapFieldSet(FieldSet fieldSet) throws BindException {
    	
    	NewHoldingsUploadRowInstance rowInstance = new NewHoldingsUploadRowInstance();
    
        String[] fieldNames = fieldSet.getNames();
        for (int i=0; i<fieldNames.length; i++) {
        	String fieldName = fieldNames[i];
        	String fieldValue = fieldSet.readString(fieldName);
        	switch (fieldName) {
        		case "Account Name":
        			if (fieldValue == null || fieldValue.trim().length() == 0) {
        				throw new BindException(buildBindingResult(rowInstance, "Account Name", "Account name is empty or null. Row: " + i));
        			}
        			rowInstance.setAccountName(fieldValue);;
        			break;
        		case "Investment":
        			if (fieldValue == null || fieldValue.trim().length() == 0) {
        				throw new BindException(buildBindingResult(rowInstance, "Investment", "Investment symbol is empty or null. Row: " + i));
        			}
        			rowInstance.setInvestmentSymbol(fieldValue);
        			break;
        		case "Date of Data":
        			try {
        				rowInstance.setDateOfData(CommonUtils.convertCommonDateFormatStringToDate(fieldValue));
					} catch (ParseException e1) {
						throw new BindException(buildBindingResult(rowInstance, "Date of Data", "Unable to parse date: " + fieldValue + ". Row: " + i));
					}
        			break;
        		case "Acquired":
					try {
						rowInstance.setAcquiredDate(CommonUtils.convertCommonDateFormatStringToDate(fieldValue));
					} catch (ParseException e) {
						throw new BindException(buildBindingResult(rowInstance, "Acquired", "Unable to parse date: " + fieldValue + ". Row: " + i));
					}
        			break;
        		case "Current Value":
        			if (fieldValue == null || fieldValue.trim().length() == 0) {
        				throw new BindException(buildBindingResult(rowInstance, "Current Value", "Current value is null or empty" + fieldValue + ". Row: " + i));
        			}
        			rowInstance.setCurrentValue(BigDecimal.valueOf(CommonUtils.convertCurrencyStringToFloat(fieldValue)));
        			break;
        		case "Quantity":
        			if (fieldValue == null || fieldValue.trim().length() == 0) {
        				throw new BindException(buildBindingResult(rowInstance, "Quantity", "Quantity is null or empty. Row: " + i));
        			}
        			rowInstance.setQuantity(new BigDecimal(fieldValue.replace(",", "")));
        			break;
        		case "Cost Basis Per Share":
        			if (fieldValue == null || fieldValue.trim().length() == 0) {
        				throw new BindException(buildBindingResult(rowInstance, "Cost Basis Per Share", "Price is null or empty. Row: " + i));
        			}
        			rowInstance.setSharePrice(BigDecimal.valueOf(CommonUtils.convertCurrencyStringToFloat(fieldValue)));
        			break;
        	}
        }
        
        return rowInstance;
    }
}
