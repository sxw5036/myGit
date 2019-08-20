package com.lwxf.industry4.webapp.facade.admin.factory.warehouse;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.utils.excel.ExcelParam;
import com.lwxf.industry4.webapp.domain.dto.warehouse.DispatchBillPlanDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockDto;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBillPlanItem;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStockItem;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/24/024 13:46
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface FinishedStockFacade extends BaseFacade {
	RequestResult findFinishedDto(MapContext mapContext, Integer pageNum, Integer pageSize);

	RequestResult addFinishedStock(FinishedStockDto finishedStockDto);

	RequestResult addFinishedItem(FinishedStockItem finishedStockItem, String id,String storageId);

	RequestResult updateFinishedStock(String id, MapContext mapContext,String storageId);

	RequestResult deleteFinishedStockById(String id,String storageId);

	RequestResult updateItemById(String itemId,MapContext mapContext);

	RequestResult deleteByItemId(String id, String itemId,String storageId);

	RequestResult itemWarehousing(String itemId,MapContext mapContext);

	RequestResult addDispatchPlan(DispatchBillPlanDto dispatchBillPlanDto);

	RequestResult uploadPackageFiles(String id, String itemId, List<MultipartFile> multipartFileList);

	RequestResult deletePackageFiles(String id, String itemId, String fileId);

	RequestResult writeExcel(PaginatedFilter paginatedFilter, ExcelParam excelParam);

	RequestResult findFinishedStockNos(String order,MapContext mapContext);

	RequestResult findFinishedStockCount(String branchId);

	RequestResult addDispatchPlanByOrder(DispatchBillPlanDto dispatchBillPlanDto);
}
