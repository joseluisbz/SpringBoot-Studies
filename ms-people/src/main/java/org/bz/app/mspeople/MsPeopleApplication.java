package org.bz.app.mspeople;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class MsPeopleApplication {
    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(MsPeopleApplication.class, args);
        displayAllBeans();
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            log.info("CommandLineRunner del Microservicio.");
        };
    }

    public static void displayAllBeans() {
        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : allBeanNames) {
            log.info("beanName: " + beanName);
        }
    }

}
