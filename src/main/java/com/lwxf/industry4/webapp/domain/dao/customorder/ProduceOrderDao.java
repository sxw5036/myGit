package com.lwxf.industry4.webapp.domain.dao.customorder;


import java.util.Date;
import java.util.List;
import java.util.Map;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dto.customorder.ProduceOrderDto;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.mybatis.annotation.IBatisSqlTarget;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDao;
import com.lwxf.industry4.webapp.domain.entity.customorder.ProduceOrder;


/**
 * 功能：
 * 
 * @author：F_baisi(F_baisi@126.com)
 * @created：2019-04-08 15:09:45
 * @version：2019 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@IBatisSqlTarget
public interface ProduceOrderDao extends BaseDao<ProduceOrder, String> {

	PaginatedList<ProduceOrder> selectByFilter(PaginatedFilter paginatedFilter);

    List<Map> findByOrderId(String orderId);

	ProduceOrderDto findOneById(String id);

	PaginatedList<ProduceOrderDto> findListByFilter(PaginatedFilter paginatedFilter);

	List<ProduceOrder> findListByIds(List<String> ids);

	List<ProduceOrderDto> findListByOrderId(String id);

	List<ProduceOrder> findIncompleteListByOrderId(String customOrderId);

	int updatePayByOrderIdAndWays(String orderId, List<Integer> ways);

	List<ProduceOrder> findListByOrderIdAndWays(String id, List<Integer> ways);

	int updateStateByIds(List<String> ids, int state);

	int updatePlanTimeByIds(Date planTime, List ids);


	PaginatedList<ProduceOrder> findProduceOrderList(PaginatedFilter paginatedFilter);
}