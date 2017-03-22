package ua.rd.cm.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.rd.cm.domain.Role;
import ua.rd.cm.domain.User;
import ua.rd.cm.domain.UserInfo;
import ua.rd.cm.domain.VerificationToken;
import ua.rd.cm.dto.RegistrationDto;
import ua.rd.cm.repository.UserRepository;
import ua.rd.cm.repository.specification.AndSpecification;
import ua.rd.cm.repository.specification.OrSpecification;
import ua.rd.cm.repository.specification.Specification;
import ua.rd.cm.repository.specification.user.*;
import ua.rd.cm.services.MailService;
import ua.rd.cm.services.RoleService;
import ua.rd.cm.services.UserService;
import ua.rd.cm.services.VerificationTokenService;
import ua.rd.cm.services.exception.ResourceNotFoundException;
import ua.rd.cm.services.preparator.ConfirmAccountPreparator;

import java.util.Collections;
import java.util.List;

import static ua.rd.cm.services.exception.ResourceNotFoundException.USER_NOT_FOUND;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleService roleService;
    private MailService mailService;
    private ModelMapper mapper;
    private VerificationTokenService tokenService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService,
                           MailService mailService, VerificationTokenService tokenService,
                           ModelMapper mapper, PasswordEncoder passwordEncode) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.mailService = mailService;
        this.tokenService = tokenService;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncode;
    }

    @Override
    public User find(Long id) {
        List<User> users = userRepository.findBySpecification(new UserById(id));
        if (users.isEmpty()) {
            throw new ResourceNotFoundException(USER_NOT_FOUND);
        }
        return users.get(0);
    }

    @Override
    @Transactional
    public void save(User user) {
        if (user.getUserRoles().size() == 0) {
            user.addRole(roleService.getByName(Role.SPEAKER));
        }
        if (user.getUserInfo() == null) {
            user.setUserInfo(new UserInfo());
        }
        userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getByFirstName(String name) {
        return userRepository.findBySpecification(new UserByFirstName(name));
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = userRepository.findBySpecification(new UserByEmail(email));
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    @Override
    public List<User> getByLastName(String lastName) {
        return userRepository.findBySpecification(new UserByLastName(lastName));
    }

    @Override
    public boolean isEmailExist(String email) {
        return !userRepository.findBySpecification(new UserByEmail(email)).isEmpty();
    }

    @Override
    @Transactional
    public void registerNewUser(RegistrationDto dto) {
        User newUser = mapRegistrationDtoToUser(dto);
        save(newUser);
        if (User.UserStatus.UNCONFIRMED.equals(dto.getUserStatus())) {
            VerificationToken token = tokenService.createToken(newUser, VerificationToken.TokenType.CONFIRMATION);
            tokenService.saveToken(token);
            mailService.sendEmail(newUser, new ConfirmAccountPreparator(token, mailService.getUrl()));
        }
    }

    @Override
    @Transactional
    public void updateUserProfile(User user) {
        userRepository.update(user);
    }

    @Override
    public List<User> getByRole(String role) {
        return userRepository.findAllWithRoles(new UserByRole(role));
    }

    @Override
    public List<User> getByRoleExceptCurrent(User currentUser, String roleName) {
        return userRepository.findAllWithRoles(
                new AndSpecification<>(
                        new UserByRole(roleName),
                        new UserExceptThisById(currentUser.getId())
                )
        );
    }

    @Override
    public List<User> getByRolesExceptCurrent(User currentUser, String... roles) {
        List<User> users = Collections.emptyList();
        if (roles.length > 0) {
            Specification<User> current = new UserByRole(roles[0]);
            for (int i = 1; i < roles.length; i++) {
                current = new OrSpecification<>(current, new UserByRole(roles[i]));
            }
            users = userRepository.findAllWithRoles(new UserOrderByLastName(
                            new AndSpecification<>(
                                    current,
                                    new UserExceptThisById(currentUser.getId())
                            )
                    )
            );
        }
        return users;
    }

    @Override
    public boolean isAuthenticated(User user, String password) {
        String hashedPassword = user.getPassword();
        return passwordEncoder.matches(password, hashedPassword);
    }

    private User mapRegistrationDtoToUser(RegistrationDto dto) {
        User user = mapper.map(dto, User.class);
        user.setEmail(user.getEmail().toLowerCase());
        user.addRole(roleService.getByName(dto.getRoleName()));
        return user;
    }
}