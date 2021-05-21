package cn.gehc.ii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ImportResource(locations = "datasource.xml")
@EntityScan(basePackages = {"cn.gehc.cpm.domain", "cn.gehc.ii.domain"})
@EnableJpaRepositories(basePackages = {"cn.gehc.ii.repository", "cn.gehc.cpm.repository"})
public class IISchedulersApplication {

    public static void main(String[] args) {
        SpringApplication.run(IISchedulersApplication.class);
    }
}
