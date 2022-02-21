package kdk.hometact.postcategory;

import java.util.List;
import kdk.hometact.postcategory.dto.PostCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/postcategory")
public class PostCategoryController {

	private final PostCategoryService postCategoryService;

	@GetMapping
	public ResponseEntity<List<PostCategoryDto>> selectAllPostCategory() {
		PageRequest pageRequest = PageRequest.of(0, 100, Sort.by("postCategoryId").ascending());

		return ResponseEntity.ok().body(postCategoryService.selectAllPostCategory(pageRequest));
	}

}
