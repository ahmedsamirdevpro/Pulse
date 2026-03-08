package com.ahmedsamir.pulse.feature.feed.domain.usecase

import com.ahmedsamir.pulse.core.model.Post
import javax.inject.Inject

class RankFeedUseCase @Inject constructor() {

    operator fun invoke(posts: List<Post>): List<Post> {
        return posts.sortedByDescending { calculateScore(it) }
    }

    private fun calculateScore(post: Post): Double {
        val likeWeight = 3.0
        val commentWeight = 2.0
        val repostWeight = 4.0
        val recencyWeight = 1.0

        val ageInHours = (System.currentTimeMillis() - post.createdAt) / 3_600_000.0
        val recencyScore = if (ageInHours < 1) 100.0
        else if (ageInHours < 24) 50.0 / ageInHours
        else if (ageInHours < 168) 10.0 / ageInHours
        else 0.0

        return post.likesCount * likeWeight +
                post.commentsCount * commentWeight +
                post.repostsCount * repostWeight +
                recencyScore * recencyWeight
    }
}