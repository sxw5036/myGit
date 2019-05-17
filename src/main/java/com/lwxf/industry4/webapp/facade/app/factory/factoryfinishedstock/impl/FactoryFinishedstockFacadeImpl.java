package com.lwxf.industry4.webapp.facade.app.factory.factoryfinishedstock.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.aftersale.AftersaleApplyService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.customorder.ProduceOrderService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockItemService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockService;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.dto.aftersale.AftersaleOrderDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStock;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStockItem;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factoryfinishedstock.FactoryFinishedstockFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/4/18 0018 10:37
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "factoryFinishedstockFacade")
public class FactoryFinishedstockFacadeImpl extends BaseFacadeImpl implements FactoryFinishedstockFacade {
	@Resource(name = "finishedStockService")
	private FinishedStockService finishedStockService;
	@Resource(name = "finishedStockItemService")
	private FinishedStockItemService finishedStockItemService;
	@Resource(name = "customOrderService")
	private CustomOrderService customOrderService;
	@Resource(name = "aftersaleApplyService")
	private AftersaleApplyService aftersaleApplyService;


	/**
	 * 条件查询
	 * 入库列表
	 *
	 * @param pageNum
	 * @param pageSize
	 * @param mapContext
	 * @param isIn
	 * @return
	 */
	@Override
	public RequestResult findFactoryFinishedstockList(Integer pageNum, Integer pageSize, MapContext mapContext, String isIn) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		paginatedFilter.setFilters(mapContext);
		if(isIn==null||isIn.equals("")){
			mapContext.put("isIn",false);
		}else {
			if(Integer.valueOf(isIn)==0){
				mapContext.put("isIn",false);
			}
			if(Integer.valueOf(isIn)==1){
				mapContext.put("isIn",true);
			}

		}
		PaginatedList<MapContext>  finishStockItemList = this.finishedStockItemService.findListByOrderId(paginatedFilter);
		MapContext result = MapContext.newOne();
		result.put("finishedStockList", finishStockItemList.getRows());
		return ResultFactory.generateRequestResult(result,finishStockItemList.getPagination());
	}

	/**
	 * 入库详情
	 *
	 * @param finishedStockItemId
	 * @param orderId
	 * @return
	 */
	@Override
	public RequestResult findFactoryFinishedStockItemInfo(String finishedStockItemId, String orderId) {
		//订单相关基础信息
		MapContext params = MapContext.newOne();
		params.put("orderId", orderId);
		AftersaleOrderDto customOrder = this.aftersaleApplyService.findOrderBaseInfoByOrderId(params);
		if (customOrder == null) {
			return ResultFactory.generateResNotFoundResult();
		}
		//入库包裹信息
		MapContext finishedStockItem = this.finishedStockItemService.findOneByFinishedStockitemId(finishedStockItemId);
		MapContext result = MapContext.newOne();
		result.put("orderBaseInfo", customOrder);
		result.put("finishedStockItem", finishedStockItem);
		return ResultFactory.generateRequestResult(result);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addFinishedStock(String orderId, String finishedStockItemId) {
		MapContext params = MapContext.newOne();
		MapContext result = MapContext.newOne();
		params.put("orderId", orderId);
		AftersaleOrderDto customOrder = this.aftersaleApplyService.findOrderBaseInfoByOrderId(params);
		if (finishedStockItemId != null && !finishedStockItemId.equals("")) {
			FinishedStockItem finishedStockItem = this.finishedStockItemService.findOneById(finishedStockItemId);
			Date packCreated = finishedStockItem.getCreated();
			String creator = finishedStockItem.getCreator();
			User user = AppBeanInjector.userService.findByUserId(creator);
			String packName = user.getName();
			Integer type = finishedStockItem.getType();
			String barcode = finishedStockItem.getBarcode();
			result.put("packCreated", packCreated);
			result.put("packName", packName);
			result.put("barcode", barcode);
			result.put("type", type);
		}
		result.put("orderBaseInfo", customOrder);

		return ResultFactory.generateRequestResult(result);
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateFinishedStockItem(String finishedStockItemId, MapContext mapContext, HttpServletRequest request) {
		String operator = mapContext.getTypedValue("inputer",String.class);//入库人
		if(operator==null||operator.equals("")){
			operator=request.getHeader("X-UID");
		}
		String operated = mapContext.getTypedValue("operated", String.class);//入库时间
		if(operated==null||operated.equals("")){
			operated = DateUtil.dateTimeToString(DateUtil.getSystemDate(), DateUtil.FORMAT_DATETIME);
		}
		String notes = mapContext.getTypedValue("notes", String.class);//入库说明
		Boolean isIn = true;//入库状态
		MapContext params = MapContext.newOne();
		params.put("id", finishedStockItemId);
		params.put("operator", operator);
		params.put("operated", operated);
		params.put("ins", isIn);
		params.put("notes", notes);
		this.finishedStockItemService.updateByMapContext(params);
		String orderId=this.finishedStockItemService.findOrderIdByFinishedItemId(finishedStockItemId);
		CustomOrder customOrder=this.customOrderService.findByOrderId(orderId);
		Integer status=customOrder.getStatus();
		Integer statusValue= OrderStatus.TO_WAREHOUSE.getValue();//待入库状态
		Integer statusvalue=OrderStatus.TO_DISPATCH.getValue();
		//如果订单状态为待入库,则改为待配送
		if(status==statusValue){
			MapContext mapContext1=MapContext.newOne();
			mapContext1.put("id",orderId);
			mapContext1.put("status",statusvalue);
			this.customOrderService.updateByMapContext(mapContext1);
		}
		return ResultFactory.generateSuccessResult();
	}

	/**
	 * 外协 特供 五金打包入库
	 * @param mapContext
	 * @param request
	 * @return
	 */
	@Override
	public RequestResult addFinishedStockItem(MapContext mapContext, HttpServletRequest request) {
		String uid=request.getHeader("X-UID");
		//创建入库单
		FinishedStock finishedStock=new FinishedStock();
		finishedStock.setStorageId("4ujh7687hips");
		finishedStock.setOrderId(mapContext.getTypedValue("orderId",String.class));
		finishedStock.setPackages(0);
		finishedStock.setStatus(0);
		finishedStock.setNotes(mapContext.getTypedValue("notes",String.class));
		finishedStock.setCreator(uid);
		finishedStock.setCreated(DateUtil.getSystemDate());
		finishedStock.setOrderNo(mapContext.getTypedValue("orderNo",String.class));
		finishedStock.setWay(1);
		this.finishedStockService.add(finishedStock);
		//创建入库明细
		String finishedStockItems=mapContext.getTypedValue("finishedStockItems",String.class);
		String[] finishedItems=finishedStockItems.split(",");
		for(int i=0;i<finishedItems.length;i++){
			FinishedStockItem finishedStockItem=new FinishedStockItem();
			finishedStockItem.setFinishedStockId(finishedStock.getId());
			finishedStockItem.setType(mapContext.getTypedValue("type",Integer.class));
			finishedStockItem.setCreator(uid);
			finishedStockItem.setCreated(DateUtil.getSystemDate());
			finishedStockItem.setBarcode(mapContext.getTypedValue("barcode",String.class));
			finishedStockItem.setShipped(false);
			finishedStockItem.setNotes(mapContext.getTypedValue("notes",String.class));
			finishedStockItem.setIn(true);
			finishedStockItem.setLocation(null);
			finishedStockItem.setOperator(uid);
			finishedStockItem.setOperated(DateUtil.getSystemDate());
           this.finishedStockItemService.add(finishedStockItem);
		}
		return ResultFactory.generateSuccessResult();
	}


}
