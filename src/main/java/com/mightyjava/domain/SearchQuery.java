package com.mightyjava.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import javax.persistence.*;
import java.net.URL;

@Entity
@Table(name="rankedurlsezza")
public class SearchQuery {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private URL URLs;
	@Column
	private int Rank;

	@Override
	public String toString() {
		return "SearchQuery{" +
				"URLs=" + URLs +
				", Rank=" + Rank +
				'}';
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
}
