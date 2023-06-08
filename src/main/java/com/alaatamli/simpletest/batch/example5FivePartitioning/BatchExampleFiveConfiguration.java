package com.alaatamli.simpletest.batch.example5FivePartitioning;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class BatchExampleFiveConfiguration {


    @Autowired
    @Lazy
    JobBuilderFactory jobBuilderFactory;


    @Autowired
    @Lazy
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Bean
    public Job jobFive(){
        return jobBuilderFactory.get("job Five")
                .start(stepFive())
                .build();
    }


    @Bean
    public Step stepFive(){
        return stepBuilderFactory
                .get("step Example Five")
                .partitioner( workerStep().getName()  , partitioner())
                .step( workerStep() )
                .gridSize(4)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public ColumnRangePartitioner partitioner(){
        ColumnRangePartitioner columnRangePartitioner = new ColumnRangePartitioner();
        columnRangePartitioner.setColumn("id");
        columnRangePartitioner.setDataSource(dataSource);
        columnRangePartitioner.setTable("customer");
        return columnRangePartitioner;
    }

    @Bean
    public Step workerStep(){
        return stepBuilderFactory
                .get("workerStep")
                .<Customer,Customer>chunk(1000)
                .reader(pagingItemReader(null,null))
                .writer(customerItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Customer> pagingItemReader(
            @Value("#{stepExecutionContext['minValue']}") Long minValue ,
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue
    ){
        System.out.println("Reading "+ minValue + " to "+ maxValue);
        Map< String , Order> sortKey = new HashMap<>();
        sortKey.put("id",Order.ASCENDING);

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, firstName, lastName, birthdate");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause(" where id >= " + minValue + " and id < " + maxValue );
        queryProvider.setSortKeys( sortKey );

        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(this.dataSource);
        reader.setFetchSize(1000);
        reader.setRowMapper(new CustomerRowMapper());
        reader.setQueryProvider(queryProvider);

        return reader;
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Customer> customerItemWriter(){
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO NEW_CUSTOMER VALUES (:id, :firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(
                new BeanPropertyItemSqlParameterSourceProvider<>()
        );
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

}
