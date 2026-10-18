package ru.bdayloop.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.bdayloop.model.Group;
import ru.bdayloop.service.i.GroupService;
import ru.bdayloop.web.AuthUser;
import ru.bdayloop.web.dto.request.CreateGroupRequest;
import ru.bdayloop.web.dto.request.UpdateGroupRequest;
import ru.bdayloop.web.dto.response.GroupResponse;
import ru.bdayloop.web.dto.response.UserResponse;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse create(@RequestBody CreateGroupRequest body, AuthUser user) {
        return GroupResponse.from(groupService.create(body.name(), user.id()));
    }

    @GetMapping
    public List<GroupResponse> find(@RequestParam(required = false) String name, @RequestParam(required = false) Integer memberId) {
        List<Group> groups;
        if (memberId != null) {
            groups = groupService.findByMember(memberId);
        } else if (name != null) {
            groups = groupService.findByName(name);
        } else {
            groups = groupService.findAll();
        }
        return groups.stream().map(g -> GroupResponse.from(g)).toList();
    }

    @GetMapping(params = "subscribed=true")
    public List<GroupResponse> mySubscriptions(AuthUser user) {
        return groupService.findBySubscriber(user.id()).stream().map(g -> GroupResponse.from(g)).toList();
    }

    @GetMapping("/{id}")
    public GroupResponse get(@PathVariable int id) {
        return GroupResponse.from(groupService.findById(id));
    }

    @GetMapping("/{id}/members")
    public List<UserResponse> members(@PathVariable int id) {
        return groupService.groupMembers(id).stream().map(u -> UserResponse.from(u)).toList();
    }

    @PutMapping("/{id}")
    public GroupResponse update(@PathVariable int id, @RequestBody UpdateGroupRequest body, AuthUser user) {
        return GroupResponse.from(groupService.update(id, body.name(), user.id(), user.role()));
    }

    @PostMapping("/{id}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void join(@PathVariable int id, AuthUser user) {
        groupService.joinGroup(user.id(), id);
    }

    @PostMapping("/{id}/subscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void subscribe(@PathVariable int id, AuthUser user) {
        groupService.subscribeToGroup(user.id(), id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id, AuthUser user) {
        groupService.delete(id, user.id(), user.role());
    }

    @DeleteMapping("/{id}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leave(@PathVariable int id, AuthUser user) {
        groupService.leaveGroup(id, user.id());
    }

    @DeleteMapping("/{id}/subscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable int id, AuthUser user) {
        groupService.unsubscribeFromGroup(user.id(), id);
    }
}
