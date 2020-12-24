package cn.ivdone.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name="blog_User")
public class User implements Serializable {
    @Id
    @GeneratedValue
    private Long id ;

    // 用户名
    @NotEmpty(message = "用户名不能为空")
    @Column(name = "username", nullable = false)
    private String username ;
    // 密码
    @NotEmpty(message = "密码不能为空")
    @Size(min = 5, max = 20, message = "密码长度最少为5个字符， 最大为20个字符")
    @Column(name = "password", nullable = false)
    private  String password ;
    // 昵称
    @NotEmpty(message = "昵称不能为空")
    @Column(name = "nickname", nullable = true)
    private String nickname ;

    @NotEmpty(message = "firstname不能为空")
    @Column(name = "firstname", nullable = true)
    private String firstname ;

    @NotEmpty(message = "lastname不能为空")
    @Column(name = "lastname", nullable = true)
    private String lastname ;
    // 创建时间
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date create_time ;

    // email
    @NotEmpty(message = "邮箱不能为空")
    @Email(message = "邮件格式不符合要求")
    @Column(name = "email", length = 255)
    private String email ;


    // 是否为员工
    private Boolean is_stuff ;

    // 是否激活
    private  Boolean is_active ;

}
