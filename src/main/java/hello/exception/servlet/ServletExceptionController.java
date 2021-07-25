package hello.exception.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    @GetMapping("/error-400")
    public void error400(HttpServletResponse response) throws IOException {
        response.sendError(400, "400 오류!");
    }

    /**
     * HttpServletResponse의 sendError() 메소드를 사용해서 404 HTTP 상태코 드와 메시지를 반환
     *
     * @param response
     */
    @GetMapping("/error-404")
    public void error404(HttpServletResponse response) throws IOException {
        response.sendError(404, "404 오류!");
    }

    /**
     * HttpServletResponse의 sendError() 메소드를 사용해서 404 HTTP 상태코 드와 메시지를 반환
     *
     * @param response
     * @throws IOException
     */
    @GetMapping("/error-500")
    public void error500(HttpServletResponse response) throws IOException {
        response.sendError(500);
    }
}
