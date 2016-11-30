package io.pivotal.quote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class QuoteController {

    private QuoteService quoteService;

    @Autowired
    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }


    @RequestMapping("/random-quote")
    public String getView(Model model) {
        model.addAttribute("quote", quoteService.getQuote());
        model.addAttribute("uri", quoteService.getQuoteServiceURL());
        return "quote";
    }
}
