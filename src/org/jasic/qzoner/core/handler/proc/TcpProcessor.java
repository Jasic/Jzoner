package org.jasic.qzoner.core.handler.proc;
import jpcap.packet.TCPPacket;
import org.jasic.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * User: Jasic
 * Date: 13-9-18
 */
public class TcpProcessor extends AProcessor<TCPPacket> {
    private final static Logger logger = LoggerFactory.getLogger(TcpProcessor.class);

    @Override
    public void doProcess(TCPPacket tcpPacket) {
        logger.info(StringUtils.mapToString(StringUtils.fieldval2Map(tcpPacket, TCPPacket.class, 2)));
    }
}
