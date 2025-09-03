package com.easybudget.easybudget_spring.user;

import com.easybudget.easybudget_spring.auth.RegisterRequestDto;
import com.easybudget.easybudget_spring.exception.NotFoundException;
import com.easybudget.easybudget_spring.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_ShouldSaveAndReturnJwtToken() {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("test@example.com")
                .username("testuser")
                .password("password123")
                .build();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .password("encodedPassword")
                .role(Role.USER)
                .authProvider(AuthProvider.LOCAL)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(org.springframework.security.core.userdetails.User
                .withUsername("1") // simulate userId returned as username
                .password("encodedPassword")
                .authorities("ROLE_USER")
                .build());
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(jwtUtil.generateToken("1")).thenReturn("mockJwtToken");

        String token = userService.createUser(request);

        assertEquals("mockJwtToken", token);
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void testCreateUser_ShouldThrowException_WhenEmailExists() {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("test@example.com")
                .username("testuser")
                .password("password123")
                .build();

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(request));
    }

    @Test
    void testFindById_ShouldReturnUser() {
        User user = User.builder().id(1L).email("a@a.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.findById(1L);
        assertEquals("a@a.com", found.getEmail());
    }

    @Test
    void testFindById_ShouldThrow_WhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void testGetCurrentAuthenticatedUser_ShouldReturnUser() {
        User user = User.builder().id(5L).email("b@b.com").build();

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("5");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        User result = userService.getCurrentAuthenticatedUser();

        assertEquals("b@b.com", result.getEmail());
    }

    @Test
    void testUpdateCurrency_ShouldSaveNewCurrency() {
        User user = User.builder().id(7L).email("c@c.com").currency("USD").build();

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("7");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        String response = userService.updateCurrency("EUR");

        assertEquals("Currency updated to: EUR", response);
        assertEquals("EUR", user.getCurrency());
    }
}
