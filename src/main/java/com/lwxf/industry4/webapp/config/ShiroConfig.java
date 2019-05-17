package com.lwxf.industry4.webapp.config;

import javax.annotation.Resource;
import javax.servlet.Filter;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import com.lwxf.industry4.webapp.common.constant.WebConstant;
import com.lwxf.industry4.webapp.common.shiro.*;

/**
 * 功能：
 *
 * @author：renzhongshan(d3shan@126.com)
 * @create：2018-05-24 14:01:38
 * @version：2018 1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@Configuration
public class ShiroConfig {
	@Value("${shiro.rememberme.max.age:604800}")
	private Integer shiroRememberMeMaxAge;
	@Value("${shiro.session.redis.expire:10800}")
	private Integer shiroSessionRedisExpire;
	@Resource
	private com.lwxf.industry4.webapp.common.utils.Configuration configuration;

	@Bean
	public LwxfShiroRealm shiroRealm() {
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(LwxfShiroRealm.HASH_ALGORITHM);
		matcher.setHashIterations(LwxfShiroRealm.HASH_INTERATIONS);
		LwxfShiroRealm realm = new LwxfShiroRealm();
		WildcardPermissionResolver resolver = new WildcardPermissionResolver();
		realm.setPermissionResolver(resolver);
		realm.setCredentialsMatcher(matcher);
		return realm;
	}
//	@Bean(name = "sessionIdCookie")
//	public SimpleCookie getSessionIdCookie() {
//		SimpleCookie cookie = new SimpleCookie("sid");
////		cookie.setHttpOnly(true);
//		cookie.setMaxAge(-1);
////		cookie.setName("PHPSESSID");
//		return cookie;
//	}
	@Bean
	public Cookie simpleCookie(){
		SimpleCookie rememberMe = new SimpleCookie("rememberMe");
		rememberMe.setHttpOnly(true);
		rememberMe.setMaxAge(this.shiroRememberMeMaxAge);
		if(configuration.isOnProd())
			rememberMe.setSecure(true);
		return rememberMe;
	}

	@Bean
	public LwxfRememberMeManager rememberMeManager(){
		LwxfRememberMeManager rememberMeManager = new LwxfRememberMeManager();
		rememberMeManager.setCipherKey(org.apache.shiro.codec.Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
		rememberMeManager.setCookie(simpleCookie());
		return rememberMeManager;
	}

	@Bean
	public SessionManager sessionManager(){
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionIdCookieEnabled(true);
		sessionManager.setSessionIdUrlRewritingEnabled(false);
		//sessionManager.setSessionDAO(redisSessionDAO());
		return sessionManager;
	}

	@Bean
	public org.apache.shiro.mgt.SecurityManager securityManager(){
		DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
		securityManager.setRealm(this.shiroRealm());
		securityManager.setRememberMeManager(rememberMeManager());
		//securityManager.setSessionManager(sessionManager());  // TODO：暂时屏蔽sessionManger
		//securityManager.setCacheManager(this.cacheManager());
		return securityManager;
	}

	@Bean
	public ShiroFilterFactoryBean shiroFilterFactoryBean(org.apache.shiro.mgt.SecurityManager securityManager){
		ShiroFilterFactoryBean shiroFilterFactoryBean  = new ShiroFilterFactoryBean();
		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		//拦截器.
		Map<String,String> filterChainDefinitionMap = new LinkedHashMap<>();
		//配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
		filterChainDefinitionMap.put("/logout", "logout");
		filterChainDefinitionMap.put("/404", "anon");
		filterChainDefinitionMap.put("/flogin", "anon");

		// 匿名访问

		// 用户注册、激活、登录
		// 匿名url
		//TODO:数据库连接池监控

		filterChainDefinitionMap.put("/signup", "anon");
		filterChainDefinitionMap.put("/favicon.ico", "anon");
		filterChainDefinitionMap.put("/*.png", "anon");
		filterChainDefinitionMap.put("/*.jpg", "anon");
		filterChainDefinitionMap.put("/*.txt", "anon");
		filterChainDefinitionMap.put("/*.js", "anon");
		filterChainDefinitionMap.put("/*.css", "anon");
		filterChainDefinitionMap.put("/images/**", "anon");
		filterChainDefinitionMap.put("/sitemap.xml", "anon");
		filterChainDefinitionMap.put("/*.txt", "anon");
		filterChainDefinitionMap.put("/news/**", "anon");

		filterChainDefinitionMap.put("/js/**", "anon");
		filterChainDefinitionMap.put("/css/**", "anon");
		filterChainDefinitionMap.put("/img/**", "anon");
		filterChainDefinitionMap.put("/fonts/**", "anon");

		filterChainDefinitionMap.put("/auth/user", "anon");
		filterChainDefinitionMap.put("/auth/resource", "anon");
		filterChainDefinitionMap.put("/resetpass", "anon");
		filterChainDefinitionMap.put("/forgot", "anon");
		filterChainDefinitionMap.put("/resetpass/*", "anon");
		// 本地打包文件处理
		filterChainDefinitionMap.put("/*.js.map", "anon");

		// 匿名api
		// 公共api
		filterChainDefinitionMap.put("/app/commons/cities", "anon");
		filterChainDefinitionMap.put("/app/commons/updateVersions", "anon");//版本查询
		//swagger访问界面
		filterChainDefinitionMap.put("/swagger-ui.html", "anon");
		filterChainDefinitionMap.put("/webjars/**", "anon");
		filterChainDefinitionMap.put("/swagger-resources/**", "anon");
		filterChainDefinitionMap.put("/v2/**", "anon");
		filterChainDefinitionMap.put("/swagger-ui.html/**", "anon");

		filterChainDefinitionMap.put("/api/sys/touch", "anon");
		filterChainDefinitionMap.put("/api/site/touch", "anon");
		filterChainDefinitionMap.put("/api/users/login", "anon");
		filterChainDefinitionMap.put("/api/users/wxlogin", "anon");
		filterChainDefinitionMap.put("/api/users/register", "anon");
		filterChainDefinitionMap.put("/api/users/sharelogin", "anon");
		//用户忘记密码发的验证码
		filterChainDefinitionMap.put("/api/users/sendcode/forgot", "anon");
		filterChainDefinitionMap.put("/api/users/forgotcode/validate", "anon");
		filterChainDefinitionMap.put("/app/sms/register/phonenumbers/*", "anon");
		filterChainDefinitionMap.put("/app/sms/uppassword/phonenumbers/*", "anon");
		filterChainDefinitionMap.put("/app/sms/login/phonenumbers/*", "anon");
		filterChainDefinitionMap.put("/app/users/0/password", "anon");

		// 站点的匿名路径
		filterChainDefinitionMap.put("/authCode", "anon");
		filterChainDefinitionMap.put("/dev/**", "anon");
		filterChainDefinitionMap.put("/p/**", "anon");
		filterChainDefinitionMap.put("/resources/**", "anon");
		filterChainDefinitionMap.put("/error", "anon");
		filterChainDefinitionMap.put("/WEB-INF/**", "anon");
		filterChainDefinitionMap.put("/api/wx", "anon");
		filterChainDefinitionMap.put("/wx/wxlogin", "anon");
		filterChainDefinitionMap.put("/wx/auth", "anon");
		//微信授权回调分发请求
		filterChainDefinitionMap.put("/wx/login", "anon");
		filterChainDefinitionMap.put("/app/login", "anon");
		filterChainDefinitionMap.put("/privacy", "anon");
		filterChainDefinitionMap.put("/terms", "anon");
		filterChainDefinitionMap.put("/privacy/", "anon");
		filterChainDefinitionMap.put("/terms/", "anon");


		filterChainDefinitionMap.put("/mall", "user"); // 商城首页及其他页面
		filterChainDefinitionMap.put("/mall/#/**", "user"); // 商城首页及其他页面

		//<!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
		//<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
		filterChainDefinitionMap.put("/", "anon");
		filterChainDefinitionMap.put("/api/users/baseinfos", "anon");//获取用户的所有信息
		filterChainDefinitionMap.put("/common.js", "anon"); //测试api

		/**
		 * 预加载的api
		 */
		filterChainDefinitionMap.put("/api/preload/**", "anon"); //预加载

		//地址

		filterChainDefinitionMap.put("/app/commons/cities", "anon"); //查询所有区域地址


		//B端api路径
		//登录
		filterChainDefinitionMap.put("/app/sms/*/phonenumbers/*","anon");//获取短信验证码
		filterChainDefinitionMap.put("/app/users/login", "anon");//B端经销商和经销商员工登录
		filterChainDefinitionMap.put("/app/users/password/login", "anon");//密码登录
		filterChainDefinitionMap.put("/app/b/users/authCode", "anon");//验证验证码是否正确
		filterChainDefinitionMap.put("/app/b/users/forgetPassword", "anon");//忘记密码


		filterChainDefinitionMap.put("/app/b/companies/*/emp", "anon");//公司下的员工
		filterChainDefinitionMap.put("/app/b/companies/*/inservice/emp", "anon");//公司下的在职员工
		filterChainDefinitionMap.put("/app/b/companies/*/allcustomers", "anon");//公司下的客户信息

        //B端学习帮助
		filterChainDefinitionMap.put("/app/b/contenttypes", "anon");//分类列表
		filterChainDefinitionMap.put("/app/b/contenttypes/*/contents", "anon");//列表
		filterChainDefinitionMap.put("/app/b/contenttypes/*/contents/*", "anon");//详情
		//B端店铺
		filterChainDefinitionMap.put("/app/b/companies/*/dealerShops/*", "anon");//店铺信息展示及修改
		filterChainDefinitionMap.put("/app/b/companies/*/dealerShops/*/coverImage", "anon");//店铺封面图片替换
		filterChainDefinitionMap.put("/app/b/companies/*/dealerShops/*/showImages", "anon");//店铺展示图片添加和删除
		filterChainDefinitionMap.put("/app/b/companies/0/dealerShops", "anon");//店铺列表
		//B端客户管理
		filterChainDefinitionMap.put("/app/b/companies/*/customers", "anon");//客户列表查询，客户添加
		filterChainDefinitionMap.put("/app/b/companies/*/customers/*", "anon");//客户信息修改、删除，客户信息详情
		//B端员工管理
		filterChainDefinitionMap.put("/app/b/companies/*/employees", "anon");//员工列表查询，员工添加
		filterChainDefinitionMap.put("/app/b/companies/*/employees/*", "anon");//员工辞退、离职操作，员工修改，员工详情
		filterChainDefinitionMap.put("/app/b/companies/*/employees/*/*", "anon");//员工禁用和启用
		filterChainDefinitionMap.put("/app/b/companies/*/employees/*/roles/*", "anon");//设置角色
		filterChainDefinitionMap.put("/app/b/companies/*/roles/*", "anon");//角色列表
		//B端安装管理
		filterChainDefinitionMap.put("/app/b/companies/*/shareMembers", "anon");//安装工列表，安装工添加
		filterChainDefinitionMap.put("/app/b/companies/*/shareMembers/*", "anon");//安装工信息修改，删除
		filterChainDefinitionMap.put("/app/b/companies/*/dispatchLists", "anon");//安装单列表，安装单添加
		filterChainDefinitionMap.put("/app/b/companies/*/dispatchLists/*", "anon");//安装单详情
		filterChainDefinitionMap.put("/app/b/companies/*/dispatchLists/*/statuses", "anon");//安装单修改
		filterChainDefinitionMap.put("/app/b/companies/*/dispatchLists/*/addFiles", "anon");//上传施工图
		//B端售后信息管理
		filterChainDefinitionMap.put("/app/b/companies/*/aftersales/redis", "anon");//售后单预创建
		filterChainDefinitionMap.put("/app/b/companies/*/aftersales", "anon");//创建售后单，售后单列表
		filterChainDefinitionMap.put("/app/b/companies/*/aftersales/*", "anon");//售后单修改,售后单统计
		filterChainDefinitionMap.put("/app/b/companies/*/customOrders/*/aftersales/*", "anon");//售后单详情
		filterChainDefinitionMap.put("/app/b/companies/*/aftersales/*/addfiles", "anon");//售后图片上传
		//B端个人信息管理
		filterChainDefinitionMap.put("/app/b/users/0/**", "anon");
		filterChainDefinitionMap.put("/app/b/users/0/*", "anon");//注册及验证码验证
		//快享管理
		filterChainDefinitionMap.put("/app/b/quickshares", "anon");//快享列表
		filterChainDefinitionMap.put("/app/b/quickshares/*/quickshareComments", "anon");//快享评论添加
		filterChainDefinitionMap.put("/app/b/quickshares/*", "anon");//快享帖子添加、删除
		filterChainDefinitionMap.put("/app/b/quickshares/*/praises", "anon");//点赞/取消点赞
		filterChainDefinitionMap.put("/app/b/quickshares/*/quickshareComments", "anon");//评论列表
		filterChainDefinitionMap.put("/app/b/quickshares/*/quickshareComments/*", "anon");//快享评论删除
		filterChainDefinitionMap.put("/app/b/quickshares/*/files", "anon");//添加快享图片

		//首页
		filterChainDefinitionMap.put("/app/b/advertisings/homecarousel","anon");//获取首页轮播
		//首页案例
		filterChainDefinitionMap.put("/app/b/homecases","anon");//获取首页案例列表和检索
		filterChainDefinitionMap.put("/app/b/homecases/*","anon");//获取首页案例详情
		filterChainDefinitionMap.put("/app/b/companies/*/homecases/*","anon");//查询案例的上架的、下架的、草稿的、删除的案例列表
		filterChainDefinitionMap.put("/app/b/companies/*/homecases/*/*","anon");//修改案例的上架的、下架的、草稿的、删除的状态
		filterChainDefinitionMap.put("/app/b/users/*/homecases/*/attention","anon");//收藏/取消收藏设计案例
		filterChainDefinitionMap.put("/app/b/homecases/*/share","anon");//分享设计案例，分享数加1
		filterChainDefinitionMap.put("/app/b/designstyles","anon");//查询所有设计风格
		filterChainDefinitionMap.put("/app/b/companies/*/designers","anon");//查询设计师列表
		filterChainDefinitionMap.put("/app/b/companies/*/designers/*","anon");//查询设计师详情
		filterChainDefinitionMap.put("/app/b/companies/*/designScheme/add","anon");//添加案例
		filterChainDefinitionMap.put("/app/b/companies/*/designschemes/*/*","anon");//上传图片
		filterChainDefinitionMap.put("/app/b/companies/*/designScheme/*","anon");//编辑案例
		filterChainDefinitionMap.put("/app/b/companies/*/designScheme/*/schemeContents","anon");//编辑案例




		//订单需求
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/files","anon");//上传户型图
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/customdemands","anon");//添加设计需求
		filterChainDefinitionMap.put("/app/b/customdemands/choose","anon");//添加设计需求的需求选择
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/customdemands/*","anon");//修改设计需求、根据设计需求id查询单个设计需求
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/customdemands/*/files","anon");//查询设计需求的图片和详细信息

		//订单设计
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/customdesigns","anon");//查询订单设计列表
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/customdesigns/*","anon");//查询订单设计详情和图片
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/customdesigns/*","anon");//订单设计修改意见
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/customdesigns/*/*","anon");//修改订单设计状态

		//订单管理
		filterChainDefinitionMap.put("/app/b/companies/*/customorders","anon");//订单列表和列表检索
		filterChainDefinitionMap.put("/app/b/customorders/*","anon");//订单详情
		filterChainDefinitionMap.put("/app/b/companies/*/users/*/customorders/create","anon");//创建订单（无用）
		filterChainDefinitionMap.put("/app/b/companies/*/customorders","anon");//添加订单
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*","anon");//修改订单
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/*","anon");//修改订单状态
		filterChainDefinitionMap.put("/app/b/companies/*/orderQuotes","anon");//订单报价列表
		filterChainDefinitionMap.put("/app/b/companies/*/customeOrder/*/orderQuotes","anon");//修改及确认报价信息



		//支付
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/payments","anon");//添加订单支付/查询支付的列表
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/payments/*","anon");//订单支付详情
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/payments/*/files","anon");//添加订单支付文件
		filterChainDefinitionMap.put("/app/b/companies/*/payments/*/files","anon");//添加订单支付文件

		//生产
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/process","anon");//订单生产列表
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/process/*","anon");//订单生产视频
		//配送
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/dispatchs","anon");//订单配送列表
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/dispatchs/*","anon");//订单配送清单/确认收货并创建安装单

		//我的首页
		filterChainDefinitionMap.put("/app/b/users/*/mymessages","anon");//查看个人消息列表
		filterChainDefinitionMap.put("/app/b/users/*/mymessages/*","anon");//查看个人消息详情
		filterChainDefinitionMap.put("/app/b/users/*/mymessages/unread","anon");//未读的消息列表
		filterChainDefinitionMap.put("/app/b/users/*","anon");//个人信息展
		filterChainDefinitionMap.put("/app/b/companies/*/users/*","anon");//个人信息修改
		filterChainDefinitionMap.put("/app/b/companies/*/users/*/avatar","anon");//个人头像修改

		filterChainDefinitionMap.put("/app/f/userPermission/modules","anon");//app用户模块权限接口
		filterChainDefinitionMap.put("/app/f/userPermission/modules/*/menus","anon");//app用户菜单权限接口

		filterChainDefinitionMap.put("/app/b/users/*/isDisabled","anon");//每次打开app进入判断用户的禁用信息


		//财务

		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/payments/*","anon");//B端经销商审核C端的付款(确认收款)
		filterChainDefinitionMap.put("/app/b/companies/*/customorders/designFees","anon");//B端经销商查看未审核的设计需求

		filterChainDefinitionMap.put("/app/b/companies/*/factory/dealerAccounts","anon");//查询经销商的对厂账户资金信息
		filterChainDefinitionMap.put("/app/b/companies/*/mime/dealerAccounts","anon");//查询经销商的个人账户资金信息
		filterChainDefinitionMap.put("/app/b/companies/*/mime/dealerAccounts/details","anon");//查询经销商的个人账户资金信息明细
		filterChainDefinitionMap.put("/app/b/companies/*/mime/dealerAccounts/inform","anon");//查询经销商的个人账户的收入详情

		filterChainDefinitionMap.put("/app/b/companies/*/customorders/*/reminders","anon");//经销商催款


		filterChainDefinitionMap.put("/app/b/companies/*/dealerAccounts","anon");//添加支付
		filterChainDefinitionMap.put("/app/b/companies/*/addBank","anon");//添加银行账号信息

		filterChainDefinitionMap.put("/app/b/companies/*/dealerAccounts/*","anon");//根据账号类型查询账号明细列表
		filterChainDefinitionMap.put("/app/b/companies/*/dealerAccounts/*/dealerAccountDetails/*","anon");//账号明细详情


		filterChainDefinitionMap.put("/app/b/companies/*/accountLogs","anon");//财务日志（就是财务对厂的收支明细列表）
		filterChainDefinitionMap.put("/app/b/companies/*/accountLogs/*","anon");//财务日志（就是财务对厂的单个收支明细详情）
		filterChainDefinitionMap.put("/app/b/companies/*/factory/payments","anon");//转入转出记录列表


		//通讯录
		filterChainDefinitionMap.put("/app/b/companies/*/*/TelBooks","anon");//通讯录的列表
		filterChainDefinitionMap.put("/app/b/companies/*/TelBooks","anon");//添加通讯录


		//活动
		filterChainDefinitionMap.put("/app/b/companies/*/activities","anon");//添加活动、经销商查询活动列表、
		filterChainDefinitionMap.put("/app/b/companies/*/activities/*/activityJoins","anon");//终端参加活动
		filterChainDefinitionMap.put("/app/b/companies/*/activities/*/bjoin","anon");//终端参加活动
		filterChainDefinitionMap.put("/app/b/companies/*/activities/*","anon");//查询详情、编辑活动、删除果冻
		filterChainDefinitionMap.put("/app/b/companies/*/activities/*/activityParams/*","anon");//编辑活动参数
		filterChainDefinitionMap.put("/app/b/users/0/mobile/*","anon");//根据用的手机号查询用户是否存在
		filterChainDefinitionMap.put("/app/b/companies/*/activities/*/*","anon");//添加活动的封面和内容中的图片
		filterChainDefinitionMap.put("/app/b/companies/*/activities/*/*","anon");//修改活动的封面和内容中的图片
		filterChainDefinitionMap.put("/app/b/companies/*/activities/*/status/*","anon");//设置商家的活动的状态
		filterChainDefinitionMap.put("/app/b/companies/*/b/activities","anon");//查询B端的经销商的活动
		filterChainDefinitionMap.put("/app/b/companies/*/f/activities","anon");//查询f端的工厂的活动、经销商参加的活动

		//上样管理
		filterChainDefinitionMap.put("/app/b/companies/*/specimens","anon");//查看上样的订单、添加上样订单
		filterChainDefinitionMap.put("/app/b/companies/*/createspecimen","anon");//创建上样的订单（不保存到数据库）
		filterChainDefinitionMap.put("/app/b/orderStatus","anon");
		filterChainDefinitionMap.put("/app/b/companies/*/specimenOrders/*/specimenDemands","anon");//上样订单的需求
		filterChainDefinitionMap.put("/app/b/companies/*/specimenOrders/*/files","anon");//上样订单的需求的图片
		filterChainDefinitionMap.put("/app/b/companies/*/specimenOrders/*/specimenDemands/*","anon");//上样订单的需求的图片
		filterChainDefinitionMap.put("/app/b/companies/*/specimenOrders/*/designs","anon");//上样订单的设计列表
		filterChainDefinitionMap.put("/app/b/companies/*/specimenOrders/*/designs/*","anon");//上样订单的设计详情
		filterChainDefinitionMap.put("/app/b/companies/*/specimenOrders/*/designs/*/*","anon");//上样订单的设计详情
		filterChainDefinitionMap.put("/app/b/companies/*/specimenOrders/*/Process","anon");//上样订单的生产列表
		filterChainDefinitionMap.put("/app/b/companies/*/specimenOrders/*/dispatchs","anon");//上样订单的配送列表
		filterChainDefinitionMap.put("/app/b/companies/*/specimenOrders/*/Process/*","anon");//上样订单的生产详情
		filterChainDefinitionMap.put("/app/b/companies/*/specimenOrders/*/dispatchs/*","anon");//上样订单的配送详情

		filterChainDefinitionMap.put("/app/b/companies/*/specimenOrders/*/payments","anon");//上样订单的添加支付、支付的列表
		filterChainDefinitionMap.put("/app/b/companies/*/specimenOrders/*/payments/*","anon");//上样订单的支付详情
		filterChainDefinitionMap.put("/app/b/companies/*/specimenOrders/*/payments/*/files","anon");//上样订单的支付上传图片



		//工厂端app的路径定义
		filterChainDefinitionMap.put("/app/f/homepage","anon");				//工厂端app 首页

		//F端个人信息管理
		filterChainDefinitionMap.put("/app/f/companies/*/users/*","anon");				//工厂端app 查询个人信息，修改个人信息
		filterChainDefinitionMap.put("/app/f/companies/*/users/*/accountInfo","anon");	//工厂端app 查询账号信息
		filterChainDefinitionMap.put("/app/f/companies/*/users/*/avatar","anon");		//个人头像修改
		filterChainDefinitionMap.put("/app/f/companies/*/users/*/putpassword","anon");	//工厂端app 修改个人密码
		filterChainDefinitionMap.put("/app/f/companies/*/users/*","anon");				//工厂端app 查询个人信息
        //F端员工信息管理
		filterChainDefinitionMap.put("/app/f/companies/*/depts/0/employees","anon");//工厂端app 查询部门下员工数
		filterChainDefinitionMap.put("/app/f/companies/*/depts","anon");//查询工厂部门列表
		filterChainDefinitionMap.put("/app/f/companies/*/roles","anon");//查询工厂角色列表
		filterChainDefinitionMap.put("/app/f/companies/*/employees","anon");//查询工厂员工列表
		filterChainDefinitionMap.put("/app/f/companies/*/employees/*","anon");//查询员工详情
		//F端售后信息管理
		filterChainDefinitionMap.put("/app/f/companies/*/aftersaleApplies/count","anon");//查询售后单概览
		filterChainDefinitionMap.put("/app/f/companies/*/aftersaleApplies","anon");//查询售后单列表
		filterChainDefinitionMap.put("/app/f/companies/*/aftersaleApplies/*","anon");//售后单详情
		filterChainDefinitionMap.put("/app/f/companies/*/dealers","anon");//查询经销商列表
		filterChainDefinitionMap.put("/app/f/companies/*/dealers/*/customers","anon");//查询经销商客户列表
		filterChainDefinitionMap.put("/app/f/companies/*/aftersaleApplies/add","anon");//添加售后申请单
		filterChainDefinitionMap.put("/app/f/companies/*/aftersales/*/addfiles","anon");//售后单附件上传

		//F端售后后台管理
		filterChainDefinitionMap.put("/api/f/aftersales/v2/aftersaleApplies","anon");//查询售后单列表
		filterChainDefinitionMap.put("/api/f/aftersales/v2/aftersaleApplies/*","anon");//查询售后单详情
		filterChainDefinitionMap.put("/api/f/aftersales/v2/dealers","anon");//查询经销商列表
		filterChainDefinitionMap.put("/api/f/aftersales/v2/dealers/*/customers","anon");//查询经销商客户列表
		filterChainDefinitionMap.put("/api/f/aftersales/v2/add","anon");//添加售后申请单
		filterChainDefinitionMap.put("/api/f/aftersales/v2/handle","anon");//处理售后单
		filterChainDefinitionMap.put("/api/f/aftersales/v2/aftersaleApplies/*/status/*","anon");//更新售后单状态
		filterChainDefinitionMap.put("/api/f/aftersales/v2/aftersaleApplies/*/addfiles","anon");//售后单上传
		//C端客户管理
		filterChainDefinitionMap.put("/app/f/companies/*/customers/count","anon");//客户概览
		filterChainDefinitionMap.put("/app/f/companies/*/customers","anon");//客户列表
		filterChainDefinitionMap.put("/app/f/companies/*/dealers/*/customers/*","anon");//客户详情\客户修改
		filterChainDefinitionMap.put("/app/f/companies/*/dealers/*/customers/*/orderInfo","anon");//客户下的订单列表
		filterChainDefinitionMap.put("/app/f/companies/*/dealers/*/customers","anon");//客户添加



		//F端经销商列表
		filterChainDefinitionMap.put("/app/f/companies/*/dealers","anon");//经销商列表
		filterChainDefinitionMap.put("/app/f/companies/*/dealers/*","anon");//经销商基础信息
		filterChainDefinitionMap.put("/app/f/companies/*/dealers/*/employees","anon");//经销商下的员工列表

		//F端快享管理
		filterChainDefinitionMap.put("/app/f/quickshares", "anon");//快享列表
		filterChainDefinitionMap.put("/app/f/quickshares/*/quickshareComments", "anon");//快享评论添加
		filterChainDefinitionMap.put("/app/f/quickshares/*", "anon");//快享帖子添加、删除
		filterChainDefinitionMap.put("/app/f/quickshares/*/praises", "anon");//点赞/取消点赞
		filterChainDefinitionMap.put("/app/f/quickshares/*/quickshareComments/*", "anon");//快享评论删除
		filterChainDefinitionMap.put("/app/f/quickshares/*/files", "anon");//添加快享图片
        //F端发货管理
		filterChainDefinitionMap.put("/app/f/companies/*/dispatchBills/count", "anon");//发货概览
		filterChainDefinitionMap.put("/app/f/companies/*/dispatchBills", "anon");//发货列表
		filterChainDefinitionMap.put("/app/f/companies/*/dispatchBills/*/dispatchBillItems/*", "anon");//发货详情
		filterChainDefinitionMap.put("/app/f/companies/*/dispatchBills/baseInfo", "anon");//创建发货单时基础信息
		filterChainDefinitionMap.put("/app/f/companies/*/dispatchBills/add", "anon");//创建发货单
		filterChainDefinitionMap.put("/app/f/logistics", "anon");//物流公司
		filterChainDefinitionMap.put("/app/f/companies/*/dispatchBills/*/addfiles", "anon");//发货单附件
        //F端入库管理
		filterChainDefinitionMap.put("/app/f/companies/*/finishedStocks", "anon");//入库列表
		filterChainDefinitionMap.put("/app/f/companies/*/finishedStocks/*", "anon");//入库详情/添加
		filterChainDefinitionMap.put("/app/f/companies/*/finishedStocks/*/show","anon");//入库相关信息展示



		//工厂端APP经销商公司管理
		filterChainDefinitionMap.put("/app/f/companies","anon");//查询所有经销商公司信息
		filterChainDefinitionMap.put("/app/f/companies/*","anon");//查询某经销商公司信息
		filterChainDefinitionMap.put("/app/f/companies/*/ordersCountMonthly","anon");//按月统计经销商订单信息
		filterChainDefinitionMap.put("/app/f/companies/companyStatistics","anon");//统计经销商公司信息，供首页字段使用
		filterChainDefinitionMap.put("/app/f/companies/status","anon");//统计经销商公司信息，供首页字段使用
		filterChainDefinitionMap.put("/app/f/companies/*/status/*","anon");//更新经销商状态
		filterChainDefinitionMap.put("/app/f/companies/findCompaniesForApp","anon");//查询经销商列表post方式
		filterChainDefinitionMap.put("/app/f/companies/*/companyAccountInfo","anon");//F端APP经销商财务信息页面
		filterChainDefinitionMap.put("/app/f/companies/dataImport","anon");//F端APP经销商财务信息页面

		//工厂端订单管理
		filterChainDefinitionMap.put("/app/f/orderNum","anon");			//工厂端app 查询上月、当月、当天订单数量
		filterChainDefinitionMap.put("/app/f/orders/time","anon");		//工厂端app 查询上月、当月、当天订单列表
		filterChainDefinitionMap.put("/app/f/orders/*","anon");			//工厂端app 查询订单详情
		filterChainDefinitionMap.put("/app/f/orderStatus","anon");		//工厂端app 查询所有的订单状态
		filterChainDefinitionMap.put("/app/f/orders/*/list","anon");	//工厂端app 查询订单的列表根据订单的状态


		filterChainDefinitionMap.put("/app/f/orders/homepage","anon");	//工厂端app 查询订单的首页
		filterChainDefinitionMap.put("/app/f/orders/create","anon");	//工厂端app 新建订单
		filterChainDefinitionMap.put("/app/f/companies/info/name","anon");	//工厂端app 根据公司名称和地区查询公司的信息
		filterChainDefinitionMap.put("/app/f/companies/*/customerlist","anon");	//工厂端app 查询公司的顾客
		filterChainDefinitionMap.put("/app/f/orders/add","anon");		//工厂端app 添加订单信息
		filterChainDefinitionMap.put("/app/f/orders/condition","anon");	//工厂端app 根据条件查询订单列表
		filterChainDefinitionMap.put("/app/f/orders/*/info","anon");	//工厂端app 查询订单的详细信息


        filterChainDefinitionMap.put("/app/f/orders/process/follow","anon");			//工厂端app 查询生产跟进的首页
        filterChainDefinitionMap.put("/app/f/orders/process/execute","anon");			//工厂端app 查询生产执行的首页
        filterChainDefinitionMap.put("/app/f/orders/process/follow/list","anon");		//工厂端app 查询生产跟进的列表
        filterChainDefinitionMap.put("/app/f/orders/*/process/follow/info","anon");		//工厂端app 查询生产跟进的详情
        filterChainDefinitionMap.put("/app/f/orders/*/process/execute/info","anon");	//工厂端app 查询生产执行的详情
        filterChainDefinitionMap.put("/app/f/orders/*/process/execute","anon");			//工厂端app 确认完成
        filterChainDefinitionMap.put("/app/f/orders/*/packno","anon");			//工厂端app 生成包裹编号
        filterChainDefinitionMap.put("/app/f/orders/*/process/execute/pack","anon");	//工厂端app 确认完成包装
        filterChainDefinitionMap.put("/app/f/orders/*/process/*/execute/accessory","anon");	//工厂端app 确认完成的附件
        filterChainDefinitionMap.put("/app/f/orders/*/goworkshop","anon");				//工厂端app 下车间




		//F端app财务管理
		filterChainDefinitionMap.put("/app/f/finances/**","anon");//工厂端活动管理列表
		//日常账F端APP
		filterChainDefinitionMap.put("/app/f/payment_simples/**","anon");//简单记账管理
		filterChainDefinitionMap.put("/app/f/payment_simples","anon");//简单记账管理
		//日常账F端后台
		filterChainDefinitionMap.put("/api/f/payment_simples/**","anon");//简单记账管理
		filterChainDefinitionMap.put("/api/f/payment_simples","anon");//简单记账管理
		//F端经销商财务管理
		filterChainDefinitionMap.put("/app/f/companyFinances/**","anon");//简单记账管理
		filterChainDefinitionMap.put("/api/f/companyFinances/**","anon");//简单记账管理

		//信息管理
		filterChainDefinitionMap.put("/app/f/activities","anon");			//工厂端app 查询发布的活动列表
		filterChainDefinitionMap.put("/app/f/activities/*","anon");			//工厂端app 查询发布的活动详情
		filterChainDefinitionMap.put("/app/f/contents/notices","anon");		//工厂端app 查询公告的列表
		filterChainDefinitionMap.put("/app/f/contents/notices/*","anon");	//工厂端app 查询公告的详情
		filterChainDefinitionMap.put("/app/f/schemes","anon");				//工厂端app 查询案例的列表
		filterChainDefinitionMap.put("/app/f/schemes/*","anon");			//工厂端app 查询案例的详情

		//app财务审核
		filterChainDefinitionMap.put("/app/f/finances/index","anon");//app财务审核首页接口
		filterChainDefinitionMap.put("/app/f/finances/*/verifyOrderPrice","anon");//app财务审核订单信息
		filterChainDefinitionMap.put("/app/f/finances/*/verifyDesignPrice","anon");//app财务审核设计单信息
		filterChainDefinitionMap.put("/app/f/finances/uploadImages","anon");//app财务审核设计单信息


		filterChainDefinitionMap.put("/app/f/orders/undesigns","anon");			//工厂端app 设计待设计
		filterChainDefinitionMap.put("/app/f/orders/designeds","anon");			//工厂端app 设计已设计
		filterChainDefinitionMap.put("/app/f/orders/*/designs/*/info","anon");	//工厂端app 设计详情和设计添加页面
		filterChainDefinitionMap.put("/app/f/orders/*/designs","anon");			//工厂端app 添加设计信息
		filterChainDefinitionMap.put("/app/f/orders/*/designs/*/files","anon");			//工厂端app 添加设计图片








		filterChainDefinitionMap.put("/api/f/cityareas","anon");//工厂端 获取所有的地区信息
		filterChainDefinitionMap.put("/api/f/cities","anon");//工厂端
		filterChainDefinitionMap.put("/api/f/cities/*","anon");//工厂端

		filterChainDefinitionMap.put("/api/f/basecodes","anon");//工厂端 字典列表
		filterChainDefinitionMap.put("/api/f/basecodes/*","anon");//工厂端 字典 管理
		filterChainDefinitionMap.put("/api/f/basecodes/types/*","anon");//工厂端 字典 管理

		filterChainDefinitionMap.put("/api/sms/phonenumbers/*","anon");//工厂端 用户获取修改密码的验证码
		filterChainDefinitionMap.put("/api/f/users/login", "anon");//工厂端 用户登录
		filterChainDefinitionMap.put("/api/f/users/password", "anon");//工厂端 用户修改密码
		filterChainDefinitionMap.put("/api/f/users/info", "anon");//工厂端 查询用户信息
