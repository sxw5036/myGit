package com.lwxf.industry4.webapp.controller.app.dealer.company;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.lwxf.industry4.webapp.common.enums.company.CompanyShareMemberStatus;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import org.springframework.web.bind.annotation.*;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.facade.app.dealer.company.ShareMemberFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：公共人员管理
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2018/12/10 0010 15:18
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value="/app/b/companies/{companyId}", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class ShareMemberController extends BaseDealerController {
	@Resource(name="shareMemberFacade")
	private ShareMemberFacade shareMemberFacade;

	//安装工列表
	@GetMapping("/shareMembers")
	public String findShareMemberByCid(@RequestParam(required = false) Integer pageNum,
									   @RequestParam(required = false) Integer pageSize,
									   @PathVariable String companyId,
									   @RequestParam Integer identity,
									   @RequestParam(required = false) Integer status,
									   HttpServletRequest request){

		if(null == pageSize){
			pageSize = AppBeanInjector.configuration.getPageSizeLimit();
		}
		if(null == pageNum){
			pageNum = 1;
		}
		if(null==status){
			status= CompanyShareMemberStatus.NORMAL.getValue();
		}
		JsonMapper resultMapper=new JsonMapper();
		String xp="bassemblemng-erectorinfo-view";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return resultMapper.toJson(result1);
		}
		RequestResult result=this.shareMemberFacade.findShareMemberList(pageNum,pageSize,companyId,identity,status);
		return resultMapper.toJson(result);
	}
	//安装人员添加
	@PostMapping("/shareMembers")
	public String addShareMember(@PathVariable String companyId,
								 @RequestBody MapContext mapContext,
								HttpServletRequest request		){

		JsonMapper resultMapper=new JsonMapper();
		String xp="bassemblemng-erectorinfo-edit";
		RequestResult result1 = this.validUserPermission(request,xp);
		if(result1!=null){
			return resultMapper.toJson(result1);
		}
		RequestResult result=this.shareMemberFacade.addShareMember(companyId,mapContext,request);
		return resultMapper.toJson(result);
	}

	//修改安装人员
	@PutMapping("/shareMembers/{userId}")
	public RequestResult updateShareMember(@PathVariable String companyId,
										   @PathVariable String userId,
										   @RequestBody MapContext mapContext,
										   HttpServletRequest request){

		String xp="bassemblemng-erectorinfo-edit";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		return this.shareMemberFacade.updateShareMember(companyId,userId,mapContext);
	}

    //删除安装人员
	@DeleteMapping("/shareMembers/{userId}")
    public RequestResult deleteShareMember(@PathVariable String companyId,
										   @PathVariable String userId,
										   HttpServletRequest request){

		String xp="bassemblemng-erectorinfo-update_status";
		RequestResult result = this.validUserPermission(request,xp);
		if(result!=null){
			return result;
		}
		return this.shareMemberFacade.deleteShareMember(companyId,userId);
	}


}
