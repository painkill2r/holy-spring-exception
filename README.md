#### 최초 작성일 : 2021.07.24(토)

# Spring Boot 예외 처리와 오류 페이지

Spring Boot를 서블릿/스프링 예외 처리와 오류페이지 학습

## 학습 환경

1. OS : MacOS
2. JDK : OpenJDK 11.0.5
3. Framework : Spring Boot 2.5.3
    - [Spring Initializer 링크 : https://start.spring.io](https://start.spring.io)
    - 패키징 : jar
    - 의존설정(Dependencies)
        - Spring Web
        - Thymeleaf
        - Lombok
        - Validation
4. Build Tools : Gradle

## 서블릿 예외 처리

1. 서블릿은 다음 2가지 방식으로 예외를 처리한다.
    - Exception(예외)
    - response.sendError(HTTP 상태 코드, 오류 메시지)

### Exception

1. 자바 직접 실행
    - 자바의 main() 메소드를 직접 실행하는 경우 `main`이라는 스레드가 실행된다.
    - 실행 도중에 예외를 잡지 못하고 처음 실행한 main() 메소드를 넘어서 예외가 던져지면, 예외 정보를 남기고 해당 쓰레드는 종료 된다.

2. 웹 애플리케이션
    - 웹 애플리케이션은 사용자 `요청별로 별도의 쓰레드가 할당`되고, 서블릿 컨테이너 안에서 실행된다.
    - 애플리케이션에서 예외가 발생했는데, 어디선가 `try ~ catch`로 예외를 잡아서 처리하면 아무런 문제가 없다.
    - 그런데 만약에 애플리케이션에서 예외를 잡지 못하고, 서블릿 밖으로 까지 예외가 전달되면 결국 톰캣과 같은 WAS까지 예외가 전달된다.
   ```text
   WAS(여기까지 전파) < 필터 < 서블릿 < 인터셉터 < 컨트롤러(예외발생)
   ```

### response.sendError(HTTP 상태 코드, 오류 메시지)

1. 오류가 발생했을 때 `HttpServletResponse`가 제공하는 `sendError`라는 메소드를 사용해도 된다.
    - 이것을 호출한다고 당장 예외가 발생하는 것은 아니지만, 서블릿 컨테이너에게 오류가 발생했다는 점을 전달할 수 있다.
    - 이 메소드를 사용하면 HTTP 상태 코드와 오류 메시지를 같이 전달할 수 있다.
    - `response.sendError(HTTP 상태 코드)`
    - `response.sendError(HTTP 상태 코드, 오류 메시지)`
2. sendError의 흐름
   ```text
   WAS(sendError 호출 기록 확인) < 필터 < 서블릿 < 인터셉터 < 컨트롤러(response.sendError())
   ```
    - `response.sendError()`을 호출하면 `response` 내부에는 오류가 발생했다는 상태를 저장해둔다.
    - 그리고 서블릿 컨테이너는 클라이언트에게 응답하기 전에 `response`에 `sendError()`가 호출되었는지 확인한다.
    - 만약, 호출되었으면 설정한 오류 코드에 맞추어 기본 오류 페이지를 보여준다.

## 서블릿이 제공하는 오류 화면 기능 사용

1. 서블릿 컨테이너가 제공하는 기본 예외 처리 화면은 친화적이지 않다. 따라서 서블릿이 제공하는 오류 화면 기능을 사용하여 이를 변경해서 사용해야 한다.
2. 서블릿은 `Exception`이 발생해서 서블릿 밖으로 전달되거나, `response.sendError()`가 호출되었을 때 각각의 상황에 맞춘 오류 처리 기능을 제공한다.
    - 이 기능을 활용하면 친절한 오류 화면을 준비해서 클라이언트에게 보여줄 수 있다.
3. 과거에는 다음과 같이 `web.xml`이라는 파일에 오류 화면을 등록했다.
   ```xml
   <web-app>
       <error-page> 
           <error-code>404</error-code> 
           <location>/error-page/404.html</location> 
       </error-page> 
       <error-page> 
           <error-code>500</error-code> 
           <location>/error-page/500.html</location> 
       </error-page> 
       <error-page> 
           <exception-type>java.lang.RuntimeException</exception-type> 
           <location>/error-page/500.html</location> 
       </error-page> 
   </web-app>
   ```
4. 여기서는 스프링 부트를 통해서 서블릿 컨테이너를 실행하기 때문에, 스프링 부터가 제공하는 기능을 사용해서 서블릿 오류 페이지를 등록하면 된다. 