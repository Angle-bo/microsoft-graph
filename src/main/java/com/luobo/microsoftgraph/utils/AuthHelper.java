package com.luobo.microsoftgraph.utils;

import com.microsoft.graph.auth.confidentialClient.AuthorizationCodeProvider;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * oauth2认证请求逻辑
 */
@Component
public class AuthHelper {

    private  final String authority = "https://login.microsoftonline.com";
    private  final String authorizeUrl = authority + "/common/oauth2/v2.0/authorize";

    @Value("${microsof-tgraph.appId}")
    private  String appId;
    @Value("${microsof-tgraph.appPassword}")
    private  String appPassword;
    @Value("${microsof-tgraph.redirectUrl}")
    private  String redirectUrl;

    private List<String> scopes = Arrays.asList("user.read", "openid", "profile", "offline_access", "Mail.Read", "Calendars.Read","Calendars.ReadWrite");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private  String getAppId() {

        return appId;
    }
    private  String getAppPassword() {
        return appPassword;
    }

    private  String getRedirectUrl() {
        return redirectUrl;
    }

    private  String getScopes() {
        StringBuilder sb = new StringBuilder();
        for (String scope: scopes) {
            sb.append(scope + " ");
        }
        return sb.toString().trim();
    }


    /**
     * 获取登录url
     * https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-auth-code-flow
     * @param state
     * @param nonce
     * @return
     */
    public  String getLoginUrl(UUID state, UUID nonce) {

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(authorizeUrl);
        urlBuilder.queryParam("client_id", getAppId());
        urlBuilder.queryParam("redirect_uri", getRedirectUrl());
        urlBuilder.queryParam("response_type", "code id_token");
        urlBuilder.queryParam("scope", getScopes());
        urlBuilder.queryParam("state", state);
        urlBuilder.queryParam("nonce", nonce);
        urlBuilder.queryParam("response_mode", "form_post");

        return urlBuilder.toUriString();
    }


    /**
     * 获取IGraphServiceClient （调用Graph Api 的客户端）
     * @return
     */
    public IGraphServiceClient getAuthorizationCodeProvider(String code){
        AuthorizationCodeProvider authorizationCodeProvider = new AuthorizationCodeProvider(getAppId(), scopes , code, getRedirectUrl(), getAppPassword());
        IGraphServiceClient graphClient = GraphServiceClient
                .builder()
                .authenticationProvider(authorizationCodeProvider)
                .buildClient();
        return graphClient;
    }

}