package org.bz.app.mspeople;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class MsPeopleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsPeopleApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            log.info("CommandLineRunner del Microservicio.");
        };
    }

}
