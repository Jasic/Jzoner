package org.jasic.qzoner.core.handler;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import org.jasic.qzoner.core.PacketSender;
import org.jasic.utils.ObjectUtils;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public class IpHandler extends AHandler {

    private PacketSender pSender;


    public IpHandler(PacketSender packetSender) {
        this.pSender = packetSender;
    }

    @Override
    public void handle(Packet packet) {

        if (packet instanceof TCPPacket) {
            TCPPacket tcpPacket = ObjectUtils.cloneObject(TCPPacket.class, null);

        }


        //TODO Modify packet and send

    }
}
