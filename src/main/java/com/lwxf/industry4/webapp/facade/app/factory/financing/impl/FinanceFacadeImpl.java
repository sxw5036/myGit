package com.lwxf.industry4.webapp.facade.app.factory.financing.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.common.UploadFilesService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.customorder.impl.CustomOrderServiceImpl;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentFunds;
import com.lwxf.industry4.webapp.common.enums.order.OrderStatus;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.FinanceCountDto;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.VerifyDesignPriceDto;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.VerifyOrderPriceDto;
import com.lwxf.industry4.webapp.domain.entity.common.UploadFiles;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.financing.FinanceFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component("financeFacadeFapp")
public class FinanceFacadeImpl extends BaseFacadeImpl implements FinanceFacade {

    @Resource(name = "paymentService")
    private PaymentService paymentService;
    @Resource(name = "companyEmployeeService")
    private CompanyEmployeeService companyEmployeeService;
    @Resource(name = "uploadFilesService")
    private UploadFilesService uploadFilesService;
    @Resource(name = "customOrderService")
    private CustomOrderServiceImpl customOrderServiceImpl;

    @Override
    public RequestResult viewIndex() {
        FinanceCountDto dto = paymentService.selectFinanceCountForApp();
        dto.setVerifyList(paymentService.selectVerifyPaymentList());
        return ResultFactory.generateRequestResult(dto);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult verifyOrderPrice(MapContext mapContext) {
        //更新图片信息为正常
        if(mapContext.get("fileIds")!=null && mapContext.get("fileIds").equals("")){
            String[] ids = mapContext.get("fileIds").toString().split(",");
            for(String file_id: ids){
                MapContext map = MapContext.newOne();
                map.put("id",file_id);
                map.put("resourceId",mapContext.get("paymentId"));
                map.put("status",1);
                this.uploadFilesService.updateByMapContext(map);
            }
        }
        mapContext.put("funds", PaymentFunds.ORDER_FEE_CHARGE.getValue());
        int i = paymentService.verifyOrderPrice(mapContext);
        Payment payment = paymentService.findById(mapContext.get("paymentId").toString()) ;
        if(i>0){
            MapContext resmap = MapContext.newOne();
            resmap.put("result","success");
            //更新订单状态为待生产
            MapContext map = MapContext.newOne();
            map.put("id",payment.getCustomOrderId());
            map.put("status",OrderStatus.TO_PRODUCTION.getValue());
            customOrderServiceImpl.updateByMapContext(map);
            return ResultFactory.generateRequestResult(resmap);
        }else{
            return ResultFactory.generateErrorResult(ErrorCodes.SYS_EXECUTE_FAIL_00001,"数据保存失败");//bug 要转换成i18n
        }
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult verifyDesignPrice(MapContext mapContext) {
        //更新图片信息为正常
        if(mapContext.get("fileIds")!=null && mapContext.get("fileIds").equals("")){
            String[] ids = mapContext.get("fileIds").toString().split(",");
            for(String file_id: ids){
                MapContext map = MapContext.newOne();
                map.put("id",file_id);
                map.put("resourceId",mapContext.get("paymentId"));
                map.put("status",1);
                this.uploadFilesService.updateByMapContext(map);
            }
        }
        mapContext.put("funds", PaymentFunds.DESIGN_FEE_CHARGE.getValue());
        int i = paymentService.verifyDesignPrice(mapContext);
        Payment payment = paymentService.findById(mapContext.get("paymentId").toString()) ;
        //更新订单状态为待设计
        MapContext map = MapContext.newOne();
        map.put("id",payment.getCustomOrderId());
        map.put("status", OrderStatus.TO_DESIGN.getValue());
        customOrderServiceImpl.updateByMapContext(map);
        if(i>0){
            MapContext resmap = MapContext.newOne();
            resmap.put("result","success");
            return ResultFactory.generateRequestResult(resmap);
        }else{
            return ResultFactory.generateErrorResult(ErrorCodes.SYS_EXECUTE_FAIL_00001,"数据保存失败");//bug 要转换成i18n
        }
    }

    @Override
    public RequestResult viewVeiryOrderPrice(String paymentId) {
        VerifyOrderPriceDto dto = paymentService.selectVerifyOrderPriceInfo(paymentId);
        dto.setOrderTypeName("正常/散单");// bug 暂时处理 后期补充数据
        dto.setProductionType("自产");// bug 暂时处理 后期补充数据
        dto.setFinancer(companyEmployeeService.getUserListByRoleKey("012"));//bug 暂时处理 审核人后期修改为字典数据
        return ResultFactory.generateRequestResult(dto);
    }

    @Override
    public RequestResult viewVeiryDesignOrderPrice(String paymentId) {
        VerifyDesignPriceDto dto = paymentService.selectVerifyDesignPriceInfo(paymentId);
        dto.setOrderTypeName("正常/散单");// bug 暂时处理 后期补充数据
        dto.setProductionType("设计");// bug 暂时处理 后期补充数据
        dto.setFinancer(companyEmployeeService.getUserListByRoleKey("012"));//bug 暂时处理 审核人后期修改为字典数据
        return ResultFactory.generateRequestResult(dto);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult uploadImage(String userId, String companyId, List<MultipartFile> multipartFileList) {
        //添加图片到本地
        List<String> listUrls = new ArrayList<>();
        if(multipartFileList!=null && multipartFileList.size()>0){
            for(MultipartFile multipartFile:multipartFileList){
                UploadInfo uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, multipartFile, UploadResourceType.PAYMENT_SIMPLE, "cover");
                //添加图片到数据库
                UploadFiles uploadFiles = new UploadFiles();
                uploadFiles.setCreated(DateUtil.getSystemDate());
                uploadFiles.setFullPath(WebUtils.getDomainUrl() + uploadInfo.getRelativePath());
                uploadFiles.setMime(uploadInfo.getFileMimeType().getRealType());
                uploadFiles.setName(uploadInfo.getFileName());
                uploadFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
                uploadFiles.setPath(uploadInfo.getRelativePath());
                uploadFiles.setStatus(0);
                uploadFiles.setCompanyId(companyId);
                uploadFiles.setResourceType(10);
                uploadFiles.setCreator(userId);
                this.uploadFilesService.add(uploadFiles);
                listUrls.add(uploadFiles.getId());
            }
        }
        MapContext map = MapContext.newOne();
        map.put("ids",listUrls);
        return ResultFactory.generateRequestResult(map);
    }
}
