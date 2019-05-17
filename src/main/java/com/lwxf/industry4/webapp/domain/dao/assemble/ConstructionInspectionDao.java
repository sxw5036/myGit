package com.lwxf.industry4.webapp.domain.dao.assemble;


import java.util.List;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dto.dispatchList.ConstructionInspectionDto;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.mybatis.annotation.IBatisSqlTarget;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDao;
import com.lwxf.industry4.webapp.domain.entity.assemble.ConstructionInspection;


/**
 * 功能：
 * 
 * @author：SunXianWei(17838625030@163.com)
 * @created：2018-12-19 15:05:23
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@IBatisSqlTarget
public interface ConstructionInspectionDao extends BaseDao<ConstructionInspection, String> {

	PaginatedList<ConstructionInspection> selectByFilter(PaginatedFilter paginatedFilter);

	ConstructionInspectionDto findConstructionByDispatchId(String dispatchListId);
}