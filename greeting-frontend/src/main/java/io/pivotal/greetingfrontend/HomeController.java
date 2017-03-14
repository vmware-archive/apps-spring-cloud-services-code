package io.pivotal.greetingfrontend;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static java.lang.String.format;

@Controller
public class HomeController {

    private final EurekaClient discoveryClient;

    public HomeController(EurekaClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }


    @GetMapping("/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("index");

        Map<String, Object> model = modelAndView.getModel();
        model.put("apiServerUrl", format("%s/fortune", getGatewayUrl()));

        return modelAndView;
    }


    private String getGatewayUrl() {
        return discoveryClient
            .getNextServerFromEureka("gateway-application", false)
            .getHomePageUrl();
    }
}
