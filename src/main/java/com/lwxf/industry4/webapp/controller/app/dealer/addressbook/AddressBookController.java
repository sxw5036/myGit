package com.lwxf.industry4.webapp.controller.app.dealer.addressbook;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.controller.app.dealer.base.BaseDealerController;
import com.lwxf.industry4.webapp.facade.AppBeanInjector;
import com.lwxf.industry4.webapp.facade.app.dealer.addressbook.AddressBookFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2019/3/6 15:21
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RestController
@RequestMapping(value = "/app/b",produces = WebConstant.RESPONSE_CONTENT_TYPE_JSON_CHARTSET)
public class AddressBookController extends BaseDealerController {

    @Resource(name = "addressBookFacade")
    private AddressBookFacade addressBookFacade;

    /**
     * 根据条件查询公司下的员工的电话，共享人员的电话，以及业务人员自己单独的客户电话
     * @param request
     * @param type(如果是1表示查询员工电话，如果是2共享人员的电话，3自己的客户的电话)
     * @param status
     * (如果是查询员工0 - 正常状态；1 - 禁用（删除）；2 - 离职
     * 如果是查询共享人员0 - 待审批（申请状态）；1 - 审批未通过；2 - 已审批（可营业状态）；3 - 被禁用)
     * @param identity(0 顾问 1设计师 2安装工)
     * @return
     */
    @GetMapping(value = "/companies/{companyId}/{type}/TelBooks")
    public RequestResult findTelBookList(HttpServletRequest request,
                                         @PathVariable Integer type,
                                         @PathVariable String companyId,
                                         @RequestParam(required = false) Integer status,
                                         @RequestParam(required = false) Integer identity,
                                         @RequestParam(required = false) Integer pageNum,
                                         @RequestParam(required = false) Integer pageSize,
                                         @RequestParam(required = false) String condition){

        if (null==pageSize){
            pageSize = AppBeanInjector.configuration.getPageSizeLimit();
        }
        if(null == pageNum){
            pageNum = 1;
        }
        String uid = request.getHeader("X-UID");
        Pagination pagination = new Pagination();
        pagination.setPageNum(pageNum);
        pagination.setPageSize(pageSize);
        RequestResult telBookList = this.addressBookFacade.findTelBookList(companyId, status, type, identity, pagination, uid,condition);

        return telBookList;
    }


    /**
     * 添加通讯录 mapContext 中有type ，type=1 添加安装工 ，type=2 ，添加个人的联系人
     * @param mapContext
     * @param companyId
     * @param request
     * @return
     */
    @PostMapping(value = "/companies/{companyId}/TelBooks")
    public RequestResult addTelBook(@PathVariable String companyId,
                                    HttpServletRequest request,
                                    @RequestBody MapContext mapContext){
        RequestResult result = this.addressBookFacade.addTelBook(companyId,mapContext,request);
        return result;
    }



}
