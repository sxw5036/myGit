package com.lwxf.industry4.webapp.controller.admin.factory.warehouse;

import io.swagger.annotations.*;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.storage.FinishedStockStatus;
import com.lwxf.industry4.webapp.common.enums.storage.FinishedStockWay;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.FileMimeTypeUtil;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.warehouse.DispatchBillPlanDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockItemDto;
import com.lwxf.industry4.webapp.domain.entity.customorder.CustomOrderFiles;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBillPlanItem;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStock;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStockItem;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.admin.factory.warehouse.FinishedStockFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/24/024 13:41
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/api/f/storages/{storageId}/finisheds",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
@Api(value = "FinishedStockController",tags = "成品库管理")
public class FinishedStockController {
	@Resource(name = "finishedStockFacade")
	private FinishedStockFacade finishedStockFacade;

	/**
	 * 查询成品库包裹列表
	 * @param way
	 * @param orderNo
	 * @param status
	 * @return
	 */
	@ApiResponses({
			@ApiResponse(code = 200,message = "查询成功",response =FinishedStockItemDto.class)
	})
	@ApiOperation(value = "查询成品库包裹列表",notes = "查询成品库包裹列表")
	@ApiImplicitParams({
			@ApiImplicitParam(value = "入库方式：0 - 系统自动入库；1 - 人工手动入库",name = "way",paramType = "query",dataType = "int"),
			@ApiImplicitParam(value = "订单编号",name = "orderNo",paramType = "query",dataType = "string"),
			@ApiImplicitParam(value = "状态：0 - 未配送；1 - 部分配送；2 - 全部配送",name = "status",paramType = "query",dataType = "int"),
			@ApiImplicitParam(value = "是否已创建配送计划",name = "ship",paramType = "query",dataType = "int"),
			@ApiImplicitParam(value = "是否已入库",name = "in",paramType = "query",dataType = "int"),
			@ApiImplicitParam(value = "包装编号/条形码",name = "barcode",paramType = "query",dataType = "int"),
			@ApiImplicitParam(value = "是否发货",name = "delivery",dataType = "boolean", paramType = "query"),
			@ApiImplicitParam(value = "页数",name = "pageSize",dataType = "int", paramType = "query"),
			@ApiImplicitParam(value = "页码",name = "pageNum",dataType = "int",paramType = "query")
	})
	@GetMapping
	private String findFinishedDto(@PathVariable String storageId,
								   @RequestParam(required = false) Integer way,
								   @RequestParam(required = false) String orderNo,
								   @RequestParam(required = false) Integer status,
								   @RequestParam(required = false) Integer ship,
								   @RequestParam(required = false) Integer in,
								   @RequestParam(required = false) String barcode,
								   @RequestParam(required = false) Integer type,
								   @RequestParam(required = false) Boolean delivery,
								   @RequestParam(required = false) Integer pageNum,
								   @RequestParam(required = false) Integer pageSize){
 		if(null == pageSize){
			pageSize = AppBeanInjector.configuration.getPageSizeLimit();
		}
		if(null == pageNum){
			pageNum = 1;
		}
		MapContext mapContext = this.createMapContent(way,orderNo,status,storageId,ship,in,barcode,type,delivery);
		JsonMapper jsonMapper = new JsonMapper();
		return jsonMapper.toJson(this.finishedStockFacade.findFinishedDto(mapContext,pageNum,pageSize));
	}

