package com.aixianshengxian.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aixianshengxian.BaseActivity;
import com.aixianshengxian.R;
import com.aixianshengxian.activity.addOrder.ChooseClientActivity;
import com.aixianshengxian.adapters.FragmentAdapter;
import com.aixianshengxian.constant.UrlConstants;
import com.aixianshengxian.entity.ResponseBean;
import com.aixianshengxian.fragment.LoadMoreOrderFragment;
import com.aixianshengxian.util.JsonUtil;
import com.aixianshengxian.util.SessionUtils;
import com.aixianshengxian.view.BardgeView;
import com.xmzynt.storm.basic.BasicConstants;
import com.xmzynt.storm.basic.constants.SaleConstants;
import com.xmzynt.storm.util.GsonUtil;
import com.xmzynt.storm.util.query.QueryFilter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private final int PAGE_SIZE  = 10 ;
    private TabLayout tablayout;
    private ViewPager viewPager;
    private ImageView image_personal,image_scan;
    private ImageView iv_add_new,iv_narrow;
    private TextView tv_head_title;
    private List<Fragment>mFragmentList;
    private List<BardgeView> views;
    private boolean isLogin = false;
    private int initial = 0 ;//待审核
    private int delivered = 0;//待收货
    private int audited = 0;//待发货
    private int all = 0;//全部
    private String  orderJson = "";

    private int narrowCount = 0;//记录添加按钮是否显示
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sale);
        initFragements();
        initViews();
        initTab();
        initEvents();
        isLogin = SessionUtils.getInstance(getApplicationContext()).getLoginState();
        if(isLogin){
            getRedPoint(1);
        }else {
          initial = 0 ;//待审核
          delivered = 0;//待收货
            audited = 0;//待发货
            showHideRed();
            // showCustomToast("尚未登录",getActivity().getApplicationContext());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        isLogin = SessionUtils.getInstance(getApplicationContext()).getLoginState();
        if(isLogin){
            getRedPoint(1);
        }else {
            initial = 0 ;//待审核
            delivered = 0;//待收货
            audited = 0;//待发货
            showHideRed();
        }


    }

    @Override
    protected void initViews() {
        image_personal = (ImageView) findViewById(R.id.image_personal);
        //image_personal.setImageResource(R.mipmap.account);
        //image_scan = (ImageView) findViewById(R.id.iv_scan);
        //image_scan.setVisibility(View.VISIBLE);
        //image_scan.setImageResource(R.mipmap.scan);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText("订货单");

        iv_add_new = (ImageView) findViewById(R.id.image_add_new);
        iv_narrow = (ImageView) findViewById(R.id.iv_narrow);
        tablayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        FragmentAdapter adapter =
                new FragmentAdapter(getSupportFragmentManager(), mFragmentList);

        viewPager.setAdapter(adapter);

        tablayout.setupWithViewPager(viewPager);//关联

    }

    @Override
    protected void initEvents() {
        image_personal.setOnClickListener(this);
        iv_add_new.setOnClickListener(this);
        iv_narrow.setOnClickListener(this);
        //image_scan.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_personal:
                finish();
                break;
            case R.id.image_add_new:
                startActivity(ChooseClientActivity.class);
                break;
            case R.id.iv_narrow:
                narrowCount ++;
                if (narrowCount % 2 != 0) {
                    iv_add_new.setVisibility(View.GONE);
                } else {
                    iv_add_new.setVisibility(View.VISIBLE);
                }
                break;
            /*case R.id.iv_scan:
                break;*/
        }
    }
    private void initFragements() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new LoadMoreOrderFragment().newInstance("全部"));
        mFragmentList.add(new LoadMoreOrderFragment().newInstance("待审核"));
        mFragmentList.add(new LoadMoreOrderFragment().newInstance("待发货"));
        mFragmentList.add(new LoadMoreOrderFragment().newInstance("待收货"));
    }

    private void initTab(){//导航栏
         views = new ArrayList<>();
        BardgeView view1= new BardgeView(getApplicationContext(),"全部");
        view1.hideImageView();
        views.add(view1);
        BardgeView view2 = new BardgeView(getApplicationContext(),"待审核");
        view2.hideImageView();
        views.add(view2);
        BardgeView view3 = new BardgeView(getApplicationContext(),"待发货");
        view3.hideImageView();
        views.add(view3);
        BardgeView view4 = new BardgeView(getApplicationContext(),"待收货");
        view4.hideImageView();
        views.add(view4);

    }

    private void getRedPoint(int page){
        JSONObject params = JsonUtil.getInstance().buildParams(getApplicationContext());;
        JSONObject body = new JSONObject();
        QueryFilter filter = new QueryFilter();
        isLogin  = SessionUtils.getInstance(getApplicationContext()).getLoginState();
        if(!isLogin){
          showLongToast("请先登录");
            return;
        }else {

//            filter.setPage(page);
//            filter.setPageSize(PAGE_SIZE);
//            filter.addOrder(new QueryFilter.Order("createInfo.operateTime", OrderDir.desc));// 按下单时间降序排列
            try {
                body.put(SaleConstants.Field.QUERY_FILTER, GsonUtil.getGson().toJson(filter));
                params.put(BasicConstants.URL_BODY, body);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OkHttpUtils.postString().url(UrlConstants.URL_RED_POINT)
                    .addHeader("Cookie", "PHPSESSID=" + 123456)
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader("Content-Type", "application/json;chartset=utf-8")
                    .content(params.toString())
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            showShortToast("请求失败");
                        }
                        @Override
                        public void onResponse(String response, int id) {
                            // showShortToast("请求成功");
                            showLogDebug("main",response);
                            ResponseBean res = GsonUtil.getGson().fromJson(response,ResponseBean.class);
                            if(res.getErrorCode() == 0){
                                try {
                                    JSONObject data = new JSONObject(res.getData());
                                    if(data.has("initial")){
                                        initial = data.getInt("initial") ;//待审核
                                    }else {
                                        initial = 0;
                                        }
                                    if(data.has("delivered")){
                                        delivered = data.getInt("delivered");//待收货
                                    }else {
                                        delivered = 0 ;
                                    }
                                    if(data.has("audited")){
                                        audited = data.getInt("audited");//待发货
                                    }else {
                                        audited = 0 ;
                                    }
                                    all = initial+ delivered+ audited;

                                    //receipted = data.getInt("receipted");
                                    showHideRed();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //showLogDebug("main",res.getData());
                                //showShortToast(res.getData());

                            }else {
                                //showShortToast(res.getMessage());
                            }

                        }
                    });


        }

    }

    private void showHideRed(){
//        if(all >0){
//            views.get(0).showeImageView();
//            tablayout.getTabAt(0).setCustomView(views.get(0));
//        }else {
//            views.get(0).hideImageView();
//            tablayout.getTabAt(0).setCustomView(views.get(0));
//        }
        if(initial > 0){
            views.get(1).showeImageView();
            tablayout.getTabAt(1).setCustomView(views.get(1));
        }else {
            views.get(1).hideImageView();
            tablayout.getTabAt(1).setCustomView(views.get(1));
        }
        if(audited >0){
            views.get(2).showeImageView();
            tablayout.getTabAt(2).setCustomView(views.get(2));
        }else {
            views.get(2).hideImageView();
            tablayout.getTabAt(2).setCustomView(views.get(2));
        }
        if(delivered >0){
            views.get(3).showeImageView();
            tablayout.getTabAt(3).setCustomView(views.get(3));
        }else {
            views.get(3).hideImageView();
            tablayout.getTabAt(3).setCustomView(views.get(3));
        }

    }



}
