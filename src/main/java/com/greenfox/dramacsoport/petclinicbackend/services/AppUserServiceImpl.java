package com.greenfox.dramacsoport.petclinicbackend.services;

import com.greenfox.dramacsoport.petclinicbackend.config.webtoken.JwtService;
import com.greenfox.dramacsoport.petclinicbackend.dtos.LoginRequestDTO;
import com.greenfox.dramacsoport.petclinicbackend.dtos.RegisterRequestDTO;
import com.greenfox.dramacsoport.petclinicbackend.models.AppUser;
import com.greenfox.dramacsoport.petclinicbackend.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.NameAlreadyBoundException;

@RequiredArgsConstructor
@Service
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;

    private final AppUserDetailsService userDetailsService;

    private final ModelMapper modelMapper = new ModelMapper();

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String petClinicEmail;

    /**
     * Convert any user related DTO to the Entity object used by this Service
     *
     * @param sourceDTO the DTO, you want to convert
     * @return MyUser entity
     */
    private <T> AppUser convertToEntity(T sourceDTO) {
        return modelMapper.map(sourceDTO, AppUser.class);
    }

    @Override
    public AppUser saveUser(AppUser user) {
        return appUserRepository.save(user);
    }


    /**
     * <h3>This method registers a new user.</h3>
     * <ul>
     *     <li>If even one field is not filled, then throws an exception.</li>
     *     <li>If the password is only 3 characters long, throws an exception.</li>
     *     <li>If the user is already created, throws an exception.</li>
     * </ul>
     * Saves the input fields and encodes the password for storage.
     * After the entity has been created, the application sends a greeting email
     * to the e-mail address of the new user.
     *
     * @param userRequest the user object created from the registration form
     */
    @Override
    public AppUser registerUser(RegisterRequestDTO userRequest) throws RuntimeException {

        if (!isPasswordLongerThanThreeChar(userRequest.getPassword())) {
            throw new RuntimeException("Password must be longer than 3 characters.", new Exception());
        }
        if (isUserRegistered(userRequest.getEmail())) {
            throw new RuntimeException("User already exists.", new NameAlreadyBoundException());
        }

        AppUser newUser = convertToEntity(userRequest);
        newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        sendEmailAfterRegistration(newUser);

        return saveUser(newUser);
    }


    @Override
    public String login(LoginRequestDTO requestDTO) throws UsernameNotFoundException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDTO.email(), requestDTO.password())
            );
            return jwtService.generateToken(userDetailsService.loadUserByUsername(requestDTO.email()));
        } catch (AuthenticationException e) {
            throw new UsernameNotFoundException("Authentication failed!");
        }
    }

    public boolean isPasswordLongerThanThreeChar(String password) {
        return password.length() > 3;
    }

    public boolean isUserRegistered(String email) {
        return appUserRepository.findByEmail(email).isPresent();
    }

    private void sendEmailAfterRegistration(AppUser user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(petClinicEmail);
        message.setTo(user.getEmail());
        message.setSubject("Registration successful - Pet Clinic");
        message.setText("Dear " + user.getUsername() + ",\n\n" +
                "Thank you for registering to our Pet Clinic application!\n\n" +
                "Best regards,\n" +
                "Pet Clinic Team");
        javaMailSender.send(message);
    }

}
