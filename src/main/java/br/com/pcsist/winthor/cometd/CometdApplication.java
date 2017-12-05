package br.com.pcsist.winthor.cometd;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.cometd.annotation.AnnotationCometDServlet;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.server.BayeuxServerImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import br.com.pcsist.winthor.cometd.service.CometService;

@SpringBootApplication
public class CometdApplication extends SpringBootServletInitializer implements WebApplicationInitializer {

  public static void main(String[] args) {
    SpringApplication.run(CometdApplication.class, args);
  }
  
  @Bean
  public WebMvcConfigurer corsConfigurer() {
      return new WebMvcConfigurerAdapter() {
          @Override
          public void addCorsMappings(CorsRegistry registry) {
              registry.addMapping("/cometd/**").allowedOrigins("http://localhost:8080");
          }
      };
  }
  
  @Bean
  public ServletRegistrationBean servletRegistrationBean() {
    final AnnotationCometDServlet cometdServlet = new AnnotationCometDServlet();
    
    ServletRegistrationBean sr = new ServletRegistrationBean(cometdServlet, "/cometd/*");
    sr.addInitParameter("services", CometService.class.getName());
    
    return sr;
  }
  
  @Bean
  public ServletContextInitializer servletInitializer() {
    return new ServletContextInitializer() {
      @Override
      public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.setAttribute(BayeuxServer.ATTRIBUTE, bayeuxServer(servletContext));
      }
    };
  }
  
  @Bean
  @DependsOn("servletInitializer")
  public BayeuxServer bayeuxServer(ServletContext servletContext) {
    final BayeuxServerImpl bean = new BayeuxServerImpl();
    bean.addExtension(new org.cometd.server.ext.AcknowledgedMessagesExtension());
    bean.setOption(ServletContext.class.getName(), servletContext);
    bean.setOption("ws.cometdURLMapping", "/cometd/*");
    
    // bean.setTransports(Arrays.asList(new WebSocketTransport(bean)));
    
    return bean;
  }
}
