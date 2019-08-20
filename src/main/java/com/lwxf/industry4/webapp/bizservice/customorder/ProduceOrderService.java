package com.lwxf.industry4.webapp.bizservice.customorder;


import java.util.Date;
import java.util.List;
import java.util.Map;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDemandDto;
import com.lwxf.industry4.webapp.domain.dto.customorder.ProduceOrderDto;
import com.lwxf.industry4.webapp.domain.dto.printTable.CoordinationPrintTableDto;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.industry4.webapp.bizservice.base.BaseService;
import com.lwxf.industry4.webapp.domain.entity.customorder.ProduceOrder;


/**
 * 功能：
 * 
 * @author：F_baisi(F_baisi@126.com)
 * @created：2019-04-08 15:09:45
 * @version：2019 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface ProduceOrderService extends BaseService <ProduceOrder, String> {

	PaginatedList<ProduceOrder> selectByFilter(PaginatedFilter paginatedFilter);

    List<Map> findByOrderId(String orderId);
	ProduceOrderDto findOneById(String id);

	PaginatedList<ProduceOrderDto> findListByFilter(PaginatedFilter paginatedFilter);

	List<ProduceOrder> findListByIds(List<String> ids);



	List<ProduceOrderDto> findListByOrderId(String id);

	List<ProduceOrderDto> findProduceOrderByProductId(String id);

	List<ProduceOrder> findIncompleteListByOrderId(String customOrderId,List<Integer> ways);

	int updatePayByOrderIdAndWays(String orderId, List<Integer> ways);

	List<ProduceOrder> findListByOrderIdAndTypesAndWays(String id, List<Integer> types, List<Integer> ways);

	int updateMapContextByIds(MapContext mapContext);

	int updatePlanTimeByIds(Date planTime, List ids);

	int deleteByOrderId(String orderId);

	int deleteByProductId(String pId);

	List findListOrderIdByPId(List ids);

	CoordinationPrintTableDto findCoordinationPrintInfo(String id);

	MapContext findCoordinationCount(String branchId);
}