package ro.tuc.ds2020.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }


    @Bean
    public Queue deviceQueue(RabbitAdmin rabbitAdmin) {
        Queue queue = new Queue("device-queue", true); // Durable queue
        rabbitAdmin.declareQueue(queue);
        System.out.println("Queue 'device-queue' declared successfully.");
        return queue;
    }

    @Bean
    public Queue measurementsQueue(RabbitAdmin rabbitAdmin) {
        Queue queue = new Queue("measurements-queue", true); // Durable queue
        rabbitAdmin.declareQueue(queue);
        System.out.println("Queue 'measurements-queue' declared successfully.");
        return queue;
    }
}