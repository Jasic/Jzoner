package org.jasic.qzoner.common;
import org.jasic.qzoner.core.entity.IpMacPair;
/**
 * User: Jasic
 * Date: 13-9-16
 */
public class Globalvariables {

    // 系统变量刷新时间间隔单位秒
    public static int SYSTEM_REFRESH_INTERVAL = 1;

    // 本机所使用网卡的物理地址
//    public static String MAC_LOCAL_ETH_0 = "78-45-C4-05-80-1D";
    public static String MAC_LOCAL_ETH_0 = "0C-82-68-05-A6-C6";

    // 广播的物理地址
    public static String MAC_NETWORK_BROCAST = "FF-FF-FF-FF-FF-FF";

    // 本局域网的子网掩码
    public static String SUB_NET_MASK_ETH_0;


    // 本机的ip—mac对
    public static IpMacPair LOCAL_IP_MAC_PAIR;
    // 网关的ip—mac对
    public static IpMacPair GATE_WAY_IP_MAC_PAIR;


    // arp策略发送时间间隔
    public static long ARP_STRATEGY_INTERVAL = 5000;

    // 系统监控时间
    public static int SYSTEM_MONITOR_INTERVAL = 10;
}
