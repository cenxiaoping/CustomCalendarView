package com.example.customcalendarview.adapter;

import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;
    private View mRootView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mRootView = itemView;
        mViews = new SparseArray<>();
    }

    //获取view
    public <T extends View> T findViewById(int id) {
        View view = mViews.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            mViews.put(id, view);
        }
        return (T) view;
    }

    public View getRootView() {
        return mRootView;
    }

    /**
     * 直接设置TextView的内容，仅限TextView及它的子类使用
     *
     * @param id   需要设置View的ID
     * @param text 需要设置的内容
     * @return
     */
    public ViewHolder setText(int id, String text) {
        if (!TextUtils.isEmpty(text) && id != 0) {
            View view = findViewById(id);
            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            }
        }
        return this;
    }
}