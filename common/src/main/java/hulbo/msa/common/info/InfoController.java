package hulbo.msa.common.info;

import hulbo.msa.common.util.JsonUtil;
import hulbo.msa.common.vo.Greeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/info")
public class InfoController {

    @Autowired
    private Greeting greeting;

    private final Environment env;

    public InfoController(Environment env) {
        this.env = env;
    }

    @GetMapping("/healthCheck")
    public String status() {
        // 서비스 상태 정보를 Map에 저장
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("message", "It's Working in User Service");
        statusMap.put("localServerPort", env.getProperty("local.server.port"));
        statusMap.put("serverPort", env.getProperty("server.port"));
        statusMap.put("gatewayIp", env.getProperty("gateway.ip"));
        statusMap.put("greetingMessage", env.getProperty("greeting.message"));
        statusMap.put("tokenSecret", env.getProperty("token.secret"));
        statusMap.put("tokenExpirationTime", env.getProperty("token.expiration_time"));
        statusMap.put("repo:location", env.getProperty("hulbo.location"));
        statusMap.put("repo:version", env.getProperty("hulbo.version"));
        statusMap.put("repo:message", env.getProperty("hulbo.message"));
        statusMap.put("spring.application.name", env.getProperty("spring.application.name"));

        // 공통 JsonUtil을 사용하여 JSON 형식으로 반환
        return JsonUtil.toJson(statusMap);
    }

    @GetMapping("/info")
    public String info(){
        return greeting.getMessage();
    }

    @GetMapping("/welcome")
    public String welcome(){
        return greeting.getMessage();
    }
}
