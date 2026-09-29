package ru.bdayloop.web.dto.response;

import ru.bdayloop.model.Group;

public record GroupResponse(int id, String name, Integer createdBy) {
    public static GroupResponse from(Group group){
        return new GroupResponse(group.getId(), group.getName(), group.getCreatedBy());
    }
}
