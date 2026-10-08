package com.mateusleite.loginseguro.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Página inicial pública.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "pages/index";
    }
}
