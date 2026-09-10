# Full Blogging Project

Spring Boot + Thymeleaf + JPA + MySQL + Spring Security blogging application.

## Before running

1. Create/use a MySQL database named `blogpost`.
2. Open `src/main/resources/application.properties`.
3. Replace `CHANGE_ME` with your MySQL password.
4. Run the application.

## Main URLs

- `/` - public blog
- `/register` - registration
- `/login` - login
- `/new` - create post (login required)
- `/my-posts` - your posts (login required)
- `/post/{id}` - read a post
- `/edit/{id}` - edit your own post
- `/delete/{id}` - delete your own post

## Authorization test

1. Register User A.
2. Create a post as User A.
3. Logout.
4. Register/Login as User B.
5. User B can read User A's post but cannot edit/delete it.
6. The ownership check is performed on the backend as well as the UI.

Note: This version is intended as the complete working base that we will study from scratch afterward.
