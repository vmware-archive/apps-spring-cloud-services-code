package io.pivotal.fortune;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FortuneController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private FortuneService fortuneService;

    @Autowired
    public FortuneController(FortuneService fortuneService) {
        this.fortuneService = fortuneService;
    }


    @RequestMapping("/")
    public String getQuote() {
        logger.debug("fetching fortune.");
        return fortuneService.getFortune();
    }
}
