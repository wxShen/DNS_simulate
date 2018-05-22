package com.company;

import com.company.Dns.*;
import com.company.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.EventQueue;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import static com.company.util.Util.revfail;

public class TestDns extends JFrame{

    private JTextField txtDns;
    private JTextField Domain;
    private JTextField dns_ip;
    //private final Dnsdefine result = null;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    TestDns frame = new TestDns();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public TestDns() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 419);
        getContentPane().setLayout(new BorderLayout(0, 0));

        txtDns = new JTextField();
        txtDns.setHorizontalAlignment(SwingConstants.CENTER);
        txtDns.setText("Dns \u89E3\u6790\u670D\u52A1");
        getContentPane().add(txtDns, BorderLayout.NORTH);
        txtDns.setColumns(10);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        JLabel lblDomain = new JLabel("Domain:");
        lblDomain.setFont(new Font("Consolas", Font.PLAIN, 14));
        lblDomain.setBounds(73, 33, 66, 25);
        panel.add(lblDomain);

        Domain = new JTextField();
        Domain.setFont(new Font("Consolas", Font.PLAIN, 13));
        Domain.setBounds(163, 34, 175, 21);
        Domain.setText("www.baidu.com");
        panel.add(Domain);
        Domain.setColumns(10);

        dns_ip = new JTextField();
        dns_ip.setFont(new Font("Consolas", Font.PLAIN, 13));
        dns_ip.setColumns(10);
        dns_ip.setBounds(163, 76, 175, 21);
        dns_ip.setText("101.226.4.6");
        panel.add(dns_ip);


        JLabel label = new JLabel("dns_ip:");
        label.setFont(new Font("Consolas", Font.PLAIN, 14));
        label.setBounds(73, 74, 66, 25);
        panel.add(label);


        JRadioButton rev_btn = new JRadioButton("\u9012\u5F52\u67E5\u8BE2");
        rev_btn.setSelected(true);
        rev_btn.setBounds(91, 120, 94, 23);
        panel.add(rev_btn);

        JRadioButton ite_btn = new JRadioButton("\u8FED\u4EE3\u67E5\u8BE2");
        ite_btn.setBounds(217, 120, 86, 23);
        panel.add(ite_btn);

        ButtonGroup group=new ButtonGroup();
        group.add(rev_btn);
        group.add(ite_btn);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(73, 149, 265, 132);
        panel.add(scrollPane);


        JTextArea result = new JTextArea();
        scrollPane.setViewportView(result);
        //setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{getContentPane()}));


        JButton btnNext = new JButton("next");
        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //获取当前的域名,以及服务器
                String domin = Domain.getText();
                String dnsserver = dns_ip.getText();
                if (rev_btn.isSelected()) {
                    //递归查询查询
                    System.out.println(domin);
                    System.out.println(dnsserver);
                    LocalDnsBuf dnsBuf = new LocalDnsBuf();
                    List<Map<String,String>> buf = dnsBuf.ReadBuf(domin);
                    String testres = "";
                    if (buf.size() < 1){
                        //获取数据
                        System.out.println("本地dns：");
                        Dnsdefine dnsdefine = revQuery(domin,dnsserver);
                       testres = dnsdefine.printfinfo();
                       for (int i =0;i <dnsdefine.getHead().getAnswerRRs();++i ){
                           //将所有查询的记录缓存到文件中
                           dnsBuf.WirtednsBuf(domin,Util.getKey(dnsdefine.getAnswerRRs()[i].getType()),dnsdefine.getAnswerRRs()[i].getData());
                           System.out.println("write "+ Util.getKey(dnsdefine.getAnswerRRs()[i].getType())+dnsdefine.getAnswerRRs()[i].getData());
                       }
                    }else{
                        System.out.println("bufsize:" + buf.size() + "缓存数据：");
                        for (int i =0;i<buf.size();i++){
                            Map<String,String > map = buf.get(i);
                            System.out.println(map.toString());
                            testres += map.get(domin) + "\n";
                            System.out.println("getdata:  "+testres);
                        }
                    }
                    result.setText(testres);
                    }
                if (ite_btn.isSelected()){
                    //迭代查询
                    System.out.println(domin);
                    System.out.println(dnsserver);
                    //获取数据
                    Dnsdefine dnsdefine = IterQuery(domin,dnsserver);;
                    result.setText(dnsdefine.printfinfo());
                    if (dnsdefine.getHead().getAnswerRRs() > 0){
                        if (Util.getType("CNAME") == dnsdefine.getAnswerRRs()[0].getType()){
                            String  newDoamin = dnsdefine.getAnswerRRs()[0].getData();
                            String ipdnsString = dnsdefine.getAddtitionalRRs()[0].getData();
                            Domain.setText(newDoamin);
                            dns_ip.setText(ipdnsString);
                        }
                    }
                }
            }
        });


        btnNext.setFont(new Font("Consolas", Font.PLAIN, 14));
        btnNext.setBounds(269, 313, 93, 23);
        panel.add(btnNext);

    }
    public static Dnsdefine revQuery(String domin,String HostAddress){

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
        Dnsdefine result = null;
       /* Thread t = new Thread(){

            @Override
            public void run() {
                //子线程访问网络*/
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
                   /* int  port = recdp.getPort();

                    System.out.println(ip +":"+ port +"----->"+ recdp.getData() +" -----len"+recdp.getData().length);
                   */
            String data1 = new String(recdp.getData(),0,recdp.getData().length);
            System.out.println(Util.byte2hex(data1.getBytes()));

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
            result = new Dnsdefine(RecvHead,RecvQue);
            /*---------------开始解析answer部分---------------*/
            curLen += QueriesLen; //记录当前获得的应该获取byte
            RR[] rr = new RR[RecvHead.getAnswerRRs()];
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
            if (result!=null)
                System.out.println(result.printfinfo());
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
                    String nsserver = Util.revfail(Util.trandNs(ans_data,recdp.getData()));
                    auto[i].setData(nsserver);
                    System.out.println(nsserver+"         "+nsserver.length());
                }
                curLen += 12 + auto[i].getDatalength();
            }
            if (auto!=null)
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
            if (addition!=null)
                result.setAddtitionalRRs(addition);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static  Dnsdefine IterQuery(String domin,String HostAddress){
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
        Dnsdefine result = null;
        /*Thread t = new Thread(){

            @Override
            public void run() {
            */
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
            int  port1 = recdp.getPort();
            String data1 = new String(recdp.getData(),0,recdp.getData().length);
            System.out.println(ip +":"+ port1 +"----->"+ recdp.getData() +" -----len"+recdp.getData().length);
            System.out.println(Util.byte2hex(data1.getBytes()));

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
            result = new Dnsdefine(RecvHead,RecvQue);
            /*---------------开始解析answer部分---------------*/
            curLen += QueriesLen; //记录当前获得的应该获取byte
            RR[] rr = new RR[RecvHead.getAnswerRRs()];
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
            if (rr!=null)
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
                    String nsserver = Util.revfail(Util.trandNs(ans_data,recdp.getData()));
                    auto[i].setData(nsserver);
                    System.out.println(nsserver+"         "+nsserver.length());
                }
                curLen += 12 + auto[i].getDatalength();
            }
            if (auto!=null)
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
            if (addition!=null)
                result.setAddtitionalRRs(addition);
            System.out.println(result.printfinfo());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}


