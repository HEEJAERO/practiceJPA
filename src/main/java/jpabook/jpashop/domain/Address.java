package jpabook.jpashop.domain;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

@Embeddable // 값 타입일때 사용하는 어노테이션 (어딘가에 내장될 수 있다)
public class Address {

    private String city;
    private String street;
    private String zipcode;

}
