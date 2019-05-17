package com.lwxf.industry4.webapp.facade.app.factory.company.impl;

import com.lwxf.commons.exception.ErrorCodes;
import com.lwxf.commons.uniquekey.IIdGenerator;
import com.lwxf.commons.utils.DateUtil;
import com.lwxf.commons.utils.ValidateUtils;
import com.lwxf.industry4.webapp.baseservice.cache.constant.RedisConstant;
import com.lwxf.industry4.webapp.bizservice.common.CityAreaService;
import com.lwxf.industry4.webapp.bizservice.common.UploadFilesService;
import com.lwxf.industry4.webapp.bizservice.company.*;
import com.lwxf.industry4.webapp.bizservice.dealer.DealerAccountService;
import com.lwxf.industry4.webapp.bizservice.financing.PaymentService;
import com.lwxf.industry4.webapp.bizservice.system.RolePermissionService;
import com.lwxf.industry4.webapp.bizservice.system.RoleService;
import com.lwxf.industry4.webapp.bizservice.user.UserBasisService;
import com.lwxf.industry4.webapp.bizservice.user.UserExtraService;
import com.lwxf.industry4.webapp.bizservice.user.UserService;
import com.lwxf.industry4.webapp.bizservice.user.UserThirdInfoService;
import com.lwxf.industry4.webapp.common.authcode.AuthCodeUtils;
import com.lwxf.industry4.webapp.common.component.UploadInfo;
import com.lwxf.industry4.webapp.common.component.UploadType;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.dto.UserInfoObj;
import com.lwxf.industry4.webapp.common.enums.UploadResourceType;
import com.lwxf.industry4.webapp.common.enums.company.*;
import com.lwxf.industry4.webapp.common.enums.user.*;
import com.lwxf.industry4.webapp.common.model.PaginatedFilter;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.uniquecode.UniquneCodeGenerator;
import com.lwxf.industry4.webapp.common.utils.UserExtraUtil;
import com.lwxf.industry4.webapp.common.utils.WebUtils;
import com.lwxf.industry4.webapp.common.utils.excel.POIUtil;
import com.lwxf.industry4.webapp.domain.dto.company.*;
import com.lwxf.industry4.webapp.domain.dto.financing.PaymentDtoForApp;
import com.lwxf.industry4.webapp.domain.entity.common.CityArea;
import com.lwxf.industry4.webapp.domain.entity.common.UploadFiles;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.company.DealerShop;
import com.lwxf.industry4.webapp.domain.entity.company.EmployeeInfo;
import com.lwxf.industry4.webapp.domain.entity.dealer.DealerAccount;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.system.Role;
import com.lwxf.industry4.webapp.domain.entity.system.RolePermission;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserBasis;
import com.lwxf.industry4.webapp.domain.entity.user.UserExtra;
import com.lwxf.industry4.webapp.domain.entity.user.UserThirdInfo;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.factory.company.CompanyFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.MapContext;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("companyFacadeFapp")
public class CompanyFacadeImpl extends BaseFacadeImpl implements CompanyFacade {

