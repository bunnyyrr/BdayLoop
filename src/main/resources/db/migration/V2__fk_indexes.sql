CREATE INDEX idx_group_members_user_id ON group_members (user_id);
CREATE INDEX idx_group_subscriptions_group_id ON group_subscriptions (group_id);
CREATE INDEX idx_subscriptions_target_id ON subscriptions (target_id);
CREATE INDEX idx_gifts_user_id ON gifts (user_id);
CREATE INDEX idx_messages_subject_created ON messages (subject_user_id, created_at);
CREATE INDEX idx_messages_sender_id ON messages (sender_id);
CREATE INDEX idx_groups_created_by ON groups (created_by);