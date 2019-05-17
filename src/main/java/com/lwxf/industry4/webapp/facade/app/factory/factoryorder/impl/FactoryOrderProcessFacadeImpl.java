package com.lwxf.industry4.webapp.facade.app.factory.factoryorder.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.customorder.ProduceFlowService;
import com.lwxf.industry4.webapp.bizservice.customorder.ProduceOrderService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockItemService;
import com.lwxf.industry4.webapp.bizservice.warehouse.FinishedStockService;
import com.lwxf.industry4.webapp.bizservice.warehouse.StorageService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.company.EmployeeStatus;
import com.lwxf.industry4.webapp.common.enums.customorder.ProduceFlowNode;
import com.lwxf.industry4.webapp.common.enums.customorder.ProduceOrderState;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.enums.product.ProductCategoryId;
import com.lwxf.industry4.webapp.common.enums.storage.FinishedStockStatus;
import com.lwxf.industry4.webapp.common.enums.storage.FinishedStockWay;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockItemDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.customorder.ProduceFlow;
import com.lwxf.industry4.webapp.domain.entity.customorder.ProduceOrder;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStock;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStockItem;
import com.lwxf.industry4.webapp.domain.entity.warehouse.Storage;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.factoryorder.FactoryOrderProcessFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/4/1 13:13
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "factoryOrderProcessFacade")
public class FactoryOrderProcessFacadeImpl extends BaseFacadeImpl implements FactoryOrderProcessFacade {
    @Resource(name = "customOrderService")
    private CustomOrderService customOrderService;
    @Resource(name = "produceFlowService")
    private ProduceFlowService produceFlowService;
    @Resource(name = "produceOrderService")
    private ProduceOrderService produceOrderService;
    @Resource(name = "finishedStockService")
    private FinishedStockService finishedStockService;
    @Resource(name = "storageService")
    private StorageService storageService;
    @Resource(name = "finishedStockItemService")
    private FinishedStockItemService finishedStockItemService;
    @Resource(name = "companyEmployeeService")
    private CompanyEmployeeService companyEmployeeService;


    /**
     * 生产跟进 和  生产执行
     * @return
     */
    @Override
    public RequestResult findProcessFollow(MapContext params,Pagination pagination) {
        List<Map> processFollowList = this.findProcessList(null, null, params, pagination);
        return ResultFactory.generateRequestResult(processFollowList);
    }


    /**
     * 生产跟进列表
     * @param deliveryDate
     * @param created
     * @param params
     * @param pagination
     * @return
     */
    @Override
    public RequestResult findProcessFollowList(String deliveryDate, String created, MapContext params, Pagination pagination) {
        //params.put("status",OrderStatus.TO_PRODUCTION.getValue());
        List<Map> processList = this.findProcessList(deliveryDate, created, params, pagination);
        return ResultFactory.generateRequestResult(processList);
    }


