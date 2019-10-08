package xyz.elidom.rabbitmq.client.event;

import org.springframework.amqp.core.Message;
import org.springframework.context.ApplicationEvent;

/**
 * system 메시지 event
 * @author yang
 *
 */
public class SystemMessageReceiveEvent extends ApplicationEvent {
	private static final long serialVersionUID = -5637200314112693717L;
	private final Message message;
	private final String vHost;
	public final String queueName;
	private boolean isExecuted;
	
	public SystemMessageReceiveEvent(Object source, Message message, String vHost) {
		super(source);
	    this.message = message;
	    this.vHost =vHost;
	    this.queueName = message.getMessageProperties().getConsumerQueue();
	    this.isExecuted = false;
	}
	
	public Message getMessage() {
		return message;
	}
	
	public String getVhost() {
		return vHost;
	}
	
	public String getQueueName() {
		return queueName;
	}

	public boolean isExecuted() {
		return isExecuted;
	}

	public void setExecuted(boolean isExecuted) {
		this.isExecuted = isExecuted;
	}
}