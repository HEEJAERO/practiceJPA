package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {
    //spring boot 2.3 버전부터는 별로로 gradle에 'org.springframework.boot:spring-boot-starter-validation'을 추가시켜 줘야 검증가능
    @NotEmpty(message = "회원이름은 필수 입니다.")
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
