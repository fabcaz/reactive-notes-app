package com.reactivenotesapp;


import com.reactivenotesapp.domain.BaseNoteEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import reactor.netty.http.server.HttpServer;

public class ReactiveNotesApp {
    private static final Log log = LogFactory.getLog(ReactiveNotesApp.class);

    public static void main(String[]args){
      //BaseEntity be = new BaseEntity();
      //log.debug(be);
//      ApplicationContext ctx = new FileSystemXmlApplicationContext("src/main/java/com/reactivenotesapp/configs/application.xml"); // works but want to get from jar
      ApplicationContext ctx = new ClassPathXmlApplicationContext("configs/application.xml", ReactiveNotesApp.class);// works; no idea why. This seems to get from jar and docstring says so
//      ApplicationContext ctx = new ClassPathXmlApplicationContext("file:/home/fc/JavaProj/reactive-notes-app/src/main/java/com/reactivenotesapp/configs/application.xml");
      BaseNoteEntity be = ctx.getBean(BaseNoteEntity.class);
      log.debug("BE.url: " + be.getUrl());
      log.debug("BE.name: " + be.getName());
      log.debug("let the note taking begin");

      String host = "localhost";
      int port = 8081;
      // section 1.2.1 netty example & section 1.3 WebHttpHandlerBuilder example

//      ApplicationContext context = ...
      HttpHandler handler = WebHttpHandlerBuilder.applicationContext(ctx).build();
      ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(handler);
      HttpServer.create().host(host).port(port).handle(adapter).bind().block();


    }

}
