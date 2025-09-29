package com.mypractice.oauth.member.repository;

import com.mypractice.oauth.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// 여기서 JapRepository<?, ?> ?안에는 Entity명과 pk를 넣어주면 된다.
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findBySocialId(String socialId);
}
