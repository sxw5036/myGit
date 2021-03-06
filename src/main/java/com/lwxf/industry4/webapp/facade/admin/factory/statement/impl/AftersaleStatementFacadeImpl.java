package com.lwxf.industry4.webapp.facade.admin.factory.statement.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.statements.app.factory.aftersaleStatement.AftersaleStatementService;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.DateUtilsExt;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByMonthDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByQuarterDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByWeekDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByYearDto;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.statement.AftersaleStatementFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/6/30 0030 10:47
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("AftersaleStatementFacade")
public class AftersaleStatementFacadeImpl extends BaseFacadeImpl implements AftersaleStatementFacade {
	@Resource(name = "aftersaleStatementService")
	private AftersaleStatementService aftersaleStatementService;

	/**
	 * 本周售后报表
	 * @param branchId
	 * @param countType
	 * @return
	 */
	@Override
	public RequestResult weekAftersaleStatements(String branchId, String countType) {
		MapContext result=MapContext.newOne();
		CountByWeekDto countByWeekDto=this.aftersaleStatementService.findAftersaleStatementByWeek(branchId,countType);
		String[] strings={countByWeekDto.getMon().toString(),countByWeekDto.getTues().toString(),
				countByWeekDto.getWed().toString(),countByWeekDto.getThur().toString(),
				countByWeekDto.getFri().toString(),countByWeekDto.getSat().toString(),
				countByWeekDto.getSun().toString()};
		//本周第一天
		Date startTime = DateUtilsExt.getBeginDayOfWeek();
		//本周最后一天
		Date endTime=DateUtilsExt.getEndDayOfWeek();
		MapContext map=MapContext.newOne();
		map.put("branchId",branchId);
		map.put("startTime",startTime);
		map.put("endTime",endTime);
		List list=this.getPieCharts(countType,map);
		result.put("countByWeekDto",strings);
		result.put("type",list);
		return ResultFactory.generateRequestResult(result);
	}

	@Override
	public RequestResult monthAftersaleStatements(String branchId, String countType) {
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
		CountByMonthDto countByMonthDto=this.aftersaleStatementService.findAftersaleStatementByMonth(mapContext);
		String[] strings={countByMonthDto.getPoint1().toString(),countByMonthDto.getPoint2().toString(),
				countByMonthDto.getPoint3().toString(),countByMonthDto.getPoint4().toString()};
		MapContext map=MapContext.newOne();
		map.put("branchId",branchId);
		map.put("startTime",(Date)timeList.get(0));
		map.put("endTime",(Date)timeList.get(timeList.size()-1));
		List list=this.getPieCharts(countType,map);
		MapContext result=MapContext.newOne();
		result.put("countByMonthDto",strings);
		result.put("type",list);
		return ResultFactory.generateRequestResult(result);
	}

	@Override
	public RequestResult quarterAftersaleStatements(String branchId, String countType) {
		//柱状图信息
		Date date=DateUtil.getSystemDate();
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
		CountByQuarterDto countByQuarterDto=this.aftersaleStatementService.findAftersaleStatementByQuarter(mapContext);
		String[] strings={countByQuarterDto.getMonth1().toString(),
				countByQuarterDto.getMonth2().toString(),
				countByQuarterDto.getMonth3().toString()};
		String[] months={firstMonth+"月份",secondMonth+"月份",thirdMonth+"月份"};
		//圆饼图信息
		//本季度第一个月的第一天
		calendar.setTime(firstSeasonDate);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date startTime=calendar.getTime();
		//某季度的最后一个月
		SimpleDateFormat dateFormat=new SimpleDateFormat("MM");
		int a =Integer.parseInt(dateFormat.format(firstSeasonDate)) ;
		calendar.set(Calendar.MONTH,a+1);
		Date endTime=calendar.getTime();
		MapContext map=MapContext.newOne();
		map.put("branchId",branchId);
		map.put("startTime",startTime);
		map.put("endTime",endTime);
		List list=this.getPieCharts(countType,map);
		MapContext result=MapContext.newOne();
		result.put("type",list);
		result.put("months",months);
		result.put("countByQuarterDto",strings);
		return ResultFactory.generateRequestResult(result);
	}

