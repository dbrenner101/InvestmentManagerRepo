/**
 * 
 */
package com.brenner.portfoliomgmt.batch.investments;

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
public class InvestmentsUploadBatchConfig {
    
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    JobLauncher launcher;
    
    protected static final String[] COLUMN_NAMES = new String[]{"Account Number", "Account Name", "Symbol", "Description", 
    		"Quantity", "Last Price", "Last Price Change", "Current Value", "Today's Gain/Loss Dollar", "Today's Gain/Loss Percent", 
    		"Total Gain/Loss Dollar", "Total Gain/Loss Percent", "Percent Of Account", "Cost Basis", "Cost Basis Per Share", "Type"};
    
    ListItemWriter<InvestmentsUploadRowInstance> writer = new ListItemWriter<>();
    
    @Bean
    InvestmentsUploadRowProcessor rowProcessor() {
    	return new InvestmentsUploadRowProcessor();
    }
    
    JobExecutionListener listener() {
    	return new InvestmentsUploadJobCompletionListener();
    }
    
    public InvestmentsUploadBatchConfig() {}
    
    public void runJob(InputStream inStream, String jobName) {
		init(inStream, jobName);
	}
	
	private void init(InputStream inStream, String jobName) {
		
		Step step1 = stepBuilderFactory.get("step1")
                .<InvestmentsUploadRowInstance, InvestmentsUploadRowInstance>chunk(2)
                .reader(reader(inStream))
                .processor(rowProcessor())
                .writer(writer)
                .build();
    	
    	Job job = jobBuilderFactory.get("importInvestmentsJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener())
                .flow(step1)
                .end()
                .build();
    	
    	JobParameters jobParams = new JobParametersBuilder().addString("importInvestmentsJob", jobName).toJobParameters();
    	
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
    
    FlatFileItemReader<InvestmentsUploadRowInstance> reader(InputStream inStream) {
    	
    	FlatFileItemReader<InvestmentsUploadRowInstance> reader = new FlatFileItemReader<>();
    	DefaultLineMapper<InvestmentsUploadRowInstance> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(",");
        lineMapper.setLineTokenizer(tokenizer);
        tokenizer.setNames(COLUMN_NAMES);
        lineMapper.setFieldSetMapper(new InvestmentsUploadFieldSetMapper());
        reader.setResource(new InputStreamResource(inStream));
        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(1);
        reader.setName("TransactionsCsvReader");
    	
    	return reader;
    }
}
