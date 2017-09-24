package com.lpzahd.essay.context.guide.waiter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.essay.EssayActivity;
import com.lpzahd.essay.context.guide.GuideActivity;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class RecyclerWaiter extends ToneActivityWaiter<GuideActivity> {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private GuideAdapter adapter;

    public RecyclerWaiter(GuideActivity guideActivity) {
        super(guideActivity);
    }

    @Override
    protected void initView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new GuideAdapter(context);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        super.initData();

        String[] guides = new String[] {
           "畅谈随笔", "秀色可餐", "因缺思厅", "饿了么账单", "固执"
        };

        adapter.setData(Arrays.asList(guides));
    }

    class GuideHolder extends ToneAdapter.ToneHolder {

        @BindView(R.id.text)
        AppCompatTextView text;

        private GuideHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            RxView.clicks(text)
                    .throttleFirst(500, TimeUnit.MICROSECONDS)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            String guide = adapter.getItem(getAdapterPosition());
                            switch (guide) {
                                case "畅谈随笔":
                                    EssayActivity.startActivity(context);
                                    break;
                                case "秀色可餐":
                                    break;
                                case "因缺思厅":
                                    break;
                                case "饿了么账单":
                                    break;
                                case "固执":
                                    break;
                            }
                        }
                    });
        }
    }

    private class GuideAdapter extends ToneAdapter<String, GuideHolder> {

        private GuideAdapter(Context context) {
            super(context);
        }

        @Override
        public GuideHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GuideHolder(inflateItemView(R.layout.item_guide, parent));
        }

        @Override
        public void onBindViewHolder(GuideHolder holder, int position) {
            String name = getItem(position);
            holder.text.setText(name);
        }
    }
}
