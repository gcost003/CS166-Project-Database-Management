CREATE SEQUENCE orderid_seq START WITH 50000;

CREATE LANGUAGE plpgsql;
CREATE OR REPLACE FUNCTION incr_func()
RETURNS "trigger" AS
$BODY$
BEGIN
NEW.orderid := nextval('orderid_seq');
RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER name BEFORE INSERT 
ON Orders FOR EACH ROW
EXECUTE PROCEDURE incr_func();

DROP TRIGGER IF EXISTS name ON Orders;


