package com.lwxf.industry4.webapp.facade.app.dealer.order.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.company.CompanyEmployeeService;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.bizservice.customorder.OrderAccountLogService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentFilesService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentDto;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.financing.PaymentFiles;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.order.PaymentFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/19 11:14
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("paymentFacade")
public class PaymentFacadeImpl extends BaseFacadeImpl implements PaymentFacade {

    @Resource(name = "paymentService")
    private PaymentService paymentService;
    @Resource(name = "paymentFilesService")
    private PaymentFilesService paymentFilesService;
    @Resource(name = "companyEmployeeService")
    private CompanyEmployeeService companyEmployeeService;
    @Resource(name = "customOrderService")
    private CustomOrderService customOrderService;
    @Resource(name = "orderAccountLogService")
    private OrderAccountLogService orderAccountLogService;


    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addPayment(Payment payment,String userId,String companyId) {

        payment.setCreated(DateUtil.getSystemDate());
        payment.setStatus(0);
        payment.setCreator(userId);
        payment.setCompanyId(companyId);
        payment.setNotes("您有新的支付信息，请尽快查收！");
        //查询店主开始
        List<String> companies = new ArrayList<>();
        companies.add(companyId);
        List<CompanyEmployee> companyEmployees = this.companyEmployeeService.selectShopkeeperByCompanyIds(companies);
        String payee = companyEmployees.get(0).getUserId();
        //结束
        payment.setPayee(payee);
        payment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
        RequestResult result = payment.validateFields();
        if (null!=result){
            return  result;
        }
        this.paymentService.add(payment);
        MapContext map = MapContext.newOne();
        map.put("id",payment.getId());
        return ResultFactory.generateRequestResult(map);
    }


    @Override
    public RequestResult findByOrderId(String orderId) {
        List<PaymentDto> paymentDtoList = this.paymentService.findByOrderId(orderId);
//        BigDecimal paid = new BigDecimal(0);//已支付的价格
//        if (paymentDtoList!=null&&paymentDtoList.size()>0){
//            for (PaymentDto paymentDto :paymentDtoList){
//                BigDecimal amount = paymentDto.getAmount();
//                paid = paid.add(amount);
//            }
//        }
//
//        CustomOrder order = this.customOrderService.findById(orderId);
//
//        List<OrderAccountLogDto> accountLogDtos = this.orderAccountLogService.findByOrderId(orderId);
//        BigDecimal allPreferentialPrice = new BigDecimal(0);//总优惠
//        if (accountLogDtos!=null&&accountLogDtos.size()>0){
//            for (OrderAccountLogDto logDto:accountLogDtos){
//                BigDecimal preferentialPrice = logDto.getPreferentialPrice();
//                allPreferentialPrice = allPreferentialPrice.add(preferentialPrice);
//            }
//        }
//        BigDecimal amount = order.getAmount();//总金额
//        BigDecimal realPrice = new BigDecimal(0);//实际价格
//        BigDecimal unpaid = new BigDecimal(0);//待支付的价格
//        if (amount.compareTo(BigDecimal.ZERO)>0){
//             realPrice = amount.subtract(allPreferentialPrice);//实际价格
//             unpaid = realPrice.subtract(paid);//待支付的价格
//        }
//        MapContext map = MapContext.newOne();
//        map.put("amount", amount);
//        map.put("allPreferentialPrice", allPreferentialPrice);//总优惠
//        map.put("realPrice", realPrice);//实际价格
//        map.put("paid", paid);//已支付的价格
//        map.put("unpaid", unpaid);//待支付的价格
//        MapContext params = MapContext.newOne();
//        params.put("paidList", map);
//        params.put("paymentDtoList", paymentDtoList);
        return ResultFactory.generateRequestResult(paymentDtoList);
    }


    @Override
    public RequestResult findByPaymentId(String orderId,String paymentId) {

        PaymentDto paymentDto = this.paymentService.findByPaymentId(paymentId);
        if (null==paymentDto){
            return ResultFactory.generateRequestResult(paymentDto);
        }

        List<PaymentFiles> paymentFilesList = this.paymentFilesService.findByPaymentId(paymentId);
        List<String> filesPath = new ArrayList<>();
        for (PaymentFiles paymentFile : paymentFilesList){
            String path = paymentFile.getPath();
            filesPath.add(path);
        }

        MapContext map = MapContext.newOne();
        map.put("paymentDto",paymentDto);
        map.put("filesPath",filesPath);
        return ResultFactory.generateRequestResult(map);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addPaymentFiles(List<MultipartFile> multipartFiles,String paymentId,String userId) {
        List<Map<String,Object>> parmas = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles){
            UploadInfo uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL,multipartFile,UploadResourceType.PAYMENT,paymentId);
            //创建

            PaymentFiles paymentFiles = PaymentFiles.creator(uploadInfo,UploadType.FORMAL,paymentId);
            paymentFiles.setCreator(userId);
            this.paymentFilesService.add(paymentFiles);
            MapContext map = MapContext.newOne();
            map.put("filePath",paymentFiles.getPath());
            parmas.add(map);

        }
        return ResultFactory.generateRequestResult(parmas);
    }



    @Override
    @Transactional(value = "transactionManager")
    public RequestResult BAuditPayment(String uid,String paymentId) {
        MapContext pramas = MapContext.newOne();
        pramas.put("auditor",uid);//审核人
        pramas.put("audited", DateUtil.getSystemDate());//审核时间
        pramas.put("status",1);//审核状态。1
        pramas.put("id",paymentId);
        paymentService.updateByMapContext(pramas);
        MapContext data = MapContext.newOne();
        return ResultFactory.generateRequestResult(data);
    }
}

