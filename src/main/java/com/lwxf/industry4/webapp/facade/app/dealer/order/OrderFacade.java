package com.lwxf.industry4.webapp.facade.app.dealer.order;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderInfoDto;
import com.lwxf.industry4.webapp.domain.dto.customorder.ProduceOrderDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockItemDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.*;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStockItem;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/6 16:34
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface OrderFacade extends BaseFacade {

    RequestResult selectByCondition(Integer pageNum,Integer pageSize,MapContext params,String uid,String cid);
    RequestResult selectByOrderId(String id);
    RequestResult addOrder(CustomOrder customOrder,String uid,String cid);
    RequestResult updateOrder(MapContext updateMap, HttpServletRequest request);
    RequestResult deleteByOrderId(String orderId, HttpServletRequest request);
    RequestResult commitOrderDemand(MapContext params);

    RequestResult findFinishedOrderList(List<Integer> integers,Integer pageNum,Integer pageSize,boolean finishedOrder);

    RequestResult updateOrderStatus(String id, Integer status,MapContext mapContext);

    RequestResult findOrderList(MapContext mapContext,Integer pageNum,Integer pageSize);

	RequestResult updateOrderDesigner(String userId,String id);

	RequestResult findOrderInfo(String id);

	RequestResult findOrderDemand(String id, String demandId);

	RequestResult findOrderDesignId(String id, String designId);

	RequestResult addOrderDesign(String id,CustomOrderDesign customOrderDesign);

	RequestResult updateDesign(String id, String designId, MapContext mapContext);

	RequestResult deleteDesign(String id, String designId);

	RequestResult uploadFile(String id, String designId, List<MultipartFile> multipartFileList,Integer category);

	RequestResult deleteFile(String id, String designId, String fileId);

	RequestResult factoryAddOrder(CustomOrderInfoDto customOrderInfoDto, String aftersaleId, Payment payment);

	RequestResult factoryUpdateOrder(String id, MapContext mapContext);

	RequestResult findAmountInfo(String orderId);

	RequestResult customerOrderCount(String companyId, HttpServletRequest request, MapContext mapContext);

	RequestResult addProduceOrder(String id, ProduceOrderDto produceOrderDto, List<String> fileIds);

	RequestResult addOrderProduct(String id, OrderProduct orderProduct);

	RequestResult updateOrderProduct(String id, String pId, MapContext mapContext);

	RequestResult deleteOrderProduct(String id, String pId);

	RequestResult updateProduceOrder(String id, String pId, MapContext mapContext);

	RequestResult deleteProduceOrder(String id, String pId);

	RequestResult findProducesList(MapContext mapContext, Integer pageNum, Integer pageSize);

	RequestResult producesPlansListStart(List<String> ids);

	RequestResult addProduceFlow(String id, ProduceFlow produceFlow);

	RequestResult produceFlowPacking(String id, ProduceFlow produceFlow);

	RequestResult addOrderPack(String id, List<FinishedStockItemDto> finishedStockItemList);

	RequestResult orderPacked(String id);

	RequestResult findPacksOrderList(Integer pageNum, Integer pageSize, MapContext mapContext);

	RequestResult updateProducesOrderPlanTime(List ids,Date planTime);

	RequestResult endCoordination(String id);

	RequestResult findAllDesign(MapContext mapContext,Integer pageNum,Integer pageSize);

	RequestResult findOrderPackagesNo(String id);

	RequestResult uploadOrderFiles(String id, Integer type, String resId, List<MultipartFile> multipartFileList);

	RequestResult deleteCustomOrderFile(String id, String fileId);
}
