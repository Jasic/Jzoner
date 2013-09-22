package org.jasic.qzoner.core.handler;
import jpcap.PacketReceiver;
import jpcap.packet.ARPPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public class HandlerDispatcher implements PacketReceiver {

    private AHandler arpHandler;
    private AHandler ipHandler;

    public HandlerDispatcher() {

        this.init();
    }

    private void init() {
        this.arpHandler = new ArpHandler();
        this.ipHandler = new IpHandler();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void dispatch(Packet packet) {

        if (packet instanceof ARPPacket) {
            this.arpHandler.handle(packet);
        } else if (packet instanceof IPPacket) {
            this.ipHandler.handle(packet);
        } else {
            throw new RuntimeException("The type[" + packet.getClass().getSimpleName() + "] is not supported now~");
        }
    }

    @Override
    public void receivePacket(Packet packet) {
        dispatch(packet);
    }
}
