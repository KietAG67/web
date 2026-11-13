# Kế hoạch thực hiện Lab 04-05 - Student Management System

## 1. Phạm vi đã xác nhận

Thực hiện toàn bộ nội dung thực hành và cả ba bài tập trong tài liệu:

- Phần nền tảng: Maven Web Application, MySQL, Model, DAO, Servlet Controller, JSP/JSTL/EL, CSS và triển khai trên Tomcat.
- Exercise 1: CRUD hoàn chỉnh, tìm kiếm, validation phía server và trang xác nhận xóa.
- Exercise 2: phân trang, sắp xếp và kết hợp tìm kiếm + phân trang + sắp xếp.
- Exercise 3: xuất CSV, dashboard thống kê và xóa hàng loạt bằng transaction.
- Chuẩn bị ảnh chụp và phần giải thích được yêu cầu trong từng exercise.
- Viết báo cáo cuối cùng bằng tiếng Anh.

Lab 05 về authentication/session chỉ được PDF giới thiệu ở phần “Bridge to Lab 05”, không phải yêu cầu triển khai của bài này nên không đưa vào phạm vi code.

## 2. Nguồn yêu cầu và nguyên tắc không tự suy diễn

Thứ tự ưu tiên khi có khác biệt:

1. Yêu cầu trực tiếp của người dùng.
2. Yêu cầu chức năng trong Exercise 1-3 và checklist của PDF.
3. Code mẫu trong PDF chỉ dùng để hiểu mục tiêu, không sao chép các điểm trái với hai nguồn trên.

Các điều chỉnh có căn cứ:

- Dùng Java 17+, Tomcat 10+, MySQL 8 và Jakarta EE; không dùng `javax.*`.
- JSP chỉ dùng JSTL Core và EL; không dùng Java scriptlet và không sinh HTML trong servlet.
- Mọi thao tác insert/update/delete/bulk delete dùng POST và kết thúc bằng redirect theo PRG. Mẫu nền trong PDF xóa bằng GET sẽ không được dùng vì Exercise 1 yêu cầu POST.
- DAO chỉ dùng `PreparedStatement` và `try-with-resources`.
- Cột sắp xếp không thể bind bằng placeholder, vì vậy chỉ ánh xạ từ một allowlist cố định; không ghép trực tiếp input của người dùng vào `ORDER BY`.
- CSS được viết lại theo BEM, responsive và CSS variables; không dùng nguyên mẫu class CSS không theo BEM trong PDF.
- Mỗi exercise là một Maven project độc lập và nằm trực tiếp trong thư mục lab hiện tại.
- `DB_URL` và `DB_USER` là hằng `static final` trong DAO để bám sát code mẫu; mật khẩu đọc từ biến môi trường `DB_PASSWORD`.
- Chart.js được lưu cục bộ trong Exercise 3, không dùng CDN.

## 3. Phong cách code hiện có của người dùng sẽ được giữ

Đã quan sát các bài Web Application Development trước đó:
- Giữ cấu hình Maven bằng `maven.compiler.release` ở Java 17.
- Xử lý UTF-8 rõ ràng ở request/response.
- Dùng tên biến đầy đủ, dễ đọc; validation gom lỗi thay vì dừng ở lỗi đầu tiên.
- Dùng helper method nhỏ cho logic lặp lại như chuẩn hóa input.
- Comment ngắn, chỉ giải thích quyết định hoặc ràng buộc quan trọng.

Không tiếp tục cách servlet dùng `PrintWriter` để dựng HTML vì bài này yêu cầu View bằng JSP/JSTL/EL và Clean MVC.

## 4. Kiến trúc và cấu trúc dự kiến

Thư mục hiện tại là root duy nhất của toàn bộ bài nộp. Không tạo project ở thư mục khác và không tạo parent Maven project ngoài cấu trúc dưới đây. Ba exercise là ba WAR project độc lập; mỗi project có `pom.xml`, `src/` và có thể build/deploy riêng.

