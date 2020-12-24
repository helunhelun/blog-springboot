package cn.ivdone.blog.service;

import cn.ivdone.blog.entity.User;
import cn.ivdone.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository ;

    public User findUserByUsername(String username){
        return userRepository.findUserByUsername(username) ;
    }

    public List<User> findAllUser(){
        return userRepository.findAll() ;
    }

    public void saveUser(User user){
        userRepository.saveAndFlush(user) ;
    }
}
