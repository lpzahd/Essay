package com.lpzahd.common.taxi;

/**
 * Author : Lpzahd
 * Date : 十月
 * Desction : (•ิ_•ิ)
 */
public class RxTaxi {

    private static Taxi sTaxi;

    public static synchronized Taxi get() {
        return sTaxi != null ? sTaxi : (sTaxi = new Taxi());
    }

    public static class PrivateCar {

        private final Object tag;
        private Transmitter transmitter;

        public PrivateCar(Object tag) {
            this.tag = tag;
        }

        public PrivateCar(Object tag, Transmitter transmitter) {
            this.tag = tag;
            this.transmitter = transmitter;
        }

        public void setReceiver(Transmitter transmitter) {
            this.transmitter = transmitter;
        }

        public <T> Transmitter<T> pull() {
            return get().pull(tag);
        }

        public void regist() {
            if(tag == null || transmitter == null) return;
            get().regist(tag, transmitter);
        }

        public void unregist() {
            if(tag == null) return;
            get().unregist(tag);
        }

    }

}
