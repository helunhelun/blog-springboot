package cn.ivdone.blog.service;

import cn.ivdone.blog.entity.Article;
import cn.ivdone.blog.entity.Category;
import cn.ivdone.blog.repository.CategoryRepository ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set ;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository ;

    @Autowired
    private ArticleService articleService ;
    // 由目录名找目录
    public Category findCategoryByName(String name){
        return categoryRepository.findCategoryByName(name) ;
    }
    // 获取所有目录
    public List<Category> findAllCategory(){
        return categoryRepository.findAll() ;
    }

    // 获取目录库中有文章的目录
    public Set<Category> findCategoriesHasArticle(){
        Set<Category> categorySet = new HashSet<>() ;
        List<Category> categoryList = categoryRepository.findCategoriesHasArticle() ;
        categorySet.addAll(categoryList) ;
        return categorySet ;
    }

    // 保存目录
    public void saveCategory(Category category){
        categoryRepository.saveAndFlush(category) ;
    }

    // 由分类名删除分类
    public boolean deleteCategoryByName(String name){
        // 从数据库中找到这个分类对象
        Category category = categoryRepository.findCategoryByName(name) ;
        if (category == null){
            return false ;
        }
        //categoryRepository.deleteCategoryByName(name);
        //  从所有文章中属于这个分类的文章 指向新的 默认分类
        for (Article article : articleService.findAllArticle()){
            if (article.getCategory().getName().equals(name)){
                Category categoryDefault ;
                // 新建默认
                if (categoryRepository.findCategoryByName("default") == null){
                    categoryDefault = new Category();
                    categoryDefault.setName("default");
                    categoryRepository.saveAndFlush(categoryDefault) ;
                }
                else {
                    categoryDefault = categoryRepository.findCategoryByName("default") ;
                }
                // 指向默认
                article.setCategory(categoryDefault);
                // 保存到数据库
                articleService.saveArticle(article);
            }
        }
        // 删除分类
        categoryRepository.delete(category);
        return true ;
    }
}
