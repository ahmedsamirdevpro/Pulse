package com.ahmedsamir.pulse.core.common

object Constants {
    const val USERS_COLLECTION = "users"
    const val POSTS_COLLECTION = "posts"
    const val COMMENTS_COLLECTION = "comments"
    const val NOTIFICATIONS_COLLECTION = "notifications"
    const val FOLLOWS_COLLECTION = "follows"
    const val LIKES_COLLECTION = "likes"

    const val POSTS_PAGE_SIZE = 20
    const val COMMENTS_PAGE_SIZE = 30
    const val USERS_PAGE_SIZE = 20

    const val DATASTORE_NAME = "pulse_preferences"
    const val KEY_USER_ID = "user_id"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_THEME = "theme"

    const val SYNC_WORK_NAME = "pulse_sync_work"
    const val SYNC_INTERVAL_HOURS = 1L

    const val MAX_POST_LENGTH = 280
    const val MAX_BIO_LENGTH = 160
    const val MAX_USERNAME_LENGTH = 30
}