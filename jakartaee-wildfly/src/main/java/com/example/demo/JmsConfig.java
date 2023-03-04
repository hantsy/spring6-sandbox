package com.example.demo;


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

//    @Bean
//    public CachingConnectionFactory connectionFactory() {
//        return new CachingConnectionFactory(
//            new ActiveMQConnectionFactory("tcp://localhost:6161", "user", "password")
//        );
//    }

    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String CONNECTION_FACTORY = "java:comp/env/jms/RemoteConnectionFactory";

    private static final String brokerUrl = "tcp://localhost:6161";
    private static final String username = "user";
    private static final String password = "password";

    @Bean
    public ConnectionFactory connectionFactory() {
        try {
            JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
            jndiObjectFactoryBean.setJndiName(CONNECTION_FACTORY);

            jndiObjectFactoryBean.setJndiEnvironment(getEnvProperties());
            jndiObjectFactoryBean.afterPropertiesSet();

            return (QueueConnectionFactory) jndiObjectFactoryBean.getObject();

        } catch (NamingException e) {
            System.out.println("Error while retrieving JMS queue with JNDI name: [" + CONNECTION_FACTORY + "]");
        } catch (Exception ex) {
            System.out.println("Error");
        }
        return null;
    }

    Properties getEnvProperties() {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, brokerUrl);
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);
        return env;
    }


    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        //factory.setDestinationResolver(destinationResolver());
        factory.setSessionTransacted(true);
        factory.setConcurrency("5");
        return factory;
    }


    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(connectionFactory());
    }

}
