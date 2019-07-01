package com.luobo.microsoftgraph.service;

import com.luobo.microsoftgraph.entity.Event;
import com.luobo.microsoftgraph.entity.Message;
import com.luobo.microsoftgraph.entity.OutlookUser;
import com.luobo.microsoftgraph.entity.PagedResult;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * 业务示例接口
 * https://docs.microsoft.com/zh-cn/graph/api/overview?view=graph-rest-1.0
 * https://developer.microsoft.com/zh-cn/graph/graph-explorer
 */
public interface OutlookService {

    /**
     * 获取用户信息
     * @return
     */
    @GET("/v1.0/me")
    Call<OutlookUser> getCurrentUser();

    /**
     * 获取邮件列表
     * @param folderId
     * @param orderBy
     * @param select
     * @param maxResults
     * @return
     */
    @GET("/v1.0/me/mailfolders/{folderid}/messages")
    Call<PagedResult<Message>> getMessages(
            @Path("folderid") String folderId,
            @Query("$orderby") String orderBy,
            @Query("$select") String select,
            @Query("$top") Integer maxResults
    );

    /**
     * 日历-创建事件（安排会议）
     * @param body
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})//添加header表明参数是json格式的
    @POST("/v1.0/me/events")
    Call<Event> calendarsReadWrite(@Body RequestBody body);

}