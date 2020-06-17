package cn.gehc.ii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author 212706300
 */

@SpringBootApplication
@ImportResource(locations = "datasource.xml")
public class IIRisApplication {

    public static void main(String[] args) {
        SpringApplication.run(IIRisApplication.class, args);
    }

}
