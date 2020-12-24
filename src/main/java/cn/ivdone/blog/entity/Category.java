package cn.ivdone.blog.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="blog_Category")
public class Category {

    @Id
    @GeneratedValue
    private Long id ;

    @Column(name = "name", length = 256)
    private String name ;


}
