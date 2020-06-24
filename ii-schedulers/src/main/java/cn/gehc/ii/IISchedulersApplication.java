package cn.gehc.ii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations = "datasource.xml")
public class IISchedulersApplication {

    public static void main(String[] args) {
        SpringApplication.run(IISchedulersApplication.class);
    }
}
