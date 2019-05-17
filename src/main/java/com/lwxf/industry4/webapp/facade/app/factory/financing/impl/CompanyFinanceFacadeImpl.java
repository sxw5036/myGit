package com.lwxf.industry4.webapp.facade.app.factory.financing.impl;

import com.lwxf.commons.utils.DateUtil;
import com.lwxf.industry4.webapp.bizservice.common.UploadFilesService;
import com.lwxf.industry4.webapp.bizservice.company.CompanyService;
import com.lwxf.industry4.webapp.bizservice.dealer.DealerAccountService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentFilesService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.company.DealerAccountType;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentFunds;
import com.lwxf.industry4.webapp.common.enums.financing.PaymentTypeNew;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.domain.dto.company.CompanyPaymentInfoDto;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.CompanyFinanceInfoDto;
import com.lwxf.industry4.webapp.domain.dto.financing.dtoForApp.CompanyFinanceListDto;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.dealer.DealerAccount;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.financing.PaymentFiles;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.financing.CompanyFinanceFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("CompanyFinanceFacade")
public class CompanyFinanceFacadeImpl extends BaseFacadeImpl implements CompanyFinanceFacade {

    @Resource(name = "paymentService")
    private PaymentService paymentService;
    @Resource(name = "paymentFilesService")
    private PaymentFilesService paymentFilesService;
    @Resource(name = "dealerAccountService")
    private DealerAccountService dealerAccountService;
    @Resource(name = "uploadFilesService")
    private UploadFilesService uploadFilesService;
    @Resource(name = "companyService")
    private CompanyService companyService;

    @Override
    public RequestResult viewIndex() {
        MapContext map = MapContext.newOne();
        map.put("top",paymentService.countCompanyFinance());
        //类型科目--扣款 对应type:类型
        List<MapContext> listTypes = new ArrayList<>();
        for (PaymentTypeNew p : PaymentTypeNew.values()) {
                List<MapContext> listFunds = new ArrayList<>();
                for (PaymentFunds s : PaymentFunds.values()) {
                    if(s.getValue().intValue()>30 &&s.getValue().intValue()<40) {
                        MapContext mapValue = MapContext.newOne();
                        mapValue.put("name", s.getName());
                        mapValue.put("id", s.getValue());
                        listFunds.add(mapValue);
                    }
                    if(s.getValue().intValue()>20 &&s.getValue().intValue()<30) {
                        MapContext mapValue = MapContext.newOne();
                        mapValue.put("name", s.getName());
                        mapValue.put("id", s.getValue());
                        listFunds.add(mapValue);
                    }
                    if(s.getValue().intValue()>10 &&s.getValue().intValue()<20) {
                        MapContext mapValue = MapContext.newOne();
                        mapValue.put("name", s.getName());
                        mapValue.put("id", s.getValue());
                        listFunds.add(mapValue);
                    }
                }
                MapContext typeMaps = MapContext.newOne();
                typeMaps.put("name", p.getName());
                typeMaps.put("id", p.getValue());
                typeMaps.put("funds", listFunds);
                listTypes.add(typeMaps);
        }
        map.put("types",listTypes);
        return ResultFactory.generateRequestResult(map);
    }

    @Override
    public RequestResult findCompanyFinanceList(MapContext mapContext, Integer pageNum, Integer pageSize) {
        PaginatedFilter paginatedFilter = new PaginatedFilter();
        Pagination pagination = new Pagination();
        pagination.setPageNum(pageNum);
        pagination.setPageSize(pageSize);
        paginatedFilter.setPagination(pagination);
        Map<String,String> created = new HashMap<String, String>();
        if(mapContext.get("order")!=null && mapContext.get("sort")!=null) {
            created.put(mapContext.get("order").toString(), mapContext.get("sort").toString());
        }else{
            created.put(WebConstant.KEY_ENTITY_CREATED,"desc");
        }
        List sort = new ArrayList();
        sort.add(created);
        paginatedFilter.setSorts(sort);
        paginatedFilter.setFilters(mapContext);
        return ResultFactory.generateRequestResult(this.paymentService.findCompanyFinanceList(paginatedFilter));
    }


