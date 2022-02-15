package kdk.hometact.post;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Override
	@EntityGraph(attributePaths = "user")
	Page<Post> findAll(Pageable pageable);

	@Override
	@EntityGraph(attributePaths = "user")
	Optional<Post> findById(Long aLong);
}
