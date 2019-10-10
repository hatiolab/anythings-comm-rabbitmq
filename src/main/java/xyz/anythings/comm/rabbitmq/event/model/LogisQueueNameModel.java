package xyz.anythings.comm.rabbitmq.event.model;

/**
 * Logis 큐 명칭 생성 모델  
 * @author yang
 *
 */
public class LogisQueueNameModel implements IQueueNameModel{
	
	private String befQueueName;
	private String areaCd;
	private String stageCd;
	private long domainId;
	private String domainSite;
	private String cudFlag_;
	
	public LogisQueueNameModel() {
	}
	
	public LogisQueueNameModel(long domainId, String cudFlag_,String domainSite,String befQueueName, String areaCd, String stageCd) {
		this.domainId = domainId;
		this.cudFlag_ = cudFlag_;
		this.domainSite = domainSite;
		this.areaCd = areaCd;
		this.stageCd = stageCd;
		this.befQueueName = befQueueName;
	}
	
	@Override
	public String getBefQueueName() {
		return this.befQueueName;
	}
	public void setBefQueueName(String befQueueName) {
		this.befQueueName = befQueueName;
	}
	
	public String getAreaCd() {
		return areaCd;
	}
	public void setAreaCd(String areaCd) {
		this.areaCd = areaCd;
	}
	public String getStageCd() {
		return stageCd;
	}
	public void setStageCd(String stageCd) {
		this.stageCd = stageCd;
	}
	
	@Override
	public long getDomainId() {
		return domainId;
	}
	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}
	@Override
	public String getCudFlag_() {
		return cudFlag_;
	}
	public void setCudFlag_(String cudFlag_) {
		this.cudFlag_ = cudFlag_;
	}
	
	public String getDomainSite() {
		return domainSite;
	}
	
	@Override
	public void setDomainSite(String domainSite) {
		this.domainSite = domainSite;
	}
	
	@Override
	public String getQueueName() {
		return this.getDomainSite() + '/' + this.getAreaCd() + '/' + this.stageCd;
	}
}
