package com.lwxf.industry4.webapp.controller.app.dealer.finished;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.app.dealer.finished.ProductSeriesFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/18 20:19
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class ProductSeriesController {

    @Resource(name = "productSeriesFacade")
    private ProductSeriesFacade productSeriesFacade;

    /**
     * 查询所有的产品系列
     * @return
     */

    @GetMapping("/productseries")
    public RequestResult findAll(){
        return this.productSeriesFacade.findAll();
    }

}

