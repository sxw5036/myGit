package com.lwxf.industry4.webapp.facade.app.factory.factorySchedule.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lwxf.industry4.webapp.bizservice.user.UserScheduleItemService;
import com.lwxf.industry4.webapp.bizservice.user.UserScheduleService;
import com.lwxf.industry4.webapp.common.enums.user.UserScheduleItemType;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.entity.user.UserSchedule;
import com.lwxf.industry4.webapp.domain.entity.user.UserScheduleItem;
import com.lwxf.industry4.webapp.facade.app.factory.factorySchedule.FactoryScheduleFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：日程管理相关业务处理
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/4/4 0004 13:22
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "factoryScheduleFacade")
public class FactoryScheduleFacadImpl extends BaseFacadeImpl implements FactoryScheduleFacade {
	@Resource(name = "userScheduleItemService")
	private UserScheduleItemService userScheduleItemService;
	@Resource(name = "userScheduleService")
	private UserScheduleService userScheduleService;

	/**
	 * 返回一个月的日程数据
	 *
	 * @param userId
	 * @param time
	 * @param request
	 * @return
	 */
	@Override
	public RequestResult findUserScheduleItems(String userId, String time, HttpServletRequest request) {
		List<UserSchedule> userScheduleList = this.userScheduleService.findUserSchedule(userId, time);
		if (userScheduleList.isEmpty()) {
			return ResultFactory.generateSuccessResult();
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		List result = new ArrayList();
		for (UserSchedule userSchedule : userScheduleList) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			String id = userSchedule.getId();
			String date = format.format(userSchedule.getTime());
			List<UserScheduleItem> userScheduleItems = this.userScheduleItemService.findUserScheduleItem(id);
			map.put("date", date);
			map.put("userScheduleItem", userScheduleItems);
			result.add(map);
		}
		return ResultFactory.generateRequestResult(result);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addUserScheduleItem(String userId, String scheduleTime, MapContext mapContext) {
		UserSchedule userSchedule = this.userScheduleService.findScheduleByUidAndTime(userId, scheduleTime);
		UserScheduleItem userScheduleItem = new UserScheduleItem();
		String userScheduleId;
		if (userSchedule == null) {
			UserSchedule schedule = new UserSchedule();
			schedule.setUserId(userId);
			try {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date timeValue = format.parse(scheduleTime);
				schedule.setTime(timeValue);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			schedule.setSize(1);
			this.userScheduleService.add(schedule);
			userScheduleId = schedule.getId();

		} else {
			userScheduleId = userSchedule.getId();
			MapContext mapContext1 = MapContext.newOne();
			mapContext1.put("id", userScheduleId);
			mapContext1.put("size", userSchedule.getSize() + 1);
			this.userScheduleService.updateByMapContext(mapContext1);
		}

		//字符串转Date
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startTimeValue = mapContext.getTypedValue("startTime", String.class);
			String endTimeValue = mapContext.getTypedValue("endTime", String.class);
			Date startTime = dateFormat.parse(startTimeValue);
			Date endTime = dateFormat.parse(endTimeValue);
			userScheduleItem.setStartTime(startTime);
			userScheduleItem.setEndTime(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		userScheduleItem.setUserScheduleId(userScheduleId);
		userScheduleItem.setNotes(mapContext.getTypedValue("notes", String.class));
		userScheduleItem.setType(UserScheduleItemType.PERSONAL.getValue());
		String status = (String) mapContext.get("status");
		userScheduleItem.setStatus(Integer.valueOf(status));
		this.userScheduleItemService.add(userScheduleItem);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateUserScheduleItem(String scheduleItemId, MapContext mapContext) {
		if(!mapContext.isEmpty()){
			mapContext.put("id",scheduleItemId);
			this.userScheduleItemService.updateByMapContext(mapContext);
		}
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult deleteUserScheduleItem(String scheduleItemId) {
		UserScheduleItem userScheduleItem=this.userScheduleItemService.findById(scheduleItemId);
		if(userScheduleItem==null){
			return ResultFactory.generateResNotFoundResult();
		}
		String userScheduleId=userScheduleItem.getUserScheduleId();
		UserSchedule userSchedule=this.userScheduleService.findById(userScheduleId);
		this.userScheduleItemService.deleteById(scheduleItemId);
		if(userSchedule.getSize()<=1){
			this.userScheduleService.deleteById(userScheduleId);
		}else {
			MapContext mapContext = MapContext.newOne();
			mapContext.put("id", userScheduleId);
			mapContext.put("size", userSchedule.getSize() - 1);
			this.userScheduleService.updateByMapContext(mapContext);
		}

		return ResultFactory.generateSuccessResult();
	}
}

