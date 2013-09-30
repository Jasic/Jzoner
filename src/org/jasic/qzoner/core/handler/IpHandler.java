package org.jasic.qzoner.core.handler;
import jpcap.packet.IPPacket;
import jpcap.packet.TCPPacket;
import org.jasic.qzoner.core.handler.proc.DefaultProcessor;
import org.jasic.qzoner.core.handler.proc.TcpProcessor;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public class IpHandler extends AHandler<IPPacket> {

    private TcpProcessor tcpProc;
    private DefaultProcessor defaultProc;

    public IpHandler() {
        this.tcpProc = new TcpProcessor();
        this.defaultProc = new DefaultProcessor();
    }

    @Override
    public void handle(IPPacket packet) {
        if (packet instanceof TCPPacket) {
            tcpProc.process((TCPPacket) packet);
        }
    }

    @Override
    public void after(IPPacket packet) {
        this.defaultProc.process(packet);
    }
}
