package cn.ivdone.blog.controller;

import cn.ivdone.blog.entity.Category;
import cn.ivdone.blog.entity.Tag;
import cn.ivdone.blog.service.CategoryService;
import cn.ivdone.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@RequestMapping("/wp-admin")
public class CategoryController {

    @Autowired
    private CategoryService categoryService ;

    @Autowired
    private TagService tagService ;

    @GetMapping(value = "/ct", produces = "text/html;charset=UTF-8")
    public ModelAndView categoryTag(@RequestParam(value = "message", required = false) String message,
                                    ModelAndView mv){
        if (message == null){
            message = "ok" ;
        }
        List<Category> categoryList = categoryService.findAllCategory() ;
        List<Tag> tagList = tagService.findAllTag() ;
        mv.addObject("categoryList", categoryList) ;
        mv.addObject("tagList", tagList) ;
        mv.addObject("message", message) ;
        mv.setViewName("admin/ct");
        return mv ;
    }

    @PostMapping("/deleteCategory")
    public RedirectView deleteCategory(@RequestParam("category") String categoryName,
                                        RedirectView redirectView){
        String message ;

        if (categoryName == null || categoryName.equals("")){
            message = "Field cannot be empty" ;
            redirectView.setUrl("/wp-admin/ct?message=" + message);
            return redirectView ;
        }
        // 不能删除默认  只能修改默认
        if (categoryName.equals("default")){
            message = "you cannot delete default" ;
            redirectView.setUrl("/wp-admin/ct?message=" + message);
            return redirectView ;
        }
        // 删除不成功
        if (!categoryService.deleteCategoryByName(categoryName)){
            message = "cannot find the name";
            redirectView.setUrl("/wp-admin/ct?message=" + message);
            return redirectView ;
        }
        redirectView.setUrl("/wp-admin/ct");
        return redirectView ;
    }

    @PostMapping("/addCategory")
    public RedirectView addCategory(@RequestParam("name") String name,
                                    @RequestParam("oriName") String oriName,
                                    @RequestParam("type") String type,
                                    RedirectView redirectView){
        // 这里需要做一些校验处理
        String message  ;
        if (name == null || type == null || name.equals("") || type.equals("")){
            message = "Field cannot be empty" ;
            redirectView.setUrl("/wp-admin/ct?message=" + message);
            return redirectView ;
        }
        // 创建新的分类
        if (type.equals("create")){
            Category category = categoryService.findCategoryByName(name) ;
            // 不存在分类名 则新建
            if (category == null){
                Category categoryNew = new Category();
                categoryNew.setName(name);
                categoryService.saveCategory(categoryNew);
                redirectView.setUrl("/wp-admin/ct");
            }
            // 存在分类名 则提示错误
            else {
                message =  "The category name already exists" ;
                redirectView.setUrl("/wp-admin/ct?message=" + message);
            }
        }
        // 修改分类
        else {
            // 通过原始分类名从数据库中获取对象
            Category category = categoryService.findCategoryByName(oriName) ;
            // 获取不到
            if (category == null){
                message =  "The category name not existed" ;
                redirectView.setUrl("/wp-admin/ct?message=" + message);
            }
            // 能够获取到 则修改分类名
            else {
                // 新的分类名在数据库中没有
                if (categoryService.findCategoryByName(name) == null){
                    category.setName(name);
                    categoryService.saveCategory(category);
                    redirectView.setUrl("/wp-admin/ct");
                }
                //  新的分类名在数据库中有
                else {
                    message =  "The category name already exists" ;
                    redirectView.setUrl("/wp-admin/ct?message=" + message);
                }
            }
        }
        return redirectView ;
    }
}
