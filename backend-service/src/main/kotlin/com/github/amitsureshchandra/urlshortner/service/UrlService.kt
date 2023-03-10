package com.github.amitsureshchandra.urlshortner.service

import com.github.amitsureshchandra.urlshortner.repo.UrlRepo
import com.github.amitsureshchandra.urlshortner.dto.UrlCreateDto
import com.github.amitsureshchandra.urlshortner.dto.UserUrl
import com.github.amitsureshchandra.urlshortner.entity.UrlMap
import com.github.amitsureshchandra.urlshortner.entity.User
import com.github.amitsureshchandra.urlshortner.repo.UserRepo
import com.github.amitsureshchandra.urlshortner.service.util.AuthUtil
import com.github.amitsureshchandra.urlshortner.service.util.UrlUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.*

@Service
class UrlService(val urlRepo: UrlRepo, val authUtil: AuthUtil, val userRepo: UserRepo, val urlUtil: UrlUtil, @Value("\${short-url-length}") var shortUrlLength: Int) {

    fun saveUrl(dto: UrlCreateDto): UrlMap {
        return saveUrlDB(dto)
    }

    fun saveUrlDB(dto: UrlCreateDto): UrlMap {
        val authUser: User = authUtil.getAuthUser()
        val shortUrl = if(dto.shortUrl == "") urlUtil.createShortUrl(shortUrlLength) else dto.shortUrl
        val urlMap = UrlMap(shortUrl, dto.url, authUser)
        authUser.getUrls().add(urlMap)
        userRepo.save(authUser)
        return urlMap
    }

    @Override
    fun getAllUrls(): List<UserUrl> {
        return urlRepo.findAllShortUrlAndFullUrl();
    }

    fun containsKey(shortUrl: String): Boolean {
        return urlRepo.existsByShortUrl(shortUrl);
    }

    @Cacheable(value = ["urls"], key = "#shortUrl")
    fun getLongUrl(shortUrl: String): String {
        return urlRepo.findLongUrlByShortUrl(shortUrl);
    }
}