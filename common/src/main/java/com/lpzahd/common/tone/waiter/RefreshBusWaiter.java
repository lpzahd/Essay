package com.lpzahd.common.tone.waiter;

import android.view.View;

import com.lpzahd.waiter.agency.WindowWaiter;

/**
 * Author : Lpzahd
 * Date : 五月
 * Desction : (•ิ_•ิ) RxBus无效
 */
@Deprecated
public class RefreshBusWaiter extends WindowWaiter<RefreshBusWaiter> {

    public static final String TAG = "RefreshBusWaiter";

    private CaughtEvent caughtEvent;

    public RefreshBusWaiter(CaughtEvent caughtEvent) {
        this.caughtEvent = caughtEvent;
    }

    @Override
    public void create(View rootView) {
        super.create(rootView);
//        RxBus.get().register(this);
    }

    @Override
    protected void destroy() {
        super.destroy();
//        RxBus.get().unregister(this);
    }

//    @Subscribe(
//            thread = EventThread.MAIN_THREAD,
//            tags = {@Tag(RefreshBusWaiter.TAG)}
//    )
    public void refresh() {
        caughtEvent.caught();
    }

    public static void post() {
//        RxBus.get().post(TAG, Boolean.TRUE);
    }

    public interface CaughtEvent {
        void caught();
    }
}
