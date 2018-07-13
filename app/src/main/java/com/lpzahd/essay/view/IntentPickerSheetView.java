package com.lpzahd.essay.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

import com.lpzahd.Lists;
import com.lpzahd.atool.ui.Ui;
import com.lpzahd.common.tone.adapter.OnItemHolderTouchListener;
import com.lpzahd.common.tone.adapter.ToneAdapter;
import com.lpzahd.essay.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressLint("ViewConstructor")
public class IntentPickerSheetView extends FrameLayout {

    private static final int COLUMN_WIDTH_DP = 100;

    public interface Filter {
        boolean include(ActivityInfo info);
    }

    public interface OnIntentPickedListener {
        void onIntentPicked(IntentPickerSheetView view, ActivityInfo activityInfo);
    }

    private class SortAlphabetically implements Comparator<ActivityInfo> {
        @Override
        public int compare(ActivityInfo lhs, ActivityInfo rhs) {
            return lhs.label.compareTo(rhs.label);
        }
    }

    private class FilterNone implements Lists.Predicate<ActivityInfo> {

        @Override
        public boolean test(ActivityInfo var1) {
            return false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (ActivityInfo activityInfo : adapter.getData()) {
            if (activityInfo.iconLoadTask != null) {
                activityInfo.iconLoadTask.cancel(true);
                activityInfo.iconLoadTask = null;
            }
        }
    }

    /**
     * Represents an item in the picker grid
     */
    public static class ActivityInfo {
        public Drawable icon;
        public final String label;
        public final ComponentName componentName;
        public final ResolveInfo resolveInfo;
        private AsyncTask<Void, Void, Drawable> iconLoadTask;
        public Object tag;

        public ActivityInfo(Drawable icon, String label, Context context, Class<?> clazz) {
            this.icon = icon;
            resolveInfo = null;
            this.label = label;
            this.componentName = new ComponentName(context, clazz.getName());
        }

        ActivityInfo(ResolveInfo resolveInfo, CharSequence label, ComponentName componentName) {
            this.resolveInfo = resolveInfo;
            this.label = label.toString();
            this.componentName = componentName;
        }

    }

    protected final Intent intent;
    protected final RecyclerView recyclerView;
    protected final AppCompatTextView titleView;

    protected PackageManager packageManager;
    protected IntentPickerSheetAdapter adapter;
    protected GridLayoutManager manager;
    protected Lists.Predicate<ActivityInfo> filter = new FilterNone();
    protected Comparator<ActivityInfo> sortMethod = new SortAlphabetically();

    public static IntentPickerSheetView share(Context context, String shareTxt, OnIntentPickedListener listener) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareTxt);
        shareIntent.setType("text/plain");
        return new IntentPickerSheetView(context, shareIntent, "Share with...", listener);
    }

    public static void startActivity(IntentPickerSheetView view, ActivityInfo activityInfo) {
        Intent concreteIntent = new Intent(view.intent);
        concreteIntent.setComponent(activityInfo.componentName);
        view.getContext().startActivity(concreteIntent);
    }

    public IntentPickerSheetView(Context context, Intent intent, @StringRes int titleRes, OnIntentPickedListener listener) {
        this(context, intent, context.getString(titleRes), listener);
    }

    public IntentPickerSheetView(Context context, final Intent intent, final String title, final OnIntentPickedListener listener) {
        super(context);
        this.intent = intent;

        inflate(context, R.layout.view_intent_picker_sheet, this);
        recyclerView = findViewById(R.id.recycler_view);
        titleView = findViewById(R.id.text_view);

        titleView.setText(title);
        recyclerView.addOnItemTouchListener(new OnItemHolderTouchListener<IntentPickerSheetHolder>(recyclerView) {
            @Override
            public void onClick(RecyclerView rv, IntentPickerSheetHolder intentPickerSheetHolder) {
                listener.onIntentPicked(IntentPickerSheetView.this, adapter.getItem(intentPickerSheetHolder.getAdapterPosition()));
            }
        });


        ViewCompat.setElevation(this, Ui.dip2px(getContext(), 16));
    }

    public void setSortMethod(Comparator<ActivityInfo> sortMethod) {
        this.sortMethod = sortMethod;
    }

    public void setFilter(Lists.Predicate<ActivityInfo> filter) {
        this.filter = filter;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        final Context context = getContext();

        recyclerView.setHasFixedSize(true);
        manager = new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        adapter = new IntentPickerSheetAdapter(getContext());
        recyclerView.setAdapter(adapter);

        packageManager = context.getPackageManager();
        List<ResolveInfo> infos = packageManager.queryIntentActivities(intent, 0);
        List<ActivityInfo> activityInfos = new ArrayList<>(infos.size());
        for (ResolveInfo info : infos) {
            ComponentName componentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
            ActivityInfo activityInfo = new ActivityInfo(info, info.loadLabel(packageManager), componentName);
            activityInfos.add(activityInfo);
        }
        Lists.removeIf(activityInfos, filter);
        Collections.sort(activityInfos, sortMethod);
        adapter.setData(activityInfos);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        final float density = getResources().getDisplayMetrics().density;
        this.manager.setSpanCount((int) (width / (COLUMN_WIDTH_DP * density)));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Necessary for showing elevation on 5.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new ShadowOutline(w, h));
        }
    }

    private class IntentPickerSheetHolder extends ToneAdapter.ToneHolder {

        final AppCompatImageView iconIv;
        final AppCompatTextView labelTv;

        IntentPickerSheetHolder(View itemView) {
            super(itemView);
            iconIv = itemView.findViewById(R.id.icon_iv);
            labelTv = itemView.findViewById(R.id.label_tv);
        }
    }

    private class IntentPickerSheetAdapter extends ToneAdapter<ActivityInfo, IntentPickerSheetHolder> {

        IntentPickerSheetAdapter(Context context) {
            super(context);

        }

        @NonNull
        @Override
        public IntentPickerSheetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new IntentPickerSheetHolder(inflateItemView(R.layout.item_intent_picker_sheet, parent));
        }

        @Override
        public void onBindViewHolder(@NonNull IntentPickerSheetHolder holder, int position) {
            final ActivityInfo info = getItem(position);
            if (info.iconLoadTask != null) {
                info.iconLoadTask.cancel(true);
                info .iconLoadTask = null;
            }
            if (info.icon != null) {
                holder.iconIv.setImageDrawable(info.icon);
            } else {
                holder.iconIv.setImageDrawable(getResources().getDrawable(flipboard.bottomsheet.commons.R.color.divider_gray));
                info.iconLoadTask = new AsyncTask<Void, Void, Drawable>() {
                    @Override
                    protected Drawable doInBackground(@NonNull Void... params) {
                        return info.resolveInfo.loadIcon(packageManager);
                    }

                    @Override
                    protected void onPostExecute(@NonNull Drawable drawable) {
                        info.icon = drawable;
                        info.iconLoadTask = null;
                        holder.iconIv.setImageDrawable(drawable);
                    }
                };
                info.iconLoadTask.execute();
            }
            holder.labelTv.setText(info.label);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static class ShadowOutline extends ViewOutlineProvider {

        int width;
        int height;

        ShadowOutline(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRect(0, 0, width, height);
        }
    }
}
