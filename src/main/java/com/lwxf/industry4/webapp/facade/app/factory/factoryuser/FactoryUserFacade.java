package com.lwxf.industry4.webapp.facade.app.factory.factoryuser;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.multipart.MultipartFile;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/3/27 10:35
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface FactoryUserFacade extends BaseFacade {
    RequestResult factoryUserPersonalInfo(String companyId, String userId);

    RequestResult updateFactoryUserPersonalInfo(String companyId, String userId,MapContext params);

    RequestResult findAccountInfo(String companyId, String userId);

    RequestResult updateAvatar(String userId, MultipartFile multipartFile);

    RequestResult updatePassword(String userId, MapContext mapContext);
}
