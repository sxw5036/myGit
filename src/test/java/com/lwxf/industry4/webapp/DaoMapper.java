package com.lwxf.industry4.webapp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.lwxf.industry4.webapp.domain.entity.activity.ActivityInfo;
import com.lwxf.industry4.webapp.domain.entity.company.*;
import com.lwxf.industry4.webapp.domain.entity.contentmng.Contents;
import com.lwxf.industry4.webapp.domain.entity.contentmng.ContentsContent;
import com.lwxf.industry4.webapp.domain.entity.contentmng.ContentsFiles;
import com.lwxf.industry4.webapp.domain.entity.contentmng.ContentsType;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBillPlan;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBillPlanItem;
import com.lwxf.industry4.webapp.domain.entity.product.ProductDoor;

import org.junit.Test;

import com.lwxf.industry4.webapp.domain.entity.common.UploadFiles;
import com.lwxf.industry4.webapp.domain.entity.quickshare.*;
import com.lwxf.industry4.webapp.domain.entity.reservation.Reservation;
import com.lwxf.industry4.webapp.domain.entity.reservation.ReservationDesignFile;
import com.lwxf.industry4.webapp.domain.entity.reservation.ReservationDesignRecord;
import com.lwxf.industry4.webapp.domain.entity.reservation.ReservationPayedRecord;
import com.lwxf.industry4.webapp.domain.entity.system.SystemActivity;
import com.lwxf.industry4.webapp.domain.entity.user.User;
import com.lwxf.industry4.webapp.domain.entity.user.UserAttention;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyShareMember;
import com.lwxf.industry4.webapp.domain.entity.company.StoreConfig;
import com.lwxf.industry4.webapp.domain.entity.company.StoreHomeNav;
import com.lwxf.industry4.webapp.domain.entity.version.UpdateVersion;
import com.lwxf.mybatis.tool.MySqlTool;
import com.lwxf.mybatis.tool.MySqlToolParams;


