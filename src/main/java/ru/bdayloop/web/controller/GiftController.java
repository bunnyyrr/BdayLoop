package ru.bdayloop.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.bdayloop.service.i.GiftService;
import ru.bdayloop.web.AuthUser;
import ru.bdayloop.web.dto.request.CreateGiftRequest;
import ru.bdayloop.web.dto.response.GiftResponse;

import java.util.List;

@RestController
@RequestMapping("/gifts")
public class GiftController {
    private final GiftService giftService;

    public GiftController(GiftService giftService) {
        this.giftService = giftService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GiftResponse create(@RequestBody CreateGiftRequest body, AuthUser user){
        return GiftResponse.from(giftService.create(user.id(), body.title()));
    }

    @GetMapping
    public List<GiftResponse> findByUserId(@RequestParam int userId){
        return giftService.findByUserId(userId).stream().map(g -> GiftResponse.from(g)).toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id, AuthUser user){
        giftService.delete(id, user.id());
    }
}
