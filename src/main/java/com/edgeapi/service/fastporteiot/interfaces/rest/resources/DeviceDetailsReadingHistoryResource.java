package com.edgeapi.service.fastporteiot.interfaces.rest.resources;

import com.edgeapi.service.fastporteiot.domain.model.entities.ReadingHistory;
import java.util.List;

public record DeviceDetailsReadingHistoryResource(List<ReadingHistory> history) {
}
