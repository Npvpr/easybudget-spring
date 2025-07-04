package com.easybudget.easybudget_spring.user;

import com.easybudget.easybudget_spring.auth.LoginRequestDto;
import com.easybudget.easybudget_spring.auth.RegisterRequestDto;
import com.easybudget.easybudget_spring.exception.NotFoundException;
import com.easybudget.easybudget_spring.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----------------- createUser -----------------

    @Test
    void testCreateUser_success() {
        RegisterRequestDto dto = RegisterRequestDto.builder()
                .email("test@example.com")
                .username("Test User")
                .password("password")
                .build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPwd");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new org.springframework.security.core.userdetails.User("123", "", Collections.emptyList()));
        when(jwtUtils.generateToken("123")).thenReturn("jwt-token");

        String result = userService.createUser(dto);

        assertEquals("jwt-token", result);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("Test User", userCaptor.getValue().getUsername());
        assertEquals("encodedPwd", userCaptor.getValue().getPassword());
    }

    @Test
    void testCreateUser_existingEmail_throwsException() {
        RegisterRequestDto dto = RegisterRequestDto.builder().email("exists@example.com").build();
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
    }

    // ----------------- authenticateUser -----------------

    @Test
    void testAuthenticateUser_success() {
        LoginRequestDto dto = LoginRequestDto.builder()
                .email("email@example.com")
                .password("password")
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new org.springframework.security.core.userdetails.User("456", "", Collections.emptyList()));
        when(jwtUtils.generateToken("456")).thenReturn("jwt-token");

        String token = userService.authenticateUser(dto);
        assertEquals("jwt-token", token);
    }

    // ----------------- findById -----------------

    @Test
    void testFindById_success() {
        User user = User.builder().id(1L).username("John").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.findById(1L);
        assertEquals("John", found.getUsername());
    }

    @Test
    void testFindById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findById(99L));
    }

    // ----------------- existsByEmail -----------------

    @Test
    void testExistsByEmail_true() {
        when(userRepository.existsByEmail("a@a.com")).thenReturn(true);
        assertTrue(userService.existsByEmail("a@a.com"));
    }

    @Test
    void testExistsByEmail_false() {
        when(userRepository.existsByEmail("b@b.com")).thenReturn(false);
        assertFalse(userService.existsByEmail("b@b.com"));
    }

    // ----------------- findByEmail -----------------

    @Test
    void testFindByEmail_success() {
        User user = User.builder().email("a@a.com").username("A").build();
        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("a@a.com");
        assertEquals("A", result.getUsername());
    }

    @Test
    void testFindByEmail_notFound() {
        when(userRepository.findByEmail("none@none.com")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findByEmail("none@none.com"));
    }

    // ----------------- getCurrentAuthenticatedUser -----------------

    @Test
    void testGetCurrentAuthenticatedUser_success() {
        User user = User.builder().id(123L).email("abc@abc.com").build();
        Authentication auth = mock(Authentication.class);

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("123");

        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepository.findById(123L)).thenReturn(Optional.of(user));

        User result = userService.getCurrentAuthenticatedUser();
        assertEquals("abc@abc.com", result.getEmail());
    }

    @Test
    void testGetCurrentAuthenticatedUser_unauthenticated() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(NotFoundException.class, () -> userService.getCurrentAuthenticatedUser());
    }

    @Test
    void testGetCurrentAuthenticatedUser_userNotFound() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("999");

        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getCurrentAuthenticatedUser());
    }

    // ----------------- getUserInfos -----------------

    @Test
    void testGetUserInfos_success() {
        User user = User.builder().id(100L).email("info@test.com").username("InfoUser").build();
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("100");

        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepository.findById(100L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserInfos();
        assertEquals("info@test.com", result.getEmail());
        assertEquals("InfoUser", result.getUsername());
    }
}

