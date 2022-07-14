/**
 * 
 */
package com.brenner.portfoliomgmt.investments;

import org.springframework.core.convert.converter.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author dbrenner
 * 
 */
public class StringToInvestmentConverter implements Converter<String, Investment> {

	ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public Investment convert(String source) {
		
		try {
			if (source != null && source.trim().length() > 0) {
				return mapper.readValue(source, Investment.class);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
