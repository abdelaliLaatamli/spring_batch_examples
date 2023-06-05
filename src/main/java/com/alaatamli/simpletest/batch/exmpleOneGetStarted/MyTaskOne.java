package com.alaatamli.simpletest.batch.exmpleOneGetStarted;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class MyTaskOne implements Tasklet  {
    @Override
    public RepeatStatus execute(
            StepContribution stepContribution,
            ChunkContext chunkContext
    ) throws Exception {
        System.out.println( "My task one started ...." );

        // your code

        Thread.sleep(3000);

        System.out.println( "My task one finished ...." );

        return RepeatStatus.FINISHED;
    }
}
