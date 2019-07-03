# microsoft-graph

**Microsoft Graph REST API 接口调用**

#### 其它分支
使用msgraph-sdk-java请切换到[sdk分支](https://github.com/Angle-bo/microsoft-graph/tree/sdk)

#### 相关框架
    | spring-boot  		        | 2.1.6.RELEASE   |
    | spring-boot-starter-freemarker| 	          |
    | fastjson 			| 1.2.46  	  |
    | jackson.version  		| 2.9.9           |
    | retrofit2                     | 2.6.0 	  |
    | okhttp3                       | 3.12.0	  |

#### 实现功能
    | 生成认证登录url 
    | 认证登录回调authorize，生成TokenResponse，调用用户信息接口，获取登录用户信息
    | 使用TokenResponse封装请求获取邮件列表 
    | 使用TokenResponse封装请求生成日历事件 

#### 参考文档
[编写 Java Spring MVC Web 应用程序以获取 Outlook 邮件、日历和联系人](https://docs.microsoft.com/zh-cn/outlook/rest/java-tutorial)
<br>[Microsoft Graph REST API 1.0 版参考](https://docs.microsoft.com/zh-cn/graph/api/overview?toc=.%2Fref%2Ftoc.json&view=graph-rest-1.0)
<br>[Graph 浏览器](https://developer.microsoft.com/zh-cn/graph/graph-explorer#)

#### github参考
 无
### End