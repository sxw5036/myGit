package com.lwxf.industry4.webapp.controller.app.factory.factoryorder;

import com.lwxf.commons.json.JsonMapper;
import com.lwxf.commons.utils.LwxfStringUtils;
import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.enums.order.OrderProductType;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.common.utils.excel.POIUtil;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.admin.factory.org.employee.DeptMemberFacade;
import com.lwxf.industry4.webapp.facade.app.factory.factoryorder.FactoryOrderFacade;
import com.lwxf.mybatis.utils.MapContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/3/28 11:04
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Api(value = "FactoryOrderController", tags = {"F端App:订单管理"})
@RestController
@RequestMapping(value = "/app/f", produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class FactoryOrderController {
    @Resource(name = "factoryOrderFacade")
    private FactoryOrderFacade factoryOrderFacade;
    @Resource(name = "deptMemberFacade")
    private DeptMemberFacade deptMemberFacade;

    /**
     * 查询当月、上月、当天的订单数量(无用了)
     *
     * @return
     */
    @GetMapping(value = "/orderNum")
    public String findFactoryOrder() {

        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult factoryOrder = this.factoryOrderFacade.findFactoryOrder();
        return mapper.toJson(factoryOrder);
    }

    /**
     * 查询订单的当月、上月、当天的订单列表(无用了)
     *
     * @return
     */
    @GetMapping(value = "/orders/time")
    public String findFactoryOrderByCreated(@RequestParam Integer type,
                                            @RequestParam(required = false) Integer pageSize,
                                            @RequestParam(required = false) Integer pageNum,
                                            @RequestParam(required = false) Integer status) {

        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        Pagination pagination = new Pagination();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.factoryOrderFacade.findFactoryOrderByCreated(pagination, type, status);
        return mapper.toJson(result);
    }


    /**
     * name//可以是经销商名称，客户的姓名、订单的编号(无用了)
     *
     * @param pageSize
     * @param pageNum
     * @param mapContext
     * @return
     */
    @PostMapping(value = "/orders/condition")
    public String findFactoryOrderByCondition(@RequestParam(required = false) Integer pageSize,
                                              @RequestParam(required = false) Integer pageNum,
                                              @RequestBody(required = false) MapContext mapContext) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        Pagination pagination = new Pagination();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        RequestResult result = this.factoryOrderFacade.findFactoryOrderByCondition(pagination, mapContext);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);
    }


    /**
     * 工厂端查询订单的详情(无用了)
     *
     * @return
     */
    @GetMapping("/orders/{orderId}")
    public String findOrderInfoByOrderId(@PathVariable String orderId) {

        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        RequestResult result = this.factoryOrderFacade.findOrderInfoByOrderId(orderId);
        return mapper.toJson(result);
    }


    /**
     * 根据订单的状态查询订单的列表(无用了)
     *
     * @param status
     * @return
     */
    @GetMapping("/orders/{status}/list")
    public String findOrderListByStatus(@PathVariable Integer status,
                                        @RequestParam(required = false) Integer pageSize,
                                        @RequestParam(required = false) Integer pageNum) {

        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        Pagination pagination = new Pagination();
        pagination.setPageNum(pageNum);
        pagination.setPageSize(pageSize);
        MapContext params = MapContext.newOne();

        switch (status) {
            case 0:
                params.put("status", "0");
                break;//经销商订单
            case 1:
                params.put("status", "1 or o.status = 2 or o.status = 3 or o.status = 4 or o.status = 5 or o.status = 6");
                break;//订单部处理
            case 2:
                params.put("status", "7 or o.status = 8 or o.status = 9 ");
                break;//报价单审核
            case 3:
                break;
            case 4:
                break;
        }
        RequestResult result = this.factoryOrderFacade.findOrderListByStatus(pagination, params);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);
    }


    /**
     * 订单的首页
     *
     * @return
     */
    @ApiOperation(value = "订单首页", notes = "")
    @GetMapping(value = "/orders/homepage")
    public String findOrderHomePage() {
        RequestResult result = this.factoryOrderFacade.findOrderHomePage();
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);

    }

    /**
     * 新建订单
     *
     * @return
     */
    @GetMapping(value = "/orders/create")
    public String createdOrder() {
        RequestResult result = this.factoryOrderFacade.findCreatedOrder();
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);
    }

    /**
     * 添加订单的信息
     *
     * @return
     */
    @PostMapping(value = "/orders/add")
    public RequestResult addOrderInfo() {
        MapContext params = MapContext.newOne();
        RequestResult result = this.factoryOrderFacade.addOrderInfo(params);
        return result;
    }


    /**
     * 根据经销商的名称和客户的名称查询所属公司的信息（list列表）
     *
     * @param name
     * @return
     */
    @GetMapping(value = "/companies/info/name")
    public String findCompnayByCNameAndBName(@RequestParam(required = false) String name,
                                             @RequestParam(required = false) String cityName) {
        RequestResult compnayByCNameAndBName = this.factoryOrderFacade.findCompnayByCNameAndBName(name, cityName);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(compnayByCNameAndBName);
    }

    @GetMapping(value = "/companies/{companyId}/customerlist")
    public String findCompanyCustomer(@PathVariable String companyId) {
        RequestResult findCompanyCustomer = this.factoryOrderFacade.findCompanyCustomer(companyId);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(findCompanyCustomer);
    }


    /**
     * 根据条件去查询订单的列表
     *
     * @param mapContext
     * @return
     */
    @ApiOperation(value = "根据条件去查询订单的列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "(不必传)每页显示的条数，默认显示50条", dataType = "Integer", paramType = ""),
            @ApiImplicitParam(name = "pageNum", value = "页码，默认显示第一页", dataType = "Integer", paramType = "")
    })
    @PostMapping(value = "/orders/conditions")
    public String findOrderListByCondition(@RequestBody(required = false) MapContext mapContext) {

        Integer pageNum = 1;
        Integer pageSize = AppBeanInjector.configuration.getPageSizeLimit();

        String countType = null;
        if (mapContext != null && mapContext.size() > 0) {
            String ps = (String) mapContext.get("pageSize");
            String pn = (String)mapContext.get("pageNum");
            if (LwxfStringUtils.isNotBlank(ps)){
                Integer pageSize1 = Integer.valueOf(ps) ;
                if (pageSize1 != null) {
                    pageSize = pageSize1;
                }
            }
            if (LwxfStringUtils.isNotBlank(pn)){
                Integer pageNum1 = Integer.valueOf(pn);
                if (pageNum1 != null) {
                    pageNum = pageNum1;
                }
            }

            String status = (String) mapContext.get("status");
            if (LwxfStringUtils.isNotBlank(status) && status.equals("有效订单")) {
                mapContext.put("valid", 0);
            }
            if (LwxfStringUtils.isNotBlank(status) && status.equals("无效订单")) {
                mapContext.put("unValid", 1);
            }
            String type = (String) mapContext.get("type");
            if (LwxfStringUtils.isNotBlank(type) && type.equals("自产")) {
                mapContext.put("isCoordination", 0);
            }
            if (LwxfStringUtils.isNotBlank(type) && type.equals("外协")) {
                mapContext.put("isCoordination", 1);
            }

            String productType = (String) mapContext.get("productType");
            if (LwxfStringUtils.isNotBlank(productType)) {
                switch (productType) {
                    case "橱柜（J）":
                        mapContext.put("pType", OrderProductType.CUPBOARD.getValue());
                        break;
                    case "衣柜（B）":
                        mapContext.put("pType", OrderProductType.WARDROBE.getValue());
                        break;
                    case "成品家具（J）":
                        mapContext.put("pType", OrderProductType.FINISHED_FURNITURE.getValue());
                        break;
                    case "电器（J）":
                        mapContext.put("pType", OrderProductType.ELECTRICAL_EQUIPMENT.getValue());
                        break;
                    case "样块（Y））":
                        mapContext.put("pType", OrderProductType.SAMPLE_PIECE.getValue());
                        break;
                    default:
                        mapContext.put("pType", OrderProductType.HARDWARE.getValue());
                        break;
                }
            }
            countType = (String) mapContext.get("countType");
            mapContext.remove(WebConstant.KEY_ENTITY_STATUS);
            mapContext.remove("type");
            mapContext.remove("productType");
            mapContext.remove("countType");
            mapContext.remove("pageSize");
            mapContext.remove("pageNum");
        }
        Pagination pagination = new Pagination();
        pagination.setPageSize(pageSize);
        pagination.setPageNum(pageNum);
        RequestResult result = this.factoryOrderFacade.findOrderListByCondition(pagination, mapContext, countType);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(result);
    }


    @GetMapping(value = "/orders/{orderId}/info")
    public String findOrderInfos(@PathVariable String orderId) {
        RequestResult orderInfos = this.factoryOrderFacade.findOrderInfos(orderId);
        JsonMapper mapper = JsonMapper.createAllToStringMapper();
        return mapper.toJson(orderInfos);
    }


    /**
     * 经销商excel导入
     *
     * @return
     */
    @ApiOperation(value = "订单信息excel导入")
    @PostMapping(value = "/orderexcel")
    public RequestResult importOrderByExcel(@RequestBody MultipartFile multipartFile, HttpServletRequest request) {
        return this.factoryOrderFacade.addOrderExcel(multipartFile);
    }

    @ApiOperation(value = "员工信息excel导入")
    @PostMapping(value = "/empexcel")
    public RequestResult addEmpInfoExcel(@RequestBody MultipartFile multipartFile) {
        try {
            List<String[]> list = POIUtil.readExcel(multipartFile);
            for (String[] arr : list) {
                try {
                    String name = arr[0];
                    String mobile = arr[1];
                    String sno = arr[2];
                    String role = arr[3];
                    String dept = arr[4];
                    String address = arr[5];
                    String sex = arr[6];
                    String no = arr[7];
                    MapContext mapContext = MapContext.newOne();
                    mapContext.put("name", name);
                    mapContext.put("roleId", role);
                    mapContext.put("mobile", mobile);
                    mapContext.put("no", no);
                    mapContext.put("sno", sno);
                    mapContext.put("address", address);
                    //mapContext.put("dept", dept);
                    mapContext.put("sex", sex);
                    StringBuffer pwd = new StringBuffer();
                    this.deptMemberFacade.addDeptMember(mapContext, dept, pwd);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //continue;
                }
            }
        } catch (IOException ex) {
            ResultFactory.generateErrorResult("0000000", "数据导入失败");
        }
        return ResultFactory.generateSuccessResult();


    }


}
