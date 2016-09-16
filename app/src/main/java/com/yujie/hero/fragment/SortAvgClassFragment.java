package com.yujie.hero.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yujie.hero.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class SortAvgClassFragment extends Fragment {
    public static final String TAG = SortAvgClassFragment.class.getSimpleName();
    @Bind(R.id.chart_top)
    LineChartView chartTop;
    @Bind(R.id.chart_bottom)
    ColumnChartView chartBottom;

    /** student's name array*/
    String[] nameArray;
    /** class's name array*/
    String[] classArray;
    /** LineChart data*/
    private LineChartData lineData;

    /** ColumnChart data*/
    private ColumnChartData columnData;

    public SortAvgClassFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sort_avg_class, container, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    /**
     * init Data
     */
    private void initData() {
        initClassData();
    }

    private void initClassData() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
