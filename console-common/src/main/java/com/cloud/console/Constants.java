package com.cloud.console;

/**
 * Created by Frank on 2017/8/16.
 */
public class Constants {
    public static final String PRIVATE_KEY = "private_key";

    public static final String PUBLIC_KEY = "public_key";

    /**
     * 密码加密方式
     */
    public static String BCRYPT = "{bcrypt}";

    public static String JWT_USER_ID_KEY = "id";
    public static String JWT_CLIENT_ID_KEY = "client_id";

    /**
     * JWT载体key
     */
    public static String JWT_PAYLOAD_KEY = "payload";

    /**
     * 黑名单token前缀
     */
    public static String TOKEN_BLACKLIST_PREFIX = "auth:token:blacklist:";

    public static String CLIENT_DETAILS_FIELDS = "client_id, CONCAT('{noop}',client_secret) as client_secret, " +
            "resource_ids, scope, "
            + "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, "
            + "refresh_token_validity, additional_information, autoapprove";

    public static String BASE_CLIENT_DETAILS_SQL = "select " + CLIENT_DETAILS_FIELDS + " from oauth_client_details";

    public static String FIND_CLIENT_DETAILS_SQL = BASE_CLIENT_DETAILS_SQL + " order by client_id";

    public static String SELECT_CLIENT_DETAILS_SQL = BASE_CLIENT_DETAILS_SQL + " where client_id = ?";
}
