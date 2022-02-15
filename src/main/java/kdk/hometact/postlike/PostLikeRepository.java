package kdk.hometact.postlike;

import java.util.Optional;
import kdk.hometact.post.Post;
import kdk.hometact.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	Optional<PostLike> findByPostAndUser(Post post, User user);

	boolean existsByPostAndUser(Post post, User user);
}
