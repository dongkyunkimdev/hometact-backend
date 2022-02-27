package kdk.hometact.post;

import java.util.Optional;
import kdk.hometact.postcategory.PostCategory;
import kdk.hometact.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Override
	@EntityGraph(attributePaths = {"user", "postCategory", "postLikes"})
	Page<Post> findAll(Pageable pageable);

	@Override
	@EntityGraph(attributePaths = {"user", "postCategory", "postLikes"})
	Optional<Post> findById(Long aLong);

	@EntityGraph(attributePaths = {"user", "postCategory", "postLikes"})
	Page<Post> findAllByPostCategory(Pageable pageable, PostCategory postCategory);

	@EntityGraph(attributePaths = {"user", "postCategory", "postLikes"})
	Page<Post> findAllByUser(Pageable pageable, User user);
}
