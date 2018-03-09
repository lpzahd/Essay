package com.lpzahd.essay.context.main.waiter;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lpzahd.common.tone.waiter.ToneActivityWaiter;
import com.lpzahd.common.util.fresco.Frescoer;
import com.lpzahd.essay.R;
import com.lpzahd.essay.context.main.MainActivity;
import com.lpzahd.waiter.consumer.State;

import butterknife.BindView;

/**
 * @author lpzahd
 * @describe
 * @time 2018/2/28 14:57
 * @change
 */
public class GuideNavigationWaiter extends ToneActivityWaiter<MainActivity> implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    public GuideNavigationWaiter(MainActivity mainActivity) {
        super(mainActivity);
    }

    @Override
    protected void initView() {
        super.initView();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                context, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationHeader(navigationView);
    }

    private void navigationHeader(NavigationView navigationView) {
        LinearLayout headerView = (LinearLayout) navigationView.getHeaderView(0);
        SimpleDraweeView headerDraweeView = (SimpleDraweeView) headerView.findViewById(R.id.header_drawee_view);
        AppCompatTextView nameTv = (AppCompatTextView) headerView.findViewById(R.id.name_tv);
        AppCompatTextView infoTv = (AppCompatTextView) headerView.findViewById(R.id.info_tv);

        headerDraweeView.setImageURI(Frescoer.res(R.mipmap.ic_launcher));
        nameTv.setText("Lpzahd");
        infoTv.setText("lpzajs@gmail.com");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected int backPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return State.STATE_PREVENT;
        } else {
            return super.backPressed();
        }
    }
}
