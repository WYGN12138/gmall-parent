package com.atguigu.gmall.search.repository;


import com.atguigu.gmall.search.bean.Person;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository //@Controller--控制器  @Service--业务逻辑组件 @Component--任何 @Repository--数据库层
public interface PersonRepository extends PagingAndSortingRepository<Person,Long> {

    //SpringData 起名
    // 查询address 在北京的人
    List<Person> findAllByAddressLike(String address);

    //查询 年龄小于等于 19的人
    List<Person> findAllByAgeLessThanEqual(Integer age);

    //查询年龄 大于18 且 在北京的
    List<Person> findAllByAgeGreaterThanAndAddressLike(Integer age, String address);



    List<Person> findAllByAgeGreaterThanAndAddressLikeOrIdEquals(Integer age, String address, Long id);
}
