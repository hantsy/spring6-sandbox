package com.example.demo;


import jakarta.annotation.Resource;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.QueueConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Properties;

@Configuration
@EnableJms
public class JmsConfig {

    @Resource(lookup = "java:comp/DefaultJMSConnectionFactory")
    ConnectionFactory connectionFactory;

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //factory.setDestinationResolver(destinationResolver());
        factory.setSessionTransacted(true);
        factory.setConcurrency("5");
        return factory;
    }


    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(connectionFactory);
    }

}
