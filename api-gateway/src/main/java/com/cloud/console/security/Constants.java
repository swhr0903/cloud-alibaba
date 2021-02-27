package com.cloud.console.security;

/**
 * Created by Frank on 2017/8/16.
 */
public class Constants {
    /**
     * 认证信息Http请求头
     */
    public static String JWT_TOKEN_HEADER = "Authorization";

    /**
     * JWT令牌前缀
     */
    public static String JWT_TOKEN_PREFIX = "Bearer ";

    /**
     * JWT载体key
     */
    public static String JWT_PAYLOAD_KEY = "payload";

    /**
     * Redis缓存权限规则key
     */
    public static String PERMISSION_ROLES_KEY = "auth:permission:roles";

    /**
     * 黑名单token前缀
     */
    public static String TOKEN_BLACKLIST_PREFIX = "auth:token:blacklist:";

    public static String CLIENT_DETAILS_FIELDS = "client_id, CONCAT('{noop}',client_secret) as client_secret, " +
            "resource_ids, scope, "
            + "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, "
            + "refresh_token_validity, additional_information, autoapprove";

    public static String JWT_JTI_KEY = "client_id";

    /**
     * JWT存储权限前缀
     */
    public static String AUTHORITY_PREFIX = "ROLE_";

    /**
     * JWT存储权限属性
     */
    public static String JWT_AUTHORITIES_KEY = "authorities";

    /**
     * 后台管理接口路径匹配
     */
    public static String ADMIN_URL_PATTERN = "*_/youlai-admin/**";
}
