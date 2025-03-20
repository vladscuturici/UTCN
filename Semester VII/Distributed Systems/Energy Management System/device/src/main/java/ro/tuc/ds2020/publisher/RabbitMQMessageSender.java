package ro.tuc.ds2020.publisher;

import java.util.UUID;

public interface RabbitMQMessageSender {
    void sendMessage(UUID deviceId, UUID userId, Double maxHourlyConsumption);
}
