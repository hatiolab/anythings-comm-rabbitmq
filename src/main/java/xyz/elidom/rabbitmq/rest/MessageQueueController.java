package xyz.elidom.rabbitmq.rest;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import xyz.elidom.dbist.dml.Filter;
import xyz.elidom.dbist.dml.Page;
import xyz.elidom.orm.system.annotation.service.ApiDesc;
import xyz.elidom.orm.system.annotation.service.ServiceDesc;
import xyz.elidom.rabbitmq.config.RabbitmqProperties;
import xyz.elidom.rabbitmq.service.BrokerAdminService;
import xyz.elidom.rabbitmq.service.ServiceUtil;
import xyz.elidom.rabbitmq.service.model.Queue;
import xyz.elidom.rabbitmq.service.model.QueueItem;
import xyz.elidom.rabbitmq.service.model.QueueSearch;
import xyz.elidom.sys.SysConfigConstants;
import xyz.elidom.sys.system.service.AbstractRestService;
import xyz.elidom.sys.util.SettingUtil;
import xyz.elidom.util.FormatUtil;
import xyz.elidom.util.ValueUtil;

/**
 * 메시지 큐 관리 
 * @author yang
 *
 */
@RestController
@Transactional
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/rest/rabbitmq/queue")
@ServiceDesc(description = "Mq Message Queue Manager Service API")
public class MessageQueueController extends AbstractRestService {
	
	@Autowired
	BrokerAdminService adminServie ;
	
	@Autowired
	RabbitmqProperties properties;
	
	@Override
	protected Class<?> entityClass() {
		return QueueItem.class;
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description = "Search (Pagination) By Search Conditions")
	public Page<?> index(@RequestParam(name = "page", required = false) Integer page,
			@RequestParam(name = "limit", required = false) Integer limit,
			@RequestParam(name = "select", required = false) String select,
			@RequestParam(name = "sort", required = false) String sort,
			@RequestParam(name = "query", required = false) String query) {
		
		page =  page == null ? 1 : page.intValue();
		limit = (limit == null) ? ValueUtil.toInteger(SettingUtil.getValue(SysConfigConstants.SCREEN_PAGE_LIMIT, "50")) : limit.intValue();
		
		Filter[] querys = new Filter[0];
		String vhost = "";
		String queueName = "";
		
		if (ValueUtil.isNotEmpty(query)) querys = FormatUtil.jsonToObject(query, Filter[].class);
		
		for(Filter filter : querys) {
			if(filter.getName().equalsIgnoreCase("site_code")) vhost = filter.getValue().toString();
			else if (filter.getName().equalsIgnoreCase("queue_name")) queueName = filter.getValue().toString();
		}
		
		// queue list 조회 
		QueueSearch queues = this.adminServie.getQueueList(vhost, page , limit, queueName);
		
		List<QueueItem> items = new ArrayList<QueueItem>();
		
		// filter 조건 적용 
		for(Queue queue : queues.getItems()) {
			QueueItem item = new QueueItem();
			String siteCode = queue.getVhost();
			
			String itemQueueName = queue.getName().replaceAll("mqtt-subscription-", "").replaceAll("qos1", "");
			
			boolean isSystemQueue = false;
			
			if(ValueUtil.isInclude(properties.getSystemQueueList(), itemQueueName)) isSystemQueue = true;
			else if (itemQueueName.startsWith("trace")) isSystemQueue = true;
			
			item.setSiteCode(siteCode);
			item.setClient(queue.getConsumers());
			item.setQueueName(itemQueueName);
			item.setIsSystemQueue(isSystemQueue);
			item.setMessageCount(queue.getMessageReady());
			item.setMessageBytes(ServiceUtil.valueToByteString(queue.getMessagesReadyDetails()));
			
			items.add(item);
		}
		
		// 페이지 리턴값 생성 
		Page<QueueItem> result = new Page<QueueItem>();
		result.setTotalSize(queues.getTotalCount());
		result.setList(items);
		
		return result;
	}
	
	/**
	 * 큐 메시지 비우기 
	 * @param vhost
	 * @param queue
	 * @param is_system_queue
	 * @return
	 */
	@RequestMapping(value="/{vhost}/{queue}/{is_system_queue}", method=RequestMethod.DELETE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Queue Message purge")
	public Boolean purgeQueue(@PathVariable("vhost") String vhost,
			@PathVariable("queue") String queue,
			@PathVariable("is_system_queue") boolean is_system_queue) {
		
		queue = queue.replaceAll("\\.", "/");
		
		this.adminServie.purgeQueue(vhost, queue, is_system_queue);
		return true;
	}
	
	/**
	 * 큐 추가 삭제 
	 * @param list
	 * @return
	 */
	@RequestMapping(value="/update_multiple", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	@ApiDesc(description="Create, Update or Delete multiple at one time")
	public Boolean multipleUpdate(@RequestBody List<QueueItem> list) {
		for(QueueItem item : list) {
			if(item.getCudFlag_().equalsIgnoreCase("d")) {
				this.adminServie.deleteQueue(item.getSiteCode(), item.getQueueName(), item.getIsSystemQueue());
			} else if(item.getCudFlag_().equalsIgnoreCase("c")) {
				this.adminServie.createQueue(item.getSiteCode(), item.getQueueName(), false);
			}
		}
		return true;
	}
}