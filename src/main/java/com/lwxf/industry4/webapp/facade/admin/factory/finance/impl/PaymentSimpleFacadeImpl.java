package com.lwxf.industry4.webapp.facade.admin.factory.finance.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentSimpleFilesService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentSimpleService;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.company.DealerAccountType;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentSimpleFunds;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.UniqueKeyUtil;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentSimpleDto;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentSimpleListDtoForApp;
import com.lwxf.industry4.webapp.domain.entity.financing.PaymentSimple;
import com.lwxf.industry4.webapp.domain.entity.financing.PaymentSimpleFiles;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.admin.factory.finance.PaymentSimpleFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("paymentSimpleFacade")
public class PaymentSimpleFacadeImpl extends BaseFacadeImpl implements PaymentSimpleFacade {
    @Resource(name = "paymentSimpleService")
    private PaymentSimpleService paymentSimpleService;

    @Resource(name = "paymentSimpleFilesService")
    private PaymentSimpleFilesService paymentSimpleFilesService;

    @Resource(name = "companyService")
    private CompanyService companyService;

    @Override
    public RequestResult findPaymentSimpleList(MapContext mapContext, Integer pageNum, Integer pageSize) {
        PaginatedFilter paginatedFilter = new PaginatedFilter();
        Pagination pagination = new Pagination();
        pagination.setPageNum(pageNum);
        pagination.setPageSize(pageSize);
        paginatedFilter.setPagination(pagination);
        mapContext.put(WebConstant.KEY_ENTITY_STATUS, 0);
        paginatedFilter.setFilters(mapContext);
        return ResultFactory.generateRequestResult(this.paymentSimpleService.selectDtoByFilter(paginatedFilter));
    }

    @Override
    public RequestResult exportExcel() {
        return null;
    }

    @Override
    public RequestResult viewIndex() {
        //返回结果Map
        MapContext resMap= MapContext.newOne();
        //获取统计
        Map<String,String> reportMap = paymentSimpleService.countPaymentSimpleForApp();
        resMap.put("report",reportMap);
        //今日流水
        List<PaymentSimpleListDtoForApp> paymentSimplelist = paymentSimpleService.selectCurrentDayListByFilterForApp();
        resMap.put("dataList",paymentSimplelist);
        //类型科目
        List<MapContext> list = new ArrayList<>();
        for (PaymentSimpleFunds s : PaymentSimpleFunds.values()) {
            if(s.getValue().intValue()<100) {
                MapContext mapCompanyStatus = MapContext.newOne();
                mapCompanyStatus.put("name", s.getName());
                mapCompanyStatus.put("pid",s.getValue().toString().substring(0,1));
                mapCompanyStatus.put("id", s.getValue());
                list.add(mapCompanyStatus);
            }
        }
        resMap.put("funds1",list);
        return ResultFactory.generateRequestResult(resMap);
    }

    @Override
    public RequestResult getPaymentSimpleById(String paymentId) {
        PaymentSimpleDto paymentSimple=this.paymentSimpleService.findDtoById(paymentId);
        //将图片放入实体
        List<String> pathList;
        List<PaymentSimpleFiles> fileList=paymentSimpleFilesService.findByPaymentSimpleId(paymentId);
        if(fileList!=null && fileList.size()>0){
            pathList = new ArrayList<>();
            for(PaymentSimpleFiles file: fileList){
                pathList.add(file.getFullPath());
            }
            paymentSimple.setImgPathList(pathList);
        }
        return ResultFactory.generateRequestResult(paymentSimple);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult updatePaymentSimple(String paymentSimpleId, MapContext map) {
        map.put("id",paymentSimpleId);
        return ResultFactory.generateRequestResult(this.paymentSimpleService.updateByMapContext(map));
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult deleteById(String paymentSimpleId) {
        PaymentSimple payment = paymentSimpleService.findById(paymentSimpleId);
        BigDecimal amount = payment.getAmount();
        this.paymentSimpleService.deleteById(paymentSimpleId);
        //從日常總賬減去
        companyService.balanceMinusForFactory(Double.parseDouble(amount.toString()),DealerAccountType.PAYMENTSIMPLE_ACCOUNT.getValue());
        return ResultFactory.generateSuccessResult();
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addPaymentSimple(PaymentSimpleDto paymentSimple) {
        String id = UniqueKeyUtil.getStringId();
        paymentSimple.setCreated(DateUtil.getSystemDate());
        paymentSimple.setId(id);
        if(this.paymentSimpleService.add(paymentSimple)==1){
            if(paymentSimple.getType()==1) {
                //将收入加入总账
                companyService.balancePlusForFactory(Double.parseDouble(paymentSimple.getAmount().toString()), DealerAccountType.PAYMENTSIMPLE_ACCOUNT.getValue());
            }else if (paymentSimple.getType()==2){
                //将收入支出记录总账
                companyService.balanceMinusForFactory(Double.parseDouble(paymentSimple.getAmount().toString()),DealerAccountType.PAYMENTSIMPLE_ACCOUNT.getValue());
            }
            //更新图片信息为正常
            if(paymentSimple.getFileIds()!=null && !paymentSimple.getFileIds().equals("")){
                String[] ids = paymentSimple.getFileIds().split(",");
                for(String file_id: ids){
                    MapContext map = MapContext.newOne();
                    map.put("id",file_id);
                    map.put("paymentSimple",paymentSimple.getId());
                    map.put("status",1);
                    this.paymentSimpleFilesService.updateByMapContext(map);
                }
            }
            //删除多余图片垃圾信息(待实现)
            return ResultFactory.generateRequestResult(getPaymentSimpleById(paymentSimple.getId()).getData());
        }else{
            return ResultFactory.generateErrorResult(ErrorCodes.SYS_EXECUTE_FAIL_00001,"数据保存失败");
        }
    }

    @Override
    public RequestResult getUserForPaymentSimple() {
        return ResultFactory.generateRequestResult(this.paymentSimpleService.getUserForPaymentSimple("4k8cbm6hbswh"));
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult uploadImage(String userId,List<MultipartFile> multipartFileList) {
        //添加图片到本地
        List<MapContext> listUrls = new ArrayList<>();
        if(multipartFileList!=null && multipartFileList.size()>0){
            for(MultipartFile multipartFile:multipartFileList){
                UploadInfo uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, multipartFile, UploadResourceType.PAYMENT_SIMPLE, "cover");
                //添加图片到数据库
                PaymentSimpleFiles paymentSimpleFiles = new PaymentSimpleFiles();
                paymentSimpleFiles.setCreated(DateUtil.getSystemDate());
                paymentSimpleFiles.setFullPath(WebUtils.getDomainUrl() + uploadInfo.getRelativePath());
                paymentSimpleFiles.setMime(uploadInfo.getFileMimeType().getRealType());
                paymentSimpleFiles.setName(uploadInfo.getFileName());
                paymentSimpleFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
                paymentSimpleFiles.setPath(uploadInfo.getRelativePath());
                paymentSimpleFiles.setStatus(0);
                paymentSimpleFiles.setCreator(userId);
              //  paymentSimpleFiles.setCreator("4v14mj3ampdy");
                this.paymentSimpleFilesService.add(paymentSimpleFiles);
                MapContext map = MapContext.newOne();
                map.put("imgFullPath",paymentSimpleFiles.getFullPath());
                map.put("imgRelPath",uploadInfo.getRelativePath());
                map.put("fileId",paymentSimpleFiles.getId());
                listUrls.add(map);
            }
        }
        return ResultFactory.generateRequestResult(listUrls);
    }

}
