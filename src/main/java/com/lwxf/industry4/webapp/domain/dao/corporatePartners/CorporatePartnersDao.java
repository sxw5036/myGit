package com.lwxf.industry4.webapp.domain.dao.corporatePartners;


import java.util.List;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dto.corporatePartners.CorporatePartnersDto;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.mybatis.annotation.IBatisSqlTarget;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDao;
import com.lwxf.industry4.webapp.domain.entity.corporatePartners.CorporatePartners;


/**
 * 功能：
 * 
 * @author：F_baisi(F_baisi@126.com)
 * @created：2019-08-01 14:13:42
 * @version：2019 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@IBatisSqlTarget
public interface CorporatePartnersDao extends BaseDao<CorporatePartners, String> {

	PaginatedList<CorporatePartners> selectByFilter(PaginatedFilter paginatedFilter);

	PaginatedList<CorporatePartnersDto> findListByFilter(PaginatedFilter paginatedFilter);

	CorporatePartnersDto findCorporatePartnersInfo(String id);
}