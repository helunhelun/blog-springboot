package cn.ivdone.blog.controller;

import cn.ivdone.blog.entity.*;
import cn.ivdone.blog.service.* ;

import cn.ivdone.blog.util.ErrorUtil;
import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/wp-admin")
public class AdminController {

    @Autowired
    private UserService userService ;
    @Autowired
    private CategoryService categoryService ;
    @Autowired
    private TagService tagService ;
    @Autowired
    private ArticleService articleService ;
    // 图像库服务类
    @Autowired
    private ImageLibraryService imageLibraryService ;


    /**
     * 管理后台 主页
     * @param mv 用于渲染模板
     * @return 渲染模板的对象
     */
    @GetMapping("/index")
    public ModelAndView index(ModelAndView mv){
        //String username = SecurityUtils.getSubject().getSession().getAttribute("username").toString() ;
        //User user = userService.findUserByUsername(username) ;
        //mv.addObject("user", user) ;
        mv.setViewName("admin/index") ;
        return mv ;
    }

    /**
     * 关于我  总文章 公开文章 草稿文章  用户信息
     * @param mv 用于渲染模板
     * @return 渲染模板的对象 (1)总文章 (2)公开文章 (3)草稿文章  (4)用户对象
     */
    @GetMapping("/about")
    public ModelAndView about(ModelAndView mv, Authentication authentication){
        String username = authentication.getName() ;
        User user = userService.findUserByUsername(username) ;
        // 分页处理
        Sort sort = Sort.by(Sort.Direction.DESC, "create_time") ;
        PageRequest pr = PageRequest.of(1, 1, sort) ;
        // 公开文章数 草稿文章  ->  全部文章 = 公开文章数 + 草稿文章数
        long publicArticleCount = articleService.findArticlesByUsernameAndStatusOfPage(pr, username, "p").getTotalElements() ;
        long draftArticleCount = articleService.findArticlesByUsernameAndStatusOfPage(pr, username, "d").getTotalElements() ;
        long totalArticleCount = publicArticleCount + draftArticleCount ;
        // 往视图html中渲染
        mv.addObject("user", user) ;
        mv.addObject("publicArticleCount", publicArticleCount) ;
        mv.addObject("draftArticleCount", draftArticleCount) ;
        mv.addObject("totalArticleCount", totalArticleCount) ;
        // 设置html
        mv.setViewName("admin/about");
        return mv ;
    }

    /**
     *
     * @param page 请求的分页
     * @param status 请求需要的文章状态 "p" -> 公开 "d" -> 草稿 "a" -> 所有文章
     * @param mv  用于渲染模板
     * @return 渲染模板的对象
     */
    @GetMapping("/ArticleAdmin")
    public ModelAndView articleAdmin(@RequestParam(value = "page", required = false) Integer page,
                                     @RequestParam(value = "post_status", required = false) String status,
                                     ModelAndView mv,
                                     Authentication authentication){
        // 用于渲染html界面的分页处理 全部文章 callStatus -> "a" 草稿文章 callStatus -> "d"  公开文章 callStatus -> "p"
        String callStatus ;
        // 获取访问的用户名
        String username = authentication.getName() ;
        if (page == null){
            page = 0 ;
        }
        // 建立一个分页文章的对象
        Page<Article> articlePage ;
        // 分页处理
        Sort sort = Sort.by(Sort.Direction.DESC, "create_time") ;
        PageRequest pr = PageRequest.of(page, 4, sort) ;
        // 文章数据库查找
        if (status == null || (!status.equals("p") && !status.equals("d"))){
             //articlePage = articleService.findAllArticleOfPage(pr) ;
            articlePage = articleService.findArticlesByUsernameOfPage(pr, username);
            callStatus = "a" ;
        }
        else {
             //articlePage = articleService.findArticlesByStatusOfPage(pr, status) ;
            articlePage = articleService.findArticlesByUsernameAndStatusOfPage(pr, username, status) ;
            callStatus = status ;
        }
        // 公开文章数 草稿文章  ->  全部文章 = 公开文章数 + 草稿文章数
        //long publicArticleCount = articleService.findArticlesByStatusOfPage(pr, "p").getTotalElements() ;
        //long draftArticleCount = articleService.findArticlesByStatusOfPage(pr, "d").getTotalElements() ;
        long publicArticleCount = articleService.findArticlesByUsernameAndStatusOfPage(pr, username, "p").getTotalElements() ;
        long draftArticleCount = articleService.findArticlesByUsernameAndStatusOfPage(pr, username, "d").getTotalElements() ;

        long totalArticleCount = publicArticleCount + draftArticleCount ;
        // 页面对象渲染
        mv.addObject("articlePage", articlePage) ;
        mv.addObject("publicArticleCount", publicArticleCount) ;
        mv.addObject("draftArticleCount", draftArticleCount) ;
        mv.addObject("totalArticleCount", totalArticleCount) ;
        mv.addObject("callStatus", callStatus) ;
        // 指定html渲染文件
        mv.setViewName("admin/admin");
        return  mv ;
    }

