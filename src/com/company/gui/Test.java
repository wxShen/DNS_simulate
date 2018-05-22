package com.company.gui;

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

public class Test extends JFrame {
    private JTextField txtDns;
    private JTextField Domain;
    private JTextField dns_ip;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Test frame = new Test();
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
    public Test() {
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
        panel.add(Domain);
        Domain.setColumns(10);

        dns_ip = new JTextField();
        dns_ip.setFont(new Font("Consolas", Font.PLAIN, 13));
        dns_ip.setColumns(10);
        dns_ip.setBounds(163, 76, 175, 21);
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

        JTextPane txtpnResult = new JTextPane();
        scrollPane.setColumnHeaderView(txtpnResult);
        txtpnResult.setFont(new Font("Consolas", Font.PLAIN, 13));

        JTextArea result = new JTextArea();
        scrollPane.setViewportView(result);
        //setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{getContentPane()}));


        JButton btnNext = new JButton("next");
        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //获取当前的域名,以及服务器
                String doamin = Domain.getText();
                String dnsserver = dns_ip.getText();
                if (rev_btn.isSelected()) {
                    //迭代查询
                    System.out.println(doamin);
                    System.out.println(dnsserver);

                    String testres ="bsjafbbbbbasklaslkas\n============lkfoakfn,zmxncv\nnslajfnzmxd=====\nnvk;asfjnkSDfvzsdfbsjafbbbbbasklaslkas\n============lkfoakfn,zmxncv\nnslajfnzmxd=====\nnvk;asfjnkSDfvzsdfbsjafbbbbbasklaslkas\n============lkfoakfn,zmxncv\nnslajfnzmxd=====\nnvk;asfjnkSDfvzsdfbsjafbbbbbasklaslkas\n============lkfoakfn,zmxncv\nnslajfnzmxd=====\nnvk;asfjnkSDfvzsdfbsjafbbbbbasklaslkas\n============lkfoakfn,zmxncv\nnslajfnzmxd=====\nnvk;asfjnkSDfvzsdfbsjafbbbbbasklaslkas\n============lkfoakfn,zmxncv\nnslajfnzmxd=====\nnvk;asfjnkSDfvzsdfbsjafbbbbbasklaslkas\n============lkfoakfn,zmxncv\nnslajfnzmxd=====\nnvk;asfjnkSDfvzsdfbsjafbbbbbasklaslkas\n============lkfoakfn,zmxncv\nnslajfnzmxd=====\nnvk;asfjnkSDfvzsdf";
                    result.setText(testres);
                    String  newDoamin = "www.baidu.com";
                    String ipdnsString ="123.256.123.13";
                    Domain.setText(newDoamin);
                    dns_ip.setText(ipdnsString);
                }

            }
        });


        btnNext.setFont(new Font("Consolas", Font.PLAIN, 14));
        btnNext.setBounds(269, 313, 93, 23);
        panel.add(btnNext);

    }
}
