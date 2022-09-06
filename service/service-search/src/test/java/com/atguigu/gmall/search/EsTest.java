package com.atguigu.gmall.search;


import com.atguigu.gmall.search.bean.Person;
import com.atguigu.gmall.search.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EsTest {
    @Autowired
    PersonRepository personRepository;

    @Test
    void save(){
        Person person = new Person();
        person.setId(0L);
        person.setFirstName("李");
        person.setLastName("洋洋");
        person.setAge(18);
        person.setAddress("我的家在陕西你来过码");

        personRepository.save(person);
        System.out.println("完成，，，，，");


    }

}