    /**
     * 生产跟进详情
     * @return
     */
    @Override
    public RequestResult findProcessFollowInfo(String orderId) {
        MapContext result = MapContext.newOne();
        //订单的信息
        Map orderBaseInfo = this.findOrderBaseInfo(orderId,2);
        // 查询拆单的信息
        List<Map> produceOrder = this.produceOrderService.findByOrderId(orderId);
        List<Map> ii = new ArrayList<>();
        for (Map map:produceOrder){
            String id =(String) map.get("id");
            Integer way =(Integer) map.get("way");
            Integer type =(Integer) map.get("type");

            String typeName = (String) map.get("typeName");
            String wayName = (String) map.get("wayName");
            String name = typeName + "--" + wayName;
            map.put("name", name);
            List<Map> produceFlow = this.produceFlowService.findByProduceOrderId(id);
            map.put("produceFlow", produceFlow);
            map.remove("typeName");
            map.remove("wayName");
            map.remove("type");
            map.remove("way");
            map.remove("notes");
            map.remove("amount");
            map.remove("coordinationName");
            map.remove("count");
            map.remove("coordinationBank");
            switch (type) {
                case 0://柜体
                    break;
                case 1:
                    switch (way) {
                        case 0://门板自产
                            break;
                        case 1://门板外协
                            ii.add(map);
                            break;
                        default://门板特供实木
                            ii.add(map);
                            break;
                    }
                    break;
                default://五金
                    ii.add(map);
                    break;
            }
        }
        for (Map map:ii){
            produceOrder.remove(map);
        }


        //查询各种包裹
        Set<Map> set = new HashSet<>();
        set.add(new HashMap(){
            {
                put("name", "柜体");
                put("value", 0);
            }
        });//柜体
        set.add(new HashMap(){
            {
                put("name", "门板-自产");
                put("value", 1);
            }
        });//门板-自产
        set.add(new HashMap(){
            {
                put("name", "门板-外协");
                put("value", 2);
            }
        });//门板-外协
        set.add(new HashMap(){
            {
                put("name", "门板-特供实木");
                put("value", 3);
            }
        });//门板-特供实木
        set.add(new HashMap(){
            {
                put("name", "五金");
                put("value", 4);
            }
        });//五金
        Map stor = this.storageService.findByOrderId(orderId);
        List<Map> storages = new ArrayList<>();
        MapContext orderIdAndType = MapContext.newOne();
        orderIdAndType.put("orderId", orderId);
        for (Map map:set) {
            orderIdAndType.put("type", map.get("value"));
            orderIdAndType.put("isIn", true);
            List<FinishedStockItemDto> FinishedStockItemDtos = this.finishedStockItemService.findByOrderIdAndType(orderIdAndType);
            Integer couNum = FinishedStockItemDtos.size();
            if (FinishedStockItemDtos != null && couNum > 0) {
                String barcodes = "";
                for (FinishedStockItemDto dto : FinishedStockItemDtos) {
                    String barcode = dto.getBarcode();
                    barcodes = barcodes + ";" + barcode;
                }
                int i = barcodes.indexOf(";");
                barcodes = barcodes.substring(i + 1);
                String operatorName = FinishedStockItemDtos.get(0).getOperatorName();
                Date operated = FinishedStockItemDtos.get(0).getOperated();
                String notes = FinishedStockItemDtos.get(0).getNotes();
                MapContext storage = MapContext.newOne();
                storage.put("storageName", stor.get("storageName"));
                storage.put("barcodes", barcodes);
                storage.put("couNum", couNum);
                storage.put("operatorName", operatorName);
                storage.put("operated", operated);
                storage.put("notes", notes);
                storage.put("name", map.get("name"));
                storages.add(storage);
            }
        }
            //查询是否下车间（下车间了返回下车间的信息/没下车间返回操作人的列表）
            String documentaryNotes =(String) orderBaseInfo.get("documentaryNotes");
            Date documentaryTime =(Date) orderBaseInfo.get("documentaryTime");
            String merchandiserName =(String) orderBaseInfo.get("merchandiserName");
            MapContext goWorkInfo = MapContext.newOne();
            if (documentaryTime!=null&&LwxfStringUtils.isNotBlank(merchandiserName)){
                goWorkInfo.put("documentaryNotes", documentaryNotes);
                goWorkInfo.put("documentaryTime", documentaryTime);
                goWorkInfo.put("merchandiserName", merchandiserName);
                goWorkInfo.put("documentaryStatus", "1");//已下车间
            }else {
                goWorkInfo.put("documentaryStatus", "0");//未下车间
                MapContext cidAndStatus = MapContext.newOne();
                cidAndStatus.put(WebConstant.KEY_ENTITY_COMPANY_ID, WebUtils.getCurrCompanyId());
                cidAndStatus.put("status", EmployeeStatus.NORMAL.getValue());
                List<Map> empName = this.companyEmployeeService.findListCidAndStatus(cidAndStatus);
                goWorkInfo.put("empName", empName);
            }

        result.put("storage", storages);
        result.put("orderBaseInfo", orderBaseInfo);
        result.put("goWorkInfo", goWorkInfo);
        result.put("orderProduce", produceOrder);
        return ResultFactory.generateRequestResult(result);
    }


