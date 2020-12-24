package cn.ivdone.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "blog_imagelibrary")
public class ImageLibrary {

    @Id
    @GeneratedValue
    private  Long id ;

    // 命名
    @Column(name = "name")
    private String name ;

    // 文件的地址
    @Column(name = "url")
    private String url ;

    // 创建时间
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date create_time ;

    // 文件的属主
    @ManyToOne
    private User user ;

    // 关联的文章
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Article> articles ;
}
