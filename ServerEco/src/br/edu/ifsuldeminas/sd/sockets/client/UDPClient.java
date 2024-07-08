package br.edu.ifsuldeminas.sd.sockets.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class UDPClient {
    private static final int TIME_OUT = 5000;
    private static final int SERVER_PORT = 3000;
    private static final int BUFFER_SIZE = 200;
    private static final String KEY_TO_EXIT = "q";

    public static void main(String[] args) {
        DatagramSocket datagramSocket = null;
        Scanner reader = new Scanner(System.in);
        String stringMessage = "";
        InetAddress serverAddress;

        try {
            datagramSocket = new DatagramSocket();
            serverAddress = InetAddress.getLocalHost();

            while (!stringMessage.equals(KEY_TO_EXIT)) {
                System.out.printf("Escreva uma mensagem (%s para sair): ", KEY_TO_EXIT);
                stringMessage = reader.nextLine();
                
                if (!stringMessage.equals(KEY_TO_EXIT)) {
                    try {
                        sendMessage(datagramSocket, serverAddress, stringMessage);
                        receiveResponse(datagramSocket, reader);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        } finally {
            closeResources(datagramSocket, reader);
            System.out.printf("Cliente saindo com %s ...\n", KEY_TO_EXIT);
        }
    }

    private static void sendMessage(DatagramSocket datagramSocket, InetAddress serverAddress, String message) throws IOException {
        byte[] messageBytes = message.getBytes();
        DatagramPacket datagramPacketToSend = new DatagramPacket(messageBytes, messageBytes.length, serverAddress, SERVER_PORT);
        datagramSocket.send(datagramPacketToSend);
    }

    private static void receiveResponse(DatagramSocket datagramSocket, Scanner reader) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket datagramPacketForResponse = new DatagramPacket(buffer, buffer.length);

        try {
            datagramSocket.setSoTimeout(TIME_OUT);
            datagramSocket.receive(datagramPacketForResponse);
            String receivedMessage = new String(datagramPacketForResponse.getData(), 0, datagramPacketForResponse.getLength());
            System.out.printf("Resposta do servidor: %s\n", receivedMessage);

            System.out.print("Escreva uma resposta: ");
            String responseMessage = reader.nextLine();
            sendMessage(datagramSocket, datagramPacketForResponse.getAddress(), responseMessage);
        } catch (SocketTimeoutException e) {
            System.out.printf("Sem resposta do servidor de eco UDP.\n");
        }
    }

    private static void closeResources(DatagramSocket datagramSocket, Scanner reader) {
        if (datagramSocket != null) datagramSocket.close();
        if (reader != null) reader.close();
    }
}
