package com.lpzahd.common.tone.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public abstract class ToneAdapter<T, VH extends ToneAdapter.ToneHolder> extends RecyclerView.Adapter<VH> implements ToneItemTouchHelperCallback.ItemTouchAdapter {

    private Context mContext;
    private LayoutInflater mInflater;

    List<T> mData;

    public ToneAdapter(Context context) {
        this(context, null);
    }

    public ToneAdapter(Context context, List<T> data) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    /**
     * 默认填充view 的方式
     */
    protected View inflateItemView(@LayoutRes int layoutId, @NonNull ViewGroup root) {
        return mInflater.inflate(layoutId, root, false);
    }

    public List<T> getData() {
        if (mData == null)
            return mData = new ArrayList<>();
        return mData;
    }

    public T getItem(int position) {
        return mData.get(position);
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getItemCount() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    public void setData(List<T> data) {
        setData(data, true);
    }

    public void setData(List<T> data, boolean auto) {
        this.mData = data;
        if(auto) notifyDataSetChanged();
    }

    public void addFirst(T t) {
        add(0, t);
    }

    public void add(T t) {
        add(index(), t);
    }

    public void add(int index, T t) {
        add(index, t, true);
    }

    public void add(int index, T t, boolean auto) {
        getData().add(index, t);
        if(auto) notifyItemInserted(index);
    }

    public void addAll(List<T> t) {
        addAll(index(), t);
    }

    public void addAll(int index, List<T> t) {
        addAll(index, t, true);
    }

    public void addAll(int index, List<T> t, boolean auto) {
        getData().addAll(index, t);
        if(auto) notifyItemRangeInserted(index, t.size());
    }

    private int index() {
        return getData().size();
    }

    public void remove(int index) {
        remove(index, true);
    }

    public void remove(int index, boolean auto) {
        mData.remove(index);
        if(auto) notifyItemRemoved(index);
    }

    @Override
    public boolean onMove(int fromPosition, int toPosition) {
        if (null == mData || mData.size() == 0 || mData.size() == 1) return false;
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mData, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mData, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(int position) {
        if (null == mData || mData.size() == 0) return;
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public static class ToneHolder extends RecyclerView.ViewHolder {

        public ToneHolder(View itemView) {
            super(itemView);
        }

    }

}
