package com.lwxf.industry4.webapp.facade.app.dealer.dealeruser;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.multipart.MultipartFile;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/11/30 15:01
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface DealerUserFacade extends BaseFacade {

    RequestResult isDisabled(String userId);

    RequestResult authCode(MapContext params);

    RequestResult forgetPassword(MapContext params);

    RequestResult selectPersonByUserId(String userId);

    RequestResult updatePersonByMap(MapContext userMap);

    RequestResult updateAvatar(String userId, MultipartFile multipartFile);

    RequestResult findEmpIdAndEmpNameByCid(MapContext params);

    RequestResult findCustomerByCompanyIdAndCustomer(String companyId,String name,String creator);


}
