package cn.gehc.cpm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RccHisApplication {

	public static void main(String[] args) {
		SpringApplication.run(RccHisApplication.class, args);
	}

}
