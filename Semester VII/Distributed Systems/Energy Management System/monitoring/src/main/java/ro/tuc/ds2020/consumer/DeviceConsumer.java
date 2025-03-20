package ro.tuc.ds2020.consumer;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.services.DeviceService;

import java.util.Map;
import java.util.Properties;

@Component
public class DeviceConsumer {
    private final DeviceService deviceService;
    private final RabbitAdmin rabbitAdmin;

    @Autowired
    public DeviceConsumer(DeviceService deviceService, RabbitAdmin rabbitAdmin) {
        this.deviceService = deviceService;
        this.rabbitAdmin = rabbitAdmin;
        ensureQueueExists();
    }

    @RabbitListener(queues = "device-queue")
    public void receiveMessage(String message) {
        String type = getHttpMethod(message);

        switch (type) {
            case "POST":
                deviceService.addMessage(message);
                break;
            case "PUT":
                deviceService.update(message);
                break;
            case "DELETE":
                deviceService.delete(message);
                break;
            default:
                System.out.println("Unknown message type: " + type);
        }

        System.out.println("Received from RabbitMQ: " + message);
    }

    private String getHttpMethod(String message) {
        String trimmedJson = message.replaceAll("[{}\"]", "");
        String[] keyValuePairs = trimmedJson.split(",");
        return keyValuePairs[0].split(":")[1].trim();
    }

    private void ensureQueueExists() {
        Properties queueProperties = rabbitAdmin.getQueueProperties("device-queue");

        if (queueProperties == null || queueProperties.isEmpty()) {
            rabbitAdmin.declareQueue(new Queue("device-queue"));
            System.out.println("Queue 'device-queue' created.");
        } else {
            System.out.println("Queue 'device-queue' already exists.");
        }
    }

}
