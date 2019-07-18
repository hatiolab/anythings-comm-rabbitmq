package xyz.elidom.rabbitmq.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * rabbitmq 관련 properties 셋팅 
 * @author yang
 *
 */
@Component
public class RabbitmqProperties {
	
	@Value("${server.port:80}")
	int managerPort;
	
	/**
	 * 브로커 관련 설정 
	 */
	// 브로커 주소 ( def : localhost )
	@Value("${mq.broker.address:localhost}")
	private String brokerAddress;
	// 브로커 포트 (def : 5672 : amqp 기본 )
	@Value("${mq.broker.port:5672}")
	private int brokerPort;	
	// 브로커 web api 포트 ( def : 15672 )
	@Value("${mq.broker.api.port:15672}")
	private int brokerApiPort;	
	// 브로커 관리자 아이디.
	@Value("${mq.broker.user.id:admin}")
	private String brokerAdminId;	
	// 브로커 관리자 비밀번호.
	@Value("${mq.broker.user.pw:admin}")
	private String brokerAdminPw;
	// 브로커 기본 exchange 설정 
	@Value("${mq.broker.exchange.default:amq.direct}")
	private String brokerExchange;		

	/**
	 * 브로커 메시지 트레이스 관련 설정 
	 */
	@Value("${mq.trace.use:false}")
	private boolean traceUse;
	
	// 트레이스 기록 대상 설정 ( db, file, elastic )
	@Value("${mq.trace.type:db}")
	private String traceType;	
	// 트레이스 타입이 file 일때 root path
	@Value("${mq.trace.file.root:./traces}")
	private String traceFileRoot;	
	// 트레이스 메시지 보관 일수 
	@Value("${mq.trace.keep.date:5}")
	private String traceKeepDate;
	// 트레이스 메시지 삭제 처리 서버 설정 (true 인 서버에서만 삭제 실행)
	@Value("${mq.trace.delete.main:false}")
	private boolean deleteMain;
	// 트레이스 메시지 삭제 시간 
	@Value("${mq.trace.delete.time:01}")
	private String traceDelTime;
	// 트레이스 메시지 리스터 숫자 
	@Value("${mq.trace.consume.count:3}")
	private int traceConsumeCnt;	
	// 엘라스틱 주소 
	@Value("${mq.trace.elastic.address:localhost}")
	private String traceElasticAddress;
	// 엘라스틱 포트 
	@Value("${mq.trace.elastic.port:0}")
	private int traceElasticPort;	
	
	/**
	 * 시스템 메시지 리스너 설정 
	 */
	private List<String> systemQueueList;
	
	private List<String> appInitVHosts;
	
	// 리스너 숫자 
	@Value("${mq.system.receive.queue.consume.count:3}")
	private int systemConsumeCnt;
	
	public String getBrokerAddress() {
		return brokerAddress;
	}
	public int getBrokerApiPort() {
		return brokerApiPort;
	}
	public String getBrokerAdminId() {
		return brokerAdminId;
	}
	public String getBrokerAdminPw() {
		return brokerAdminPw;
	}
	public String getBrokerExchange() {
		return brokerExchange;
	}
	public String getTraceType() {
		return traceType;
	}
	public String getTraceFileRoot() {
		return traceFileRoot;
	}
	public String getTraceKeepDate() {
		return traceKeepDate;
	}
	public String getTraceDelTime() {
		return traceDelTime;
	}
	public int getTraceConsumeCnt() {
		return traceConsumeCnt;
	}
	public String getTraceElasticAddress() {
		return traceElasticAddress;
	}
	public int getTraceElasticPort() {
		return traceElasticPort;
	}
	public int getSystemConsumeCnt() {
		return systemConsumeCnt;
	}
	public int getBrokerPort() {
		return brokerPort;
	}
	public boolean isDeleteMain() {
		return deleteMain;
	}
	public int getManagerPort() {
		return managerPort;
	}
	public boolean getTraceUse() {
		return traceUse;
	}
	
	public List<String> getSystemQueueList(){
		return systemQueueList;
	}
	
	public void setSystemQueueList(List<String> systemQueueList){
		this.systemQueueList = systemQueueList;
	}
	
	public List<String> getAppInitVHosts(){
		return appInitVHosts;
	}
	
	public void setAppInitVHosts(List<String> appInitVHosts) {
		this.appInitVHosts = appInitVHosts;
	}
}
