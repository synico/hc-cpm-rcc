package cn.gehc.cpm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations = "datasource/*.xml")
public class DataMockApplication {

    public static void main(String args[]) {
        SpringApplication.run(DataMockApplication.class);
    }
}
