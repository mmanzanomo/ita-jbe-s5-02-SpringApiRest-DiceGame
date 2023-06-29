package cat.itacademy.barcelonactiva.mznmon.s05.t02.config;

import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.users.Role;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.repositories.jpa.IRolesJpaRepository;
import cat.itacademy.barcelonactiva.mznmon.s05.t02.model.repositories.jpa.IUserJpaRepository;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;


@Configuration
@RequiredArgsConstructor
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER
)

public class ApplicationConfig {
    @Autowired
    private final IUserJpaRepository repository;
    @Autowired
    private IRolesJpaRepository rolesJpaRepository;


    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Dice Game API")
                        .version("0.1")
                        .description("API with Spring Boot to manage a Dice Game")
                        .termsOfService("http://swagger.io/terms")
                        .license(new License().name("Apache 2.0")
                                .url("http://springdoc.org")
                        ));
    }

    @Bean
    public CommandLineRunner initRoles() {
        return args -> {
            createRoleIfNotExists("ADMIN");
            createRoleIfNotExists("USER");
        };
    }

    private void createRoleIfNotExists(String name) {
        Optional<Role> optionalRole = rolesJpaRepository.findByName(name);
        if (optionalRole.isEmpty()) {
            Role role = new Role();
            role.setName(name);
            rolesJpaRepository.save(role);
        }
    }

}
