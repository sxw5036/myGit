package com.lwxf.industry4.webapp.facade.app.dealer.design;

import com.lwxf.industry4.webapp.common.model.Pagination;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.domain.entity.design.DesignScheme;
import com.lwxf.industry4.webapp.domain.entity.design.SchemeContent;
import com.lwxf.industry4.webapp.facade.base.BaseFacade;
import com.lwxf.mybatis.utils.MapContext;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 功能：
 *
 * @author：panchenxiao(Mister_pan@126.com)
 * @create：2018/12/1 14:50
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public interface HomeCaseFacade extends BaseFacade {

    RequestResult selectByCondition(Integer pageNum,Integer pageSize, MapContext filters,HttpServletRequest request);

    RequestResult findById(String id);

    RequestResult addSchemeAttention(String userId , String resourceId);
    RequestResult addShare(String id);

    RequestResult findDesignStyleAndDoorState();

    RequestResult updateSchemeStatus(String companyId,String schemeId,Integer status);

    RequestResult updateDesignScheme(String companyId,String schemeId,MapContext params);

    RequestResult addDesignScheme(DesignScheme designScheme);

    RequestResult addSchemeFiles(List<MultipartFile> files,String uid,Integer type,String schemeId);

    RequestResult deleteDesignScheme(String schemeId, String companyId);

    RequestResult findAllDesigner(String quotations, String sort, Pagination pagination, String companyId);

    RequestResult findDesignByDesigner(String designer,String companyId);

    RequestResult updateSchemeStatus(String companyId, Integer status, String[] schemeIds);

    RequestResult addSchemeContent(SchemeContent schemeContent,Integer type);
}
