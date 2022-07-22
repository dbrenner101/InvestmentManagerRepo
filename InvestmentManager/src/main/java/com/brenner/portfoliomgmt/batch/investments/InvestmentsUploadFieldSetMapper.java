/**
 * 
 */
package com.brenner.portfoliomgmt.batch.investments;

import java.math.BigDecimal;
import java.util.Date;

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
public class InvestmentsUploadFieldSetMapper extends PortfolioManagementFieldSetMapper implements FieldSetMapper<InvestmentsUploadRowInstance> {

	@Override
	public InvestmentsUploadRowInstance mapFieldSet(FieldSet fieldSet) throws BindException {
		
		InvestmentsUploadRowInstance rowInstance = new InvestmentsUploadRowInstance();
		rowInstance.setDate(new Date());
		
		String[] fieldNames = fieldSet.getNames();
        for (int i=0; i<fieldNames.length; i++) {
        	String fieldName = fieldNames[i];
        	String fieldValue = fieldSet.readString(fieldName);
        	
        	switch (fieldName) {
        	case "Symbol":
        		if (fieldValue == null || fieldValue.trim().length() == 0) {
    				throw new BindException(buildBindingResult(rowInstance, fieldName, fieldName + " is empty or null. Row: " + i));
    			}
        		rowInstance.setSymbol(fieldValue);
        		break;
        	case "Description":
        		if (fieldValue == null || fieldValue.trim().length() == 0) {
    				throw new BindException(buildBindingResult(rowInstance, fieldName, fieldName + " is empty or null. Row: " + i));
    			}
        		rowInstance.setName(fieldValue);
        		break;
        	case "Last Price":
        		if (fieldValue == null || fieldValue.trim().length() == 0) {
    				throw new BindException(buildBindingResult(rowInstance, fieldName, fieldName + " is empty or null. Row: " + i));
    			}
        		rowInstance.setLastPrice(BigDecimal.valueOf(CommonUtils.convertCurrencyStringToFloat(fieldValue)));
        		break;
        	}
        }
		
		return rowInstance;
	}

}
