package com.alaatamli.simpletest.batch.exmple4forReadNotFromResource;

import com.alaatamli.simpletest.batch.exmple2TwoReadFromCSV.Employee;
import com.alaatamli.simpletest.batch.exmple3ThreeReadMultipleFile.ConsoleItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class BatchExampleFourConfiguration {

    @Autowired
    @Lazy
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    @Lazy
    StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobFour(){
        return jobBuilderFactory
                .get("readCSVFilesJob")
                .incrementer(new RunIdIncrementer())
                .start(stepTest())
                .build();
    }

    @Bean
    public Step stepTest(){
        return stepBuilderFactory
                .get("step 1")
                .<Employee,Employee>chunk(5)
                .reader(exmpleFourReader())
                .writer(exmpleFourWritter())
                .build();
    }

    @Bean
    public FlatFileItemReader<Employee> exmpleFourReader(){
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<Employee>();
        reader.setResource(new FileSystemResource("input/inputData.csv"));

        reader.setLinesToSkip(1);

        reader.setLineMapper( new DefaultLineMapper<>(){
            {
                setLineTokenizer(new DelimitedLineTokenizer(){
                    {
                        setNames(new String[]{ "id", "firstName", "lastName" });
                    }
                });

                setFieldSetMapper( new BeanWrapperFieldSetMapper<Employee>(){
                    {
                        setTargetType(Employee.class);
                    }
                });
            }
        });


        return reader;
    }

    @Bean
    public ConsoleItemWriter<Employee> exmpleFourWritter(){
        return new ConsoleItemWriter<Employee>();
    }


}
