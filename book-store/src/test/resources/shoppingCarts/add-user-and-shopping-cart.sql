insert into roles (id, name) values (10, 'ROLE_USER');

insert into users (id, first_name, last_name, shipping_address, email, password)
values (10, 'Patryk', 'Kowalski', 'Noniewicza12/12',
        'patryk@gmail.com', 'strongpassword');

insert into users_roles (user_id, role_id) values (10, 10);

insert into shopping_carts (id, user_id) values (10, 10);
