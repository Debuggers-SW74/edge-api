package com.edgeapi.service.iam.domain.services;

import com.edgeapi.service.iam.domain.model.aggregates.Device;
import com.edgeapi.service.iam.domain.model.queries.GetAllDevicesQuery;
import com.edgeapi.service.iam.domain.model.queries.GetDeviceByIdQuery;
import com.edgeapi.service.iam.domain.model.queries.GetDeviceByMacAddressQuery;
import com.edgeapi.service.iam.domain.model.queries.GetSecretByMacAddressQuery;

import java.util.List;
import java.util.Optional;

public interface DeviceQueryService {
    List<Device> handle(GetAllDevicesQuery query);
    Optional<Device> handle(GetDeviceByIdQuery query);
    Optional<Device> handle(GetDeviceByMacAddressQuery query);
    Optional<String> getSecretByMacAddress(GetSecretByMacAddressQuery query);
}