Exercise sau là phiên bản mở rộng tích lũy của exercise trước:

- `ex1`: CRUD, search, server-side validation và delete confirmation.
- `ex2`: toàn bộ `ex1` cộng pagination, sorting và phối hợp search + page + sort.
- `ex3`: toàn bộ `ex2` cộng CSV export, dashboard và bulk delete.

```text
./
├── ex1/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/university/
│       │   ├── controller/StudentController.java
│       │   ├── dao/StudentDAO.java
│       │   ├── model/Student.java
│       │   └── util/StudentValidator.java
│       └── webapp/
│           ├── WEB-INF/views/
│           │   ├── student-list.jsp
│           │   ├── student-detail.jsp
│           │   ├── student-form.jsp
│           │   └── student-delete-confirm.jsp
│           └── css/style.css
├── ex2/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/university/
│       │   ├── controller/StudentController.java
│       │   ├── dao/StudentDAO.java
│       │   ├── model/Student.java
│       │   └── util/StudentValidator.java
│       └── webapp/
│           ├── WEB-INF/views/
│           │   ├── student-list.jsp
│           │   ├── student-detail.jsp
│           │   ├── student-form.jsp
│           │   └── student-delete-confirm.jsp
│           └── css/style.css
├── ex3/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/university/
│       │   ├── controller/StudentController.java
│       │   ├── dao/StudentDAO.java
│       │   ├── model/
│       │   │   ├── Student.java
│       │   │   ├── MajorCount.java
│       │   │   └── MonthlyGrowth.java
│       │   └── util/
│       │       ├── CsvWriter.java
│       │       └── StudentValidator.java
│       └── webapp/
│           ├── WEB-INF/views/
│           │   ├── student-list.jsp
│           │   ├── student-detail.jsp
│           │   ├── student-form.jsp
│           │   ├── student-delete-confirm.jsp
│           │   ├── student-bulk-delete-confirm.jsp
│           │   └── student-dashboard.jsp
│           ├── css/style.css
│           └── js/
│               ├── chart.js
│               └── student-list.js
├── student_management.sql
└── report.md
```

Ghi chú:

- Package Java dùng `com.university` đúng cấu trúc mẫu của PDF, nhưng toàn bộ source vẫn nằm bên trong `ex1`, `ex2`, `ex3` của thư mục hiện tại.
- `MajorCount` và `MonthlyGrowth` chỉ xuất hiện ở `ex3` vì chỉ phục vụ dashboard.
- `student-list.js` chỉ cần ở `ex3` cho thao tác Select All; không thêm JavaScript không được yêu cầu.
- `student_management.sql` là file SQL dùng chung ở root và phải nhỏ hơn hoặc bằng 100 MB.

## 5. Contract dữ liệu

### 5.1 Bảng `students`

Tạo `student_management.sql` ở root đúng cấu trúc trong PDF:

- `id INT AUTO_INCREMENT PRIMARY KEY`
- `student_code VARCHAR(20) NOT NULL UNIQUE`
- `full_name VARCHAR(100) NOT NULL`
- `email VARCHAR(100) NOT NULL UNIQUE`
- `major VARCHAR(50) NOT NULL`
- `created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP`
- `updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`
- index cho `student_code` và `email`
- charset `utf8mb4`, collation `utf8mb4_unicode_ci`, engine InnoDB
- năm dòng dữ liệu mẫu đúng theo PDF

### 5.2 JavaBean `Student`

Trong từng project, tạo `src/main/java/com/university/model/Student.java`:

- Implements `Serializable` và khai báo `serialVersionUID`.
- Private fields: `id`, `studentCode`, `fullName`, `email`, `major`, `createdAt`, `updatedAt`.
- No-arg constructor, constructor tiện dụng, đầy đủ getter/setter và `toString()`.
- Không chứa SQL, validation request hoặc logic View.

## 6. Các route và quy tắc HTTP

