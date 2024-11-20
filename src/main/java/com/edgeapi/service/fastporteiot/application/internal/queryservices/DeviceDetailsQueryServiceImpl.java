package com.edgeapi.service.fastporteiot.application.internal.queryservices;

import com.edgeapi.service.fastporteiot.domain.model.aggregates.DeviceDetails;
import com.edgeapi.service.fastporteiot.domain.model.entities.ReadingHistory;
import com.edgeapi.service.fastporteiot.domain.model.queries.GetAllDevicesDetailsQuery;
import com.edgeapi.service.fastporteiot.domain.model.queries.GetDeviceDetailsQuery;
import com.edgeapi.service.fastporteiot.domain.model.queries.GetDeviceDetailsReadingHistoryQuery;
import com.edgeapi.service.fastporteiot.domain.services.DeviceDetailsQueryService;
import com.edgeapi.service.fastporteiot.infrastructure.persistence.jpa.repositories.DeviceDetailsRepository;
import com.edgeapi.service.fastporteiot.infrastructure.persistence.jpa.repositories.ReadingHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceDetailsQueryServiceImpl implements DeviceDetailsQueryService {
    private final DeviceDetailsRepository deviceDetailsRepository;
    private final ReadingHistoryRepository readingHistoryRepository;

    /**
     * Constructor para inyectar dependencias y configurar el servicio.
     *
     * @param deviceDetailsRepository repositorio para acceder a los detalles del dispositivo.
     * @param readingHistoryRepository repositorio para acceder al historial de lecturas.
     */
    @Autowired
    public DeviceDetailsQueryServiceImpl(
            DeviceDetailsRepository deviceDetailsRepository,
            ReadingHistoryRepository readingHistoryRepository
    ) {
        this.deviceDetailsRepository = deviceDetailsRepository;
        this.readingHistoryRepository = readingHistoryRepository;
    }

    /**
     * Maneja la consulta para obtener los detalles de un dispositivo.
     *
     * @param query la consulta que contiene los datos necesarios para obtener los detalles del dispositivo.
     * @return una instancia de DeviceDetails con los detalles del dispositivo, si se encuentra.
     */
    @Override
    public Optional<DeviceDetails> handle(GetDeviceDetailsQuery query) {
        return deviceDetailsRepository.findByMacAddress(query.macAddress());
    }

    /**
     * Maneja la consulta para obtener los detalles de todos los dispositivos.
     *
     * @param query la consulta para obtener todos los detalles de los dispositivos.
     * @return una lista de DeviceDetails con los detalles de todos los dispositivos.
     */
    @Override
    public List<DeviceDetails> handle(GetAllDevicesDetailsQuery query) {
        return deviceDetailsRepository.findAll();
    }

    /**
     * Maneja la consulta para obtener el historial de lecturas de un dispositivo.
     *
     * @param query la consulta que contiene los datos necesarios para obtener el historial de lecturas.
     * @return una lista de ReadingHistory con el historial de lecturas del dispositivo.
     */
    @Override
    public List<ReadingHistory> handle(GetDeviceDetailsReadingHistoryQuery query) {
        return readingHistoryRepository.findByMacAddressAndTimestampBetween(
                query.macAddress(),
                query.startTime(),
                query.endTime()
        );
    }
}
