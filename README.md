# microsoft-graph

**msgraph-sdk-java 1.5.0-SNAPSHOT使用**

####其它分支
使用okhttp3与retrofit2请切换到master分支

####相关框架
    | spring-boot  						| 2.1.6.RELEASE   |
    | spring-boot-starter-freemarker 						|
    | fastjson 							| 1.2.46  		|
    | microsoft-graph  					| 1.5.0-SNAPSHOT  |
    | org.apache.oltu.oauth2.client		| 1.0.2   		|

#### 实现功能
    | 生成认证登录url |
    | 认证登录回调authorize，生成IGraphServiceClient，获取登录用户信息|
    | IGraphServiceClient获取邮件列表 |
    | IGraphServiceClient生成日历事件 |

#### 参考文档
[Microsoft Graph REST API 1.0 版参考](https://docs.microsoft.com/zh-cn/graph/api/overview?toc=.%2Fref%2Ftoc.json&view=graph-rest-1.0)
[Microsoft identity platform and OAuth 2.0 authorization code flow](https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-auth-code-flow)
[Graph 浏览器](https://developer.microsoft.com/zh-cn/graph/graph-explorer#)

####github参考
[msgraph-sdk-java](https://github.com/microsoftgraph/msgraph-sdk-java)
[msgraph-sdk-java-auth](https://github.com/microsoftgraph/msgraph-sdk-java-auth)
\*msgraph-sdk-java-auth 0.1.0-SNAPSHOT版本  提供的jar与github上源码不符\*参考[AuthorizationCodeProvider Cannot be use](https://github.com/microsoftgraph/msgraph-sdk-java-auth/issues/15) 所以在本示例中直接使用的msgraph-sdk-java-auth源码

### End