Tất cả đi qua `@WebServlet("/student")`.

| Method | Action | Mục đích | Kết quả |
|---|---|---|---|
| GET | `list` hoặc thiếu action | Danh sách + search + sort + page | Forward `student-list.jsp` |
| GET | `view` | Xem chi tiết | Forward `student-detail.jsp` |
| GET | `new` | Hiện form thêm | Forward `student-form.jsp` |
| GET | `edit` | Hiện form sửa | Forward `student-form.jsp` |
| GET | `confirmDelete` | Hiện xác nhận xóa một sinh viên | Forward `student-delete-confirm.jsp` |
| GET | `dashboard` | Hiện thống kê | Forward `student-dashboard.jsp` |
| GET | `export`, `format=csv` | Tải toàn bộ dữ liệu CSV | Stream file CSV |
| POST | `insert` | Thêm sinh viên | PRG về danh sách khi thành công |
| POST | `update` | Sửa sinh viên | PRG về danh sách khi thành công |
| POST | `delete` | Xóa một sinh viên | PRG về danh sách |
| POST | `confirmBulkDelete` | Kiểm tra ID và hiện trang xác nhận | Forward trang xác nhận, chưa xóa |
| POST | `bulkDelete` | Xóa các ID đã xác nhận trong transaction | PRG về danh sách |

`doGet()` chỉ điều phối các action đọc/hiển thị; `doPost()` chỉ điều phối action thay đổi dữ liệu hoặc bước xác nhận POST. Không lưu request parameter, danh sách sinh viên hoặc trạng thái người dùng trong instance field của servlet. `StudentDAO` có thể khởi tạo một lần nếu bản thân DAO stateless và mỗi method tự mở/đóng connection.

## 7. Kế hoạch triển khai theo giai đoạn

### Giai đoạn 1 - Khởi tạo Maven và cấu hình

- Tạo ba WAR project độc lập trong `ex1`, `ex2`, `ex3`; mỗi project có `pom.xml` riêng.
- Dùng artifact/final name riêng: `student-mvc-ex1`, `student-mvc-ex2`, `student-mvc-ex3` để có thể deploy đồng thời mà không trùng context path.
- Mỗi `pom.xml` cấu hình Java 17, UTF-8, Servlet API 6.0, JSP API 3.1, JSTL API/implementation Jakarta 3.x và MySQL Connector/J 8.x như tài liệu.
- JSTL Core dùng URI của Jakarta JSTL 3 (`jakarta.tags.core`).
- Cấu hình `maven-war-plugin` và không bắt buộc `web.xml` vì dùng annotation.
- Trong từng `StudentDAO`, giữ `DB_URL` và `DB_USER` dưới dạng `private static final`; lấy mật khẩu bằng `System.getenv("DB_PASSWORD")` và báo lỗi rõ ràng nếu biến chưa được đặt.
- Chạy `mvn clean package` riêng trong cả ba project.

### Giai đoạn 2 - Database và Model

- Viết file dùng chung `student_management.sql` tại root, chạy trên MySQL 8 và kiểm tra năm dòng mẫu.
- Tạo `Student` JavaBean.
- Tạo các bean kết quả thống kê cần cho dashboard.
- Kiểm tra mapping đầy đủ cả `created_at` và `updated_at`.

### Giai đoạn 3 - DAO CRUD cơ bản

Trong `StudentDAO` triển khai:

- `getAllStudents()` cho export và các trường hợp cần toàn bộ dữ liệu.
- `getStudentById(int id)`.
- `insertStudent(Student student)`.
- `updateStudent(Student student)`.
- `deleteStudent(int id)`.
- Helper private duy nhất để map `ResultSet` sang `Student`.

Mỗi method:

- Dùng `PreparedStatement` kể cả truy vấn không có parameter.
- Dùng `try-with-resources` cho Connection, PreparedStatement và ResultSet.
- Không nuốt `SQLException`; chuyển lên controller dưới dạng `ServletException` có ngữ cảnh phù hợp.

