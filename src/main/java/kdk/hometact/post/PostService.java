package kdk.hometact.post;

import java.util.List;
import java.util.stream.Collectors;
import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.EntityNotFoundException;
import kdk.hometact.post.dto.PostDto;
import kdk.hometact.post.dto.UploadPostDto;
import kdk.hometact.postcategory.PostCategory;
import kdk.hometact.postcategory.PostCategoryRepository;
import kdk.hometact.security.SecurityUtil;
import kdk.hometact.user.User;
import kdk.hometact.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final PostCategoryRepository postCategoryRepository;

	@Transactional
	public PostDto uploadPost(UploadPostDto postDto) {
		Post post = toEntity(postDto);

		return PostDto.from(postRepository.save(post));
	}

	private Post toEntity(UploadPostDto postDto) {
		return Post.builder()
			.user(getUser())
			.postCategory(postCategoryRepository.findById(postDto.getPostCategoryId()).orElseThrow(
				() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
			))
			.title(postDto.getTitle())
			.content(postDto.getContent())
			.build();
	}

	private User getUser() {
		return SecurityUtil.getCurrentUsername()
			.flatMap(userRepository::findOneWithAuthoritiesByEmail).orElseThrow(
				() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));
	}

	@Transactional(readOnly = true)
	public List<PostDto> selectAllPost(PageRequest pageRequest) {
		return postRepository.findAll(pageRequest).getContent().stream()
			.map(post -> PostDto.from(post))
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public PostDto selectPost(Long postId) {
		return PostDto.from(postRepository.findById(postId).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage()))
		);
	}

	@Transactional
	public PostDto updatePost(Long postId, UploadPostDto postDto) {
		Post post = postRepository.findById(postId).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
		);

		PostCategory postCategory = postCategoryRepository.findById(postDto.getPostCategoryId())
			.orElseThrow(
				() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
			);

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
			.getAuthentication().getPrincipal();
		if (validAuthority(post, userDetails)) {
			post.update(postDto, postCategory);
		}

		return PostDto.from(post);
	}

	@Transactional
	public void deletePost(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
		);

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
			.getAuthentication().getPrincipal();
		if (validAuthority(post, userDetails)) {
			postRepository.delete(post);
		}
	}

	private boolean validAuthority(Post post, UserDetails userDetails) {
		if (post.getUser().getEmail().equals(userDetails.getUsername()) ||
			userDetails.getAuthorities().stream()
				.filter(authority -> authority.getAuthority().equals("ROLE_ADMIN"))
				.count() == 1) {
			return true;
		}
		throw new AccessDeniedException(ErrorCode.HANDLE_ACCESS_DENIED.getMessage());
	}

	@Transactional
	public void addViewCnt(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
		);
		post.addViewCnt();
	}
}
