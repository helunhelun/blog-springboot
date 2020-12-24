package cn.ivdone.blog.repository;


import cn.ivdone.blog.entity.Tag ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    // 通过name来寻找Tag
    @Query(value = "SELECT * FROM blog_tag WHERE name = ?1", nativeQuery = true)
    Tag findTagByName(String name) ;

    // 获取标签库中有文章的标签
    @Query(value = "SELECT * FROM blog_tag t INNER JOIN blog_article a ON a.tag_id = t.id", nativeQuery = true)
    List<Tag> findTagsHasArticle() ;

    // 由标签名来删除标签
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM blog_tag t WHERE t.name = ?1", nativeQuery = true)
    void deleteTagByName(String name) ;
}
