package com.edgeapi.service.iam.interfaces.acl;
import com.edgeapi.service.iam.domain.model.commands.SignUpCommand;
import com.edgeapi.service.iam.domain.model.queries.GetDeviceByIdQuery;
import com.edgeapi.service.iam.domain.model.queries.GetDeviceByMacAddressQuery;
import com.edgeapi.service.iam.domain.services.DeviceCommandService;
import com.edgeapi.service.iam.domain.services.DeviceQueryService;
import org.apache.logging.log4j.util.Strings;

/**
 * IamContextFacade
 * <p>
 *     This class is a facade for the IAM context. It provides a simple interface for other bounded contexts to interact with the
 *     IAM context.
 *     This class is a part of the ACL layer.
 * </p>
 */
public class IamContextFacade {
    private final DeviceCommandService userCommandService;
    private final DeviceQueryService userQueryService;

    public IamContextFacade(DeviceCommandService userCommandService, DeviceQueryService userQueryService) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    /**
     * Creates a user with the given username and password.
     * @param macAddress The username of the user.
     * @param secret The password of the user.
     * @return The id of the created user.
     */
    public Long createDevice(String macAddress, String secret) {
        var signUpCommand = new SignUpCommand(macAddress, secret);
        var result = userCommandService.handle(signUpCommand);
        if (result.isEmpty()) return 0L;
        return result.get().getId();
    }

    /**
     * Fetches the id of the user with the given username.
     * @param macAddress The username of the user.
     * @return The id of the user.
     */
    public Long fetchDeviceByMacAddress(String macAddress) {
        var getUserByUsernameQuery = new GetDeviceByMacAddressQuery(macAddress);
        var result = userQueryService.handle(getUserByUsernameQuery);
        if (result.isEmpty()) return 0L;
        return result.get().getId();
    }

    /**
     * Fetches the username of the user with the given id.
     * @param deviceId The id of the user.
     * @return The username of the user.
     */
    public String fetchMacAddressByDeviceId(Long deviceId) {
        var getUserByIdQuery = new GetDeviceByIdQuery(deviceId);
        var result = userQueryService.handle(getUserByIdQuery);
        if (result.isEmpty()) return Strings.EMPTY;
        return result.get().getMacAddress();
    }
}
