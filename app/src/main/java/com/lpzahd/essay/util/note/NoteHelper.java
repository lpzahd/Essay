package com.lpzahd.essay.util.note;

import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;

import com.lpzahd.essay.util.note.parse.TagParse;
import com.lpzahd.essay.util.note.parse.TxtParse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author : Lpzahd
 * Date : 三月
 * Desction : (•ิ_•ิ)
 */
public class NoteHelper {

    private SpannableStringBuilder builder = new SpannableStringBuilder();

    private TxtParse[] txtParses;

    private TagParse[] tagParses;

    private Disposable fDispose;
    private Disposable tDispose;

    public NoteHelper setTxtParses(TxtParse... parses) {
        txtParses = parses;
        return this;
    }

    public NoteHelper setTagParses(TagParse... parses) {
        tagParses = parses;
        return this;
    }

    private void clear() {
        builder.clear();

        if (txtParses != null) {
            for (int i = 0; i < txtParses.length; i++) {
                txtParses[i] = null;
            }
        }

        if (tagParses != null) {
            for (int i = 0; i < tagParses.length; i++) {
                tagParses[i] = null;
            }
        }
    }

    private void dispose(Disposable dispose) {
        if(dispose != null || !dispose.isDisposed())
            dispose.dispose();
    }

    private void dispossAll() {
        dispose(fDispose);
        dispose(tDispose);
    }

    public void destroy() {
        dispossAll();
        clear();
    }

    /**
     * 解析内容
     */
    public void doTxt(@NonNull String txt, final OnDoListener listener) {
        builder.clear();

        dispose(fDispose);
        fDispose = Observable.just(txt)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        if (txtParses != null && txtParses.length != 0) {
                            for (TxtParse parse : txtParses) {
                                s = parse.parseTxt(s);
                            }
                        }
                        return s;
                    }
                })
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (listener != null) {
                            listener.onStart();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        parseTags(s, listener);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (listener != null) {
                            listener.onError(throwable);
                        }
                    }
                });

    }

    private void parseTags(String txt, final OnDoListener listener) {
        List<Observable<List<TagParse.Tag>>> obs = new ArrayList<>(tagParses.length);
        for (TagParse tagParse : tagParses) {
            obs.add(Observable.just(tagParse).zipWith(Observable.just(txt), new BiFunction<TagParse, String, List<TagParse.Tag>>() {
                @Override
                public List<TagParse.Tag> apply(TagParse tagParse, String s) throws Exception {
                    return tagParse.parseTxt(s);
                }
            })
                    .subscribeOn(Schedulers.io()));
        }

        dispose(tDispose);
        tDispose = Observable.zip(obs, new Function<Object[], List<TagParse.Tag>>() {
            @Override
            public List<TagParse.Tag> apply(Object[] tags) throws Exception {
                List<TagParse.Tag> allTag = new ArrayList<>();
                for (Object tag : tags) {
                    allTag.addAll((Collection<? extends TagParse.Tag>) tag);
                }
                return allTag;
            }
        })
                .zipWith(Observable.just(txt),
                        new BiFunction<List<TagParse.Tag>, String, SpannableStringBuilder>() {

                            @Override
                            public SpannableStringBuilder apply(List<TagParse.Tag> tags, String s) throws Exception {
                                SpannableStringBuilder builder = new SpannableStringBuilder(s);
                                final int size = tags.size();
                                for (int i = 0; i < size; i++) {
                                    TagParse.Tag tag = tags.get(i);
                                    if (tag.getWhat() != null) {
                                        builder.setSpan(tag.getWhat(), tag.getStart(), tag.getEnd(), tag.getFlag());
                                    }
                                }
                                return builder;
                            }
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SpannableStringBuilder>() {
                    @Override
                    public void accept(SpannableStringBuilder spannableStringBuilder) throws Exception {
                        builder.append(spannableStringBuilder);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (listener != null) {
                            listener.onError(throwable);
                        }
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        if (listener != null) {
                            listener.onComplete(builder);
                        }
                    }
                });

    }

    public interface OnDoListener {

        void onStart();

        void onError(Throwable e);

        void onComplete(SpannableStringBuilder builder);
    }
}