//		filterChainDefinitionMap.put("/api/f/productcategories/*","anon");//工厂端分类管理
//		filterChainDefinitionMap.put("/api/f/productcategories","anon");//工厂端分类管理
//		filterChainDefinitionMap.put("/api/f/productcategories/*/colors/*","anon");//工厂端分类下颜色管理
//		filterChainDefinitionMap.put("/api/f/productcategories/*/colors","anon");//工厂端分类下颜色管理
//		filterChainDefinitionMap.put("/api/f/productcategories/*/materials/*","anon");//工厂端分类下板材管理
//		filterChainDefinitionMap.put("/api/f/productcategories/*/materials","anon");//工厂端分类下板材管理
//		filterChainDefinitionMap.put("/api/f/productcategories/*/specs/*","anon");//工厂端分类下规格管理
//		filterChainDefinitionMap.put("/api/f/productcategories/*/specs","anon");//工厂端分类下规格管理
//		filterChainDefinitionMap.put("/api/f/products","anon");//工厂端产品管理
//		filterChainDefinitionMap.put("/api/f/products/*","anon");//工厂端产品管理
//		filterChainDefinitionMap.put("/api/f/products/*/files/*","anon");//工厂端产品管理
//		filterChainDefinitionMap.put("/api/f/products/*/info","anon");//工厂端产品管理
//		//filterChainDefinitionMap.put("/api/f/depts/members","anon");//工厂端部门员工管理
		//filterChainDefinitionMap.put("/api/f/depts/members/*","anon");//工厂端部门员工管理
