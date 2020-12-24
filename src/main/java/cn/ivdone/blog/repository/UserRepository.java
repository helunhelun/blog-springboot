package cn.ivdone.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import cn.ivdone.blog.entity.User ;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM blog_user WHERE id = ?1", nativeQuery = true)
    User findUserById(Long id) ;

    @Query(value = "SELECT * FROM blog_user WHERE username = ?1", nativeQuery = true)
    User findUserByUsername(String username) ;
}
