package kdk.hometact.comment;

import java.util.List;
import java.util.stream.Collectors;
import kdk.hometact.comment.dto.CommentDto;
import kdk.hometact.comment.dto.UpdateCommentDto;
import kdk.hometact.error.ErrorCode;
import kdk.hometact.error.exception.EntityNotFoundException;
import kdk.hometact.post.Post;
import kdk.hometact.post.PostRepository;
import kdk.hometact.security.SecurityUtil;
import kdk.hometact.user.User;
import kdk.hometact.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	@Transactional
	public CommentDto uploadComment(CommentDto commentDto) {
		Comment comment = toEntity(commentDto);

		return CommentDto.from(commentRepository.save(comment));
	}

	private Comment toEntity(CommentDto commentDto) {
		return Comment.builder()
			.post(getPost(commentDto.getPostId()))
			.user(getUser())
			.content(commentDto.getContent())
			.build();
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

	@Transactional(readOnly = true)
	public CommentDto selectComment(Long commentId) {
		return CommentDto.from(commentRepository.findById(commentId).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage()))
		);
	}

	@Transactional(readOnly = true)
	public List<CommentDto> selectAllPostComment(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
		);

		return commentRepository.findAllByPost(post).stream()
			.map(comment -> CommentDto.from(comment))
			.collect(Collectors.toList());
	}

	@Transactional
	public CommentDto updateComment(Long commentId, UpdateCommentDto updateCommentDto) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
		);

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
			.getAuthentication().getPrincipal();
		if (validAuthority(comment, userDetails)) {
			comment.update(updateCommentDto);
		}

		return CommentDto.from(comment);
	}

	@Transactional
	public void deleteComment(Long commentId) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND.getMessage())
		);

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
			.getAuthentication().getPrincipal();
		if (validAuthority(comment, userDetails)) {
			commentRepository.delete(comment);
		}
	}

	private boolean validAuthority(Comment comment, UserDetails userDetails) {
		if (comment.getUser().getEmail().equals(userDetails.getUsername()) ||
			userDetails.getAuthorities().stream()
				.filter(authority -> authority.getAuthority().equals("ROLE_ADMIN"))
				.count() == 1) {
			return true;
		}
		throw new AccessDeniedException(ErrorCode.HANDLE_ACCESS_DENIED.getMessage());
	}

}
