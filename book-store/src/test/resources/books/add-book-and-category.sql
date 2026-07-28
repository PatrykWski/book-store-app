insert into categories (id, name, description, is_deleted)
values (1, 'FANTASY', 'FANTASY BOOKS', false);

insert into books (id, title, author, isbn, price, description, cover_image, is_deleted)
values (1, 'Wiedzmin', 'Andrzej Sapkowski', '978-1-4919-4600-2',
        45.55, 'A book about monster killer', null, false);

insert into books_categories (book_id, category_id)
values (1, 1);