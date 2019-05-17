package com.lwxf.industry4.webapp.facade.app.dealer.order.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.assemble.DispatchListService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderFilesService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderLogService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderProcessService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.dispatch.DispatchBillService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockItemService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockService;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderProcessDto;
import com.lwxf.industry4.webapp.domain.dto.dispatch.DispatchBillDto;
import com.lwxf.industry4.webapp.domain.entity.assemble.DispatchList;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderFiles;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBill;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStock;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStockItem;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.order.OrderTrackingFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/17 14:26
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("orderTrackingFacade")
public class OrderTrackingFacadeImpl extends BaseFacadeImpl implements OrderTrackingFacade {
    @Resource(name = "customOrderLogService")
    private CustomOrderLogService customOrderLogService;

    @Resource(name = "dispatchBillService")
    private DispatchBillService dispatchBillService;

    @Resource(name = "finishedStockItemService")
    private FinishedStockItemService finishedStockItemService;

    @Resource(name = "dispatchListService")
    private DispatchListService dispatchListService;
    @Resource(name = "customOrderProcessService")
    private CustomOrderProcessService customOrderProcessService;
    @Resource(name = "customOrderFilesService")
    private CustomOrderFilesService customOrderFilesService;
    @Resource(name = "customOrderService")
    private CustomOrderService customOrderService;
    @Resource(name = "finishedStockService")
    private FinishedStockService finishedStockService;

    @Override
    public RequestResult findProcessByOrderId(String orderId) {
        List<CustomOrderProcessDto> customOrderProcessDtoList = this.customOrderProcessService.findListByOrderId(orderId);
        if (null==customOrderProcessDtoList||customOrderProcessDtoList.size()==0){
            return ResultFactory.generateRequestResult(customOrderProcessDtoList);
        }
        for (CustomOrderProcessDto processDto:customOrderProcessDtoList){
            Integer type = processDto.getType();
            String id = processDto.getId();
            switch (type){
                case 0:processDto.setName("备料");break;
                case 1:processDto.setName("加工");break;
                case 2:processDto.setName("封边");break;
                case 3:processDto.setName("修边");break;
                case 4:processDto.setName("质检");break;
                case 5:processDto.setName("包装");break;
                case 6:processDto.setName("待入库");break;
                case 7:processDto.setName("已入库");break;
            }
            CustomOrderFiles customOrderFile = this.customOrderFilesService.findByBelongIdAndTypeAndOrderId(orderId, 3, id);
            if (customOrderFile==null){
                processDto.setPath("");
            }else {
                processDto.setPath(customOrderFile.getPath());
            }
        }
        return ResultFactory.generateRequestResult(customOrderProcessDtoList);
    }


    @Override
    public RequestResult findProcessVideosByOrderId(String orderId, String processId) {
        List<CustomOrderFiles> filesList = this.customOrderFilesService.selectByOrderIdAndType(orderId, null, processId);
        if (null==filesList||filesList.size()==0){
            return ResultFactory.generateRequestResult(filesList);
        }
        List<MapContext> list = new ArrayList<>();
        for (CustomOrderFiles files:filesList){
            MapContext params = MapContext.newOne();
            params.put("path",files.getPath());
            params.put("type", files.getType());
            list.add(params);
        }
        return ResultFactory.generateRequestResult(list);
    }

    @Override
    public RequestResult findDispatchsByOrderId(String orderId) {
        List<DispatchBillDto> dispatchBillDtoList = this.dispatchBillService.findDispatchsByOrderId(orderId);
        return ResultFactory.generateRequestResult(dispatchBillDtoList);
    }


    @Override
    public RequestResult findByDispatchId(String dispatchId) {
        DispatchBillDto disBill = this.dispatchBillService.findDispatchsBillById(dispatchId);
        List<FinishedStockItem> finishedStockItemList = this.finishedStockItemService.findByDispatchId(dispatchId);
        String notes = "";
        for (FinishedStockItem finishedStockItem:finishedStockItemList){
            Integer type = finishedStockItem.getType();
            switch (type){
                case 0: notes+="衣柜备注："+finishedStockItem.getNotes()+"  |  ";break;
                case 1: notes+="橱柜备注："+finishedStockItem.getNotes()+"  |  ";break;
                case 2: notes+="门板备注："+finishedStockItem.getNotes()+"  |  ";break;
                case 3: notes+="五金备注："+finishedStockItem.getNotes()+"  |  ";break;
            }
            finishedStockItem.setNotes(null);
            finishedStockItem.setAmount(1);
        }
        if (LwxfStringUtils.isNotBlank(notes)){
            int i = notes.lastIndexOf("|");
            notes = notes.substring(0,i);
        }
        MapContext params = MapContext.newOne();
        params.put("disBill",disBill);
        params.put("notes",notes);
        params.put("finishedStockItemList",finishedStockItemList);
        return ResultFactory.generateRequestResult(params);
    }

    /**
     * 确认收货并创建派工单
     * @param dispatchId
     * @param uid
     * @param cid
     * @param orderId
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult putStatusByDispatchId(String dispatchId,String uid,String cid,String orderId) {

        MapContext value = MapContext.newOne();
        MapContext params = MapContext.newOne();
        params.put("status",3);
        params.put("id",dispatchId);
        int i = this.dispatchBillService.updateByMapContext(params);
        if (i==0){
            value.put("value","你已收货，不可重复收货！");
            return ResultFactory.generateRequestResult(value);
        }
        List<DispatchBillDto> dispatchs = this.dispatchBillService.findDispatchsByOrderId(orderId);
        List<Integer> list = new ArrayList<>();
//        for (DispatchBillDto billDto:dispatchs){
//            Integer status = billDto.getStatus();
//            list.add(status);
//        }
//        Set s = new HashSet(list);
        //查询该订单下已收货的产品的包数
        int count = this.dispatchBillService.findYSHItemCount(orderId);
        FinishedStock finishedStock = this.finishedStockService.findByOrderId(orderId);
        if (count==finishedStock.getPackages()){
            params.put("id",orderId);
            params.put("status",OrderStatus.RECEIVED.getValue());
            params.put("deliveryDate",DateUtil.getSystemDate());
            this.customOrderService.updateByMapContext(params);
        }

        //查询配送信息
        DispatchBill dispatchBill = this.dispatchBillService.findById(dispatchId);
        //查询配送的货物的信息
        List<FinishedStockItem> finishedStockItems = this.finishedStockItemService.findByDispatchId(dispatchId);
        for (FinishedStockItem finishedStockItem : finishedStockItems){
            Integer type = finishedStockItem.getType();
            DispatchList dispatchList = new DispatchList();
            if (type==0){
                dispatchList.setName("柜体");
            }
            if (type==1){
                dispatchList.setName("门板");
            }
            if (type==2){
                dispatchList.setName("五金");
            }
            dispatchList.setCreator(uid);
            dispatchList.setCreated(DateUtil.getSystemDate());
            dispatchList.setStatus(0);
            dispatchList.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.DISPATCH_LIST_NO));
            dispatchList.setTakeDelivery(true);
            dispatchList.setOrderId(orderId);
            dispatchList.setCompanyId(cid);
            dispatchList.setDispatchBillId(dispatchBill.getId());
            this.dispatchListService.add(dispatchList);
        }
        value.put("value","确认收货，已生成安装单！");
        return ResultFactory.generateRequestResult(value);
    }
}

