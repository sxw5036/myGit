package com.lwxf.industry4.webapp.facade.admin.factory.system.impl;

import com.lwxf.industry4.webapp.bizservice.system.BasecodeService;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.entity.system.Basecode;
import com.lwxf.industry4.webapp.facade.admin.factory.system.BaseCodeFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("baseCodeFacade")
public class BaseCodeFacadeImpl extends BaseFacadeImpl implements BaseCodeFacade {

    @Resource(name = "basecodeService")
    private BasecodeService basecodeService;

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult add(Basecode basecode) {
        basecode.setDelFlag(0);
        return ResultFactory.generateRequestResult(this.basecodeService.add(basecode));
    }

    @Override
    public RequestResult findById(String baseCodeId) {
        return ResultFactory.generateRequestResult(this.basecodeService.findById(baseCodeId));
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult update(String basecodeId, MapContext params) {
        RequestResult requestResult = Basecode.validateFields(params);
        if (null != requestResult) {
            return requestResult;
        }
        this.basecodeService.updateByMapContext(params);
        Basecode bc = basecodeService.findById(basecodeId);
        return ResultFactory.generateRequestResult(bc);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult delete(String id) {
        return ResultFactory.generateRequestResult(this.basecodeService.deleteById(id));
    }

    @Override
    public RequestResult findListBasecodes(MapContext mapContext, Integer pageNum, Integer pageSize) {
        PaginatedFilter paginatedFilter = new PaginatedFilter();
        paginatedFilter.setFilters(mapContext);
        Pagination pagination = new Pagination();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        paginatedFilter.setPagination(pagination);
        Map<String,String> created = new HashMap<String, String>();
        created.put(WebConstant.KEY_ENTITY_NAME,"desc");
        List sort = new ArrayList();
        sort.add(created);
        paginatedFilter.setSorts(sort);
        return ResultFactory.generateRequestResult(this.basecodeService.selectByFilter(paginatedFilter));
    }

    @Override
    public RequestResult findByMap(MapContext mapContext) {
        return ResultFactory.generateRequestResult(this.basecodeService.selectByFilter(mapContext));
    }
}
