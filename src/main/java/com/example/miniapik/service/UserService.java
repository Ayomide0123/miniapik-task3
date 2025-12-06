package com.example.miniapik.service;

import com.example.miniapik.model.AppUser;
import com.example.miniapik.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepo) { this.userRepo = userRepo; }

    public AppUser signup(String username, String password) {
        if (userRepo.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        String hash = passwordEncoder.encode(password);
        AppUser u = new AppUser(username, hash);
        return userRepo.save(u);
    }

    public Optional<AppUser> findByUsername(String username){
        return userRepo.findByUsername(username);
    }

    public boolean verifyPassword(AppUser user, String rawPassword){
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }
}

