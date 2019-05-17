package com.lwxf.industry4.webapp.facade.app.factory.factoryDispatchBill;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/4/8 0008 14:41
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface FactoryDispatchBillFacade extends BaseFacade{
	RequestResult findDispatchBillCount(String companyId);

	RequestResult findDispathcBillList(Integer pageNum, Integer pageSize, MapContext mapContext);

	RequestResult findDispatchBillInfo(String dispatchBillId,String dispatchBillItemId);

	RequestResult dispatchBillRedis(String finishedStockItemIds,String orderId);


	RequestResult addDispatchBill(MapContext mapContext, HttpServletRequest request);

	RequestResult findLogisticsList();

	RequestResult addFiles(String dispatchBillId, List<MultipartFile> multipartFiles, HttpServletRequest request);
}
