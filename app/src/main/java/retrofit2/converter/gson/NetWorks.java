package retrofit2.converter.gson;


import bean.AconntBean;
import bean.AnnouncementBean;
import bean.BankListBean;
import bean.BiaoBean;
import bean.BorrowDetailBean;
import bean.CaseFlowBean;
import bean.CenterIndexBean;
import bean.CertificationBean;
import bean.ChagerBean;
import bean.DiscountBean;
import bean.DiscountListBean;
import bean.InfoBean;
import bean.InterestBean;
import bean.IntroduceBean;
import bean.InvestmentBean;
import bean.LoginBean;
import bean.MyMessgeBean;
import bean.OneBean;
import bean.ProductDetialBean;
import bean.ShareBean;
import bean.TouziBean;
import bean.WithdrawBean;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by maxy on 2016/11/22.
 */
public class NetWorks extends RetrofitUtils {
    protected static final NetService service = getRetrofit().create(NetService.class);


    /**
     * 获取理财列表
     *
     * @param observer
     */
    public static void selectBorrowList(String page, String pagesize, Subscriber<BiaoBean> observer) {
        setSubscribe(service.selectBorrowList(page, pagesize), observer);
    }

    /**
     * 获取理财列表(新)
     *
     * @param observer
     */
    public static void selectBorrowListApp(String page, String pagesize,String type, Subscriber<BiaoBean> observer) {
        setSubscribe(service.selectBorrowListApp(page, pagesize,type), observer);
    }



    /**
     * 提现
     *
     * @param observer
     */
    public static void tongLianUserWithdraw(String withdrawAmount, String pwd, Subscriber<InfoBean> observer) {
        setSubscribe(service.tongLianUserWithdraw(withdrawAmount, pwd), observer);
    }



    /**
     * 获取理财列表
     *
     * @param observer
     */
    public static void userWithdraw(Subscriber<WithdrawBean> observer) {
        setSubscribe(service.userWithdraw(), observer);
    }



    /**
     * 获取新手标
     *
     * @param observer
     */
    public static void selectNoviceBorrowList(String page, String pagesize, Subscriber<BiaoBean> observer) {
        setSubscribe(service.selectNoviceBorrowList(page, pagesize), observer);
    }

    /**
     * 设置交易密码
     *
     * @param observer
     */
    public static void setUserPayPwd(String address, String readdress, Subscriber<InfoBean> observer) {
        setSubscribe(service.setUserPayPwd(address, readdress), observer);
    }

    /**
     * 投资
     *
     * @param observer
     */
    public static void investAjaxBorrow(String tradingPassword, String investAmount, String borrowId, Subscriber<String> observer) {
        setSubscribe(service.investAjaxBorrow(tradingPassword, investAmount, borrowId), observer);
    }

    /**
     * 2.账户总览
     *
     * @param observer
     */
    public static void selectUserNumber(Subscriber<AconntBean> observer) {
        setSubscribe(service.selectUserNumber(), observer);
    }






    /**
     * 插入观察者-泛型
     *
     * @param observable
     * @param observer
     * @param <T>
     */
    public static <T> void setSubscribe(Observable<T> observable, Subscriber<T> observer) {

        observable.subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


}
