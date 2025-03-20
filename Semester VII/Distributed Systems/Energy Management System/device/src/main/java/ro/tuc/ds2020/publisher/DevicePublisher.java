package ro.tuc.ds2020.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.services.DeviceService;

import java.util.UUID;

@Component
public class DevicePublisher {

    @Autowired
    private PostSender postSender;

    @Autowired
    private PutSender putSender;

    @Autowired
    private DeleteSender deleteSender;
    @Autowired
    private DeviceService deviceService;

    public void sendPostRequest(DeviceDTO dto) {
        postSender.sendMessage(dto.getId(), deviceService.getUserIdByDeviceId(dto.getId()), dto.getMaxHourlyEnergyConsumption());
    }

    public void sendPutRequest(DeviceDTO dto) {
        putSender.sendMessage(dto.getId(), deviceService.getUserIdByDeviceId(dto.getId()), dto.getMaxHourlyEnergyConsumption());
    }

    public void sendDeleteRequest(UUID deviceId) {
        deleteSender.sendMessage(deviceId, null, null);
    }
}