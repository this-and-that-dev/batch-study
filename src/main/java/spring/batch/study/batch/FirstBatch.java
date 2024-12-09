package spring.batch.study.batch;

import spring.batch.study.entity.AfterEntity;
import spring.batch.study.entity.BeforeEntity;
import spring.batch.study.repository.AfterRepository;
import spring.batch.study.repository.BeforeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class FirstBatch {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final BeforeRepository beforeRepository;
    private final AfterRepository afterRepository;

    @Bean
    public Job firstBatchJob() {
        //배치 이름
        return new JobBuilder("firstBatchJob", jobRepository)
                .start(firstBatchStep())
                .build();
    }

    @Bean
    public Step firstBatchStep() {
        //첫번째 인자 : step 이름
        //두번째 인자 : 트래킹할 jobRepository
        return new StepBuilder("firstBatchStep", jobRepository)
                .<BeforeEntity, AfterEntity> chunk(10, platformTransactionManager)
                .reader(beforeItemReader())
                .processor(middleItemProcessor())
                .writer(afterItemWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<BeforeEntity> beforeItemReader() {
        return new RepositoryItemReaderBuilder<BeforeEntity>()
                .name("beforeItemReader")
                .pageSize(10)
                .repository(beforeRepository) //before entity 를 읽어올 repository
                .methodName("findAll") //위에 repository 에서 설정한 repository에서 어떤 메소드를 읽은것인지
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();

    }

    //process : 읽어온 데이터를 처리하는 Process (큰 작업을 수행하지 않을 경우 생략가능, 지금과 같이 단순 이동은 사실 필요없다.)
    @Bean
    public ItemProcessor<BeforeEntity, AfterEntity> middleItemProcessor() {
        return item -> {
            AfterEntity afterEntity = new AfterEntity();
            afterEntity.setUsername(item.getUsername());
            return afterEntity;
        };
    }

    @Bean
    public RepositoryItemWriter<AfterEntity> afterItemWriter() {
        return new RepositoryItemWriterBuilder<AfterEntity>()
                .repository(afterRepository)
                .methodName("save")
                .build();
    }
}
