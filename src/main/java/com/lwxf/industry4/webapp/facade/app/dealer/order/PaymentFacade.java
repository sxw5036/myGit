package com.lwxf.industry4.webapp.facade.app.dealer.order;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/19 11:13
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface PaymentFacade extends BaseFacade {

    RequestResult addPayment(Payment payment,String userId,String companyId);

    RequestResult findByOrderId(String orderId);

    RequestResult findByPaymentId(String orderId,String paymentId);

    RequestResult addPaymentFiles(List<MultipartFile> multipartFiles, String paymentId, String userId);

    RequestResult BAuditPayment(String uid,String paymentId);



}
