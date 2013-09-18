package org.jasic.qzoner.common;
import jpcap.packet.Packet;
import org.jasic.qzoner.core.entity.IpMacPair;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * User: Jasic
 * Date: 13-9-18
 */
public class GlobalCaches {

    static {
        init();
    }

    public static void init() {

        IP_MAC_LAN_ARP_REQUEST_QUEUE = new LinkedBlockingQueue<List<? extends Packet>>();
        IP_MAC_LAN_CONNECTIVITY_CACHE = new ConcurrentHashMap<String, IpMacPair>();
    }

    // Arp请求发送队列
    public static LinkedBlockingQueue<List<? extends Packet>> IP_MAC_LAN_ARP_REQUEST_QUEUE;


    // 保存局网内所有能连通的机器
    public static ConcurrentHashMap<String, IpMacPair> IP_MAC_LAN_CONNECTIVITY_CACHE;


}
