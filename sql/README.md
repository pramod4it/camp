# SQL Scripts

SQL scripts are organized by the database owned by each microservice.

| Database | Service | Scripts |
| --- | --- | --- |
| `userdb` | User Service | `userdb/01_schema.sql`, `userdb/02_seed.sql` |
| `inventorydb` | Inventory Service | `inventorydb/01_schema.sql`, `inventorydb/02_seed.sql` |
| `orderdb` | Order Service | `orderdb/01_schema.sql`, `orderdb/02_seed.sql` |
| `paymentdb` | Payment Service | `paymentdb/01_schema.sql`, `paymentdb/02_seed.sql` |
| `notificationdb` | Notification Service | `notificationdb/01_schema.sql`, `notificationdb/02_seed.sql` |

The application currently uses `spring.jpa.hibernate.ddl-auto=update`, so these scripts are mainly for training, manual database setup, and future migration-tool adoption.

Recommended production next step: move these scripts into Flyway or Liquibase migrations per service.
