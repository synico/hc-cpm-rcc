package cn.gehc.cpm;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author Nick Liu
 */

@SpringBootApplication
@ImportResource(locations = "datasource/*.xml")
@EnableScheduling
public class DataMockApplication implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        Executor taskExecutor = new ScheduledThreadPoolExecutor(2);
        return taskExecutor;
    }

    public static void main(String[] args) {
        SpringApplication.run(DataMockApplication.class);
    }
}
