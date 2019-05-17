package com.lwxf.industry4.webapp.controller.app.dealer.order;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.order.OrderQuoteFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：订单报价详情
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/27 0027 14:05
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b/companies/{companyId}",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class OrderQuoteController extends BaseDealerController {
	@Resource(name="orderQuoteFacade")
	private OrderQuoteFacade orderQuoteFacade;


	/**
	 * 查看报价列表信息
	 * @param companyId
	 * @param request
	 * @return
	 */
	@GetMapping("/orderQuotes")
	public String findOrderQuoteMessage(@RequestParam(required = false) Integer pageNum,
										@RequestParam(required = false) Integer pageSize,
			                            @PathVariable String companyId,
										@RequestParam(required = false) String confirmFprice,
										@RequestParam(required = false) String condition,
										HttpServletRequest request){
		if(null == pageSize){
			pageSize = AppBeanInjector.configuration.getPageSizeLimit();
		}
		if(null == pageNum){
			pageNum = 1;
		}
		JsonMapper resultMapper=new JsonMapper();
		String xp="borderquotemng-quoteinfo-view";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return resultMapper.toJson(result1);
		}
		RequestResult result=this.orderQuoteFacade.findOrderQuoteMessage(companyId,confirmFprice,condition,pageNum,pageSize,request);
		return resultMapper.toJson(result);
	}

	/**
	 * 修改及确认报价信息
	 * @param customOrderId
	 * @param companyId
	 * @param request
	 * @return
	 */
    @PutMapping("/customeOrder/{customOrderId}/orderQuotes")
	public RequestResult updateOrderQuoteMessage(@PathVariable String customOrderId,
												 @PathVariable String companyId,
												 @RequestBody MapContext mapContext,
												 HttpServletRequest request){
		String xp="borderquotemng-quoteinfo-approval";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		String cid=request.getHeader("X-CID");
		if (!companyId.equals(cid)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020, AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		return this.orderQuoteFacade.updateOrderQuoteMessage(customOrderId,mapContext);
	}
}
