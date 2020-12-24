/**
 * @author : helun
 * @desc : 定义文章相关的字段
 */
package cn.ivdone.blog.entity;


import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Data
@Entity
@Table(name = "blog_Article")
public class Article {
    @Id
    @GeneratedValue
    private  Long id ;

    @Column(name="post_id", length = 100000, nullable = false)
    private Long post_id ;

    @Column(name = "views", length = 1000000, nullable = false)
    private Long views ;

    @Column(name = "likeview", length = 100000, columnDefinition = "INT default 0")
    private Long like ;

    // 文章标题
    @Column(name = "title", length = 255, nullable = false)
    private String title ;

    // 摘要
    @Column(name = "summary", length = 1024, nullable = false)
    private String summary ;

    // 文章内容
    @Column(name = "body", length = 100000, nullable = false)
    private String body ;

    // 文章对应的图片
    @ManyToOne
    //@Cascade({org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private ImageLibrary imageLibrary ;

    // 文章公开 -> 'p'  文章草稿 -> 'd'
    @Column(name = "status", columnDefinition="varchar(255) default 'p'")
    private String status ;

    // 创建时间
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date create_time ;

    // 修改时间
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @Column(name = "modify_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modify_time ;

    // 对应的分类
    @ManyToOne
    private  Category category ;

    // 作者
    @ManyToOne
    private  User user ;

    // 标签
    @ManyToOne
    private Tag tag ;

    // 空构造函数
    public Article(){

    }
    // 带参数的构造函数
    // 用于创造一个文章
    public Article(Long post_id, Long views,
                   String title, String summary, String body, String status,
                   Category category, User user, Tag tag){
        this.post_id = post_id ;
        this.views = views ;
        this.title = title ;
        this.summary = summary ;
        this.body = body ;
        this.status = status ;
        this.category = category ;
        this.user = user ;
        this.tag = tag ;
    }
    // 对数据进行修改
    public void modifyAll(Long post_id, Long views,
                          String title, String summary, String body, String status,
                          Category category, User user, Tag tag){
        this.post_id = post_id ;
        this.views = views ;
        this.title = title ;
        this.summary = summary ;
        this.body = body ;
        this.status = status ;
        this.category = category ;
        this.user = user ;
        this.tag = tag ;
    }

    //  格式话输出时期
    public String getDate(){
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
        return ft.format(this.create_time) ;
    }

    public String getDay(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.create_time);
        return String.valueOf(cal.get(Calendar.DATE)) ;
    }

    // 浏览量加1
    public void viewed(){
        this.views += 1 ;
    }

}
