package cn.ivdone.blog.auth;


import cn.ivdone.blog.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class BlogAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private BlogUserDetailsService userDetailsService ;

    @Autowired
    private PasswordEncoder passwordEncoder ;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal() ;
        String password = (String) authentication.getCredentials() ;

        // 通过用户名 加载用户信息
        UserDetails userInfo = userDetailsService.loadUserByUsername(username) ;
        // 用户不存在
        if (userInfo == null) {
            throw new BadCredentialsException("用户名不存在");
        }
        // 这个地方是密码验证
        boolean flag = passwordEncoder.matches(password, userInfo.getPassword()) ;
        // 密码验证不成功
        if (!flag){
            throw new BadCredentialsException("密码不正确");
        }

        Collection<? extends GrantedAuthority>  authorities = userInfo.getAuthorities() ;

        return new UsernamePasswordAuthenticationToken(userInfo, password, authorities) ;

    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
