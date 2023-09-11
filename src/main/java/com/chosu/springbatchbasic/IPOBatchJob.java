package com.chosu.springbatchbasic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@Slf4j
public class IPOBatchJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final IPOService ipoService;

    private final IPORepository ipoRepository;

    private final DataSource dataSource;

    public IPOBatchJob(JobBuilderFactory jobBuilderFactory
                            , StepBuilderFactory stepBuilderFactory
                            , IPOService ipoService
                            , IPORepository ipoRepository
                            , DataSource dataSource
                            ){
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.ipoService = ipoService;
        this.ipoRepository = ipoRepository;
        this.dataSource = dataSource;
    }


    @Bean
    public Job chunkProcessingJob() throws Exception {
        return jobBuilderFactory.get("chunkProcessingJob")
                .incrementer(new RunIdIncrementer())
                .start(this.initTableStep())
                .next(this.getIPOListStep())
                .build();
    }

    @Bean
    @JobScope
    public Step initTableStep(){
        return stepBuilderFactory.get("initTableStep")
        .tasklet((contribution, chunkContext) -> {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            log.info("today : {}", today);
            ipoRepository.deleteByRegistDate(today);
            return RepeatStatus.FINISHED;
        })
        .build();
    }


    @Bean
    @JobScope
    public Step getIPOListStep() {
        log.info("getIPOListStep Start!!");
        return stepBuilderFactory.get("getIPOListStep")
                .<IPODto, IPODto> chunk(10000)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    private ItemWriter<IPODto> itemWriter() {

        JdbcBatchItemWriter<IPODto> itemWriter = new JdbcBatchItemWriterBuilder<IPODto>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("insert \n" +
                        "        into\n" +
                        "            ipo_info\n" +
                        "            (id, compCategory, compName, competition, gongmoComp, gongmoPrice, gongmoState, listingDate, marketName, requestTerm, registDate, registTime) \n" +
                        "        values \n"+
                        "            (:id, :compCategory, :compName, :competition, :gongmoComp, :gongmoPrice, :gongmoState, :listingDate, :marketName, :requestTerm, :registDate, :registTime)")
                .build();

        itemWriter.afterPropertiesSet();

        return itemWriter;
    }



    private ItemProcessor<IPODto, IPODto> itemProcessor() {
        log.info("itemProcessor Start!!");
        return item -> item;
    }

    private ItemReader<? extends IPODto> itemReader() {
        log.info("itemReader Start!!");
        return new ListItemReader<>(getItems());
    }

    private List<IPODto> getItems() {
        log.info("getItems Start!!");
        return ipoService.getNaverIPOList();
    }

}
