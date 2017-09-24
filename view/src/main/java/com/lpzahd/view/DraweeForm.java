package com.lpzahd.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lpzahd.Lists;
import com.lpzahd.atool.ui.Ui;

import java.util.List;

/**
 * Author : Lpzahd
 * Date : 四月
 * Desction : (•ิ_•ิ)
 */
public class DraweeForm extends FrameLayout implements View.OnClickListener {

    private SimpleDraweeView oneDrawee;
    private SimpleDraweeView twoDrawee;
    private SimpleDraweeView threeDrawee;
    private SimpleDraweeView fourDrawee;
    private SimpleDraweeView moreDrawee;

    private DraweeClickListener listener;

    public void setDraweeClickListener(DraweeClickListener listener) {
        this.listener = listener;
    }

    public DraweeForm(Context context) {
        this(context, null);
    }

    public DraweeForm(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DraweeForm(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DraweeForm(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        initViews(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (isInEditMode())
            return;
    }

    private void initViews(Context context) {
        View draweeForm = LayoutInflater.from(context).inflate(R.layout.view_drawee_form, this, false);

        oneDrawee = Ui.findViewById(draweeForm, R.id.one_drawee_view);
        twoDrawee = Ui.findViewById(draweeForm, R.id.two_drawee_view);
        threeDrawee = Ui.findViewById(draweeForm, R.id.three_drawee_view);
        fourDrawee = Ui.findViewById(draweeForm, R.id.four_drawee_view);
        moreDrawee = Ui.findViewById(draweeForm, R.id.more_drawee_view);

        oneDrawee.setOnClickListener(this);
        twoDrawee.setOnClickListener(this);
        threeDrawee.setOnClickListener(this);
        fourDrawee.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(listener == null) return ;

        int id = v.getId();
        if(id == R.id.one_drawee_view) {
            listener.click(this, (SimpleDraweeView) v, 0);
            return ;
        }

        if(id == R.id.two_drawee_view) {
            listener.click(this, (SimpleDraweeView) v, 1);
            return ;
        }

        if(id == R.id.three_drawee_view) {
            listener.click(this, (SimpleDraweeView) v, 2);
            return ;
        }

        if(id == R.id.four_drawee_view) {
            listener.click(this, (SimpleDraweeView) v, 3);
        }

    }

    public interface DraweeClickListener {
        void click(DraweeForm parent, SimpleDraweeView view, int index);
    }

    public void update(List<Photo> photos) {
        if(Lists.empty(photos)) {
            this.setVisibility(View.GONE);
            return;
        }

        restoreViews();

        switch (photos.size()) {
            case 1:
                twoDrawee.setVisibility(View.GONE);
                threeDrawee.setVisibility(View.GONE);
                fourDrawee.setVisibility(View.GONE);

                oneDrawee.setImageURI(photos.get(0).getPath());
                break;
            case 2:
                threeDrawee.setVisibility(View.GONE);
                fourDrawee.setVisibility(View.GONE);

                oneDrawee.setImageURI(photos.get(0).getPath());
                twoDrawee.setImageURI(photos.get(1).getPath());
                break;
            case 3:
                fourDrawee.setVisibility(View.GONE);

                oneDrawee.setImageURI(photos.get(0).getPath());
                twoDrawee.setImageURI(photos.get(1).getPath());
                threeDrawee.setImageURI(photos.get(2).getPath());
                break;
            case 4:
                oneDrawee.setImageURI(photos.get(0).getPath());
                twoDrawee.setImageURI(photos.get(1).getPath());
                threeDrawee.setImageURI(photos.get(2).getPath());
                fourDrawee.setImageURI(photos.get(3).getPath());
                break;
            default:
                oneDrawee.setImageURI(photos.get(0).getPath());
                twoDrawee.setImageURI(photos.get(1).getPath());
                threeDrawee.setImageURI(photos.get(2).getPath());
                fourDrawee.setImageURI(photos.get(3).getPath());
                moreDrawee.setVisibility(View.VISIBLE);
                break;
        }

        requestLayout();
    }

    private void restoreViews() {
        this.setVisibility(View.VISIBLE);
        oneDrawee.setVisibility(View.VISIBLE);
        twoDrawee.setVisibility(View.VISIBLE);
        threeDrawee.setVisibility(View.VISIBLE);
        fourDrawee.setVisibility(View.VISIBLE);
        moreDrawee.setVisibility(View.INVISIBLE);
    }

    public static class Photo {

        private String path;

        private int width;

        private int height;

//        private String suffix;

//        private String thumb;

        public Photo(String path) {
            this.path = path;
        }

        public Photo(String path, int width, int height) {
            this.path = path;
            this.width = width;
            this.height = height;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

}
