COPY MENU
FROM 'menu.csv'
WITH DELIMITER ';';

COPY USERS
FROM 'users.csv'
WITH DELIMITER ';';

COPY ORDERS
FROM 'orders.csv'
WITH DELIMITER ';';
ALTER SEQUENCE orders_orderid_seq RESTART 87257;

COPY ITEMSTATUS
FROM 'itemStatus.csv'
WITH DELIMITER ';';

COPY USER_LIST
FROM '/home/user/project/data/usr_list.csv'
WITH DELIMITER ';';

