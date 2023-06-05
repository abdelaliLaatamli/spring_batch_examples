package com.alaatamli.simpletest.batch.exmple2TwoReadFromCSV;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class BatchExampleTwoConfiguration {

    @Autowired
    @Lazy
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    @Lazy
    private StepBuilderFactory stepBuilderFactory;

    @Value("classPath:/input/exampleTwoFile.csv")
    private Resource inputResource;

    @Bean
    public Job jobTwo(){
        return jobBuilderFactory
                .get("readCSVFileJob")
                .incrementer( new RunIdIncrementer())
                .start(step())
                .build();
    }

    @Bean
    public Step step(){
        return stepBuilderFactory.get("step")
                .<Employee, Employee>chunk(2)
                .reader(reader())
                .processor(processor())
                .writer(writter())
                .build();
    }

    @Bean
    public FlatFileItemReader<Employee> reader(){
        FlatFileItemReader<Employee> itemReader = new FlatFileItemReader<Employee>();
        itemReader.setLineMapper( lineMapper() );
        itemReader.setLinesToSkip(1);
        itemReader.setResource(inputResource);
        return itemReader;
    }

    @Bean
    public LineMapper<Employee> lineMapper(){
        DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<Employee>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(new String[]{"id","firstName","lastName"});
        lineTokenizer.setIncludedFields(new int[]{ 0 , 1 , 2 });
        BeanWrapperFieldSetMapper<Employee> fieldSetMapper = new BeanWrapperFieldSetMapper<Employee>();
        fieldSetMapper.setTargetType(Employee.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }


    @Bean public ItemProcessor<Employee, Employee> processor(){
        return new DBLogProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Employee> writter(){
        JdbcBatchItemWriter<Employee> itemWriter = new JdbcBatchItemWriter<Employee>();
        itemWriter.setDataSource(dataSource());
        itemWriter.setSql(" INSERT INTO EMPLOYEE ( ID, FIRSTNAME, LASTNAME ) Values (:id, :firstName, :lastName)");
        itemWriter.setItemSqlParameterSourceProvider( new BeanPropertyItemSqlParameterSourceProvider<Employee>());
        return itemWriter;
    }

    @Bean
    public DataSource dataSource(){
        EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
        return embeddedDatabaseBuilder
                .addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
                .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
                .addScript("classPath:employee.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

}
