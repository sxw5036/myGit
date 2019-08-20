package com.lwxf.industry4.webapp.facade.admin.factory.CorporatePartners.impl;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lwxf.industry4.webapp.bizservice.corporatePartners.CorporatePartnersService;
import com.lwxf.industry4.webapp.common.aop.syslog.OperationMoudule;
import com.lwxf.industry4.webapp.common.aop.syslog.OperationType;
import com.lwxf.industry4.webapp.common.aop.syslog.SysOperationLog;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.entity.corporatePartners.CorporatePartners;
import com.lwxf.industry4.webapp.facade.admin.factory.CorporatePartners.CorporatePartnersFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：Administrator
 * @create：2019/8/1/001 14:26
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("corporatePartnersFacade")
public class CorporatePartnersFacadeImpl extends BaseFacadeImpl implements CorporatePartnersFacade {

	@Resource(name = "corporatePartnersService")
	private CorporatePartnersService corporatePartnersService;

	@Override
	public RequestResult findCorporatePartnersList(MapContext mapContext, Integer pageSize, Integer pageNum) {
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		mapContext.put(WebConstant.KEY_ENTITY_BRANCH_ID,WebUtils.getCurrBranchId());
		paginatedFilter.setFilters(mapContext);
		List<Map<String,String>> sort = new ArrayList<Map<String,String>>();
		HashMap<String, String> created = new HashMap<>();
		created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
		sort.add(created);
		paginatedFilter.setSorts(sort);
		return ResultFactory.generateRequestResult(this.corporatePartnersService.findListByFilter(paginatedFilter));
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "创建外协厂家",operationType = OperationType.INSERT,operationMoudule = OperationMoudule.CORPORATE_PARTNERS)
	public RequestResult addCorporatePartners(CorporatePartners corporatePartners) {
		this.corporatePartnersService.add(corporatePartners);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "修改外协厂家",operationType = OperationType.UPDATE,operationMoudule = OperationMoudule.CORPORATE_PARTNERS)
	public RequestResult updateCorporatePartners(String id, MapContext update) {
		update.put(WebConstant.KEY_ENTITY_ID,id);
		this.corporatePartnersService.updateByMapContext(update);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	@SysOperationLog(detail = "删除外协厂家",operationType = OperationType.DELETE,operationMoudule = OperationMoudule.CORPORATE_PARTNERS)
	public RequestResult deleteCorporatePartners(String id) {
		this.corporatePartnersService.deleteById(id);
		return ResultFactory.generateSuccessResult();
	}

	@Override
	public RequestResult findCorporatePartnersInfo(String id) {
		return ResultFactory.generateRequestResult(this.corporatePartnersService.findCorporatePartnersInfo(id));
	}
}
