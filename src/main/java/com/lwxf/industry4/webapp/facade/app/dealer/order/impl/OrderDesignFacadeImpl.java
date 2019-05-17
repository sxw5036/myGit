package com.lwxf.industry4.webapp.facade.app.dealer.order.impl;

import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderDesignService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderFilesService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.common.enums.order.OrderDesignStatus;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDesignDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrder;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderDesign;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderFiles;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.order.OrderDesignFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.events.Event;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/15 16:24
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("orderDesignFacade")
public class OrderDesignFacadeImpl extends BaseFacadeImpl implements OrderDesignFacade {

    @Resource(name = "customOrderDesignService")
    private CustomOrderDesignService customOrderDesignService;
    @Resource(name = "customOrderService")
    private CustomOrderService customOrderService;
    @Resource(name = "customOrderFilesService")
    private CustomOrderFilesService customOrderFilesService;


    /**
     * 查询订单下的设计列表
     *
     * @param orderId
     * @return
     */
    @Override
    public RequestResult findByOrderId(String orderId) {
        List<CustomOrderDesignDto> customOrderDesignDtos = this.customOrderDesignService.selectByOrderId(orderId);
        return ResultFactory.generateRequestResult(customOrderDesignDtos);
    }

    /**
     * 查看设计详情和图片
     *
     * @param orderId
     * @param designId
     * @return
     */
    @Override
    public RequestResult findBydesignId(String orderId, String designId) {
        MapContext result = MapContext.newOne();
        Boolean exist = this.customOrderService.isExist(orderId);
        if (!exist) {
            return ResultFactory.generateResNotFoundResult();
        }
        CustomOrderDesign design = this.customOrderDesignService.findById(designId);
        if (null == design) {
            return ResultFactory.generateResNotFoundResult();
        }
        Integer status = design.getStatus();
        result.put("status", status);
        List<CustomOrderFiles> filesList = this.customOrderFilesService.selectByOrderIdAndType(orderId, 1, designId);
        if (null == filesList || filesList.size() == 0) {
            result.put("filesPath", filesList);
            return ResultFactory.generateRequestResult(result);
        }

        List<Object> filesPath = new ArrayList<>();
        for (CustomOrderFiles customOrderFiles : filesList) {
            Map<String, Object> map = new HashedMap();
            String path = customOrderFiles.getPath();
            Integer category = customOrderFiles.getCategory();
            map.put("path", path);
            map.put("category", category);
            filesPath.add(map);
        }

        result.put("filesPath", filesPath);
        return ResultFactory.generateRequestResult(result);
    }

    /**
     * 添加修改意见
     *
     * @param orderId
     * @param params
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addAmendmentsById(String orderId, MapContext params) {
        String id = params.getTypedValue("id", String.class);
        CustomOrderDesign design = this.customOrderDesignService.findById(id);
        if (null == design) {
            return ResultFactory.generateResNotFoundResult();
        }
        Integer status = design.getStatus();
        if (status == OrderDesignStatus.UNCONFIRMED.getValue()) {
            if (LwxfStringUtils.isBlank((String) params.get("amendments"))) {
                params.put("amendments", null);
            }
            params.put("status", 0);
            params.put("endTime", null);
            RequestResult result = CustomOrderDesign.validateFields(params);
            if (null != result) {
                return result;
            }
            this.customOrderDesignService.updateByMapContext(params);
            params.remove("amendments");
            params.remove("endTime");
            params.put("id", orderId);
            params.put("status", OrderStatus.DESIGNING.getValue());
            RequestResult result1 = CustomOrder.validateFields(params);
            if (null != result1) {
                return result1;
            }
            this.customOrderService.updateByMapContext(params);
            return ResultFactory.generateRequestResult(params);
        }

        return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
    }

    /**
     * 设计确认
     *
     * @param orderId
     * @param params
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult putStatusById(String orderId, MapContext params) {
        CustomOrder order = this.customOrderService.findById(orderId);
        if (order==null){
            return ResultFactory.generateResNotFoundResult();
        }
        String id = params.getTypedValue("id", String.class);
        CustomOrderDesign design = this.customOrderDesignService.findById(id);
        if (null == design||!design.getCustomOrderId().equals(orderId)) {
            return ResultFactory.generateResNotFoundResult();
        }
        Integer status = design.getStatus();
        if (status == OrderDesignStatus.UNCONFIRMED.getValue()) {
            this.customOrderDesignService.updateByMapContext(params);
            MapContext map = MapContext.newOne();
            map.put("id", orderId);
            map.put("status", OrderStatus.FACTORY_CONFIRMED_FPROCE.getValue());
            this.customOrderService.updateByMapContext(map);
            return ResultFactory.generateRequestResult(params);
        }

        return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
    }

}

