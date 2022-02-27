package kdk.hometact.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	@EntityGraph(attributePaths = {"authorities", "postLikes"})
	Optional<User> findOneWithAuthoritiesByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);
}
