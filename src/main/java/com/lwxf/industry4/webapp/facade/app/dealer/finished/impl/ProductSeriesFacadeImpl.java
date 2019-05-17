package com.lwxf.industry4.webapp.facade.app.dealer.finished.impl;

import com.lwxf.industry4.webapp.bizservice.finished.FinishedProductSeriesService;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.entity.finished.FinishedProductSeries;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.finished.ProductSeriesFacade;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/18 20:21
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("productSeriesFacade")
public class ProductSeriesFacadeImpl extends BaseFacadeImpl implements ProductSeriesFacade {

    @Resource(name = "finishedProductSeriesService")
    private FinishedProductSeriesService finishedProductSeriesService;

    @Override
    public RequestResult findAll() {

        List<FinishedProductSeries> all = this.finishedProductSeriesService.findAll();
//        if (null==all||all.size()>0){
//            return ResultFactory.generateResNotFoundResult();
//        }
        return ResultFactory.generateRequestResult(all);
    }
}

