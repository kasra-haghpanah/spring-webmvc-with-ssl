package org.application.spring.ddd.service;

import org.application.spring.ddd.model.entity.User;
import org.application.spring.ddd.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends AppService<User, String, UserRepository> implements UserRepository {

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        super(userRepository);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByUserName(String userName) {
        return repository.findByUserName(userName);
    }

    @Override
    public int updateUserForActivationCode(String email) {
        return this.repository.updateUserForActivationCode(email);
    }


    @Transactional("appTM")
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAuthority("ADMIN","USER");
        return this.repository.save(user);
    }
}
