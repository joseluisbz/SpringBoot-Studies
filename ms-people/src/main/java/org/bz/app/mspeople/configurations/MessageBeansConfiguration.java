package org.bz.app.mspeople.configurations;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class MessageBeansConfiguration {

    @Bean
    public ResourceBundleMessageSource resourceBundleMessageSource() {
        // Alternative to messageSource() Bean
        var source = new ResourceBundleMessageSource();
        source.setBasenames("messages/custom-messages");
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }

    @Bean("customMessageSource")
    public MessageSource customMessageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages/custom-messages");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean getValidator() {
        // Unfortunately doesn't work
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(customMessageSource());
        return bean;
    }

}
