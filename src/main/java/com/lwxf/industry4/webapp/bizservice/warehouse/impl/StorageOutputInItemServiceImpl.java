package com.lwxf.industry4.webapp.bizservice.warehouse.impl;


import java.util.List;
import java.util.Map;


import org.springframework.stereotype.Component;


import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.lwxf.industry4.webapp.bizservice.base.BaseServiceImpl;
import com.lwxf.industry4.webapp.domain.dao.warehouse.StorageOutputInItemDao;
import com.lwxf.industry4.webapp.bizservice.warehouse.StorageOutputInItemService;
import com.lwxf.industry4.webapp.domain.entity.warehouse.StorageOutputInItem;


/**
 * 功能：
 * 
 * @author：F_baisi(F_baisi@163.com)
 * @created：2018-12-20 10:16:05
 * @version：2018 V1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Service("storageOutputInItemService")
public class StorageOutputInItemServiceImpl extends BaseServiceImpl<StorageOutputInItem, String, StorageOutputInItemDao> implements StorageOutputInItemService {


	@Resource

	@Override	public void setDao( StorageOutputInItemDao storageOutputInItemDao) {
		this.dao = storageOutputInItemDao;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public PaginatedList<StorageOutputInItem> selectByFilter(PaginatedFilter paginatedFilter) {
		//
		return this.dao.selectByFilter(paginatedFilter) ;
	}

}