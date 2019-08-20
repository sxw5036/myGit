package com.lwxf.industry4.webapp.domain.dao.statements.factory.dealerStatement.impl;

import org.springframework.stereotype.Repository;

import com.lwxf.industry4.webapp.domain.dao.base.BaseNoIdDaoImpl;
import com.lwxf.industry4.webapp.domain.dao.statements.factory.dealerStatement.DealerStatementDao;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByMonthDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByQuarterDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByWeekDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByYearDto;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/6/28 0028 14:12
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Repository("dealerStatementDao")
public class DealerStatementDaoImpl extends BaseNoIdDaoImpl implements DealerStatementDao {
	@Override
	public CountByWeekDto findDealerStatementByWeek(String branchId, String countType) {
		String sqlId=this.getNamedSqlId("findDealerStatementByWeek");
		MapContext mapContext=MapContext.newOne();
		mapContext.put("branchId",branchId);
		mapContext.put("countType",countType);
		return this.getSqlSession().selectOne(sqlId,mapContext);
	}

	@Override
	public CountByMonthDto findDealerStatementByMonth(MapContext mapContext) {
		String sqlId=this.getNamedSqlId("findDealerStatementByMonth");
		return this.getSqlSession().selectOne(sqlId,mapContext);
	}

	@Override
	public CountByQuarterDto findDealerStatementByQuarter(MapContext mapContext) {
		String sqlId=this.getNamedSqlId("findDealerStatementByQuarter");
		return this.getSqlSession().selectOne(sqlId,mapContext);
	}

	@Override
	public CountByYearDto findDealerStatementByYear(String branchId, String countType) {
		String sqlId=this.getNamedSqlId("findDealerStatementByYear");
		MapContext mapContext=MapContext.newOne();
		mapContext.put("branchId",branchId);
		mapContext.put("countType",countType);
		return this.getSqlSession().selectOne(sqlId,mapContext);
	}

	@Override
	public Integer findDealerStatements(MapContext mapContext) {
		String sqlId=this.getNamedSqlId("findDealerStatements");
		return this.getSqlSession().selectOne(sqlId,mapContext);
	}
}
