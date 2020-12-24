package cn.ivdone.blog.repository;

import cn.ivdone.blog.entity.ImageLibrary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



// 图像库
@Repository
public interface ImageLibraryRepository extends JpaRepository<ImageLibrary, Long> {

    @Query(value = "SELECT * FROM blog_imagelibrary WHERE name = ?1", nativeQuery = true)
    ImageLibrary findImageLibraryByName(String name) ;

    @Query(value = "SELECT * FROM blog_imagelibrary",
          countQuery = "SELECT count(*) FROM blog_imagelibrary",
          nativeQuery = true)
    Page<ImageLibrary> findAllImageLibraryByPage(Pageable pageable) ;

    // 通过用户名查找分页图像文件库
    @Query(value = "SELECT * FROM blog_imagelibrary i INNER JOIN blog_user u ON i.user_id = u.id WHERE u.username = ?1",
            countQuery = "SELECT count(*) FROM blog_imagelibrary i INNER JOIN blog_user u ON i.user_id = u.id WHERE u.username = ?1",
            nativeQuery = true)
    Page<ImageLibrary> findImageLibrariesByUsernameOfPage(String username, Pageable pageable) ;

    //查找有用户的分页图像文件库
    @Query(value = "SELECT * FROM blog_imagelibrary i WHERE i.user_id IS NOT NULL ",
    countQuery = "SELECT count(*) FROM blog_imagelibrary i WHERE i.user_id IS NOT NULL ",
            nativeQuery = true)
    Page<ImageLibrary> findImageLibrariesHasUserOfPage(Pageable pageable) ;

    // 通过文件命名来删除该文件库
    // @Transactional  @Modifying 一定需要加这两个注解 否则就不能对数据库进行删除和更新
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM blog_imagelibrary WHERE name = ?1", nativeQuery = true)
    void deleteImageLibraryByName(String name);
}
