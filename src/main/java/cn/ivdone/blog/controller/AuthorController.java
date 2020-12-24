package cn.ivdone.blog.controller;

import cn.ivdone.blog.entity.User;
import cn.ivdone.blog.service.UserService;
import cn.ivdone.blog.util.Util;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
public class AuthorController {

    @Autowired
    private UserService userService ;

    /**
     * 创建管理员账户
     * @param mv 渲染对象
     * @return 渲染对象
     */
    @GetMapping("/launchpad")
    public ModelAndView launchpad(ModelAndView mv){
        mv.setViewName("admin/user");
        return mv ;
    }

    /**
     *  新增管理员用户post请求
     * @param user 用户提交的user对象
     * @param bindingResult 进行表单校验的存储结果
     * @return { "success" : 0/1 , "message" : "xxx"} -> json格式
     */
    @PostMapping(value = "/add/user", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addUser( @Valid User user, BindingResult bindingResult){
        JSONObject resultJson = new JSONObject();
        // 表单校验
        if (bindingResult.hasFieldErrors()){

            String error =  bindingResult.getFieldError().getDefaultMessage().toString() ;
            resultJson.put("success", 0) ;
            resultJson.put("message", error) ;
            return resultJson.toJSONString() ;
        }

        List<User> userList = userService.findAllUser() ;
        if (userList.size() > 0){

            resultJson.put("success", 0) ;
            resultJson.put("message", "已存在管理员账户，请登录") ;
            return resultJson.toJSONString() ;
        }
        if (Util.isCNChar(user.getUsername())){
            resultJson.put("success", 0) ;
            resultJson.put("message", "用户名不能含有中文") ;
            return resultJson.toJSONString() ;
        }
        // 用户密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder() ;
        String encodedPassword = passwordEncoder.encode(user.getPassword().trim()) ;
        user.setPassword(encodedPassword);
        // 设置日期
        user.setCreate_time(new Date());
        // 激活
        user.setIs_stuff(true);
        user.setIs_active(true);
        // 写入数据库
        userService.saveUser(user);

        resultJson.put("success", 1) ;
        resultJson.put("message", "创建成功，请登录后台写文章吧") ;
        return resultJson.toJSONString() ;
    }
}
