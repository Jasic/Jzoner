package org.jasic.qzoner.core.handler;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;
import org.jasic.qzoner.core.handler.proc.AProcessor;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public class IpHandler extends AHandler {

    private AProcessor<TCPPacket> tcpProc;
    private AProcessor<UDPPacket> udpProc;

    @Override
    public void handle(Packet packet) {

        if (packet instanceof TCPPacket) {
            tcpProc.process((TCPPacket) packet);
        } else if (packet instanceof UDPPacket) {
            udpProc.process((UDPPacket) packet);
        }


        //TODO Modify packet and send

    }
}
