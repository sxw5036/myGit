package com.lwxf.industry4.webapp.controller.admin.factory.warehouse;

import javax.annotation.Resource;

import java.util.List;

import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import org.springframework.web.bind.annotation.*;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.entity.warehouse.Stock;
import com.lwxf.industry4.webapp.facade.admin.factory.warehouse.StockFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/14/014 16:53
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/api/f/storages/{id}/stocks",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class StockController {
	@Resource(name = "stockFacade")
	private StockFacade stockFacade;

	/**
	 * 添加库存
	 * @param stock
	 * @return
	 */
	@PostMapping
	private RequestResult addStock(@RequestBody Stock stock,@PathVariable String id){
		stock.setStorageId(id);
		stock.setOperateTime(DateUtil.getSystemDate());
		stock.setOperator(WebUtils.getCurrUserId());
		RequestResult result = stock.validateFields();
		if(result!=null){
			return result;
		}
		return this.stockFacade.addStock(stock);
	}

	/**
	 * 根据仓库查询商品列表
	 * @param id
	 * @return
	 */
	@GetMapping
	private RequestResult findStockList(@PathVariable String id,
										@RequestParam(required = false) Integer pageNum,
										@RequestParam(required = false) Integer pageSize,
										@RequestParam(required = false) String productName){
		if(null == pageSize){
			pageSize = AppBeanInjector.configuration.getPageSizeLimit();
		}
		if(null == pageNum){
			pageNum = 1;
		}
		MapContext mapContext = this.createdMapContent(productName);
		return this.stockFacade.findStockList(id,mapContext,pageNum,pageSize);
	}

	private MapContext createdMapContent(String productName) {
		MapContext mapContext = MapContext.newOne();
		if (productName!=null&&!productName.trim().equals("")){
			mapContext.put(WebConstant.KEY_ENTITY_NAME,productName);
		}
		return mapContext;
	}

	/**
	 * 修改仓库库存
	 * @param id
	 * @param stockId
	 * @param mapContext
	 * @return
	 */
	@PutMapping("/{stockId}")
	private RequestResult updateStock(@PathVariable String id, @PathVariable String stockId, @RequestBody MapContext mapContext){
		RequestResult result = Stock.validateFields(mapContext);
		if(result!=null){
			return result;
		}
		return this.stockFacade.updateStock(id,stockId,mapContext);
	}

	/**
	 * 删除仓库下的某种库存
	 * @param id
	 * @param stockId
	 * @return
	 */
	@DeleteMapping("/{stockId}")
	private RequestResult deleteById(@PathVariable String id, @PathVariable String stockId){
		return this.stockFacade.deleteById(id,stockId);
	}

	/**
	 * 出库
	 * @param id
	 * @param stockList
	 * @return
	 */
	@PutMapping("/quantities")
	private RequestResult updateColumn(@PathVariable String id,@RequestBody List<Stock> stockList){
		return this.stockFacade.updateColumn(id,stockList);
	}
}
