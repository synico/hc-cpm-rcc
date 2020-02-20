package cn.gehc.cpm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ImportResource(locations = "datasource/*.xml")
@EnableScheduling
public class DataMockApplication {

    public static void main(String args[]) {
        SpringApplication.run(DataMockApplication.class);
    }
}
