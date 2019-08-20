package com.lwxf.industry4.webapp.facade.admin.factory.statement.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.statements.app.factory.dealerStatements.DealerStatementService;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.DateUtilsExt;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByMonthDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByQuarterDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByWeekDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByYearDto;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.admin.factory.statement.DealerStatementFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/6/30 0030 9:26
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("dealerStatementFacade")
public class DealerStatementFacadeImpl extends BaseFacadeImpl implements DealerStatementFacade {

	@Resource(name = "dealerStatementService")
	private DealerStatementService dealerStatementService;

	/**
	 * 本周业务报表
	 * @param branchId
	 * @param countType
	 * @return
	 */
	@Override
	public RequestResult weekDealerStatements(String branchId, String countType) {
			CountByWeekDto countByWeekDto=this.dealerStatementService.findDealerStatementByWeek(branchId,countType);
			String[] strings={countByWeekDto.getMon().toString(),countByWeekDto.getTues().toString(),
					           countByWeekDto.getWed().toString(),countByWeekDto.getThur().toString(),
					            countByWeekDto.getFri().toString(),countByWeekDto.getSat().toString(),
					            countByWeekDto.getSun().toString()};
			MapContext mapContext=MapContext.newOne();
			mapContext.put("result",strings);
		return ResultFactory.generateRequestResult(mapContext);
	}

	/**
	 * 本月业务报表
	 * @param branchId
	 * @param countType
	 * @return
	 */
	@Override
	public RequestResult monthDealerStatements(String branchId, String countType) {
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
		CountByMonthDto countByMonthDto=this.dealerStatementService.findDealerStatementByMonth(mapContext);
		String[] strings={countByMonthDto.getPoint1().toString(),countByMonthDto.getPoint2().toString(),
				          countByMonthDto.getPoint3().toString(),countByMonthDto.getPoint4().toString()};
		return ResultFactory.generateRequestResult(strings);
	}

	/**
	 * 本季业务报表
	 * @param branchId
	 * @param countType
	 * @return
	 */
	@Override
	public RequestResult quarterDealerStatements(String branchId, String countType) {
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
		CountByQuarterDto countByQuarterDto=this.dealerStatementService.findDealerStatementByQuarter(mapContext);
		String[] strings={countByQuarterDto.getMonth1().toString(),
				          countByQuarterDto.getMonth2().toString(),
				          countByQuarterDto.getMonth3().toString()};
		String[] months={firstMonth+"月份",secondMonth+"月份",thirdMonth+"月份"};
		List list=new ArrayList();
		list.add(strings);
		list.add(months);
		return ResultFactory.generateRequestResult(list);
	}

	/**
	 * 本年业务报表
	 * @param branchId
	 * @param countType
	 * @return
	 */
	@Override
	public RequestResult yearDealerStatements(String branchId, String countType) {
		CountByYearDto countByYearDto=this.dealerStatementService.findDealerStatementByYear(branchId,countType);
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
		return ResultFactory.generateRequestResult(strings);
	}

	@Override
	public RequestResult getSalemans(String branchId) {
		List<MapContext> list= AppBeanInjector.companyEmployeeService.findsalemans(branchId);
		return ResultFactory.generateRequestResult(list) ;
	}

	/**
	 * 条件搜索业务报表
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult dealerStatements(Date StartDate,Date endDate,MapContext map,Integer dateType) {
		MapContext mapRes = MapContext.newOne();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<MapContext> listRes = new ArrayList<>();
		if(dateType==null){
			dateType=0;
		}
		if(dateType==0){ //按日统计
			if(StartDate!=null && endDate!=null) {
				SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd");
				List<Date> list = DateUtilsExt.findDates(StartDate, endDate);
				if (list != null && list.size() > 0) {
					for (Date date : list) {
						map.put("date",sdf.format(date));
						MapContext mapValue = MapContext.newOne();
						mapValue.put("date",sdf1.format(date));
						mapValue.put("value",dealerStatementService.findDealerStatements(map));
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
							mapValue.put("value",dealerStatementService.findDealerStatements(map));
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
							mapValue.put("value",dealerStatementService.findDealerStatements(map));
							listRes.add(mapValue);
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		mapRes.put("chart1",listRes);
		return ResultFactory.generateRequestResult(mapRes);
	}




}
