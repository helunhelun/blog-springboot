package cn.ivdone.blog.repository;

import cn.ivdone.blog.entity.Category ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "SELECT * FROM blog_category WHERE name = ?1", nativeQuery = true)
    Category findCategoryByName(String name) ;

    // 获取目录库中有文章的目录
    @Query(value = "SELECT * FROM blog_category c INNER JOIN blog_article a ON a.category_id = c.id", nativeQuery = true)
    List<Category> findCategoriesHasArticle();

    // 由分类名来删除分类
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM blog_category c WHERE c.name = ?1", nativeQuery = true)
    void deleteCategoryByName(String name) ;

}
