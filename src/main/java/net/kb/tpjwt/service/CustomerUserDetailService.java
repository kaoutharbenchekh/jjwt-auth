package net.kb.tpjwt.service;

import net.kb.tpjwt.entity.AppUser;
import net.kb.tpjwt.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomerUserDetailService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public CustomerUserDetailService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        AppUser appUser = userRepository
                .findAppUserByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found!!")
                );

        return User.builder()
                .username(appUser.getEmail())
                .password(appUser.getPassword())
                .roles(appUser.getRole())
                .build();
    }
}
