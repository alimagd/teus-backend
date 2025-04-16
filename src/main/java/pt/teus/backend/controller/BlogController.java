package pt.teus.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.teus.backend.entity.blogs.Blog;
import pt.teus.backend.service.BlogService;

import java.util.List;
@RestController
@RequestMapping("/api/v1/blogs")
@CrossOrigin(origins = "*") // Allow frontend to access API
public class BlogController {

    @Autowired
    private BlogService blogService;

    @GetMapping
    public List<Blog> getAllBlogs() {
        return blogService.getAllBlogs();
    }

    @GetMapping("/category/{category}")
    public List<Blog> getBlogsByCategory(@PathVariable String category) {
        return blogService.getBlogsByCategory(category);
    }

    @GetMapping("/tag/{tag}")
    public List<Blog> getBlogsByTag(@PathVariable String tag) {
        return blogService.getBlogsByTag(tag);
    }

    @PostMapping
    public Blog createBlog(@RequestBody Blog blog) {
        return blogService.createBlog(blog);
    }

    @GetMapping("/related/{postId}")
    public ResponseEntity<List<Blog>> getRelatedPosts(@PathVariable Long postId) {
        Blog post = blogService.getBlogById(postId);
        List<Blog> relatedPosts = blogService.getRelatedPostsByCategory(post.getCategory(), postId);
        return ResponseEntity.ok(relatedPosts);
    }

}


