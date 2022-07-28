/**
 * 
 */
package com.brenner.portfoliomgmt.batch.quotes;

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
public class QuotesUploadBatchConfig {
    
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    JobLauncher launcher;
    
    protected static final String[] COLUMN_NAMES = new String[]{"Symbol", "Last Price", "Change", "Chg %", "Currency", "Market Time", 
    		"Volume", "Shares", "Avg Vol (3m)", "Day Range", "52-Wk Range", "Day Chart", "Market Cap", "Quote Date"};
    
    ListItemWriter<QuotesUploadRowInstance> writer = new ListItemWriter<>();
    
    @Bean
    QuotesUploadRowProcessor quotesRowProcessor() {
    	return new QuotesUploadRowProcessor();
    }
    
    JobExecutionListener listener() {
    	return new QuotesUploadJobCompletionListener();
    }
    
    public QuotesUploadBatchConfig() {}
    
    public void runJob(InputStream inStream, String jobName) {
		init(inStream, jobName);
	}
	
	private void init(InputStream inStream, String jobName) {
		
		Step step1 = stepBuilderFactory.get("step1")
                .<QuotesUploadRowInstance, QuotesUploadRowInstance>chunk(2)
                .reader(reader(inStream))
                .processor(quotesRowProcessor())
                .writer(writer)
                .build();
    	
    	Job job = jobBuilderFactory.get("importInvestmentsJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener())
                .flow(step1)
                .end()
                .build();
    	
    	JobParameters jobParams = new JobParametersBuilder().addString("importQuotesJob", jobName).toJobParameters();
    	
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
    
    FlatFileItemReader<QuotesUploadRowInstance> reader(InputStream inStream) {
    	
    	FlatFileItemReader<QuotesUploadRowInstance> reader = new FlatFileItemReader<>();
    	DefaultLineMapper<QuotesUploadRowInstance> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(",");
        lineMapper.setLineTokenizer(tokenizer);
        tokenizer.setNames(COLUMN_NAMES);
        lineMapper.setFieldSetMapper(new QuotesUploadFieldSetMapper());
        reader.setResource(new InputStreamResource(inStream));
        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(1);
        reader.setName("QuotesCsvReader");
    	
    	return reader;
    }

}
