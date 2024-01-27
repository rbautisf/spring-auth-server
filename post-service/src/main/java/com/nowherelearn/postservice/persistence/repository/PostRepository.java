package com.nowherelearn.postservice.persistence.repository;

import com.nowherelearn.postservice.persistence.entity.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "posts", path = "posts")
public interface PostRepository extends PagingAndSortingRepository<Post, UUID>, CrudRepository<Post, UUID> {
    Optional<Post> findByAuthorName(String authorName);
}
