/**
 * 
 */
package com.brenner.portfoliomgmt.batch.investments;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

/**
 *
 * @author dbrenner
 * 
 */
public class InvestmentsUploadJobCompletionListener  extends JobExecutionListenerSupport {
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
        } 
    }

}
