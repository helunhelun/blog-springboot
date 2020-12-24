package cn.ivdone.blog.controller;


import cn.ivdone.blog.entity.User;
import cn.ivdone.blog.service.ImageLibraryService;
import cn.ivdone.blog.entity.ImageLibrary ;
import cn.ivdone.blog.service.UserService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping(value = "/wp-admin")
public class ImageLibraryController {

    // 图像库处理服务
    @Autowired
    private ImageLibraryService imageLibraryService ;

    @Autowired
    private UserService userService ;

    /**
     * 用于向用户展现图像图的界面 具有分页功能
     * @param mv 模板渲染便利
     * @param request http请求参数对象
     * @return 模板变量
     */
    @GetMapping(value = "/ImageLibrary")
    public ModelAndView imageLibrary(ModelAndView mv, HttpServletRequest request){
        String pageStr = request.getParameter("page") ;
        // 初始化页面为0
        Integer page = new Integer(0) ;
        if (pageStr != null){
            page = Integer.valueOf(pageStr) ;
        }
        // 排序 与 分页
        Sort sort = Sort.by(Sort.Direction.DESC, "create_time") ;
        PageRequest pr = PageRequest.of(page, 4, sort) ;
        //List<ImageLibrary> imageLibraries = imageLibraryService.findAllImageLibrary() ;
        //Page<ImageLibrary> imageLibraryPage = imageLibraryService.findAllImageLibraryByPage(pr) ;
        Page<ImageLibrary> imageLibraryPage = imageLibraryService.findImageLibrariesHasUserOfPage(pr) ;
        //System.out.println(imageLibraryPage.getTotalPages());
        //List<ImageLibrary> imageLibraryList = imageLibraryPage.getContent() ;

        //mv.addObject("imageList", imageLibraryList) ;
        mv.addObject("imagePage", imageLibraryPage) ;

        mv.setViewName("admin/image");
        //imageLibraries.
        return mv ;
    }

    /**
     * 新增图像库处理
     * @param name 图像的数据库的名字
     * @param file springboot 文件对象
     * @return { "success" : 0/1 , "message" : "xxx", "url" : "/xxx/xxx" } -> json格式
     */
    @PostMapping(value = "/addImageLibrary", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addImage(@RequestParam("name") String name, @RequestParam("image") MultipartFile file, Authentication authentication){
        // 获取用户名 通过session机制 shiro
        String username = authentication.getName() ;
        User user ;
        try{
            user = userService.findUserByUsername(username) ;
        }
        catch (Exception e){
            JSONObject jsonResult = new JSONObject();
            jsonResult.put("success", 0);
            jsonResult.put("message", "无法找到用户名") ;
            return jsonResult.toJSONString() ;
        }
        return imageLibraryService.uploadFileApi(file, name, user) ;
    }

    /**
     * 通过图像名删除图像
     * @param name 删除图像库中的图像名称
     * @return { "success" : 0/1 , "message" : "xxx", "url" : "/xxx/xxx" } -> json格式
     */
    @PostMapping(value = "/ImageLibraryDelete", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deleteImage(@RequestParam("delete_name") String name, Authentication authentication){
        //System.out.println(name);
        JSONObject jsonResult = new JSONObject() ;
        // 判断用户是否有删除权限
        String username = authentication.getName() ;
        ImageLibrary imageLibrary = imageLibraryService.findImageLibraryByName(name) ;
        // 该文件没有权限属主 或者权限不是本用户的
        if (imageLibrary.getUser() == null || !imageLibrary.getUser().getUsername().equals(username)){
            jsonResult.put("success", 0);
            jsonResult.put("message", "无法获得文件权限") ;
            return jsonResult.toJSONString() ;
        }

        Boolean result = imageLibraryService.deleteImageLibraryByName(name) ;
        if (result){
            jsonResult.put("success", 1) ;
            jsonResult.put("message", "成功删除") ;
        }
        else {
            jsonResult.put("success", 0) ;
            jsonResult.put("message", "无法找到该数据或者提交字段为空") ;
        }
        return jsonResult.toJSONString() ;
    }

    /**
     * 处理普通文件上传 用于markdown编辑器文件上传
     * @param file springboot 文件对象
     * @return  { "success" : 0/1 , "message" : "xxx", "url" : "/xxx/xxx" } -> json格式
     */
    @PostMapping(value = "/file/upload", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public  String uploadHandle(@RequestParam("editormd-image-file") MultipartFile file){
        // 普通上传
        String nickname = UUID.randomUUID().toString();
        return imageLibraryService.uploadFileApi(file, nickname, null) ;
    }
}
