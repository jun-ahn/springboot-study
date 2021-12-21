package hello.core.beanfind;

import hello.core.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationContextInfoTest {

    AnnotationConfigApplicationContext  ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("모든빈출력하기")
    void findAllBean(){
        String[] beanDeinitionNames = ac.getBeanDefinitionNames();
        for (String beanDeinitionName : beanDeinitionNames) {
            Object bean = ac.getBean(beanDeinitionName);
            System.out.println("name = " + beanDeinitionName + "object = " + bean);

        }
    }    
    
    @Test
    @DisplayName("애플리케이션 빈 출력하기")
    void findApplicationBean(){
        String[] beanDeinitionNames = ac.getBeanDefinitionNames();
        for (String beanDeinitionName : beanDeinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDeinitionName);

            //ROLE_APPLICATION
            //ROLE_INFRASTRUCTURE: 스프링에서 사용하는 빈빈
           if(beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION){
                Object bean = ac.getBean(beanDeinitionName);
                System.out.println("name = " + beanDeinitionName + "object = " + bean);

            }
        }
    }

}
