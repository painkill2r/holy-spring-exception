package hello.exception;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 스프링 부트가 제공하는 기능을 통해 오류 페이지 등록
 */
@Component
public class WebServerCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    /**
     * 오류 페이지 등록 후 각 오류 페이지를 처리할 컨트롤러를 구현해야 한다.
     *
     * @param factory
     */
    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error-page/404");//404 HTTP 상태 코드인 경우 보여줄 페이지(URL) 설정
        ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500");//500 HTTP 상태 코드인 경우 보여줄 페이지(URL) 설정
        ErrorPage errorPageEx = new ErrorPage(RuntimeException.class, "/error-page/500");//RuntimeException 또는 그 자식 예외 발생시 보여줄 페이지(URL) 설정

        factory.addErrorPages(errorPage404, errorPage500, errorPageEx); //위에서 선언한 오류 페이지 등록
    }
}
