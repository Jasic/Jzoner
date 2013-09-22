package org.jasic.qzoner.core.handler.proc;
import jpcap.packet.Packet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 对数据包的类，使用线程池去执行处理。
 * User: Jasic
 * Date: 13-9-18
 */
public abstract class AProcessor<P extends Packet> {

    private ExecutorService es;

    public AProcessor() {
        this(null);
    }

    public AProcessor(ExecutorService es) {
        this.es = es;

        this.init();
    }

    private void init() {
        if (this.es == null) {
            this.es = Executors.newCachedThreadPool();
        }
    }

    public void process(final P packet) {
        es.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        doProcess(packet);
                    }
                }
        );
    }

    protected abstract void doProcess(P p);
}
