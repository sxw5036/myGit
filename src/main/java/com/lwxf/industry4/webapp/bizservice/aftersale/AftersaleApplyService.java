package com.lwxf.industry4.webapp.bizservice.aftersale;


import java.util.List;
import java.util.Map;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dto.aftersale.AftersaleDto;
import com.lwxf.industry4.webapp.domain.dto.aftersale.AftersaleOrderDto;
import com.lwxf.industry4.webapp.domain.dto.aftersale.AftersaleStatementDto;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.printTable.AftersalesPrintTableDto;
import com.lwxf.industry4.webapp.domain.dto.printTable.OrderPrintTableDto;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.industry4.webapp.bizservice.base.BaseService;
import com.lwxf.industry4.webapp.domain.entity.aftersale.AftersaleApply;


/**
 * 功能：
 * 
 * @author：SunXianWei(17838625030@163.com)
 * @created：2019-01-02 20:27:26
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface AftersaleApplyService extends BaseService <AftersaleApply, String> {

	PaginatedList<AftersaleDto> selectByFilter(PaginatedFilter paginatedFilter);

	PaginatedList<AftersaleDto> selectDtoByFilter(PaginatedFilter paginatedFilter);

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

	String getAftersaleOrderNo(String orderId,String orderNo);


	List<AftersaleApply> findAftersaleListByOrderId(String orderId);

	int deleteByResultOrderId(String orderId);

	Map<String,Long> countAftersale(String branchId);

	MapContext countAftersaleForPageIndex(String branchId);

	PaginatedList<MapContext> findWxAftersaleApplyList(PaginatedFilter paginatedFilter);

	AftersalesPrintTableDto findAftersalesPrintInfo(String id);

	OrderPrintTableDto findOrderPrintTable(MapContext mapContext);
}