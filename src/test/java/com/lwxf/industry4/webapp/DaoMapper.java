package com.lwxf.industry4.webapp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.lwxf.industry4.webapp.domain.entity.activity.ActivityInfo;
import com.lwxf.industry4.webapp.domain.entity.advertising.Advertising;
import com.lwxf.industry4.webapp.domain.entity.company.*;
import com.lwxf.industry4.webapp.domain.entity.customorder.*;
import com.lwxf.industry4.webapp.domain.entity.dealer.DealerAccount;
import com.lwxf.industry4.webapp.domain.entity.dealer.DealerAccountDetails;
import com.lwxf.industry4.webapp.domain.entity.dealer.DealerAccountLog;
import com.lwxf.industry4.webapp.domain.entity.design.DesignScheme;
import com.lwxf.industry4.webapp.domain.entity.design.DesignSchemeFiles;
import com.lwxf.industry4.webapp.domain.entity.design.DesignStyle;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBill;
import com.lwxf.industry4.webapp.domain.entity.dispatch.DispatchBillItem;
import com.lwxf.industry4.webapp.domain.entity.financing.Payment;
import com.lwxf.industry4.webapp.domain.entity.financing.PaymentFiles;
import com.lwxf.industry4.webapp.domain.entity.finished.FinishedProductSeries;
import com.lwxf.industry4.webapp.domain.entity.product.ProductDoor;
import com.lwxf.industry4.webapp.domain.entity.supplier.Material;
import com.lwxf.industry4.webapp.domain.entity.system.LogisticsCompany;
import com.lwxf.industry4.webapp.domain.entity.user.UserNotify;
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
import com.lwxf.industry4.webapp.domain.entity.common.UploadFiles;
import com.lwxf.industry4.webapp.domain.entity.company.Company;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyEmployee;
import com.lwxf.industry4.webapp.domain.entity.company.CompanyShareMember;
import com.lwxf.industry4.webapp.domain.entity.company.StoreConfig;
import com.lwxf.industry4.webapp.domain.entity.company.StoreHomeNav;
import com.lwxf.industry4.webapp.domain.entity.quickshare.Microblog;
import com.lwxf.industry4.webapp.domain.entity.quickshare.MicroblogComment;
import com.lwxf.industry4.webapp.domain.entity.quickshare.MicroblogPraise;
import com.lwxf.industry4.webapp.domain.entity.reservation.Reservation;
import com.lwxf.industry4.webapp.domain.entity.reservation.ReservationDesignFile;
import com.lwxf.industry4.webapp.domain.entity.reservation.ReservationDesignRecord;
import com.lwxf.industry4.webapp.domain.entity.reservation.ReservationPayedRecord;
import com.lwxf.industry4.webapp.domain.entity.system.SystemActivity;
import com.lwxf.industry4.webapp.domain.entity.warehouse.FinishedStock;
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
		String supplierPackageName  = "material";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("SunXianWei(17838625030@163.com)","2018 V1.0","老屋新房","com.lwxf.industry4");
		List<Class> entityList = Arrays.asList(Material.class);
		if (entityList.size() > 0) {
			for (Class c : entityList) {
				MySqlTool.generateDaoClassAndServiceClassAndXmlFiles(c, usePagedFilter, outputDir, supplierPackageName,params);
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
		String outputDir = "E:\\gencode\\industry4";
		//生成的包前缀
		String packageName = "company";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("dongshibo(F_baisi@163.com)","2018 V1.0","老屋新房","com.lwxf.app");
		List<Class> entityList = Arrays.asList(Company.class,CompanyEmployee.class,CompanyShareMember.class,StoreConfig.class,StoreHomeNav.class
		,EmployeeCertificate.class,EmployeeEducationExperience.class,EmployeeExperience.class,EmployeeInfo.class,EmployeeAssessment.class,
				OutsourcingFactory.class);
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
		String packageName = "advertising";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("panchenxiao(Mister_pan@126.com)","2018 V1.0","老屋新房","com.lwxf.industry4");
		List<Class> entityList = Arrays.asList(Advertising.class);
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
	/**
	 * 自动生成order相关的实体和mapper
	 * @throws IOException
	 */
	@Test
	public void test_batchGenerateOrderClassAndServiceClassAndDaoXmlFiles() throws IOException {
		// 生成的java和xml文件的输出目录（默认为桌面）
		String outputDir = "E:\\gencode\\industry4";
		//生成的包前缀
		String packageName = "customorder";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("F_baisi(F_baisi@126.com)","2019 V1.0","老屋新房","com.lwxf.industry4");
		List<Class> entityList = Arrays.asList(ProduceOrder.class,CustomOrder.class,OrderProduct.class,ProduceFlow.class);
		if (entityList.size() > 0) {
			for (Class c : entityList) {
				MySqlTool.generateDaoClassAndServiceClassAndXmlFiles(c, usePagedFilter, outputDir, packageName,params);
			}
		}
	}
	/**
	 * 自动生成warehouse相关的实体和mapper
	 * @throws IOException
	 */
	@Test
	public void test_batchGenerateWarehouseClassAndServiceClassAndDaoXmlFiles() throws IOException {
		// 生成的java和xml文件的输出目录（默认为桌面）
		String outputDir = "E:\\gencode\\industry4";
		//生成的包前缀
		String packageName = "warehouse";
		// 是否使用带分页的 selectByFilter
		boolean usePagedFilter = true;
		MySqlToolParams params = new MySqlToolParams("F_baisi(F_baisi@126.com)","2019 V1.0","老屋新房","com.lwxf.industry4");
		List<Class> entityList = Arrays.asList(FinishedStock.class);
		if (entityList.size() > 0) {
			for (Class c : entityList) {
				MySqlTool.generateDaoClassAndServiceClassAndXmlFiles(c, usePagedFilter, outputDir, packageName,params);
			}
		}
	}


}
