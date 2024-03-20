package org.bz.eggprice.mspeople;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class MsPeopleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsPeopleApplication.class, args);
	}

}
