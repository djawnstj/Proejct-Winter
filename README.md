# Project-Winter
SpringMVC의 작동 원리를 학습하며 체득시키기 위한 백엔드 프레임워크

[블로그 정리](https://djawnstj.tistory.com/24)

## 핵심
![image](https://github.com/djawnstj/Project-Winter/assets/90193598/70aa8513-0514-4474-89af-48c1c1951a89)
프론트 컨트롤러 패턴을 적용해 [`DispatcherServlet`](https://github.com/djawnstj/Project-Winter/blob/main/src/main/java/com/project/winter/DispatcherServlet.java) 이 모든 요청을 받아 적절한 [핸들러를 찾고](https://github.com/djawnstj/Project-Winter/tree/main/src/main/java/com/project/winter/mvc/handler) [View를 반환](https://github.com/djawnstj/Project-Winter/tree/main/src/main/java/com/project/winter/mvc/view)하도록 구현

리플렉션을 이용해 컴포넌트를 런타임시에 동적으로 생성하여 [Bean 객체](https://github.com/djawnstj/Project-Winter/tree/main/src/main/java/com/project/winter/beans)로 관리하도록 하였고, 적절한 Bean 을 찾아 DI 를 해주는 IoC 역할을 할 수 있게끔 구현.

이 외에 [인터셉터](https://github.com/djawnstj/Project-Winter/tree/main/src/main/java/com/project/winter/mvc/intercpetor), [예외처리](https://github.com/djawnstj/Project-Winter/tree/main/src/main/java/com/project/winter/mvc/resolver/exception) 등의 기능을 구현함.
