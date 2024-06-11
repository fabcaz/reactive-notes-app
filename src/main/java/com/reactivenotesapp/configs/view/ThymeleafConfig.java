package com.reactivenotesapp.configs.view;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.unit.DataSize;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;

@Configuration
public class ThymeleafConfig implements ApplicationContextAware {


  @Nullable
  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(@Nullable ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Bean
  public SpringResourceTemplateResolver templateResolver(){

    SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
    templateResolver.setApplicationContext(this.applicationContext);
    templateResolver.setPrefix("/WEB-INF/Templates");
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode(TemplateMode.HTML);
    templateResolver.setCacheable(true);
    return templateResolver;
  }

  @Bean
  public SpringWebFluxTemplateEngine springWebFluxTemplateEngine(){
    SpringWebFluxTemplateEngine engine = new SpringWebFluxTemplateEngine();
    engine.setEnableSpringELCompiler(true);
    engine.addTemplateResolver(templateResolver());
    engine.addDialect(new StandardDialect());//is this correct???
    return engine;
  }

  @Bean
  public ThymeleafReactiveViewResolver thymeleafReactiveViewResolver(){
    ThymeleafReactiveViewResolver resolver = new ThymeleafReactiveViewResolver();
    resolver.setTemplateEngine(springWebFluxTemplateEngine());
    resolver.setDefaultCharset(StandardCharsets.UTF_8);
    //TODO look into the viewname settings
//    resolver.setExcludedViewNames(properties.getExcludedViewNames());
//    resolver.setViewNames(properties.getViewNames());

    //resolver.setSupportedMediaTypes(); //default is null; viewresolversupport has default val of text/html
    resolver.setResponseMaxChunkSizeBytes(0); // default is DataSize.ofBytes(0)
    // This resolver acts as a fallback resolver (e.g. like a
    // InternalResourceViewResolver) so it needs to have low precedence
    resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 5);
    return resolver;
  }




}
