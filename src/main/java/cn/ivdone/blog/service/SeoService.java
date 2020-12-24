package cn.ivdone.blog.service;

import cn.ivdone.blog.entity.Article;
import cn.ivdone.blog.entity.Category;
import cn.ivdone.blog.entity.Tag;
import cn.ivdone.blog.entity.User;
import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;


// 用于引擎 sitemap地图
@Service
public class SeoService {

    // 文章
    @Autowired
    private ArticleService articleService ;
    // 分类
    @Autowired
    private CategoryService categoryService ;
    // 标签
    @Autowired
    private TagService tagService ;
    // 用户与作者
    @Autowired
    private UserService userService ;

    public String createSiteMapXmlContent() throws MalformedURLException, ParseException {
        String baseUrl = String.format("https://www.ivdone.cn") ;
        // 定义时间格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") ;
        WebSitemapGenerator wsg = new WebSitemapGenerator("https://www.ivdone.cn");
        // 首页
        WebSitemapUrl url = new WebSitemapUrl.Options(baseUrl)
                .lastMod(dateTimeFormatter.format(LocalDateTime.now())).priority(1.0).changeFreq(ChangeFreq.DAILY).build();
        wsg.addUrl(url) ;

        // 文章页

        List<Article> articleList = articleService.findArticlesByStatus("p") ;

        for (Article article : articleList){
            // Date -> LocalDateTime
            LocalDateTime tmpTime = LocalDateTime.ofInstant(article.getCreate_time().toInstant(), ZoneId.systemDefault()) ;
            WebSitemapUrl tmpUrl = new WebSitemapUrl.Options(baseUrl+"/article/"+article.getPost_id()+".html")
                    .lastMod(dateTimeFormatter.format(tmpTime)).priority(0.6).changeFreq(ChangeFreq.DAILY).build();
            wsg.addUrl(tmpUrl) ;
        }

        // 分类页

        Set<Category> categoryList = categoryService.findCategoriesHasArticle() ;

        for (Category category : categoryList){
            WebSitemapUrl tmpUrl = new WebSitemapUrl.Options(baseUrl+"/category/"+category.getName())
                    .lastMod(dateTimeFormatter.format(LocalDateTime.now())).priority(0.6).changeFreq(ChangeFreq.DAILY).build();
            wsg.addUrl(tmpUrl) ;
        }

        // 标签页

        Set<Tag> tagSet = tagService.findTagsHasArticle() ;

        for (Tag tag : tagSet){
            WebSitemapUrl tmpUrl = new WebSitemapUrl.Options(baseUrl+"/tag/"+tag.getName())
                    .lastMod(dateTimeFormatter.format(LocalDateTime.now())).priority(0.6).changeFreq(ChangeFreq.DAILY).build();
            wsg.addUrl(tmpUrl) ;
        }

        // 作者页

        List<User> userList = userService.findAllUser() ;

        for (User user : userList){
            WebSitemapUrl tmpUrl = new WebSitemapUrl.Options(baseUrl+"/author/"+user.getNickname())
                    .lastMod(dateTimeFormatter.format(LocalDateTime.now())).priority(0.6).changeFreq(ChangeFreq.DAILY).build();
            wsg.addUrl(tmpUrl) ;
        }
        // System.out.println(String.join("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", wsg.writeAsStrings()));

        return String.join("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", wsg.writeAsStrings()) ;

    }

}
