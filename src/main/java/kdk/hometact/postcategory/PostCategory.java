package kdk.hometact.postcategory;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import kdk.hometact.post.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCategory {

	@Id
	@Column(name = "post_category_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long postCategoryId;

	private String categoryName;

	@OneToMany(mappedBy = "postCategory")
	List<Post> posts = new ArrayList<>();

	@Builder
	public PostCategory(Long postCategoryId, String categoryName,
		List<Post> posts) {
		this.postCategoryId = postCategoryId;
		this.categoryName = categoryName;
		this.posts = posts;
	}
}
