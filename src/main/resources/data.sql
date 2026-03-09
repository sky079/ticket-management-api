INSERT INTO sla_policies (priority, max_hours)
SELECT 'HIGH', 4
WHERE NOT EXISTS (SELECT 1 FROM sla_policies WHERE priority = 'HIGH');

INSERT INTO sla_policies (priority, max_hours)
SELECT 'MEDIUM', 24
WHERE NOT EXISTS (SELECT 1 FROM sla_policies WHERE priority = 'MEDIUM');

INSERT INTO sla_policies (priority, max_hours)
SELECT 'LOW', 72
WHERE NOT EXISTS (SELECT 1 FROM sla_policies WHERE priority = 'LOW');