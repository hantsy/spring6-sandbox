package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.*;

@Configuration
public class QuartzConfig {

    @Autowired
    Greeting greeting;

    @Bean
    JobDetailFactoryBean counterJobDetailFactoryBean() {
        var jobFactory = new JobDetailFactoryBean();
        jobFactory.setJobClass(Counter.class);
        return jobFactory;
    }

    @Bean
    MethodInvokingJobDetailFactoryBean greetingJobDetailFactoryBean() {
        var jobFactory = new MethodInvokingJobDetailFactoryBean();
        //jobFactory.setTargetBeanName("greeting");
        jobFactory.setTargetObject(greeting);
        jobFactory.setTargetMethod("sayHello");
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        var factory = new SchedulerFactoryBean();
        factory.setTriggers(
                simpleTriggerFactoryBean().getObject(),
                cronTriggerFactoryBean().getObject()
        );
        return factory;
    }

    @Bean
    public SimpleTriggerFactoryBean simpleTriggerFactoryBean() {
        SimpleTriggerFactoryBean simpleTrigger = new SimpleTriggerFactoryBean();
        simpleTrigger.setJobDetail(greetingJobDetailFactoryBean().getObject());
        simpleTrigger.setStartDelay(1_000);
        simpleTrigger.setRepeatInterval(5_000);
        return simpleTrigger;
    }

    @Bean
    public CronTriggerFactoryBean cronTriggerFactoryBean() {
        CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
        cronTrigger.setJobDetail(counterJobDetailFactoryBean().getObject());
        cronTrigger.setCronExpression("*/5 * * * * ?");
        return cronTrigger;
    }
}
