package com.brenner.portfoliomgmt.view.controller;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.brenner.portfoliomgmt.batch.holdings.HoldingsUploadBatchConfig;
import com.brenner.portfoliomgmt.batch.investments.InvestmentsUploadBatchConfig;
import com.brenner.portfoliomgmt.batch.quotes.QuotesUploadBatchConfig;
import com.brenner.portfoliomgmt.exception.BulkDataParseException;

/**
 * Controller for handling bulkd data requests
 * 
 * @author dbrenner
 *
 */
@Controller
public class BulkDataLoaderController {
    
    @Autowired
    HoldingsUploadBatchConfig transactionsUploadBatchConfig;
    
    @Autowired
    InvestmentsUploadBatchConfig investmentsUploadBatchConfig;
    
    @Autowired
    QuotesUploadBatchConfig quotesUploadBatchConfig;
    
   /**
    * Entry point for uploading bulk data.
    *  
    * @param dataType - The type of bulk data being uploaded.
    * @param delimiter - character that denotes unique fields
    * @param symbol - Investment identifier
    * @param file - File containing the bulk data
    * @param model - Object container
    * @return /loadBulkData
    * @throws BulkDataParseException - Common exception for errors
    * @throws IOException - File stream errors
    * @throws ParseException - Errors parsing the date
 * @throws JobParametersInvalidException 
 * @throws JobInstanceAlreadyCompleteException 
 * @throws JobRestartException 
 * @throws JobExecutionAlreadyRunningException 
    */
    @RequestMapping("/parseBulkData")
    public String loadBulkInvestmentData(
            @RequestParam(name="dataType", required=false) String dataType, 
            @RequestParam(name="delimiter", required=false) String delimiter, 
            @RequestParam(name="symbol", required=false) String symbol,
            @RequestParam(name="bulkData") MultipartFile file,
            Model model) throws BulkDataParseException, IOException {
    	
        
        if (dataType.trim().equals("holdings")) {
        	this.transactionsUploadBatchConfig.runJob(file.getInputStream(), file.getOriginalFilename());
        }
        else if (dataType.trim().equals("investments")) {
        	this.investmentsUploadBatchConfig.runJob(file.getInputStream(), file.getOriginalFilename());
        }
        else if(dataType.trim().equals("quotes")) {
        	this.quotesUploadBatchConfig.runJob(file.getInputStream(), file.getOriginalFilename());
        }
        
        return "/loadBulkData";
    }
    
    @RequestMapping("/loadBulkDataForm")
    public String loadBulkDataForm() {
        
        return "/loadBulkData";
    }

}
