package shb.gpark.userservice.config

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import shb.gpark.userservice.service.UserService

@Component
class DataInitializer(
    private val userService: UserService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        // 기본 사용자 데이터 생성
        userService.createDefaultUsers()
    }
} 