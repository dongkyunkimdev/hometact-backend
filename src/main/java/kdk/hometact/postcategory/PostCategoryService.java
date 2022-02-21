package kdk.hometact.postcategory;

import java.util.List;
import java.util.stream.Collectors;
import kdk.hometact.postcategory.dto.PostCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCategoryService {

	private final PostCategoryRepository postCategoryRepository;

	@Transactional(readOnly = true)
	public List<PostCategoryDto> selectAllPostCategory(PageRequest pageRequest) {
		return postCategoryRepository.findAll(pageRequest).getContent().stream()
			.map(postCategory -> PostCategoryDto.from(postCategory))
			.collect(Collectors.toList());
	}
}
