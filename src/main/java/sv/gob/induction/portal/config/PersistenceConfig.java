package sv.gob.induction.portal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import sv.gob.induction.portal.commons.datatables.repository.DataTablesRepositoryFactoryBean;
import sv.gob.induction.portal.service.UserDetailsServiceImpl;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class, basePackages = {"sv.gob.induction.portal.repository"})
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class PersistenceConfig {
    private static final Logger LOG = LoggerFactory.getLogger(PersistenceConfig.class);

    private final UserDetailsServiceImpl userDetailsService;

    public PersistenceConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(this.userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).authenticationProvider(authenticationProvider())
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests()
                .requestMatchers("/css/**",
                        "/docs/**"
                        ,"/fonts/**"
                        ,"/images/**"
                        ,"/js/**"
                        , "/vendor/**"
                        , "/public/**",
                        "/").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/user/login")
                .defaultSuccessUrl("/user/index", true)
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/user/logout")
                .permitAll()
        ;
        return http.build();
    }
}
