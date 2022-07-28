/**
 * 
 */
package com.brenner.portfoliomgmt.batch.quotes;

import java.math.BigDecimal;
import java.text.ParseException;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.brenner.portfoliomgmt.batch.PortfolioManagementFieldSetMapper;
import com.brenner.portfoliomgmt.util.CommonUtils;

/**
 *
 * @author dbrenner
 * 
 */
public class QuotesUploadFieldSetMapper extends PortfolioManagementFieldSetMapper implements FieldSetMapper<QuotesUploadRowInstance> {

	@Override
	public QuotesUploadRowInstance mapFieldSet(FieldSet fieldSet) throws BindException {
		
		QuotesUploadRowInstance rowInstance = new QuotesUploadRowInstance();
		
		String[] fieldNames = fieldSet.getNames();
        for (int i=0; i<fieldNames.length; i++) {
        	String fieldName = fieldNames[i];
        	String fieldValue = fieldSet.readString(fieldName);
        	
        	switch (fieldName) {
        		case ("Symbol"):
        			if (fieldValue == null || fieldValue.trim().length() == 0) {
        				throw new BindException(buildBindingResult(rowInstance, fieldName, fieldName + " is empty or null. Row: " + i));
        			}
        			rowInstance.setSymbol(fieldValue);
        			break;
        		case ("Quote Date"):
        			if (fieldValue == null || fieldValue.trim().length() == 0) {
        				throw new BindException(buildBindingResult(rowInstance, fieldName, fieldName + " is empty or null. Row: " + i));
        			}
				try {
					rowInstance.setQuoteDate(CommonUtils.convertDateString2DigitYearToDate(fieldValue));
				} catch (ParseException e) {
					throw new BindException(buildBindingResult(rowInstance, fieldName, fieldName + ": " + e.getMessage() + " row: " + i));
				}
        			break;
        		case ("Last Price"):
        			if (fieldValue == null || fieldValue.trim().length() == 0) {
        				throw new BindException(buildBindingResult(rowInstance, fieldName, fieldName + " is empty or null. Row: " + i));
        			}
        			rowInstance.setClose(BigDecimal.valueOf(CommonUtils.convertCurrencyStringToFloat(fieldValue)));
        			break;
        	}
        }
		
		return rowInstance;
	}

}
