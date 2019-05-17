package com.lwxf.industry4.webapp.domain.dao.contentmng;


import java.util.List;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dto.contentmng.ContentsDto;
import com.lwxf.mybatis.utils.MapContext;
import com.lwxf.mybatis.annotation.IBatisSqlTarget;
import com.lwxf.industry4.webapp.domain.dao.base.BaseDao;
import com.lwxf.industry4.webapp.domain.entity.contentmng.ContentsType;


/**
 * 功能：
 * 
 * @author：SunXianWei(17838625030@163.com)
 * @created：2019-03-27 11:58:34
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@IBatisSqlTarget
public interface ContentsTypeDao extends BaseDao<ContentsType, String> {

	PaginatedList<ContentsType> selectByFilter(PaginatedFilter paginatedFilter);

	List<ContentsType> findContentsTypeList();

	ContentsType findContentsListByCode(String code);
}