package com.lwxf.industry4.webapp.domain.dao.financing.impl;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentSimpleFundsDto;
import org.springframework.stereotype.Repository;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDaoImpl;
import com.lwxf.industry4.webapp.domain.dao.financing.PaymentSimpleFundsDao;
import com.lwxf.industry4.webapp.domain.entity.financing.PaymentSimpleFunds;

import java.util.List;

/**
 * 功能：
 * 
 * @author：djl(yuuyoo@163.com)
 * @created：2019-07-23 10:10:19
 * @version：2019 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Repository("paymentSimpleFundsDao")
public class PaymentSimpleFundsDaoImpl extends BaseDaoImpl<PaymentSimpleFunds, String> implements PaymentSimpleFundsDao {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<PaymentSimpleFunds> selectByFilter(PaginatedFilter paginatedFilter) {
		String sqlId = this.getNamedSqlId("selectByFilter");
		//
		//  过滤查询参数
		PageBounds pageBounds = this.toPageBounds(paginatedFilter.getPagination(), paginatedFilter.getSorts());
		PageList<PaymentSimpleFunds> pageList = (PageList) this.getSqlSession().selectList(sqlId, paginatedFilter.getFilters(), pageBounds);
		return this.toPaginatedList(pageList);
	}

	@Override
	public int insertBatch(List<PaymentSimpleFundsDto> fundsList) {
		String sqlId = this.getNamedSqlId("insertBatch");
		return this.getSqlSession().insert(sqlId, fundsList);
	}

	@Override
	public int deleteByPid(String paymentSimpleId) {
		String slqId = this.getNamedSqlId("deleteByPid");
		return this.getSqlSession().delete(slqId, paymentSimpleId);
	}


}