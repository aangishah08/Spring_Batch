package com.softvan.job;

import com.google.gson.Gson;
import com.softvan.entity.Student;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
@Configuration
public class ItemProcessors {
    public ItemProcessor<Student, Student> itemProcessor() {
        return student -> student;

    }
    @Bean
    public AsyncItemProcessor asyncItemProcessor() throws Exception {
        AsyncItemProcessor<Student, Student> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(itemProcessor());
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return asyncItemProcessor;
    }
}
