# Lab 04-05 Report: Student Management System

## 1. Overview

This lab implements a Student Management System with Java 17, Jakarta Servlet 6, JSP/JSTL, MySQL 8, Maven, and Apache Tomcat 10+. The submission is divided into three independent Maven WAR projects:

- `ex1`: complete CRUD, search, server-side validation, and a dedicated delete-confirmation page.
- `ex2`: all Exercise 1 features plus database pagination and safe sorting.
- `ex3`: all Exercise 2 features plus CSV export, a statistics dashboard, and transactional bulk deletion.

The shared database script is `student_management.sql`.

## 2. Architecture

The application follows the MVC pattern:

- Model: `Student` is a serializable JavaBean with private fields, a no-argument constructor, and getters/setters.
- DAO: `StudentDAO` contains all JDBC operations and maps database rows to model objects.
- Controller: `StudentController` validates request data, calls the DAO, selects a view, and applies PRG after successful POST requests.
- View: JSP pages under `WEB-INF/views` render request data with Jakarta JSTL Core and Expression Language.

No JSP contains Java scriptlets. The servlets do not generate HTML and do not store request-specific data in instance fields.

## 3. Environment and Setup

### Requirements

- JDK 17 or newer
- Apache Tomcat 10.1+
- MySQL 8
- Maven 3.9+
- Visual Studio Code with the Extension Pack for Java

### Database

1. Run `student_management.sql` in MySQL 8.
2. Set the `DB_PASSWORD` environment variable to the MySQL root password.
3. The DAO connects to `student_management` on `localhost:3306` with user `root`.

Example for a temporary PowerShell session:

```powershell
$env:DB_PASSWORD = "your_mysql_password"
```

Tomcat must inherit this environment variable. Do not commit a real password to source control.

### Build

Run the following command separately inside `ex1`, `ex2`, and `ex3`:

```text
mvn clean package
```

The generated WAR files are:

- `ex1/target/student-mvc-ex1.war`
- `ex2/target/student-mvc-ex2.war`
- `ex3/target/student-mvc-ex3.war`

Deploy them to Tomcat and open:

- `http://localhost:8080/student-mvc-ex1/student`
- `http://localhost:8080/student-mvc-ex2/student`
- `http://localhost:8080/student-mvc-ex3/student`

## 4. Exercise 1

Exercise 1 provides list, detail, insert, update, and delete operations. Search uses a prepared statement with two `LIKE` parameters for student code and full name. Insert and update validate required fields, column lengths, the student-code pattern, email format, valid major values, duplicate student codes, and duplicate emails.

Deletion is a two-step flow. A GET request displays the selected student on a confirmation page, while the actual delete operation is sent by POST. After a successful write, the controller redirects to the list page.

### Why validate on the server if client-side validation exists?

Client-side validation improves usability but cannot be trusted. A user can disable browser validation or submit a request directly with tools such as curl or Postman. Server-side validation protects the database regardless of the client.

### What is PRG and why is it used?

Post-Redirect-Get means that a successful POST returns a redirect to a GET page. Refreshing the result page repeats only the GET request, so it does not insert, update, or delete the same record again.

### How does PreparedStatement prevent SQL injection in search?

The SQL structure is compiled separately from parameter values. Search text is bound with `setString`, so characters in the keyword are treated as data rather than executable SQL syntax.

## 5. Exercise 2

Exercise 2 displays five students per page. The DAO uses `LIMIT` and `OFFSET`, while a separate `COUNT(*)` query calculates the number of pages. Search conditions are applied consistently to both queries.

The public sort keys are mapped to a fixed allowlist of database columns. Only `asc` and `desc` are accepted as directions. Search, sort, direction, and page parameters are preserved in generated links.

### Why use LIMIT/OFFSET instead of loading every row?

Database pagination transfers and stores only the requested page. Loading all records wastes database bandwidth, server memory, and CPU time, especially when the table grows to hundreds of thousands of rows.

### How is SQL injection prevented for a sort column?

SQL identifiers cannot be bound with a normal placeholder. The controller and DAO therefore map a small set of accepted public keys to known column names. Unrecognized input falls back to `student_code` and is never concatenated into SQL.

### What happens for page -1, page 999, or non-numeric input?

Invalid or non-positive values fall back to page 1. Values greater than the final page are clamped to the final page. An empty result still has a stable page state and does not generate a negative offset.

## 6. Exercise 3

### CSV export

The export endpoint returns all students rather than the current page. It sets `Content-Type: text/csv;charset=UTF-8` and `Content-Disposition: attachment; filename=students.csv`. A UTF-8 BOM helps spreadsheet programs recognize the encoding. Fields containing commas, quotes, or line breaks are quoted, and embedded quotes are doubled.

For 100,000 records, production code should stream database rows directly to the response with an appropriate fetch size instead of first building a complete list in memory. The current lab implementation already writes rows incrementally after retrieval, but its DAO returns a list to stay consistent with the lab architecture.

### Statistics dashboard

The dashboard shows the total number of students, counts grouped by major, and students added per month during the last six months. Chart.js 4.5.1 is stored locally at `ex3/src/main/webapp/js/chart.js`; the application does not depend on a CDN at runtime.

### Bulk deletion and transactions

The user selects rows, reviews a server-generated confirmation page, and submits the confirmed IDs by POST. The server parses positive integer IDs, removes duplicates, and reads the matching records again. The DAO performs deletion in a transaction and rolls back when the number of deleted rows differs from the confirmed selection.

A transaction is necessary because bulk deletion is one logical operation. Without it, an error in the middle could leave only part of the selection deleted.

An attacker could submit arbitrary IDs, including every ID in the table. This lab reduces accidental or malformed requests with POST, two-step confirmation, ID validation, and an exact-count transaction check. A production system must additionally require authentication, authorization, CSRF protection, audit logging, and a safe maximum batch size.

## 7. Thread Safety and Resource Management

Tomcat uses one servlet instance for many concurrent requests. Request parameters, students, errors, and result lists are local variables or request attributes. The only servlet field is a stateless DAO reference initialized once.

Every JDBC connection, prepared statement, and result set is enclosed in try-with-resources. Bulk deletion explicitly commits or rolls back and then restores auto-commit.

## 8. UI and CSS

The interface uses BEM-style classes such as `student-list__toolbar`, `data-table__cell`, and `button--danger`. Reusable CSS variables define colors, spacing, radius, and shadows. Tables scroll horizontally on small screens, and forms and dashboards collapse to one column.

## 9. Verification Results

All three Maven projects compile and package successfully with Java release 17:

- Exercise 1: BUILD SUCCESS
- Exercise 2: BUILD SUCCESS
- Exercise 3: BUILD SUCCESS

Runtime database tests and screenshots require a running MySQL 8 and Tomcat 10 installation with `DB_PASSWORD` configured.

## 10. Screenshot Checklist

Add the following screenshots after runtime verification:

1. Exercise 1 search results.
2. Exercise 1 validation errors.
3. Exercise 2 pagination.
4. Exercise 2 ascending and descending sorting.
5. Exercise 3 CSV download.
6. Exercise 3 statistics dashboard.
7. Exercise 3 bulk-delete confirmation and success message.
