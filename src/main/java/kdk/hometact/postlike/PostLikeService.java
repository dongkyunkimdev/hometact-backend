package kdk.hometact.postlike;

import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.EntityNotFoundException;
import kdk.hometact.post.Post;
import kdk.hometact.post.PostRepository;
import kdk.hometact.postlike.exception.PostLikeAlreadyAddException;
import kdk.hometact.security.SecurityUtil;
import kdk.hometact.user.User;
import kdk.hometact.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

	private final PostLikeRepository postLikeRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	@Transactional
	public void addLike(Long postId) {
		Post post = getPost(postId);
		User user = getUser();
		validDuplAddLike(post, user);

		postLikeRepository.save(PostLike.builder()
			.post(post)
			.user(user)
			.build());
	}

	private Post getPost(Long postId) {
		return postRepository.findById(postId).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
		);
	}

	private User getUser() {
		return SecurityUtil.getCurrentUsername()
			.flatMap(userRepository::findOneWithAuthoritiesByEmail).orElseThrow(
				() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));
	}

	private void validDuplAddLike(Post post, User user) {
		if (postLikeRepository.existsByPostAndUser(post, user)) {
			throw new PostLikeAlreadyAddException();
		}
	}

	@Transactional
	public void cancelLike(Long postId) {
		Post post = getPost(postId);
		User user = getUser();
		PostLike postLike = postLikeRepository.findByPostAndUser(post, user).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
		);
		postLikeRepository.delete(postLike);
	}

}
