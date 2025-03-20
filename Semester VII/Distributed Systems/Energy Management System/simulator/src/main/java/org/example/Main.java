package org.example;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {
        String deviceId = loadDeviceId();

        try (Connection connection = connectToRabbitMQ(); Channel channel = connection.createChannel()) {
            channel.queueDeclare("measurements-queue", true, false, false, null);
            System.out.println("Connected to RabbitMQ");

            readCsvAndSendToQueue(channel, deviceId, "C:\\Users\\start\\Documents\\SD\\assignment1\\simulator\\src\\main\\resources\\sensor.csv");
        } catch (Exception e) {
            System.err.println("RabbitMQ connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String loadDeviceId() {
        Properties properties = new Properties();
        String configPath = "C:\\Users\\start\\Documents\\SD\\assignment1\\simulator\\src\\main\\resources\\config.properties";

        try (FileReader reader = new FileReader(configPath)) {
            properties.load(reader);
            String deviceId = properties.getProperty("device.id");

            if (deviceId == null || deviceId.isEmpty()) {
                System.err.println("device.id is missing or empty in config.properties");
                return null;
            }

            System.out.println("Loaded deviceId from config: " + deviceId);
            return deviceId;
        } catch (IOException e) {
            System.err.println("Failed to load config.properties from " + configPath);
            e.printStackTrace();
            return null;
        }
    }

    private static void readCsvAndSendToQueue(Channel channel, String deviceId, String csvPath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(csvPath));
            for (String line : lines) {
                sendMessageToQueue(channel, deviceId, line.trim());
                Thread.sleep(5000);
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + csvPath);
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted while processing CSV file");
        }
    }

    private static void sendMessageToQueue(Channel channel, String deviceId, String value) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS"));
            String jsonMessage = String.format("{\"timestamp\": \"%s\", \"device_id\": \"%s\", \"measurement_value\": %s}",
                    timestamp, deviceId, value);

            channel.basicPublish("", "measurements-queue", null, jsonMessage.getBytes());
            System.out.println("Sent to RabbitMQ: " + jsonMessage);
        } catch (IOException e) {
            System.err.println("Failed to send message to queue");
            e.printStackTrace();
        }
    }

    private static Connection connectToRabbitMQ() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        return factory.newConnection();
    }
}
