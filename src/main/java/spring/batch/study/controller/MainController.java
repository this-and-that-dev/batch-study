package spring.batch.study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @GetMapping("/first")
    @ResponseBody
    public String firstApi(@RequestParam String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value) //주로 일자를 넣는다. 같은 일자를 넣어서 다시 실행했을때 막을 수 있다.
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("firstBatchJob"), jobParameters);

        return "OK";
    }

    @GetMapping("/second")
    @ResponseBody
    public String secondApi(@RequestParam String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value) //주로 일자를 넣는다. 같은 일자를 넣어서 다시 실행했을때 막을 수 있다.
                .toJobParameters();

        jobLauncher.run(jobRegistry.getJob("secondBatchJob"), jobParameters);

        return "OK";
    }
}
