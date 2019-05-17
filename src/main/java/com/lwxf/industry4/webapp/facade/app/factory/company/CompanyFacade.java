package com.lwxf.industry4.webapp.facade.app.factory.company;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyDtoForAppAdd;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CompanyFacade extends BaseFacade {
     /*
     F端app首页的上方4个统计
      */
     RequestResult getCompanyStatistics();

     /*
     访问经销商管理首页
      */
     RequestResult viewCompanyIndex();
     /*
     查询经销商列表
      */
     RequestResult findCompanyList(MapContext map,Integer pageNum,Integer pageSize);
     /*
     经销商公司app首页根据月份比较不同经销商公司的订单信息
      */
     RequestResult getCompaniesOrdersCountMonthly(MapContext mapContext);
     /*
     查询经销商详情
      */
     RequestResult findCompanyInfo(String companyId);

     RequestResult updateStatus(MapContext map);


     RequestResult addCompany(CompanyDtoForAppAdd company);

     Company checkCompanyNo(String no);

     int  checkUserMobile(String mobile);

     RequestResult updateCompany(MapContext map);

     RequestResult getUsersForAdd();

     RequestResult uploadImage(String userId, String companyId,List<MultipartFile> multipartFile);

     /*
     查看经销商财务信息(app财务信息首页)
      */
     RequestResult viewCompanyAccountInfo(String companyId);

     RequestResult importCompanies(MultipartFile file);






}
