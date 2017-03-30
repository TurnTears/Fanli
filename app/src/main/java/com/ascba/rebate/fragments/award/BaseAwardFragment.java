package com.ascba.rebate.fragments.award;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ascba.rebate.R;
import com.ascba.rebate.adapter.AwardAdapter;
import com.ascba.rebate.beans.FirstRec;
import com.ascba.rebate.fragments.base.Base2Fragment;
import com.ascba.rebate.view.SuperSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 奖励基类
 */
public class BaseAwardFragment extends Base2Fragment {


    private RecyclerView rv;
    private SuperSwipeRefreshLayout refreshLat;
    private List<FirstRec> data=new ArrayList<>();
    private AwardAdapter adapter;

    public BaseAwardFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_award, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv = ((RecyclerView) view.findViewById(R.id.award_list));
        refreshLat = ((SuperSwipeRefreshLayout) view.findViewById(R.id.refresh_layout));
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AwardAdapter(R.layout.rec_list_item,data);
        rv.setAdapter(adapter);
    }

    public RecyclerView getRv() {
        return rv;
    }

    public void setRv(RecyclerView rv) {
        this.rv = rv;
    }

    public SuperSwipeRefreshLayout getRefreshLat() {
        return refreshLat;
    }

    public void setRefreshLat(SuperSwipeRefreshLayout refreshLat) {
        this.refreshLat = refreshLat;
    }

    public List<FirstRec> getData() {
        return data;
    }

    public void setData(List<FirstRec> data) {
        this.data = data;
    }

    public AwardAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(AwardAdapter adapter) {
        this.adapter = adapter;
    }
}
