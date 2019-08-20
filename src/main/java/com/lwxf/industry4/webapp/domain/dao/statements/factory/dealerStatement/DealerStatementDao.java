package com.lwxf.industry4.webapp.domain.dao.statements.factory.dealerStatement;

import com.lwxf.industry4.webapp.domain.dao.base.BaseNoIdDao;
import com.lwxf.industry4.webapp.domain.dto.aftersale.DateNum;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByMonthDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByQuarterDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByWeekDto;
import com.lwxf.industry4.webapp.domain.dto.statement.CountByYearDto;
import com.lwxf.mybatis.annotation.IBatisSqlTarget;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/6/28 0028 14:11
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@IBatisSqlTarget
public interface DealerStatementDao extends BaseNoIdDao {

	CountByWeekDto findDealerStatementByWeek(String branchId, String countType);

	CountByMonthDto findDealerStatementByMonth(MapContext mapContext);

	CountByQuarterDto findDealerStatementByQuarter(MapContext mapContext);

	CountByYearDto findDealerStatementByYear(String branchId, String countType);

	Integer findDealerStatements(MapContext mapContext);
}
