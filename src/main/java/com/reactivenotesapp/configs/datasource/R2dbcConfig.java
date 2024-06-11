package com.reactivenotesapp.configs.datasource;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;



import java.time.Duration;
import java.util.Collections;
import java.util.List;


@EnableR2dbcRepositories
public class R2dbcConfig extends AbstractR2dbcConfiguration {

  @Profile("test")
  @Bean
  public ConnectionFactory embeddedConnectionFactory(){
    H2ConnectionConfiguration h2Configuration = H2ConnectionConfiguration.builder()
            .username("user1")
            .password("50RandChars")
            .inMemory("testdb")
            .build();

    return new H2ConnectionFactory(h2Configuration);
  }

  @Profile("!test")
  @Bean
  public ConnectionFactory connectionFactory() {
    //TODO proxy for observation
    // r2dbc:proxy:pool:mysql://localhost:3306/my_database?proxyListener=com.example.MyListener
    //            .option(ConnectionFactoryOptions.DRIVER, "proxy")
    //            .option(ConnectionFactoryOptions.PROTOCOL, "pool:mysql") //ProxyConnectionFactoryProvider splits PROTOCOL -> driver:protocol
    //            .option(ProxyConnectionFactoryProvider.PROXY_LISTENERS, myListener)

    ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
            .option(ConnectionFactoryOptions.DRIVER, "mysql")
            .option(ConnectionFactoryOptions.HOST, "localhost")
            .option(ConnectionFactoryOptions.USER, "root")
            .option(ConnectionFactoryOptions.PASSWORD, "50RandChars")
            .option(ConnectionFactoryOptions.PORT, 3306)
            .option(ConnectionFactoryOptions.DATABASE, "my_database")
            .build();

    return ConnectionFactories.get(options);

  }
  
  @Bean(destroyMethod = "dispose")
  public ConnectionPool connectionPool(ConnectionFactory connectionFactory){

    ConnectionPoolConfiguration.Builder builder = ConnectionPoolConfiguration.builder(connectionFactory);
    // auto config defaults
    builder.maxIdleTime(Duration.ofMinutes(30));
    builder.initialSize(10);
    builder.maxSize(10);
    builder.validationDepth(ValidationDepth.LOCAL);
      
    return new ConnectionPool(builder.build());
  }

  @Override
  protected List<Object> getCustomConverters() {
    return Collections.emptyList();
    /* section 16.3; Example 87
        List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
    converterList.add(new org.springframework.data.r2dbc.test.PersonReadConverter());
    converterList.add(new org.springframework.data.r2dbc.test.PersonWriteConverter());
    return converterList;
     */
  }

}
