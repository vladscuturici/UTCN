package ro.tuc.ds2020.publisher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeleteSender implements  RabbitMQMessageSender{
    @Override
    public void sendMessage(UUID deviceId, UUID userId, Double maxHourlyConsumption) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            channel.queueDeclare("device-queue", true, false, false, null);

            String json = "{"+ "\"type\": \"DELETE\"," + "\"device_id\": \"" + deviceId + "\"" + (userId != null ? ", \"user_id\": \"" + userId + "\"" : "")  + (maxHourlyConsumption != null ? ", \"max_hourly_consumption\": \"" + maxHourlyConsumption + "\"" : "") + "}";

            channel.basicPublish("", "device-queue", null, json.getBytes());
            System.out.println("Sent to RabbitMQ: " + json);

        } catch (Exception e) {
            System.out.println("Connection to RabbitMQ failed");
            e.printStackTrace();
        }
    }
}

