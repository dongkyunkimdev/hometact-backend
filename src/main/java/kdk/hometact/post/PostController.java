package kdk.hometact.post;

import java.util.List;
import javax.validation.Valid;
import kdk.hometact.post.dto.PostDto;
import kdk.hometact.post.dto.UploadPostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

	private final PostService postService;

	@PostMapping
	public ResponseEntity<PostDto> uploadPost(@Valid @RequestBody UploadPostDto postDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(postService.uploadPost(postDto));
	}

	@GetMapping("/get")
	public ResponseEntity<List<PostDto>> selectAllPost(
		@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
		@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
		@RequestParam(value = "order", required = false, defaultValue = "createdDate") String order) {
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(order).descending());

		return ResponseEntity.ok().body(postService.selectAllPost(pageRequest));
	}

	@GetMapping("/get/category/{postCategoryId}")
	public ResponseEntity<List<PostDto>> selectAllPostByPostCategory(
		@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
		@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
		@RequestParam(value = "order") String order,
		@PathVariable Long postCategoryId) {
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(order).descending());

		return ResponseEntity.ok().body(postService.selectAllPostByPostCategory(pageRequest, postCategoryId));
	}

	@GetMapping("/get/{postId}")
	public ResponseEntity<PostDto> selectPost(@PathVariable Long postId) {
		postService.addViewCnt(postId);
		return ResponseEntity.ok().body(postService.selectPost(postId));
	}

	@DeleteMapping("/{postId}")
	public ResponseEntity deletePost(@PathVariable Long postId) {
		postService.deletePost(postId);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{postId}")
	public ResponseEntity<PostDto> updatePost(@PathVariable Long postId,
		@Valid @RequestBody UploadPostDto postDto) {
		return ResponseEntity.ok()
			.body(postService.updatePost(postId, postDto));
	}

	@GetMapping("/my/post")
	public ResponseEntity<List<PostDto>> selectAllMyPost(
		@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
		@RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdDate").descending());

		return ResponseEntity.ok().body(postService.selectAllMyPost(pageRequest));
	}

	@GetMapping("/my/like")
	public ResponseEntity<List<PostDto>> selectAllMyLikePost(
		@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
		@RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdDate").descending());

		return ResponseEntity.ok().body(postService.selectAllMyLikePost(pageRequest));
	}
}
