package com.company;

import com.company.Dns.Dnsdefine;
import com.company.Dns.Head;
import com.company.Dns.Queries;
import com.company.Dns.RR;
import com.company.util.Util;
import sun.security.krb5.internal.HostAddress;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {

    public static String revQuery(String domin,String HostAddress){

        Random rand = new Random();
        int port = rand.nextInt(48000)+1024;
        /* String HostAddress = "198.41.0.4"; //域名服务器*/
        //顶级域名：198.41.0.4  192.5.6.30   202.108.22.220  61.135.165.224
        Util.init();
        //生成需要发送的额数据
        short transid = (short)rand.nextInt(1000);
        Head head = new Head(transid,(short)0x0100,(short)1,(short)0,(short)0,(short)0);
        Queries queries = new Queries(domin,"A");
        final Dnsdefine data = new Dnsdefine(head,queries);
        byte[] msg = data.Sendmsgbyte();
        System.out.println("----------数据准备完毕 开始发送udp--------------");
        byte[] rec = new byte[1024];
        Dnsdefine result;
        Thread t = new Thread(){

            @Override
            public void run() {
                //子线程访问网络
                try {
                    DatagramSocket ds = new DatagramSocket(port);
                    //向dns 发送报文
                    DatagramPacket senddp = new DatagramPacket(msg,msg.length,InetAddress.getByName(HostAddress),53);
                    ds.send(senddp);
                    System.out.println(senddp.getAddress().getHostAddress() +"         "+senddp.getPort() +"         "+senddp.getData().toString());

                    //接受报文
                    DatagramPacket recdp = new DatagramPacket(rec,rec.length);
                    ds.receive(recdp);;
                    String ip = recdp.getAddress().getHostAddress();
                    int  port = recdp.getPort();
                    String data = new String(recdp.getData(),0,recdp.getData().length);
                    System.out.println(ip +":"+ port +"----->"+ recdp.getData() +" -----len"+recdp.getData().length);
                    System.out.println(Util.byte2hex(data.getBytes()));

                    //开始头部解析事件
                    int HeadLen = head.getHead().length;
                    int QueriesLen = queries.getQueries().length;
                    int domianlen = queries.getNameLen();
                    int curLen = 0;

                    byte[] recHead = new byte[head.getHead().length];
                    System.arraycopy(recdp.getData(), 0, recHead , 0, head.getHead().length);
                    Head RecvHead = new Head(recHead);
                    System.out.println("head: "+ Util.byte2hex(RecvHead.getHead()));
                    System.out.println("Questions:" + RecvHead.getQuestions());
                    System.out.println("AnswerRRs: "+RecvHead.getAnswerRRs());
                    System.out.println("AnswerRRs: "+RecvHead.getAuthorityRRs());
                    System.out.println("AnswerRRs: "+RecvHead.getAddtitionalRRs());
                    curLen = HeadLen;

                    /*----------------------开始解析queries----------------------*/
                    byte[] recQuerise = new byte[QueriesLen];
                    System.arraycopy(recdp.getData(), curLen, recQuerise , 0, QueriesLen);
                    Queries RecvQue = new Queries(recQuerise);

                    //存储结果
                    Dnsdefine result = new Dnsdefine(RecvHead,RecvQue);
                    /*---------------开始解析answer部分---------------*/
                    curLen += QueriesLen; //记录当前获得的应该获取byte
                    RR[] rr = new RR[3];
                    for (int i=0;i< RecvHead.getAnswerRRs();i++){
                        //对每一个answer解析
                        byte[] Answer = new byte[200];
                        System.arraycopy(recdp.getData(),curLen,Answer,0,12);
                        rr[i] = new RR(Answer);
                        byte[] ans_data = new byte[rr[i].getDatalength()];
                        System.arraycopy(recdp.getData(),curLen+12,ans_data,0,rr[i].getDatalength());
                        if (rr[i].getType() == Util.getType("CNAME")){
                            String newDomin = Util.transfer(ans_data,queries.getName().getBytes());
                            //System.out.println(newDomin);
                            rr[i].setData(newDomin);
                        }else if (rr[i].getType() == Util.getType("A")){
                            String addr = Util.getAddr(ans_data);
                            //System.out.println(addr);
                            rr[i].setData(addr);
                        }else if (rr[i].getType() == Util.getType("AAAA")){
                            //System.out.println(i + ":   ipv6_data:" + Util.byte2hex(ans_data));
                            //System.out.println();
                            String addr6 = Util.getAddr(ans_data);
                            rr[i].setData(addr6);
                        }
                        curLen += 12 + rr[i].getDatalength();
                    }
                    result.setAnswerRRs(rr);
                    System.out.println(result.printfinfo());

                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        t.start();
        return null;
    }


    public String IterQuery(String domin,String HostAddress){
        Random rand = new Random();
        int port = rand.nextInt(48000)+1024;
        /* String HostAddress = "198.41.0.4"; //域名服务器*/
        //顶级域名：198.41.0.4  192.5.6.30   202.108.22.220  61.135.165.224
        Util.init();
        //生成需要发送的额数据
        short transid = (short)rand.nextInt(1000);
        Head head = new Head(transid,(short)0x0100,(short)1,(short)0,(short)0,(short)0);
        Queries queries = new Queries(domin,"A");
        final Dnsdefine data = new Dnsdefine(head,queries);
        byte[] msg = data.Sendmsgbyte();
        System.out.println("----------数据准备完毕 开始发送udp--------------");
        byte[] rec = new byte[1024];
        Dnsdefine result;
        Thread t = new Thread(){

            @Override
            public void run() {
                //子线程访问网络
                try {
                    DatagramSocket ds = new DatagramSocket(port);
                    //向dns 发送报文
                    DatagramPacket senddp = new DatagramPacket(msg,msg.length,InetAddress.getByName(HostAddress),53);
                    ds.send(senddp);
                    System.out.println(senddp.getAddress().getHostAddress() +"         "+senddp.getPort() +"         "+senddp.getData().toString());

                    //接受报文
                    DatagramPacket recdp = new DatagramPacket(rec,rec.length);
                    ds.receive(recdp);;
                    String ip = recdp.getAddress().getHostAddress();
                    int  port = recdp.getPort();
                    String data = new String(recdp.getData(),0,recdp.getData().length);
                    System.out.println(ip +":"+ port +"----->"+ recdp.getData() +" -----len"+recdp.getData().length);
                    System.out.println(Util.byte2hex(data.getBytes()));

                    //开始头部解析事件
                    int HeadLen = head.getHead().length;
                    int QueriesLen = queries.getQueries().length;
                    int domianlen = queries.getNameLen();
                    int curLen = 0;

                    byte[] recHead = new byte[head.getHead().length];
                    System.arraycopy(recdp.getData(), 0, recHead , 0, head.getHead().length);
                    Head RecvHead = new Head(recHead);
                    System.out.println("head: "+ Util.byte2hex(RecvHead.getHead()));
                    System.out.println("Questions:" + RecvHead.getQuestions());
                    System.out.println("AnswerRRs: "+RecvHead.getAnswerRRs());
                    System.out.println("AnswerRRs: "+RecvHead.getAuthorityRRs());
                    System.out.println("AnswerRRs: "+RecvHead.getAddtitionalRRs());
                    curLen = HeadLen;

                    /*----------------------开始解析queries----------------------*/
                    byte[] recQuerise = new byte[QueriesLen];
                    System.arraycopy(recdp.getData(), curLen, recQuerise , 0, QueriesLen);
                    Queries RecvQue = new Queries(recQuerise);

                    //存储结果
                    Dnsdefine result = new Dnsdefine(RecvHead,RecvQue);
                    /*---------------开始解析answer部分---------------*/
                    curLen += QueriesLen; //记录当前获得的应该获取byte
                    RR[] rr = new RR[3];
                    for (int i=0;i< RecvHead.getAnswerRRs();i++){
                        //对每一个answer解析
                        byte[] Answer = new byte[200];
                        System.arraycopy(recdp.getData(),curLen,Answer,0,12);
                        rr[i] = new RR(Answer);
                        byte[] ans_data = new byte[rr[i].getDatalength()];
                        System.arraycopy(recdp.getData(),curLen+12,ans_data,0,rr[i].getDatalength());
                        if (rr[i].getType() == Util.getType("CNAME")){
                            String newDomin = Util.transfer(ans_data,queries.getName().getBytes());
                            //System.out.println(newDomin);
                            rr[i].setData(newDomin);
                        }else if (rr[i].getType() == Util.getType("A")){
                            String addr = Util.getAddr(ans_data);
                            //System.out.println(addr);
                            rr[i].setData(addr);
                        }else if (rr[i].getType() == Util.getType("AAAA")){
                            //System.out.println(i + ":   ipv6_data:" + Util.byte2hex(ans_data));
                            //System.out.println();
                            String addr6 = Util.getAddr(ans_data);
                            rr[i].setData(addr6);
                        }
                        curLen += 12 + rr[i].getDatalength();
                    }
                    result.setAnswerRRs(rr);
                    //data.setAddtitionalRRs();
                    //开始解析 authorityRRs
                    System.out.println("----------------开始解析 authorityRRs--------------");
                    RR[] auto = new RR[RecvHead.getAuthorityRRs()];
                    for (int i=0;i< RecvHead.getAuthorityRRs();i++){
                        //对每一个answer解析
                        byte[] Answer = new byte[200];
                        System.arraycopy(recdp.getData(),curLen,Answer,0,12);
                        //System.out.println("截取answer："+ Util.byte2hex(Answer));
                        auto[i] = new RR(Answer);
                        //System.out.println("ans: "+ Util.byte2hex(auto[i].getinfoByte()));
                        //System.out.println(rr[i].getDatalength());
                        byte[] ans_data = new byte[auto[i].getDatalength()];
                        System.arraycopy(recdp.getData(),curLen+12,ans_data,0,auto[i].getDatalength());
                        //System.out.println(Util.byte2hex(ans_data));
                        //System.out.println(Util.bytetoSting(ans_data));
                        if (auto[i].getType() == Util.getType("CNAME")){
                            String newDomin = Util.transfer(ans_data,queries.getName().getBytes());
                            //System.out.println(newDomin);
                            auto[i].setData(newDomin);

                        }else if (auto[i].getType() == Util.getType("A")){
                            String addr = Util.getAddr(ans_data);
                            auto[i].setData(addr);
                            //System.out.println(addr);
                        }else if(auto[i].getType() == Util.getType("NS")){
                            //对地柜解析过程中得到的nS类型数据的解析
                            //System.out.println("ns_data:" + Util.byte2hex(ans_data));
                            String nsserver = Util.trandNs(ans_data,recdp.getData());
                            auto[i].setData(nsserver);
                           // System.out.println(nsserver);
                        }
                        curLen += 12 + auto[i].getDatalength();
                    }
                    result.setAuthorityRRs(auto);

                    System.out.println("----------------开始解析additionnalRRs--------------");
                    RR[] addition = new RR[RecvHead.getAddtitionalRRs()];
                    for (int i=0;i< RecvHead.getAddtitionalRRs();i++){
                        //对每一个answer解析
                        byte[] Answer = new byte[200];
                        System.arraycopy(recdp.getData(),curLen,Answer,0,12);
                        //System.out.println("截取answer："+ Util.byte2hex(Answer));
                        addition[i] = new RR(Answer);
                        //System.out.println("ans: "+ Util.byte2hex(addition[i].getinfoByte()));
                        //System.out.println(rr[i].getDatalength());
                        byte[] ans_data = new byte[addition[i].getDatalength()];
                        System.arraycopy(recdp.getData(),curLen+12,ans_data,0,addition[i].getDatalength());
                        //System.out.println(Util.byte2hex(ans_data));
                        //System.out.println(Util.bytetoSting(ans_data));
                        if (addition[i].getType() == Util.getType("CNAME")){
                            String newDomin = Util.transfer(ans_data,queries.getName().getBytes());
                            System.out.println(newDomin);
                            addition[i].setData(newDomin);
                        }else if (addition[i].getType() == Util.getType("A")){
                            System.out.println(i + ":   ip4_addr:"+Util.byte2hex(ans_data));
                            String addr = Util.getAddr(ans_data);
                            System.out.println(addr);
                            addition[i].setData(addr);
                        }else if(addition[i].getType() == Util.getType("NS")){
                            //对地柜解析过程中得到的nS类型数据的解析
                            System.out.println(i + ":   ns_data:" + Util.byte2hex(ans_data));
                            String nsserver = Util.trandNs(ans_data,recdp.getData());
                            System.out.println(nsserver);
                            addition[i].setData(nsserver);
                        }else if (addition[i].getType() == Util.getType("AAAA")){
                            System.out.println(i + ":   ipv6_data:" + Util.byte2hex(ans_data));
                            System.out.println(Util.getAddr6(ans_data));
                            addition[i].setData(Util.getAddr6(ans_data));
                        }
                        curLen += 12 + addition[i].getDatalength();
                    }
                    result.setAddtitionalRRs(addition);
                    System.out.println(result.printfinfo());
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        t.start();
        return null;
    }

    public static void main(String[] args) {
/*	// write your code here
        byte [] ba = {1, 2, 3, 4};
        System.out.println(ba[0]+" --> "+ba[1]+" --> "+ba[2]+" --> "+ba[3]+" --> ");
// 4 bytes to int
        int in = ByteBuffer.wrap(ba).order(ByteOrder.LITTLE_ENDIAN).getInt();
        System.out.println(in);

        byte [] ab = null;

// int to 4 bytes method 1
        ab = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(in).array();
        System.out.println(ab[0]+" --> "+ab[1]+" --> "+ab[2]+" --> "+ab[3]+" --> ");


        // int to 4 bytes method 2
        ab = new byte[4];
        ByteBuffer.wrap(ab).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().put(in);
        System.out.println(ab[0]+" --> "+ab[1]+" --> "+ab[2]+" --> "+ab[3]+" --> ");

        short t = 0x0010;
        System.out.println(t);
        byte[] test = Util.shortToBytes(t);
        short ans = Util.byteCattoShort(test);
        System.out.println(ans);

        short x = -32752;//定义一个short
        byte high = (byte) (0x00FF & (x>>8));//定义第一个byte
        byte low = (byte) (0x00FF & x);//定义第二个byte
        System.out.println(high);//打印第一个byte值
        System.out.println(low);//打印第二个byte值
        // 复原
        short z = (short)(((high & 0x00FF) << 8) | (0x00FF & low));
        System.out.println(z);//输出的结果就是-32752*/

        String domin = "ded.nuaa.edu.cn";//请求的域名  //www.a.shifen.com
        Random rand = new Random();
        int port = rand.nextInt(48000)+1024;
        String HostAddress = "198.41.0.4"; //域名服务器
                                            //顶级域名：198.41.0.4  192.5.6.30   202.108.22.220  61.135.165.224

        //开始准备数据
        Util.init();
        //生成需要发送的额数据
        short transid = (short)rand.nextInt(1000);
        Head head = new Head(transid,(short)0x0100,(short)1,(short)0,(short)0,(short)0);
        System.out.println("head:  ");
        System.out.println(Util.byte2hex(head.getHead()));

        Queries queries = new Queries(domin,"A");
        System.out.println("queries:");
        System.out.println(Util.byte2hex(queries.getQueries()));

        final Dnsdefine data = new Dnsdefine(head,queries);
        byte[] msg = data.Sendmsgbyte();



        System.out.println("--------------------------------------");
        System.out.println("final packet:  "+ Util.byte2hex(msg));
        //将byte[] 转化为String
        //Base64 Encoded
        String encoded = Base64.getEncoder().encodeToString(msg);
        //Base64 Decoded
        byte[] decoded = Base64.getDecoder().decode(encoded);
        //Verify original content
        System.out.println( new String(decoded) );


        byte[] rec = new byte[1024];
        System.out.println("----------数据准备完毕 开始发送udp--------------");
        //数据准备完毕 开始发送udp

/*        Thread t = new Thread(){
            @Override
            public void run() {
                //子线程访问网络
                try {
                    DatagramSocket ds = new DatagramSocket(port);
                    //向dns 发送报文
                    System.out.println("send befor:   "+Util.bytetoSting(msg));
                    //DatagramPacket senddp = new DatagramPacket(msg,msg.length,InetAddress.getByName(HostAddress),53);
                    DatagramPacket senddp = new DatagramPacket(msg,msg.length,InetAddress.getByName("101.226.4.6"),53);
                    ds.send(senddp);
                    *//*
                     * test
                     * *//*
                    System.out.println(senddp.getAddress().getHostAddress());
                    System.out.println(senddp.getPort());
                    System.out.println(senddp.getData().toString() +"         "+senddp.getData().length);

                    //接受报文
                    DatagramPacket recdp = new DatagramPacket(rec,rec.length);
                    System.out.println("star recive");
                    ds.receive(recdp);
                    System.out.println("end recive");
                    String ip = recdp.getAddress().getHostAddress();
                    int  port = recdp.getPort();
                    String data = new String(recdp.getData(),0,recdp.getData().length);
                    System.out.println(ip +":"+ port +"----->"+ recdp.getData() +" -----len"+recdp.getData().length);
                    System.out.println(data+"----------len"+data.length());
                    System.out.println(Util.byte2hex(data.getBytes()));

                    //开始头部解析事件
                    int HeadLen = head.getHead().length;
                    int QueriesLen = queries.getQueries().length;
                    int domianlen = queries.getNameLen();
                    int curLen = 0;

                    byte[] recHead = new byte[head.getHead().length];
                    System.arraycopy(recdp.getData(), 0, recHead , 0, head.getHead().length);
                    Head RecvHead = new Head(recHead);
                    System.out.println("head: " + Util.byte2hex(recHead));
                    System.out.println("head: "+ Util.byte2hex(RecvHead.getHead()));
                    System.out.println("Questions:" + RecvHead.getQuestions());
                    System.out.println("AnswerRRs: "+RecvHead.getAnswerRRs());
                    System.out.println("AnswerRRs: "+RecvHead.getAuthorityRRs());
                    System.out.println("AnswerRRs: "+RecvHead.getAddtitionalRRs());

                    System.out.println("截取后剩余的： "+Util.byte2hex(recdp.getData()));


                    *//*System.out.println("-----------------------------------------");
                    byte[] test = Util.intToByteArray(1152);
                    System.out.println(Util.byte2hex(test));//0000 0480
                    int ans = Util.byteCattoInt(test);
                    System.out.println(ans);*//*

                    *//*---------------开始解析answer部分---------------*//*
                    curLen = HeadLen+QueriesLen; //记录当前获得的应该获取byte
                    RR[] rr = new RR[3];
                    for (int i=0;i< RecvHead.getAnswerRRs();i++){
                        //对每一个answer解析
                        byte[] Answer = new byte[200];
                        System.arraycopy(recdp.getData(),curLen,Answer,0,12);
                        //System.out.println("截取answer："+ Util.byte2hex(Answer));
                        rr[i] = new RR(Answer);
                        System.out.println("ans: "+ Util.byte2hex(rr[i].getinfoByte()));
                        //System.out.println(rr[i].getDatalength());
                        byte[] ans_data = new byte[rr[i].getDatalength()];
                        System.arraycopy(recdp.getData(),curLen+12,ans_data,0,rr[i].getDatalength());
                        //System.out.println(Util.byte2hex(ans_data));
                        //System.out.println(Util.bytetoSting(ans_data));
                        if (rr[i].getType() == Util.getType("CNAME")){
                            String newDomin = Util.transfer(ans_data,queries.getName().getBytes());
                            System.out.println(newDomin);
                        }else if (rr[i].getType() == Util.getType("A")){
                            String addr = Util.getAddr(ans_data);
                            System.out.println(addr);
                        }
                        curLen += 12 + rr[i].getDatalength();
                    }

                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();*/



        //迭代查询的过程
        Thread t2 = new Thread(){
            @Override
            public void run() {
                try {
                    DatagramSocket ds = new DatagramSocket(port);
                    //向dns 发送报文
                    System.out.println("send befor:   " + Util.bytetoSting(msg));
                    DatagramPacket senddp = new DatagramPacket(msg,msg.length,InetAddress.getByName(HostAddress),53);
                   //DatagramPacket senddp = new DatagramPacket(msg, msg.length, InetAddress.getByName("101.226.4.6"), 53);
                    ds.send(senddp);

                    //接受报文
                    DatagramPacket recdp = new DatagramPacket(rec, rec.length);
                    //System.out.println("star recive");
                    ds.receive(recdp);
                    //System.out.println("end recive");
                    String ip = recdp.getAddress().getHostAddress();
                    int port = recdp.getPort();
                    String data = new String(recdp.getData(), 0, recdp.getData().length);
                    /*System.out.println(ip + ":" + port + "----->" + recdp.getData() + " -----len" + recdp.getData().length);
                    System.out.println(data + "----------len" + data.length());*/
                    System.out.println(Util.byte2hex(data.getBytes()));
                    //开始头部解析事件
                    int HeadLen = head.getHead().length;
                    int QueriesLen = queries.getQueries().length;
                    int domianlen = queries.getNameLen();
                    int curLen = 0;

                    byte[] recHead = new byte[head.getHead().length];
                    System.arraycopy(recdp.getData(), 0, recHead , 0, head.getHead().length);
                    Head RecvHead = new Head(recHead);
                    System.out.println("head: " + Util.byte2hex(recHead));
                    System.out.println("head: "+ Util.byte2hex(RecvHead.getHead()));
                    System.out.println("Questions:" + RecvHead.getQuestions());
                    System.out.println("AnswerRRs: "+RecvHead.getAnswerRRs());
                    System.out.println("AnswerRRs: "+RecvHead.getAuthorityRRs());
                    System.out.println("AnswerRRs: "+RecvHead.getAddtitionalRRs());

                    System.out.println("截取后剩余的： "+Util.byte2hex(recdp.getData()));

                    //*---------------开始解析answer部分---------------*/
                    System.out.println("---------------开始解析answer部分---------------");
                    curLen = HeadLen+QueriesLen; //记录当前获得的应该获取byte
                    RR[] rr = new RR[RecvHead.getAnswerRRs()];
                    for (int i=0;i< RecvHead.getAnswerRRs();i++){
                        //对每一个answer解析
                        byte[] Answer = new byte[200];
                        System.arraycopy(recdp.getData(),curLen,Answer,0,12);
                        //System.out.println("截取answer："+ Util.byte2hex(Answer));
                        rr[i] = new RR(Answer);
                        System.out.println("ans: "+ Util.byte2hex(rr[i].getinfoByte()));
                        //System.out.println(rr[i].getDatalength());
                        byte[] ans_data = new byte[rr[i].getDatalength()];
                        System.arraycopy(recdp.getData(),curLen+12,ans_data,0,rr[i].getDatalength());
                        //System.out.println(Util.byte2hex(ans_data));
                        //System.out.println(Util.bytetoSting(ans_data));
                        if (rr[i].getType() == Util.getType("CNAME")){
                            String newDomin = Util.transfer(ans_data,queries.getName().getBytes());
                            System.out.println(newDomin);
                        }else if (rr[i].getType() == Util.getType("A")){
                            String addr = Util.getAddr(ans_data);
                            System.out.println(addr);
                        }
                        curLen += 12 + rr[i].getDatalength();
                    }

                    //data.setAddtitionalRRs();
                    //开始解析 authorityRRs
                    System.out.println("----------------开始解析 authorityRRs--------------");
                    RR[] auto = new RR[RecvHead.getAuthorityRRs()];
                    for (int i=0;i< RecvHead.getAuthorityRRs();i++){
                        //对每一个answer解析
                        byte[] Answer = new byte[200];
                        System.arraycopy(recdp.getData(),curLen,Answer,0,12);
                        //System.out.println("截取answer："+ Util.byte2hex(Answer));
                        auto[i] = new RR(Answer);
                        //System.out.println("ans: "+ Util.byte2hex(auto[i].getinfoByte()));
                        //System.out.println(rr[i].getDatalength());
                        byte[] ans_data = new byte[auto[i].getDatalength()];
                        System.arraycopy(recdp.getData(),curLen+12,ans_data,0,auto[i].getDatalength());
                        //System.out.println(Util.byte2hex(ans_data));
                        //System.out.println(Util.bytetoSting(ans_data));
                        if (auto[i].getType() == Util.getType("CNAME")){
                            String newDomin = Util.transfer(ans_data,queries.getName().getBytes());
                            System.out.println(newDomin);
                        }else if (auto[i].getType() == Util.getType("A")){
                            String addr = Util.getAddr(ans_data);
                            System.out.println(addr);
                        }else if(auto[i].getType() == Util.getType("NS")){
                            //对地柜解析过程中得到的nS类型数据的解析
                            System.out.println("ns_data:" + Util.byte2hex(ans_data));
                            String nsserver = Util.trandNs(ans_data,recdp.getData());
                            auto[i].setData(nsserver);
                            System.out.println(nsserver);
                        }
                        curLen += 12 + auto[i].getDatalength();
                    }

                    System.out.println("----------------开始解析additionnalRRs--------------");
                    RR[] addition = new RR[RecvHead.getAddtitionalRRs()];
                    for (int i=0;i< RecvHead.getAddtitionalRRs();i++){
                        //对每一个answer解析
                        byte[] Answer = new byte[200];
                        System.arraycopy(recdp.getData(),curLen,Answer,0,12);
                        //System.out.println("截取answer："+ Util.byte2hex(Answer));
                        addition[i] = new RR(Answer);
                        //System.out.println("ans: "+ Util.byte2hex(addition[i].getinfoByte()));
                        //System.out.println(rr[i].getDatalength());
                        byte[] ans_data = new byte[addition[i].getDatalength()];
                        System.arraycopy(recdp.getData(),curLen+12,ans_data,0,addition[i].getDatalength());
                        //System.out.println(Util.byte2hex(ans_data));
                        //System.out.println(Util.bytetoSting(ans_data));
                        if (addition[i].getType() == Util.getType("CNAME")){
                            String newDomin = Util.transfer(ans_data,queries.getName().getBytes());
                            System.out.println(newDomin);
                            addition[i].setData(newDomin);
                        }else if (addition[i].getType() == Util.getType("A")){
                            System.out.println(i + ":   ip4_addr:"+Util.byte2hex(ans_data));
                            String addr = Util.getAddr(ans_data);
                            System.out.println(addr);
                            addition[i].setData(addr);
                        }else if(addition[i].getType() == Util.getType("NS")){
                            //对地柜解析过程中得到的nS类型数据的解析
                            System.out.println(i + ":   ns_data:" + Util.byte2hex(ans_data));
                            String nsserver = Util.trandNs(ans_data,recdp.getData());
                            System.out.println(nsserver);
                            addition[i].setData(nsserver);
                        }else if (addition[i].getType() == Util.getType("AAAA")){
                            System.out.println(i + ":   ipv6_data:" + Util.byte2hex(ans_data));
                            System.out.println(Util.getAddr6(ans_data));
                            addition[i].setData(Util.getAddr6(ans_data));
                        }
                        curLen += 12 + addition[i].getDatalength();
                    }


                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        t2.start();

    }
}


