package com.hueemulator.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.conn.util.*;

import com.hueemulator.emulator.Controller;

public class UPNPServer extends Thread {
    
	private Logger log = LoggerFactory.getLogger(UPNPServer.class);
	private static final int UPNP_DISCOVERY_PORT = 1900;
	private static final String UPNP_MULTICAST_ADDRESS = "239.255.255.250";
	private int upnpResponsePort;

	private int httpServerPort;

	private String responseAddress;

    public boolean runUPNPServer=true;
    
    public Controller controller;

	private boolean traceupnp;
    
    // First implementation of a UPNP server.  Am not sure if this is the correct implementation but it seems to work.  The emulator is found by SDK bridge searches.
    // It will be improved if the implementation is faulty, as it could well be as this (java network stuff) not my strong point.
	// Improved on 10/19/15
    public UPNPServer(Controller controller) {
		super();
        this.controller = controller;
		upnpResponsePort = Integer.valueOf("50000");
		httpServerPort = Integer.valueOf(controller.getPort());
		responseAddress = controller.getIpAddress();
		traceupnp = true;
    }
    
    public void run() {
        log.info("Starting UPNPServer for discovery...");
		try (DatagramSocket responseSocket = new DatagramSocket(upnpResponsePort);
				MulticastSocket upnpMulticastSocket  = new MulticastSocket(UPNP_DISCOVERY_PORT);) {
			InetSocketAddress socketAddress = new InetSocketAddress(UPNP_MULTICAST_ADDRESS, UPNP_DISCOVERY_PORT);
			Enumeration<NetworkInterface> ifs =	NetworkInterface.getNetworkInterfaces();

			while (ifs.hasMoreElements()) {
				NetworkInterface xface = ifs.nextElement();
				Enumeration<InetAddress> addrs = xface.getInetAddresses();
				String name = xface.getName();
				int IPsPerNic = 0;

				while (addrs.hasMoreElements()) {
					InetAddress addr = addrs.nextElement();
					if(traceupnp)
						log.info("Traceupnp: " + name + " ... has addr " + addr);
					else
						log.debug(name + " ... has addr " + addr);
					if (InetAddressUtils.isIPv4Address(addr.getHostAddress())) {
						IPsPerNic++;
					}
				}
				log.debug("Checking " + name + " to our interface set");
				if (IPsPerNic > 0) {
					upnpMulticastSocket.joinGroup(socketAddress, xface);
					if(traceupnp)
						log.info("Traceupnp: Adding " + name + " to our interface set");
					else
						log.debug("Adding " + name + " to our interface set");
				}
			}

			log.info("UPNP Discovery Listener running and ready....");

			while(true){ //trigger shutdown here
				byte[] buf = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				upnpMulticastSocket.receive(packet);
				String packetString = new String(packet.getData());
				if(packetString != null && packetString.contains("M-SEARCH")) {
					if(traceupnp)
						log.info("Traceupnp: SSDP packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + ", body: " + packetString);
					else
						log.debug("Got SSDP packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort() + ", body: " + packetString);
				}
				if(isSSDPDiscovery(packetString)){
					sendUpnpResponse(responseSocket, packet.getAddress(), packet.getPort());
				}
			}

		}  catch (IOException e) {
			log.error("UpnpListener encountered an error opening sockets. Shutting down", e);

		}
		log.info("UPNP Discovery Listener Stopped");

    }
    
    public void startUPNPServer() {
        runUPNPServer=true;
        start();
    }
    
    public void stopUPNPServer() {
        runUPNPServer=false;
    }
    
	/**
	 * very naive ssdp discovery packet detection
	 * @param body
	 * @return
	 */
	protected boolean isSSDPDiscovery(String body){
		// log.debug("Check if this is a MAN ssdp-discover packet for a upnp basic device: " + body);
		//Only respond to discover request for upnp basic device from echo, the others are for the wemo
		if(body != null && body.contains("M-SEARCH") && body.contains("\"ssdp:discover\"")){
			if(traceupnp)
				log.info("Traceupnp: isSSDPDiscovery found message to be an M-SEARCH message.");
			if(body.startsWith("M-SEARCH * HTTP/1.1") && body.contains("MAN: \"ssdp:discover\"") && (body.contains("ST: urn:schemas-upnp-org:device:basic:1") || body.contains("ST: upnp:rootdevice") || body.contains("ST: ssdp:all")))
			{
				if(traceupnp)
					log.info("Traceupnp: isSSDPDiscovery found message to be valid.");
				return true;
			}
		}
		if(traceupnp)
			log.info("Traceupnp: isSSDPDiscovery found message to not be valid.");
		return false;
	}

	String discoveryTemplate = "HTTP/1.1 200 OK\r\n" +
			"CACHE-CONTROL: max-age=86400\r\n" +
			"EXT:\r\n" +
			"LOCATION: http://%s:%s/description.xml\r\n" +
			"SERVER: FreeRTOS/6.0.5, UPnP/1.0, IpBridge/0.1\r\n" + 
			"ST: urn:schemas-upnp-org:device:basic:1\r\n" +
			"USN: uuid:Socket-1_0-221438K0100073::urn:schemas-upnp-org:device:basic:1\r\n\r\n";
	protected void sendUpnpResponse(DatagramSocket socket, InetAddress requester, int sourcePort) throws IOException {
		String discoveryResponse = null;
		discoveryResponse = String.format(discoveryTemplate, responseAddress, httpServerPort, getRandomUUIDString());
		if(traceupnp)
			log.info("Traceupnp: sendUpnpResponse: " + discoveryResponse);
		else
			log.debug("sendUpnpResponse: " + discoveryResponse);
		DatagramPacket response = new DatagramPacket(discoveryResponse.getBytes(), discoveryResponse.length(), requester, sourcePort);
		socket.send(response);
	}

	protected String getRandomUUIDString(){
		return "88f6698f-2c83-4393-bd03-cd54a9f8595"; // https://xkcd.com/221/
	}
}