    @Override
    public RequestResult getCompanyFinanceById(String paymentId) {
        CompanyFinanceInfoDto dto = this.paymentService.getCompanyFinanceInfoByPaymentId(paymentId);
        //将图片放入实体
        List<String> pathList;
        List<PaymentFiles> fileList=paymentFilesService.findByPaymentId(paymentId);
        if(fileList!=null && fileList.size()>0){
            pathList = new ArrayList<>();
            for(PaymentFiles file: fileList){
                pathList.add(file.getFullPath());
            }
            dto.setFileList(pathList);
        }
        return ResultFactory.generateRequestResult(this.paymentService.getCompanyFinanceInfoByPaymentId(paymentId));
    }

    //bug:代码需要优化，可以写专门接口来更新经销商账户，不用每次都遍历dealer_account
    @Override
    @Transactional(value = "transactionManager")
    public RequestResult addCompanyPayment(CompanyPaymentInfoDto companyPaymentInfoDto) {
        Payment payment = new Payment();
        payment.setWay(companyPaymentInfoDto.getWay());
        payment.setFunds(companyPaymentInfoDto.getFunds());
        payment.setType(companyPaymentInfoDto.getType());
        payment.setCreated(DateUtil.getSystemDate());
        payment.setCreator(WebUtils.getCurrUserId());
       // payment.setCreator("4gv9wx40aj9c");
        payment.setPayee("4j1u3r1efshq");//bug 需要替换为常量
        payment.setStatus(1);
        payment.setNotes(companyPaymentInfoDto.getNotes());
        payment.setCompanyId(companyPaymentInfoDto.getCompanyId());
        payment.setHolder(companyPaymentInfoDto.getHolder());
        payment.setAmount(companyPaymentInfoDto.getAmount());
        payment.setNo(AppBeanInjector.uniquneCodeGenerator.getNextNo(UniquneCodeGenerator.UniqueResource.PAYMENT_NO));
        paymentService.add(payment);
        List<DealerAccount> list =dealerAccountService.findByCompanIdAndNature(companyPaymentInfoDto.getCompanyId(),1);

        //更新账户信息
        //自由资金充值
        if(companyPaymentInfoDto.getType()==1) {
            if (payment.getFunds() == PaymentFunds.FREE_FUNDS.getValue()) {
                //给自由资金充值
                for (DealerAccount d : list) {
                    if (d.getType() == DealerAccountType.FREE_ACCOUNT.getValue()) {
                        MapContext map = MapContext.newOne();
                        map.put("id", d.getId());
                        map.put("balance", d.getBalance().add(companyPaymentInfoDto.getAmount()));
                        dealerAccountService.updateByMapContext(map);
                    }
                }
            }
            //保证金
            if (payment.getFunds() == PaymentFunds.BOND.getValue()) {
                //给押金充值
                for (DealerAccount d : list) {
                    if (d.getType() == DealerAccountType.DEPOSIT_ACCOUNT.getValue()) {
                        MapContext map = MapContext.newOne();
                        map.put("id", d.getId());
                        map.put("balance", d.getBalance().add(companyPaymentInfoDto.getAmount()));
                        dealerAccountService.updateByMapContext(map);
                    }
                }
            }
            //设计金
            if (payment.getFunds() == PaymentFunds.DESSIGN_FEE.getValue()) {
                //给设计账户充值
                for (DealerAccount d : list) {
                    if (d.getType() == DealerAccountType.DEPOSIT_ACCOUNT.getValue()) {
                        MapContext map = MapContext.newOne();
                        map.put("id", d.getId());
                        map.put("balance", d.getBalance().add(companyPaymentInfoDto.getAmount()));
                        dealerAccountService.updateByMapContext(map);
                    }
                }
            }
        }else if(companyPaymentInfoDto.getType()==2){
            //设计金支付
            if (payment.getFunds() == PaymentFunds.DESIGN_FEE_PAY.getValue()) {
                //从自由账户扣款
                for (DealerAccount d : list) {
                    if (d.getType() == DealerAccountType.FREE_ACCOUNT.getValue()) {
                        MapContext map = MapContext.newOne();
                        map.put("id", d.getId());
                        map.put("balance", d.getBalance().subtract(companyPaymentInfoDto.getAmount()));
                        dealerAccountService.updateByMapContext(map);
                    }
                }
            }
            //经销商账户退款
            if (payment.getFunds() == PaymentFunds.FREE_FUNDS_REFUND.getValue()) {
                //自由账户退款
                for (DealerAccount d : list) {
                    if (d.getType() == DealerAccountType.FREE_ACCOUNT.getValue()) {
                        MapContext map = MapContext.newOne();
                        map.put("id", d.getId());
                        map.put("balance", d.getBalance().subtract(companyPaymentInfoDto.getAmount()));
                        dealerAccountService.updateByMapContext(map);
                    }
                }
            }
            //经销商设计费退款
            if (payment.getFunds() == PaymentFunds.DESIGN_FEE_REFUND.getValue()) {
                //自由账户退款
                for (DealerAccount d : list) {
                    if (d.getType() == DealerAccountType.FREE_ACCOUNT.getValue()) {
                        MapContext map = MapContext.newOne();
                        map.put("id", d.getId());
                        map.put("balance", d.getBalance().subtract(companyPaymentInfoDto.getAmount()));
                        dealerAccountService.updateByMapContext(map);
                    }
                }
            }
            //保证金退款
            if (payment.getFunds() == PaymentFunds.BOND_REFUND.getValue()) {
                //自由账户退款
                for (DealerAccount d : list) {
                    if (d.getType() == DealerAccountType.DEPOSIT_ACCOUNT.getValue()) {
                        MapContext map = MapContext.newOne();
                        map.put("id", d.getId());
                        map.put("balance", d.getBalance().subtract(companyPaymentInfoDto.getAmount()));
                        dealerAccountService.updateByMapContext(map);
                    }
                }
            }
        }
        //更新图片信息为正常
        if(companyPaymentInfoDto.getFileIds()!=null && !companyPaymentInfoDto.getFileIds().equals("")){
            String[] ids = companyPaymentInfoDto.getFileIds().split(",");
            for(String file_id: ids){
                MapContext map = MapContext.newOne();
                map.put("id",file_id);
                map.put("payment",payment.getId());
                map.put("status",1);
                this.paymentFilesService.updateByMapContext(map);
            }
        }
        //更新红田（厂家）总账
        //设计费和货款加入到自由账户账户
        if(companyPaymentInfoDto.getFunds()==PaymentFunds.FREE_FUNDS.getValue() || companyPaymentInfoDto.getFunds()==PaymentFunds.DESSIGN_FEE.getValue()) {
            companyService.balancePlusForFactory(companyPaymentInfoDto.getAmount().doubleValue(), DealerAccountType.FREE_ACCOUNT.getValue());
        }
        //保证金加入到保证金账户
        if(companyPaymentInfoDto.getFunds()==PaymentFunds.BOND.getValue()){
            companyService.balancePlusForFactory(companyPaymentInfoDto.getAmount().doubleValue(), DealerAccountType.DEPOSIT_ACCOUNT.getValue());
        }
        MapContext mapparam = MapContext.newOne();
        mapparam.put("id",payment.getId());
        return  ResultFactory.generateRequestResult(findCompanyFinanceList(mapparam,1,1).getData());
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult uploadImage(String userId,String companyId,List<MultipartFile> multipartFileList) {
        //添加图片到本地
        List<MapContext> listUrls = new ArrayList<>();
        if(multipartFileList!=null && multipartFileList.size()>0){
            for(MultipartFile multipartFile:multipartFileList){
                UploadInfo uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, multipartFile, UploadResourceType.FCOMPANY_FINANCE, "file");
                //添加图片到数据库
                PaymentFiles paymentFiles = new PaymentFiles();
                paymentFiles.setCreated(DateUtil.getSystemDate());
                paymentFiles.setFullPath(WebUtils.getDomainUrl() + uploadInfo.getRelativePath());
                paymentFiles.setMime(uploadInfo.getFileMimeType().getRealType());
                paymentFiles.setName(uploadInfo.getFileName());
                paymentFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
                paymentFiles.setPath(uploadInfo.getRelativePath());
                paymentFiles.setStatus(0);
                paymentFiles.setCreator(userId);
               // paymentFiles.setCreator("4v14mj3ampdy");
                this.paymentFilesService.add(paymentFiles);
                MapContext map = MapContext.newOne();
                map.put("imgFullPath",paymentFiles.getFullPath());
                map.put("imgRelPath",uploadInfo.getRelativePath());
                map.put("fileId",paymentFiles.getId());
                listUrls.add(map);
            }
        }
        return ResultFactory.generateRequestResult(listUrls);
    }
}
