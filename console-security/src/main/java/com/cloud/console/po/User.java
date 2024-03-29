package com.cloud.console.po;

import com.cloud.console.Constants;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Frank on 2017/8/3.
 */
@Data
public class User implements Serializable {
    private Long id;
    private String client_id;
    private String username;
    private String password;
    private String name;
    private String email;
    private Timestamp create_time;
    private String avatar;
    private Integer status;
    private String mfa_secret;

    public User() {
    }

    public User(User user) {
        this.id = user.getId();
        this.client_id = user.getClient_id();
        this.username = user.getUsername();
        this.password = Constants.BCRYPT + user.getPassword();
        this.name = user.getName();
        this.email = user.getEmail();
        this.create_time = user.getCreate_time();
        this.status = user.getStatus();
        this.mfa_secret = user.getMfa_secret();
    }
}
