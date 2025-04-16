package pt.teus.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.teus.backend.entity.blogs.Blog;
import pt.teus.backend.repository.BlogRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public List<Blog> getBlogsByCategory(String category) {
        return blogRepository.findByCategory(category);
    }

    public List<Blog> getBlogsByTag(String tag) {
        return blogRepository.findAll().stream()
                .filter(blog -> blog.getTags() != null && blog.getTags().contains(tag))
                .collect(Collectors.toList());
    }


    public Blog getBlogById(Long id) {
        return blogRepository.findById(id).orElseThrow(() -> new RuntimeException("Blog not found"));
    }

    public Blog createBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    public List<Blog> getRelatedPostsByCategory(String category, Long excludePostId) {
        return blogRepository.findTop3ByCategoryAndIdNot(category, excludePostId);
    }

}
