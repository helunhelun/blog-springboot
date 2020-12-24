package cn.ivdone.blog.repository;

import cn.ivdone.blog.entity.Article ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List ;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    // 原始的SQL语句来查询
    @Query(value = "SELECT * FROM blog_article WHERE post_id = ?1", nativeQuery = true)
    Article findArticleByPostId(Long post_id) ;

    // 通过post_id号来删除文章 必须加一个@Modifying 才可以执行删除操作
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM blog_article WHERE post_id = ?1", nativeQuery = true)
    void deleteArticleByPostId(Long post_id) ;

    // 全部文章分页查找
    @Query(value = "SELECT * FROM blog_article",
            countQuery = "SELECT count(*) FROM blog_article",
            nativeQuery = true)
    Page<Article> findAllArticleOfPage(Pageable pageable);

    // 查询所有公开的文章
    @Query(value = "SELECT * FROM blog_article a WHERE a.status = ?1", nativeQuery = true)
    List<Article> findArticlesByStatus(String status);

    // 通过文章状态分页查找
    @Query(value = "SELECT * FROM blog_article WHERE status = ?1",
            countQuery = "SELECT count(*) FROM blog_article WHERE status = ?1",
            nativeQuery = true)
    Page<Article> findArticlesByStatusOfPage(String status, Pageable pageable) ;

    //  通过用户名分页查找
    @Query(value = "SELECT * FROM blog_article a INNER JOIN blog_user u  ON a.user_id = u.id WHERE u.username = ?1",
            countQuery = "SELECT count(*) FROM blog_article a INNER JOIN blog_user u  ON a.user_id = u.id WHERE u.username = ?1",
            nativeQuery = true)
    Page<Article> findArticlesByUsernameOfPage(String username, Pageable pageable) ;

    // 通过用户名和文章状态找文章
    @Query(value = "SELECT * FROM blog_article a INNER JOIN blog_user u  ON a.user_id = u.id WHERE u.username = ?1 AND a.status = ?2",
            countQuery = "SELECT count(*) FROM blog_article a INNER JOIN blog_user u  ON a.user_id = u.id WHERE u.username = ?1 AND a.status = ?2",
            nativeQuery = true)
    Page<Article> findArticlesByUsernameAndStatusOfPage(String username, String status, Pageable pageable) ;

    // 通过昵称和文章状态找文章
    @Query(value = "SELECT * FROM blog_article a INNER JOIN blog_user u  ON a.user_id = u.id WHERE u.nickname = ?1 AND a.status = ?2",
            countQuery = "SELECT count(*) FROM blog_article a INNER JOIN blog_user u  ON a.user_id = u.id WHERE u.nickname = ?1 AND a.status = ?2",
            nativeQuery = true)
    Page<Article> findArticlesByNicknameAndStatusOfPage(String nickname, String status, Pageable pageable) ;

    // 获取下一遍文章
    @Query(value = "SELECT * FROM blog_article a WHERE a.post_id > ?1", nativeQuery = true)
    List<Article> findArticlesOverPostId(Long post_id);

    // 获取上一遍文章
    @Query(value = "SELECT * FROM blog_article a WHERE a.post_id < ?1", nativeQuery = true)
    List<Article> findArticlesLessPostId(Long post_id);

    // 通过目录名查找文章
    @Query(value = "SELECT * FROM blog_article a INNER JOIN blog_category c ON a.category_id = c.id WHERE c.name = ?1 AND a.status = ?2",
            countQuery = "SELECT count(*) FROM blog_article a INNER JOIN blog_category c ON a.category_id = c.id WHERE c.name = ?1 AND a.status = ?2",
            nativeQuery = true)
    Page<Article> findArticlesByCategoryNameAndStatus(String categoryName, String status, Pageable pageable);

    // 通过标签名查找文章
    @Query(value = "SELECT * FROM blog_article a INNER JOIN blog_tag t ON a.tag_id = t.id WHERE t.name = ?1 AND a.status = ?2",
            countQuery = "SELECT count(*) FROM blog_article a INNER JOIN blog_tag t ON a.tag_id = t.id WHERE t.name = ?1 AND a.status = ?2",
            nativeQuery = true)
    Page<Article> findArticlesByTagNameAndStatus(String tagName, String status, Pageable pageable);

    // 用户通过关键字搜索
    @Query(value = "SELECT * FROM blog_article a WHERE (a.title LIKE CONCAT('%',?1,'%') OR a.summary LIKE CONCAT('%',?1,'%') OR a.body LIKE CONCAT('%',?1,'%')) AND a.status = ?2",
            countQuery = "SELECT count(*) FROM blog_article a WHERE (a.title LIKE CONCAT('%',?1,'%') OR a.summary LIKE CONCAT('%',?1,'%') OR a.body LIKE CONCAT('%',?1,'%')) AND a.status = ?2",
            nativeQuery = true)
    Page<Article> findArticlesByKeyWordAndStatus(String s, String status, Pageable pageable) ;
}
