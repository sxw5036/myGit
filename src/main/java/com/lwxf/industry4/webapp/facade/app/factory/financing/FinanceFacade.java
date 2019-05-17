package com.lwxf.industry4.webapp.facade.app.factory.financing;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FinanceFacade extends BaseFacade {

    RequestResult viewIndex();

    RequestResult viewVeiryOrderPrice(String paymentId);

    RequestResult viewVeiryDesignOrderPrice(String paymentId);

    RequestResult verifyOrderPrice(MapContext map);

    RequestResult verifyDesignPrice(MapContext map);

    RequestResult uploadImage(String userId, String companyId,List<MultipartFile> multipartFile);

}