    @Resource(name = "roleService")
    private RoleService roleService;
    @Resource(name = "userExtraService")
    private UserExtraService userExtraService;
    @Resource(name = "userThirdInfoService")
    private UserThirdInfoService userThirdInfoService;
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "companyService")
    private CompanyService companyService;
    @Resource(name = "companyEmployeeService")
    private CompanyEmployeeService companyEmployeeService;
    @Resource(name = "userBasisService")
    private UserBasisService userBasisService;
    @Resource(name = "dealerShopService")
    private DealerShopService dealerShopService;
    @Resource(name = "employeeInfoService")
    private EmployeeInfoService employeeInfoService;
    @Resource(name = "rolePermissionService")
    private RolePermissionService rolePermissionService;
    @Resource(name = "dealerAccountService")
    private DealerAccountService dealerAccountService;
    @Resource(name = "employeePermissionService")
    private EmployeePermissionService employeePermissionService;
    @Resource(name = "uploadFilesService")
    private UploadFilesService uploadFilesService;
    @Resource(name = "paymentService")
    private PaymentService paymentService;
    @Resource(name = "cityAreaService")
    private CityAreaService cityAreaService;

    public RequestResult getCompanyStatistics(){
       return ResultFactory.generateRequestResult(companyService.findCompanyStatistics());
    }
    @Override
    public RequestResult findCompanyList(MapContext mapContext,Integer pageNum,Integer pageSize) {
        PaginatedFilter paginatedFilter = new PaginatedFilter();
        Integer topParam = mapContext.get("topParam")==null?0:Integer.parseInt( mapContext.get("topParam").toString());
        if(topParam!=0){
            mapContext = new MapContext();
            if(topParam==1){
                mapContext.put("beginTime",DateUtil.dateTimeToString(initDateByMonth(),"yyyy-MM-dd"));
                mapContext.put("endTime",DateUtil.dateTimeToString(new Date(),"yyyy-MM-dd"));
            }
            if(topParam==2){
                mapContext.put("status","1");
            }
            if(topParam==3){
                mapContext.put("status","2");
            }
            paginatedFilter.setFilters(mapContext);
        }else{
            paginatedFilter.setFilters(mapContext);
        }
        Pagination pagination = new Pagination();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        paginatedFilter.setPagination(pagination);
        Map<String,String> created = new HashMap<String, String>();
        created.put(mapContext.get("order")==null?"created":mapContext.get("order").toString(),mapContext.get("sort")==null?"desc":mapContext.get("sort").toString());
        List sort = new ArrayList();
        sort.add(created);
        paginatedFilter.setSorts(sort);
        return ResultFactory.generateRequestResult(this.companyService.selectByFilterForApp(paginatedFilter));
    }

    public Company checkCompanyNo(String no){
        return this.companyService.selectByNo(no);
    }

    @Override
    public RequestResult getCompaniesOrdersCountMonthly(MapContext mapContext) {
        //如果月份没数据则补0
        String[] companies = (String[])mapContext.get("companyIds");
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM");
        List<String> listDate;
        String beginTimeStr= "";
        String endTimeStr= "";
        List res = new ArrayList();
        beginTimeStr=sdf.format( (Date)mapContext.get("beginTime"));
        endTimeStr=sdf.format((Date)mapContext.get("endTime"));
        listDate = getMonthBetween(beginTimeStr,endTimeStr);
        if(companies.length>1){
            for (String companyId:companies){
                mapContext.put("id",companyId);
                List<MonthsOrdersDto> listMonthsOrders = this.companyService.findCompaniesOrdersCountMonthly(mapContext);
                List<Integer> listNew=null;
                MapContext companyMap = MapContext.newOne();
                companyMap.put("companyName",listMonthsOrders.get(0).getCompanyName());
                int count=0;
                listNew = new ArrayList<>(listDate.size());
                for (String date : listDate){
                    count=0;
                    for(MonthsOrdersDto obj : listMonthsOrders){
                        if(obj.getMonth().equals(date) && obj.getCompanyId().equals(companyId)){
                                listNew.add(obj.getOrderCount());
                                count++;
                        }
                    }
                    if(count==0){
                        listNew.add(0);
                    }
                }
                companyMap.put("orderCount",listNew);
                res.add(companyMap);
            }
        }else{
            mapContext.put("id",companies[0]);
            Company company = companyService.findById(companies[0]);
            List<MonthsOrdersDto> listMonthsOrders = this.companyService.findCompaniesOrdersCountMonthly(mapContext);
            MapContext companyMap = MapContext.newOne();
            companyMap.put("companyName",listMonthsOrders.get(0).getCompanyName());
            List<Integer> listNew=null;
            int count=0;
            listNew = new ArrayList<>(listDate.size());
            for (String date : listDate){
                count=0;
                for(MonthsOrdersDto obj : listMonthsOrders){
                    if(obj.getMonth().equals(date)){
                        listNew.add(obj.getOrderCount());
                        count++;
                    }
                }
                if(count==0){
                    listNew.add(0);
                }
            }
            companyMap.put("orderCount",listNew);
            res.add(companyMap);
        }

        return ResultFactory.generateRequestResult(res);
    }

    @Override
    public RequestResult findCompanyInfo(String companyId) {
        CompanyInfoDtoForApp companyDto = this.companyService.findCompanyByIdForApp(companyId);
        //将图片放入实体
        List<String> pathList;
        List<UploadFiles> fileList=uploadFilesService.findByResourceId(companyId);
        if(fileList!=null && fileList.size()>0){
            pathList = new ArrayList<>();
            for(UploadFiles file: fileList){
                pathList.add(file.getFullPath());
            }
            companyDto.setImgPathList(pathList);
        }
        if(companyDto==null)
            return null;
        return ResultFactory.generateRequestResult(companyDto);
    }

    /**
     * 获取两个日期之间所有的月份集合
     * @return：YYYY-MM
     */
    private static List<String> getMonthBetween(String minDate, String maxDate){
        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        try {
            min.setTime(sdf.parse(minDate));
            min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

            max.setTime(sdf.parse(maxDate));
            max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

            Calendar curr = min;
            while (curr.before(max)) {
                result.add(sdf.format(curr.getTime()));
                curr.add(Calendar.MONTH, 1);
            }
        }catch (ParseException ex){
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public RequestResult viewCompanyIndex() {
        MapContext mapIndexContent = MapContext.newOne();
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        //返回上方统计信息
        Map<String,String> map= companyService.findCompanyStatistics();
        mapIndexContent.put("companyInfo",map);
        //返回经销商类型
        List<MapContext> listTypes = new ArrayList<>();
        for (CompanyType s : CompanyType.values()) {
            if(s.getValue()==0)  //屏蔽生产商
                continue;
            MapContext mapCompanyType = MapContext.newOne();
            mapCompanyType.put("name",s.getName());
            mapCompanyType.put("typeId",s.getValue());
            listTypes.add(mapCompanyType);
        }
        mapIndexContent.put("types",listTypes);
        //返回经销商状态
        List<MapContext> listStatus = new ArrayList<>();
        for (CompanyStatus s : CompanyStatus.values()) {
            MapContext mapCompanyStatus = MapContext.newOne();
            mapCompanyStatus.put("name",s.getName());
            mapCompanyStatus.put("statusId",s.getValue());
            listStatus.add(mapCompanyStatus);
        }
        mapIndexContent.put("status",listStatus);
        //返回经销商级别
        List<MapContext> listGrade = new ArrayList<>();
        for (CompanyGrade s : CompanyGrade.values()) {
            MapContext mapCompanyGrade = MapContext.newOne();
            mapCompanyGrade.put("name",s.getName());
            mapCompanyGrade.put("gradeId",s.getValue());
            listGrade.add(mapCompanyGrade);
        }
        mapIndexContent.put("grade",listGrade);
        List<Map<String,Object>> listReport = companyService.companyTypesStatistics();
        List<MapContext> listTypesCount = new ArrayList<>();
        long count=0;
        MapContext maptypeCount;
        long countCompanys=0;  //经销商类型总数
        for (Map<String,Object> map1 : listReport) {
            countCompanys+=Long.parseLong(map1.get("countTypes").toString());
        }
        for (CompanyType s : CompanyType.values()) {
            if(s.getValue()==0)  //屏蔽生产商
                continue;
            maptypeCount = MapContext.newOne();
            count=0;
            for(Map<String,Object> maps : listReport){
                if(Integer.parseInt(maps.get("type").toString())==s.getValue()){
                    count = Long.parseLong(maps.get("countTypes").toString());
                }
            }
            maptypeCount.put("name",s.getName());
            maptypeCount.put("companyCount",count);
            String res = numberFormat.format((float) count / (float) countCompanys * 100);
            maptypeCount.put("percent",res+"%");
            listTypesCount.add(maptypeCount);
        }

        mapIndexContent.put("report",listTypesCount);
        //返回下方统计报表
//        //如果月份没数据则补0
//        String[] companies = null;
//        String beginTimeStr=getCurrentYearJan();
//        String endTimeStr=getCurrentDec();
//        MapContext mapContext= MapContext.newOne();
//        mapContext.put("beginTime",beginTimeStr);
//        mapContext.put("endTime",endTimeStr);
//        List<String> listDate = getMonthBetween(beginTimeStr,endTimeStr);
//        List<String> listTop5ComapanyIds = companyService.getTop5CustOrdersCompaniesId(mapContext);
//        if(listTop5ComapanyIds!=null && listTop5ComapanyIds.size()>0){
//            companies = listTop5ComapanyIds.toArray(new String[listTop5ComapanyIds.size()]);
//            //生成报表数据
//            List res = new ArrayList();
//            for (String companyId:companies){
//                mapContext.put("id",companyId);
//                List<MonthsOrdersDto> listMonthsOrders = this.companyService.findCompaniesOrdersCountMonthly(mapContext);
//                MapContext companyMap = MapContext.newOne();
//                companyMap.put("companyName",listMonthsOrders.get(0).getCompanyName());
//                List<Integer> listNew=null;
//                int count=0;
//                listNew = new ArrayList<>(listDate.size());
//                for (String date : listDate){
//                    count=0;
//                    for(MonthsOrdersDto obj : listMonthsOrders){
//                        if(obj.getMonth().equals(date) && obj.getCompanyId().equals(companyId)){
//                            listNew.add(obj.getOrderCount());
//                            count++;
//                        }
//                    }
//                    if(count==0){
//                        listNew.add(0);
//                    }
//                }
//                companyMap.put("orderCount",listNew);
//                res.add(companyMap);
//            }
//            mapIndexContent.put("report",res);
//        }
        return ResultFactory.generateRequestResult(mapIndexContent);
    }

    @Transactional(value = "transactionManager")
    public RequestResult addCompany(CompanyDtoForAppAdd company) {
        User user = new User();
        StringBuffer pwd = new StringBuffer();
//        if(company.getCityAreaId()==null){
//            //System.out.print("+++++++++++++:"+company.getProvince()+","+company.getCity()+","+company.getArea());
//            CityArea cityId =cityAreaService.selectDistrictByName(company.getProvince(),company.getCity(),company.getArea());
//            company.setCityAreaId(cityId.getId());
//        }
        //用户信息
        user.setMobile(company.getLeaderTel());
        user.setName(company.getLeaderName());
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
        RequestResult validate = user.validateFields();
        if (validate != null) {
            return validate;
        }

        //验证电话号码是否正确
        if (!ValidateUtils.isChinaPhoneNumber(user.getMobile())) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_INVALID_MOBILE_NO"));
            return ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_ERROR, user.getMobile());
        }
        //判断手机号是否已存在
        if(this.userService.findByMobile(user.getMobile())!=null){
            Map result = new HashMap<>();
            result.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
            return ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.VALIDATE_ERROR,result);
        }

        //判断公司是否存在店主
        if(this.companyEmployeeService.selectShopkeeperByCompanyIds(Arrays.asList(company.getId())).size()!=0){
            return ResultFactory.generateErrorResult(com.lwxf.industry4.webapp.common.exceptions.ErrorCodes.BIZ_SHOPKEEPERS_ALREADY_EXIST_10080,AppBeanInjector.i18nUtil.getMessage("BIZ_SHOPKEEPERS_ALREADY_EXIST_10080"));
        }
        //给经销商设定用户生日
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            user.setBirthday(simpleDateFormat.parse("1970-1-1 00:00:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.userService.add(user);
        company.setLeader(user.getId());
        this.companyService.add(company);
        //更新图片信息为正常
        if(company.getFileIds()!=null && !company.getFileIds().equals("")){
            String[] ids = company.getFileIds().split(",");
            for(String file_id: ids){
                MapContext map = MapContext.newOne();
                map.put("id",file_id);
                map.put("resourceId",company.getId());
                map.put("status",1);
                this.uploadFilesService.updateByMapContext(map);
            }
        }
        //用户扩展信息
        UserExtra userExtra = new UserExtra();
        userExtra.setUserId(user.getId());
        pwd.append(AuthCodeUtils.getRandomNumberCode(6));
        UserExtraUtil.saltingPassword(userExtra,new Md5Hash(pwd.toString()).toString());
        this.userExtraService.add(userExtra);

        // 第三方账号信息
        UserThirdInfo userThirdInfo = new UserThirdInfo();
        userThirdInfo.setWxNickname(user.getMobile());
        userThirdInfo.setWxIsBind(Boolean.FALSE);
        userThirdInfo.setWxIsSubscribe(Boolean.FALSE);
        userThirdInfo.setEmailIsBind(Boolean.FALSE);
        userThirdInfo.setMobileIsBind(Boolean.FALSE);
        userThirdInfo.setUserId(user.getId());
        userThirdInfo.setAppToken(UserExtraUtil.generateAppToken(userExtra,null));
        AppBeanInjector.redisUtils.hPut(RedisConstant.PLATFORM_TAG, user.getId(), Integer.valueOf(1));
        this.userThirdInfoService.add(userThirdInfo);

        //用户基本信息表
        UserBasis userBasis = new UserBasis();
        userBasis.setUserId(user.getId());
        userBasis.setPoliticalOutlook(UserPoliticalOutlookType.MASSES.getValue());
        userBasis.setEducation(EducationType.UNDERGRADUATE.getValue());
        userBasis.setIncome(IncomeType.FOUR.getValue());
        this.userBasisService.add(userBasis);

        //修改公司的状态为正常
        MapContext updateCompany = new MapContext();
        updateCompany.put(WebConstant.KEY_ENTITY_ID,company.getId());
        updateCompany.put(WebConstant.KEY_ENTITY_STATUS,CompanyStatus.NORMAL.getValue());
        updateCompany.put("leader",user.getId());
        this.companyService.updateByMapContext(updateCompany);

        //生成店铺数据
        DealerShop dealerShop = new DealerShop();
        dealerShop.setId(company.getId());
        dealerShop.setAddress(company.getAddress());
        dealerShop.setBusinessManager(company.getBusinessManager());
        dealerShop.setCityAreaId(company.getCityAreaId());
        dealerShop.setCompanyId(company.getId());
        dealerShop.setCreated(company.getCreated());
        dealerShop.setCreator(company.getCreator());
        dealerShop.setFollowers(company.getFollowers());
        dealerShop.setGrade(company.getGrade());
        dealerShop.setLat(company.getLat());
        dealerShop.setLeader(user.getId());
        dealerShop.setLeaderTel(company.getLeaderTel());
        dealerShop.setLng(company.getLng());
        dealerShop.setLogo(company.getLogo());
        dealerShop.setName(company.getName());
        dealerShop.setServiceStaff(company.getServiceStaff());
        dealerShop.setServiceTel(company.getServiceTel());
        dealerShop.setShopArea(company.getShopArea());
        dealerShop.setShopInfo(company.getCompanyInfo());
        dealerShop.setStatus(company.getStatus());
        dealerShop.setType(company.getType());
        this.dealerShopService.add(dealerShop);

        CompanyDto companyDto = new CompanyDto();
        companyDto.clone(company);
        companyDto.setCreatorName(this.userService.findById(companyDto.getCreator()).getName());

        //给该用户添加公司角色 店主
        CompanyEmployee companyEmployee = new CompanyEmployee();
        companyEmployee.setCompanyId(company.getId());
        companyEmployee.setUserId(user.getId());
        //店主编号未定
        Role role = this.roleService.selectByKey(DealerEmployeeRole.SHOPKEEPER.getValue());
        companyEmployee.setRoleId(role.getId());
        companyEmployee.setStatus(EmployeeStatus.NORMAL.getValue());
        companyEmployee.setCreated(DateUtil.getSystemDate());
        this.companyEmployeeService.add(companyEmployee);
        //把店主相关权限添加至 员工表中
        List<RolePermission> rolePermissionList= this.rolePermissionService.selectRolePermissionList(role.getId());
        if(rolePermissionList!=null&&rolePermissionList.size()!=0){
            IIdGenerator idGenerator = AppBeanInjector.idGererateFactory;
            for(RolePermission rolePermission:rolePermissionList){
                //重新生成主键ID
                rolePermission.setId(idGenerator.nextStringId());
                //用公司员工主键ID替换权限ID
                rolePermission.setRoleId(companyEmployee.getId());
            }
            this.employeePermissionService.addList(rolePermissionList);
        }

        //设置经销商的资金账户信息
        DealerAccount dealerAccount = new DealerAccount();
        dealerAccount.setBalance(new BigDecimal(0));
        dealerAccount.setCompanyId(company.getId());
        dealerAccount.setNature(1);
        dealerAccount.setStatus(1);
        for (int i =3;i>=0;i--){
            dealerAccount.setType(i);
            this.dealerAccountService.add(dealerAccount);
        }

        //新增员工时 设置 员工信息 初始数据
        EmployeeInfo employeeInfo = new EmployeeInfo();
        employeeInfo.setCompanyEmployeeId(companyEmployee.getId());
        employeeInfo.setUserId(user.getId());
        this.employeeInfoService.add(employeeInfo);
        return ResultFactory.generateRequestResult(UserInfoObj.newOne(user,userExtra,userThirdInfo,companyDto));
    }

    @Transactional(value = "transactionManager")
    public RequestResult updateCompany(MapContext map) {
            return ResultFactory.generateRequestResult(this.companyService.updateByMapContext(map));
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult updateStatus(MapContext map) {
        return ResultFactory.generateRequestResult(this.companyService.updateByMapContext(map));
    }

    public static String getCurrentYearJan() {
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        return year+"-01-01";
    }

    public static String getCurrentDec() {
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        return year+"-12-01";
    }

    /**
     * 返回经销商添加页面的用户信息（大区经理和负责人）
     * @return
     */
    public RequestResult getUsersForAdd(){
        MapContext mapCompanyAdd = MapContext.newOne();
        //返回经销商类型
        List<MapContext> listTypes = new ArrayList<>();
        for (CompanyType s : CompanyType.values()) {
            if(s.getValue()==0)  //屏蔽生产商
                continue;
            MapContext mapCompanyType = MapContext.newOne();
            mapCompanyType.put("name",s.getName());
            mapCompanyType.put("typeId",s.getValue());
            listTypes.add(mapCompanyType);
        }
        mapCompanyAdd.put("types",listTypes);
        //返回经销商级别
        List<MapContext> listGrade = new ArrayList<>();
        for (CompanyGrade s : CompanyGrade.values()) {
            MapContext mapCompanyGrade = MapContext.newOne();
            mapCompanyGrade.put("name",s.getName());
            mapCompanyGrade.put("gradeId",s.getValue());
            listGrade.add(mapCompanyGrade);
        }
        mapCompanyAdd.put("grade",listGrade);
        mapCompanyAdd.put("daqujingli",companyEmployeeService.getUserListByRoleKey("023"));  //bug
        return  ResultFactory.generateRequestResult(mapCompanyAdd);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult uploadImage(String userId,String companyId,List<MultipartFile> multipartFileList) {
        //添加图片到本地
        List<String> listUrls = new ArrayList<>();
        if(multipartFileList!=null && multipartFileList.size()>0){
            for(MultipartFile multipartFile:multipartFileList){
                UploadInfo uploadInfo = AppBeanInjector.baseFileUploadComponent.doUploadByModule(UploadType.FORMAL, multipartFile, UploadResourceType.FCOMPANY, "file");
                //添加图片到数据库
                UploadFiles uploadFiles = new UploadFiles();
                uploadFiles.setCreated(DateUtil.getSystemDate());
                uploadFiles.setFullPath(WebUtils.getDomainUrl() + uploadInfo.getRelativePath());
                uploadFiles.setMime(uploadInfo.getFileMimeType().getRealType());
                uploadFiles.setName(uploadInfo.getFileName());
                uploadFiles.setOriginalMime(uploadInfo.getFileMimeType().getOriginalType());
                uploadFiles.setPath(uploadInfo.getRelativePath());
                uploadFiles.setStatus(0);
                uploadFiles.setCompanyId(companyId);
                uploadFiles.setResourceType(10);
                //paymentSimpleFiles.setCreator(userId);
                uploadFiles.setCreator(userId);
                this.uploadFilesService.add(uploadFiles);
                listUrls.add(uploadFiles.getId());
            }
        }
        MapContext map = MapContext.newOne();
        map.put("ids",listUrls);
        return ResultFactory.generateRequestResult(map);
    }

    @Override
    public int checkUserMobile(String mobile) {
        //验证电话号码是否正确
        if (!ValidateUtils.isChinaPhoneNumber(mobile)) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put(WebConstant.KEY_ENTITY_MOBILE, AppBeanInjector.i18nUtil.getMessage("VALIDATE_INVALID_MOBILE_NO"));
            return 0;
        }
        //判断手机号是否已存在
        if(this.userService.findByMobile(mobile)!=null){
            Map result = new HashMap<>();
            result.put(WebConstant.KEY_ENTITY_MOBILE,AppBeanInjector.i18nUtil.getMessage("VALIDATE_NOT_ALLOWED_REPEAT"));
            return 0;
        }
        return 1;
    }

    @Override
    public RequestResult viewCompanyAccountInfo(String companyId) {
        MapContext map = MapContext.newOne();
        //返回经销商账号类型
        List<String> listAccontTypes = new ArrayList<>();
        for (DealerAccountType s : DealerAccountType.values()) {
            listAccontTypes.add(s.getName());
        }
        map.put("accountType",listAccontTypes);
        //查询经销商统计信息，顶部
        Map mapCompanyInfo =  companyService.getCompanyInfoForApp(companyId);
        if(mapCompanyInfo.get("companyCustCount")==null){
            mapCompanyInfo.put("companyCustCount","0");
        }
        if(mapCompanyInfo.get("companyOrderCount")==null){
            mapCompanyInfo.put("companyOrderCount","0");
        }
        if(mapCompanyInfo.get("balance")==null){
            mapCompanyInfo.put("balance","0");
        }
        if(mapCompanyInfo.get("sumBalance")==null){
            mapCompanyInfo.put("sumBalance","0");
        }

        map.put("companyInfo",mapCompanyInfo);
        //查询经销商账户信息，页面中部
        List<CompanyAccountDto> mapAccountInfo= companyService.getCompanyAccountInfoForApp(companyId);
        if(mapAccountInfo==null){
            mapAccountInfo = new ArrayList<>();
        }
        map.put("accountInfo",mapAccountInfo);
        MapContext mapFillter = MapContext.newOne();
        mapFillter.put("id",companyId);
        PaginatedFilter paginatedFilter = new PaginatedFilter();
        Pagination pagination = new Pagination();
        paginatedFilter.setFilters(mapFillter);
        pagination.setPageSize(10);
        pagination.setPageNum(1);
        paginatedFilter.setPagination(pagination);
        List list = this.paymentService.selectPaymentByCompanyIdForApp(paginatedFilter).getRows();
        Double sumAmount=0d;
        for(Object obj : list){
            PaymentDtoForApp payment = (PaymentDtoForApp)obj;
            sumAmount+=Double.parseDouble(payment.getAmount().toString());
        }
        map.put("sumAmount",sumAmount);
        map.put("pageCount",list!=null?list.size():0);
        map.put("paymentList",list);
        return ResultFactory.generateRequestResult(map);
    }

    @Override
    @Transactional(value = "transactionManager")
    public RequestResult importCompanies(MultipartFile file) {
        try {
            List<String[]> list = POIUtil.readExcel(file);
            for(String[] arr :list){
                CompanyDtoForAppAdd dto = new CompanyDtoForAppAdd();
                dto.setName(arr[0]);
               switch (arr[1]){
                   case "直营店":
                       dto.setType(CompanyType.MANUFACTURERS.getValue());
                       break;
                   case "店中店":
                       dto.setType(CompanyType.SHOP_IN_STORE.getValue());
                       break;
                   case "专卖店":
                       dto.setType(CompanyType.EXCLUSIVE_SHOP.getValue());
                       break;
                   case "加盟店":
                       dto.setType(CompanyType.FRANCHISED_STORE.getValue());
                       break;
                   case "散单":
                       dto.setType(CompanyType.LOOSE_ORDER.getValue());
                       break;
                   case "供应商":
                       dto.setType(CompanyType.SUPPLIER.getValue());
                       break;
               }
               dto.setType(CompanyType.LOOSE_ORDER.getValue());
               dto.setLeaderName(arr[2]);
               dto.setLeaderTel(arr[4]);
               dto.setFounderName(arr[2]);
               dto.setCityNameForIos(arr[7]);
               dto.setAddress(arr[8]);
               dto.setContractTime(DateUtil.stringToDate(arr[13],"yyyy-MM-dd"));
               dto.setBusinessManager(arr[15]);
               String citystr = arr[7]==null||arr[7].equals("")?arr[5]+","+arr[6]:arr[5]+","+arr[6]+","+arr[7];
               CityArea cityId =cityAreaService.selectByMergerName(citystr);
               dto.setCityAreaId(cityId.getId());
               dto.setCreated(DateUtil.getSystemDate());
               dto.setCreator("4i5gkmpeqi33");
                dto.setFollowers(0);
                dto.setStatus(7);
                dto.setFounderName(dto.getLeaderName());
                dto.setGrade(0);
               addCompany(dto);
            }
        }catch (IOException ex){

        }
        return null;
    }

    /**
     * 获得当月1号零时零分零秒
     * @return
     */
    public Date initDateByMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
}
