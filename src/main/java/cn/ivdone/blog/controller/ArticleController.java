package cn.ivdone.blog.controller;


import cn.ivdone.blog.entity.Article;
import cn.ivdone.blog.entity.Category;
import cn.ivdone.blog.entity.Tag;
import cn.ivdone.blog.service.ArticleService;
import cn.ivdone.blog.service.CategoryService;
import cn.ivdone.blog.service.TagService;
import cn.ivdone.blog.service.UserService;
import cn.ivdone.blog.util.ErrorUtil;
import cn.ivdone.blog.util.MarkdownUtil;
import cn.ivdone.blog.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


import java.util.*;


@Controller
@Service
public class ArticleController {

    // 文章服务
    @Autowired
    private ArticleService articleService ;
    // 分类服务
    @Autowired
    private CategoryService categoryService ;
    // 标签
    @Autowired
    private TagService tagService ;
    // 用户
    @Autowired
    private UserService userService ;

    /**
     * 用户接口 主页   分页
     * @param number 分页的参数
     * @param mv 渲染对象
     * @return 渲染对象 调用index方法 page等于number
     */
    @GetMapping("/index/page/{number}")
    @Cacheable(value = "index-for-page")
    public ModelAndView indexForPage(@PathVariable("number") Integer number, ModelAndView mv){
        if (number == null){
            number = 0 ;
        }
        return this.index(mv, number) ;
    }

    /**
     * 首页
     * @param mv  渲染对象
     * @return 渲染对象 调用index方法 page视为0
     */
    @GetMapping("/")
    public ModelAndView indexForFirst(ModelAndView mv){
        return this.index(mv, 0) ;
    }

    /**
     * 错误页
     * @param mv 渲染对象
     * @return 渲染对象 错误也渲染对象
     */
    @GetMapping("/error")
    public ModelAndView error(ModelAndView mv){
        return ErrorUtil.error4xx(mv) ;
    }


    /**
     * 用户请求文章详情页
     * @param post_id 文章的post id
     * @param mv 渲染对象
     * @return 渲染对象 /blog/detail.html
     */

    @GetMapping("/article/{post_id}.html")
    public ModelAndView detail(@PathVariable("post_id") Long post_id, ModelAndView mv){
        if (post_id == null){
            return  ErrorUtil.error4xx(mv) ;
        }
        Article article = articleService.findArticleByPostId(post_id) ;
        // 找不到文章 404
        if (article == null){
            return ErrorUtil.error4xx(mv) ;
        }
        // 浏览量加1
        article.viewed();
        articleService.saveArticle(article);
        // markdown to html
        article.setBody(MarkdownUtil.markdownExtToHtml(article.getBody()));
        ModelAndView modelAndView = publicSection(mv) ;
        // 上, 下文章
        Article preArticle = articleService.findArticleLessPostId(post_id) ;
        Article nextArticle = articleService.findArticleOverPostId(post_id) ;
        // 渲染
        modelAndView.addObject("preArticle", preArticle) ;
        modelAndView.addObject("nextArticle", nextArticle) ;
        modelAndView.addObject("article", article) ;
        modelAndView.setViewName("blog/detail");
        return modelAndView ;
    }

    /**
     *  用户通过目录名来寻找相关目录的文章
     * @param categoryName 目录名
     * @param mv 渲染对象
     * @param page 用户 请求的 分页
     * @return 渲染对象  /blog/category.html
     * @throws Exception 异常类型
     */
    @GetMapping("/category/{name}")
    public ModelAndView categorySearch(@PathVariable(value = "name") String categoryName,
                                       @RequestParam(value = "page", required = false) Integer page,
                                       ModelAndView mv) throws  Exception{
        if (page == null){
            page = 0 ;
        }
        if (categoryName == null){
            return  ErrorUtil.error4xx(mv) ;
        }
        // 获取文章
        Sort sort = Sort.by(Sort.Direction.DESC, "create_time") ;// 分页处理
        PageRequest pr = PageRequest.of(page, 10, sort) ;
        Page<Article> articlesPage = articleService.findArticlesByCategoryNameAndStatus(categoryName, "p", pr) ;
        ModelAndView modelAndView = publicSection(mv) ;
        modelAndView.addObject("articlesPage", articlesPage) ;
        modelAndView.addObject("categoryName", categoryName) ;
        modelAndView.setViewName("blog/category");
        return modelAndView ;
    }

