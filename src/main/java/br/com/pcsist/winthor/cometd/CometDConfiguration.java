package br.com.pcsist.winthor.cometd;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.cometd.annotation.ServerAnnotationProcessor;
import org.cometd.bayeux.server.BayeuxServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.web.context.ServletContextAware;

public class CometDConfiguration implements DestructionAwareBeanPostProcessor, ServletContextAware {
  
  private BayeuxServer bayeuxServer;
  private ServerAnnotationProcessor processor;

  @Inject
  private void setBayeuxServer(BayeuxServer bayeuxServer) {
      this.bayeuxServer = bayeuxServer;
  }
  
  @PostConstruct
  private void init() {
      this.processor = new ServerAnnotationProcessor(bayeuxServer);
  }
  
  @Override
  public void setServletContext(ServletContext servletContext) {
    servletContext.setAttribute(BayeuxServer.ATTRIBUTE, bayeuxServer);
  }
  
  @Override
  public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
      processor.processDependencies(bean);
      processor.processConfigurations(bean);
      processor.processCallbacks(bean);
      return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String name) throws BeansException {
      return bean;
  }

  @Override
  public boolean requiresDestruction(Object bean) {
      return true;
  }

  @Override
  public void postProcessBeforeDestruction(Object bean, String name) throws BeansException {
      processor.deprocessCallbacks(bean);
  }
}
