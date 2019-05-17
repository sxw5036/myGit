package com.lwxf.industry4.webapp.facade.app.dealer.order.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.bizservice.customorder.CustomOrderService;
import com.lwxf.industry4.webapp.common.exceptions.ErrorCodes;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.PaginatedList;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.dto.customorder.OrderQuoteDto;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.industry4.webapp.facade.app.dealer.order.OrderQuoteFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/28 0028 14:42
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("orderQuoteFacade")
public class OrderQuoteFacadeImpl extends BaseFacadeImpl implements OrderQuoteFacade {
	@Resource(name = "customOrderService")
	private CustomOrderService customOrderService;


	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateOrderQuoteMessage(String customOrderId, MapContext mapContext) {
		MapContext update=MapContext.newOne();
		if(mapContext.containsKey("marketPrice")){
			update.put("marketPrice",mapContext.get("marketPrice"));
		}
		if(mapContext.containsKey("factoryPrice")){
			update.put("factoryPrice",mapContext.get("factoryPrice"));
		}
		if(mapContext.containsKey("factoryDiscounts")){
			update.put("factoryDiscounts",mapContext.get("factoryDiscounts"));
		}
		if(mapContext.containsKey("factoryFinalPrice")){
			update.put("factoryFinalPrice",mapContext.get("factoryFinalPrice"));
		}
		if(mapContext.containsKey("discounts")){
			update.put("discounts",mapContext.get("discounts"));
		}
		if(mapContext.containsKey("amount")){
			update.put("amount",mapContext.get("amount"));
		}
		if(update.size()>0){
			update.put("id",customOrderId);
			update.put("confirmFprice",true);
			this.customOrderService.updateByMapContext(update);
		}

		return ResultFactory.generateSuccessResult();
	}

	@Override
	@Transactional(value = "transactionManager")
	public RequestResult findOrderQuoteMessage(String companyId,String confirmFprice,String condition, Integer pageNum, Integer pageSize, HttpServletRequest request) {
		String cid=request.getHeader("X-CID");
		if (!companyId.equals(cid)){
			return ResultFactory.generateErrorResult(ErrorCodes.BIZ_NOT_ALLOW_OPERATION_10020,AppBeanInjector.i18nUtil.getMessage("BIZ_NOT_ALLOW_OPERATION_10020"));
		}
		PaginatedFilter paginatedFilter = new PaginatedFilter();
		Pagination pagination = new Pagination();
		pagination.setPageSize(pageSize);
		pagination.setPageNum(pageNum);
		paginatedFilter.setPagination(pagination);
		MapContext mapContext=MapContext.newOne();
		mapContext.put("companyId",companyId);
		if(LwxfStringUtils.isNotBlank(condition)){
			mapContext.put("condition",condition);
		}
		if(LwxfStringUtils.isNotBlank(confirmFprice)){
			if(confirmFprice.equals("true")){
				mapContext.put("confirmFprice",true);
			}
			if(confirmFprice.equals("false")){
				mapContext.put("confirmFprice",false);
			}
		}
		paginatedFilter.setFilters(mapContext);
//		Map dataSort = new HashMap();
//		List sorts = new ArrayList();
//		dataSort.put("co.created","desc");
//		sorts.add(dataSort);
//		paginatedFilter.setSorts(sorts);
		PaginatedList<OrderQuoteDto> orderQuoteDtoPaginatedList=this.customOrderService.findOrderQuoteMessage(paginatedFilter);
        //进行地址拼接
		for(int i=0;i<orderQuoteDtoPaginatedList.getRows().size();i++) {
			String cityName = orderQuoteDtoPaginatedList.getRows().get(i).getMergerName();
			String address1=orderQuoteDtoPaginatedList.getRows().get(i).getCustomerAddress();
			String address="";
			if(address1!=null){
				address=address1;
			}
			if (cityName != null) {
				int a = cityName.indexOf(",");
				String mergerName = cityName.substring(a + 1);
				String replaceName = mergerName.replace(",", "");//去除逗号后的名字
				address=replaceName;
				if(address1!=null){
					address = replaceName+""+address1;

				}

			}
			orderQuoteDtoPaginatedList.getRows().get(i).setCustomerAddress(address);

		}

		MapContext result=MapContext.newOne();
		result.put("orderQuote",orderQuoteDtoPaginatedList.getRows());
		return ResultFactory.generateRequestResult(result);
	}


}
