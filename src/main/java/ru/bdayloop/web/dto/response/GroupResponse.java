package ru.bdayloop.web.dto.response;

import ru.bdayloop.model.Group;

public record GroupResponse(int id, String name, Integer createdBy) {
    public static GroupResponse from(Group group){
        Integer createdBy = group.getCreatedBy() == null ? null : group.getCreatedBy().getId();
        return new GroupResponse(group.getId(), group.getName(), createdBy);
    }
}
