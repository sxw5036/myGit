package com.lwxf.industry4.webapp.controller.admin.factory.statement;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.admin.factory.statement.DealerStatementFacade;
import com.lwxf.mybatis.utils.MapContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/6/30 0030 9:28
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Api(value="StatementController",tags={"F端后台管理接口:业务报表接口"})
@RestController("DealerStatementController")
@RequestMapping(value = "/api/f/",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class DealerStatementController {
	@Resource(name = "dealerStatementFacade")
	private DealerStatementFacade dealerStatementFacade;

	/**
	 * 业务报表
	 * @param startTime
	 * @param endTime
	 * @param cityId
	 * @param salesmanId
	 * @return
	 */
	@ApiOperation(value="业务报表条件搜索接口",notes="业务报表条件搜索接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "startTime",value = "开始时间",dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "endTime",value = "结束时间",dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "cityId",value = "区域id",dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "salesmanId",value = "业务经理id",dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "countType",value = "要查看的类型，在网统计传“0”，签约统计传“1”，退网-2，意向-3",dataType = "string",paramType = "path",required = true)
	})
	@GetMapping("/dealerStatements/{countType}")
	private String dealerStatements(@RequestParam(required = false) String startTime,
									@RequestParam(required = false) String endTime,
									@RequestParam(required = false) String cityId,
									@RequestParam(required = false) Integer dateType,
									@RequestParam(required = false) String salesmanId,
									@PathVariable String countType){
		JsonMapper jsonMapper=new JsonMapper();
		String uid = WebUtils.getCurrUserId();
		String branchId =WebUtils.getCurrBranchId();
		if(uid==null){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		MapContext mapContext=MapContext.newOne();
		if(startTime!=null&&!startTime.equals("")){
			mapContext.put("startTime",startTime);
		}
		if(endTime!=null&&!endTime.equals("")){
			mapContext.put("endTime",endTime);
		}
		if(cityId!=null&&!cityId.equals("")){
			mapContext.put("cityId",cityId);
		}
		if(salesmanId!=null&&!salesmanId.equals("")){
			mapContext.put("salesmanId",salesmanId);
		}
		if(countType!=null&&!countType.equals("")){
			mapContext.put("countType",countType);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		RequestResult result= null;
		try {
			result = this.dealerStatementFacade.dealerStatements(sdf.parse(startTime), sdf.parse(endTime), mapContext,dateType);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return jsonMapper.toJson(result);
	}

	@ApiOperation(value="本周业务报表",notes="本周业务报表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "countType",value = "要查看的类型，在网统计传“0”，签约统计传“1”，退网-2，意向-3",dataType = "string",paramType = "path",required = true)
	})
	@GetMapping("/dealerStatements/week/{countType}")
	private String weekDealerStatements(@PathVariable String countType){
		JsonMapper jsonMapper=new JsonMapper();
		String uid = WebUtils.getCurrUserId();
		String branchId =WebUtils.getCurrBranchId();
		if(uid==null){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}

		RequestResult result=this.dealerStatementFacade.weekDealerStatements(branchId,countType);
		return jsonMapper.toJson(result);
	}
	@ApiOperation(value="本月业务报表接口",notes="本月业务报表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "countType",value = "要查看的类型，在网统计传“0”，签约统计传“1”，退网-2，意向-3",dataType = "string",paramType = "path",required = true)
	})
	@GetMapping("/dealerStatements/month/{countType}")
	private String monthDealerStatements(@PathVariable String countType){
		JsonMapper jsonMapper=new JsonMapper();
		String uid = WebUtils.getCurrUserId();
		String branchId =WebUtils.getCurrBranchId();
		if(uid==null){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		RequestResult result=this.dealerStatementFacade.monthDealerStatements(branchId,countType);
		return jsonMapper.toJson(result);
	}
	@ApiOperation(value="本季业务报表接口",notes="本季业务报表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "countType",value = "要查看的类型，在网统计传“0”，签约统计传“1”，退网-2，意向-3",dataType = "string",paramType = "path",required = true)
	})
	@GetMapping("/dealerStatements/quarter/{countType}")
	private String quarterDealerStatements(@PathVariable String countType){
		JsonMapper jsonMapper=new JsonMapper();
		String uid = WebUtils.getCurrUserId();
		String branchId =WebUtils.getCurrBranchId();
		if(uid==null){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		RequestResult result=this.dealerStatementFacade.quarterDealerStatements(branchId,countType);
		return jsonMapper.toJson(result);
	}
	@ApiOperation(value="本年业务报表接口",notes="本年业务报表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "countType",value = "要查看的类型，在网统计传“0”，签约统计传“1”，退网-2，意向-3",dataType = "string",paramType = "path",required = true)
	})
	@GetMapping("/dealerStatements/year/{countType}")
	private String yearDealerStatements(@PathVariable String countType){
		JsonMapper jsonMapper=new JsonMapper();
		String uid = WebUtils.getCurrUserId();
		String branchId =WebUtils.getCurrBranchId();
		if(uid==null){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		RequestResult result=this.dealerStatementFacade.yearDealerStatements(branchId,countType);
		return jsonMapper.toJson(result);
	}
    @ApiOperation(value = "业务经理接口",notes = "业务经理接口")
	@GetMapping("/dealerStatements/salemans")
	private String getSalemans(){
		JsonMapper jsonMapper=new JsonMapper();
		String uid = WebUtils.getCurrUserId();
		String branchId =WebUtils.getCurrBranchId();
		if(uid==null){
			return jsonMapper.toJson(ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_USER_NOT_LOGGED, AppBeanInjector.i18nUtil.getMessage("VALIDATE_USER_NOT_LOGGED")));
		}
		RequestResult result=this.dealerStatementFacade.getSalemans(branchId);
		return jsonMapper.toJson(result);
	}
}
