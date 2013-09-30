package org.jasic.qzoner.core.handler;
import jpcap.PacketReceiver;
import jpcap.packet.ARPPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import org.jasic.common.DefualtThreadFactory;
import org.jasic.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * User: Jasic
 * Date: 13-9-11
 */
public class HandlerDispatcher implements PacketReceiver {
    private static final Logger logger = LoggerFactory.getLogger(HandlerDispatcher.class);

    private AHandler arpHandler;
    private AHandler ipHandler;
    private ExecutorService es;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public HandlerDispatcher() {

        this.init();
    }

    private void init() {
        this.arpHandler = new ArpHandler();
        this.ipHandler = new IpHandler();
        this.es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new DefualtThreadFactory("Link包处理池"));

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
        }
//        else {
//            logger.info(packet.toString());
//            throw new RuntimeException("The type[" + packet.getClass().getSimpleName() + "] is not supported now~");
//        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void receivePacket(final Packet packet) {
        this.es.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dispatch(ObjectUtils.cloneObject(packet));
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.warn(e.getMessage());
                        }
                    }
                }
        );
    }
}
