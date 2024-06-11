package com.reactivenotesapp.configs.webflux;

import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver;

public class WebFluxConfig extends WebFluxConfigurationSupport {

  @Override
  protected void configureHttpMessageCodecs(ServerCodecConfigurer serverCodecConfigurer){

    Decoder<?> jackson2JsonDecoder = getApplicationContext().getBean("jackson2JsonDecoder", Jackson2JsonDecoder.class);
    Encoder<?> jackson2JsonEncoder = getApplicationContext().getBean("jackson2JsonEncoder", Jackson2JsonEncoder.class);
    serverCodecConfigurer.defaultCodecs().jackson2JsonDecoder(jackson2JsonDecoder);
    serverCodecConfigurer.defaultCodecs().jackson2JsonEncoder(jackson2JsonEncoder);
  }

  @Override
  protected void configureViewResolvers(ViewResolverRegistry registry) {

    ViewResolver thymeleafViewResolver = getApplicationContext().getBean("thymeleafReactiveViewResolver", ThymeleafReactiveViewResolver.class);
    registry.viewResolver(thymeleafViewResolver);
  }
}
