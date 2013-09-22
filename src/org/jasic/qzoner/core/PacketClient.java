package org.jasic.qzoner.core;
import jpcap.packet.Packet;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * User: Jasic
 * Date: 13-9-18
 */
public class PacketClient extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(PacketClient.class);

    private PacketSender sender;
    private ExecutorService es;
    private BlockingQueue<List<? extends Packet>> queue;

    private PacketCaptor captor;

    public PacketClient(IpMacPair ipMacPair, BlockingQueue<List<? extends Packet>> queue) {
        this(ipMacPair, queue, Executors.newCachedThreadPool());
    }

    public PacketClient(IpMacPair ipMacPair, BlockingQueue<List<? extends Packet>> queue, ExecutorService es) {
        this(PacketSender.newPacketSender(ipMacPair), PacketCaptor.newPacketCaptor(ipMacPair), queue, es);
    }

    public PacketClient(PacketSender sender, PacketCaptor captor, BlockingQueue<List<? extends Packet>> queue, ExecutorService es) {
        this.sender = sender;
        this.captor = captor;
        this.queue = queue;
        this.es = es;
    }

    @Override
    public void start() {

        /**
         * 开启捕获线程
         */
        new Thread() {
            @Override
            public void run() {
                captor.startCaptor();
            }
        }.start();

        /**
         * 循环发送包
         */
        while (!Thread.interrupted()) {
            try {
                final List<? extends Packet> list = this.queue.take();
                if (list.size() == 0) continue;

                this.es.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (Packet o : list) {
                            if (o instanceof Packet) {
                                sender.send(o);
                            }
                        }
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
    }
}
