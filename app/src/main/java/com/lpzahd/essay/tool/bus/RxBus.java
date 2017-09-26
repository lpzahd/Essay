package com.lpzahd.essay.tool.bus;


/**
 * Author : Lpzahd
 * Date : 九月
 * Desction : (•ิ_•ิ)
 */
public class RxBus {

    private static Bus sBus;

    public static synchronized Bus get() {
        return sBus != null ? sBus : (sBus = new Bus());
    }

    public static class BusService {

        private final Object tag;
        private Receiver receiver;

        public BusService(Object tag) {
            this.tag = tag;
        }

        public BusService(Object tag, Receiver receiver) {
            this.tag = tag;
            this.receiver = receiver;
        }

        public void setReceiver(Receiver receiver) {
            this.receiver = receiver;
        }

        public <T> void post(T argu) {
            get().post(tag, argu);
        }

        public void regist() {
            if(tag == null || receiver == null) return;
            get().regist(tag, receiver);
        }

        public void unregist() {
            if(tag == null) return;
            get().unregist(tag);
        }

    }
}