### Giai đoạn 4 - MVC CRUD và các JSP nền

- Tạo `StudentController` với dispatch GET/POST rõ ràng.
- Tạo list, detail và form JSP trong `WEB-INF/views` để không truy cập trực tiếp.
- Dùng `<c:url>` tạo URL có context path, `<c:out>` cho dữ liệu người dùng, `<c:forEach>`, `<c:if>` và `<c:choose>`.
- Form thêm/sửa dùng chung view nhưng controller phải truyền mode và dữ liệu rõ ràng.
- Khi ID thiếu, không phải số, nhỏ hơn 1 hoặc không tồn tại: không để `NumberFormatException` thoát ra; trả trạng thái/luồng lỗi có kiểm soát.
- Sau insert/update/delete thành công, redirect về URL GET; flash message chỉ hiển thị một lần rồi bị xóa.

### Giai đoạn 5 - Exercise 1: search, validation, delete confirmation

#### Search

- Thêm search bar ở `student-list.jsp`.
- DAO có `searchStudents(String keyword)` tìm theo `student_code` hoặc `full_name` bằng `LIKE ?`.
- Controller trim keyword và chuyển chuỗi rỗng về luồng danh sách bình thường.
- Hiện “No results found” khi search không có kết quả.

#### Validation phía server

- Tạo `StudentValidator` trả về danh sách/map lỗi để tái sử dụng cho insert và update.
- Chuẩn hóa input bằng trim trước khi validate.
- Student code phải khớp `[A-Z]{2,6}IU\d{5}`.
- Email phải đúng định dạng email.
- Các trường bắt buộc không được rỗng và không vượt chiều dài cột database.
- Major phải thuộc đúng tập giá trị xuất hiện trong PDF: Computer Science, Information Technology, Software Engineering, Data Science.
- DAO kiểm tra trùng student code và email trước insert; khi update phải loại trừ chính ID hiện tại.
- Unique constraint của MySQL vẫn là lớp bảo vệ cuối; bắt duplicate SQL exception và trả lỗi form thay vì lỗi 500.
- Khi lỗi, forward lại form với Student đã nhập và lỗi trong request scope; không redirect để không mất lỗi.

#### Xác nhận xóa

- Link xóa chỉ mở `GET action=confirmDelete&id=...`.
- Trang xác nhận hiển thị thông tin sinh viên bằng JSTL/EL.
- Nút xác nhận gửi `POST action=delete`; nút hủy quay về danh sách.
- POST kiểm tra lại sinh viên còn tồn tại trước khi xóa, rồi PRG.

### Giai đoạn 6 - Exercise 2: pagination và sorting

- Cố định `pageSize = 5` đúng PDF.
- DAO có truy vấn dữ liệu dùng `LIMIT ? OFFSET ?` và truy vấn `COUNT(*)` tương ứng.
- Kết hợp điều kiện search với cả query dữ liệu và query đếm.
- Controller nhận `search`, `page`, `sort`, `order`; trim và chuẩn hóa.
- Map sort key công khai vào tên cột cố định:
  - `studentCode` -> `student_code`
  - `fullName` -> `full_name`
  - `email` -> `email`
  - `major` -> `major`
- Chỉ chấp nhận `asc` hoặc `desc`; mọi giá trị khác dùng mặc định an toàn.
- `page < 1` được chuẩn hóa về trang 1; `page > maxPage` được chuẩn hóa về trang cuối khi có kết quả; tập rỗng không sinh trang 0 gây lỗi.
- JSP hiển thị Previous, số trang, Next và “Page X of Y”.
- Header bấm lần nữa đảo ASC/DESC và hiển thị ▲/▼.
- Mọi link page/sort giữ đủ `search`, `sort`, `order`, `page` bằng `<c:url>` và `<c:param>`.

### Giai đoạn 7 - Exercise 3A: export CSV

