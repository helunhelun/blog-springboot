package cn.ivdone.blog.util;

import org.springframework.web.servlet.ModelAndView;

public class ErrorUtil {

    public static ModelAndView error4xx(ModelAndView mv){
        mv.setViewName("/error/4xx");
        mv.addObject("errorCode", 404) ;
        return  mv ;
    }

    public static ModelAndView error5xx(ModelAndView mv){
        mv.setViewName("/error/4xx");
        mv.addObject("errorCode", 500) ;
        return  mv ;
    }
}
