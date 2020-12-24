package cn.ivdone.blog.service;

import cn.ivdone.blog.entity.Article;
import cn.ivdone.blog.entity.ImageLibrary;
import cn.ivdone.blog.entity.User;
import cn.ivdone.blog.repository.ImageLibraryRepository;
import cn.ivdone.blog.util.FileUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Service
public class ImageLibraryService {

    @Autowired
    private ImageLibraryRepository imageLibraryRepository ;

    @Autowired
    private ArticleService articleService ;

    public ImageLibrary findImageLibraryByName(String name){
        return imageLibraryRepository.findImageLibraryByName(name) ;
    }

    public List<ImageLibrary> findAllImageLibrary(){
        return imageLibraryRepository.findAll() ;
    }

    public Page<ImageLibrary> findAllImageLibraryByPage(PageRequest pr){
        //PageRequest pr = new PageRequest();
        return imageLibraryRepository.findAllImageLibraryByPage(pr) ;
    }

    public Page<ImageLibrary> findImageLibrariesHasUserOfPage(PageRequest pr){
        return imageLibraryRepository.findImageLibrariesHasUserOfPage(pr) ;
    }

    public void saveImageLibrary(ImageLibrary imageLibrary){
        imageLibraryRepository.saveAndFlush(imageLibrary) ;
    }

    public Boolean deleteImageLibraryByName(String name){
        Boolean flag = false ;
        // 参数预处理
        if (name == null || name.equals("")){
            return false ;
        }
        for (ImageLibrary i: this.findAllImageLibrary()){
            // 在数据库中存在这个数据
            if (i.getName().equals(name)) {
                flag = true ;
                break;
            }
        }
        // 从数据库中删除
        if (flag){
            ImageLibrary image = imageLibraryRepository.findImageLibraryByName(name) ;
            // 找出磁盘地址
            String rootPath = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "static" ;
            String fileAddr = rootPath + image.getUrl() ;

            List<Article> allArticle = articleService.findAllArticle() ;
            // 把与之关联的外键设置为null  防止删除失败 这个地方主要用于解决删除父外键时 不成功 必须先解除与父外键的关系
            for (Article article : allArticle){
                if (article.getImageLibrary() != null && article.getImageLibrary().getName().equals(name)){
                    //ImageLibrary imageLibrary = new ImageLibrary() ;
                    article.setImageLibrary(null);
                    articleService.saveArticle(article);
                }
            }
            // 从数据库中删除
            imageLibraryRepository.deleteById(image.getId());
            // 从磁盘中删除
            File file = new File(fileAddr);
            // 文件存在的情况下去删除
            if (file.exists()){
                // 删除不成功
                if (!file.delete()){
                    return  false ;
                }
            }
            return true ;
        }
        return false ;
    }
    /**
     *
     * @param file springboot 文件对象
     * @param nickname 该文件数据库的别名
     * @return 是否写入磁盘并存入数据库
     */
    public Boolean upload(@RequestParam("editormd-image-file") MultipartFile file, String nickname, User user){
        // 上传的文件不为空
        if (!file.isEmpty()){
            // 必须有该文件的属主
            if (nickname.equals("")){
                return false ;
            }
            // 用户获取时间
            Calendar cal = Calendar.getInstance() ;
            // uuid 随机
            String oriFilename = file.getOriginalFilename();
            //String nameSuffix = oriFilename.substring(oriFilename.lastIndexOf(".")) ;
            String filename = UUID.randomUUID().toString() + '-' + oriFilename ;
            // 要保存的文件目录
            String rootPath = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "media/" ;
            String url = "file/" + String.valueOf(cal.get(Calendar.YEAR)) + "/" + String.valueOf(cal.get(Calendar.MONTH)+1) +
                    "/" + String.valueOf(cal.get(Calendar.DATE)) + "/" ;
            String savaPath = rootPath + url ;
            // 写入磁盘
            try {
                FileUtil.fileUpload(file.getBytes(), savaPath, filename);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            // 设置数据库
            ImageLibrary imageLibrary = new ImageLibrary() ;
            imageLibrary.setName(nickname) ;
            imageLibrary.setUrl("/media/" + url + filename) ;
            // 判断user是否为空 为null 即为普通上传
            if (user != null){
                imageLibrary.setUser(user);
            }
            // 保存在数据库
            imageLibraryRepository.saveAndFlush(imageLibrary) ;
            return true;
        }
        return  false ;
    }

    /**
     *
     * @param file springboot 文件对象
     * @param nickname 该文件数据库的别名
     * @return json 格式信息(包含正确与错误信息)
     */
    public String uploadFileApi(@RequestParam("editormd-image-file")MultipartFile file, String nickname, User user){
        // json 数据返回
        JSONObject jsonResult = new JSONObject() ;
        // 没有提交nickname字段处理
        if (nickname == null){
            jsonResult.put("success", 0);
            jsonResult.put("message", "无法找到命名") ;
            return jsonResult.toJSONString();
        }
        // 命名重复处理
        for (ImageLibrary i: this.findAllImageLibrary()){
            if (i.getName().equals(nickname)){
                jsonResult.put("success", 0);
                jsonResult.put("message", "该命名已在图像库中存在") ;
                return jsonResult.toJSONString() ;
            }
        }
        // 保存到磁盘 并存入数据库
        Boolean result = this.upload(file, nickname, user) ;
        if (result){
            ImageLibrary imageLibrary = this.findImageLibraryByName(nickname) ;
            jsonResult.put("success", 1);
            jsonResult.put("message", "ok") ;
            jsonResult.put("url", imageLibrary.getUrl()) ;
        }
        else {
            jsonResult.put("success", 0);
            jsonResult.put("message", "error") ;
        }
        // 结果返回
        return jsonResult.toJSONString();
    }
}
