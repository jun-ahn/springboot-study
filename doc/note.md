##관심사의 분리 

기존 문제
```java
public class OrderServiceImpl implements OrderService{

    //할인 정책을 적용하려다보니 OrderServiceImpl을 변경해야한다!!!
    private final MemberRepository memberRepository = new MemoryMemberRepository();
    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
}

```

해결 방법 -> 인터페이스에만 의존하도록 하면 된다!
그러기 위해서는 누군가 클라이언트(OrderServiceImpl, DiscountPlicy)의 구현 객체를 대신 생성하고
주집해주어야 한다.

AppConfig의 등장 -> 구현 객체를 생성하고 연결하는 책임을 가지고 있는 Class

## 생성자 주입

```java
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository = new MemoryMemberRepository(); 이렇게 하면,  MemberServiceImpl이 MemoryMemberRepository에도 의존한다.    
}

생성은 이렇게...
public class AppConfig { -> 구현객체를 생성하는 class
    public MemberService memberService(){
        return new MemberServiceImpl(new MemoryMemberRepository()); //생성자 주입
    }
}

public class OrderServiceImpl implements OrderService{
    원래 코드.. interface말고 class에 의존적이다.
    private final MemberRepository memberRepository = new MemoryMemberRepository();
    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
}

public class AppConfig {
    public OrderService orderService(){
        return new OrderServiceImpl(new MemoryMemberRepository(), new FixDiscountPolicy());
    }
}

```

Appconfig refactoring -> 역할과 구현이 한눈에 들어오게..

```java
public class AppConfig {

    public MemberService memberService(){
        return new MemberServiceImpl(new MemoryMemberRepository()); //생성자 주입
    }

    public OrderService orderService(){
        return new OrderServiceImpl(new MemoryMemberRepository(), new FixDiscountPolicy());
    }
}

이렇게 변경..

public class AppConfig {

    public MemberService memberService(){
        return new MemberServiceImpl(memberRepository()); //생성자 주입
    }

    private MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    public OrderService orderService(){
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    private DiscountPolicy discountPolicy() {
        return new FixDiscountPolicy();
    }
}

```

기존 프로그램은 클라이언트 구현객체가 스스로 필요한 구현 객체를 생성, 연결 실행. 구현 객페가 프로그램의 제어 흐름을 스스로 조종
AppConfig가 등장한 이후 구현 객체는 자신의 로직을 실행하는 역할만 한다.
AppConfig처럼 객체를 생성하고 관리하며서 의존관계를 연결해주는 것을 IOC컨테이너 또는 DI컨테이너라고 한다.
제어의 역전 

Sping 사용하는 법
```java
ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
```
이렇게 하면 스프링에서 AppConfig에 있는 Bean을 스프링컨테이너에 모두 등록하고, 관리한다.

```java
public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();
        MemberService memberService = appConfig.memberService();
        이렇게 생성했던것을
        
        이렇게 생성한다.
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
    
}

```
ApplicationContext를 스프링 컨테이너라고 한다. 
@Bean이라고 적힌 메서드를 모두 호출해서 반환된 객체를 스프링컨테이너에 등록한다.

Singletone
호출할때마다 객체가 생성된다 = 비효율
해결방안은 객체가 1개만 생서오디고, 공유하도록 설계하면 된다.

```java
public class SingletonService {

    //자기자신을 스태틱 영역에 가지고 있는 것
    private static final SingletonService instance = new SingletonService();

    //조회하고 싶으면 이것을 호출한다
    public static SingletonService getInstance(){
        return instance;
    }

    //밖에서는 호출할 수 없도록 private생성자를 만든다
    private SingletonService(){

    }

    public void logic(){
        System.out.println("싱글콘 객체 로직 호출");
    }

}
```

ConfigurationSingletoneTest
스프링 @Configuration을 통해서 생성한 @Bean은 싱글톤을 보장한다.
@Configuration을 지우면 bean에 올려주기는 하지만 싱글톤이 아니다.


컴포넌트 스캔
@ComponentScan( excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class))
-> 예제로 만들어 놓은 AppConfig에 @Configuration도 자동으로 읽어와서 충돌이 일어나는 것을 방지하기 위함 

@Component만 붙여주면 @ComponentScan이 자동으로 Bean으로 올려준다 -> @Bean을 붙일 필요가 없다!

```java

@Component
public class RateDiscountPolicy implements DiscountPolicy{
    ...
    
}
```

@Autowired
의존관계 주입을 자동으로 해주는 것. 생성자에 붙여준다
AppConfig에서 했던 의존관계 주입을 자동으로 해준다.

```java
@Component
public class MemberServiceImpl implements MemberService{
    
    private final MemberRepository memberRepository;

    @Autowired -> MemberRepository type에 맞는 bean을 찾아와서 자동으로 주입해준다. 마치 ac.getBean(MemberRepository.class)하는 것 처럼!
    public MemberServiceImpl(MemberRepository memberRepository) {  
        this.memberRepository = memberRepository;
    }

    
```

basePackages: 어디서부터 찾는지 지정하는 것. basePackages 하위에 있는 것부터 컴포넌트 스캔을 시작한다.
시간을 절약하기 위해서 사용한다.
컴포넌트 스캔을 안하고 싶은 부분이 있을 수 있는데, 그럴 때 사용한다.

```java
package hello.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan( 
        basePackages = "hello.core.member", -> hello.core.member 하위를 뒤지기 시작한다 
        basePackageClasses = AutoAppConfig.class, -> AutoAppConfig 가 위치한 hello.core서부터 뒤지기 시작한다.
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
)
public class AutoAppConfig {
}

```
default: @ComponentScan을 사용한 클래스 하위부터 뒤지기 시작한다
권장하는 방법: 패키지 위치를 지정하지 않고, 설정 정보 클래스의 위치를 프로젝트 최상단에 주는 것을 권장

@Component("service") -> 이 이름을 두번 등록하면 Err
bean의 충돌. 같은 이름으로 bean을 등록하면 err가 난다 
@Bean(name="MemoryMemberRepository") -> 이 이름을 두번 

자동과 수동이 충돌이 날 경우, 스프링은 자동이 수동을 Override한다(수동이 우선권)-> 이러면 찾기가 어려운 에러가 날 수 있다.
최근의 스프링부트는 자동과 수동이 겹치면 Err를 내보낸다.
오버라이드하고 싶으면 application.propertise에 설정값을 넣어주면된다

##의존관계주입방법

###생성자주입
- 생성자 호출시점에 호출되는 것이 보장됨
- 불편, 필수 의존관계에 사용
- 생성자가 하나만 있으면 스프링 자동으로 @Autowired를 해준다

###수정자주입(Setter주입)
- private 멤버의 setter위에 @Autowired를 붙이는 경우.
```java

@Component
public class OrderServiceImpl implements OrderService{
    private MemberRepository memberRepository;
    private DiscountPolicy discountPolicy;

    @Autowired
    public void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Autowired
    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
    }
}
```
- 수정자 주입을 할 경우 생성자 주입은 하지 않아도 된다.
- 선택, 변경 가능성이 있는 의존관계에서 사용

###필드주입
```java
public class OrderServiceImpl implements OrderService {

    @Autowired private MemberRepository memberRepository;
    @Autowired private DiscountPolicy discountPolicy;
}
```
- 코드가 간결해지지만, 외부에서 변경이 불가능하므로 쓰면 안된다. (test환경에서나 사용됨)

###일반메서드주입
아무 메서드에서나 @Autowired를 사용하는 것
```java
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    public void init(MemberRepository memberRepository, DiscountPolicy discountPolicy){
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
}
```

