package cn.gehc.cpm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author 212706300
 */

@SpringBootApplication
@ImportResource(locations = "datasource/*.xml")
public class RccDosewatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(RccDosewatchApplication.class, args);
	}

}
