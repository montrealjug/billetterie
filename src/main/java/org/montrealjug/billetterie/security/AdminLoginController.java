// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AdminLoginController {

    @GetMapping("/admin/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login-failed")
    public RedirectView loginFailed() {
        return new RedirectView("/");
    }
}