    /**
     * 写文章视图展示
     * @param mv 用于渲染模板
     * @return 渲染模板的对象
     */
    @GetMapping("/ArticleCreate")
    public ModelAndView articleCreate(ModelAndView mv, Authentication authentication){
        Map<String, Object>  modelMap = new HashMap<String, Object>() ;
        Long new_post_id = new Long(2) ;
        // 获取用户名
        String username = authentication.getName() ;
        // 获取所有的用户 目录 标签
        User user = userService.findUserByUsername(username) ;
        //List<User> users = userService.findAllUser() ;
        List<Article> articles = articleService.findAllArticle() ;
        List<Category> categories = categoryService.findAllCategory() ;
        List<Tag> tags = tagService.findAllTag() ;
        // 获取图像库中的数据
        Sort sort = Sort.by(Sort.Direction.DESC, "create_time") ;
        PageRequest pr = PageRequest.of(0, 1000, sort) ;
        Page<ImageLibrary> imageLibraryPage = imageLibraryService.findImageLibrariesHasUserOfPage(pr) ;
        //String path = ClassUtils.getDefaultClassLoader().getResource("").getPath() ;
        //System.out.println(path);
        // post_id 随机增加3~6
        if (articles.size() > 0){
            for (Article article : articles){
                if (new_post_id < article.getPost_id()){
                    new_post_id = article.getPost_id() ;
                }
            }
            new_post_id += Math.round(3 + Math.random()*3) ;
        }
        modelMap.put("user", user) ;
        //modelMap.put("users", users) ;
        modelMap.put("imageLibraryPage", imageLibraryPage) ;
        modelMap.put("categories", categories) ;
        modelMap.put("tags", tags) ;
        modelMap.put("new_post_id", new_post_id) ;
        //modelMap.put("new_post_id", new_post_id) ;
        //mv.addObject("categories", categories) ;
        mv.addAllObjects(modelMap) ;
        mv.setViewName("admin/create");
        return  mv ;
    }

    /**
     * 修改文章
     * @param post_id 从http请求中 获取文章id号 用于修改文章
     * @param mv 用于渲染模板
     * @return 渲染模板的对象
     */
    @GetMapping("/modify/{post_id}.html")
    public ModelAndView articleModify(@PathVariable("post_id") Long post_id, ModelAndView mv, Authentication authentication){
        Map<String, Object> modelMap = new HashMap<String, Object>() ;
        // 获取用户名
        String username = authentication.getName() ;
        // 新建一个用户与文章
        User user ;
        Article article ;
        // 数据库异常处理
        try{
            user = userService.findUserByUsername(username) ;
            article = articleService.findArticleByPostId(post_id);
        }
        catch (Exception e){
            // 抛出5xx界面
            return ErrorUtil.error5xx(mv) ;
        }

        if (!article.getUser().getUsername().equals(username)){
            // 向客户端返回4xx
            return ErrorUtil.error4xx(mv) ;
        }
        // 获取图像库中的数据
        Sort sort = Sort.by(Sort.Direction.DESC, "create_time") ;
        PageRequest pr = PageRequest.of(0, 1000, sort) ;
        Page<ImageLibrary> imageLibraryPage = imageLibraryService.findImageLibrariesHasUserOfPage(pr) ;

        //List<User> users = userService.findAllUser() ;
        List<Category> categories = categoryService.findAllCategory() ;
        List<Tag> tags = tagService.findAllTag() ;
        String imageName = article.getImageLibrary() != null ? article.getImageLibrary().getName() : "" ;
        // 渲染到视图
        modelMap.put("imageName", imageName) ;
        modelMap.put("imageLibraryPage", imageLibraryPage) ;
        modelMap.put("article", article) ;
        modelMap.put("user", user) ;
        modelMap.put("categories", categories) ;
        modelMap.put("tags", tags) ;
        mv.addAllObjects(modelMap);
        mv.setViewName("admin/modify");
        return mv ;
    }

