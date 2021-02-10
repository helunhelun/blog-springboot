package cn.ivdone.blog.config;


import cn.ivdone.blog.auth.BlogAuthenticationProvider;
import cn.ivdone.blog.auth.BlogUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private BlogAuthenticationProvider authenticationProvider ;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder() ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                // 关闭csrf防护
                .csrf().disable()
                .headers().frameOptions().disable()
                .and();

        http
                .logout()
                .logoutUrl("/admin/logout")   // 登出url
                .logoutSuccessUrl("/login");  // 登出成功重定向界面
        http
                .formLogin()
                .defaultSuccessUrl("/wp-admin/index")
                .permitAll()
                .failureUrl("/login/error")
                .and();

        http
                .authorizeRequests()
                .antMatchers("/wp-admin/userAdmin/get/username").authenticated()   // 用于获取当前用户
                .antMatchers("/wp-admin/userAdmin/**").hasRole("ADMIN")  // 一定先配置小范围 再配置大范围的
                .antMatchers("/wp-admin/**").authenticated()
                //.antMatchers("/wp-admin/userAdmin/**").
                .anyRequest().permitAll();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(authenticationProvider) ;
    }


//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .userDetailsService(userDetailsService())
//                .passwordEncoder(passwordEncoder());
//
////        auth
////                .inMemoryAuthentication()
////                .passwordEncoder(passwordEncoder())
////                .withUser("admin").password(passwordEncoder().encode("123456")).roles("ADMIN")
////                .and()
////                .withUser("losha").password(passwordEncoder().encode("helun520")).roles("USER") ;
//        //super.configure(auth);
//    }
}

