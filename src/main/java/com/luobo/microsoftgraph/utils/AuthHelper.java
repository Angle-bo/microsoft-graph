package com.luobo.microsoftgraph.utils;

import com.luobo.microsoftgraph.entity.TokenResponse;
import com.luobo.microsoftgraph.service.TokenService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

/**
 * oauth2认证请求逻辑
 */
@Component
public class AuthHelper {

    private  final String authority = "https://login.microsoftonline.com";
    private  final String authorizeUrl = authority + "/common/oauth2/v2.0/authorize";

    private  String[] scopes = {
            "openid",
            "offline_access",
            "profile",
            "User.Read",
            "Mail.Read",
            "Calendars.Read",
            "Calendars.ReadWrite"
    };

    @Value("${microsof-tgraph.appId}")
    private  String appId;
    @Value("${microsof-tgraph.appPassword}")
    private  String appPassword;
    @Value("${microsof-tgraph.redirectUrl}")
    private  String redirectUrl;

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
     * 进行令牌请求
     * @param authCode
     * @param tenantId
     * @return
     */
    public TokenResponse getTokenFromAuthCode(String authCode, String tenantId) {
        // Create a logging interceptor to log request and responses
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor).build();

        // Create and configure the Retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(authority)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        // Generate the token service
        TokenService tokenService = retrofit.create(TokenService.class);

        try {
            return tokenService.getAccessTokenFromAuthCode(tenantId, getAppId(), getAppPassword(),
                    "authorization_code", authCode, getRedirectUrl()).execute().body();
        } catch (IOException e) {
            TokenResponse error = new TokenResponse();
            error.setError("IOException");
            error.setErrorDescription(e.getMessage());
            return error;
        }
    }

    /**
     * 刷新访问令牌
     * @param tokens
     * @param tenantId
     * @return
     */
    public  TokenResponse ensureTokens(TokenResponse tokens, String tenantId) {
        // Are tokens still valid?
        Calendar now = Calendar.getInstance();
        if (now.getTime().before(tokens.getExpirationTime())) {
            // Still valid, return them as-is
            return tokens;
        }
        else {
            // Expired, refresh the tokens
            // Create a logging interceptor to log request and responses
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor).build();

            // Create and configure the Retrofit object
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(authority)
                    .client(client)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();

            // Generate the token service
            TokenService tokenService = retrofit.create(TokenService.class);

            try {
                return tokenService.getAccessTokenFromRefreshToken(tenantId, getAppId(), getAppPassword(),
                        "refresh_token", tokens.getRefreshToken(), getRedirectUrl()).execute().body();
            } catch (IOException e) {
                TokenResponse error = new TokenResponse();
                error.setError("IOException");
                error.setErrorDescription(e.getMessage());
                return error;
            }
        }
    }

}