package org.jasic.qzoner.core.handler;
import jpcap.PacketReceiver;
import jpcap.packet.ARPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public class HandlerDispatcher implements PacketReceiver {

    private AHandler arpHandler;
    private AHandler tcpHandler;
    private AHandler udpHandler;

    public HandlerDispatcher() {

        this.init();
    }

    private void init() {
        this.arpHandler = new ArpHandler();
        this.tcpHandler = new TCPHandler();
        this.udpHandler = new UDPHandler();
    }

    public void dispatch(Packet packet) {

        if (packet instanceof ARPPacket) {
            this.arpHandler.handle(packet);
        } else if (packet instanceof TCPPacket) {
            this.tcpHandler.handle(packet);
        } else if (packet instanceof UDPPacket) {
            this.udpHandler.handle(packet);
        } else {
            throw new RuntimeException("The type[" + packet.getClass().getSimpleName() + "] is not supported now~");
        }
    }

    @Override
    public void receivePacket(Packet packet) {
        dispatch(packet);
    }
}
