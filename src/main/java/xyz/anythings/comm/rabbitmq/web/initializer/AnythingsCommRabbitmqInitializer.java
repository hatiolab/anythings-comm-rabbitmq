/* Copyright © HatioLab Inc. All rights reserved. */
package xyz.anythings.comm.rabbitmq.web.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import xyz.anythings.comm.rabbitmq.config.AnythingsCommRabbitmqModuleProperties;
import xyz.elidom.sys.config.ModuleConfigSet;
import xyz.elidom.sys.system.service.api.IEntityFieldCache;
import xyz.elidom.sys.system.service.api.IServiceFinder;

/**
 * Anythings Communication Rabbitmq Startup시 Framework 초기화 클래스 
 * 
 * @author yang
 */
@Component
public class AnythingsCommRabbitmqInitializer {

	/**
	 * Logger
	 */
	private Logger logger = LoggerFactory.getLogger(AnythingsCommRabbitmqInitializer.class);
	
	@Autowired
	@Qualifier("rest")
	private IServiceFinder restFinder;
	
	@Autowired
	private IEntityFieldCache entityFieldCache;
	
	@Autowired
	private AnythingsCommRabbitmqModuleProperties module;
	
	@Autowired
	private ModuleConfigSet configSet;

	@EventListener({ ContextRefreshedEvent.class })
	public void ready(ContextRefreshedEvent event) {
		this.logger.info("Anythings Communication Rabbitmq module initializing ready...");
		this.configSet.addConfig(this.module.getName(), this.module);
		this.scanServices();		
	}
	
	@EventListener({ApplicationReadyEvent.class})
    void contextRefreshedEvent(ApplicationReadyEvent event) {
		this.logger.info("Anythings Communication Rabbitmq module initializing started...");		
    }
	
	/**
	 * 모듈 서비스 스캔 
	 */
	private void scanServices() {
		this.entityFieldCache.scanEntityFieldsByBasePackage(this.module.getBasePackage());
		this.restFinder.scanServicesByPackage(this.module.getName(), this.module.getBasePackage());
	}
}