	@Override
	public RequestResult yearAftersaleStatements(String branchId, String countType) {
		CountByYearDto countByYearDto=this.aftersaleStatementService.findAftersaleStatementByYear(branchId,countType);
		String[] strings={countByYearDto.getJan().toString(),
				countByYearDto.getFeb().toString(),
				countByYearDto.getMar().toString(),
				countByYearDto.getApr().toString(),
				countByYearDto.getMay().toString(),
				countByYearDto.getJun().toString(),
				countByYearDto.getJul().toString(),
				countByYearDto.getAug().toString(),
				countByYearDto.getSept().toString(),
				countByYearDto.getOct().toString(),
				countByYearDto.getNov().toString(),
				countByYearDto.getDece().toString()};
		MapContext map=MapContext.newOne();
		Date startTime=DateUtilsExt.getBeginDayOfYear();//本年的开始时间
		Date endTime=DateUtilsExt.getEndDayOfYear();//本年的结束时间
		map.put("branchId",branchId);
		map.put("startTime",startTime);
		map.put("endTime",endTime);
		List list=this.getPieCharts(countType,map);
		MapContext result=MapContext.newOne();
		result.put("type",list);
		result.put("countByYearDto",strings);
		return ResultFactory.generateRequestResult(result);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult aftersaleStatements(Date StartDate,Date endDate,MapContext map,Integer dateType) {
		MapContext mapRes = MapContext.newOne();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<MapContext> listRes = new ArrayList<>();
		if(dateType==null){
			dateType=0;
		}
		if(dateType==0){ //按日统计
			if(StartDate!=null && endDate!=null) {
				List<Date> list = DateUtilsExt.findDates(StartDate, endDate);
				if (list != null && list.size() > 0) {
					for (Date date : list) {
						SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd");
						map.put("created",sdf.format(date));
						MapContext mapValue = MapContext.newOne();
						mapValue.put("date",sdf1.format(date));
						mapValue.put("value",aftersaleStatementService.findAftersaleStatements(map));
						listRes.add(mapValue);
					}
				}
			}
		}else if(dateType==1){ //按月统计
			SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
			SimpleDateFormat sdfParam = new SimpleDateFormat("yyyy-MM-dd");
			if(StartDate!=null && endDate!=null){
				List<Date> list = null;
				try {
					list = DateUtilsExt.getMonthBetween(StartDate,endDate);
					if(list!=null&&list.size()>0){
						for(Date date: list){
							map.put("beginDate",sdfParam.format(DateUtilsExt.getFirstDayOfMonth(date)));
							map.put("endDate",sdfParam.format(DateUtilsExt.getLastDayOfMonth(date)));
							MapContext mapValue = MapContext.newOne();
							mapValue.put("date",sdfMonth.format(date));
							mapValue.put("value",aftersaleStatementService.findAftersaleStatements(map));
							listRes.add(mapValue);
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}else if(dateType==2){ //按年统计
			SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
			SimpleDateFormat sdfParam = new SimpleDateFormat("yyyy-MM-dd");
			if(StartDate!=null && endDate!=null){
				List<Date> list = null;
				try {
					list = DateUtilsExt.getYearBetween(StartDate,endDate);
					if(list!=null&&list.size()>0){
						for(Date date: list){
							map.put("beginDate",sdfParam.format(DateUtilsExt.getFirstDayOfYear(date)));
							map.put("endDate",sdfParam.format(DateUtilsExt.getLastDayOfYear(date)));
							MapContext mapValue = MapContext.newOne();
							mapValue.put("date",sdfYear.format(date));
							mapValue.put("value",aftersaleStatementService.findAftersaleStatements(map));
							listRes.add(mapValue);
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		mapRes.put("chart1",listRes);
		//圆饼图信息
        MapContext mapParam=MapContext.newOne();
		mapParam.put("branchId", WebUtils.getCurrBranchId());
		mapParam.put("beginDate",StartDate);
		mapParam.put("endDate",endDate);
		List piceCharts=this.getPieCharts(map.get("countType").toString(),map);
		mapRes.put("chart2",piceCharts);
		return ResultFactory.generateRequestResult(mapRes);
	}

	/**
	 * 圆饼图数据信息
	 * @param countType
	 * @param map
	 * @return
	 */
	public List getPieCharts(String countType,MapContext map){
		List list=new ArrayList();
		if(countType.equals("0")){
			MapContext mapZero=this.aftersaleStatementService.findAftersaleStatementTypeByWeek(map);
			int a=0;
			for(int i=0;i<2;i++){
				Map map1=new HashMap<>();
				switch (a){
					case 0:
						map1.put("name","反馈");
						map1.put("value",mapZero.getTypedValue("feedback",Integer.class));
						a=a+1;
						break;
					case 1:
						map1.put("name","补料");
						map1.put("value",mapZero.getTypedValue("buLiao",Integer.class));
						a=a+1;
						break;
				}
				list.add(map1);
			}

		}
		if(countType.equals("1")){
			MapContext mapOne=this.aftersaleStatementService.findfindAftersaleStatementTypeOneByWeek(map);
			int a=0;
			for(int i=0;i<3;i++){
				Map map2 = new HashMap();
				switch (a) {
				case 	0:
					map2.put("name", "免费");
					map2.put("value",mapOne.getTypedValue("free",Integer.class));
					a=a+1;
					break;
					case 1:
					map2.put("name", "收费");
					map2.put("value",mapOne.getTypedValue("charge",Integer.class));
					a=a+1;
					break;
					case 2:
					map2.put("name", "拒绝");
					map2.put("value",mapOne.getTypedValue("refuse",Integer.class));
					a=a+1;
					break;
				}
				list.add(map2);
			}
		}
		if(countType.equals("2")){
			MapContext mapTwo=this.aftersaleStatementService.findfindAftersaleStatementTypeTwoByWeek(map);
			int a=0;
			for(int i=0;i<4;i++){
				Map map2 = new HashMap();
				switch (a) {
					case 0:
					map2.put("name", "待支付");
					map2.put("value",mapTwo.getTypedValue("waitPay",Integer.class));

					a=a+1;
					break;
					case 1:
					map2.put("name", "生产中");
					map2.put("value",mapTwo.getTypedValue("produce",Integer.class));
					a=a+1;
					break;
					case 2:
					map2.put("name", "待发货");
					map2.put("value",mapTwo.getTypedValue("waitDispatchBill",Integer.class));
					a=a+1;
					break;
					case 3:
					map2.put("name", "已发货");
					map2.put("value",mapTwo.getTypedValue("endDispatchBill",Integer.class));
					a=a+1;
					break;
				}
				list.add(map2);
			}
		}
		return list;
	}
}
