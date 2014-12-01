package com.hueemulator.server;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.hueemulator.emulator.Controller;

public class UPNPServer extends Thread {
    
    private static final int UPNP_PORT = 1901;
    public DatagramSocket socket;
    public boolean runUPNPServer=true;
    
    public Controller controller;
    
    // First implementation of a UPNP server.  Am not sure if this is the correct implementation but it seems to work.  The emulator is found by SDK bridge searches.
    // It will be improved if the implementation is faulty, as it could well be as this (java network stuff) not my strong point.
    public UPNPServer(Controller controller) {
        this.controller = controller;
        
        try {
            socket = new DatagramSocket(UPNP_PORT);
        } catch (SocketException e) {
              controller.addTextToConsole("Cound not run UPnP Server", Color.RED, true);
              System.out.println("Cound not run UPnP Server: " + e.getLocalizedMessage());
        }
    }
    
    public void run() {
        System.out.println("Starting Server");
        
        while (runUPNPServer) {
            try {
                byte[] buf = new byte[256];
                // don't wait for request...just send a quote

                String remoteIP = InetAddress.getLocalHost().getHostAddress();
                
                String msg;
                msg = "HTTP/1.1 200 OK\r\n" +
                "CACHE-CONTROL: max-age=100\r\n" +
                "EXT:\r\n" +
                "LOCATION: http://" + remoteIP + ":80/description.xml\r\n" +
                "SERVER: FreeRTOS/6.0.5, UPnP/1.0, IpBridge/0.1\r\n" +
                "ST: uuid:0FDD7736-722C-4995-89F2-ABCDEF000000\r\n" +
                "USN: uuid:0FDD7736-722C-4995-89F2-ABCDEF000000\r\n" +
                "\r\n";
                
                buf = msg.getBytes();

                InetAddress group = InetAddress.getByName("239.255.255.250");
                DatagramPacket packet;
                packet = new DatagramPacket(buf, buf.length, group, UPNP_PORT);
                socket.send(packet);

                try {
                    sleep(2000);  // Wait 2 seconds
                } 
                catch (InterruptedException e) { }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        socket.close();
    }
    
    public void startUPNPServer() {
        runUPNPServer=true;
        start();
    }
    
    public void stopUPNPServer() {
        runUPNPServer=false;
    }
    
}
