package ru.bdayloop.dao.i;

import java.sql.SQLException;

public interface SubscriptionDao {
    void subscribe(int subscriberId, int targetId) throws SQLException;
    void unsubscribe(int subscriberId, int targetId) throws SQLException;
    boolean isSubscribedDirectly(int subscriberId, int targetId) throws SQLException;
    boolean isSubscribedDirectlyOrViaGroup(int subscriberId, int targetId) throws SQLException;
}
