package org.jasic.qzoner.core;
import jpcap.packet.Packet;
import org.jasic.qzoner.common.GlobalCaches;
import org.jasic.qzoner.common.Globalvariables;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.qzoner.util.PacketGener;
import org.jasic.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * User: Jasic
 * Date: 13-9-16
 */
@SuppressWarnings("unchecked")
public class ArpStrategy extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(ArpStrategy.class);
    private static final String logHeader = "[Arp策略]-";

    private long interval;
    private IpMacPair localIpMacPair;
    private IpMacPair gateWay;

    private LinkedBlockingQueue<List<? extends Packet>> queue;//请求队列

    public ArpStrategy(IpMacPair ipMacPair, IpMacPair gateWay) {
        this.localIpMacPair = ipMacPair;
        this.gateWay = gateWay;
        this.interval = Globalvariables.ARP_STRATEGY_INTERVAL;
        super.setName(logHeader);
    }

    @Override
    public void run() {

        this.queue = GlobalCaches.IP_MAC_LAN_PACKET_TO_BE_SEND_QUEUE;
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 1、arp广播请求
                try {
                    List bs = PacketGener.brocasStrategy(gateWay, localIpMacPair);
                    logger.info(logHeader + bs.toString());
                    if (bs.size() == 0) {
                        logger.info(logHeader + "network configure error, generate none arp brocast packet~");
                        return;
                    }
                    queue.put(bs);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info(logHeader + e.getMessage());
                }

            }
        }, 0, this.interval * 6);
        while (!isInterrupted()) {
            try {
                // 2、伪造网关请请
                // 3、伪造网关响应
                List fqs = PacketGener.fakeRandomArpStrategy(gateWay, localIpMacPair);

                if (fqs.size() != 0) {
                    queue.put(fqs);
                    logger.info(logHeader + "[随机伪造网关请求或响应]" + fqs.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(logHeader + e.getMessage());
            } finally {
                TimeUtil.sleep(this.interval);
            }
        }
    }
}
