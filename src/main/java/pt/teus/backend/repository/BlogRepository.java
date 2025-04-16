package pt.teus.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.teus.backend.entity.blogs.Blog;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByCategory(String category);

    List<Blog> findTop3ByCategoryAndIdNot(String category, Long id);

}

