package io.pivotal.greeting;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class GreetingController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private EurekaClient discoveryClient;
    private RestTemplate restTemplate;

    @Autowired
    public GreetingController(EurekaClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = new RestTemplate();
    }


    @RequestMapping("/")
    public String getGreeting(Model model) {
        logger.debug("Adding greeting");
        model.addAttribute("msg", "Greetings!!!");

        String fortune = restTemplate.getForObject(fetchFortuneServiceUrl(), String.class);

        logger.debug("Adding fortune");
        model.addAttribute("fortune", fortune);

        //resolves to the greeting.vm velocity template
        return "greeting";
    }


    private String fetchFortuneServiceUrl() {
        InstanceInfo instance = discoveryClient.getNextServerFromEureka("FORTUNE-SERVICE", false);
        logger.debug("instanceID: {}", instance.getId());

        String fortuneServiceUrl = instance.getHomePageUrl();
        logger.debug("fortune service homePageUrl: {}", fortuneServiceUrl);

        return fortuneServiceUrl;
    }
}
