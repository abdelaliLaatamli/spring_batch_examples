package com.alaatamli.simpletest.controllers;

import org.springframework.batch.core.Job;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    @Qualifier("jobOne")
    Job jobOne;

    @Autowired
    @Qualifier("jobTwo")
    Job jobTwo;
    @GetMapping()
    public ResponseEntity<String> getTest(){
        return ResponseEntity.ok( "test is works now !!" );
    }

    @GetMapping("/ex1")
    public ResponseEntity<String> startExampleOne() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        JobParameters params = new JobParametersBuilder()
                .addString("JobId" , String.valueOf(System.currentTimeMillis()))
                .toJobParameters();

        jobLauncher.run(jobOne , params);

        return ResponseEntity.ok("Batch Example 1 done ... ");
    }

    @GetMapping("/ex2")

    public ResponseEntity<String> startExampleTwo() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters parameters = new JobParametersBuilder()
                .addString("jobId" , String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run( jobTwo , parameters );
        return ResponseEntity.ok("Batch CSV example 2 is done !!!");
    }

}
