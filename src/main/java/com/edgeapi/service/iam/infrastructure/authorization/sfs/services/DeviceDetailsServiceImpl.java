package com.edgeapi.service.iam.infrastructure.authorization.sfs.services;

import com.edgeapi.service.iam.infrastructure.authorization.sfs.model.DeviceDetailsImpl;
import com.edgeapi.service.iam.infrastructure.persistence.jpa.repositories.DeviceRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * This class is responsible for providing the user details to the Spring Security framework.
 * It implements the UserDetailsService interface.
 */
@Service(value = "defaultUserDetailsService")
public class DeviceDetailsServiceImpl implements UserDetailsService {

    private final DeviceRepository userRepository;

    public DeviceDetailsServiceImpl(DeviceRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This method is responsible for loading the user details from the database.
     * @param macAddress The username.
     * @return The UserDetails object.
     * @throws UsernameNotFoundException If the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String macAddress) throws UsernameNotFoundException {
        var user = userRepository.findByMacAddress(macAddress)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + macAddress));
        return DeviceDetailsImpl.build(user);
    }
}
