package com.lwxf.industry4.webapp.facade.app.common.version.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lwxf.industry4.webapp.bizservice.version.UpdateVersionService;
import com.lwxf.industry4.webapp.common.result.RequestResult;
import com.lwxf.industry4.webapp.common.result.ResultFactory;
import com.lwxf.industry4.webapp.domain.entity.version.UpdateVersion;
import com.lwxf.industry4.webapp.facade.app.common.version.UpdateVersionFacade;
import com.lwxf.industry4.webapp.facade.base.BaseFacadeImpl;
import com.lwxf.mybatis.utils.DateUtil;
import com.lwxf.mybatis.utils.MapContext;

/**
 * 功能：
 *
 * @author：SunXianWei(17838625030@163.com)
 * @create：2019/5/6 0006 10:19
 * @version：2019 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Component("updateVersionFacade")
public class UpdateVersionFacadeImpl extends BaseFacadeImpl implements UpdateVersionFacade {
	@Resource(name = "updateVersionService")
	private UpdateVersionService updateVersionService;

	/**
	 * 查询版本信息
	 * @param sysType
	 * @param platform
	 * @return
	 */
	@Override
	public RequestResult findVersionNo(Integer sysType, Integer platform) {
		MapContext params=MapContext.newOne();
		params.put("sysType",sysType);
		params.put("platform",platform);
		MapContext result=MapContext.newOne();
		UpdateVersion updateVersion=this.updateVersionService.findVersionNo(params);
		result.put("updateVersion",updateVersion);
		return ResultFactory.generateRequestResult(result);
	}

	/**
	 * 修改版本信息
	 * @param mapContext
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult updateVersion(MapContext mapContext) {
		Integer sysType=mapContext.getTypedValue("sysType",Integer.class);
		Integer platform=mapContext.getTypedValue("platform",Integer.class);
		MapContext params=MapContext.newOne();
		params.put("sysType",sysType);
		params.put("platform",platform);
		UpdateVersion updateVersion=this.updateVersionService.findVersionNo(params);
		Integer versionId=updateVersion.getVersionId();
		mapContext.put("versionId",versionId);
		this.updateVersionService.updateByMapContext(mapContext);
		return ResultFactory.generateSuccessResult();
	}

	/**
	 * 添加版本信息
	 * @param mapContext
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManager")
	public RequestResult addVersion(MapContext mapContext) {
		UpdateVersion updateVersion=new UpdateVersion();
		updateVersion.setVersionNo(mapContext.getTypedValue("versionNo",String.class));//内部版本号
		updateVersion.setSysType(mapContext.getTypedValue("sysType",Integer.class));//系统类型
		updateVersion.setPlatform(mapContext.getTypedValue("platform",Integer.class));//平台
		updateVersion.setVersion(mapContext.getTypedValue("version",String.class));//显示版本号
		updateVersion.setName(mapContext.getTypedValue("name",String.class));//版本名称
		updateVersion.setUrl(mapContext.getTypedValue("url",String.class));//下载地址
		updateVersion.setDescription(mapContext.getTypedValue("description",String.class));//更新说明
		updateVersion.setFileSize(mapContext.getTypedValue("fileSize",Integer.class));//文件大小
		if(mapContext.containsKey("updateState")){
			updateVersion.setUpdateState(mapContext.getTypedValue("updateState",Integer.class));//是否有效，0：无效，1：有效，默认：0
		}else {
			updateVersion.setUpdateState(0);
		}
        updateVersion.setCreateUser(mapContext.getTypedValue("createUser",String.class));//创建人名称
		updateVersion.setCreateTime(DateUtil.now());//创建时间
		this.updateVersionService.add(updateVersion);
		return ResultFactory.generateSuccessResult();
	}
}
