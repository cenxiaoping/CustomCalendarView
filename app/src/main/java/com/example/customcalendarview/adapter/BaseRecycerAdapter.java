package com.example.customcalendarview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecycerAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    private List<T> mData;
    private Context mContext;
    private int mResId;

    private OnItemClickListener<T> mItemClickListener;
    private OnItemLongClickListener<T> mItemLongClickListener;

    public BaseRecycerAdapter(Context context, List<T> datas, int resId) {
        this.mContext = context;
        this.mData = datas;
        this.mResId = resId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(mContext).inflate(mResId, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public final void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        final T t = mData.get(i);
        onBindViewHolder(viewHolder, t, i);
        viewHolder.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(t, i);
                }
            }
        });

        viewHolder.getRootView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClickListener(t, i);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData != null && mData.size() > 0) {
            return mData.size();
        }
        return 0;
    }

    /**
     * @param viewHolder
     * @param itemVO
     * @param position
     */
    public abstract void onBindViewHolder(ViewHolder viewHolder, T itemVO, int position);

    public interface OnItemClickListener<T> {
        void onItemClick(T t, int position);
    }

    public interface OnItemLongClickListener<T> {
        void onItemLongClickListener(T t, int position);
    }

    public BaseRecycerAdapter setOnItemClickListener(OnItemClickListener listener) {
        if (listener != null) {
            this.mItemClickListener = listener;
        }
        return this;
    }

    public BaseRecycerAdapter setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
        return this;
    }

    public List<T> getData() {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        return mData;
    }

    public void setData(List<T> mData) {
        this.mData = mData;
    }
}
