package kdk.hometact.postcategory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import kdk.hometact.postcategory.PostCategory;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCategoryDto {

	@JsonProperty(access = Access.READ_ONLY)
	private Long postCategoryId;

	private String categoryName;

	@Builder
	public PostCategoryDto(Long postCategoryId, String categoryName) {
		this.postCategoryId = postCategoryId;
		this.categoryName = categoryName;
	}

	public static PostCategoryDto from(PostCategory postCategory) {
		return PostCategoryDto.builder()
			.postCategoryId(postCategory.getPostCategoryId())
			.categoryName(postCategory.getCategoryName())
			.build();
	}
}
