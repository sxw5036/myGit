package com.lwxf.industry4.webapp.facade.app.factory.financing;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentSimpleDto;
import com.lwxf.industry4.webapp.domain.entity.financing.PaymentSimple;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PaymentSimpleFacade extends BaseFacade {

    RequestResult viewIndex();
    /*
       查询收支列表
     */
    RequestResult findPaymentSimpleList(MapContext mapContext, Integer pageNum, Integer pageSize);

    /*
    保存收支信息
     */
    RequestResult addPaymentSimple(PaymentSimpleDto paymentSimple);
    /*
       导出excel
     */
    RequestResult exportExcel();
    /*
    获得收支详细信息
     */
    RequestResult getPaymentSimpleById(String id);

   RequestResult updatePaymentSimple(String paymentSimpleId, MapContext map);

   RequestResult deleteById(String paymentSimpleId);

   RequestResult getUserForPaymentSimple();

   RequestResult uploadImage(String userId,List<MultipartFile> multipartFile);

}
