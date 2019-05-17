package com.lwxf.industry4.webapp.controller.admin.factory.supplier;

import javax.annotation.Resource;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.company.CompanyStatus;
import com.lwxf.industry4.webapp.common.enums.company.CompanyType;
import com.lwxf.industry4.webapp.common.enums.user.UserSex;
import com.lwxf.industry4.webapp.common.enums.user.UserState;
import com.lwxf.industry4.webapp.common.enums.user.UserType;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyDto;
import com.lwxf.industry4.webapp.domain.entity.supplier.SupplierProduct;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.admin.factory.supplier.SupplierFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/29/029 15:22
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/api/f/suppliers",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class SupplierController {
	@Resource(name = "supplierFacade")
	private SupplierFacade supplierFacade;

	/**
	 * 查询供应商列表
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping
	private String findAllSupplierList(@RequestParam(required = false) String companyName,
									   @RequestParam(required = false)Integer status,
									   @RequestParam(required = false) String leaderTel,
									   @RequestParam(required = false) Integer pageNum,
									   @RequestParam(required = false) Integer pageSize){

		if(null == pageSize){
			pageSize = AppBeanInjector.configuration.getPageSizeLimit();
		}
		if(null == pageNum){
			pageNum = 1;
		}
		MapContext mapContext = this.createdMapContent(companyName,leaderTel,status);
		JsonMapper jsonMapper = new JsonMapper();
		return jsonMapper.toJson(this.supplierFacade.findAllSupplierList(pageNum,pageSize,mapContext));
	}

	private MapContext createdMapContent(String companyName, String leaderTel,Integer status) {
		MapContext mapContext = new MapContext();
		if(companyName!=null&&!companyName.trim().equals("")){
			mapContext.put("companyName",companyName);
		}
		if(leaderTel!=null&&!leaderTel.trim().equals("")){
			mapContext.put("leaderTel",leaderTel);
		}
		if(status!=null&&status!=-1){
			mapContext.put(WebConstant.KEY_ENTITY_STATUS,status);
		}
		return mapContext;
	}

	/**
	 * 供应商下新增产品
	 * @param id
	 * @param supplierProductList
	 * @return
	 */
	@PostMapping("{id}/products")
	private RequestResult addSupplierProdcut(@PathVariable String id,@RequestBody List<SupplierProduct> supplierProductList){
		for(SupplierProduct supplierProduct:supplierProductList){
			supplierProduct.setSupplierId(id);
			supplierProduct.setCreated(DateUtil.getSystemDate());
			supplierProduct.setCreator(WebUtils.getCurrUserId());
			RequestResult result = supplierProduct.validateFields();
			if (result!=null){
				return result;
			}
		}
		return this.supplierFacade.addSupplierProdcut(id,supplierProductList);
	}

	/**
	 * 新增供应商
	 * @param company
	 * @return
	 */
	@PostMapping
	private RequestResult addSupplier(@RequestBody CompanyDto company){
		//公司表信息
		company.setCreated(DateUtil.getSystemDate());
		company.setCreator(WebUtils.getCurrUserId());
		company.setType(CompanyType.SUPPLIER.getValue());
		company.setStatus(CompanyStatus.INTENTION.getValue());
		company.setFollowers(0);
		if(company.getNo()==null&&company.getNo().trim().equals("")){
			company.setNo(null);
		}
		RequestResult result = company.validateFields();
		if(result!=null){
			return result;
		}
		return this.supplierFacade.addSupplier(company);
	}

	/**
	 * 供应商添加负责人
	 * @param user
	 * @param id
	 * @return
	 */
	@PostMapping("{id}")
	private RequestResult addSupplierLeader(@RequestBody User user,@PathVariable String id){
		//用户信息
		user.setType(UserType.DEALER.getValue());
		user.setSex(UserSex.MAN.getValue());
		user.setAvatar(AppBeanInjector.configuration.getUserDefaultAvatar());
		user.setTimeZone(WebConstant.TIMEZONE_DEFAULT);
		user.setLanguage(WebConstant.LANGUAGE_DEFAULT);
		user.setCreated(DateUtil.getSystemDate());
		user.setState(UserState.ENABLED.getValue());
		user.setLoginName(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.USER_LOGNAME_NO));
		user.setFollowers(0);
		user.setChangedLoginName(false);
		RequestResult result = user.validateFields();
		if(result!=null){
			return result;
		}
		return this.supplierFacade.addSupplierLeader(user,id);
	}

	/**
	 * 查询供应商下的产品列表
	 * @param id
	 * @param categoryId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@GetMapping("{id}/products")
	private RequestResult findProductList(@PathVariable String id,@RequestParam(required = false) String categoryId,@RequestParam(required = false,defaultValue = "1") Integer pageNum,@RequestParam(required = false,defaultValue = "10")Integer pageSize ){
		MapContext mapContext= this.createMapContent(id,categoryId);
		return this.supplierFacade.findProductList(mapContext,pageNum,pageSize);
	}

	/**
	 * 修改供应商下的产品价格及备注
	 * @param id
	 * @param productId
	 * @param mapContext
	 * @return
	 */
	@PutMapping("{id}/products/{productId}")
	private RequestResult updateProduct(@PathVariable String id,@PathVariable String productId,@RequestBody MapContext mapContext){
		RequestResult result = SupplierProduct.validateFields(mapContext);
		if(result!=null){
			return result;
		}
		return this.supplierFacade.updateProduct(id,productId,mapContext);
	}

	/**
	 * 删除供应商下的产品
	 * @param id
	 * @param productId
	 * @return
	 */
	@DeleteMapping("{id}/products/{productId}")
	private RequestResult deleteProduct(@PathVariable String id,@PathVariable String productId){
		return this.supplierFacade.deleteProduct(id,productId);
	}

	private MapContext createMapContent(String id,String categoryId) {
		MapContext mapContext = MapContext.newOne();
		if(categoryId!=null&&!categoryId.trim().equals("")){
			mapContext.put(WebConstant.KEY_ENTITY_CATEGORY_ID,categoryId);
		}
		mapContext.put("supplierId",id);
		return mapContext;
	}
}
