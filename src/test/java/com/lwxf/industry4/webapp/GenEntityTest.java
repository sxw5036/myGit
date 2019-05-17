package com.lwxf.industry4.webapp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.lwxf.industry4.webapp.common.utils.GenEntity;
import com.lwxf.industry4.webapp.common.utils.GenEntity;

/**
 * 功能：根据配置，数据库表生成java实体
 *
 * @author：renzhongshan(d3shan@126.com)
 * @create：2018-05-26 10:07:46
 * @version：2018 1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public class GenEntityTest {
	public static void main(String[] args) {
		//默认生成java文件路径
		String appointPath = "E:\\gencode\\industry4";
		//不允许修改的字段配置表　key 数据库表 value数据库字段列名
		Map<String, String> mapDisabledUpdate = new HashMap<>();
		mapDisabledUpdate.put("brand", "id,companyId");
		mapDisabledUpdate.put("goods_comment","id,memberId,created");
		mapDisabledUpdate.put("goods","id,created,creator,companyId");
		mapDisabledUpdate.put("goods_extend","id");
		mapDisabledUpdate.put("goods_tag","id,goods,tag");
		mapDisabledUpdate.put("tag","id,companyId");
		mapDisabledUpdate.put("good_type","id,companyId");
		mapDisabledUpdate.put("spec_option","id");
		mapDisabledUpdate.put("goods_spec","id,companyId");
		mapDisabledUpdate.put("goods_show","id");
		mapDisabledUpdate.put("cart", "id,created");
		mapDisabledUpdate.put("store_home_nav", "id,companyId");
		mapDisabledUpdate.put("system_config", "id");
		mapDisabledUpdate.put("store_config", "id,companyId");
		mapDisabledUpdate.put("video_file","id,created,creator");
		mapDisabledUpdate.put("video_type","id,created,creator");
		mapDisabledUpdate.put("advertising", "id,created,creator");
		// 公共类的表处理
		mapDisabledUpdate.put("upload_files", "path,name,mime,originalMime,creator,created");
		// 活动日志
		mapDisabledUpdate.put("system_activity", "id,company_id,event,created,creator,r1,r2,r3,scope");
		// 用户通知
		mapDisabledUpdate.put("user_notify", "id,companyId,systemActivityId,userId,grouping,created");
		// 行政区划
		mapDisabledUpdate.put("city_area","id,parentId,name,mergerName,shortName,mergerShortName,levelType,cityCode,zipCode,namePinyin,nameJianpin,fisrtChar,lng,lat");
		/************************ goods ********************/
		//包
		String goodsPackageName = "goods";
		//表名集合
		List<String> goodsTableNameList = Arrays.asList("goods","tag","goods_tag","goods_comment","goods_extend","goods_spec","goods_type","spec_option","brand","goods_show");
		goodsTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, goodsPackageName, appointPath, mapDisabledUpdate,"F_baisi(F_baisi@163.com)")
		);
		/************************ scheme ********************/
		mapDisabledUpdate.put("scheme","id,companyId,userId,creator,created");
		mapDisabledUpdate.put("scheme_praise","schemeId,memberId,created");
		//包
		String schemePackageName = "scheme";
		//表名集合
		List<String> schemeTableNameList = Arrays.asList("scheme","scheme_praise");
		schemeTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, schemePackageName, appointPath, mapDisabledUpdate,"F_baisi(F_baisi@163.com)")
		);

		/************************ video ********************/
		//包
		String videoPackageName = "video";
		//表名集合
		List<String> videoTableNameList = Arrays.asList("video_file");
		videoTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, videoPackageName, appointPath, mapDisabledUpdate,"F_baisi(F_baisi@163.com)")
		);
		/************************ member F_baisi ********************/
		//订单管理 wangmingyuan
		mapDisabledUpdate.put("address","id,member_id，created");
//		mapDisabledUpdate.put("city_area", "id");
		mapDisabledUpdate.put("logistics", "id,companyId,deliverTime，receiptTime，created");
		mapDisabledUpdate.put("order", "id,companyId,orderNumber,memberId,created");
		mapDisabledUpdate.put("paid_records","id,companyId,memberId,paidNum,paidPrice,paidTime,type,created,orderId");
		mapDisabledUpdate.put("order_goods", "id,orderId,goodsExtendId");
