package hello.core.singeton;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

public class StatefulServiceTest {

    /**
     * 싱클턴방식은 여러 클라이언트가 하나의 같은 객체 인스턴스를 공유하기 때문에 싱글턴 객체는 상태를 유지하게 설계하면 안된다.
     * 무상태로 설계해야 한다!
     *  - 특정 클라리언트에 의존적인 필드가 있으면 안된다
     *  - 특정 클라이언트가 값을 변경할 수 있는 필드가 있으면 안된다
     *  - 가급적 읽기만 가능해야한다.
     *  0 필드 대신에 자바에서 공유되지 않는 지역변수, 파라미터, ThreadLocal등을 사용해야 한다
     */
    @Test
    void statefuleServiceSingletone(){
        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

        StatefulService statefulService1 = ac.getBean("statefulService", StatefulService.class);
        StatefulService statefulService2 = ac.getBean("statefulService", StatefulService.class);
        //Thread A:A사용자 10,000원 주문
        statefulService1.order("userA", 10000);
        //Thread B:B사용자 20,000원 주문
        statefulService2.order("userB", 20000);

        //Thread A:사용자A 주문금액 조회
        int price = statefulService1.getPrice();
        System.out.println("price: "+statefulService1.getPrice());

        Assertions.assertThat(price).isEqualTo(20000); //10000원이 나와야하는데, 20000원이 나왔다
    }

    static class TestConfig {
        @Bean
        public StatefulService statefulService(){
            return new StatefulService();
        }
    }
}
