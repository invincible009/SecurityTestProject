INSERT INTO authorities (name) VALUES
('order_read'),
('profile_read'),
('book_create'),
('book_update'),
('book_delete'),
('order_manage');

INSERT INTO roles (name) VALUES
('user'),
('admin');

INSERT INTO role_authorities (role_id, authority_id)
SELECT r.id, a.id
FROM roles r, authorities a
WHERE r.name = 'user'
  AND a.name IN ('order_read', 'profile_read');

INSERT INTO role_authorities (role_id, authority_id)
SELECT r.id, a.id
FROM roles r, authorities a
WHERE r.name = 'admin'
  AND a.name IN ('book_create', 'book_update', 'book_delete', 'order_read', 'order_manage');

INSERT INTO books (title, author, isbn, price, in_stock) VALUES
('Clean Code', 'Robert C. Martin', '978-0132350884', 39.99, TRUE),
('The Pragmatic Programmer', 'Andrew Hunt', '978-0201616224', 42.50, TRUE),
('Effective Java', 'Joshua Bloch', '978-0134685991', 45.00, TRUE),
('Design Patterns', 'Erich Gamma', '978-0201633610', 54.99, TRUE),
('Refactoring', 'Martin Fowler', '978-0201485677', 49.99, TRUE),
('Domain-Driven Design', 'Eric Evans', '978-0321125217', 59.00, TRUE),
('Spring in Action', 'Craig Walls', '978-1617294945', 44.99, TRUE),
('Java Concurrency in Practice', 'Brian Goetz', '978-0321349606', 55.00, TRUE),
('Head First Design Patterns', 'Eric Freeman', '978-0596007126', 37.99, TRUE),
('Test Driven Development', 'Kent Beck', '978-0321146533', 34.50, TRUE),
('Working Effectively with Legacy Code', 'Michael Feathers', '978-0131177055', 52.00, TRUE),
('Patterns of Enterprise Application Architecture', 'Martin Fowler', '978-0321127426', 58.00, TRUE),
('Microservices Patterns', 'Chris Richardson', '978-1617294549', 46.00, TRUE),
('Building Microservices', 'Sam Newman', '978-1491950357', 47.99, TRUE),
('Release It!', 'Michael T. Nygard', '978-1680502398', 41.00, TRUE),
('Continuous Delivery', 'Jez Humble', '978-0321601919', 57.00, TRUE),
('Clean Architecture', 'Robert C. Martin', '978-0134494166', 43.99, TRUE),
('Code Complete', 'Steve McConnell', '978-0735619678', 49.50, TRUE),
('Cracking the Coding Interview', 'Gayle Laakmann McDowell', '978-0984782857', 35.00, TRUE),
('Algorithms', 'Robert Sedgewick', '978-0321573513', 61.00, TRUE),
('Grokking Algorithms', 'Aditya Bhargava', '978-1617292231', 36.00, TRUE),
('Introduction to Algorithms', 'Thomas H. Cormen', '978-0262033848', 79.00, TRUE),
('Kubernetes Up & Running', 'Kelsey Hightower', '978-1491935675', 39.00, TRUE),
('Docker Deep Dive', 'Nigel Poulton', '978-1521822807', 29.99, TRUE),
('HTTP: The Definitive Guide', 'David Gourley', '978-1565925090', 44.00, TRUE),
('RESTful Web APIs', 'Leonard Richardson', '978-1449358068', 38.00, TRUE),
('Spring Microservices in Action', 'John Carnell', '978-1617293986', 45.50, TRUE),
('Pro Git', 'Scott Chacon', '978-1484200773', 0.00, TRUE),
('You Don''t Know JS Yet', 'Kyle Simpson', '978-1091210090', 33.00, TRUE),
('Software Engineering at Google', 'Titus Winters', '978-1492082798', 48.00, TRUE);