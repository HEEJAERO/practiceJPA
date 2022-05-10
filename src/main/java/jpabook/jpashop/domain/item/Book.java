package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B") // 싱글테이블에 모든 상속받은 테이블값이 들어가는데 이를 구분하기 위한 DTYPE을 지정해 줄 수 있다.
@Getter @Setter
public class Book extends Item{ //

    private String author;
    private String isbn;

}
