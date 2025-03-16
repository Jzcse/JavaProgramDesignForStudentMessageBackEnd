package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


/**
 * User用户表实体类 保存每个允许登录的信息人员的账号信息，
 * Integer personId 用户表 user 主键 person_id
 * UserType userType 关联到用户类型对象
 * Person person 关联到该用户所用的Person对象，账户所对应的人员信息 person_id 关联 person 表主键 person_id
 * String userName 登录账号 和Person 中的num属性相同
 * String password 用户密码 非对称加密，这能加密，无法解码
 *
 */

@Entity
@Table(	name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "userName"), //这里指数据库表结构的约束，表示列名为userName的列的值必须唯一
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer personId;

    @ManyToOne() //指多个记录可以对应到多个记录上，这里是说多个用户可以关联到一个用户类型上
    @JoinColumn(name = "user_type_id") //表示这个表对另一个表的主键的引用，这里是对UserType表的主键的引用
    private UserType userType;

    @OneToOne //指只有一个记录可以对应到一个记录上，这里是说一个用户只关联到一个个人上
    @JoinColumn(name="person_id") //这里是指一个用户指向一个个人，用户主要储存账户的相关信息，而个人储存个人信息
    private Person person;

    @NotBlank //在该项目中没有DTO，这个注解的意思是不能为空
    @Size(max = 20)
    private String userName;

    @NotBlank
    @Size(max = 60)
    private String password;

    private Integer loginCount;

    @Size(max = 20)
    private String lastLoginTime;

    @Size(max = 20)
    private String  createTime;

    private Integer creatorId;

    /**
     *构造器
     */
    public User() {
    }

    public User(String username, String password) {
        this.userName = username;
        this.password = password;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }
}
