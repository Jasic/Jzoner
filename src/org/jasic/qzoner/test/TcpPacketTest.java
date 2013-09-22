package org.jasic.qzoner.test;
import cn.tisson.framework.interrupt.InterruptHandler;
import org.junit.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * User: Jasic
 * Date: 13-9-14
 */
public class TcpPacketTest {


    @Test
    public void testServer() {
        new Server().start();
    }

    @Test
    public void testClient() {
        new Client().start();
    }

    class Client {

        public void start() {
            Socket socket = null;
            try {
                socket = new Socket("192.168.1.103", 4444);


//             socket = new Socket();
//            socket.bind(new InetSocketAddress("127.0.0.1", 4444));
                while (true) {
                    String req = "Client sending..";
                    OutputStreamWriter ow = new OutputStreamWriter(socket.getOutputStream());
                    ow.write(req);
                    ow.flush();

                    InputStreamReader ir = new InputStreamReader(socket.getInputStream());
                    BufferedReader br = new BufferedReader(ir);

                    String line = null;
                    while (line != null) {
                        line = br.readLine();
                        System.out.println(line);
                        line = br.readLine();
                    }


                    String rep = new String();
                    System.out.println(rep);
                    InterruptHandler.Sleep(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class Server {

        private ServerSocket ss;

        public Server() {
            try {
                this.ss = new ServerSocket(4444);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void start() {

            while (true) {
                try {
                    final Socket socket = ss.accept();
                    new Thread() {
                        public void run() {
                            try {
                                InputStream in = socket.getInputStream();
                                InputStreamReader isr = new InputStreamReader(in);
                                BufferedReader reader = new BufferedReader(isr);

                                String line = null;
                                line = reader.readLine();
                                while (line != null) {
                                    System.out.println(line);
                                    OutputStream os = socket.getOutputStream();
                                    OutputStreamWriter osw = new OutputStreamWriter(os);
                                    osw.write("Server receive[" + line + "] success!\n\n");
                                    osw.flush();
                                    line = reader.readLine();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


}
