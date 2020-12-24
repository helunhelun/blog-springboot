package cn.ivdone.blog.entity;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;

@Data
@Entity
@Table(name="blog_Tag")
public class Tag {
    @Id
    @GeneratedValue
    private Long id ;

    @Column(name = "name", length = 255)
    private String name ;

}
