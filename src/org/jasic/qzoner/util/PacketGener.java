package org.jasic.qzoner.util;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import org.jasic.qzoner.core.entity.IpMacPair;

import java.net.InetAddress;
import java.net.UnknownHostException;
/**
 * User: Jasic
 * Date: 13-9-12
 */
public class PacketGener {


    /**
     * 返回数据链路层数据包头
     *
     * @param src_addr
     * @param dst_addr
     * @return
     */
    public static EthernetPacket genEthpacket(byte[] src_addr, byte[] dst_addr) {
        EthernetPacket packet = new EthernetPacket();
        packet.frametype = EthernetPacket.ETHERTYPE_ARP;  //选择以太包类型
        packet.src_mac = src_addr;
        packet.dst_mac = dst_addr;
        return packet;
    }

    /**
     * 获取arp请求包
     * @param src
     * @param dst
     * @return
     * @throws UnknownHostException
     */
    public static ARPPacket getArpReqPacket(IpMacPair src, IpMacPair dst) throws UnknownHostException {

        ARPPacket packet = getArppacket(src, dst);
        packet.operation = 0x01;
        return packet;
    }

    /**
     * 获取arp响应包
     * @param src
     * @param dst
     * @return
     * @throws UnknownHostException
     */
    public static ARPPacket getArpRepPacket(IpMacPair src, IpMacPair dst) throws UnknownHostException {

        ARPPacket packet = getArppacket(src, dst);
        packet.operation = 0x02;
        return packet;
    }

    /**
     * 获取Arp公共包信息
     *
     * @param src
     * @param dst
     * @return
     * @throws UnknownHostException
     */
    private static ARPPacket getArppacket(IpMacPair src, IpMacPair dst) throws UnknownHostException {

        ARPPacket packet = new ARPPacket();
        packet.hardtype = ARPPacket.HARDTYPE_ETHER;
        packet.prototype = ARPPacket.PROTOTYPE_IP;
        packet.hlen = 6;
        packet.plen = 4;
        packet.operation = -1; // 请求包与答应包分别为1,2

        packet.sender_hardaddr = NetWorkUtil.macStrToByte(src.getMac());
        packet.sender_protoaddr = InetAddress.getByName(src.getIp()).getAddress();
        packet.target_hardaddr = NetWorkUtil.macStrToByte(dst.getMac());
        packet.target_protoaddr = InetAddress.getByName(dst.getIp()).getAddress();
        return packet;
    }
}
