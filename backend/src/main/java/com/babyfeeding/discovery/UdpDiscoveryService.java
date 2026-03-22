package com.babyfeeding.discovery;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UdpDiscoveryService {
    private static final int DISCOVERY_PORT = 8766;
    private static final String DISCOVER_MESSAGE = "BFT_DISCOVER";
    private static final int BUFFER_SIZE = 1024;

    @Value("${server.port}")
    private int serverPort;

    private DatagramSocket socket;
    private Thread listenerThread;
    private volatile boolean running;

    @PostConstruct
    public void start() {
        running = true;
        listenerThread = new Thread(this::listen, "udp-discovery-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
    }

    private void listen() {
        try {
            socket = new DatagramSocket(DISCOVERY_PORT);
            while (running) {
                DatagramPacket request = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                socket.receive(request);

                String message = new String(request.getData(), 0, request.getLength(), StandardCharsets.UTF_8).trim();
                if (DISCOVER_MESSAGE.equals(message)) {
                    reply(request);
                }
            }
        } catch (SocketException e) {
            if (running) {
                throw new IllegalStateException("Failed to bind UDP discovery socket", e);
            }
        } catch (IOException e) {
            if (running) {
                throw new IllegalStateException("UDP discovery listener failed", e);
            }
        }
    }

    private void reply(DatagramPacket request) throws IOException {
        String localIp = InetAddress.getLocalHost().getHostAddress();
        String response = "BFT_HERE:" + localIp + ":" + serverPort;
        byte[] data = response.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, request.getAddress(), request.getPort());
        socket.send(packet);
    }
}
