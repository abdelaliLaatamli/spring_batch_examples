package com.alaatamli.simpletest.batch.exmple1;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchExampleOneConfig {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;


    @Bean
    public Step stepOne(){
        return steps.get("stepOne").tasklet(new MyTaskOne()).build();
    }

    @Bean
    public Step stepTwo(){
        return steps.get("stepTwo").tasklet(new MyTaskTwo()).build();
    }

    @Bean
    public Job exampleOneJob(){
        return jobs.get("exmpleOne")
                .incrementer(new RunIdIncrementer())
                .start(stepOne())
                .next(stepTwo())
                .build();
    }

}
