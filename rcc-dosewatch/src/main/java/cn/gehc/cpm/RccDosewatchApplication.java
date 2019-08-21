package cn.gehc.cpm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations = "datasource/*.xml")
public class RccDosewatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(RccDosewatchApplication.class, args);
	}

}
