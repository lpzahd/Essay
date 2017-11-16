package com.lpzahd.essay.exotic.storage;

import com.lpzahd.atool.keeper.storage.Progress;
import com.lpzahd.atool.keeper.storage.internal.ProgressDao;
import com.lpzahd.atool.ui.L;

import io.realm.Realm;

/**
 * 作者 : 迪
 * 时间 : 2017/11/15.
 * 描述 ： 命里有时终须有，命里无时莫强求
 */
public class ProgressDaoImpl implements ProgressDao {

    private Realm realm;

    public ProgressDaoImpl() {
        realm = Realm.getDefaultInstance();
        L.e("init thread : " + Thread.currentThread().getName());
    }


    @Override
    public void insert(Progress progress) {
        realm.beginTransaction();
        realm.insert(ProgressBean.convert(progress));
        realm.commitTransaction();
    }

    private ProgressBean currentBean;

    @Override
    public void update(Progress progress) {
        if(currentBean == null) {
            currentBean =  realm.where(ProgressBean.class)
                    .equalTo("url", progress.url)
                    .findFirst();
        }

        if(currentBean == null) {
            L.e("没有查询到" + progress.url + "的progress信息");
            return ;
        }
        realm.beginTransaction();
        ProgressBean.update(progress, currentBean, realm);
        realm.commitTransaction();
    }

    @Override
    public void delete(String url) {
        realm.beginTransaction();
        realm.where(ProgressBean.class)
                .equalTo("url", url)
                .findFirst()
                .deleteFromRealm();
        realm.commitTransaction();
    }

    @Override
    public Progress select(String url) {
        ProgressBean progressBean = realm.where(ProgressBean.class)
                .equalTo("url", url)
                .findFirst();

        return ProgressBean.convert(progressBean);
    }

    @Override
    public void close() {
        L.e("close thread : " + Thread.currentThread().getName());
        if(!realm.isClosed()) realm.close();
    }

    @Override
    public ProgressDao clone() {
        return new ProgressDaoImpl();
    }
}
