package mate.academy.car_sharing_app.security;

import lombok.RequiredArgsConstructor;
import mate.academy.car_sharing_app.exceptions.EntityNotFoundException;
import mate.academy.car_sharing_app.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws EntityNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by email: " + email));
    }
}
