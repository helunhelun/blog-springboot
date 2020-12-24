package cn.ivdone.blog.service;

import cn.ivdone.blog.entity.Article;
import cn.ivdone.blog.entity.Category;
import cn.ivdone.blog.entity.Tag;
import cn.ivdone.blog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository ;

    @Autowired
    private ArticleService articleService ;

    public Tag findTagByName(String name){
        return tagRepository.findTagByName(name) ;
    }

    public List<Tag> findAllTag(){
        return tagRepository.findAll() ;
    }

    // 获取标签库中有文章的标签
    public Set<Tag> findTagsHasArticle(){

        Set<Tag> tagSet = new HashSet<>() ;
        List<Tag> tagList = tagRepository.findTagsHasArticle();
        tagSet.addAll(tagList) ;
        return tagSet ;
    }

    // 保存标签
    public void saveTag(Tag tag){
        tagRepository.saveAndFlush(tag) ;
    }

    // 由标签名删除标签
    public boolean deleteTagByName(String name){
        // 从数据库中找到这个分类对象
        Tag tag = tagRepository.findTagByName(name) ;
        if (tag == null){
            return false ;
        }
        //categoryRepository.deleteCategoryByName(name);
        //  从所有文章中属于这个标签的文章 指向新的 默认标签
        for (Article article : articleService.findAllArticle()){
            if (article.getTag().getName().equals(name)){
                Tag tagDefault ;
                // 新建默认
                if (tagRepository.findTagByName("default") == null){
                    tagDefault = new Tag();
                    tagDefault.setName("default");
                    tagRepository.saveAndFlush(tagDefault) ;
                }
                else {
                    tagDefault = tagRepository.findTagByName("default") ;
                }
                // 指向默认
                article.setTag(tagDefault);
                // 保存到数据库
                articleService.saveArticle(article);
            }
        }
        // 删除标签
        tagRepository.delete(tag);
        return true ;
    }
}
