package cn.ivdone.blog.service;

import cn.ivdone.blog.entity.Article;
import cn.ivdone.blog.entity.ImageLibrary;
import cn.ivdone.blog.entity.User;
import cn.ivdone.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository ;

    @Autowired
    private ArticleService articleService ;

    @Autowired
    private ImageLibraryService imageLibraryService ;

    public User findUserByUsername(String username){
        return userRepository.findUserByUsername(username) ;
    }

    public List<User> findAllUser(){
        return userRepository.findAll() ;
    }

    public void saveUser(User user){
        userRepository.saveAndFlush(user) ;
    }
    // 删除用户
    public void deleteUser(User user){
        // 找出所有用户
        List<User> userList = this.findAllUser() ;
        // 管理员用户
        List<User> userAdminList = new ArrayList<>() ;
        for (User userObj : userList){
            if (userObj.getIs_stuff()){
                userAdminList.add(userObj) ;
            }
        }
        // 更换该用户的所有文章
        for (Article article : articleService.findAllArticle()){
            if (article.getUser().getUsername().equals(user.getUsername())){
                // 更换用户
                article.setUser(userAdminList.get(0));
                // 写入数据库
                articleService.saveArticle(article);
            }
        }
        // 更换所有图像库的用户
        for (ImageLibrary imageLibrary : imageLibraryService.findAllImageLibrary()){
            if (imageLibrary.getUser() != null){
                if (imageLibrary.getUser().getUsername().equals(user.getUsername())){
                    // 更换用户
                    imageLibrary.setUser(userAdminList.get(0));
                    // 写入数据库
                    imageLibraryService.saveImageLibrary(imageLibrary);
                }
            }
        }

        userRepository.delete(user);
    }
}
