package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String username;

    @Embedded
    private Address address;


    @OneToMany(mappedBy = "member")  //order 테이블에 있는 맴버필드에 의해 맵핑!
    private List<Order> orders = new ArrayList<>();

}