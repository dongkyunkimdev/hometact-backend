package kdk.hometact.postlike;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/postlike")
public class PostLikeController {

	private final PostLikeService postLikeService;

	@PostMapping("{postId}")
	public ResponseEntity addLike(@PathVariable Long postId) {
		postLikeService.addLike(postId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("{postId}")
	public ResponseEntity cancelLike(@PathVariable Long postId) {
		postLikeService.cancelLike(postId);
		return ResponseEntity.ok().build();
	}
}