    /**
     *  用户通过标签来寻找相关标签的文章
     * @param tagName 标签名
     * @param page 用户 请求的 分页
     * @param mv  渲染对象
     * @return 渲染对象  /blog/tag.html
     */

    @GetMapping(value = "/tag/{tagName}")
    public ModelAndView tagSearch(@PathVariable(value = "tagName") String tagName,
                                  @RequestParam(value = "page", required = false) Integer page,
                                  ModelAndView mv){
        if (tagName == null){
            return ErrorUtil.error4xx(mv) ;
        }
        if (page == null){
            page = 0 ;
        }
        // 获取文章
        Sort sort = Sort.by(Sort.Direction.DESC, "create_time") ;// 分页处理
        PageRequest pr = PageRequest.of(page, 10, sort) ;
        Page<Article> articlesPage = articleService.findArticlesByTagNameAndStatus(tagName, "p", pr) ;
        // 视图渲染
        ModelAndView modelAndView = publicSection(mv) ;
        modelAndView.addObject("tagName", tagName);
        modelAndView.addObject("articlesPage", articlesPage) ;
        modelAndView.setViewName("blog/tag");
        return  modelAndView ;
    }

    /**
     * 用户通过作者的昵称寻找作者的文章
     * @param nickname 作者的昵称
     * @param page 要查找的页数
     * @param mv 渲染对象
     * @return 渲染对象  html文件在 /blog/author.html 下
     */

    @GetMapping("/author/{nickname}")
    public ModelAndView authorSearch(@PathVariable("nickname") String nickname,
                                     @RequestParam(value = "page", required = false) Integer page,
                                     ModelAndView mv){
        if (page == null){
            page = 0 ;
        }
        if (nickname == null){
            return ErrorUtil.error4xx(mv) ;
        }
        // 获取文章
        Sort sort = Sort.by(Sort.Direction.DESC, "create_time") ;// 分页处理
        PageRequest pr = PageRequest.of(page, 10, sort) ;
        Page<Article> articlesPage = articleService.findArticlesByNicknameAndStatusOfPage(pr, nickname, "p") ;
        ModelAndView modelAndView = publicSection(mv) ;
        modelAndView.addObject("articlesPage", articlesPage) ;
        modelAndView.addObject("nickname", nickname) ;
        modelAndView.setViewName("blog/author");
        return modelAndView ;
    }

    /**
     * 用户通过关键词搜索文章
     * @param s 用户搜索的关键词
     * @param page 分页的页数
     * @param mv 渲染对象
     * @return 渲染对象
     */
    @GetMapping("/search")
    public ModelAndView searchFuncion(@RequestParam("s") String s,
                                      @RequestParam(value = "page", required = false) Integer page,
                                      ModelAndView mv){
        if (s == null){
            return  ErrorUtil.error4xx(mv) ;
        }
        if (page == null){
            page = 0 ;
        }
        // 获取文章
        Sort sort = Sort.by(Sort.Direction.DESC, "create_time") ;// 分页处理
        PageRequest pr = PageRequest.of(page, 10, sort) ;
        // 通过mysql中like语句 模糊查找
        Page<Article> articlesPage = articleService.findArticlesByKeyWordAndStatus(s, "p", pr) ;

        // 增加渲染对象
        ModelAndView modelAndView = publicSection(mv) ;
        modelAndView.addObject("articlesPage", articlesPage) ;
        modelAndView.addObject("s", s) ;
        modelAndView.setViewName("blog/search") ;
        return  modelAndView ;

    }