    /**
     * 删除文章
     * @param post_id 从http请求中 获取文章id号
     * @param redirectView 请求回应 用于给客户端重定向
     */
    @PostMapping(value = "/ArticleDelete")
    public RedirectView deleteArticle(@RequestParam(value = "delete_post_id") Long post_id,
                                      RedirectView redirectView, Authentication authentication){
        //System.out.println(post_id);
        redirectView.setContextRelative(true);
        String username = authentication.getName() ;
        // 判断用户是否有删除该文章的权限
        if (post_id == null || !articleService.findArticleByPostId(post_id).getUser().getUsername().equals(username)){
            redirectView.setUrl("/error");
            return  redirectView ;
        }
        // 删除不成功 则返回错误页
        if (!articleService.deleteArticleById(post_id)){
            redirectView.setUrl("/error");
            return  redirectView ;
        }
        // 刷新文章管理的界面
        redirectView.setUrl("/wp-admin/ArticleAdmin");
        return  redirectView ;
    }

    /**
     * 提交文章 向客户端返回json格式
     * @param categoryName 从http中post请求的目录名
     * @param tagName 标签名
     * @param username 用户名
     * @param typeName 请求类型  1 -> "create" 2 -> "modify"
     * @param post_id 从http请求中 获取文章id号
     * @param title 文章标题
     * @param summary 文章摘要
     * @param body 文章主要内容
     * @param status 文章状态  公开 -> "p" 草稿 -> "d"
     * @return  json格式  { "state" : "success"/"error" , "info" : "xxx" } -> json格式
     */
    @PostMapping(value = "/ArticleSubmit", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String articleSubmit(@RequestParam(value = "category") String categoryName,
                                @RequestParam(value = "tag") String tagName,
                                @RequestParam(value = "author") String username,
                                @RequestParam(value = "type") String typeName,
                                @RequestParam(value = "post_id") Long post_id,
                                @RequestParam(value = "title") String title,
                                @RequestParam(value = "summary") String summary,
                                @RequestParam(value = "body") String body,
                                @RequestParam(value = "image") String imageName,
                                @RequestParam(value = "article_status") String status,
                                Authentication authentication){
        JSONObject resultJson = new JSONObject() ;
        // 区分username  userName为从session中获取的用户名 username为用户文章申请的用户名
        String userName = authentication.getName() ;
        // 用户名验证处理
        if (!userName.equals(username)){
            resultJson.put("state", "error") ;
            resultJson.put("info", "用户信息不一致, 请重新提交") ;
            return resultJson.toJSONString() ;
        }
        // 提交字段为空的处理
        if (status == null || (!status.equals("p") && !status.equals("d")) ||
                title.equals("") || summary.equals("") || body.equals("") ){
            resultJson.put("state", "error") ;
            resultJson.put("info", "字段为空, 请重新提交") ;
            return resultJson.toJSONString() ;
        }
        Category category = categoryService.findCategoryByName(categoryName) ;
        Tag tag = tagService.findTagByName(tagName) ;
        User user = userService.findUserByUsername(username) ;
        if (category == null || tag == null || user == null){
            resultJson.put("state", "error") ;
            resultJson.put("info", "无法找到相关的数据") ;
            return resultJson.toJSONString() ;
        }
        // 文章关联的图片
        ImageLibrary imageLibraryObject ;
        imageLibraryObject = imageLibraryService.findImageLibraryByName(imageName) ;
        // 新建文章
        if (typeName.equals("create")){
            Long views = new Long(0);
            Article article = new Article(post_id, views,
                    title, summary, body, status,
                    category, user, tag) ;
            if (imageLibraryObject != null){
                article.setImageLibrary(imageLibraryObject);
            }
            articleService.saveArticle(article);
        }
        // 修改文章
        else if (typeName.equals("modify")) {

            Article article = articleService.findArticleByPostId(post_id) ;
            Long views = article.getViews();
            if (article == null){
                resultJson.put("state", "error") ;
                resultJson.put("info", "无法找到该文章") ;
                return resultJson.toJSONString() ;
            }
            article.modifyAll(post_id, views,
                    title, summary, body, status,
                    category, user, tag);

            article.setImageLibrary(imageLibraryObject);

            articleService.saveArticle(article);
        }
        // 其他类型
        else {
            resultJson.put("state", "error") ;
            resultJson.put("info", "请输入正确的表单") ;
            return resultJson.toJSONString() ;
        }
        // 向前端显示成功
        resultJson.put("state", "success") ;
        resultJson.put("info", "提交成功");
        return  resultJson.toJSONString() ;
    }

}
