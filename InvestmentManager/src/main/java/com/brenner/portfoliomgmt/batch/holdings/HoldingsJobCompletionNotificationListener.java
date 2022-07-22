package com.brenner.portfoliomgmt.batch.holdings;

import java.util.List;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.support.ListItemWriter;

public class HoldingsJobCompletionNotificationListener extends JobExecutionListenerSupport {
    
    ListItemWriter<NewHoldingsUploadRowInstance> writer;
    
    HoldingsJobCompletionNotificationListener(ListItemWriter<NewHoldingsUploadRowInstance> writer) {
        this.writer = writer;
    }
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            
            @SuppressWarnings("unchecked") 
			List<NewHoldingsUploadRowInstance> rowDataList = (List<NewHoldingsUploadRowInstance>) writer.getWrittenItems();
            rowDataList.forEach(System.out::println);
        } 
    }
}
