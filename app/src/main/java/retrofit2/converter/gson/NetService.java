package retrofit2.converter.gson;



import bean.AconntBean;
import bean.BiaoBean;
import bean.BorrowDetailBean;
import bean.CaseFlowBean;
import bean.CertificationBean;
import bean.InfoBean;
import bean.LoginBean;
import bean.MyMessgeBean;
import bean.TouziBean;
import bean.WithdrawBean;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by maxy on 2016/11/22.
 */
public interface NetService {
    //服务器路径
    public static final String API_SERVER = "http://172.18.5.252:8080/jp/app/";//测试地址


//    public static final String API_SERVER = "http://www.enduo168.com/app/";  //上线地址
    //网址路径
//    public static final String API_SERVER_Url = "http://192.168.1.196:8080/jp/";


    public static final String API_SERVER_Url = "http://www.enduo168.com/";

    //主程序地址
//    public static final String API_SERVER_Main = API_SERVER_Url;
    //测试主程序地址
    public static final String API_SERVER_Main = "http://172.18.5.252:8080/jp/";
    //图片地址
    // public static final String API_SERVER_Photo = "http://192.168.1.196:8080/";


    @POST("selectUserNumber.html")
    Observable<AconntBean> selectUserNumber();

    //提现
    @POST("userWithdraw.html")
    Observable<WithdrawBean> userWithdraw();


    // 开启滑块
    @POST("startCaptcha.html")
    Observable<String> startCaptcha(@Query("noncestr") String noncestr);


    // 取短信验证码
    @POST("getPhoneCode.html")
    Observable<String> getPhoneCode(@Query("noncestr") String noncestr, @Query("cellPhone") String cellPhone);

    /**
     * 提现申请
     *
     * @return
     */
    @POST("tongLianUserWithdraw.html")
    Observable<InfoBean> tongLianUserWithdraw(@Query("withdrawAmount") String withdrawAmount, @Query("pwd") String pwd);

    @POST("selectBorrowList.html")
    Observable<BiaoBean> selectBorrowList(@Query("curPage") String curPage, @Query("pageSize") String pageSize);


    /**
     * 获取产品列表
     */
    @POST("selectBorrowListApp.html")
    Observable<BiaoBean> selectBorrowListApp(@Query("curPage") String curPage, @Query("pageSize") String pageSize, @Query("borrowType") String borrowType);

    /**
     * 新手标
     */
    @POST("selectNoviceBorrowList.html")
    Observable<BiaoBean> selectNoviceBorrowList(@Query("curPage") String curPage, @Query("pageSize") String pageSize);




    /**
     * 6.实名验证
     */
    @POST("regPerson.html")
    Observable<InfoBean> regPerson(@Query("realName") String realName, @Query("cardNo") String cardNo);

    /**
     * 修改登录密码
     */
    @POST("updateUserPass.html")
    Observable<InfoBean> updateUserPass(@Query("oldPass") String oldPass, @Query("newPass") String newPass);

    /**
     * 6.设置交易密码
     */
    @POST("setUserPayPwd.html")
    Observable<InfoBean> setUserPayPwd(@Query("address") String address, @Query("reAddress") String reAddress);


    /**
     * 找回密码 验证手机号码
     */
    @POST("forGetPassPhone.html")
    Observable<InfoBean> forGetPassPhone(@Query("param") String param);


    /**
     * 投标
     */
    @POST("bfpay/investAjaxBorrow.html")
    Observable<String> investAjaxBorrow(@Query("tradingPassword") String tradingPassword, @Query("investAmount") String investAmount, @Query("borrowId") String borrowId);


    /**
     * 6.实名验证
     */
    @POST("userPerson.html")
    Observable<CertificationBean> userPerson();

    /**
     * 产品详情
     */
    @POST("queryBorrowDetail.html")
    Observable<BorrowDetailBean> queryBorrowDetail(@Query("id") String id);


    /**
     * 4.验证推荐人是否正确
     */
    @POST("chackRefereeUser.html")
    Observable<String> chackRefereeUser(@Query("regReferee") String regReferee);

    /**
     * 4.注册
     */
    @POST("regist.html")
    Observable<InfoBean> regist(@Query("cellPhone") String cellPhone, @Query("pwd") String pwd, @Query("regCode") String regCode, @Query("regReferee") String regReferee);

    /**
     * 找回密码
     */
    @POST("updateforGetPass.html")
    Observable<InfoBean> updateforGetPass(@Query("phone") String phone, @Query("telCode") String telCode, @Query("forGetpassword") String forGetpassword);

    /**
     * 修改支付密码
     */
    @POST("updateUserPay.html")
    Observable<InfoBean> updateUserPay(@Query("phone") String phone, @Query("telCode") String telCode, @Query("newPass") String newPass);

    /**
     * /**
     * 1,登录
     */
    @POST("login.html")
    Observable<LoginBean> login(@Query("userName") String userName, @Query("pwd") String pwd);

    /**
     * 我的投资
     */
    @POST("selectInvestListing.html")
    Observable<TouziBean> selectInvestListing(@Query("curPage") String curPage, @Query("pageSize") String pageSize);

    /**
     * 我的消息
     */
    @POST("selectUserMessage.html")
    Observable<MyMessgeBean> selectUserMessage(@Query("curPage") String curPage, @Query("pageSize") String pageSize);

    /**
     * 我的消息--- 详情
     */
    @POST("showUserMessage.html")
    Observable<String> showUserMessage(@Query("messageId") String messageId);

    /**
     * 资金流水
     */
    @POST("moneyFlow.html")
    Observable<CaseFlowBean> moneyFlow(@Query("curPage") String curPage, @Query("pageSize") String pageSize);




}
