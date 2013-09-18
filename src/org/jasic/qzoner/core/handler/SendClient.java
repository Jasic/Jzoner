package org.jasic.qzoner.core.handler;
import jpcap.packet.Packet;
import org.jasic.qzoner.core.PacketSender;
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
public class SendClient extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(SendClient.class);

    private PacketSender sender;
    private ExecutorService es;
    private BlockingQueue<List<? extends Packet>> queue;


    public SendClient(IpMacPair ipMacPair, BlockingQueue<List<? extends Packet>> queue) {
        this(new PacketSender(ipMacPair), queue);
    }

    public SendClient(PacketSender sender, BlockingQueue<List<? extends Packet>> queue) {
        this(sender, queue, Executors.newCachedThreadPool());
    }

    public SendClient(PacketSender sender, BlockingQueue<List<? extends Packet>> queue, ExecutorService es) {
        this.sender = sender;
        this.queue = queue;
        this.es = es;
    }

    @Override
    public void start() {
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
