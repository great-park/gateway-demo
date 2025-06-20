package shb.gpark.webtestK.restController

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/hello")
    fun healthCheck(): String {
        return "Hello from Web Server";
    }
}