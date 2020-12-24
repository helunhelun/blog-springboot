package cn.ivdone.blog.controller;

import cn.ivdone.blog.entity.Tag;
import cn.ivdone.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/wp-admin")
public class TagController {

    @Autowired
    private TagService tagService ;

    /**
     * 删除标签
     * @param tagName 标签名
     * @param redirectView  重定向
     * @return 重定向对象
     */
    @PostMapping("/deleteTag")
    public RedirectView deleteTag(@RequestParam("tag") String tagName,
                                       RedirectView redirectView){
        String message ;

        if (tagName == null || tagName.equals("")){
            message = "Field cannot be empty" ;
            redirectView.setUrl("/wp-admin/ct?message=" + message);
            return redirectView ;
        }
        // 不能删除默认  只能修改默认
        if (tagName.equals("default")){
            message = "you cannot delete default" ;
            redirectView.setUrl("/wp-admin/ct?message=" + message);
            return redirectView ;
        }
        // 删除不成功
        if (!tagService.deleteTagByName(tagName)){
            message = "cannot find the name";
            redirectView.setUrl("/wp-admin/ct?message=" + message);
            return redirectView ;
        }
        redirectView.setUrl("/wp-admin/ct");
        return redirectView ;
    }

    /**
     *  新增或者修改标签
     * @param name  标签名
     * @param oriName 原始标签名
     * @param type 类型 分为 create 或者 modify
     * @param redirectView 重定向
     * @return  重定向对象
     */
    @PostMapping("/addTag")
    public RedirectView addTag(@RequestParam("name") String name,
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
            Tag tag = tagService.findTagByName(name) ;
            // 不存在分类名 则新建
            if (tag == null){
                Tag tagNew = new Tag();
                tagNew.setName(name);
                tagService.saveTag(tagNew);
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
            Tag tag = tagService.findTagByName(oriName) ;
            // 获取不到
            if (tag == null){
                message =  "The category name not existed" ;
                redirectView.setUrl("/wp-admin/ct?message=" + message);
            }
            // 能够获取到 则修改分类名
            else {
                // 新的分类名在数据库中没有
                if (tagService.findTagByName(name) == null){
                    tag.setName(name);
                    tagService.saveTag(tag);
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
