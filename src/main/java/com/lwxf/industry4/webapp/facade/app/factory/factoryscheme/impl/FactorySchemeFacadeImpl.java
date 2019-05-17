package com.lwxf.industry4.webapp.facade.app.factory.factoryscheme.impl;

import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.design.DesignSchemeFilesService;
import com.lwxf.industry4.webapp.bizservice.design.DesignSchemeService;
import com.lwxf.industry4.webapp.bizservice.design.SchemeContentService;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.design.SchemeContentDto;
import com.lwxf.industry4.webapp.domain.entity.design.DesignScheme;
import com.lwxf.industry4.webapp.facade.app.factory.factoryscheme.FactorySchemeFacade;
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
 * @create：2019/4/2 13:15
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component(value = "factorySchemeFacade")
public class FactorySchemeFacadeImpl extends BaseFacadeImpl implements FactorySchemeFacade {

    @Resource(name = "designSchemeService")
    private DesignSchemeService designSchemeService;
    @Resource(name = "schemeContentService")
    private SchemeContentService schemeContentService;
    @Resource(name = "designSchemeFilesService")
    private DesignSchemeFilesService designSchemeFilesService;
    @Override
    public RequestResult findList(MapContext params, Pagination pagination) {
        PaginatedFilter filter = PaginatedFilter.newOne();
        filter.setFilters(params);
        filter.setPagination(pagination);
        PaginatedList<Map> mapList = this.designSchemeService.findList(filter);
        List<Map> rows = mapList.getRows();
        if (rows!=null&&rows.size()>0){
            for (Map map:rows){
                String cover = (String) map.get("cover");
                String designerAvatar = (String) map.get("designerAvatar");
                if (LwxfStringUtils.isNotBlank(cover)){
                    cover = WebUtils.getDomainUrl()+cover;
                    map.put("cover", cover);
                }
                if (LwxfStringUtils.isNotBlank(designerAvatar)){
                    designerAvatar = WebUtils.getDomainUrl()+designerAvatar;
                    map.put("designerAvatar", designerAvatar);
                }
            }
        }
        return ResultFactory.generateRequestResult(rows);
    }

    @Override
    public RequestResult findBySchemeId(String schemeId) {
        MapContext result = MapContext.newOne();
        DesignScheme scheme = this.designSchemeService.findById(schemeId);
        if (scheme == null) {
            return ResultFactory.generateRequestResult(result);
        }
        List<SchemeContentDto> contentList = this.schemeContentService.findBySchemeId(schemeId);
        //如果不为null，查询出内容中的图片，并赋值；如果为空则不显示
        if (null != contentList && contentList.size() > 0) {
            for (SchemeContentDto scdto : contentList) {
                String resourceId = scdto.getId();
                List<String> paths = this.designSchemeFilesService.findByResouceId(resourceId);
                scdto.setPaths(paths);
            }
        }
        result.put("scheme", scheme);
        result.put("contentList", contentList);
        return ResultFactory.generateRequestResult(result);
    }
}
