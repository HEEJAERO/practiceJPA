package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // 값 타입일때 사용하는 어노테이션 (어딘가에 내장될 수 있다)
@Getter // 값타입은 생성할때만 값이 생성 -> setter 제공 x
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
