/* we change the path to data files where is the absolute paths to avoid ambiguity */
COPY MENU
FROM '/extra/gcost003/CS166-Project-main/project/data/menu.csv'
WITH DELIMITER ';';

COPY USERS
FROM '/extra/gcost003/CS166-Project-main/project/data/users.csv'
WITH DELIMITER ';';

COPY ORDERS
FROM '/extra/gcost003/CS166-Project-main/project/data/orders.csv'
WITH DELIMITER ';';
ALTER SEQUENCE orders_orderid_seq RESTART 87257;

COPY ITEMSTATUS
FROM '/extra/gcost003/CS166-Project-main/project/data/itemStatus.csv'
WITH DELIMITER ';';


