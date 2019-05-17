package com.lwxf.industry4.webapp.bizservice.version;


import com.lwxf.industry4.webapp.bizservice.base.BaseNoIdService;
import com.lwxf.industry4.webapp.bizservice.base.BaseService;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.domain.dto.version.VersionDto;
import com.lwxf.industry4.webapp.domain.entity.version.UpdateVersion;
import com.lwxf.mybatis.utils.MapContext;


/**
 * 功能：
 * 
 * @author：SunXianWei(17838625030@163.com)
 * @created：2019-05-06 10:02:57
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface UpdateVersionService extends BaseNoIdService <UpdateVersion> {

	PaginatedList<UpdateVersion> selectByFilter(PaginatedFilter paginatedFilter);

	UpdateVersion findVersionNo(MapContext params);

	UpdateVersion findOneByTypeAndPlatform(Integer sysType, Integer platform);

	int updateByUpdateVersion(UpdateVersion updateVersion);

	UpdateVersion findOneByVersionId(String id);

	int deleteByVersionId(String id);
}