- Thêm nút “Export to CSV” ở danh sách.
- `GET action=export&format=csv` xuất toàn bộ sinh viên, không chỉ trang hiện tại.
- Header phản hồi gồm content type CSV với UTF-8 và `Content-Disposition: attachment` với tên file ổn định.
- Dòng đầu đúng thứ tự PDF: `student_code,full_name,email,major`.
- Escape đúng trường chứa dấu phẩy, dấu nháy kép hoặc xuống dòng; dấu nháy kép được nhân đôi và field được bao bằng dấu nháy kép.
- Ghi trực tiếp từng dòng ra response writer; không dựng toàn bộ CSV lớn trong một String.

### Giai đoạn 8 - Exercise 3B: dashboard thống kê

DAO triển khai ba truy vấn:

- Tổng số sinh viên.
- Số sinh viên theo major bằng `GROUP BY` và `COUNT(*)`.
- Số sinh viên được thêm theo từng tháng trong sáu tháng gần nhất dựa trên `created_at`.

Controller đưa dữ liệu sang `student-dashboard.jsp`; JSP hiển thị:

- Total students.
- Biểu đồ số lượng theo major.
- Biểu đồ tăng trưởng sáu tháng.

- Tải bản Chart.js UMD về `ex3/src/main/webapp/js/chart.js` và nhúng bằng context path; không dùng CDN.
- Chart.js chỉ nhận dữ liệu đã serialize/escape an toàn; JSP không chứa Java scriptlet.

### Giai đoạn 9 - Exercise 3C: bulk delete

- Thêm checkbox từng hàng và “Select All” ở danh sách.
- JavaScript chỉ hỗ trợ chọn/bỏ chọn; validation và quyền quyết định vẫn ở server.
- `POST action=confirmBulkDelete` parse danh sách ID, loại giá trị không hợp lệ/trùng lặp và đọc lại đúng Student từ database.
- Trang xác nhận liệt kê chính xác các sinh viên sẽ bị xóa và gửi lại ID bằng form POST.
- `POST action=bulkDelete` kiểm tra lại ID, mở một Connection, `setAutoCommit(false)`, xóa toàn bộ bằng PreparedStatement/batch, `commit()` khi mọi thao tác thành công và `rollback()` nếu có lỗi.
- Không chấp nhận danh sách rỗng; không có action GET nào thực hiện bulk delete.
- Kết quả trả về flash message có số bản ghi đã xóa và PRG về danh sách.

### Giai đoạn 10 - CSS BEM và responsive

- Định nghĩa CSS variables cho màu, khoảng cách, radius, shadow và breakpoint.
- Các block dự kiến: `.app-shell`, `.student-list`, `.student-form`, `.student-detail`, `.data-table`, `.pagination`, `.alert`, `.dashboard`, `.confirmation`, `.button`.
- Element/modifier theo BEM, ví dụ `.data-table__cell`, `.button--danger`, `.alert--error`.
- Bảng có vùng cuộn ngang trên màn hình nhỏ; toolbar/form/button co giãn hợp lý.
- Có focus state rõ ràng, label liên kết input, button/link có nội dung dễ hiểu và không phụ thuộc chỉ vào màu sắc.

### Giai đoạn 11 - Build, deploy và kiểm thử

- Chạy `mvn clean package` trong `ex1`, `ex2`, `ex3` và xác nhận ba WAR tương ứng được tạo trong từng thư mục `target/`.
- Deploy từng WAR lên Tomcat 10+ và kiểm tra độc lập qua `/student-mvc-ex1/student`, `/student-mvc-ex2/student`, `/student-mvc-ex3/student`.
- Kiểm tra không có import `javax.*`, Java scriptlet hoặc SQL dùng `Statement`/nối input trực tiếp.
- Kiểm tra log không có 404/500 trong các luồng hợp lệ và ca biên.

## 8. Ma trận kiểm thử bắt buộc

### CRUD và validation

