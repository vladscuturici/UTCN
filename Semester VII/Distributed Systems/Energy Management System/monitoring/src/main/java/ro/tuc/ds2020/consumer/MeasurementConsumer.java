package ro.tuc.ds2020.consumer;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.services.MeasurementService;

import java.util.Properties;

@Component
public class MeasurementConsumer {
    private final MeasurementService measurementService;
    private final RabbitAdmin rabbitAdmin;

    @Autowired
    public MeasurementConsumer(MeasurementService measurementService, RabbitAdmin rabbitAdmin) {
        this.measurementService = measurementService;
        this.rabbitAdmin = rabbitAdmin;
        ensureQueueExists();
    }

    @RabbitListener(queues = "measurements-queue")
    public void receiveMessage(String message) {
        System.out.println("Received message from RabbitMQ: " + message);
        try {
            measurementService.register(message);
        } catch (Exception e) {
            System.err.println("Error processing message from RabbitMQ: " + message);
            e.printStackTrace();
        }
    }

    public void ensureQueueExists() {
        Properties queueProperties = rabbitAdmin.getQueueProperties("measurements-queue");

        if (queueProperties == null || queueProperties.isEmpty()) {
            System.out.println("Queue 'measurements-queue' does not exist. Creating it...");
            rabbitAdmin.declareQueue(new Queue("measurements-queue"));
        } else {
            System.out.println("Queue 'measurements-queue' already exists.");
        }
    }
}