package com.github.amitsureshchandra.urlshortner.service

import com.github.amitsureshchandra.urlshortner.repo.UrlRepo
import com.github.amitsureshchandra.urlshortner.dto.request.UrlCreateDto
import com.github.amitsureshchandra.urlshortner.dto.UserUrl
import com.github.amitsureshchandra.urlshortner.entity.UrlHit
import com.github.amitsureshchandra.urlshortner.entity.UrlMap
import com.github.amitsureshchandra.urlshortner.entity.User
import com.github.amitsureshchandra.urlshortner.exception.UnAuthException
import com.github.amitsureshchandra.urlshortner.exception.ValidationException
import com.github.amitsureshchandra.urlshortner.repo.UrlHitRepo
import com.github.amitsureshchandra.urlshortner.repo.UserRepo
import com.github.amitsureshchandra.urlshortner.utils.AuthUtil
import com.github.amitsureshchandra.urlshortner.utils.UrlUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional

@Service
class UrlService(val urlRepo: UrlRepo, val authUtil: AuthUtil, val userRepo: UserRepo, val urlUtil: UrlUtil, @Value("\${short-url-length}") var shortUrlLength: Int, val urlHitRepo: UrlHitRepo) {

    fun saveUrl(dto: UrlCreateDto): UrlMap {
        if(dto.shortUrl != null && urlRepo.existsByShortUrl(dto.shortUrl)) throw ValidationException("already exists with given short url");
        return saveUrlDB(dto)
    }

    fun saveUrlDB(dto: UrlCreateDto): UrlMap {
        val authUser: User = authUtil.getAuthUser()
        val shortUrl = dto.shortUrl ?: urlUtil.createShortUrl(shortUrlLength)
        val urlMap = UrlMap(shortUrl, dto.url!!, authUser)
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

//    @Cacheable(value = ["urls"], key = "#shortUrl")
    fun getLongUrl(shortUrl: String): String {
        return urlRepo.findLongUrlByShortUrl(shortUrl);
    }

    fun getAllUserUrls(): List<UserUrl>? {
        return urlRepo.findAllUserShortUrlAndFullUrl(authUtil.getAuthEmail());
    }

    @Transactional
    fun deleteUrl(shortUrl: String) {
        val urlMap = urlRepo.findByShortUrl(shortUrl) ?: throw ValidationException("Not Found");
        if(urlMap.user.email != authUtil.getAuthEmail()) throw UnAuthException("Unauthorized");
        urlRepo.deleteByShortUrl(shortUrl);
    }

    fun storeAnalytics(url: String, request: HttpServletRequest) {
        val ip = request.remoteAddr
        val urlHit = UrlHit(ip, url, request.getHeader("User-Agent"))
        urlHitRepo.save(urlHit)
    }
}