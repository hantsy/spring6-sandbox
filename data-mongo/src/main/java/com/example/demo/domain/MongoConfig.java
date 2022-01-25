package com.example.demo.domain;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.connection.ConnectionPoolSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
@PropertySource("classpath:mongo.properties")
/*public class MongoConfig {

    @Value("${mongo.uri}")
    private String mongoUri;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient client) {
        return new MongoTemplate(client, "blogdb");
    }
}*/
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Value("${mongo.uri}")
    private String mongoUri;

    @Override
    @Bean
    public MongoClient mongoClient() {
        return super.mongoClient();
    }

    @Override
    protected String getDatabaseName() {
        return "blogdb";
    }

    @Override
    protected void configureConverters(MongoCustomConversions.MongoConverterConfigurationAdapter adapter) {
        //add extra converters
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder
                //.uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(new ConnectionString(mongoUri))
                .applyToConnectionPoolSettings(pool -> pool
                        .applySettings(
                                ConnectionPoolSettings.builder()
                                        .maxConnecting(5)
                                        .build()
                        )
                );
//        builder
//                .credential(MongoCredential.createCredential("name", "db", "pwd".toCharArray()))
//                .applyToClusterSettings(settings  -> {
//                    settings.hosts(singletonList(new ServerAddress("127.0.0.1", 27017)));
//                });
    }

    // GridFs support
    @Bean
    public GridFsTemplate gridFsTemplate(MongoDatabaseFactory mongoDbFactory, MappingMongoConverter mongoConverter) {
        return new GridFsTemplate(mongoDbFactory, mongoConverter);
    }
}
