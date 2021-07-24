package hello.exception.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ServletExceptionController {

    /**
     * 강제로 런타임 예외를 발생시키는 핸들레 메소드
     * Exception의 경우 서버 내부에서 처리할 수 없는 오류가 발생한 것으로 생각해서
     * HTTP 상태 코드 500을 반환
     */
    @GetMapping("/error-ex")
    public void errorEx() {
        throw new RuntimeException("예외 발생!");
    }
}
