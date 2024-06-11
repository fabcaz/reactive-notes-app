package com.reactivenotesapp.configs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.MimeType;

//TODO
@Configuration
public class Jackson2Config {

  @Bean
  public ObjectMapper objectMapper() {
    var builder = Jackson2ObjectMapperBuilder.json();
    builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
    builder.featuresToDisable(
            SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
            SerializationFeature.FAIL_ON_EMPTY_BEANS,
            DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    builder.featuresToEnable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    builder.modulesToInstall(JavaTimeModule.class);
    return builder.build();
  }


  //jackson2 codecs are created as a customizer in CodecsAutoConfiguration
  //but will create create codecs manually instead of relying on ServerCodecConfigurer
  private static final MimeType[] EMPTY_MIME_TYPES = {};

  @Bean
  public Jackson2JsonDecoder jackson2JsonDecoder(ObjectMapper objectMapper){
    return new Jackson2JsonDecoder(objectMapper, EMPTY_MIME_TYPES);
  }

  @Bean
  public Jackson2JsonEncoder jackson2JsonEncoder(ObjectMapper objectMapper){
    return new Jackson2JsonEncoder(objectMapper, EMPTY_MIME_TYPES);
  }
}
