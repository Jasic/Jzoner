package org.jasic.qzoner.test;
import jpcap.packet.ARPPacket;
import org.jasic.qzoner.common.Globalvariables;
import org.jasic.qzoner.core.PacketCaptor;
import org.jasic.qzoner.core.PacketSender;
import org.jasic.qzoner.core.entity.IpMacPair;
import org.jasic.qzoner.util.NetWorkUtil;
import org.jasic.qzoner.util.PacketGener;
import org.jasic.utils.TimeUtil;

import java.net.UnknownHostException;
import java.util.List;

import static org.jasic.utils.StringUtils.fieldval2Map;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public class TestCore {

    public static void main(String[] args) throws Throwable {


//        String gateWayIp = getGateWayIp("192.168.1.100");
//        System.out.println(gateWayIp);
//        String gateWayMac = getLanMacByIp(gateWayIp);
//        System.out.println(gateWayMac);
//        System.out.println(mapToString(getLanMacByIp(new String[]{"1.1.1.1",
//                "172.16.24.80", "172.16.26.21", "172.16.27.1", "224.0.0.251", "224.0.0.252", "230.0.0.3", "239.203.13.64", "239.255.255.251"})));


        new Thread() {
            @Override
            public void run() {
                PacketCaptor captor = new PacketCaptor(new IpMacPair("", Globalvariables.MAC_LOCAL_ETH_0));
                captor.startCaptor();
            }
        }.start();
        sendNormalBrocasArp();
    }

    public static void sendNormalBrocasArp() {

        String mac = Globalvariables.MAC_LOCAL_ETH_0;

        PacketSender sender = new PacketSender(new IpMacPair("", mac));

        while (true) {
            try {
                IpMacPair ipMacPair = new IpMacPair(NetWorkUtil.getIpByMac(mac), mac);
                ipMacPair.setSubNet(NetWorkUtil.getSubNetByMac(mac));
                List<ARPPacket> arpPackets = PacketGener.genBrocastArp(ipMacPair, true);
                for (ARPPacket arpPacket : arpPackets) {
                    sender.send(arpPacket);
                    System.out.println("Send-->" + fieldval2Map(arpPacket, arpPacket.getClass(), 3));
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            TimeUtil.sleep(20);
        }
    }
}
