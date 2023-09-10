package com.chosu.springbatchbasic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
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

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@Slf4j
public class NaverIPOBatchJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final NaverIPOService ipoService;

    private final NaverIPORepository ipoRepository;

    private final EntityManagerFactory entityManagerFactory;

    private final DataSource dataSource;

    public NaverIPOBatchJob(JobBuilderFactory jobBuilderFactory
                            , StepBuilderFactory stepBuilderFactory
                            , NaverIPOService ipoService
                            , NaverIPORepository ipoRepository
                            , EntityManagerFactory entityManagerFactory
                            , DataSource dataSource
                            ){
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.ipoService = ipoService;
        this.ipoRepository = ipoRepository;
        this.entityManagerFactory = entityManagerFactory;
        this.dataSource = dataSource;
    }


    @Bean
    public Job chunkProcessingJob() throws Exception {
        return jobBuilderFactory.get("chunkProcessingJob")
                .incrementer(new RunIdIncrementer())
                .start(this.initTableStep())
                .next(this.getNaverIPOListStep())
                .build();
    }

    @Bean
    @JobScope
    public Step initTableStep(){
        return stepBuilderFactory.get("initNaverIPOListStep")
        .tasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                // 비즈니스 로직 작성
                // 현재 날짜 구하기
                // 결과 출력
                String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                System.out.println(today);  // 2021/06/17
                int result = ipoRepository.deleteByRegistDate(today);
                return RepeatStatus.FINISHED;
            }
        })
        .build();
    }


    @Bean
    @JobScope
    public Step getNaverIPOListStep() throws Exception {
        log.info("getNaverIPOListStep Start!!");
        return stepBuilderFactory.get("getNaverIPOListStep")
                .<NaverIPODto, NaverIPODto> chunk(10000)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    private ItemWriter<NaverIPODto> itemWriter() {

        JdbcBatchItemWriter<NaverIPODto> itemWriter = new JdbcBatchItemWriterBuilder<NaverIPODto>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("insert \n" +
                        "        into\n" +
                        "            naver_ipo_info\n" +
                        "            (id, compCategory, compName, competition, gongmoComp, gongmoPrice, gongmoState, listingDate, marketName, requestTerm, registDate, registTime) \n" +
                        "        values \n"+
                        "            (:id, :compCategory, :compName, :competition, :gongmoComp, :gongmoPrice, :gongmoState, :listingDate, :marketName, :requestTerm, :registDate, :registTime)")
                .build();

        itemWriter.afterPropertiesSet();

        return itemWriter;
    }



    private ItemProcessor<NaverIPODto,NaverIPODto> itemProcessor() {
        log.info("itemProcessor Start!!");

        return item -> item;
    }

    private ItemReader<? extends NaverIPODto> itemReader() {
        List<NaverIPODto> list = getItems();
        log.info("list.size() ->" + list.size());
        ListItemReader<? extends NaverIPODto> listItemReader = new ListItemReader<>(list);
        return listItemReader;
    }

    private List<NaverIPODto> getItems() {
        log.info("getItems Start!!");
        return ipoService.getNaverIPOList();
    }

}
