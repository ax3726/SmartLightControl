package com.lm.common.adapter;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lm.common.R;


/**
 * @author ：LiMing
 * @date ：2020-04-08
 * @desc ：通用的ViewHolder
 * 注意： 后加的方法需在前面调用
 */
public class BaseCommonViewHolder extends BaseViewHolder {
    private int position;

    /**
     * 计算下标
     *
     * @param adapter
     */
    public void countPosition(BaseRecycleViewAdapter adapter) {
        if (getLayoutPosition() >= adapter.getHeaderLayoutCount()) {
            position = getLayoutPosition() - adapter.getHeaderLayoutCount();
        }
    }

    public int getIndex() {
        return position;
    }

    public BaseCommonViewHolder(View view) {
        super(view);
    }


    public BaseCommonViewHolder setTag(int viewId, Object tag) {
        getView(viewId).setTag(tag);
        return this;
    }

    @Override
    public BaseCommonViewHolder setEnabled(int viewId, boolean isEnabled) {
        super.setEnabled(viewId, isEnabled);
        return this;
    }

    @Override
    public BaseCommonViewHolder setText(int viewId, CharSequence value) {
        return (BaseCommonViewHolder) super.setText(viewId, TextUtils.isEmpty(value) ? "--" : value);
    }

    public BaseCommonViewHolder setText(int viewId, CharSequence text, CharSequence defaultText) {
        setText(viewId, TextUtils.isEmpty(text) ? defaultText : text);
        return this;
    }

    public BaseCommonViewHolder setSelect(@IdRes int viewId, boolean selected) {
        getView(viewId).setSelected(selected);
        return this;
    }

    public BaseCommonViewHolder setChecked(@IdRes int viewId, boolean checked) {
        View view = getView(viewId);
        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(checked);
        }
        return this;
    }



    public BaseCommonViewHolder appendText(@IdRes int id, String text) {
        TextView textView = getView(id);
        text = TextUtils.isEmpty(text) ? "--" : text;
        textView.setText((textView.getText() + text));
        return this;
    }

    public BaseCommonViewHolder setOnClickListener(@IdRes int id, View.OnClickListener onClickListener) {
        getView(id).setOnClickListener(onClickListener);
        return this;
    }

    @Override
    public BaseCommonViewHolder setImageDrawable(int viewId, Drawable drawable) {
        super.setImageDrawable(viewId, drawable);
        return this;
    }

    public BaseCommonViewHolder setBackgroundResources(@IdRes int id, int resources) {
        getView(id).setBackgroundResource(resources);
        return this;
    }

    public BaseCommonViewHolder setVisibility(@IdRes int id, int visibility) {
        getView(id).setVisibility(visibility);
        return this;
    }

    public BaseCommonViewHolder setVisibility(@IdRes int id, boolean isVisible) {
        getView(id).setVisibility(isVisible ? View.VISIBLE : View.GONE);
        return this;
    }



    public BaseCommonViewHolder callOnClick(int id) {
        getView(id).callOnClick();
        return this;
    }


    public Object getTag(int id) {
        return getView(id).getTag();
    }



    public String getText(int id) {
        return ((TextView) getView(id)).getText().toString();
    }



    public boolean getVisibility(int id) {
        return (getView(id)).getVisibility() == View.VISIBLE;
    }

    public boolean isSelected(int id) {
        return getView(id).isSelected();
    }

    public BaseCommonViewHolder setText(int id, String text) {
        setText(id, text, "--");
        return this;
    }

    public BaseCommonViewHolder setHtmlText(int id, String text) {
        setHtmlText(id, text, "--");
        return this;
    }



    public BaseCommonViewHolder setHtmlText(int id, String text, String defaultText) {
        ((TextView) getView(id)).setText(Html.fromHtml(TextUtils.isEmpty(text) ? defaultText : text));
        return this;
    }

    public BaseCommonViewHolder setText(int id, String text, String defaultText) {
        ((TextView) getView(id)).setText(TextUtils.isEmpty(text) ? defaultText : text);
        return this;
    }




    @Override
    public BaseCommonViewHolder setTextColor(int id, int textColor) {
        ((TextView) getView(id)).setTextColor(textColor);
        return this;
    }


    @Override
    public BaseCommonViewHolder setBackgroundColor(int id, int color) {
        getView(id).setBackgroundColor(color);
        return this;
    }


    public BaseCommonViewHolder setImageResources(int id, int Resources) {
        ((ImageView) getView(id)).setImageResource(Resources);
        return this;
    }

    public boolean isChecked(int id) {
        return ((Checkable) getView(id)).isChecked();
    }


    public BaseCommonViewHolder setOnListLayoutClickListener(int id, View.OnClickListener onclickListener) {
        getView(id).setOnClickListener(onclickListener);
        return this;
    }

    public BaseCommonViewHolder setOnLongClickListener(int id, View.OnLongClickListener onclickListener) {
        getView(id).setOnLongClickListener(onclickListener);
        return this;
    }

    public BaseCommonViewHolder setOnClickListener(int[] ids, View.OnClickListener onclickListener) {
        for (int id : ids) {
            getView(id).setOnClickListener(onclickListener);
        }

        return this;
    }



}