/**
 * 功能：根据数据库自动生成实体和mapper
 *
 * @author：renzhongshan(d3shan@126.com)
 * @create：2018-05-26 10:07:46
 * @version：2018 1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
public class DaoMapper {
    @Test
    public void test_createOrAlterDb() {
        MySqlTool.initDbObjects();
    }
	/**
	 * wangmingyuan
	 * 自动生成microblog及microblog相关的实体和mapper
	 * @throws IOException
	 */
	@Test
	public void test_batchGenerateMicroblogDaoClassAndServiceClassAndDaoXmlFiles() throws IOException {
		// 生成的java和xml文件的输出目录（默认为桌面）
		String outputDir = "E:\\gencode\\app";
		//生成的包前缀
		String packageName  = "version";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("SunXianWei(17838625030@163.com)","2018 V1.0","老屋新房","com.lwxf.industry4");
		List<Class> entityList = Arrays.asList(UpdateVersion.class);
		if (entityList.size() > 0) {
			for (Class c : entityList) {
				MySqlTool.generateDaoClassAndServiceClassAndXmlFiles(c, usePagedFilter, outputDir, packageName,params);
			}
		}

	}

	/**
	 * 自动生成User及User相关的实体和mapper
	 * @throws IOException
	 */
	@Test
	public void test_batchGenerateCommonDaoClassAndServiceClassAndDaoXmlFiles() throws IOException {
		// 生成的java和xml文件的输出目录（默认为桌面）
		String outputDir = "E:\\gencode\\app";
		//生成的包前缀
		String packageName  = "common";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("renzhongshan(d3shan@126.com)","2018 V1.0","老屋新房","com.lwxf.app");
		List<Class> entityList = Arrays.asList(UploadFiles.class);
		if (entityList.size() > 0) {
			for (Class c : entityList) {
				MySqlTool.generateDaoClassAndServiceClassAndXmlFiles(c, usePagedFilter, outputDir, packageName,params);
			}
		}
	}

	/**
	 * 自动生成Company及Company相关的实体和mapper
	 * @throws IOException
	 */
	@Test
	public void test_batchGeneratecompanyDaoClassAndServiceClassAndDaoXmlFiles() throws IOException {
		// 生成的java和xml文件的输出目录（默认为桌面）
		String outputDir = "E:\\gencode\\app";
		//生成的包前缀
		String packageName = "company";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("dongshibo(F_baisi@163.com)","2018 V1.0","老屋新房","com.lwxf.app");
		List<Class> entityList = Arrays.asList(Company.class,CompanyEmployee.class,CompanyShareMember.class,StoreConfig.class,StoreHomeNav.class);
		if (entityList.size() > 0) {
			for (Class c : entityList) {
				MySqlTool.generateDaoClassAndServiceClassAndXmlFiles(c, usePagedFilter, outputDir, packageName,params);
			}
		}
	}


	/**
	 * 自动生成系统活动日志及用户通知相关的实体和mapper
	 * @throws IOException
	 */
	@Test
	public void test_batchGenerateSysDaoClassAndServiceClassAndDaoXmlFiles() throws IOException {
		// 生成的java和xml文件的输出目录（默认为桌面）
		String outputDir = "E:\\gencode\\app";
		//生成的包前缀
		String packageName = "system";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("F_baisi(F_baisi@163.com)","2018 V1.0","老屋新房","com.lwxf.app");
		List<Class> entityList = Arrays.asList(SystemActivity.class);
		if (entityList.size() > 0) {
			for (Class c : entityList) {
				MySqlTool.generateDaoClassAndServiceClassAndXmlFiles(c, usePagedFilter, outputDir, packageName,params);
			}
		}
	}

	/**
	 * 自动生成Company及Company相关的实体和mapper
	 * @throws IOException
	 */
	@Test
	public void test_batchGeneratesystemHomeNavDaoClassAndServiceClassAndDaoXmlFiles() throws IOException {
		// 生成的java和xml文件的输出目录（默认为桌面）
		String outputDir = "E:\\gencode\\app";
		//生成的包前缀
		String packageName = "company";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("SunXianWei(17838625030@163.com)","2018 V1.0","老屋新房","com.lwxf.app");
		List<Class> entityList = Arrays.asList(Company.class);
		if (entityList.size() > 0) {
			for (Class c : entityList) {
				MySqlTool.generateDaoClassAndServiceClassAndXmlFiles(c, usePagedFilter, outputDir, packageName,params);
			}
		}
	}

	/**
	 * 自动生成Company及Company相关的实体和mapper
	 * @throws IOException
	 */
	@Test
	public void test_batchGenerateconfigDaoClassAndServiceClassAndDaoXmlFiles() throws IOException {
		// 生成的java和xml文件的输出目录（默认为桌面）
		String outputDir = "E:\\gencode\\app";
		//生成的包前缀
		String packageName = "config";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("zhangjiale(zjl869319827@outlook.com)","2018 V1.0","老屋新房","com.lwxf.app");
		List<Class> entityList = Arrays.asList(StoreConfig.class);
		if (entityList.size() > 0) {
			for (Class c : entityList) {
				MySqlTool.generateDaoClassAndServiceClassAndXmlFiles(c, usePagedFilter, outputDir, packageName,params);
			}
		}
	}
	/**
	 * 自动生成reservation及reservation相关的实体和mapper
	 * @throws IOException
	 */
	@Test
	public void test_batchGenerateReservationDaoClassAndServiceClassAndDaoXmlFiles() throws IOException {
		// 生成的java和xml文件的输出目录（默认为桌面）
		String outputDir = "E:\\gencode\\app";
		//生成的包前缀
		String packageName = "reservation";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("F_baisi(F_baisi@163.com)","2018 V1.0","老屋新房","com.lwxf.app");
		List<Class> entityList = Arrays.asList(Reservation.class,ReservationDesignFile.class,ReservationDesignRecord.class,ReservationPayedRecord.class);
		if (entityList.size() > 0) {
			for (Class c : entityList) {
				MySqlTool.generateDaoClassAndServiceClassAndXmlFiles(c, usePagedFilter, outputDir, packageName,params);
			}
		}
	}

	/**
	 * 自动生成user及user相关的实体和mapper
	 * @throws IOException
	 */
	@Test
	public void test_batchGenerateUserDaoClassAndServiceClassAndDaoXmlFiles() throws IOException {
		// 生成的java和xml文件的输出目录（默认为桌面）
		String outputDir = "E:\\gencode\\app";
		//生成的包前缀
		String packageName = "user";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("F_baisi(F_baisi@163.com)","2018 V1.0","老屋新房","com.lwxf.app");
		List<Class> entityList = Arrays.asList(User.class,UserAttention.class);
		if (entityList.size() > 0) {
			for (Class c : entityList) {
				MySqlTool.generateDaoClassAndServiceClassAndXmlFiles(c, usePagedFilter, outputDir, packageName,params);
			}
		}
	}

	/**
	 * 自动生成advertising相关的实体和mapper
	 * @throws IOException
	 */
	@Test
	public void test_batchGenerateadvertisingClassAndServiceClassAndDaoXmlFiles() throws IOException {
		// 生成的java和xml文件的输出目录（默认为桌面）
		String outputDir = "E:\\gencode\\app";
		//生成的包前缀
		String packageName = "contentmng";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("SunXianWei(17838625030@163.com)","2018 V1.0","老屋新房","com.lwxf.industry4");
		List<Class> entityList = Arrays.asList(Contents.class, ContentsContent.class, ContentsFiles.class, ContentsType.class);
		if (entityList.size() > 0) {
			for (Class c : entityList) {
				MySqlTool.generateDaoClassAndServiceClassAndXmlFiles(c, usePagedFilter, outputDir, packageName,params);
			}
		}
	}

	/**
	 * 自动生成advertising相关的实体和mapper
	 * @throws IOException
	 */
	@Test
	public void test_batchGenerateDesignClassAndServiceClassAndDaoXmlFiles() throws IOException {
		// 生成的java和xml文件的输出目录（默认为桌面）
		String outputDir = "E:\\gencode\\app";
		//生成的包前缀
		String packageName = "product";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("panchenxiao(Mister_pan@126.com)","2018 V1.0","老屋新房","com.lwxf.industry4");
		List<Class> entityList = Arrays.asList(ProductDoor.class);
		if (entityList.size() > 0) {
			for (Class c : entityList) {
				MySqlTool.generateDaoClassAndServiceClassAndXmlFiles(c, usePagedFilter, outputDir, packageName,params);
			}
		}
	}

	/**
	 * 自动生成activity相关的实体和mapper
	 * @throws IOException
	 */
	@Test
	public void test_batchGenerateActivityClassAndServiceClassAndDaoXmlFiles() throws IOException {
		// 生成的java和xml文件的输出目录（默认为桌面）
		String outputDir = "E:\\gencode\\app";
		//生成的包前缀
		String packageName = "activity";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("panchenxiao(Mister_pan@126.com)","2018 V1.0","老屋新房","com.lwxf.industry4");
		List<Class> entityList = Arrays.asList(ActivityInfo.class);
		if (entityList.size() > 0) {
			for (Class c : entityList) {
				MySqlTool.generateDaoClassAndServiceClassAndXmlFiles(c, usePagedFilter, outputDir, packageName,params);
			}
		}
	}


}