	/**
	 * 新增成品库单
	 * @param finishedStockDto
	 * @return
	 */
	@PostMapping
	private RequestResult addFinishedStock(@PathVariable String storageId,@RequestBody FinishedStockDto finishedStockDto){
		finishedStockDto.setStorageId(storageId);
		finishedStockDto.setCreator(WebUtils.getCurrUserId());
		finishedStockDto.setCreated(DateUtil.getSystemDate());
		finishedStockDto.setStatus(FinishedStockStatus.UNSHIPPED.getValue());
		finishedStockDto.setWay(FinishedStockWay.MANUAL.getValue());
		RequestResult result = finishedStockDto.validateFields();
		if(result!=null){
			return result;
		}
		//判断条目数是否大于包数
		if(finishedStockDto.getPackages()<finishedStockDto.getFinishedStockItemDtos().size()){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_RESOURCES_LIMIT_10076,AppBeanInjector.i18nUtil.getMessage("BIZ_RESOURCES_LIMIT_10076"));
		}
		for(FinishedStockItemDto finishedStockItemDto:finishedStockDto.getFinishedStockItemDtos()){
			finishedStockItemDto.setCreated(DateUtil.getSystemDate());
			finishedStockItemDto.setCreator(WebUtils.getCurrUserId());
			finishedStockItemDto.setShipped(false);
			finishedStockItemDto.setIn(false);
			RequestResult requestResult = finishedStockItemDto.validateFields();
			if(requestResult!=null){
				return requestResult;
			}
		}
		return this.finishedStockFacade.addFinishedStock(finishedStockDto);
	}

	/**
	 * 成品库单下新增条目
	 * @param finishedStockItem
	 * @param id
	 * @return
	 */
	@PostMapping("/{id}")
	private RequestResult addFinishedItem(@PathVariable String storageId ,@RequestBody FinishedStockItem finishedStockItem,@PathVariable String id){
		finishedStockItem.setCreated(DateUtil.getSystemDate());
		finishedStockItem.setCreator(WebUtils.getCurrUserId());
		finishedStockItem.setShipped(false);
		finishedStockItem.setIn(false);
		finishedStockItem.setFinishedStockId(id);
		RequestResult result = finishedStockItem.validateFields();
		if(result!=null){
			return result;
		}
		return this.finishedStockFacade.addFinishedItem(finishedStockItem,id,storageId);
	}

	/**
	 * 修改成品库单
	 * @param id
	 * @param mapContext
	 * @return
	 */
	@PutMapping("/{id}")
	private RequestResult updateFinishedStock(@PathVariable String id,@RequestBody MapContext mapContext,@PathVariable String storageId){
		RequestResult result = FinishedStock.validateFields(mapContext);
		if(result!=null){
			return result;
		}
		return this.finishedStockFacade.updateFinishedStock(id,mapContext,storageId);
	}

	/**
	 * 删除成品库单
	 * @param id
	 * @return
	 */
	@DeleteMapping("/{id}")
	private RequestResult deleteFinishedStockById(@PathVariable String id,@PathVariable String storageId){
		return this.finishedStockFacade.deleteFinishedStockById(id,storageId);
	}

	/**
	 * 修改成品库条目详情
	 * @param id
	 * @param itemId
	 * @param mapContext
	 * @return
	 */
	@PutMapping("/{id}/items/{itemId}")
	private RequestResult updateItemById(@PathVariable String id,@PathVariable String itemId,@RequestBody MapContext mapContext,@PathVariable String storageId){
		RequestResult result = FinishedStockItem.validateFields(mapContext);
		if(result!=null){
			return result;
		}
		return this.finishedStockFacade.updateItemById(id,itemId,mapContext,storageId);
	}

	/**
	 * 删除成品库下的条目
	 * @param id
	 * @param itemId
	 * @return
	 */