//		mapDisabledUpdate.put("order_goods", "id");

		//包
		String orderPackageName = "member";
		//表名集合
		List<String> orderTableNameList = Arrays.asList("paid_records","address","logistics","order","order_goods");
		orderTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, orderPackageName, appointPath, mapDisabledUpdate,"F_baisi(F_baisi@163.com)")
		);
		/************************ microblog dongshibo ********************/
		mapDisabledUpdate.put("microblog","created,creator");
		mapDisabledUpdate.put("microblog_comment","created,creator");
		mapDisabledUpdate.put("microblog_praise","created,member_id,created");
		//包
		String microblogPackageName = "quickshare";
		//表名集合
		List<String> microblogTableNameList = Arrays.asList("microblog","microblog_comment","microblog_praise");
		microblogTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, microblogPackageName, appointPath, mapDisabledUpdate,"F_baisi(F_baisi@163.com)")
		);

		/************************ 公共的数据表common ********************/
		//包
		String commonPackageName = "common";
		//表名集合
		Arrays.asList("upload_files","city_area").stream().forEach(
				tableName -> new GenEntity(tableName, commonPackageName, appointPath, mapDisabledUpdate,"renzhongshan(d3shan@126.com)")
		);

		/************************ 头条资讯表headline ********************/
		mapDisabledUpdate.put("headline","id,creator,created");
		mapDisabledUpdate.put("headline_category","id,parentId");
		mapDisabledUpdate.put("headline_comment","id,headlineId,creator,content,created");
		mapDisabledUpdate.put("headline_praise","microblogId,memberId,created");
		mapDisabledUpdate.put("headline_files","id,name,path,mime,originalMime,size,category");
		//包
		String headlinePackageName = "headline";
		//表名集合
		List<String> headlineTableNameList = Arrays.asList("headline","headline_category","headline_comment","headline_praise","headline_files");
		headlineTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, headlinePackageName, appointPath, mapDisabledUpdate,"F_baisi(F_baisi@163.com)")
		);



		/************************ cart ********************/
		//包
		String cartPackageName = "cart";
		//表名集合
		List<String> cartTableNameList = Arrays.asList("cart");
		cartTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, cartPackageName, appointPath, mapDisabledUpdate,"F_baisi(F_baisi@163.com)")
		);

		/************************ 活动日志和用户通知 ********************/
		//包
		String sysPackageName = "system";
		//表名集合
		List<String> sysTableNameList = Arrays.asList("system_activity");
		sysTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, sysPackageName, appointPath, mapDisabledUpdate,"renzhongshan(d3shan@126.com)")
		);






		/************************ store_config ********************/
		//包
		String configPackageName = "config";
		//表名集合
		List<String> configTableNameList = Arrays.asList("system_config","store_config");
		configTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, configPackageName, appointPath, mapDisabledUpdate,"zhangjiale(zjl869319827@outlook.com)")
		);
		/************************ reservation ********************/
		mapDisabledUpdate.put("reservation","id,companyId,userId,created");
		mapDisabledUpdate.put("reservation_design_file","id");
		mapDisabledUpdate.put("reservation_design_record","id,reservationId,created,creator");
		mapDisabledUpdate.put("reservation_payed_record","id,reservationId,payedTime");
		//包
		String reservationPackageName = "reservation";
		//表名集合
		List<String> reservationTableNameList = Arrays.asList("reservation","reservation_design_file","reservation_design_record","reservation_payed_record");
		reservationTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, reservationPackageName, appointPath, mapDisabledUpdate,"F_baisi(F_baisi@163.com)")
		);




		/************************ advertising********************/
		//包
		String advPackageName = "advertising";
		//表名集合
		List<String> advTableNameList = Arrays.asList("advertising");
		advTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, advPackageName, appointPath, mapDisabledUpdate,"panchenxiao(Mister_pan@126.com)")
		);

		/************************ advertising********************/
		//包
		String versionPackageName = "version";
		//表名集合
		List<String> versionNameList = Arrays.asList("update_version");
		versionNameList.stream().forEach(
				tableName -> new GenEntity(tableName, versionPackageName, appointPath, mapDisabledUpdate,"SunXianWei(17838625030@163.com)")
		);
		/************************ news ********************/
		mapDisabledUpdate.put("news_type","id,companyId,parentId");
		mapDisabledUpdate.put("news","id,companyId,newsTypeId,created,creator");
		mapDisabledUpdate.put("news_img","id,companyId,path,created,name,newsId,newsTypeId");
		//包
		String newsPackageName = "news";
		//表名集合
		List<String> newsTableNameList = Arrays.asList("department");
		newsTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, newsPackageName, appointPath, mapDisabledUpdate,"F_baisi(F_baisi@163.com)")
		);
		/************************ user ********************/
		mapDisabledUpdate.put("user","id,created");
		mapDisabledUpdate.put("user_identity","userId,identity");
		mapDisabledUpdate.put("user_attention","userId,resourceId,resourceType,created");
		mapDisabledUpdate.put("user_extra","userId");
		mapDisabledUpdate.put("user_third_info","userId");
		mapDisabledUpdate.put("user_basis","id,userId");
		//包
		String userPackageName = "user";
		//表名集合
		List<String> userTableNameList = Arrays.asList("user","user_identity","user_attention","user_extra","user_third_info","user_notify","user_basis");
		userTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, userPackageName, appointPath, mapDisabledUpdate,"F_baisi(F_baisi@163.com)")
		);
		/************************ product ********************/
		mapDisabledUpdate.put("product","id,creator,created");
		mapDisabledUpdate.put("product_category","id");
		mapDisabledUpdate.put("product_color","id,productCategoryId");
		mapDisabledUpdate.put("product_spec","id,productCategoryId");
		mapDisabledUpdate.put("product_material","id,productCategoryId");
		//包
		String productPackageName = "product.product";
		//表名集合
		List<String> productTableNameList = Arrays.asList("product","product_category","product_color","product_material","product_spec");
		productTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, productPackageName, appointPath, mapDisabledUpdate,"F_baisi(F_baisi@163.com)")
		);

		/************************ company ********************/
		mapDisabledUpdate.put("company_employee","id,companyId,useId,created");
		mapDisabledUpdate.put("company_share_member","id,companyId,userId,identity,created");
		mapDisabledUpdate.put("employee_permission","id,employeeId");
		mapDisabledUpdate.put("share_member_permission","id,shareMemberId");
		mapDisabledUpdate.put("company", "id,created,creator");
		mapDisabledUpdate.put("dealer_shipping_logistics","companyId,logisticsCompanyId");
		//包
		String companyPackageName = "company";
		//表名集合
		List<String> companyTableNameList = Arrays.asList("company_employee","company_share_member","employee_permission","share_member_permission","company","dealer_shipping_logistics");
		companyTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, companyPackageName, appointPath, mapDisabledUpdate,"F_baisi(F_baisi@163.com)")
		);
		/************************ role ********************/
		mapDisabledUpdate.put("role","id,type,key");
		mapDisabledUpdate.put("role_permission","id,roleId,moduleKey,menuKey,operations");
		mapDisabledUpdate.put("menus","id,type,key");
		mapDisabledUpdate.put("operations","id,name,type,key");
		//包
		String rolePackageName = "system";
		//表名集合
		List<String> roleTableNameList = Arrays.asList("role","role_permission","menus","operations");
		roleTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, rolePackageName, appointPath, mapDisabledUpdate,"F_baisi(F_baisi@163.com)")
		);
		/************************ org ********************/
		mapDisabledUpdate.put("dept","id,companyId,parentId");
		mapDisabledUpdate.put("dept_member","id,deptId,employeeId");
		//包
		String orgPackageName = "org";
		//表名集合
		List<String> orgTableNameList = Arrays.asList("dept","dept_member");
		orgTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, orgPackageName, appointPath, mapDisabledUpdate,"F_baisi(F_baisi@163.com)")
		);
		/************************ warehouse ********************/
