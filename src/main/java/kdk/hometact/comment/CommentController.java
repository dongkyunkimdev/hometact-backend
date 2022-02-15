package kdk.hometact.comment;

import javax.validation.Valid;
import kdk.hometact.comment.dto.CommentDto;
import kdk.hometact.comment.dto.UpdateCommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<CommentDto> uploadComment(@Valid @RequestBody CommentDto commentDto) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(commentService.uploadComment(commentDto));
	}

//	@GetMapping("/{postId}")
//	public ResponseEntity<List<CommentDto>> selectAllPostComment(@PathVariable Long postId) {
//		return ResponseEntity.ok().body(commentService.selectAllPostComment(postId));
//	}
//
//	@GetMapping("/{commentId}")
//	public ResponseEntity<CommentDto> selectComment(@PathVariable Long commentId) {
//		return ResponseEntity.ok().body(commentService.selectComment(commentId));
//	}

	@PatchMapping("/{commentId}")
	public ResponseEntity<CommentDto> updateComment(@PathVariable Long commentId,
		@Valid @RequestBody UpdateCommentDto updateCommentDto) {
		return ResponseEntity.ok().body(commentService.updateComment(commentId, updateCommentDto));
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity deleteComment(@PathVariable Long commentId) {
		commentService.deleteComment(commentId);
		return ResponseEntity.ok().build();
	}
}
