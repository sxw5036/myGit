package com.lwxf.industry4.webapp.facade.admin.factory.dealer;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyDto;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：dongshibo(F_baisi)
 * @create：2018/12/5/005 13:26
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface DealerFacade extends BaseFacade {
	RequestResult addDealer(CompanyDto company);

	RequestResult findDealerCompanyList(MapContext mapContent, Integer pageNum, Integer pageSize);

	RequestResult openDealer(User user,String cid,StringBuffer pwd);

	RequestResult updateDealerCompany(MapContext mapContext, String cid);

	RequestResult uploadDealerFiles(String cid, List<MultipartFile> multipartFileList);

	RequestResult deleteDealerFiles(String cid, String fileId);

	RequestResult submitDealer(String cid,Payment payment);

	RequestResult findDealerList(MapContext mapContext, Integer pageNum, Integer pageSize);

	RequestResult updateDealer(MapContext mapContext, String id);

	RequestResult updateDealerMobile(String id, String mobile);

	RequestResult updateDealerAccountPwd(String id, String newPassword);

	RequestResult findDealerCompanyInfo(String cid);
}
