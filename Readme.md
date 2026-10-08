# Webstore Order Management System

## 1. Project Overview

This project is a REST API backend for a webstore order management system, developed as part of the Database Solutions course.

The application manages products, product categories, suppliers, customers, and orders. It uses Spring Boot and Spring Data JPA to connect Java entities to a MariaDB relational database.

The project demonstrates CRUD operations, entity relationships, order processing, transactions, JPQL bulk operations, dynamic queries, and database management features.

## 2. Technologies Used

- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- MariaDB
- Maven
- IntelliJ IDEA
- REST API / JSON

## 3. Project Setup

### Requirements

- Java 21
- Maven
- MariaDB
- IntelliJ IDEA or another Java IDE

### Database Configuration

Create the MariaDB database named `project`.

Configure the database connection in `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: demo
  datasource:
    url: jdbc:mariadb://localhost:3306/project
    username: YOUR_DATABASE_USER
    password: YOUR_DATABASE_PASSWORD
  jpa:
    hibernate:
      ddl-auto: update
```

The application uses port `8080`.

### Running the Application

1. Open the project in IntelliJ IDEA.
2. Ensure MariaDB is running.
3. Configure the database connection.
4. Run `DemoApplication.java`.
5. Test the REST endpoints using IntelliJ HTTP Client or Postman.

Base URL:

`http://localhost:8080`

## 4. Database Schema

The database includes the following main tables:

| Table | Description |
|---|---|
| products | Product information, prices, stock quantities, and categories |
| product_categories | Product category information |
| suppliers | Supplier details |
| product_suppliers | Many-to-many relationship between products and suppliers |
| customers | Customer information |
| contacts | Additional customer contact information |
| orders | Customer orders and order statuses |
| orderitems | Products and quantities belonging to orders |
| order_status_log | History of order status changes |
| SupplierContactHistory | Historical supplier contact information |

### Entity Relationships

- One category can contain multiple products.
- Products and suppliers have a many-to-many relationship.
- Customers and contacts have a one-to-one relationship.
- Company customers extend the customer entity using JPA inheritance.
- One customer can place multiple orders.
- Each order can contain multiple products through order items.

## 5. REST API Endpoints

### Products

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/products` | Retrieve all products |
| GET | `/products/{id}` | Retrieve a product by ID |
| POST | `/products` | Create a product |
| PUT | `/products/{id}` | Update product information |
| DELETE | `/products/{id}` | Delete a product |
| POST | `/products/{productId}/supplier/{supplierId}` | Associate a supplier with a product |
| DELETE | `/products/{productId}/supplier/{supplierId}` | Remove a supplier association |
| PUT | `/products/increase-prices` | Increase prices in a category using JPQL |
| GET | `/products/search` | Search products by minimum price and stock |

**Create product**

```http
POST /products
Content-Type: application/json

{
  "name": "Wireless Mouse",
  "description": "Wireless computer mouse",
  "price": 25.00,
  "stockQuantity": 50
}
```

**Update product prices**

```http
PUT /products/increase-prices?categoryId=1&percentage=10
```

**Search products**

```http
GET /products/search?minPrice=50&minStock=10
```

### Product Categories

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/product-category` | Retrieve all categories |
| GET | `/product-category/{id}` | Retrieve a category |
| POST | `/product-category` | Create a category |
| PUT | `/product-category/{id}` | Update a category |
| DELETE | `/product-category/delete/{id}` | Delete a category |

**Create category**

```http
POST /product-category
Content-Type: application/json

{
  "name": "Electronics",
  "description": "Electronic products",
  "products": []
}
```

### Suppliers

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/suppliers` | Retrieve all suppliers |
| GET | `/suppliers/{id}` | Retrieve a supplier |
| POST | `/suppliers` | Create a supplier |
| PUT | `/suppliers/{id}` | Update a supplier |
| DELETE | `/suppliers/{id}` | Delete a supplier |

**Create supplier**

```http
POST /suppliers
Content-Type: application/json

{
  "name": "Tech Supplier",
  "contactName": "John Smith",
  "phone": "0401234567",
  "email": "supplier@example.com"
}
```

### Customers

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/customers` | Retrieve all customers |
| GET | `/customers/{id}` | Retrieve a customer |
| POST | `/customers` | Create a customer |
| PUT | `/customers/{id}` | Update a customer |
| DELETE | `/customers/{id}` | Delete a customer |

