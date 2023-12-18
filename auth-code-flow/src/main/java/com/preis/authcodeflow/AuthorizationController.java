package com.preis.authcodeflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class AuthorizationController {

    private final OAuthManager oAuthManager;

    public AuthorizationController(OAuthManager oAuthManager) {
        this.oAuthManager = oAuthManager;
    }

//    private String url = "https://dev-q7dzvu43orxj3t1e.us.auth0.com/authorize?response_type=code&client_id=W5AQYiq53tBnB2BLfKBBmbXhjSBDhUKZ&redirect_uri=http://localhost:8080/auth/callback&scope=openid&audience=https://my-app/api&state=xyzABC123";

    @GetMapping("/auth/login")
    public void login(HttpServletResponse httpServletResponse) {
        String url = oAuthManager.generateAuthorizeUser();
        httpServletResponse.setHeader("Location", url);
        httpServletResponse.setStatus(302);
    }

    @GetMapping("/auth/callback")
    void callback(String code, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        oAuthManager.loginUserByCode(code);

        String url = "/auth/home";
        httpServletResponse.setHeader("Location", url);
        httpServletResponse.setStatus(302);
    }

    @GetMapping("/auth/home")
    String home(Model model) {
        Map<String, Object> userDataMap = oAuthManager.getUserData();
        model.addAttribute("userDataMap", userDataMap);
        return "home";
    }
}
