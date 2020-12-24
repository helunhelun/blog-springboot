package cn.ivdone.blog.service;

import cn.ivdone.blog.entity.Article;
import cn.ivdone.blog.entity.User;
import cn.ivdone.blog.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository ;

    @Autowired
    private UserService userService ;

    // 获取所有的文章
    public List<Article> findAllArticle(){
        List<Article> articles = articleRepository.findAll() ;
        return articles ;
    }

    // 通过文章号删除文章
    public Boolean deleteArticleById(Long post_id){
        Article article = articleRepository.findArticleByPostId(post_id);
        if(article != null){
            articleRepository.deleteArticleByPostId(post_id);
            return true ;
        }
        else{
            return false ;
        }
    }


    // 通过文章号来查找文章
    public Article findArticleByPostId(Long post_id){
        return articleRepository.findArticleByPostId(post_id);
    }

    // 保存文章
    public void saveArticle(Article article){
        articleRepository.saveAndFlush(article) ;
    }

    // 查找全部分页文章
    public Page<Article> findAllArticleOfPage(PageRequest pr){
        return articleRepository.findAllArticleOfPage(pr) ;
    }

    // 通过文章状态分页查找
    public Page<Article> findArticlesByStatusOfPage(Pageable pageable, String status){
        return articleRepository.findArticlesByStatusOfPage(status, pageable) ;
    }

    // 通过用户名来查找分页文章
    public Page<Article> findArticlesByUsernameOfPage(Pageable pageable, String username){
        return articleRepository.findArticlesByUsernameOfPage(username, pageable) ;
    }

    // 通过用户名和文章状态分页查找文章
    public  Page<Article> findArticlesByUsernameAndStatusOfPage(Pageable pageable, String username, String status){
        return articleRepository.findArticlesByUsernameAndStatusOfPage(username, status, pageable) ;
    }

    // 通过昵称和文章状态分页查找文章
    public  Page<Article> findArticlesByNicknameAndStatusOfPage(Pageable pageable, String nickname, String status){
        return articleRepository.findArticlesByNicknameAndStatusOfPage(nickname, status, pageable) ;
    }

    // 下一篇文章
    public Article findArticleOverPostId(Long post_id){
        List<Article> articles = articleRepository.findArticlesOverPostId(post_id) ;
        if (articles.size() > 0){
            return articles.get(0);
        }
        return null ;
    }
    // 上一篇文章
    public Article findArticleLessPostId(Long post_id){
        List<Article> articles = articleRepository.findArticlesLessPostId(post_id) ;
        if (articles.size() > 0){
            return articles.get(0) ;
        }
        return null ;
    }

    // 通过目录名查找文章
    public Page<Article> findArticlesByCategoryNameAndStatus(String categoryName, String status, Pageable pageable){
        return articleRepository.findArticlesByCategoryNameAndStatus(categoryName, status, pageable) ;
    }

    // 通过标签名查找文章
    public Page<Article> findArticlesByTagNameAndStatus(String tagName, String status, Pageable pageable){
        return articleRepository.findArticlesByTagNameAndStatus(tagName, status, pageable) ;
    }

    // 查询所有公开的文章
    public List<Article> findArticlesByStatus(String status){
        return articleRepository.findArticlesByStatus(status) ;
    }

    // 通过关键字来检索文章
    public Page<Article> findArticlesByKeyWordAndStatus(String s, String status, Pageable pageable){
        return articleRepository.findArticlesByKeyWordAndStatus(s, status, pageable) ;
    }

}
