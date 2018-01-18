package com.netent.application.bestgameever.boundary;

import com.netent.application.bestgameever.control.GameService;
import com.netent.application.bestgameever.control.LoginService;
import com.netent.application.bestgameever.dto.ResultPage;
import com.netent.application.bestgameever.entity.RoundResult;
import com.netent.application.bestgameever.entity.User;
import com.netent.application.bestgameever.exception.UserAlreadyExistsException;
import com.netent.application.bestgameever.repo.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

import java.util.Map;

@Controller
@RequestMapping("game")
public class UIController {

    private final LoginService loginService;
    private final GameService gameService;

    @Autowired
    public UIController(LoginService loginService, final GameService gameService) {
        this.loginService = loginService;
        this.gameService = gameService;
    }

    private User user;

    @GetMapping({"","/"})
    public String welcome(Map<String, Object> model) {
        model.put("user", this.user);
        return "index";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") final String username, Map<String, Object> model) throws UserAlreadyExistsException {
        System.out.println("login");
        this.user = loginService.login(username, true);
        model.put("user", this.user);
        return "index";
    }


    @GetMapping("/subscribe")
    public Flux<ResultPage> subscribe() {
        if(this.user == null) {
            return Flux.empty();
        }
        return this.gameService.subscribeToResults(user.getUsername(), null);
    }
}