//		mapDisabledUpdate.put("finished_stock","id,storageId,orderId,creator,created,orderNo,way");
//		mapDisabledUpdate.put("finished_stock_item","id,finishedStockId,creator,created,delivered,deliverer");
//		mapDisabledUpdate.put("purchase","id,buyer,purchaseTime,supplierId,creator,created");
//		mapDisabledUpdate.put("purchase_product","id,purchaseId,supplierProductId")
		mapDisabledUpdate.put("stock","id,storageId,productId,operator,operateTime");
		mapDisabledUpdate.put("storage","id,creator,created");
		mapDisabledUpdate.put("finished_stock","id,storageId,orderId,creator,created,orderNo");
		mapDisabledUpdate.put("finished_stock_item","id,finishedStockId,operator,operated");
		mapDisabledUpdate.put("purchase","id,buyer,purchaseTime,supplierId,created,creator,batch,storageId");
		mapDisabledUpdate.put("purchase_product","id,purchaseId,supplierProductId");
		mapDisabledUpdate.put("purchase_request","id,storageId,productId,creator,created,no");
		mapDisabledUpdate.put("storage_output_in","id,creator,created,storageId,resId,type,flow,no,supplierId");
		mapDisabledUpdate.put("storage_output_in_item","id,outputInId,productId,price,supplierId,quantity");
		mapDisabledUpdate.put("supplier_product","id,supplierId,productId,creator,created");
		//包
		String warehousePackageName = "warehouse";
		//表名集合
		List<String> warehouseTableNameList = Arrays.asList("stock","storage","finished_stock","finished_stock_item","purchase","purchase_product","purchase_request","storage_output_in","storage_output_in_item");
		warehouseTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, warehousePackageName, appointPath, mapDisabledUpdate,"F_baisi(F_baisi@163.com)")
		);
		mapDisabledUpdate.put("dispatch_bill","id,orderId,orderNo,no,finishedStockId,address,cityAreaId,consigneeTel,consignee,creator,created");
		//包
		String dispatchingPackageName = "product";
		//表名集合
		List<String> dispatchingTableNameList = Arrays.asList("product_door");
		dispatchingTableNameList.stream().forEach(
				tableName -> new GenEntity(tableName, dispatchingPackageName, appointPath, mapDisabledUpdate,"panchenxiao(Mister_pan@126.com)")
		);



	}
}
