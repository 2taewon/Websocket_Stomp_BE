package com.example.chatserver.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder // builder와 allargs는 같이 따라다니면 좋다.
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Member {

    @Id // pk 사용
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동으로 값이 증가되고 정해주는 것
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING) // string으로 하지 않으면 id값 숫자로 그대로 들어가서 String을 통해 enum값 그대로 들어감.
    @Builder.Default
    private Role role = Role.USER;
}
