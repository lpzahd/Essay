package com.lpzahd.essay.context.note;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.view.View;

import com.lpzahd.aop.api.Log;
import com.lpzahd.derive.charset.CharDet;
import com.lpzahd.essay.R;
import com.lpzahd.essay.util.note.NoteHelper;
import com.lpzahd.essay.util.note.parse.TxtParse;
import com.lpzahd.essay.util.note.parser.CodeTagParser;
import com.lpzahd.essay.util.note.parser.HTagParser;
import com.lpzahd.animview.AnimView;
import com.lpzahd.waiter.WaiterActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteActivity extends StudyActivity {

//    @BindView(R.id.tv)
//    AppCompatTextView tv;
//
//    @BindView(R.id.search_anim_view)
//    AnimView searchView;

//    @Override
//    protected void inflaterView(@Nullable Bundle savedInstanceState) {
//        setContentView(R.layout.activity_note);
//        ButterKnife.bind(this);

//        tv.setText(Html.fromHtml(toText()));
//        tv.setSpannableFactory();
//        tv.setText(parseText(toText()));
//        RichText.fromHtml("").into(tv);
//        tv.setText(test());


//        intoTextView();
//
//        findViewById(R.id.start)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        searchView.startAnim();
//                    }
//                });
//
//        findViewById(R.id.end)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        searchView.stopAnim();
//                    }
//                });
//    }

    @Log
    private void intoTextView() {
        new NoteHelper()
                .setTxtParses(new TxtParse() {
                    @Override
                    public String parseTxt(String txt) {
                        return txt.replace("\t", "        ");
                    }
                })
                .setTagParses(new CodeTagParser("<code>","</code>"), new CodeTagParser("<title>","</title>")
                        ,new CodeTagParser("<h1>","</h1>")
                        ,new HTagParser())
                .doTxt(toText(), new NoteHelper.OnDoListener() {

                    @Override
                    public void onStart() {
                        android.util.Log.d("hit", "onStart : " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable e) {
                        android.util.Log.d("hit", "onError : " + Thread.currentThread().getName());
                    }

                    @Override
                    public void onComplete(SpannableStringBuilder builder) {
//                        tv.setText(builder);
                        android.util.Log.d("hit", "onComplete : " + Thread.currentThread().getName());
                    }
                });
    }


    private SpannableStringBuilder test() {
        SpannableStringBuilder builder = new SpannableStringBuilder("1234");
        builder.setSpan(new AbsoluteSizeSpan(36), 1, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        builder.append("8765");
        builder.setSpan(new BackgroundColorSpan(Color.YELLOW), 1, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    private SpannableStringBuilder parseText(String text) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        AbsoluteSizeSpan span = new AbsoluteSizeSpan(36);
        builder.setSpan(span, 2, 5, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        BackgroundColorSpan span2 = new BackgroundColorSpan(Color.YELLOW);
        builder.setSpan(span2, 100, 300, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Log
    private String toText() {
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(dir + "/test.txt");
        InputStream in = null;
        BufferedReader br = null;
        StringBuilder builder = new StringBuilder();
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            br = new BufferedReader(new InputStreamReader(in, CharDet.getCharsetFromFile(file)));
            String temp;
            while ((temp = br.readLine()) != null) {
                builder.append(temp).append("\n");
//                        .append("<div style=\"text-indent:2em\">")
//                        .append(temp).append("\n")
//                        .append("</div>");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

}
