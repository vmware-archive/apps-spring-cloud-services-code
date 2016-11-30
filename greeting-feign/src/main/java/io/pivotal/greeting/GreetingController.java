package io.pivotal.greeting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GreetingController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private FortuneServiceClient fortuneServiceClient;

    @Autowired
    public GreetingController(FortuneServiceClient fortuneServiceClient) {
        this.fortuneServiceClient = fortuneServiceClient;
    }


    @RequestMapping("/")
    public String getGreeting(Model model) {
        logger.debug("Adding greeting");
        model.addAttribute("msg", "Greetings!!!");

        String fortune = fortuneServiceClient.getFortune();

        logger.debug("Adding fortune");
        model.addAttribute("fortune", fortune);

        //resolves to the greeting.vm velocity template
        return "greeting";
    }
}