    /**
     * 下车间
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult goWorkshop(MapContext mapContext) {
        this.customOrderService.updateByMapContext(mapContext);
        List<Map> produceOrder = this.produceOrderService.findByOrderId((String) mapContext.get("orderId"));
        List<String> ids = new ArrayList<>();
        for (Map map:produceOrder){
            Integer way = (Integer) map.get("way");
            Integer type = (Integer) map.get("type");
            String id =(String) map.get("id");
            switch (type) {
                case 0://柜体
                    ids.add(id);
                    break;
                case 1:
                    switch (way) {
                        case 0://门板自产
                            ids.add(id);
                            break;
                    }
            }
        }
        this.produceOrderService.updateStateByIds(ids,ProduceOrderState.IN_PRODUCTION.getValue());
        return ResultFactory.generateRequestResult(mapContext);
    }



    /**
     * 查询生产执行的信息
     * @param orderId
     * @return
     */
    @Override
    public RequestResult findProcessExecuteInfo(String orderId) {
        Map orderBaseInfo = this.findOrderBaseInfo(orderId,2);
        // 查询拆单的信息
        List<Map> produceOrder = this.produceOrderService.findByOrderId(orderId);
        List<Map> ii = new ArrayList<>();
        for (int i = 0;i<produceOrder.size();i++){
            Map map = produceOrder.get(i);
            String id =(String) map.get("id");
            String typeName = (String) map.get("typeName");
            String wayName = (String) map.get("wayName");
            Integer way = (Integer) map.get("way");
            Integer type = (Integer) map.get("type");
            String name = typeName + "--" + wayName;
            map.put("name", name);
            List<Map> produceFlow = this.produceFlowService.findByProduceOrderId(id);
            map.put("produceFlow", produceFlow);
            map.remove("typeName");
            map.remove("wayName");
            map.remove("way");
            map.remove("notes");
            map.remove("amount");
            map.remove("coordinationName");
            map.remove("count");
            map.remove("coordinationBank");

            switch (type) {
                case 0://柜体
                    break;
                case 1:
                    switch (way) {
                        case 0://门板自产
                            break;
                        case 1://门板外协
                            ii.add(map);
                            break;
                        default://门板特供实木
                            ii.add(map);
                            break;
                    }
                    break;
                default://五金
                    ii.add(map);
                    break;
            }
        }
        for (Map map:ii){
            produceOrder.remove(map);
        }

        MapContext cidAndStatus = MapContext.newOne();
        cidAndStatus.put(WebConstant.KEY_ENTITY_COMPANY_ID, WebUtils.getCurrCompanyId());
        cidAndStatus.put("status", EmployeeStatus.NORMAL.getValue());
        List<Map> empName = this.companyEmployeeService.findListCidAndStatus(cidAndStatus);
        MapContext result = MapContext.newOne();
        result.put("empName", empName);
        result.put("orderBaseInfo", orderBaseInfo);
        result.put("orderProduce", produceOrder);
        return ResultFactory.generateRequestResult(result);
    }


