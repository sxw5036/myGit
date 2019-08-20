package com.lwxf.industry4.webapp.bizservice.customorder.impl;


import com.lwxf.industry4.webapp.bizservice.base.BaseServiceImpl;
import com.lwxf.industry4.webapp.bizservice.customorder.ProduceOrderService;
import com.lwxf.industry4.webapp.common.enums.customorder.CustomOrderFilesType;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dao.customorder.CustomOrderFilesDao;
import com.lwxf.industry4.webapp.domain.dao.customorder.OrderProductDao;
import com.lwxf.industry4.webapp.domain.dao.customorder.ProduceFlowDao;
import com.lwxf.industry4.webapp.domain.dao.customorder.ProduceOrderDao;
import com.lwxf.industry4.webapp.domain.dto.customorder.OrderProductDto;
import com.lwxf.industry4.webapp.domain.dto.printTable.CoordinationPrintTableDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.ProduceOrder;
import com.lwxf.industry4.webapp.domain.dto.customorder.ProduceOrderDto;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 功能：
 * 
 * @author：F_baisi(F_baisi@126.com)
 * @created：2019-04-08 15:09:45
 * @version：2019 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Service("produceOrderService")
public class ProduceOrderServiceImpl extends BaseServiceImpl<ProduceOrder, String, ProduceOrderDao> implements ProduceOrderService {


	@Resource

	@Override	public void setDao( ProduceOrderDao produceOrderDao) {
		this.dao = produceOrderDao;
	}

	@Resource(name = "produceFlowDao")
	private ProduceFlowDao produceFlowDao;

	@Resource(name = "customOrderFilesDao")
	private CustomOrderFilesDao customOrderFilesDao;
	@Resource(name = "orderProductDao")
	private OrderProductDao orderProductDao;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<ProduceOrder> selectByFilter(PaginatedFilter paginatedFilter) {
		//
		return this.dao.selectByFilter(paginatedFilter) ;
	}

	@Override
	public ProduceOrderDto findOneById(String id) {
		ProduceOrderDto oneById = this.dao.findOneById(id);
		if(oneById!=null){
			oneById.setUploadFiles(this.customOrderFilesDao.selectByOrderIdAndType(oneById.getCustomOrderId(),CustomOrderFilesType.PRODUCE_ORDER.getValue(),oneById.getId()));
		}
		return oneById;
	}

	@Override
	public PaginatedList<ProduceOrderDto> findListByFilter(PaginatedFilter paginatedFilter) {
		PaginatedList<ProduceOrderDto> listByFilter = this.dao.findListByFilter(paginatedFilter);
		for(ProduceOrderDto produceOrderDto:listByFilter.getRows()){
			OrderProductDto orderProductDto=this.orderProductDao.findOneById(produceOrderDto.getOrderProductId());
			produceOrderDto.setOrderProductDto(orderProductDto);
			produceOrderDto.setProduceFlowDtos(this.produceFlowDao.findListByProduceOrderId(produceOrderDto.getId()));
			produceOrderDto.setUploadFiles(this.customOrderFilesDao.selectByOrderIdAndType(produceOrderDto.getCustomOrderId(),CustomOrderFilesType.PRODUCE_ORDER.getValue(),produceOrderDto.getId()));
		}
		return listByFilter;
	}

	@Override
	public List<ProduceOrder> findListByIds(List<String> ids) {
		return this.dao.findListByIds(ids);
	}



	@Override
	public List<ProduceOrderDto> findListByOrderId(String id) {
		List<ProduceOrderDto> listByOrderId = this.dao.findListByOrderId(id);
		for(ProduceOrderDto produceOrderDto :listByOrderId){
			produceOrderDto.setUploadFiles(this.customOrderFilesDao.selectByOrderIdAndType(id,CustomOrderFilesType.PRODUCE_ORDER.getValue(),produceOrderDto.getId()));
		}
		return listByOrderId;
	}
	@Override
	public List<ProduceOrderDto> findProduceOrderByProductId(String id) {
		List<ProduceOrderDto> listByOrderId = this.dao.findProduceOrderByProductId(id);
		return listByOrderId;
	}

	@Override
	public List<ProduceOrder> findIncompleteListByOrderId(String customOrderId,List<Integer> ways) {
		return this.dao.findIncompleteListByOrderId(customOrderId,ways);
	}

	@Override
	public int updatePayByOrderIdAndWays(String orderId, List<Integer> ways) {
		return this.dao.updatePayByOrderIdAndWays(orderId,ways);
	}

	@Override
	public List<ProduceOrder> findListByOrderIdAndTypesAndWays(String id, List<Integer> types, List<Integer> ways) {
		return this.dao.findListByOrderIdAndTypesAndWays(id,types,ways);
	}

	@Override
	public int updateMapContextByIds(MapContext mapContext) {
		return this.dao.updateMapContextByIds(mapContext);
	}

	@Override
	public int updatePlanTimeByIds(Date planTime, List ids) {
		return this.dao.updatePlanTimeByIds(planTime,ids);
	}

	@Override
	public int deleteByOrderId(String orderId) {
		return this.dao.deleteByOrderId(orderId);
	}

	@Override
	public int deleteByProductId(String pId) {
		return this.dao.deleteByProductId(pId);
	}

	@Override
	public List findListOrderIdByPId(List ids) {
		return this.dao.findListOrderIdByPId(ids);
	}

	@Override
	public CoordinationPrintTableDto findCoordinationPrintInfo(String id) {
		return this.dao.findCoordinationPrintInfo(id);
	}

	@Override
	public MapContext findCoordinationCount(String branchId) {
		return this.dao.findCoordinationCount(branchId);
	}

	@Override
	public List<Map> findByOrderId(String orderId) {
		return this.dao.findByOrderId(orderId);
	}
}