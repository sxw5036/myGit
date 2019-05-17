package com.lwxf.industry4.webapp.controller.admin.factory.customOrder;

import io.swagger.annotations.*;

import javax.annotation.Resource;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.dto.customorder.CustomOrderDto;
import com.lwxf.industry4.webapp.domain.dto.warehouse.FinishedStockItemDto;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStockItem;
import com.lwxf.industry4.webapp.facade.app.dealer.order.OrderFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：订单打包包装
 *
 * @author：Administrator
 * @create：2019/4/18/018 17:16
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/api/f/customorders",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
@Api(value = "OrderPackController",tags = "{订单打包管理}")
public class OrderPackController {

	@Resource(name="orderFacade")
	private OrderFacade orderFacade;

	/**
	 * 订单下打包包裹
	 * @param id
	 * @param finishedStockItemDtoList
	 * @return
	 */
	@PostMapping("/{id}")
	@ApiOperation(value = "订单下打包包裹",notes = "订单下打包包裹")
	@ApiImplicitParams({
			@ApiImplicitParam(value = "订单主键ID",name = "id",dataType = "string",paramType = "path")
	})
	private RequestResult addOrderPack(@PathVariable String id, @RequestBody@ApiParam(value = "包裹") List<FinishedStockItemDto> finishedStockItemDtoList){
		return this.orderFacade.addOrderPack(id,finishedStockItemDtoList);
	}

	/**
	 * 订单打包完成
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "订单打包完成",notes = "订单打包完成")
	@ApiImplicitParams({
			@ApiImplicitParam(value = "订单主键ID",name = "id",dataType = "string",paramType = "path")
	})
	@PutMapping("/{id}/packed")
	private RequestResult orderPacked(@PathVariable String id){
		return this.orderFacade.orderPacked(id);
	}

	/**
	 * 查询尚未打包完毕的 订单
	 * @param pageNum
	 * @param pageSize
	 * @param orderNo
	 * @return
	 */
	@ApiResponses({
			@ApiResponse(code = 200, message = "查询成功", response = CustomOrderDto.class)
	})
	@ApiOperation(value = "查询尚未打包完毕的 订单",notes = "查询尚未打包完毕的 订单")
	@ApiImplicitParams({
			@ApiImplicitParam(value = "页码",name = "pageNum",dataType = "string",paramType = "query"),
			@ApiImplicitParam(value = "大小",name = "pageSize",dataType = "string",paramType = "query")
	})
	@GetMapping("/packs")
	private RequestResult findPacksOrderList(@RequestParam(required = false,defaultValue = "1")Integer pageNum,@RequestParam(required = false,defaultValue = "10")Integer pageSize
	,@RequestParam(required = false)String orderNo){
		MapContext mapContext = this.createMapContext(orderNo);
		return this.orderFacade.findPacksOrderList(pageNum,pageSize,mapContext);
	}

	/**
	 * 查询当前订单的下一个包裹编号
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}/no")
	@ApiOperation(value = "查询当前订单的下一个包裹编号",notes = "查询当前订单的下一个包裹编号")
	@ApiImplicitParams({
			@ApiImplicitParam(value = "订单Id",name = "id",dataType = "string",paramType = "path")
	})
	private RequestResult findOrderPackagesNo(@PathVariable String id){
		return this.orderFacade.findOrderPackagesNo(id);
	}

	private MapContext createMapContext(String orderNo) {
		MapContext mapContext = new MapContext();
		if(orderNo!=null){
			mapContext.put("orderNo",orderNo);
		}
		return mapContext;
	}
}
