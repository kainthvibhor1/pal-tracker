package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvController {

    private Map<String, String> envVars;
    public EnvController(@Value("${port:NOT SET}") String port, @Value("${memory.limit:NOT SET}") String memoryLimit, @Value("${cf.instance.index:NOT SET}") String instanceIdx, @Value("${cf.instance.addr:NOT SET}") String instanceAddr) {
        envVars = new HashMap<>();
        envVars.put("PORT", port);
        envVars.put("MEMORY_LIMIT", memoryLimit);
        envVars.put("CF_INSTANCE_INDEX", instanceIdx);
        envVars.put("CF_INSTANCE_ADDR", instanceAddr);
    }

    @GetMapping("/env")
    public Map<String, String> getEnv() {
        return this.envVars;
    }
}
