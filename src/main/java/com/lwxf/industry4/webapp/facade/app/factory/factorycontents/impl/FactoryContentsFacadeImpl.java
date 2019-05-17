package com.lwxf.industry4.webapp.facade.app.factory.factorycontents.impl;

import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.contentmng.ContentsService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.content.ContentStatus;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.contentmng.ContentsDto;
import com.lwxf.industry4.webapp.facade.app.factory.factorycontents.FactoryContentsFacade;
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
 * @create：2019/4/2 10:35
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "factoryContentsFacade")
public class FactoryContentsFacadeImpl extends BaseFacadeImpl implements FactoryContentsFacade {

    @Resource(name = "contentsService")
    private ContentsService contentsService;
    @Override
    public RequestResult findListByCodeAndStatus(Pagination pagination) {
        PaginatedFilter filter = PaginatedFilter.newOne();
        MapContext params = MapContext.newOne();
        params.put("code", "notice");
        params.put(WebConstant.KEY_ENTITY_STATUS, ContentStatus.ISSUE.getValue());
        filter.setFilters(params);
        filter.setPagination(pagination);
        PaginatedList<Map> listByCodeAndStatus = this.contentsService.findListByCodeAndStatus(filter);
        List<Map> rows = listByCodeAndStatus.getRows();
        if (rows!=null&&rows.size()>0){
            for (Map map:rows){
                String cover = (String) map.get("cover");
                String pubAvatar = (String) map.get("pubAvatar");
                if (LwxfStringUtils.isNotBlank(cover)){
                    cover = WebUtils.getDomainUrl()+cover;
                    map.put("cover", cover);
                }
                if (LwxfStringUtils.isNotBlank(pubAvatar)){
                    pubAvatar = WebUtils.getDomainUrl()+pubAvatar;
                    map.put("pubAvatar", pubAvatar);
                }
            }
        }
        return ResultFactory.generateRequestResult(rows);
    }

    @Override
    public RequestResult findNoticeInfoById(String noticeId) {
       Map contentsDto= this.contentsService.findFByContentId(noticeId);
        return ResultFactory.generateRequestResult(contentsDto);
    }
}
