create table if not exists sample(
    id serial PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    data text,
    value int default 0
);

create FUNCTION sample_trigger() RETURNS TRIGGER AS
'
    BEGIN
        IF (SELECT value FROM sample where id = NEW.id ) > 1000
           THEN
           RAISE SQLSTATE ''23503'';
           END IF;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;

create TRIGGER sample_value AFTER insert ON sample
    FOR EACH ROW EXECUTE PROCEDURE sample_trigger();

CREATE TABLE IF NOT EXISTS Product(
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    description VARCHAR(64),
    brandName VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS City(
    zip INT PRIMARY KEY,
    city VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS Company(
    cName VARCHAR(64) PRIMARY KEY,
    country VARCHAR(64),
    zip INT,
    street VARCHAR(64),
    cPhoneNumber VARCHAR(64) UNIQUE NOT NULL,
    FOREIGN KEY (zip) REFERENCES City (zip)
);

CREATE TABLE IF NOT EXISTS cEmail(
    cName VARCHAR(64),
    cEmail VARCHAR(64),
    PRIMARY KEY (cName,cEmail),
    FOREIGN KEY (cName) REFERENCES Company (cName) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Porder(
    orderID SERIAL PRIMARY KEY,
    cName VARCHAR(64) NOT NULL,
    pID INT NOT NULL,
    oAmount INT,
    oDate DATE,
    FOREIGN KEY (cName) REFERENCES Company (cName),
    FOREIGN KEY (pID) REFERENCES Product (id)
);

CREATE TABLE IF NOT EXISTS Produce(
    proID SERIAL PRIMARY KEY,
    cName VARCHAR(64) NOT NULL,
    pID INT NOT NULL,
    capacity INT,
    FOREIGN KEY (cName) REFERENCES Company (cName),
    FOREIGN KEY (pID) REFERENCES Product (id)
);

CREATE FUNCTION orderTrigger() RETURNS TRIGGER AS
'
    BEGIN
        IF (SELECT SUM(oAmount) FROM Porder WHERE cName = NEW.cName AND pID = NEW.pID ) >
           (SELECT capacity FROM Produce WHERE cName = NEW.cName AND pID = NEW.pID )
           THEN
           RAISE SQLSTATE ''23503'';
           END IF;
        RETURN NEW;
    END;
' LANGUAGE plpgsql;

CREATE TRIGGER orderCapacity
    AFTER INSERT
    ON Porder
    FOR EACH ROW EXECUTE PROCEDURE orderTrigger();