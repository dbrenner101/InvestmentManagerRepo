/**
 * 
 */
package com.brenner.portfoliomgmt.batch.holdings;

import java.io.InputStream;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;

/**
 *
 * @author dbrenner
 * 
 */
@Configuration
@EnableBatchProcessing
public class HoldingsUploadBatchConfig {
    
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    JobLauncher launcher;
    
    protected static final String[] COLUMN_NAMES = new String[]{"Account Name", "Investment", "Date of Data", "Acquired", "Term", 
    		"$ Total Gain/Loss", "% Total Gain/Loss", "Current Value", "Quantity", "Cost Basis Per Share", "Cost Basis"};
    
    ListItemWriter<NewHoldingsUploadRowInstance> writer = new ListItemWriter<>();
    
    @Bean
    HoldingsUploadRowProcessor processor() {
        return new HoldingsUploadRowProcessor();
    }
    
    @Bean
    JobExecutionListener listener() {
    	return new HoldingsJobCompletionNotificationListener(writer);
    }
	
	public HoldingsUploadBatchConfig() {}
	
	public void runJob(InputStream inStream, String jobName) {
		init(inStream, jobName);
	}
	
	private void init(InputStream inStream, String jobName) {
		
		Step step1 = stepBuilderFactory.get("step1")
                .<NewHoldingsUploadRowInstance, NewHoldingsUploadRowInstance>chunk(2)
                .reader(reader(inStream))
                .processor(processor())
                .writer(writer)
                .build();
    	
    	Job job = jobBuilderFactory.get("importTransactionsJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener())
                .flow(step1)
                .end()
                .build();
    	
    	JobParameters jobParams = new JobParametersBuilder().addString("transactionsBatch", jobName).toJobParameters();
    	
    	try {
			this.launcher.run(job, jobParams);
		} catch (JobExecutionAlreadyRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobRestartException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobInstanceAlreadyCompleteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobParametersInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    FlatFileItemReader<NewHoldingsUploadRowInstance> reader(InputStream inStream) {
    	
    	FlatFileItemReader<NewHoldingsUploadRowInstance> reader = new FlatFileItemReader<>();
    	DefaultLineMapper<NewHoldingsUploadRowInstance> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(",");
        lineMapper.setLineTokenizer(tokenizer);
        tokenizer.setNames(COLUMN_NAMES);
        lineMapper.setFieldSetMapper(new HoldingsUploadFieldSetMapper());
        reader.setResource(new InputStreamResource(inStream));
        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(1);
        reader.setName("TransactionsCsvReader");
    	
    	return reader;
    }
    

}
