package org.jasic.qzoner.common;
import org.jasic.qzoner.core.entity.IpMacPair;
/**
 * User: Jasic
 * Date: 13-9-16
 */
public class Globalvariables {

    // 本机所使用网卡的物理地址
    public static String MAC_LOCAL_ETH_0 = "78-45-C4-05-80-1D";

    // 广播的物理地址
    public static String MAC_NETWORK_BROCAST = "FF-FF-FF-FF-FF-FF";

    // 本局域网的子网掩码
    public static String SUB_NET_MASK_ETH_0;

    // 网关的ip—mac对
    public static IpMacPair GATE_WAY_IP_MAC_PAIR;

    // arp策略发送时间间隔
    public static long ARP_STRATEGY_INTERVAL = 10000;




}