    /**
     * 确认完成
     * 添加生产执行
     * @param produceFlow
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addProcessExecute(ProduceFlow produceFlow,String orderId) {
        //判断添加是否重复开始
        List<Map> flows = this.produceFlowService.findByProduceOrderId(produceFlow.getProduceOrderId());
        for (Map map:flows){
            Integer nodeNumber =(Integer) map.get("nodeNumber");
            if (nodeNumber==produceFlow.getNode()){
                return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_NOT_ALLOWED_REPEAT,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
            }
        } //判断添加是否重复结束
        Integer node = produceFlow.getNode();
        if (node==ProduceFlowNode.SPARE_PARTS.getValue()){
            MapContext params = MapContext.newOne();
            params.put("id", produceFlow.getProduceOrderId());
            params.put("state", 1);
            this.produceOrderService.updateByMapContext(params);
        }
        this.produceFlowService.add(produceFlow);
        MapContext result = MapContext.newOne();
        result.put("orderId", orderId);
        result.put("belongId", produceFlow.getId());
        return ResultFactory.generateRequestResult(result);
    }



    /**
     * 生产执行
     * 确认完成包装
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addProcessExecutePack(ProduceFlow produceFlow, String orderId,String uid,String[] barcodesList) {
        //判断添加是否重复开始
        List<Map> flows = this.produceFlowService.findByProduceOrderId(produceFlow.getProduceOrderId());
        for (Map map:flows){
            Integer nodeNumber =(Integer) map.get("nodeNumber");
            if (nodeNumber==produceFlow.getNode()){
                return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_NOT_ALLOWED_REPEAT,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
            }
        } //判断添加是否重复结束
        //包装完成，修改生产单的状态为已完成
        Integer node = produceFlow.getNode();
        if (node==ProduceFlowNode.PACKING.getValue()){
            MapContext params = MapContext.newOne();
            params.put("id", produceFlow.getProduceOrderId());
            params.put("completionTime", DateUtil.getSystemDate());
            params.put("state", 2);
            this.produceOrderService.updateByMapContext(params);
        }
        //判断编号是否存在
        List strList = Arrays.asList(barcodesList);
        Set set = new HashSet(strList);
        List<FinishedStockItem> listByBarcodes = this.finishedStockItemService.findListByBarcodes(set);
        if (listByBarcodes!=null||listByBarcodes.size()>0) {
            for (FinishedStockItem finishedStockItem : listByBarcodes) {
                return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_NOT_ALLOWED_REPEAT, "编号：" + finishedStockItem.getBarcode() + " " + AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
            }
        }
        //为了查询仓库表
        String productCategoryId = ProductCategoryId.finished.getId();
        Storage storage = this.storageService.findOneByProductCategoryId(productCategoryId);
        if (storage!=null){
            //本次保存的包裹数
            int packages = barcodesList.length;
            //拿到生产拆单类型（0柜体1门板2五金）
            ProduceOrder produceOrder = this.produceOrderService.findById(produceFlow.getProduceOrderId());
            Integer type = produceOrder.getType();
            Integer way = produceOrder.getWay();
            Integer stockItemType;
            switch (type){//0-柜体；1-门板；2-五金
                case 0:
                    stockItemType = 0;break;//柜体-自产
                case 1:
                    switch (way){
                        case 0: stockItemType = 1;break;//门板-自产
                        case 1: stockItemType = 2;break;//门板-外协
                        case 2: stockItemType = 3;break;//门板-特供实木
                        default:stockItemType = 1;break;//门板-自产
                    }break;
                case 2:
                    stockItemType = 4;break;//五金
                default:
                    stockItemType = 0;
                    break;

            }
            //为了查询订单的no
            CustomOrder order = this.customOrderService.findById(orderId);
            String finishedStockId;
            FinishedStock stock = this.finishedStockService.findByOrderId(orderId);
            if (stock!=null){
                finishedStockId = stock.getId();
                //为了判断包裹数和包装条目数是否一样，如果一样代表入库已完成，不在允许添加包裹
                MapContext params = MapContext.newOne();
                params.put("finishedStockId", finishedStockId);
                Integer packNum = this.finishedStockItemService.findByFinishedStockId(params);
                Integer packages1 = stock.getPackages();
                if (packages1==packNum){
                    return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
                }

                MapContext packAndId = MapContext.newOne();
                packAndId.put("id", finishedStockId);
                packAndId.put("packages", packNum+packages+1);
                this.finishedStockService.updateByMapContext(packAndId);
            }else {
                String storageId = storage.getId();
                FinishedStock finishedStock = new FinishedStock();
                finishedStock.setCreated(DateUtil.getSystemDate());
                finishedStock.setCreator(uid);
                finishedStock.setOrderId(orderId);
                finishedStock.setOrderNo(order.getNo());
                finishedStock.setPackages(packages+1);
                finishedStock.setWay(FinishedStockWay.MANUAL.getValue());
                finishedStock.setStorageId(storageId);
                finishedStock.setStatus(FinishedStockStatus.UNSHIPPED.getValue());
                this.finishedStockService.add(finishedStock);
                finishedStockId = finishedStock.getId();
            }

            for (String barcode:barcodesList){
                FinishedStockItem finishedStockItem = new FinishedStockItem();
                finishedStockItem.setBarcode(barcode);
                finishedStockItem.setCreated(DateUtil.getSystemDate());
                finishedStockItem.setCreator(uid);
                finishedStockItem.setFinishedStockId(finishedStockId);
                finishedStockItem.setIn(false);
                finishedStockItem.setLocation("");
                finishedStockItem.setType(stockItemType);
                finishedStockItem.setShipped(false);
                finishedStockItemService.add(finishedStockItem);
            }
            this.produceFlowService.add(produceFlow);
            if (order.getStatus()==OrderStatus.IN_PRODUCTION.getValue()) {
                MapContext upOrderStatus = MapContext.newOne();
                upOrderStatus.put("id", orderId);
                upOrderStatus.put("status", OrderStatus.TO_WAREHOUSE.getValue());
                this.customOrderService.updateByMapContext(upOrderStatus);
            }
            MapContext result = MapContext.newOne();
            result.put("orderId", orderId);
            result.put("belongId", produceFlow.getId());
            return ResultFactory.generateRequestResult(result);
        }
        return ResultFactory.generateErrorResult(ErrorCodes.SYS_EXECUTE_FAIL_00001, AppBeanInjector.i18nUtil.getMessage("SYS_EXECUTE_FAIL_00001"));
    }

    /**
     * 查询生产的列表
     * @param deliveryDate
     * @param created
     * @param params
     * @param pagination
     * @return
     */
    public List<Map> findProcessList(String deliveryDate, String created, MapContext params, Pagination pagination) {
        PaginatedFilter paginatedFilter = PaginatedFilter.newOne();
        List<Map<String, String>> sorts = new ArrayList<>();
        Map<String, String> sortMap = new HashMap<>();
        if (LwxfStringUtils.isNotBlank(deliveryDate)) {
            sortMap.put("estimated_delivery_date", deliveryDate);
        } else {
            if (LwxfStringUtils.isNotBlank(created)) {
                sortMap.put("created", created);
            } else {
                sortMap.put("created", "desc");
            }
        }
        sorts.add(sortMap);
        paginatedFilter.setSorts(sorts);
        paginatedFilter.setPagination(pagination);
        paginatedFilter.setFilters(params);
        PaginatedList<Map> fProcessOrderList = this.customOrderService.findFProcessOrderList(paginatedFilter);
        List<Map> rows = fProcessOrderList.getRows();
        Date systemDate = DateUtil.getSystemDate();
        for (Map orderInfo : rows) {
            Date deliverDate = (Date) orderInfo.get("estimated_delivery_date");
            Date documentary_time = (Date) orderInfo.get("documentary_time");
                if (documentary_time != null) {//下车间时间为空，已耗时和状态都为空
                    long nh = 1000 * 60 * 60;//一小时的毫秒数
                    //下车间时间
                    long documentaryTime = documentary_time.getTime();
                    //系统时间
                    long systemDateTime = systemDate.getTime();
                    //系统时间-下车间的时间 = 已耗时的时间
                    long diff = (systemDateTime - documentaryTime);
                    if (diff > 0) {//如果已耗时时间小于0，已耗时为空
                        if (deliverDate != null) {//交货时间为空，状态为空
                            //交货时间
                            long deliverDateTime = deliverDate.getTime();
                            //交货时间-下车间的时间 = 交货周期有多长时间
                            long totalDays = (deliverDateTime - documentaryTime);
                            long ghour = totalDays / nh;//计算一共有多少小时
                            long xhour = diff / nh;//计算消耗了多少小时
                            if (ghour < xhour) {
                                orderInfo.put("overdue", "超期");
                            } else if ((ghour - xhour) >= 0 && (ghour - xhour) <= 24) {
                                orderInfo.put("overdue", "临期");
                            } else if ((ghour - xhour) > 24) {
                                orderInfo.put("overdue", "正常");
                            }

                        } else {
                            orderInfo.put("overdue", "未知");
                        }
                    } else {
                        orderInfo.put("overdue", "超期");
                    }
                } else {
                    orderInfo.put("overdue", "未知");
                }
        }
        return rows;
    }

    /**
     * 查询订单的基础信息
     * @param orderId
     * @return
     */
    @Override
    public Map findOrderBaseInfo(String orderId,Integer type){
        MapContext params = MapContext.newOne();
        params.put("orderId", orderId);
        params.put("type", type);
        Map fAppBaseInfoByOrderId = this.customOrderService.findFAppBaseInfoByOrderId(params);
        return fAppBaseInfoByOrderId;
    }





}
