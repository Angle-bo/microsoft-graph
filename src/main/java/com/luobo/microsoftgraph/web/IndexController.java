package com.luobo.microsoftgraph.web;

import com.alibaba.fastjson.JSON;
import com.luobo.microsoftgraph.entity.*;
import com.luobo.microsoftgraph.service.OutlookService;
import com.luobo.microsoftgraph.utils.AuthHelper;
import com.luobo.microsoftgraph.utils.DateUtil;
import com.luobo.microsoftgraph.utils.OutlookServiceBuilder;
import okhttp3.RequestBody;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class IndexController {

    @Autowired
    private AuthHelper authHelper;

    @Autowired
    private OutlookServiceBuilder outlookServiceBuilder;


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
        // Get the expected state value from the session
        HttpSession session = request.getSession();
        UUID expectedState = (UUID) session.getAttribute("expected_state");
        UUID expectedNonce = (UUID) session.getAttribute("expected_nonce");

        // Make sure that the state query parameter returned matches
        // the expected state
        if (state.equals(expectedState)) {
            IdToken idTokenObj = IdToken.parseEncodedToken(idToken, expectedNonce.toString());

            if (idTokenObj != null) {
                TokenResponse tokenResponse = authHelper.getTokenFromAuthCode(code, idTokenObj.getTenantId());
                session.setAttribute("tokens", tokenResponse);
                session.setAttribute("userConnected", true);
                session.setAttribute("userName", idTokenObj.getName());
                session.setAttribute("userTenantId", idTokenObj.getTenantId());
                try {
                    OutlookService outlookService = outlookServiceBuilder.getOutlookService(tokenResponse.getAccessToken(), null);
                    OutlookUser user = outlookService.getCurrentUser().execute().body();
                    session.setAttribute("userPrincipalName", user.getUserPrincipalName());
                }catch (IOException e){
                    session.setAttribute("error", e.getMessage());
                }
            } else {
                session.setAttribute("error", "ID token failed validation.");
            }
        }
        else {
            session.setAttribute("error", "Unexpected state returned from authority.");
        }
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
        TokenResponse tokens = (TokenResponse)session.getAttribute("tokens");
        if (tokens == null) {
            // No tokens in session, user needs to sign in
            redirectAttributes.addFlashAttribute("error", "Please sign in to continue.");
            return "redirect:/";
        }

        String tenantId = (String)session.getAttribute("userTenantId");

        tokens = authHelper.ensureTokens(tokens, tenantId);

        String email = (String)session.getAttribute("userEmail");

        OutlookService outlookService = outlookServiceBuilder.getOutlookService(tokens.getAccessToken(), email);

        // Retrieve messages from the inbox
        String folder = "inbox";
        // Sort by time received in descending order
        String sort = "receivedDateTime DESC";
        // Only return the properties we care about
        String properties = "receivedDateTime,from,isRead,subject,bodyPreview";
        // Return at most 10 messages
        Integer maxResults = 10;

        try {
            PagedResult<Message> messages = outlookService.getMessages(
                    folder, sort, properties, maxResults)
                    .execute().body();
            model.addAttribute("messages", messages.getValue());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }

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
        TokenResponse tokens = (TokenResponse)session.getAttribute("tokens");
        if (tokens == null) {
            // No tokens in session, user needs to sign in
            redirectAttributes.addFlashAttribute("error", "Please sign in to continue.");
            return "redirect:/";
        }

        String tenantId = (String)session.getAttribute("userTenantId");

        tokens = authHelper.ensureTokens(tokens, tenantId);

        String email = (String)session.getAttribute("userEmail");

        OutlookService outlookService = outlookServiceBuilder.getOutlookService(tokens.getAccessToken(), email);

        try {
            Map<String,Object> start= new HashMap<>(2);
            start.put("dateTime", DateUtil.dfTZ(DateUtil.plusHours(2)));
            start.put("timeZone","UTC");
            Map<String,Object> end= new HashMap<>(2);
            end.put("dateTime",DateUtil.dfTZ(DateUtil.plusHours(3)));
            end.put("timeZone","UTC");
            Map<String,Object> json=new HashMap<>(3);
            json.put("subject","这是测试");
            json.put("start",start);
            json.put("end",end);

            RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), JSON.toJSONString(json));
            Event event=outlookService.calendarsReadWrite(body) .execute().body();
            model.addAttribute("event",event);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }

        return "event";
    }


}
