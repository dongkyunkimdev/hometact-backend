package kdk.hometact.postlike.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import kdk.hometact.post.dto.PostDto;
import kdk.hometact.postlike.PostLike;
import kdk.hometact.user.dto.UserDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLikeDto {

	@JsonProperty(access = Access.READ_ONLY)
	private Long postLikeId;

	@JsonProperty(access = Access.READ_ONLY)
	private UserDto userDto;

	@Builder
	public PostLikeDto(Long postLikeId, UserDto userDto) {
		this.postLikeId = postLikeId;
		this.userDto = userDto;
	}

	public static PostLikeDto from(PostLike postLike) {
		return PostLikeDto.builder()
			.postLikeId(postLike.getPostLikeId())
			.userDto(UserDto.from(postLike.getUser()))
			.build();
	}
}