//		filterChainDefinitionMap.put("/api/f/depts","user,factoryUrlFilter");//工厂端部门管理
//		filterChainDefinitionMap.put("/api/f/depts/**","user,factoryUrlFilter");//工厂端部门管理
//		filterChainDefinitionMap.put("/api/f/depts/*/members","anon");//工厂端部门员工管理
//		filterChainDefinitionMap.put("/api/f/depts/*/members/*","anon");//工厂端部门员工管理
//		filterChainDefinitionMap.put("/api/f/storages","anon");//工厂端仓库管理
//		filterChainDefinitionMap.put("/api/f/storages/*","anon");//工厂端仓库管理
//		filterChainDefinitionMap.put("/api/f/storage/*/stocks","anon");//工厂端库存管理
//		filterChainDefinitionMap.put("/api/f/storage/*/stocks/**","anon");//工厂端库存管理
//		filterChainDefinitionMap.put("/api/f/storage/finisheds","anon");//工厂端成品库管理
//		filterChainDefinitionMap.put("/api/f/storage/finisheds/**","anon");//工厂端成品库管理
//		filterChainDefinitionMap.put("/api/f/storage/outputins/*","anon");//工厂端出入单管理
//		filterChainDefinitionMap.put("/api/f/storage/outputins/**","anon");//工厂端出入单管理
//		filterChainDefinitionMap.put("/api/f/roles/*","anon");//工厂端角色管理
//		filterChainDefinitionMap.put("/api/f/roles/**","anon");//工厂端角色管理
//		filterChainDefinitionMap.put("/api/f/customorder/*","anon");//工厂端订单管理
//		filterChainDefinitionMap.put("/api/f/customorder/**","anon");//工厂端订单管理
//		filterChainDefinitionMap.put("/api/f/dispatchs/*","anon");//工厂端发货单
//		filterChainDefinitionMap.put("/api/f/dispatchs/**","anon");//工厂端发货单
//
//
//		filterChainDefinitionMap.put("/api/f/dealers","anon");//工厂端经销商管理
//		filterChainDefinitionMap.put("/api/f/dealers/**","anon");//工厂端经销商管理
//		filterChainDefinitionMap.put("/api/f/purchases/*","anon");//工厂端采购单管理
//		filterChainDefinitionMap.put("/api/f/purchases/**","anon");//工厂端采购单管理
		filterChainDefinitionMap.put("/api/f/company/logistics/*","anon");//工厂端物流公司管理
