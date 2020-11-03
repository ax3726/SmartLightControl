package com.lm.common.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.lm.common.R;

import java.util.List;

/**
 * @author ：LiMing
 * @date ：2020-04-08
 * @desc ：通用适配器
 */
public abstract class BaseRecycleViewAdapter<T> extends BaseQuickAdapter<T, BaseCommonViewHolder> implements LoadMoreModule {
    private int emptyViewResource = EmptyViewConfig.emptyViewResource;
    private boolean isEmpty = true;
    private boolean isLoadMore = true;
    private String noMoreDataTip = "暂无更多内容";
    private String emptyDataTip = "";
    private TextView tvEmptyHint;
    private int emptyIconResource = 0;
    private boolean isAttachedToRecyclerView = false;
    /**
     * 是否自适应高度
     */
    private boolean isAdaptiveHeight = false;

    public BaseRecycleViewAdapter() {
        super(0);
    }

    public BaseRecycleViewAdapter(int layoutResId) {
        super(layoutResId);
    }

    public BaseRecycleViewAdapter(int layoutResId, boolean isLoadMore, boolean isEmpty) {
        super(layoutResId);
        this.isEmpty = isEmpty;
        this.isLoadMore = isLoadMore;
    }


    @Override
    public void onBindViewHolder(BaseCommonViewHolder holder, int position) {
        holder.countPosition(this);
        //找到LoadMore
       /* if (position == getLoadMoreModule().getLoadMoreViewPosition()) {
            ViewGroup endView = holder.getView(R.id.load_more_load_end_view);
            View childAt = endView.getChildAt(0);
            if (childAt != null && childAt instanceof TextView) {
                ((TextView) childAt).setText(noMoreDataTip);
            }
        }*/
        super.onBindViewHolder(holder, position);

    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        isAttachedToRecyclerView = true;
        setEmptyAndFooter();
    }


    public void setEmptyType(EmptyType emptyType) {
        switch (emptyType) {
            case normal:
                setEmptyImageAndText(R.drawable.view_no_data_icon, "未找到相关内容");
                break;
            case search:
                setEmptyImageAndText(R.drawable.view_no_data_icon, "暂无搜索结果");
                break;
            case message:
                setEmptyImageAndText(R.drawable.view_no_data_icon, "暂无消息");
                break;
            default:
                setEmptyImageAndText(R.drawable.view_no_data_icon, "未找到相关内容");
                break;
        }
    }

    public void setEmptyImageAndText(int imgId, String emptyDataTip) {
        this.emptyIconResource = imgId;
        this.emptyDataTip = emptyDataTip;
    }

    protected void setEmptyAndFooter() {
        if (isEmpty) {
            View emptyView = View.inflate(getContext(), emptyViewResource, null);
            if (!TextUtils.isEmpty(emptyDataTip)) {
                tvEmptyHint = emptyView.findViewById(R.id.tv_empty_hint);
                tvEmptyHint.setText(emptyDataTip);
            }
            if (emptyIconResource != 0) {
                ((ImageView) emptyView.findViewById(R.id.image_empty)).setImageResource(emptyIconResource);
            }
            if (isAdaptiveHeight) {
                emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            setEmptyView(emptyView);
        }
        setLoadMore();
    }

    private void setLoadMore() {
        if (isLoadMore) {
            View noMoreDataView = View.inflate(getContext(), R.layout.item_no_more_data, null);
            TextView tvNoMoreTip = noMoreDataView.findViewById(R.id.tv_no_more_tip);
            tvNoMoreTip.setText(noMoreDataTip);
            setFooterView(noMoreDataView);

           /* getLoadMoreModule().setOnLoadMoreListener(() -> {

            });*/
        }else {
            removeAllFooterView();
        }
    }

    public void bindRecyclerView(RecyclerView recyclerView) {
        bindRecyclerView(recyclerView, new LinearLayoutManager(recyclerView.getContext()));
    }

    public void bindRecyclerView(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager) {
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(this);
    }

    protected boolean isLastItem(int position) {
        return position == getData().size() - 1;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public void setLoadMore(boolean loadMore) {
        isLoadMore = loadMore;
        if (isAttachedToRecyclerView) {
            setLoadMore();
        }
    }

    public void setEmptyDataTip(String emptyDataTip) {
        this.emptyDataTip = emptyDataTip;
        if (tvEmptyHint != null) {
            tvEmptyHint.setText(emptyDataTip);
        }
    }

    public void setNoMoreDataTip(String noMoreDataTip) {
        this.noMoreDataTip = noMoreDataTip;
    }

    public void setEmptyViewResource(int emptyViewResource) {
        setEmptyViewResource(emptyViewResource, false);
    }

    public void setEmptyViewResource(int emptyViewResource, boolean isAdaptiveHeight) {
        this.emptyViewResource = emptyViewResource;
        this.isAdaptiveHeight = isAdaptiveHeight;
    }

    public void showError() {
        isEmpty = true;
        notifyDataSetChanged();
    }

    public void setData(List<T> data) {
        setNewInstance(data);
        if (data == getData()) {
            notifyDataSetChanged();
        }
    }


    @Override
    public T getItem(int position) {
        return super.getItem(position);
    }

    public void setHasFooter(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
    }
}
