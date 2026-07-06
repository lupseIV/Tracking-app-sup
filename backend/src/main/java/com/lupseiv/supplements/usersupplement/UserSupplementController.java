package com.lupseiv.supplements.usersupplement;

import com.lupseiv.supplements.usersupplement.dto.UserSupplementResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-supplements")
public class UserSupplementController {

    private final UserSupplementService userSupplementService;

    public UserSupplementController(UserSupplementService userSupplementService) {
        this.userSupplementService = userSupplementService;
    }

    @GetMapping
    public List<UserSupplementResponse> getActive() {
        return userSupplementService.getActive();
    }

    @PostMapping("/{supplementId}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserSupplementResponse activate(@PathVariable Long supplementId) {
        return userSupplementService.activate(supplementId);
    }

    @DeleteMapping("/{supplementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long supplementId) {
        userSupplementService.deactivate(supplementId);
    }
}
