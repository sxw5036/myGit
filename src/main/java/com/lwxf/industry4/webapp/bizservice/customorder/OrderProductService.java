package com.lwxf.industry4.webapp.bizservice.customorder;


import com.lwxf.industry4.webapp.bizservice.base.BaseService;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDemandDto;
import com.lwxf.industry4.webapp.domain.dto.customorder.OrderProductDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.OrderProduct;

import java.util.List;
import java.util.Map;


/**
 * 功能：
 * 
 * @author：F_baisi(F_baisi@126.com)
 * @created：2019-04-11 17:38:26
 * @version：2019 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface OrderProductService extends BaseService <OrderProduct, String> {

	PaginatedList<OrderProduct> selectByFilter(PaginatedFilter paginatedFilter);

	List<Map> findByOrderId(String orderId);

	OrderProductDto findOneById(String id);

	List<OrderProductDto> findListByOrderId(String id);
}