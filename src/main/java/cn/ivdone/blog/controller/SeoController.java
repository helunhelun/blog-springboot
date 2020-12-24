package cn.ivdone.blog.controller;

import cn.ivdone.blog.service.SeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.ParseException;

// 用于网站地图
@Controller
public class SeoController {

    @Autowired
    private SeoService seoService ;

    @GetMapping(value = "/mysitemap.xml", produces = "application/xml;charset=UTF-8")
    @ResponseBody
    public String siteMapXml() throws IOException, ParseException {
        // 直接返回xml格式的文件
        return  seoService.createSiteMapXmlContent();
    }
}
