package com.cybermed.cdoc_patient.view;

import android.content.Context;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.cybermed.cdoc_patient.R;


/**
 * 继承自SwipeRefreshLayout,从而实现滑动到底部时上拉加载更多的功能.
 * <p/>
 * PS:针对有head的列表，需要重新设计上拉加载，目前只满足一般性列表
 */
public class RefreshLayout extends SwipeRefreshLayout implements OnScrollListener {

    /**
     * listview实例
     */
    private ListView mListView;

    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private OnLoadListener mOnLoadListener;
    /**
     * ListView的加载中footer
     */
    private View mListViewFooter;
    private View mListViewEndFooter;
    private View mListViewEndFooterCC;

    private Context context;

    /**
     * 加载中标志
     */
    private boolean isLoadingData;

    private boolean isLoadOver;

    private boolean removeFooter = false;

    /**
     * @param context
     */
    public RefreshLayout(Context context) {
        this(context, null);
        this.context = context;
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.common_refresh_footer, null, false);
        mListViewEndFooter = LayoutInflater.from(context).inflate(R.layout.common_end_footer, null, false);
        mListViewEndFooterCC = LayoutInflater.from(context).inflate(R.layout.common_end_footer_cc, null, false);

        setProgressBackgroundColor(R.color.blue_0_1);
        setColorSchemeResources(R.color.white_0_2);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getListView();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 初始化ListView对象
        if (mListView == null) {
            getListView();
        }
    }

    public void removeLoading(){mListView.removeFooterView(mListViewFooter);}

    public void removeEnd(){
        mListView.removeFooterView(mListViewEndFooter);
    }

    /**
     * 获取ListView对象
     */
    private void getListView() {
        int childs = getChildCount();
        if (childs > 0) {
            for (int i = 0; i < childs; i++) {
                View childView = getChildAt(i);
                if (childView instanceof ListView) {
                    mListView = (ListView) childView;
                    mListView.setOnScrollListener(this);
                    mListView.addFooterView(mListViewFooter);
                    break;
                }
            }

        }
    }

    public void loadDataError() {
        if (isRefreshing())
            setRefreshing(false);

        if (isLoadingData) {
            mListView.removeFooterView(mListViewFooter);
            mListView.removeFooterView(mListViewEndFooter);
            mListView.removeFooterView(mListViewEndFooterCC);

        }

        isLoadingData = false;
    }

    public void setRefreshing(boolean refreshing, boolean isLoadOver) {
        setRefreshing(refreshing);
        this.isLoadOver = isLoadOver;
        if (isLoadOver) {
            mListView.removeFooterView(mListViewFooter);
            mListView.removeFooterView(mListViewEndFooter);
            mListView.removeFooterView(mListViewEndFooterCC);

        }else {
            //mListView.removeFooterView(mListViewEndFooter);
            showBottonLoadView();
        }
    }

    public void setIsLoadMoreOver(boolean loadOver){
        if (loadOver){
            mListView.removeFooterView(mListViewFooter);
            mListView.removeFooterView(mListViewEndFooter);
            mListView.removeFooterView(mListViewEndFooterCC);

            isLoadingData = false;
        }
    }

    public boolean isLoadingData() {
        return isLoadingData;
    }

    public void setLoadingData(boolean loadingData) {
        isLoadingData = loadingData;
    }

    public boolean isLoadOver() {
        return isLoadOver;
    }

    public void setLoadOver(boolean loadOver) {
        isLoadOver = loadOver;
    }

    /**
     * 判断是否到了最底部
     */
    private boolean isBottom() {
        if (mListView != null && mListView.getAdapter() != null) {
            return mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
        }
        return false;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (isLoadOver)
            return;

        if (mOnLoadListener != null) {
            mOnLoadListener.onLoad();
            setRefreshing(false,false);
        }

        isLoadingData = true;
        mListView.removeFooterView(mListViewEndFooter);
        mListView.removeFooterView(mListViewEndFooterCC);

        showBottonLoadView();

    }

    //Could be deleted... not used
    public void removeFooterRefresh(boolean removeFooterBool){
        removeFooter = removeFooterBool;
    }

    public void loadComplete(boolean isLoadOver) {
        isLoadingData = false;
        this.isLoadOver = isLoadOver;
        if (isLoadOver) {
            if (mListView.getFooterViewsCount() == 0){
                if(!removeFooter) {
                    mListView.addFooterView(mListViewEndFooter);
                } else {
                    //mListView.addFooterView(mListViewEndFooterCC);
                }
            }
            mListView.removeFooterView(mListViewFooter);
        }else {
            showBottonLoadView();
        }
    }

    private void showBottonLoadView() {
        if (mListView.getFooterViewsCount() == 0)
            mListView.addFooterView(mListViewFooter);

    }


    /**
     * @param loadListener
     */
    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            //自动加载,异步加载数据的代码
            if (isBottom() && !isLoadingData && !isRefreshing()) {
                loadData();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    /**
     * 加载更多的监听器
     *
     * @author mrsimple
     */
    public interface OnLoadListener {
        void onLoad();
    }
}
