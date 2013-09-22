package org.jasic.qzoner.core.handler.proc;
import jpcap.packet.TCPPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
/**
 * User: Jasic
 * Date: 13-9-18
 */
public class TcpProcessor extends AProcessor<TCPPacket> {
    private final static Logger logger = LoggerFactory.getLogger(TcpProcessor.class);
    private final static String logHeader = "[TCP处理]";

    @Override
    public void doProcess(TCPPacket tcpPacket) {

        byte[] date = tcpPacket.data;

        if (date.length > 0) {
            logger.info(logHeader + new String(date, Charset.forName("UTF-8")));
            logger.info(logHeader + tcpPacket);
        }
//        logger.info(logHeader + tcpPacket.data.length);
//
//
    }
}
