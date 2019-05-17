package com.lwxf.industry4.webapp.domain.dao.aftersale;


import java.util.List;
import java.util.Map;

import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDao;
import com.lwxf.industry4.webapp.domain.dto.aftersale.AftersaleDto;
import com.lwxf.industry4.webapp.domain.dto.aftersale.AftersaleOrderDto;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.entity.aftersale.AftersaleApply;
import com.lwxf.mybatis.annotation.IBatisSqlTarget;
import com.lwxf.mybatis.utils.MapContext;


/**
 * 功能：
 * 
 * @author：SunXianWei(17838625030@163.com)
 * @created：2019-01-02 20:27:26
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@IBatisSqlTarget
public interface AftersaleApplyDao extends BaseDao<AftersaleApply, String> {

	PaginatedList<AftersaleDto> selectByFilter(PaginatedFilter paginatedFilter);

	AftersaleDto findAftersaleMessage(String aftersaleId);

	PaginatedList<AftersaleDto> findByFilter(PaginatedFilter paginatedFilter);

	AftersaleDto findOneById(String id);

	Integer findCountByCidAndType(MapContext mapContext);

	List<AftersaleApply> findAftersaleListByCid(MapContext mapContext);

	Integer findCountByOederIdAndType(Integer orderType, List orderIds);

	Integer findCountByCidAndStatus(MapContext mapContext);

	List<DateNum> findAftersaleByDate(MapContext mapContext);

	Integer findFactoryAftersaleApply(MapContext mapContext);

	Integer findTodayCount();

	Integer findThisWeekCount();

	Integer findThisMonthCount();

    List<Map> findAftersaleByOrderId(String orderId);

	PaginatedList<MapContext> findAftersaleApplyList(PaginatedFilter paginatedFilter);


	List<MapContext> findAftersaleProductByorderId(String resultOrderId);

	AftersaleOrderDto findOrderBaseInfoByOrderId(MapContext params);

	List<AftersaleApply> findByOrderId(String orderId);

	AftersaleApply findByAftersaleNo(String aftersaleOrderNo);
}