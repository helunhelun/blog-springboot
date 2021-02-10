package cn.ivdone.blog.controller;

import cn.ivdone.blog.entity.User;
import cn.ivdone.blog.service.UserService;
import cn.ivdone.blog.util.ErrorUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

// 用户管理控制
@Controller
@RequestMapping("/wp-admin/userAdmin")
public class UserAdminController {

    @Autowired
    private UserService userService ;

    private JSONObject resultJson = new JSONObject() ;

    // 用户服务主页面
    @GetMapping("")
    public ModelAndView userAdmin(ModelAndView mv, Authentication authentication){
        // get username
        String username = authentication.getName();

        // 从数据库中找到该用户
        User user = userService.findUserByUsername(username);
        if (user == null){
            return ErrorUtil.error4xx(mv);
        }
        // 找出所有用户
        List<User> userList = userService.findAllUser();

        mv.addObject("userList", userList) ;

        mv.setViewName("admin/useradmin");
        return mv ;
    }

    // 获取当前请求的用户
    @GetMapping(value = "/get/username", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getCurUsername(Authentication authentication){
        String username = authentication.getName() ;
        //System.out.println(username);
        // 从数据库中查找用户
        User user = userService.findUserByUsername(username) ;
        if (user == null || !user.getIs_stuff()){
            resultJson.put("IsAdmin", 0) ;
            return resultJson.toJSONString() ;
        }
        resultJson.put("IsAdmin", 1) ;
        return resultJson.toJSONString();
    }

    // 用户信息修改 返回json格式的信息
    @PostMapping(value = "/modify", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public  String userModify(@Valid User user, BindingResult bindingResult){
        resultJson.put("success", 0) ;
        // 表单校验
        if (bindingResult.hasFieldErrors()){

            String error =  bindingResult.getFieldError().getDefaultMessage().toString() ;
            resultJson.put("success", 0) ;
            resultJson.put("message", error) ;
            return resultJson.toJSONString() ;
        }
        // 数据库验证
        User userObj = userService.findUserByUsername(user.getUsername()) ;
        if (userObj == null){
            resultJson.put("message", "用户有误, 请重新输入") ;
            return resultJson.toJSONString() ;
        }
        // 修改处理
        userObj.setNickname(user.getNickname());
        userObj.setFirstname(user.getFirstname());
        userObj.setLastname(user.getLastname());
        userObj.setEmail(user.getEmail());
        resultJson.put("success", 1) ;
        resultJson.put("message", "ok") ;
        // 保存到数据库
        userService.saveUser(userObj);
        return resultJson.toJSONString();
    }

    // 新增用户
    @PostMapping(value = "/add", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String userAdd(@Valid User user, BindingResult bindingResult){
        resultJson.put("success", 0) ;
        // 表单校验
        if (bindingResult.hasFieldErrors()){

            String error =  bindingResult.getFieldError().getDefaultMessage().toString() ;
            resultJson.put("success", 0) ;
            resultJson.put("message", error) ;
            return resultJson.toJSONString() ;
        }
        // 不能够有重名的用户
        for (User userObj : userService.findAllUser()){
            if (userObj.getUsername().equals(user.getUsername())){
                resultJson.put("message", "该用户已存在") ;
                return resultJson.toJSONString();
            }
        }

        // 用户密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder() ;
        String encodedPassword = passwordEncoder.encode(user.getPassword().trim()) ;
        // 设置密码
        user.setPassword(encodedPassword);
        //  设置日期
        user.setCreate_time(new Date());
        // 设置状态
        user.setIs_active(true);
        user.setIs_stuff(false);
        // 保存到数据库
        userService.saveUser(user);
        resultJson.put("success", 1) ;
        resultJson.put("message", "ok") ;
        return  resultJson.toJSONString() ;
    }

    // 修改密码
    @PostMapping(value = "/chg-password", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String chgPassword(@RequestParam(value = "ori-password") String oriPassword,
                              @RequestParam(value = "new-password") String newPassword,
                              @RequestParam(value = "username") String username,
                              Authentication authentication
                              ){
        resultJson.put("success", 0) ;
        // 新密码不能小于5位数
        if (newPassword.length() < 5){
            resultJson.put("message", "新密码不能少于5位数") ;
            return resultJson.toJSONString();
        }
        // 通过用户名获取用户
        User user = userService.findUserByUsername(username) ;
        if (user == null){
            resultJson.put("message", "找不到用户") ;
            return resultJson.toJSONString();
        }
        BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
        //User curUsername = userService.findUserByUsername(authentication.getName()) ;
        // 如果该用户为管理员
        if (user.getIs_stuff()){
            // 匹配不成功
            if(!bcryptPasswordEncoder.matches(oriPassword, user.getPassword())){
                resultJson.put("message", "原始密码不正确") ;
                return resultJson.toJSONString();
            }
        }
        // 修改密码
        String encodedPassword = bcryptPasswordEncoder.encode(newPassword.trim()) ;
        user.setPassword(encodedPassword);
        // 保存到数据库
        userService.saveUser(user);
        // 往客户端返回信息
        String s = "修改密码成功，新密码为：" + newPassword ;
        resultJson.put("success", 1) ;
        resultJson.put("message", s) ;
        return resultJson.toJSONString();

    }

    // 删除用户
    @PostMapping(value = "delete", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deleteUser(@RequestParam(value = "username") String username , Authentication authentication){
        resultJson.put("success", 0) ;
        // 校验
        if (username == null || username.equals("")){
            resultJson.put("message", "字段有误") ;
            return  resultJson.toJSONString() ;
        }
        // 获取当前的用户
        String curUsername = authentication.getName() ;
        // 判断是否删除本用户
        if (username.equals(curUsername)){
            resultJson.put("message", "无法删除自己") ;
            return resultJson.toJSONString() ;
        }
        // 从数据库中查找该用户的信息
        User user = userService.findUserByUsername(username) ;
        if (user == null){
            resultJson.put("message", "该用户不存在") ;
            return resultJson.toJSONString();
        }
        // 无法删除管理员
        if (user.getIs_stuff()){
            resultJson.put("message", "无法删除管理员用户") ;
            return resultJson.toJSONString();
        }
        // 删除用户
        userService.deleteUser(user);
        resultJson.put("success", 1) ;
        resultJson.put("message", "ok") ;
        return resultJson.toJSONString() ;
    }
}
