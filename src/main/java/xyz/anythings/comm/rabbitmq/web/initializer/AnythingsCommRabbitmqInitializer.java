/* Copyright © HatioLab Inc. All rights reserved. */
package xyz.anythings.comm.rabbitmq.web.initializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import xyz.anythings.comm.rabbitmq.config.ModuleProperties;
import xyz.elidom.rabbitmq.config.RabbitmqProperties;
import xyz.elidom.rabbitmq.rest.VirtualHostController;
import xyz.elidom.sys.config.ModuleConfigSet;
import xyz.elidom.sys.system.config.module.IModuleProperties;
import xyz.elidom.sys.system.service.api.IEntityFieldCache;
import xyz.elidom.sys.system.service.api.IServiceFinder;
import xyz.elidom.util.BeanUtil;

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
	private ModuleProperties module;
	
	@Autowired
	private RabbitmqProperties properties;
	
	@Autowired
	private ModuleConfigSet configSet;
	
	@Autowired
	private Environment environment;
	
	/**
	 * 큐 명칭 기본 프로퍼티 명 
	 */
	private final String rabbitQueueProp = "mq.system.receive.queue.name.";
	

	@EventListener({ ContextRefreshedEvent.class })
	public void ready(ContextRefreshedEvent event) {
		this.logger.info("Anythings Communication Rabbitmq module initializing ready...");
		this.configSet.addConfig(this.module.getName(), this.module);
		this.scanServices();		
	}
	
	@EventListener({ApplicationReadyEvent.class})
	@Order(Ordered.LOWEST_PRECEDENCE)
    void contextRefreshedEvent(ApplicationReadyEvent event) {
		this.logger.info("Anythings Communication Rabbitmq module initializing started...");		
		

		this.logger.info("RabbitMq Queue Listen Ready start ...");
		
		Map<String,IModuleProperties> moduleProperties = configSet.getAll();
		List<String> systemQueueList = new ArrayList<String>();
		
		for(String moduleName : moduleProperties.keySet()) {
			String moduleQueueProp = this.rabbitQueueProp + moduleName;
			
			if(this.environment.containsProperty(moduleQueueProp)) {
				String queueName = this.environment.getProperty(moduleQueueProp);
				configSet.getConfig(moduleName).setRabbitmqQueue(queueName);
				systemQueueList.add(queueName);
			}
		}
		/*
		for(String moduleName : moduleProperties.keySet()) {
			if(moduleName.startsWith("anythings")) {
				String queue = moduleProperties.get(moduleName).getRabbitQueue();
				if(ValueUtil.isEqualIgnoreCase(queue, "not_use")) {
				} else if (ValueUtil.isEmpty(queue)) {
				} else {
					systemQueueList.add(queue);
				}
			}
		}
		*/
		this.properties.setSystemQueueList(systemQueueList);
		
		for(String vHostName : this.properties.getAppInitVHosts()) {
			BeanUtil.get(VirtualHostController.class).addVhostListener(vHostName);
		}
		
		this.logger.info("RabbitMq Queue Listen Ready Finished ...");
    }
	
	/**
	 * 모듈 서비스 스캔 
	 */
	private void scanServices() {
		this.entityFieldCache.scanEntityFieldsByBasePackage(this.module.getBasePackage());
		this.restFinder.scanServicesByPackage(this.module.getName(), this.module.getBasePackage());
	}
}