//		filterChainDefinitionMap.put("/api/f/suppliers/**","anon");//工厂端供应商管理
//		filterChainDefinitionMap.put("/api/f/suppliers/*","anon");//工厂端供应商管理
//		filterChainDefinitionMap.put("/api/f/menus/**","anon");//工厂端菜单管理
//		filterChainDefinitionMap.put("/api/f/menus/*","anon");//工厂端菜单管理
//		filterChainDefinitionMap.put("/api/f/operations/**","anon");//工厂端按钮管理
//		filterChainDefinitionMap.put("/api/f/operations/*","anon");//工厂端按钮管理
//		filterChainDefinitionMap.put("/api/f/aftersales/**","user,factoryUrlFilter");//工厂端售后单管理
//		filterChainDefinitionMap.put("/api/f/aftersales/*","user,factoryUrlFilter");//工厂端售后单管理
//		filterChainDefinitionMap.put("/api/f/finances/**","user,factoryUrlFilter");//工厂端财务管理
//		filterChainDefinitionMap.put("/api/f/finances/*","user,factoryUrlFilter");//工厂端财务管理
//		filterChainDefinitionMap.put("/api/f/designs/styles/**","anon");//工厂端设计风格管理
//		filterChainDefinitionMap.put("/api/f/designs/schemes/**","anon");//工厂端设计风格管理
//		filterChainDefinitionMap.put("/api/f/designs/doors/**","anon");//工厂端户型管理
//		filterChainDefinitionMap.put("/api/f/accounts","user,factoryUrlFilter");//工厂端审价管理
//		filterChainDefinitionMap.put("/api/f/accounts/**","user,factoryUrlFilter");//工厂端审价管理
		//工厂端活动管理
		filterChainDefinitionMap.put("/api/f/activity/**","anon");//工厂端活动管理列表
		//工厂端内容管理
		filterChainDefinitionMap.put("/api/f/contents/**","anon");
		// 工厂端内容分类管理
		filterChainDefinitionMap.put("/api/f/contentTypes/**","anon");

		filterChainDefinitionMap.put("/api/f/customers","anon");//工厂端查询顾客

		filterChainDefinitionMap.put("/api/f/versions/**","anon");//版本更新

		filterChainDefinitionMap.put("/api/f/sys/errors", "anon");
		// 厂家后台的api全局拦截
		filterChainDefinitionMap.put("/api/f/**", "user,factoryUrlFilter");
		// 经销商的APP端api链接

		//filterChainDefinitionMap.put("/app/b/**", "anon,dealerUrlFilter");

		// 造易接口调用：测试用 TODO：造易对接开始后将删除
		filterChainDefinitionMap.put("/api/zy/users","anon");

		//filterChainDefinitionMap.put("/app/b/**", "user,dealerUrlFilter");

		//filterChainDefinitionMap.put("/app/b/**", "anon");

		// 厂家的页面路径拦截
		filterChainDefinitionMap.put("/f*", "user");
		filterChainDefinitionMap.put("/f*/", "user");

		// 经销商的页面路径拦截

		//filterChainDefinitionMap.put("/b*", "user");
		//filterChainDefinitionMap.put("/b*/", "user");

		//filterChainDefinitionMap.put("/b*", "user,adminFilter");
		//filterChainDefinitionMap.put("/b*/", "user,adminFilter");

		 //filterChainDefinitionMap.put("/b*", "user");
		 //filterChainDefinitionMap.put("/b*/", "user");



		filterChainDefinitionMap.put("/**", "notFoundFilter");

		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl(WebConstant.REDIRECT_PATH_FACTORY_LOGIN);
		// 登录成功后要跳转的链接
		shiroFilterFactoryBean.setSuccessUrl("/");
		//未授权界面;
		shiroFilterFactoryBean.setUnauthorizedUrl("/404");
		/**
		 * add filters
		 */
		Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
		filterMap.put("logout",new LogoutFilter(){{
			this.setRedirectUrl("flogin");
		}});
		filterMap.put("factoryUrlFilter",new LwxfFactoryShiroUrlFilter()); // 厂家的url过滤器
		//filterMap.put("dealerUrlFilter",new LwxfDealerShiroUrlFilter()); // 经销商的的url过滤器
		// 工厂端userFilter处理
		LwxfUserFilter lwxfUserFilter = new LwxfUserFilter();
		lwxfUserFilter.setLoginUrl(WebConstant.REDIRECT_PATH_FACTORY_LOGIN);
		filterMap.put("user",lwxfUserFilter);
		filterMap.put("notFoundFilter", new NotFoundFilter());
		//filterMap.put("adminFilter",new LwxfAdminFilter());

		return shiroFilterFactoryBean;
	}

	/*@Bean
	public RedisCacheManager cacheManager(){
		RedisCacheManager redisCacheManager = new RedisCacheManager();
		return redisCacheManager;
	}

	@Bean
	public RedisSessionDAO redisSessionDAO(){
		return new RedisSessionDAO(this.shiroSessionRedisExpire);
	}*/

	/*@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
		return new LifecycleBeanPostProcessor();
	}*/
}
