package com.cloud.console.controller;

import com.cloud.console.Constants;
import com.cloud.console.common.Result;
import com.cloud.console.vo.Oauth2Token;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "认证中心")
@RestController
@RequestMapping("/oauth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private TokenEndpoint tokenEndpoint;

    @ApiOperation("OAuth2认证生成token")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "grant_type", defaultValue = "password", value = "授权模式", dataTypeClass =
                    String.class, required = true),
            @ApiImplicitParam(name = "client_id", defaultValue = "client", value = "Oauth2客户端ID", dataTypeClass =
                    String.class, required = true),
            @ApiImplicitParam(name = "client_secret", defaultValue = "123456", value = "Oauth2客户端秘钥", dataTypeClass =
                    String.class, required = true),
            @ApiImplicitParam(name = "refresh_token", dataTypeClass = String.class, value = "刷新token"),
            @ApiImplicitParam(name = "username", defaultValue = "admin", dataTypeClass = String.class, value = "登录用户名"),
            @ApiImplicitParam(name = "password", defaultValue = "123456", dataTypeClass = String.class, value = "登录密码"),

            // 微信小程序认证参数（无小程序可忽略）
            /*@ApiImplicitParam(name = "code", value = "小程序code"),
            @ApiImplicitParam(name = "encryptedData", value = "包括敏感数据在内的完整用户信息的加密数据"),
            @ApiImplicitParam(name = "iv", value = "加密算法的初始向量"),*/
    })
    @PostMapping("/token")
    public Result postAccessToken(
            @ApiIgnore Principal principal,
            @ApiIgnore @RequestParam Map<String, String> parameters, HttpServletResponse response
    ) throws Exception {
        String clientId = parameters.get(Constants.JWT_CLIENT_ID_KEY);
        switch (clientId) {
            /*case AuthConstants.WEAPP_CLIENT_ID:  // 微信认证
                return this.handleForWxAuth(principal, parameters);*/
            default:
                OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
                Oauth2Token oauth2Token = Oauth2Token.builder()
                        .accessToken(oAuth2AccessToken.getValue())
                        .refreshToken(oAuth2AccessToken.getRefreshToken().getValue())
                        .expiresIn(oAuth2AccessToken.getExpiresIn())
                        .build();
                /*Cookie cookie = new Cookie(Constants.ACCESS_TOKEN_KEY, oauth2Token.getAccessToken());
                cookie.setMaxAge(oAuth2AccessToken.getExpiresIn());
                cookie.setPath("/");
                response.addCookie(cookie);
                cookie = new Cookie(Constants.REFRESH_TOKEN_KEY, oauth2Token.getRefreshToken());
                cookie.setMaxAge(oAuth2AccessToken.getExpiresIn() * 10);
                cookie.setPath("/");
                response.addCookie(cookie);*/
                return Result.success(oauth2Token);
        }
    }

    @GetMapping("/callback")
    public Map<String, String> callback(HttpServletRequest request) {
        String accessToken = request.getParameter("access_token");
        String code = StringUtils.isNoneBlank(accessToken) ? accessToken : request.getParameter("code");
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        return map;
    }

    /*private WxMaService wxService;
    private MemberFeignService memberFeignService;
    private PasswordEncoder passwordEncoder;

    public Result handleForWxAuth(Principal principal, Map<String, String> parameters) throws
    HttpRequestMethodNotSupportedException {

        String code = parameters.get("code");

        if (StrUtil.isBlank(code)) {
            throw new BizException("code不能为空");
        }

        WxMaJscode2SessionResult session = null;
        try {
            session = wxService.getUserService().getSessionInfo(code);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        String openid = session.getOpenid();
        String sessionKey = session.getSessionKey();

        Result<AuthMemberDTO> result = memberFeignService.getUserByOpenid(openid);

        if (ResultCode.USER_NOT_EXIST.getCode().equals(result.getCode())) { // 微信授权登录 会员信息不存在时 注册会员
            String encryptedData = parameters.get("encryptedData");
            String iv = parameters.get("iv");

            WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
            if (userInfo == null) {
                throw new BizException("获取用户信息失败");
            }
            UmsUser user = new UmsUser()
                    .setNickname(userInfo.getNickName())
                    .setAvatar(userInfo.getAvatarUrl())
                    .setGender(Integer.valueOf(userInfo.getGender()))
                    .setOpenid(openid)
                    .setUsername(openid)
                    .setPassword(passwordEncoder.encode(openid).replace(AuthConstants.BCRYPT, Strings.EMPTY)) //
                    加密密码移除前缀加密方式 {bcrypt}
                    .setStatus(GlobalConstants.STATUS_NORMAL_VALUE);

            Result res = memberFeignService.add(user);
            if (!ResultCode.SUCCESS.getCode().equals(res.getCode())) {
                throw new BizException("注册会员失败");
            }
        }

        // oauth2认证参数对应授权登录时注册会员的username、password信息，模拟通过oauth2的密码模式认证
        parameters.put("username", openid);
        parameters.put("password", openid);

        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        Oauth2Token oauth2Token = Oauth2Token.builder()
                .token(oAuth2AccessToken.getValue())
                .refreshToken(oAuth2AccessToken.getRefreshToken().getValue())
                .expiresIn(oAuth2AccessToken.getExpiresIn())
                .build();
        return Result.success(oauth2Token);
    }*/
}
