package roomescape.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// @Controller: 반환한 문자열을 "뷰 이름"으로 보고 templates 폴더에서 HTML을 찾아 보여준다.
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home"; // templates/home.html 을 렌더링
    }
}
