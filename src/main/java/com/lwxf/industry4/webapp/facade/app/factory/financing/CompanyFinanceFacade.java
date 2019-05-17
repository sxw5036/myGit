package com.lwxf.industry4.webapp.facade.app.factory.financing;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyPaymentInfoDto;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CompanyFinanceFacade extends BaseFacade {

    RequestResult viewIndex();

    RequestResult findCompanyFinanceList(MapContext mapContext, Integer pageNum, Integer pageSize);

    RequestResult getCompanyFinanceById(String paymentId);

    RequestResult addCompanyPayment(CompanyPaymentInfoDto companyPaymentInfoDto);


    RequestResult uploadImage(String userId, String companyId, List<MultipartFile> multipartFile);
}
