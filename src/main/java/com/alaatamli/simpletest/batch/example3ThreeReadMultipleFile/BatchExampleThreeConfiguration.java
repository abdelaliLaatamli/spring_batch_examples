package com.alaatamli.simpletest.batch.example3ThreeReadMultipleFile;

import com.alaatamli.simpletest.batch.example2TwoReadFromCSV.Employee;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;

@Configuration
public class BatchExampleThreeConfiguration {


    @Autowired
    @Lazy
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    @Lazy
    private StepBuilderFactory stepBuilderFactory;

//    @Value("classPath:/input/ex3/*.c/*/sv")
//    @Value("classPath:/input/ex3/*.csv")
//    private Resource[] inputResources;
    @Value("classpath:/input/ex3/*.csv")
    private Resource[] inputResources;

    @Autowired
    @Lazy
    private FlatFileItemReader<Employee> reader;


    @Bean
    public Job jobThree(){
        return jobBuilderFactory
                .get("readCSVFilesJob")
                .incrementer( new RunIdIncrementer())
                .start(step1())
                .build();
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1").<Employee,Employee>chunk(3)
                .reader(multipleResourceItemReader())
                .writer(writterOnConsole())
                .build();
    }

    @Bean
    public MultiResourceItemReader<Employee> multipleResourceItemReader(){
        MultiResourceItemReader<Employee> resourceItemReader = new MultiResourceItemReader<Employee>();
        resourceItemReader.setResources(inputResources);
        resourceItemReader.setDelegate( reader );
        return resourceItemReader;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Bean
    FlatFileItemReader<Employee> readerOneFile(){

        //Create reader instance
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<Employee>();
        //Set number of lines to skips. Use it if file has header rows.
        reader.setLinesToSkip(1);
        //Configure how each line will be parsed and mapped to different values
        reader.setLineMapper(new DefaultLineMapper() {
            {
                //3 columns in each row
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[] { "id", "firstName", "lastName" });
                    }
                });
                //Set values in Employee class
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {
                    {
                        setTargetType(Employee.class);
                    }
                });
            }
        });
        return reader;

    }

    @Bean
    public ConsoleItemWriter<Employee> writterOnConsole(){
        return new ConsoleItemWriter<Employee>();
    }


}
