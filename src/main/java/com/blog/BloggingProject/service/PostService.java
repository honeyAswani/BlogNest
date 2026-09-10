package com.blog.BloggingProject.service;

import com.blog.BloggingProject.model.Post;
import com.blog.BloggingProject.model.User;
import com.blog.BloggingProject.repository.PostRepo;
import com.blog.BloggingProject.repository.UserRepo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepo postRepo;
    private final UserRepo userRepo;

    public PostService(PostRepo postRepo, UserRepo userRepo) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    public List<Post> getAllPosts() {
        return postRepo.findAllByOrderByIdDesc();
    }

    public Post getPost(int id) {
        return postRepo.findById(id).orElse(null);
    }

    public Post createPost(Post post, String username) {
        User user = getUser(username);
        post.setAuthor(user);
        return postRepo.save(post);
    }

    public Post updatePost(int id, Post formPost, String username) {

        Post existingPost = getPost(id);

        if (existingPost == null) {
            return null;
        }

        checkOwnership(existingPost, username);

        existingPost.setTitle(formPost.getTitle());
        existingPost.setContent(formPost.getContent());

        return postRepo.save(existingPost);
    }

    public boolean deletePost(int id, String username) {

        Post post = getPost(id);

        if (post == null) {
            return false;
        }

        checkOwnership(post, username);
        postRepo.delete(post);

        return true;
    }

    public List<Post> getMyPosts(String username) {
        return postRepo.findByAuthorOrderByIdDesc(getUser(username));
    }

    private User getUser(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));
    }

    private void checkOwnership(Post post, String username) {

        if (!post.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not allowed to modify this post");
        }
    }
}
