package com.lwxf.industry4.webapp.facade.wxapi.factory.statements.impl;

import javax.annotation.Resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.statements.app.factory.designStatement.DesignStatementService;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.DateUtilsExt;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByMonthDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByQuarterDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByWeekDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByYearDto;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.wxapi.factory.statements.DesignStatementFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/7/3 0003 9:24
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("wxDesignStatementFacade")
public class DesignStatementFacadeImpl extends BaseFacadeImpl implements DesignStatementFacade {
	@Resource(name = "designStatementService")
	private DesignStatementService designStatementService;
	@Override
	public RequestResult weekDesignStatements(String branchId, String countType) {
		MapContext map=MapContext.newOne();
		map.put("branchId",branchId);
		map.put("countType",countType);
		CountByWeekDto countByWeekDto=this.designStatementService.findDesignStatementByWeek(map);
		String[] strings={countByWeekDto.getMon().toString(),countByWeekDto.getTues().toString(),
				countByWeekDto.getWed().toString(),countByWeekDto.getThur().toString(),
				countByWeekDto.getFri().toString(),countByWeekDto.getSat().toString(),
				countByWeekDto.getSun().toString()};
		MapContext mapContext=MapContext.newOne();
		mapContext.put("result",strings);
		return ResultFactory.generateRequestResult(mapContext);
	}

	@Override
	public RequestResult monthDesignStatements(String branchId, String countType) {
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		List timeList = DateUtilsExt.getTimeList(year, month-1, 7);
		MapContext mapContext=MapContext.newOne();
		mapContext.put("oneTime",(Date)timeList.get(0));
		mapContext.put("twoTime",(Date)timeList.get(1));
		mapContext.put("threeTime",(Date)timeList.get(2));
		mapContext.put("fourTime",(Date)timeList.get(3));
		mapContext.put("fiveTime",(Date)timeList.get(timeList.size()-1));
		mapContext.put("branchId",branchId);
		mapContext.put("countType",countType);
		CountByMonthDto countByMonthDto=this.designStatementService.findDesignStatementByMonth(mapContext);
		String[] strings={countByMonthDto.getPoint1().toString(),countByMonthDto.getPoint2().toString(),
				countByMonthDto.getPoint3().toString(),countByMonthDto.getPoint4().toString()};
		return ResultFactory.generateRequestResult(strings);
	}

	@Override
	public RequestResult quarterDesignStatements(String branchId, String countType) {
		Date date= DateUtil.getSystemDate();
		//获取本季的第一个月
		Date firstSeasonDate = DateUtilsExt.getFirstSeasonDate(date);
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(firstSeasonDate);
		int firstMonth=calendar.get(Calendar.MONTH)+1;
		int secondMonth=firstMonth+1;
		int thirdMonth=firstMonth+2;
		MapContext mapContext=MapContext.newOne();
		mapContext.put("branchId",branchId);
		mapContext.put("countType",countType);
		mapContext.put("firstMonth",firstMonth);
		mapContext.put("secondMonth",secondMonth);
		mapContext.put("thirdMonth",thirdMonth);
		CountByQuarterDto countByQuarterDto=this.designStatementService.findDesignStatementByQuarter(mapContext);
		String[] strings={countByQuarterDto.getMonth1().toString(),
				countByQuarterDto.getMonth2().toString(),
				countByQuarterDto.getMonth3().toString()};
		String[] months={firstMonth+"月份",secondMonth+"月份",thirdMonth+"月份"};
		List list=new ArrayList();
		list.add(strings);
		list.add(months);
		return ResultFactory.generateRequestResult(list);
	}

	@Override
	public RequestResult yearDesignStatements(String branchId, String countType) {
		MapContext mapContext=MapContext.newOne();
		mapContext.put("branchId",branchId);
		mapContext.put("countType",countType);
		CountByYearDto countByYearDto=this.designStatementService.findDesignStatementByYear(mapContext);
		String[] strings={countByYearDto.getJun().toString(),
				countByYearDto.getFeb().toString(),
				countByYearDto.getMar().toString(),
				countByYearDto.getApr().toString(),
				countByYearDto.getJun().toString(),
				countByYearDto.getJul().toString(),
				countByYearDto.getAug().toString(),
				countByYearDto.getSept().toString(),
				countByYearDto.getOct().toString(),
				countByYearDto.getNov().toString(),
				countByYearDto.getDece().toString()};
		return ResultFactory.generateRequestResult(strings);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult designStatements(String branchId, String countType, MapContext mapContext) {
		mapContext.put("branchId",branchId);
		mapContext.put("countType",countType);
		MapContext result=MapContext.newOne();
		if(countType==null||countType.trim().equals("")){
			mapContext.put("countType","0");
		}
		//日期类型
		Date startTime = new Date();
		Date endTime = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		//按不同的方式截取不同的时间格式
		if (mapContext.containsKey("startTime")) {
			try {
				endTime = DateUtil.getSystemDate();
				if (mapContext.containsKey("endTime")) {
					endTime = simpleDateFormat.parse(mapContext.getTypedValue("endTime", String.class));
				}
				startTime = simpleDateFormat.parse(mapContext.getTypedValue("startTime", String.class));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			endTime = DateUtil.getSystemDate();
			startTime = DateUtilsExt.getFrontDay(endTime, 6);
		}
		List<Date> dates =new ArrayList<>();
		SimpleDateFormat format=new SimpleDateFormat("MM-dd");
		try {
			if(mapContext.getTypedValue("dateType",String.class).equals("0")) {//按日
				dates = DateUtilsExt.findDates(startTime, endTime);
			}else if(mapContext.getTypedValue("dateType",String.class).equals("1")){//按月
				dates = DateUtilsExt.getMonthBetween(startTime,endTime);
				format=new SimpleDateFormat("yyyy-MM");
			}else if(mapContext.getTypedValue("dateType",String.class).equals("2")){//按年
				dates = DateUtilsExt.getYearBetween(startTime,endTime);
				format=new SimpleDateFormat("yyyy");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List list=new ArrayList();
		for(Date date:dates) {
			mapContext.put("date",date);
			DateNum dateNum = this.designStatementService.findDesignStatements(mapContext);
			dateNum.setCreatTime(format.format(date));
			list.add(dateNum);
		}
		result.put("result",list);
		return ResultFactory.generateRequestResult(result);
	}
}
