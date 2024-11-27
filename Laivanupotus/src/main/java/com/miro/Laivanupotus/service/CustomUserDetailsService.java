package com.miro.Laivanupotus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.miro.Laivanupotus.model.User;
import com.miro.Laivanupotus.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		// My user impl.
		User user = userRepo.findByUserName(userName)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userName));
		// Convert to Spring user impl.
		return org.springframework.security.core.userdetails.User.withUsername(user.getUserName())
				.password(user.getPassword()).roles("USER").build();

	};
}
