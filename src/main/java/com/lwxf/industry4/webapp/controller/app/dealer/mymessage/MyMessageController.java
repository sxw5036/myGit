package com.lwxf.industry4.webapp.controller.app.dealer.mymessage;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.mymessage.MyMessageFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 功能：首页：消息功能
 * 我的首页：消息功能
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/10 10:09
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class MyMessageController extends BaseDealerController {

    @Resource(name = "myMessageFacade")
    private MyMessageFacade myMessageFacade;


    /**
     * 查询个人消息列表
     * @param pageSize
     * @param pageNum
     * @param userId
     * @return
     */
    @GetMapping(value = "/users/{userId}/mymessages")
    public RequestResult selectByFilter(@RequestParam(required = false) Integer pageSize,
                                        @RequestParam(required = false)Integer pageNum,
                                        @PathVariable String userId,
                                        HttpServletRequest request){
        if(null == pageSize){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if(null == pageNum){
            pageNum = 1;
        }

        String xp = "bmessagemng-messagelist-view";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        MapContext params = MapContext.newOne();
        params.put(WebConstant.KEY_ENTITY_USER_ID,userId);
        return myMessageFacade.selectByFilter(pageSize,pageNum,params);
    }

    /**
     * 查询个人消息详情
     * @param notifyId
     * @return
     */
    @GetMapping(value = "/users/{userId}/mymessages/{notifyId}")
    public RequestResult selectByNotifyId(@PathVariable String notifyId,
                                          HttpServletRequest request){
        String xp = "bmessagemng-messagelist-view";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        return myMessageFacade.selectByNotifyId(notifyId);
    }


    /**
     * 首页：消息提醒列表(未读的列表)
     * @param pageSize
     * @param pageNum
     * @param userId
     * @return
     */
    @GetMapping(value = "/users/{userId}/mymessages/unread")
    public RequestResult findByUserIdAndReaded(@RequestParam(required = false) Integer pageSize,
                                               @RequestParam(required = false)Integer pageNum,
                                               @PathVariable String userId,
                                               HttpServletRequest request){
        if(null == pageSize){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if(null == pageNum){
            pageNum = 1;
        }

        String xp = "bmessagemng-messagelist-view";
        RequestResult result1 = this.validUserPermission(request,xp);
        if (null!=result1){
            return result1;
        }
        MapContext params = MapContext.newOne();
        params.put(WebConstant.KEY_ENTITY_USER_ID,userId);
        params.put("readed",false);
        return myMessageFacade.selectByFilter(pageSize, pageNum, params);
    }



}

