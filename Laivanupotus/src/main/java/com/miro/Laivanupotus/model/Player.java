package com.miro.Laivanupotus.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.miro.Laivanupotus.interfaces.UserProfileDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "players")
@Data
@Getter
@Setter
public class Player implements UserProfileDto, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userName;
    @Column(nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String password;
    @Column(nullable = false)
    private String roles = "ROLE_USER";

    private int totalGames;
    private int gamesWon;
    private int gamesLost;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;

    @PrePersist
    protected void onCreate() {
	createdAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
	return Arrays.stream(roles.split(",")).map(role -> new SimpleGrantedAuthority(role.trim()))
		.collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
	// TODO Auto-generated method stub
	return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
	return true;
    };

    @Override
    public boolean isAccountNonLocked() {
	return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
	return true;
    };

    @Override
    public boolean isEnabled() {
	return true;
    }

    @Override
    public String getUserName() {
	// TODO Auto-generated method stub
	return userName;
    };

}
