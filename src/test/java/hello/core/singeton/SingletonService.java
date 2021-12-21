package hello.core.singeton;

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
