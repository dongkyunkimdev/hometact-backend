package kdk.hometact.comment;

import java.util.List;
import kdk.hometact.post.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@EntityGraph(attributePaths = "user")
	List<Comment> findAllByPost(Post post);
}
