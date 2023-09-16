package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.GenericContainer;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringJUnitConfig(value = {JmsTest.TestConfig.class})
@ContextConfiguration(initializers = {JmsTest.ArtemisContainerInitializer.class})
@Slf4j
public class JmsTest {

    @Configuration
    @ComponentScan(basePackageClasses = Sender.class)
    @Import(value = {JmsConfig.class, Sender.class, Receiver.class})
    static class TestConfig {
    }

    static class ArtemisContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        final static String DOCKER_IMAGE_NAME = "quay.io/artemiscloud/activemq-artemis-broker";
        final static Integer DEFAULT_EXPOSED_PORT = 61616;
        final GenericContainer container = new GenericContainer(DOCKER_IMAGE_NAME)
                .withEnv(Map.of("AMQ_USER", "user", "AMQ_PASSWORD", "password"))
                .withExposedPorts(DEFAULT_EXPOSED_PORT);


        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            container.start();
            applicationContext.addApplicationListener(event -> {
                        if (event instanceof ContextClosedEvent) {
                            container.stop();
                        }
                    }
            );

            var brokerUrlFormat = "tcp://%s:%d";
            var brokerUrl = brokerUrlFormat.formatted(container.getHost(), container.getFirstMappedPort());
            log.debug("connection url is {}", brokerUrl);

            applicationContext.getEnvironment()
                    .getPropertySources()
                    .addLast(
                            new MapPropertySource("activemqProps",
                                    Map.of("activemq.brokerUrl", brokerUrl)
                            )
                    );
        }
    }

    @Autowired
    Sender sender;

    @Autowired
    Receiver receiver;

    @Test
    public void whenWaitOneSecond_thenReceiverShouldReceiveAllMessages() {
        sender.send();

        // wait one second to verify.
        await().atMost(Duration.ofSeconds(1)).untilAsserted(() -> assertThat(receiver.getMessageList().size()).isEqualTo(10));
    }
}