    /**
     * 获取文章归档
     * @param mv 渲染对象
     * @return 渲染对象
     */
    @GetMapping(value = "/archives")
    public ModelAndView archives(ModelAndView mv){
        // dataMap : HashMap 类型  -> { [2020 , 10] : [article0, article1, ...], [2019, 5] : [article0, article1, ...]  }
        Map<List, List> dataMap = new HashMap<>();
        // ym Set类型 -> { [2020 , 10], [2020 , 11], [2019, 5] ... }
        Set<List> ym = new HashSet<>() ;
        // 获取所有公开的文章
        List<Article> articles = articleService.findArticlesByStatus("p");
        // 获取日历 用户获取文章的发表年份 和 月份
        Calendar cal = Calendar.getInstance();
        for (Article article : articles){
            List<Integer> tmpList = new ArrayList<>() ;
            cal.setTime(article.getCreate_time());
            tmpList.add(cal.get(Calendar.YEAR)) ;
            tmpList.add(cal.get(Calendar.MONTH));
            ym.add(tmpList) ;
        }
        // ym Set类型 -> { [2020 , 10], [2020 , 11], [2019, 5] ... }

        for (List<Integer> i : ym) {
            List<Article> articleList = new ArrayList<>() ;
            for (Article article : articles){
                cal.setTime(article.getCreate_time());
                Integer year = cal.get(Calendar.YEAR);
                Integer month = cal.get(Calendar.MONTH);
                Integer oYear = i.get(0) ;
                Integer oMonth = i.get(1) ;
                //System.out.println(oYear);
                //System.out.println(year);
                if (oYear.equals(year) && oMonth.equals(month)){
                    //System.out.println("hello");
                    articleList.add(article) ;
                }
            }
            dataMap.put(i, articleList) ;
        }
        // dataMap List -> List
        ModelAndView modelAndView = publicSection(mv) ;
        modelAndView.addObject("dataMap", dataMap) ;
        modelAndView.setViewName("blog/archives");
        return modelAndView ;
    }

    /**
     * 通过分页来获取文章主页的部分
     * @param mv 文章渲染对象
     * @param page 用户请求的分页参数
     * @return 文章渲染对象 /blog/index.html
     */
    private ModelAndView index(ModelAndView mv, int page){
        // 获取文章
        Sort sort = Sort.by(Sort.Direction.DESC, "create_time") ;// 分页处理
        PageRequest pr = PageRequest.of(page, 10, sort) ;
        Page<Article> articlesPage = articleService.findArticlesByStatusOfPage(pr, "p") ;
        ModelAndView modelAndView = publicSection(mv) ;
        // 把所有的对象渲染到视图中去
        modelAndView.addObject("articlesPage", articlesPage) ;
        modelAndView.setViewName("blog/index");
        return modelAndView ;
    }

    /**
     * 文章页公共部分
     * @param mv 渲染视图对象
     * @return 渲染视图对象
     */
    private ModelAndView publicSection(ModelAndView mv){
        // 最新的文章
        Sort sort = Sort.by(Sort.Direction.DESC, "create_time") ;// 分页处理
        PageRequest pr = PageRequest.of(0, 8, sort) ;  // 最新的文章8篇
        Page<Article> articlesNew = articleService.findArticlesByStatusOfPage(pr, "p") ;
        // 随机文章
        sort = Sort.by(Sort.Direction.DESC, "create_time") ;// 分页处理
        pr = PageRequest.of(0, 10000, sort) ;
        List<Article> allArticle = articleService.findArticlesByStatusOfPage(pr, "p").getContent() ;
        List<Article> randomArticles = RandomUtil.getRandomList(allArticle, allArticle.size()-1);

        // 浏览最多的文章
        sort = Sort.by(Sort.Direction.DESC, "views") ;// 分页处理
        pr = PageRequest.of(0, 10, sort) ;  // 去浏览最多的10篇文章
        Page<Article> articlesMostView = articleService.findArticlesByStatusOfPage(pr, "p") ;

        // 查找有文章的所有分类 与 标签
        Set<Category> categoryList = categoryService.findCategoriesHasArticle() ;
        Set<Tag> tagList = tagService.findTagsHasArticle() ;
        // 把所有的对象渲染到视图中去
        mv.addObject("articlesNew", articlesNew) ;
        mv.addObject("articlesMostView", articlesMostView) ;
        mv.addObject("randomArticles", randomArticles);
        mv.addObject("categoryList", categoryList) ;
        mv.addObject("tagList", tagList) ;
        return  mv ;
    }
}
