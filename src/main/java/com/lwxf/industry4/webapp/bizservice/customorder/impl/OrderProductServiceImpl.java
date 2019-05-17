package com.lwxf.industry4.webapp.bizservice.customorder.impl;


import com.lwxf.industry4.webapp.bizservice.base.BaseServiceImpl;
import com.lwxf.industry4.webapp.bizservice.customorder.OrderProductService;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderFilesType;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dao.common.UploadFilesDao;
import com.lwxf.industry4.webapp.domain.dao.customorder.CustomOrderFilesDao;
import com.lwxf.industry4.webapp.domain.dao.customorder.OrderProductDao;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDemandDto;
import com.lwxf.industry4.webapp.domain.dto.customorder.OrderProductDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.OrderProduct;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
@Service("orderProductService")
public class OrderProductServiceImpl extends BaseServiceImpl<OrderProduct, String, OrderProductDao> implements OrderProductService {


	@Resource

	@Override	public void setDao( OrderProductDao orderProductDao) {
		this.dao = orderProductDao;
	}

	@Resource(name = "customOrderFilesDao")
	private CustomOrderFilesDao customOrderFilesDao;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<OrderProduct> selectByFilter(PaginatedFilter paginatedFilter) {
		//
		return this.dao.selectByFilter(paginatedFilter) ;
	}

	@Override
	public List<Map> findByOrderId(String orderId) {
		return this.dao.findByOrderId(orderId);
	}

	@Override
	public OrderProductDto findOneById(String id) {
		return this.dao.findOneById(id);
	}

	@Override
	public List<OrderProductDto> findListByOrderId(String id) {
		List<OrderProductDto> listByOrderId = this.dao.findListByOrderId(id);
		for(OrderProductDto orderProductDto:listByOrderId){
			orderProductDto.setUploadFiles(this.customOrderFilesDao.selectByOrderIdAndType(id,CustomOrderFilesType.ORDER_PRODUCT.getValue(),orderProductDto.getId()));
		}
		return listByOrderId;
	}


}