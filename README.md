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

### 오류 페이지 작동 원리

1. 서블릿은 Exception (예외)가 발생해서 서블릿 밖으로 전달되거나 또는 되었을 때 설정된 오류 페이지를 찾는다.
2. WAS는 해당 예외를 처리하는 오류 페이지 정보를 확인한다.
   ```java
   new ErrorPage(RuntimeException.class, "/error-page/500");
   ```
3. 예를 들어서 `RuntimeException` 예외가 WAS까지 전달되면, WAS는 오류 페이지 정보를 확인한다.
4. WAS가 확인해보니 `RuntimeException`의 오류 페이지로 `/error-page/500` 이 지정되어 있다면 WAS는 오류 페이지를 출력하기 위해 `/error-page/500`를 다시 요청한다.
    - 예외 발생과 오류 페이지 요청 흐름
   ```text
   1. WAS(여기까지 전파) < 필터 < 서블릿 < 인터셉터 < 컨트롤러(예외발생)
   2. WAS '/error-page/500' 다시 요청 > 필터 > 서블릿 > 인터셉터 > 컨트롤러(/error-page/500) > 뷰
   ```
5. 중요한 점은 웹 브라우저(클라이언트)는 서버 내부에서 이런 일이 일어나는지 전혀 모른다는 점이다. 오직 서버 내부에서 오류 페이지를 찾기 위해 추가적인 호출을 한다.

### 오류 정보 추가

1. WAS는 오류 페이지를 단순히 다시 요청만 하는 것이 아니라, 오류 정보를 `request`의 `attribute`에 추가해서 넘겨준다.
2. 필요하면 오류 페이지에서 이렇게 전달된 오류 정보를 사용할 수 있다.

## 필터와 인터셉터 그리고 서블릿이 제공하는 `DispatcherType` 이해하기

   ```text
   1. WAS(여기까지 전파) < 필터 < 서블릿 < 인터셉터 < 컨트롤러(예외발생)
   2. WAS '/error-page/500' 다시 요청 > 필터 > 서블릿 > 인터셉터 > 컨트롤러(/error-page/500) > 뷰
   ```

1. 오류가 발생하면 오류 페이지를 출력하기 위해 WAS 내부에서 다시 한번 호출이 발생한다. 이때 필터, 서블릿, 인터셉터도 모두 다시 호출된다.
2. 그런데 로그인 인증 체크 같은 경우를 생각해보면, 이미 한번 필터나, 인터셉터에서 로그인 체크를 완료했다.
3. 따라서 서버 내부에서 오류 페이지를 호출한다고 해서 해당 필터나 인터셉트가 한번 더 호출되는 것은 매우 비효율적이다.
4. 결국 클라이언트로 부터 발생한 정상 요청인지, 아니면 오류 페이지를 출력하기 위한 내부 요청인지 구분할 수 있어야 한다.
5. 서블릿은 이런 문제를 해결하기 위해 `DispatcherType`이라는 추가 정보를 제공한다.

### 필터와 DispatcherType

1. 필터는 이런 경우를 위해서 `dispatcherTypes` 라는 옵션을 제공한다.
   ```java
   log.info("dispatcherType={}", request.getDispatcherType());
   ```
2. 고객이 처음 요청하면 `dispatcherType=REQUEST`이다.
3. 이렇듯 서블릿 스펙은 실제 고객이 요청한 것인지, `서버가 내부에서 오류 페이지를 요청하는 것인지 DispatcherType 으로 구분할 수 있는 방법을 제공`한다.
    - javax.servlet.DispatcherType
       ```java
      public enum DispatcherType {
         FORWARD, //서블릿에서 다른 서블릿이나 JSP(뷰 템플릿)를 호출할 때 => RequestDispatcher.forward(request, response);
         INCLUDE, //서블릿에서 다른 서블릿이나 JSP의 결과를 포함할 때 => RequestDispatcher.include(request, response);
         REQUEST, //클라이언트 요청
         ASYNC, //서블릿 비동기 호출
         ERROR //오류 요청
      }
       ```

### 인터셉터

1. 앞서 필터의 경우에는 필터를 등록할 때 어떤 `DispatcherType`인 경우에 필터를 적용할 지 선택할 수 있었다.
2. 그런데 인터셉터는 서블릿이 제공하는 기능이 아니라 스프링이 제공하는 기능이기 때문에 `DispatcherType`과 무관하게 항상 호출된다.
3. 대신에 인터셉터는 요청 경로에 따라서 추가하거나 제외하기 쉽게 되어 있기 때문에, 이러한 설정을 사용해서 오류 페이지 경로를 `excludePathPatterns()`를 사용해서 빼주면 된다.

### 전체 흐름 정리

1. 정상 요청
   ```text
   WAS("/hello", dispatcherType=REQUEST) > 필터 > 서블릿 > 인터셉터 > 컨트롤러("/hello") > 뷰
   ```

2. 오류 요청
   ```text
   1. WAS("/error-ex", dispatcherType=REQUEST) > 필터 > 서블릿 > 인터셉터 > 컨트롤러
   2. WAS(여기까지 전파) < 필터 < 서블릿 < 인터셉터 < 컨트롤러(예외 발생)
   3. WAS 오류 페이지 확인
   4. WAS("/error-page/500", dispatcherType=ERROR) > 필터(X) > 서블릿 > 인터셉터(X) > 컨트롤러("/error-page/500") > 뷰
   ```
    - 필터는 `DispatcherType`으로 중복 호출 제거
        - dispatcheType=REQUEST
    - 인터셉터는 경로 정보로 중복 호출 제거
        - excludePathPatterns("/error-page/**")

