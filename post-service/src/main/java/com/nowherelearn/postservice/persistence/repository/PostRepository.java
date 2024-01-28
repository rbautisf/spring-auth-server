package com.nowherelearn.postservice.persistence.repository;

import com.nowherelearn.postservice.persistence.entity.Post;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "posts", path = "posts")
public interface PostRepository extends JpaRepository<Post, UUID> {
    Optional<Post> findByAuthorName(String authorName);
}
