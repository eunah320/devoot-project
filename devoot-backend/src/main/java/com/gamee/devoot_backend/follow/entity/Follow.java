package com.gamee.devoot_backend.follow.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.gamee.devoot_backend.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "follow")
public class Follow {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "followerId", insertable = false, updatable = false)
	private User followerUser;
	@Column(name = "followerId", nullable = false)
	private long followerId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "followedId", insertable = false, updatable = false)
	private User followedUser;
	@Column(name = "followedId", nullable = false)
	private long followedId;
	@Column(name = "allowed", nullable = false)
	private boolean allowed;

	public boolean getAllowed() {
		return this.allowed;
	}
}
