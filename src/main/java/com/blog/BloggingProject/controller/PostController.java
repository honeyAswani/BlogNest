package com.blog.BloggingProject.controller;

import com.blog.BloggingProject.model.Post;
import com.blog.BloggingProject.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.blog.BloggingProject.repository.PostRepo;

@Controller
public class PostController {
    @Autowired
    private PostRepo repo;
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {

        model.addAttribute("listPosts", postService.getAllPosts());

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("currentUsername", authentication.getName());
        } else {
            model.addAttribute("loggedIn", false);
        }

        return "index";
    }

    @GetMapping("/new")
    public String newPost(Model model) {
        model.addAttribute("post", new Post());
        return "new_post";
    }

    @PostMapping("/save")
    public String savePost(
            @Valid @ModelAttribute("post") Post post,
            BindingResult result,
            Authentication authentication) {

        if (result.hasErrors()) {
            return "new_post";
        }

        postService.createPost(post, authentication.getName());
        return "redirect:/";
    }

    @GetMapping("/post/{id}")
    public String viewPost(@PathVariable int id,
                           Model model,
                           Authentication authentication) {

        Post post = repo.findById(id).orElse(null);

        if (post == null) {
            return "redirect:/";
        }

        boolean isOwner = authentication != null
                && authentication.getName().equals(post.getAuthor().getUsername());

        model.addAttribute("post", post);
        model.addAttribute("isOwner", isOwner);

        return "post";
    }

    @GetMapping("/edit/{id}")
    public String editPost(
            @PathVariable int id,
            Model model,
            Authentication authentication) {

        Post post = postService.getPost(id);

        if (post == null) {
            return "redirect:/";
        }

        checkOwner(post, authentication);

        model.addAttribute("post", post);
        return "edit_post";
    }

    @PostMapping("/update")
    public String updatePost(
            @Valid @ModelAttribute("post") Post post,
            BindingResult result,
            Authentication authentication) {

        if (result.hasErrors()) {
            return "edit_post";
        }

        postService.updatePost(post.getId(), post, authentication.getName());
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
public String deletePost(
        @PathVariable int id,
        Authentication authentication) {

    try {
        postService.deletePost(id, authentication.getName());
        return "redirect:/";
    } catch (AccessDeniedException e) {
        return "redirect:/access-denied";
    }
}

    @GetMapping("/my-posts")
    public String myPosts(Model model, Authentication authentication) {

        model.addAttribute("listPosts",
                postService.getMyPosts(authentication.getName()));

        model.addAttribute("currentUsername", authentication.getName());

        return "my_posts";
    }

    private void checkOwner(Post post, Authentication authentication) {

        if (!post.getAuthor().getUsername().equals(authentication.getName())) {
            throw new AccessDeniedException("You are not allowed to modify this post");
        }
    }
}
