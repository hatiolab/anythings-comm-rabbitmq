package xyz.elidom.rabbitmq.client;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.elidom.rabbitmq.config.RabbitmqProperties;
import xyz.elidom.rabbitmq.connection.ConnectionCreater;
import xyz.elidom.util.BeanUtil;
import xyz.elidom.util.ValueUtil;

/**
 * 시스템 클라이언트 
 * @author yang
 *
 */
@Component
public class SystemClient extends CreateMessageReceiver implements IClient {
	
	@Autowired
	private RabbitmqProperties mqProperties;
	
	String clientType = "system";
	Map<String, Map<String, ClientTemplate>> vHostMap = new HashMap<String, Map<String, ClientTemplate>>();
	
	/**
	 * 사이트 리스너 추가 
	 * @param vHost 사이트 코드 
	 */
	@Override
	public void addVirtualHost(String vHost) {
		if(vHostMap.containsKey(vHost) == true) return;
		if(ValueUtil.isEmpty(mqProperties.getSystemQueueList())) return; 
		
		Map<String, ClientTemplate> clients = new HashMap<String, ClientTemplate>();
		
		for(String queueName : mqProperties.getSystemQueueList()) {
			BeanUtil.get(BrokerSiteAdmin.class).createSystemQueue(vHost, queueName);
			SimpleMessageListenerContainer container = super.CreateMessageListener(vHost, queueName, clientType);
			RabbitTemplate template = ConnectionCreater.CreateMessageSender(container.getConnectionFactory());
			
			clients.put(queueName, new ClientTemplate(container, template));
		}
		
		vHostMap.put(vHost, clients);
	}
	
	/**
	 * 사이트 리스너 삭제 
	 * @param vHost 사이트 코드 
	 */
	@Override
	public void removeVirtualHost(String vHost) {
		if(vHostMap.containsKey(vHost) == false) return;
		
		Map<String, ClientTemplate> clientMap = vHostMap.get(vHost);
		
		for(String queueName : clientMap.keySet()) {
			clientMap.get(queueName).container.destroy();
		}
		
		vHostMap.remove(vHost);
	}
	
	/**
	 * 메시지 publish 
	 * @param vHost 사이트 코드 
	 * @param destId 목적지 라우팅 키 
	 * @param message 메시지 
	 * @return 
	 */
	public boolean sendMessage(String vHost, String sendQueueName, String destId, String message) {
		if(vHostMap.containsKey(vHost) == false) return false;
		if(vHostMap.get(vHost).containsKey(sendQueueName) == false) return false;
		
		// amqp to mqtt 전송시 route key 가 / -> comma
		if(destId.contains("/")) destId = destId.replaceAll("/", ".");
		
		vHostMap.get(vHost).get(sendQueueName).template.convertAndSend(mqProperties.getBrokerExchange(), destId,message);
		
		return true;
	}
	
	/**
	 * 시스템 클라이언트 리스너 및 퍼블리셔 저장 모델 클래스 
	 * @author yang
	 *
	 */
	private class ClientTemplate{
		public SimpleMessageListenerContainer container;
		public RabbitTemplate template;
		
		public ClientTemplate(SimpleMessageListenerContainer container, RabbitTemplate template) {
			this.container = container;
			this.template = template;
		}
	}

	@Override
	public void initLogger() {
	}
}
