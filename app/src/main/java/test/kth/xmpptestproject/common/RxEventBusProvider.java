package test.kth.xmpptestproject.common;

import test.kth.xmpptestproject.utils.Logger;

public class RxEventBusProvider {
    private static final RxEventBus rxEventBus = new RxEventBus();

    public static RxEventBus provide(){
        return rxEventBus;
    }
}
