package com.miro.Laivanupotus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.repository.UserRepository;

@Primary
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
	// Fetch implementation of my User and create an Spring User object out
	// of it.

	return userRepo.findByUserName(userName)
		.orElseThrow(() -> new UsernameNotFoundException("User not found!" + userName));

	// Old implementation. Not needed anymore.
	//	UserDetails ud = userRepo.findByUserName(userName)
	//		.map(user -> org.springframework.security.core.userdetails.User.withUsername(user.getUserName())
	//			.password(user.getPassword()).authorities("ROLE_USER").build())
	//		.orElseThrow(() -> new UsernameNotFoundException("User not found!"));
	//
	//	return ud;

    };

}
