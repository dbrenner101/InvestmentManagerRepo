/**
 * 
 */
package com.brenner.portfoliomgmt.batch;

import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.ObjectError;

/**
 *
 * @author dbrenner
 * 
 */
public abstract class PortfolioManagementFieldSetMapper {
    
    protected BindingResult buildBindingResult(Object targetObject, String objectName, String message) {
    	BindingResult bindingResult = new DirectFieldBindingResult(targetObject, objectName);
		bindingResult.addError(new ObjectError(objectName, message));
		return bindingResult;
    }

}
