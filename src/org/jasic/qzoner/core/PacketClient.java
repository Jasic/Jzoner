package org.jasic.qzoner.core;
import jpcap.packet.Packet;
import org.jasic.common.DefualtThreadFactory;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
/**
 * User: Jasic
 * Date: 13-9-18
 */
public class PacketClient extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(PacketClient.class);
    private static final String logHeader = "数据平台";

    /**
     * 数据包发送器
     */
    private PacketSender sender;

    /**
     * 数据包捕获器
     */
    private PacketCaptor captor;

    /**
     * 数据包发送线程池
     */
    private ExecutorService es;

    /**
     * IP包队列
     */
    private BlockingQueue<List<? extends Packet>> queue;


    public PacketClient(IpMacPair ipMacPair, BlockingQueue<List<? extends Packet>> queue) {
        this(ipMacPair, queue, Executors.newFixedThreadPool(1, new DefualtThreadFactory("数据包发送")));
    }

    public PacketClient(IpMacPair ipMacPair, BlockingQueue<List<? extends Packet>> queue, ExecutorService es) {
        this(PacketSender.newPacketSender(ipMacPair), PacketCaptor.newPacketCaptor(ipMacPair), queue, es);
    }

    public PacketClient(PacketSender sender, PacketCaptor captor, BlockingQueue<List<? extends Packet>> queue, ExecutorService es) {
        this.sender = sender;
        this.captor = captor;
        this.queue = queue;
        this.es = es;
        super.setName(logHeader);
    }

    @Override
    public void start() {

        /**
         * 开启捕获线程
         */
        new Thread(logHeader) {
            @Override
            public void run() {
                try {
                    captor.startCaptor();
                } catch (TimeoutException e) {
                    logger.info(logHeader + e.getMessage());
                    e.printStackTrace();
                    System.exit(-1);
                }
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
                                sender.send(o, false);
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