**Create customer**

```http
POST /customers
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane@example.com",
  "phone": "0401234567"
}
```

### Company Customers

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/company-customers` | Retrieve all company customers |
| GET | `/company-customers/{id}` | Retrieve a company customer |
| POST | `/company-customers` | Create a company customer |
| PUT | `/company-customers/{id}` | Update a company customer |
| DELETE | `/company-customers/{id}` | Delete a company customer |

Company customers use JPA single-table inheritance and contain additional company information.

### Orders

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/orders` | Retrieve all orders |
| GET | `/orders/{id}` | Retrieve an order |
| POST | `/orders` | Create an order and reduce stock |
| GET | `/orders/{id}/items` | Retrieve the items in an order |
| PUT | `/orders/{id}/status` | Update order status |
| PUT | `/orders/{id}/cancel` | Cancel an order and restore stock |

**Create order**

```http
POST /orders
Content-Type: application/json

{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

The application validates stock availability, creates the order, stores its items, and reduces product stock within a transaction.

**Update order status**

```http
PUT /orders/123/status
Content-Type: application/json

{
  "status": "PROCESSING"
}
```

**Cancel order**

```http
PUT /orders/123/cancel
```

Cancellation restores the stock quantities associated with the order.

## 6. Database Features

### Database Views

A view named `product_catalogue` combines product, category, and supplier information.

It simplifies queries that would otherwise require joins between multiple tables.

Example:

```sql
SELECT * FROM product_catalogue;
```

### Database Triggers

The `log_order_status_change` trigger automatically records changes to an order's status in the `order_status_log` table.

The trigger runs after an update to the `orders` table when the status has changed.

Example:

```sql
SELECT * FROM order_status_log;
```

### Scheduled Events

The database contains a scheduled event named `delete_old_contacts`.

It runs daily and removes contacts older than two years.

### Indexing

Indexes were created to improve query performance.

Examples include:

- `idx_customer_firstname` on `customers.first_name`
- `idx_customer_lastname` on `customers.last_name`
- `idx_product_category_id` on `products.category_id`
- `idx_order_items_order_id` on `orderitems.order_id`

Indexes help the database locate matching records without scanning every row.

### Transactions

Spring's `@Transactional` annotation is used in the order service.

When an order is placed, the application performs several related operations:

1. Creates an order.
2. Adds order items.
3. Reduces product stock.

If one operation fails, the transaction rolls back to prevent partially completed orders.

Order cancellation also uses a transaction to restore stock and update order status.

### Concurrency Control

Pessimistic locking is used when retrieving products for stock updates.

The repository uses `PESSIMISTIC_WRITE` to lock a product row during order processing.

This helps prevent simultaneous orders from purchasing more stock than is available.

### Temporal Data

The `SupplierContactHistory` table stores historical supplier contact changes.

The `order_status_log` table stores previous and new order statuses together with the time of each change.

These tables support auditing and historical data retrieval.

### Database Security

Database privileges are separated between administrative and application users.

The `app_user` account has `SELECT`, `INSERT`, and `UPDATE` privileges on the `project` database but does not have permission to change its schema.

A database backup and restoration plan was also developed.

## 7. Additional JPA Features

### JPQL Bulk Operations

A JPQL bulk update changes the prices of all products belonging to a selected category without loading and saving each product individually.

### Criteria API

The Criteria API builds product search queries dynamically.

Search conditions can include minimum price and minimum stock quantity.

### AttributeConverter

The `ProductNameConverter` automatically converts product names to uppercase before storing them in the database.

The converter is applied to the product name field using `@Convert`.

### Entity Relationships

The application demonstrates:

- One-to-many and many-to-one relationships
- Many-to-many relationships
- One-to-one relationships
- Cascading operations
- JPA inheritance

## 8. Testing

The REST API can be tested using IntelliJ HTTP Client.

Tests include creating and retrieving products, updating categories, managing suppliers, creating orders, changing order statuses, and cancelling orders.

Database functionality was also tested directly in MariaDB using SQL queries.

The project includes examples of transaction handling, concurrency control, database views, triggers, and indexing.

## 9. Future Improvements

Possible future improvements include:

- Pagination and sorting for large datasets
- Additional API input validation
- Standardized error responses
- Automated integration tests
- API versioning
- Additional authentication and authorization for REST endpoints

