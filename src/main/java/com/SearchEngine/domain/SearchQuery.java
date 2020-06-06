package com.SearchEngine.domain;

import javax.persistence.*;
import java.net.URL;

@Entity
@Table(name="rankedurls1")
public class SearchQuery {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private URL URLs;
	@Column
	private int Rank;
	@Column
	private String description;
	@Column
	private String title;
	@Column
	private int id;
	@Column
	private String searchquery;
	@Column
	private int image;

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}

	public String getSearchQuery() {
		return searchquery;
	}

	@Override
	public String toString() {
		return "SearchQuery{" +
				"URLs=" + URLs +
				", Rank=" + Rank +
				", description='" + description + '\'' +
				", title='" + title + '\'' +
				", id=" + id +
				'}';
	}
	public void setSearchQuery(String searchQuery) {
		this.searchquery = searchQuery;
	}


	public URL getURLs() {
		return URLs;
	}

	public void setURLs(URL URLs) {
		this.URLs = URLs;
	}

	public int getRank() {
		return Rank;
	}

	public void setRank(int rank) {
		Rank = rank;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
