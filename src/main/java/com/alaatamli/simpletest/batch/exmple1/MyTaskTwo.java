package com.alaatamli.simpletest.batch.exmple1;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class MyTaskTwo implements Tasklet {
    @Override
    public RepeatStatus execute(
            StepContribution stepContribution,
            ChunkContext chunkContext
    ) throws Exception {
        System.out.println( "My task two started ...." );

        // code task

        Thread.sleep(3000);

        System.out.println( "My task two finished ...." );

        return RepeatStatus.FINISHED;
    }
}
