package com.mypractice.oauth.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder //Entity를 유연하게 
@AllArgsConstructor // 필드에있는 모든 애들로 생성자 만들어줌
@NoArgsConstructor // 기본 생성자 만들어줌
@Getter // 머 이건 알지? setter는 안정성을 위해서 뺐음
@Entity
public class Member {
    @Id // pk 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 이걸 설정하면 자동  auto_increment 가 적용됨
    private Long id;

    private String name;

    @Column(nullable = false, unique = true) //colum 설정인데 nullable  = false 면 null 허용 안함 unique = true면 unique 한 값
    private String email;

    private String password;

    @Enumerated(EnumType.STRING) // Enum값의 경우 이렇게 Enumerated(EnumType.STRING)으로 설정을 안해주면 첫번째게 0 두번째가 1 이렇게 들어가 버리니 꼭 설정해 줘야한다.
    @Builder.Default // Role.USER를 default값으로 사용하고 싶으면 Builder.Default 설정을 해줘야 한다.
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;
}
