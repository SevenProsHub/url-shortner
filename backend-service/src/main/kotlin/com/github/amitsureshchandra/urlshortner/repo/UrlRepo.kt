package com.github.amitsureshchandra.urlshortner.repo

import com.github.amitsureshchandra.urlshortner.dto.UserUrl
import com.github.amitsureshchandra.urlshortner.entity.UrlMap
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface UrlRepo : CrudRepository<UrlMap, UUID>{
//    @Query("SELECT new com.github.amitsureshchandra.urlshortner.dto.UserUrl(u.shortUrl, u.fullUrl) FROM UrlMap u WHERE u.user.email = :userEmail")
//    fun findAllByUserEmail(userEmail: String): List<UserUrl>

    @Query("SELECT new com.github.amitsureshchandra.urlshortner.dto.UserUrl(u.shortUrl, u.fullUrl, count(uhit.id)) FROM UrlMap u JOIN UrlHit uhit on uhit.shortUrl = u.shortUrl GROUP BY u.shortUrl")
    fun findAllShortUrlAndFullUrl(): List<UserUrl>

    @Query("SELECT new com.github.amitsureshchandra.urlshortner.dto.UserUrl(u.shortUrl, u.fullUrl, (SELECT count(uhit) FROM UrlHit uhit WHERE uhit.shortUrl = u.shortUrl)) FROM UrlMap u WHERE u.user.email = :authEmail")
    fun findAllUserShortUrlAndFullUrl(authEmail: String): List<UserUrl>

    fun existsByShortUrl(shortUrl: String): Boolean

    @Query("SELECT u.fullUrl FROM UrlMap u WHERE u.shortUrl = :shortUrl")
    fun findLongUrlByShortUrl(shortUrl: String): String

    fun deleteByShortUrl(uShort: String)
    fun findByShortUrl(shortUrl: String): UrlMap?
}