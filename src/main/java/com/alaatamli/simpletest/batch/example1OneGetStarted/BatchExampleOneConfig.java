package com.alaatamli.simpletest.batch.example1OneGetStarted;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

@Configuration
public class BatchExampleOneConfig {

    @Autowired
    @Lazy
    private JobBuilderFactory jobs;

    @Autowired
    @Lazy
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
    @Primary
    public Job jobOne(){
        return jobs.get("exmpleOne")
                .incrementer(new RunIdIncrementer())
                .start(stepOne())
                .next(stepTwo())
                .build();
    }

}