//	@DeleteMapping("/{id}/items/{itemId}")
//	private RequestResult deleteByItemId(@PathVariable String id,@PathVariable String itemId,@PathVariable String storageId){
//		return this.finishedStockFacade.deleteByItemId(id,itemId,storageId);
//	}

	/**
	 * 成品库下的条目入库
	 * @param id
	 * @param itemId
	 * @return
	 */
	@ApiResponses({
			@ApiResponse(code = 200,message = "入库成功",response = FinishedStockItemDto.class)
	})
	@ApiImplicitParams({
			@ApiImplicitParam(value = "成品库主键ID",required = true,name = "id",dataType = "string",paramType = "body"),
			@ApiImplicitParam(value = "条目ID",required = true,name = "itemId",dataType = "string",paramType = "body"),
			@ApiImplicitParam(value = "仓库ID",required = true,name = "storageId",dataType = "string",paramType = "body")
	})
	@ApiOperation(value = "成品库下的条目入库",notes = "成品库下的条目入库")
	@PutMapping("/{id}/warehousing/{itemId}")
	private RequestResult itemWarehousing(@PathVariable String id,@PathVariable String itemId,@PathVariable String storageId,@RequestBody MapContext mapContext){
		RequestResult result = FinishedStockItem.validateFields(mapContext);
		if(result!=null){
			return result;
		}
		return this.finishedStockFacade.itemWarehousing(id,itemId,storageId,mapContext);
	}

	/**
	 * 包裹上传资源文件
	 * @param id
	 * @param itemId
	 * @param multipartFileList
	 * @return
	 */
	@PostMapping("/{id}/packages/{itemId}/files")
	@ApiOperation(value = "包裹上传资源文件",notes = "包裹上传资源文件",response = CustomOrderFiles.class)
	private RequestResult uploadPackageFiles(@PathVariable@ApiParam(value = "订单主键ID") String id, @PathVariable@ApiParam(value = "包裹条目ID 如果没有 则传 itemId") String itemId, @RequestBody List<MultipartFile> multipartFileList){
		Map<String, String> result = new HashMap<>();
		if (multipartFileList == null || multipartFileList.size() == 0) {
			result.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, result);
		}
		for (MultipartFile multipartFile : multipartFileList) {
			if (multipartFile == null) {
				result.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOTNULL"));
			} else if (!FileMimeTypeUtil.isLegalImageType(multipartFile)) {
				result.put("file", AppBeanInjector.i18nUtil.getMessage("VALIDATE_ILLEGAL_ARGUMENT"));
			} else if (multipartFile.getSize() > 1024 * 1024 * AppBeanInjector.configuration.getUploadBackgroundMaxsize()) {
				return ResultFactory.generateErrorResult(ErrorCodes.BIZ_FILE_SIZE_LIMIT_10031, LwxfStringUtils.format(AppBeanInjector.i18nUtil.getMessage("BIZ_FILE_SIZE_LIMIT_10031"), AppBeanInjector.configuration.getUploadBackgroundMaxsize()));
			}
			if (result.size() > 0) {
				return ResultFactory.generateErrorResult(ErrorCodes.VALIDATE_ERROR, result);
			}
		}
		return this.finishedStockFacade.uploadPackageFiles(id,itemId,multipartFileList);
	}


	/**
	 * 删除包裹相关资源文件
	 * @param id
	 * @param itemId
	 * @param fileId
	 * @return
	 */
	@DeleteMapping("/{id}/packages/{itemId}/files/{fileId}")
	@ApiOperation(value = "删除包裹相关资源文件",notes = "删除包裹相关资源文件")
	private RequestResult deletePackageFiles(@PathVariable String id,@PathVariable String itemId,@PathVariable String fileId){
		return this.finishedStockFacade.deletePackageFiles(id,itemId,fileId);
	}


	/**
	 * 创建配送计划
	 * @param dispatchBillPlanDto
	 * @return
	 */
	@ApiOperation(value = "创建配送计划",notes = "创建配送计划")
	@ApiImplicitParams({
			@ApiImplicitParam(value = "配送计划条目",name = "dispatchBillPlanItems",dataTypeClass = DispatchBillPlanItem.class,paramType = "body")
	})
	@PostMapping("/dispatchplan")
	private RequestResult addDispatchPlan(@RequestBody DispatchBillPlanDto dispatchBillPlanDto){
		if(dispatchBillPlanDto.getDispatchBillPlanItems().size()==0){
			return ResultFactory.generateResNotFoundResult();
		}
		return this.finishedStockFacade.addDispatchPlan(dispatchBillPlanDto);
	}

	private MapContext createMapContent(Integer way, String orderNo, Integer status,String storageId,Integer ship,Integer in,String barcode,Integer type,Boolean delivery) {
		MapContext mapContext = new MapContext();
		if (way!=null){
			mapContext.put("way",way);
		}
		if(orderNo!=null&&!orderNo.trim().equals("")){
			mapContext.put("orderNo",orderNo);
		}
		if(status!=null){
			mapContext.put(WebConstant.KEY_ENTITY_STATUS,status);
		}
		if(storageId!=null&&!storageId.trim().equals("all")){
			mapContext.put("storageId",storageId);
		}
		if(ship!=null){
			mapContext.put("ship",ship);
		}
		if(in!=null){
			mapContext.put("ins",in);
		}
		if(barcode!=null){
			mapContext.put("barcode",barcode);
		}
		if(type!=null){
			mapContext.put("type",type);
		}
		if(delivery!=null){
			mapContext.put("delivery",delivery);
		}
		return mapContext;
	}
}
