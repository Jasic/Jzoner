package org.jasic.qzoner.util;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import org.jasic.qzoner.common.Globalvariables;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.utils.SystemUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.jasic.utils.SystemUtil.macStrToByte;

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
    public static EthernetPacket genEthpacket(byte[] src_addr, byte[] dst_addr, short type) {
        EthernetPacket packet = new EthernetPacket();
        packet.frametype = type;  //选择以太包类型
        packet.src_mac = src_addr;
        packet.dst_mac = dst_addr;
        return packet;
    }

    /**
     * 获取arp请求包
     *
     * @param src
     * @param dst
     * @return
     * @throws UnknownHostException
     */
    public static ARPPacket getArpReqPacket(IpMacPair src, IpMacPair dst, boolean genEth) throws UnknownHostException {

        ARPPacket packet = getArppacket(src, dst, genEth, EthernetPacket.ETHERTYPE_ARP);
        packet.operation = 0x01;
        return packet;
    }

    /**
     * 获取arp响应包
     *
     * @param src
     * @param dst
     * @return
     * @throws UnknownHostException
     */
    public static ARPPacket getArpRepPacket(IpMacPair src, IpMacPair dst, boolean genEth) throws UnknownHostException {

        ARPPacket packet = getArppacket(src, dst, genEth, EthernetPacket.ETHERTYPE_ARP);
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
    private static ARPPacket getArppacket(IpMacPair src, IpMacPair dst, boolean genEth, short type) throws UnknownHostException {

        ARPPacket packet = new ARPPacket();
        packet.hardtype = ARPPacket.HARDTYPE_ETHER;
        packet.prototype = ARPPacket.PROTOTYPE_IP;
        packet.hlen = 6;
        packet.plen = 4;
        packet.operation = -1; // 请求包与答应包分别为1,2

        packet.sender_hardaddr = macStrToByte(src.getMac());
        packet.sender_protoaddr = InetAddress.getByName(src.getIp()).getAddress();
        packet.target_hardaddr = macStrToByte(dst.getMac());
        packet.target_protoaddr = InetAddress.getByName(dst.getIp()).getAddress();

        if (genEth) {
            packet.datalink = genEthpacket(packet.sender_hardaddr, packet.target_hardaddr, type);
        }
        return packet;
    }


    /**
     * 获取当前ip对网段内所有广播Arp包
     *
     * @param localIpMacPair
     * @return
     */
    public static List<ARPPacket> genBrocastArp(IpMacPair localIpMacPair, boolean isReq) throws UnknownHostException {

        //1、源ip
        IpMacPair srcIpMac = localIpMacPair;

        //2、目的ip
        String ip0 = localIpMacPair.getIp();
        String subNet = localIpMacPair.getSubNet();
        List<String> ips = SystemUtil.getLanIps(ip0, subNet, false, false);
        List<IpMacPair> pairs = new ArrayList<IpMacPair>();
        for (String ip : ips) {
            IpMacPair dstIpMac = new IpMacPair();
            dstIpMac.setIp(ip);
            dstIpMac.setMac(Globalvariables.MAC_NETWORK_BROCAST);
            dstIpMac.setSubNet(localIpMacPair.getSubNet());

            pairs.add(dstIpMac);
        }
        return genMultiArps(localIpMacPair, pairs, isReq);
    }


    /**
     * 产生多个arp包（如组播）
     *
     * @param localIpMacPair
     * @param ipMacPairs
     * @param isReq          true arp请求， false arp 响应
     * @return
     * @throws UnknownHostException
     */
    public static List<ARPPacket> genMultiArps(IpMacPair localIpMacPair, List<IpMacPair> ipMacPairs, boolean isReq) throws UnknownHostException {

        //1、源ip
        IpMacPair srcIpMac = localIpMacPair;

        //2、目的ip
        List<ARPPacket> arpPacketList = new ArrayList<ARPPacket>();
        for (IpMacPair pair : ipMacPairs) {
            ARPPacket arpPacket = null;
            if (isReq) {
                arpPacket = PacketGener.getArpReqPacket(srcIpMac, pair, false);
            } else {
                arpPacket = PacketGener.getArpRepPacket(srcIpMac, pair, false);
            }
            arpPacket.datalink = PacketGener.genEthpacket(macStrToByte(srcIpMac.getMac()), macStrToByte(pair.getMac()), EthernetPacket.ETHERTYPE_ARP);
            if (arpPacket != null) {
                arpPacketList.add(arpPacket);
            }
        }
        return arpPacketList;
    }
}
