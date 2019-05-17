package com.lwxf.industry4.webapp.facade.app.dealer.mymessage.impl;

import com.lwxf.industry4.webapp.bizservice.user.UserNotifyService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.dto.user.UserNotifyDto;
import com.lwxf.industry4.webapp.domain.entity.user.UserNotify;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.mymessage.MyMessageFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/10 10:11
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "myMessageFacade")
public class MyMessageFacadeImpl extends BaseFacadeImpl implements MyMessageFacade {


    @Resource(name = "userNotifyService")
    private UserNotifyService userNotifyService;


    /**
     * 查询个人信息列表
     * @param pageSize
     * @param pageNum
     * @param
     * @return
     */
    @Override
    public RequestResult selectByFilter(Integer pageSize, Integer pageNum,MapContext params) {
        PaginatedFilter filter = PaginatedFilter.newOne();
        Pagination pagination = Pagination.newOne();
        pagination.setPageNum(pageNum);
        pagination.setPageSize(pageSize);
        filter.setPagination(pagination);
        filter.setFilters(params);
        PaginatedList<UserNotifyDto> userNotifyDtoList = this.userNotifyService.selectAllByFilter(filter);
        return ResultFactory.generateRequestResult(userNotifyDtoList);
    }

    /**
     * 查询个人消息详情
     * @param notifyId
     * @return
     */
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult selectByNotifyId(String notifyId) {
        UserNotify userNotify = this.userNotifyService.findById(notifyId);
        MapContext params = MapContext.newOne();
        params.put(WebConstant.KEY_ENTITY_ID,notifyId);
        params.put("readed",true);
        this.userNotifyService.updateByMapContext(params);
        return ResultFactory.generateRequestResult(userNotify);
    }
}

