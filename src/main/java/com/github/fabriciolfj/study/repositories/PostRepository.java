package com.github.fabriciolfj.study.repositories;

import com.github.fabriciolfj.study.entity.Post;
import com.github.fabriciolfj.study.projection.PostWithUserRecord;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {


    @Query("""
        select new com.github.fabriciolfj.study.projection.PostWithUserRecord(p, u)
            from Post p
            left join PostDetails pd on pd.id = p.id
            left join pd.createdBy u
        where p.id = :postId""")
    Optional<PostWithUserRecord> findPostWithUser(@Param("postId") Long postId);
}
