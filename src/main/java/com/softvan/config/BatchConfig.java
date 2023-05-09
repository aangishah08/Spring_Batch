package com.softvan.config;

import com.softvan.entity.Student;
import com.softvan.job.ItemProcessors;
import com.softvan.job.ItemReader;
import com.softvan.job.ItemWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ItemReader itemReader;
    private  final ItemProcessors itemProcessor;
    private final ItemWriter itemWriter;

    @SuppressWarnings("unchecked")
    @Bean
    public Step step() throws Exception {
        return stepBuilderFactory.get("step").<Student, Student>chunk(1000)
                .reader(itemReader.reader())
                .processor(itemProcessor.asyncItemProcessor()).writer(itemWriter.writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job CsvBatch() throws Exception {
        return jobBuilderFactory.get("csv_batch").incrementer(new RunIdIncrementer()).start(step()).build();

    }
    @Bean
    public TaskExecutor taskExecutor() {
        //ThreadPoolTaskExecutor , we looked at the corePoolSize and maxPoolSize properties,
        // as well as how maxPoolSize works in tandem with queueCapacity, allowing us to easily create thread pools for any use case.
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(100);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.setThreadNamePrefix("CBS-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}
