package com.ascba.rebate.fragments.shop.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ascba.rebate.R;
import com.ascba.rebate.activities.DeliverDetailsActivity;
import com.ascba.rebate.adapter.order.DeliverOrderAdapter;
import com.ascba.rebate.adapter.order.EvaluateOrderAdapter;
import com.ascba.rebate.beans.Goods;
import com.ascba.rebate.beans.OrderBean;
import com.ascba.rebate.fragments.base.Base2Fragment;
import com.ascba.rebate.utils.TimeUtils;
import com.ascba.rebate.utils.UrlUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.yolanda.nohttp.rest.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李鹏 on 2017/03/14 0014.
 * 待评价
 */

public class EvaluateOrderFragment extends Base2Fragment {

    private RecyclerView recyclerView;
    private Context context;

    /**
     * 每笔订单中的商品列表
     */
    private List<OrderBean> beanArrayList = new ArrayList<>();
    private EvaluateOrderAdapter adapter;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        requstData();
    }

    /*
     获取数据
   */
    private void requstData() {
        Request<JSONObject> jsonRequest = buildNetRequest(UrlUtils.getOrderList, 0, true);
        jsonRequest.add("status", "wait_evaluate");
        executeNetWork(jsonRequest, "请稍后");
        setCallback(new Callback() {
            @Override
            public void handle200Data(JSONObject dataObj, String message) {
                initData(dataObj);
                if (adapter == null) {
                    initRecylerView();
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void handleReqFailed() {
            }

            @Override
            public void handle404(String message) {
                getDm().buildAlertDialog(message);
            }

            @Override
            public void handleReLogin() {
            }

            @Override
            public void handleNoNetWork() {
                getDm().buildAlertDialog("请检查网络！");
            }
        });
    }

    /*
    初始化数据
     */
    private void initData(JSONObject dataObj) {
        beanArrayList.clear();
        JSONArray jsonArray = dataObj.optJSONArray("order_list");
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                int totalNum = 0;//购买商品数量
                JSONObject object = jsonArray.optJSONObject(i);
                //头部信息
                String time = object.optString("add_time");//时间
                time = TimeUtils.milli2String((Long.parseLong(time) * 1000));
                OrderBean beanHead = new OrderBean(DeliverOrderAdapter.TYPE1, R.layout.item_order_head, time, "交易成功");
                beanArrayList.add(beanHead);

                //商品信息
                JSONArray goodsArray = object.optJSONArray("orderGoods");
                Log.d("DeliverOrderFragment", "goodsArray.length():" + goodsArray.length());
                if (goodsArray != null && goodsArray.length() > 0) {

                    for (int j = 0; j < goodsArray.length(); j++) {
                        try {
                            JSONObject goodsObject = goodsArray.getJSONObject(j);
                            Goods good = new Goods();
                            good.setTitleId(Integer.parseInt(goodsObject.optString("id")));//商品id
                            good.setImgUrl(UrlUtils.baseWebsite + goodsObject.optString("goods_img"));//图片
                            good.setGoodsTitle(goodsObject.optString("goods_name"));//商品名

                            int num = Integer.parseInt(String.valueOf(goodsObject.opt("goods_num")));
                            totalNum = num + totalNum;

                            good.setUserQuy(num);//购买数量
                            good.setGoodsPrice(goodsObject.optString("goods_pay_price"));//付款价格
                            good.setGoodsPriceOld(goodsObject.optString("goods_price"));//原价
                            beanArrayList.add(new OrderBean(DeliverOrderAdapter.TYPE2, R.layout.item_goods, good));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //底部信息
                String orderAmount = object.optString("order_amount");//订单总价
                String shippingFee = "(含" + object.optString("shipping_fee") + "元运费)";//运费
                String goodsNum = "共" + totalNum + "件商品";//商品数量
                OrderBean beadFoot = new OrderBean(DeliverOrderAdapter.TYPE3, R.layout.item_order_evaluate_foot, goodsNum, "￥" + orderAmount, shippingFee);
                beanArrayList.add(beadFoot);
            }
        }
    }

    private void initRecylerView() {

        recyclerView = (RecyclerView) view.findViewById(R.id.list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new EvaluateOrderAdapter(beanArrayList, context);
        recyclerView.setAdapter(adapter);

        View emptyView=LayoutInflater.from(context).inflate(R.layout.empty_order,null);
        adapter.setEmptyView(emptyView);

        recyclerView.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_goods_rl:
                        //点击商品查看订单详情
                        Intent intent = new Intent(context, DeliverDetailsActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }
}