- Xem danh sách, xem chi tiết, thêm, sửa, xác nhận xóa và xóa thành công.
- Refresh sau POST không lặp lại insert/update/delete.
- Student code đúng/sai regex; email đúng/sai; trường trống/quá dài.
- Trùng student code/email khi thêm và khi sửa.
- ID thiếu, chữ, số âm, bằng 0 và ID không tồn tại.
- Input HTML được hiển thị dạng text, không thực thi.

### Search, page và sort

- Search theo code, một phần tên, không phân biệt tình huống rỗng và không có kết quả.
- Page 1, trang giữa, trang cuối, `page=-1`, `page=0`, `page=999`, `page=abc`.
- Sort đủ bốn cột, cả ASC và DESC.
- Sort key/order lạ không thể thay đổi SQL ngoài allowlist.
- Search + sort + pagination giữ nguyên mọi query parameter.

### CSV, dashboard và bulk delete

- CSV chứa mọi sinh viên dù đang ở page 2; mở đúng với UTF-8.
- CSV escape đúng tên có dấu phẩy, dấu nháy kép và xuống dòng.
- Dashboard đúng tổng số, số theo major và sáu tháng; vẫn hiển thị hợp lý khi không có dữ liệu.
- Bulk delete 1/nhiều bản ghi; danh sách rỗng; ID lặp; ID sai; một ID bị xóa giữa hai bước xác nhận.
- Cố tình gây lỗi giữa transaction để xác nhận rollback, không xóa một phần.

## 9. Sản phẩm nộp bài theo PDF

- Ba Maven project `ex1`, `ex2`, `ex3` với source code đầy đủ và ba WAR build thành công.
- File `student_management.sql` dùng chung ở root, không vượt quá 100 MB.
- `report.md` viết hoàn toàn bằng tiếng Anh, gồm hướng dẫn cấu hình/chạy, ảnh chụp và các phần giải thích được PDF yêu cầu.
- Ảnh search hoạt động và validation error.
- Ảnh pagination và sorting.
- Ảnh CSV export, dashboard và bulk delete.
- Phần giải thích ngắn cho:
  - server-side validation;
  - PRG;
  - PreparedStatement và SQL injection;
  - LIMIT/OFFSET;
  - allowlist cho sort column;
  - xử lý page không hợp lệ;
  - transaction cho bulk delete;
  - HTTP headers của CSV;
  - export 100,000 bản ghi và ảnh hưởng bộ nhớ;
  - rủi ro gửi hàng loạt ID để xóa toàn bộ dữ liệu.

## 10. Definition of Done

- Hoàn thành toàn bộ checklist chức năng ở trang 36-37 của PDF và toàn bộ yêu cầu Exercise 1-3.
- Model là JavaBean thuần; DAO sở hữu toàn bộ JDBC; Controller chỉ xử lý request/điều phối; JSP chỉ trình bày.
- Chỉ có `jakarta.*`, JSTL/EL và không có scriptlet.
- GET chỉ đọc/hiển thị; POST thay đổi dữ liệu; insert/update/delete/bulk delete dùng PRG.
- Servlet không có instance field chứa dữ liệu theo request.
- Tất cả JDBC resource được đóng bằng `try-with-resources`.
- Input đi vào SQL bằng `PreparedStatement`; sort dùng allowlist.
- CSS tuân thủ BEM, variables và responsive.
- `mvn clean package` thành công riêng cho cả `ex1`, `ex2`, `ex3`; cả ba ứng dụng chạy trên Tomcat 10+ với MySQL 8.

## 11. Thông tin cần xác nhận trước khi bắt đầu viết code

Đã chốt Chart.js local, `DB_PASSWORD` từ biến môi trường và report bằng tiếng Anh. PDF không quy định ngôn ngữ giao diện, nên cần xác nhận giao diện/thông báo sẽ dùng tiếng Anh hay tiếng Việt trước khi viết JSP.

