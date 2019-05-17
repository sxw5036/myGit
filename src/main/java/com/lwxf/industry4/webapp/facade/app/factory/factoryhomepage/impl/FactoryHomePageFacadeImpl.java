package com.lwxf.industry4.webapp.facade.app.factory.factoryhomepage.impl;

import com.lwxf.industry4.webapp.bizservice.advertising.AdvertisingService;
import com.lwxf.industry4.webapp.bizservice.contentmng.ContentsService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.advertising.AdvertisingPosition;
import com.lwxf.industry4.webapp.common.enums.content.ContentStatus;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.entity.advertising.Advertising;
import com.lwxf.industry4.webapp.facade.app.factory.factoryhomepage.FactoryHomePageFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/4/1 9:38
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("factoryHomePageFacade")
public class FactoryHomePageFacadeImpl extends BaseFacadeImpl implements FactoryHomePageFacade {

    @Resource(name = "advertisingService")
    private AdvertisingService advertisingService;
    @Resource(name = "contentsService")
    private ContentsService contentsService;
    @Override
    public RequestResult findAdvertisingAndNotice() {
        MapContext map = MapContext.newOne();
        map.put("code", "notice");
        map.put(WebConstant.KEY_ENTITY_STATUS, ContentStatus.ISSUE.getValue());
        List<Map> contentsList = this.contentsService.findByCodeAndStatus(map);
        List<Advertising> advertisingList = this.advertisingService.findByPosition(AdvertisingPosition.HOME_CAROUSEL.getValue());
        if (advertisingList!=null&&advertisingList.size()>0){
            for (Advertising advertising:advertisingList){
                String imgPath = advertising.getImgPath();
                imgPath = WebUtils.getDomainUrl()+imgPath;
                advertising.setImgPath(imgPath);
            }
        }
        MapContext result = MapContext.newOne();
        result.put("advertisingList", advertisingList);
        result.put("contentsList", contentsList);
        return ResultFactory.generateRequestResult(result);
    }





}
