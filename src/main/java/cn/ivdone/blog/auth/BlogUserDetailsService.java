package cn.ivdone.blog.auth;

import cn.ivdone.blog.entity.User;
import cn.ivdone.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BlogUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService ;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userService.findUserByUsername(username) ;
        if (user == null){
            throw new BadCredentialsException("用户不存在") ;
        }

//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder() ;
//        String encodedPassword = passwordEncoder.encode(user.getPassword().trim()) ;
//        user.setPassword(encodedPassword);
//        userService.savaUser(user);
        // 没有管理员选项  普通用户没有管理员权限
        if (!user.getIs_stuff()){
            return new org.springframework.security.core.userdetails.User(username, user.getPassword(),
                    AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER")) ;
        }
        // 正常为管理员权限
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(),
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN")) ;
    }
}

