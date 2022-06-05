-- ========================================================
-- Users Indexes
-- ========================================================
CREATE INDEX Users_login_index
ON Users
(login);

CREATE INDEX Users_phoneNum_index
ON Users
( phoneNum );

CREATE INDEX Users_password_index
ON Users
( password );

CREATE INDEX Users_type_index
ON Users
( type );

CREATE INDEX Users_favItems_index
ON Users
( favItems );

-- ========================================================
-- Menu Indexes
-- ========================================================
CREATE INDEX Menu_itemName_index
ON Menu
( itemName );

CREATE INDEX Menu_description_index
ON Menu
( description );

CREATE INDEX Menu_price_index
ON Menu
( price );

CREATE INDEX Menu_type_index
ON Menu
( type );

CREATE INDEX Menu_imageURL_index
ON Menu
( imageURL );

-- ========================================================
-- Orders Indexes
-- ========================================================
CREATE INDEX Orders_orderid_index
ON Orders
(orderid);

CREATE INDEX Orders_login_index
ON Orders
( login );

CREATE INDEX Orders_paid_index
ON Orders
( paid );

CREATE INDEX Orders_timeStampRecieved_index
ON Orders
( timeStampRecieved );

CREATE INDEX Orders_total_index
ON Orders
( total );

-- ========================================================
-- ItemStatus Indexes
-- ========================================================
CREATE INDEX ItemStatus_orderid_index
ON ItemStatus
( orderid );

CREATE INDEX ItemStatus_itemName_index
ON ItemStatus
( itemName );

CREATE INDEX ItemStatus_lastUpdated_index
ON ItemStatus
( lastUpdated );

CREATE INDEX ItemStatus_status_index
ON ItemStatus
( status );

CREATE INDEX ItemStatus_comments_index
ON ItemStatus
( comments );