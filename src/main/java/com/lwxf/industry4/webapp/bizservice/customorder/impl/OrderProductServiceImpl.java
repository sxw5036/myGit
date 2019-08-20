package com.lwxf.industry4.webapp.bizservice.customorder.impl;


import com.lwxf.industry4.webapp.bizservice.base.BaseServiceImpl;
import com.lwxf.industry4.webapp.bizservice.customorder.OrderProductService;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderFilesType;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dao.common.UploadFilesDao;
import com.lwxf.industry4.webapp.domain.dao.customorder.CustomOrderFilesDao;
import com.lwxf.industry4.webapp.domain.dao.customorder.OrderProductDao;
import com.lwxf.industry4.webapp.domain.dao.warehouse.FinishedStockItemDao;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDemandDto;
import com.lwxf.industry4.webapp.domain.dto.customorder.OrderProductDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockItemDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.OrderProduct;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.math.BigDecimal;
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
	@Resource(name = "finishedStockItemDao")
	private FinishedStockItemDao finishedStockItemDao;

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
		OrderProductDto oneById = this.dao.findOneById(id);
		if(oneById!=null){
			oneById.setUploadFiles(this.customOrderFilesDao.selectByOrderIdAndType(oneById.getCustomOrderId(),CustomOrderFilesType.ORDER_PRODUCT.getValue(),id));
		}
		return oneById;
	}

	@Override
	public List<OrderProductDto> findListByOrderId(String id) {
		List<OrderProductDto> listByOrderId = this.dao.findListByOrderId(id);
		for(OrderProductDto orderProductDto:listByOrderId){
			orderProductDto.setUploadFiles(this.customOrderFilesDao.selectByOrderIdAndType(id,CustomOrderFilesType.ORDER_PRODUCT.getValue(),orderProductDto.getId()));
		}
		return listByOrderId;
	}

	@Override
	public BigDecimal findCountPriceByOrderId(String id) {
		return this.dao.findCountPriceByOrderId(id);
	}

	@Override
	public BigDecimal findCountPriceByCreatedAndStatus(String beginTime, String endTime, String created,
													   Integer status,Integer type,Integer series) {
		MapContext params = MapContext.newOne();
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		params.put("status", status);
		params.put("created", created);
		params.put("type", type);
		params.put("series", series);
		return this.dao.findCountPriceByCreatedAndStatus(params);
	}
	@Override
	public Integer findCountNumByCreatedAndStatus(String beginTime, String endTime, String created,
													   Integer status,Integer type,Integer series) {
		MapContext params = MapContext.newOne();
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		params.put("status", status);
		params.put("created", created);
		params.put("type", type);
		params.put("series", series);
		return this.dao.findCountNumByCreatedAndStatus(params);
	}

	@Override
	public int deleteByOrderId(String orderId) {
		return this.dao.deleteByOrderId(orderId);
	}

	@Override
	public List<OrderProductDto> findProductsByOrderId(String id) {
		List<OrderProductDto> orderProductDtos=this.dao.findProductsByOrderId(id);
		for(OrderProductDto orderProductDto:orderProductDtos){
			String productId=orderProductDto.getId();
			List<FinishedStockItemDto> finishedStockItemDtos=finishedStockItemDao.findListByProductId(productId);
			orderProductDto.setFinishedStockItemDtos(finishedStockItemDtos);
		}
		return orderProductDtos;
	}

	@Override
	public List<OrderProductDto> findListByAftersaleId(String id) {
		return this.dao.findListByAftersaleId(id);
	}

	@Override
	public Integer findCountNumByCreatedAndType(String beginTime, String endTime, Integer type, String created,Integer series) {
		MapContext params = MapContext.newOne();
		params.put("beginTime", beginTime);
		params.put("endTime", endTime);
		params.put("type", type);
		params.put("series", series);
		params.put("created", created);
		return this.dao.findCountNumByCreatedAndType(params);
	}



}