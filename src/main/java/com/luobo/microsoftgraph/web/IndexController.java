package com.luobo.microsoftgraph.web;

import com.alibaba.fastjson.JSON;
import com.luobo.microsoftgraph.utils.AuthHelper;
import com.luobo.microsoftgraph.utils.DateUtil;
import com.microsoft.graph.models.extensions.*;
import com.microsoft.graph.requests.extensions.IEventCollectionRequestBuilder;
import com.microsoft.graph.requests.extensions.IMessageCollectionPage;
import com.microsoft.graph.requests.extensions.IMessageCollectionRequest;
import com.microsoft.graph.requests.extensions.IMessageCollectionRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

@Controller
public class IndexController {

    @Autowired
    private AuthHelper authHelper;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        UUID state = UUID.randomUUID();
        UUID nonce = UUID.randomUUID();
        // Save the state and nonce in the session so we can
        // verify after the auth process redirects back
        HttpSession session = request.getSession();
        session.setAttribute("expected_state", state);
        session.setAttribute("expected_nonce", nonce);
        String loginUrl = authHelper.getLoginUrl(state, nonce);
        model.addAttribute("loginUrl", loginUrl);
        return "login";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/";
    }

    /**
     * 认证完成重定向页面
     * @param code
     * @param idToken
     * @param state
     * @param request
     * @param model
     * @return
     */
    @PostMapping("/authorize")
    public String authorize(
            @RequestParam("code") String code,
            @RequestParam("id_token") String idToken,
            @RequestParam("state") UUID state,
            HttpServletRequest request,Model model) {

        HttpSession session = request.getSession();
        IGraphServiceClient graphClient = authHelper.getAuthorizationCodeProvider(code);
        session.setAttribute("graphClient",graphClient);
        User user = graphClient.me().buildRequest().get();
        session.setAttribute("displayName", user.displayName);
        session.setAttribute("userPrincipalName", user.userPrincipalName);
        return "authorize";
    }

    /**
     * 邮件列表页
     * @param model
     * @param request
     * @param redirectAttributes
     * @return
     */
    @RequestMapping("/mail")
    public String mail(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession();
        IGraphServiceClient graphClient = (IGraphServiceClient) session.getAttribute("graphClient");
        if (graphClient == null) {
            // No tokens in session, user needs to sign in
            redirectAttributes.addFlashAttribute("error", "Please sign in to continue.");
            return "redirect:/";
        }
        IMessageCollectionRequestBuilder messages = graphClient.me().messages();
        IMessageCollectionPage iMessageCollectionPage = messages.buildRequest().get();
        List<Message> currentPage = iMessageCollectionPage.getCurrentPage();
        model.addAttribute("messages", currentPage);
        return "mail";
    }

    /**
     * 创建日历事件
     * @param model
     * @param request
     * @param redirectAttributes
     * @return
     */
    @RequestMapping("/calendarsReadWrite")
    public String calendarsReadWrite(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession();
        IGraphServiceClient graphClient = (IGraphServiceClient) session.getAttribute("graphClient");
        if (graphClient == null) {
            // No tokens in session, user needs to sign in
            redirectAttributes.addFlashAttribute("error", "Please sign in to continue.");
            return "redirect:/";
        }
        IEventCollectionRequestBuilder events = graphClient.me().events();
        Event event=new Event();
        event.subject="这是测试";
        DateTimeTimeZone start=new DateTimeTimeZone();
        start.dateTime=DateUtil.dfTZ(DateUtil.plusHours(2));
        start.oDataType="UTC+8";
        event.start=start;
        DateTimeTimeZone end=new DateTimeTimeZone();
        end.dateTime=DateUtil.dfTZ(DateUtil.plusHours(3));
        end.oDataType="UTC+8";
        Event post = events.buildRequest().post(event);
        model.addAttribute("event", post);
        return "event";
    }


}
