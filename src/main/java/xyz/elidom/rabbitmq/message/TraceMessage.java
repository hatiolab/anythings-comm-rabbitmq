package xyz.elidom.rabbitmq.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import xyz.elidom.rabbitmq.message.MessageProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TraceMessage {
	private MessageProperties properties;
	private TraceMessageDetail body;

	public MessageProperties getProperties() {
		return properties;
	}

	public void setProperties(MessageProperties properties) {
		this.properties = properties;
	}

	public TraceMessageDetail getBody() {
		return body;
	}

	public void setBody(TraceMessageDetail body) {
		this.body = body;
	}
}
